@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package itasserui.lib.fasta.description

import arrow.core.Either
import arrow.core.Try
import arrow.core.left
import itasserui.common.logger.Logger
import itasserui.lib.fasta.NoStartingHeader
import itasserui.lib.fasta.SequenceError


data class Description(private val initialValue: String) : Logger {
    private val _data = ArrayList<NCBIIdentifier>()
    val data get() = _data.toList()

    val value
        get() = data
            .firstOrNull { it is NCBIIdentifier.REF }
            ?.let { it as NCBIIdentifier.REF }?.name
            ?: initialValue.removePrefix(">")


    fun parse(): Either<SequenceError, List<NCBIIdentifier>> =
        when {
            !initialValue.startsWith(">") -> NoStartingHeader(null).left()
            else -> Try { mapNCBI(initialValue.removePrefix(">").split("|")) }
                .toEither { SequenceError.UnknownExceptionError(it) }
        }


    private tailrec fun mapNCBI(list: List<String>): List<NCBIIdentifier> {
        return if (list.isNotEmpty()) {
            val first = list[0]
            val match = NCBIIdentifierRule.types.firstOrNull { (it.simpleName.toLowerCase() == first.trim()) }
            if (match != null) {
                val range = list.take(match.items + 1)
                info { "Match is $match range is $range" }
                val params = range.drop(1)
                    .map { it.trim() }
                    .toTypedArray()
                match.identifierInstance(*params)
                    ?.let { _data += it }
                mapNCBI(list.drop(match.items + 1))
            } else mapNCBI(list.drop(1))
        } else listOf()
    }

    override fun toString(): String {
        return "Description(_data=$value)"
    }

}

