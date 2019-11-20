@file:Suppress("unused")

package itasserui.lib.fasta

import arrow.core.*
import itasserui.common.extensions.isFalse
import itasserui.common.extensions.remove
import itasserui.common.logger.Logger
import itasserui.lib.fasta.description.Description
import itasserui.lib.filemanager.FS
import java.nio.file.Files
import java.nio.file.Path


/**
 * Singleton for parsing fasta files and fasta strings
 */
@Suppress("MemberVisibilityCanBePrivate")
object SeqParser : Logger {

    fun parse(file: Path): Either<SequenceError, SeqFile> {
        return when {
            !Files.exists(file) -> Left(NoSuchFile(file))
            !Files.isRegularFile(file) -> Left(FileIsDirectory(file))
            else -> parse(file.toFile().readLines(), file)
        }
    }

    fun parse(
        input: List<String>?,
        file: Path = FS["-- Input was a List object --"]
    ): Either<SequenceError, SeqFile> = when {
        input == null || input.isEmpty() -> when {
            !Files.exists(file) -> Left(NoSuchFile(file))
            Files.size(file) == 0L -> Left(InvalidFile(file))
            else -> Left(EmptyFile(file))
        }
        else -> when (val verify = verifyFirstLine(input.getOrNull(0), file)) {
            is Some -> Left(verify.t)
            else -> Right(mapDescriptionToBodies(input.joinToString("\n"), file))
        }
    }


    internal fun getDescriptionIndices(input: List<String>): List<Int> {
        return input.mapIndexed { index, item ->
            if (item.startsWith(">")) index
            else null
        }.filterNotNull()
    }

    internal fun mapDescriptionToBodies(fasta: String, file: Path?): List<Sequence> {
        val sequences = fasta.split(">.*".toRegex())
        val descriptions = fasta.split("\n").filter { it.startsWith(">") }
        val bodies = sequences.filter { !it.isBlank() }.map { it.replace("\n", "") }
        return descriptions.mapIndexed { index, description ->
            determineSequenceType(bodies.getOrNull(index), description, file)
        }
    }

    fun parse(input: String?) = parse(input?.lines())

    internal fun flatten(bodies: List<String>) =
        bodies.joinToString().remove("\n", ",", " ")

    internal fun determineSequenceType(
        body: String?,
        title: String,
        file: Path?
    ): Sequence {
        val chars = mapCharValidity(body, file)
        return Sequence(
            Description(title),
            when {
                body.isNullOrBlank() -> SequenceChain.EmptySequenceChain
                chars is Some -> SequenceChain.InvalidSequenceChain(
                    body,
                    chars.t
                )
                else -> SequenceChain.ValidSequenceChain(body)
            }
        )
    }

    internal fun mapCharValidity(value: String?, file: Path?): Option<BadChar> {
        val badChars = value
            ?.trim()
            ?.toSet()
            ?.minus(AminoAcid.values().map { it.code }.toSet())
        return when (badChars?.any()) {
            true -> Some(BadChar("The sequence contains invalid characters", value, badChars, file))
            false, null -> None
        }
    }

    private fun verifyFirstLine(line: String?, filename: Path?): Option<SequenceError> {
        return when {
            line == null -> Some(EmptyFile(filename))
            line.startsWith(">").isFalse -> Some(
                NoStartingHeader(filename)
            )
            else -> None
        }
    }
}
