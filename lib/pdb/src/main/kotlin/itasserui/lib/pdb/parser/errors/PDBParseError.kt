package itasserui.lib.pdb.parser.errors

import arrow.data.Nel
import arrow.data.NonEmptyListOf
import itasserui.common.errors.RuntimeError
import itasserui.common.serialization.Serializer
import itasserui.lib.pdb.parser.StructureType

sealed class PDBParseError(parent: RuntimeError? = null) :
    RuntimeError(parent) {
    override fun toString(): String = Serializer.toPrettyJson(this)
}

class ParseFailed(val reason: String, parent: RuntimeError?) :
    PDBParseError(parent)

class ErrorList(val errors: Nel<PDBParseError>) :
    PDBParseError(), NonEmptyListOf<PDBParseError> by errors {
    override fun toString(): String {
        return "ErrorList(errors=$errors)"
    }
}

object EmptyFile :
    PDBParseError(null)

object MissingHeader :
    PDBParseError(null)

class InvalidLine(
    val line: String,
    val size: Int,
    val expectedSize: Int,
    val status: StructureType,
    parent: RuntimeError? = null
) : PDBParseError(parent)

class InvalidSequenceNumber(
    val line: String,
    val number: String,
    parent: PDBParseError? = null
) : PDBParseError(parent)

class InvalidCoordinate(
    val line: String,
    val Coordinate: String,
    value: String,
    parent: PDBParseError? = null
) : PDBParseError(parent)

