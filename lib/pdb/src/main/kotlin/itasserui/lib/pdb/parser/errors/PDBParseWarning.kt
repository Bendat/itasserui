package itasserui.lib.pdb.parser.errors

import itasserui.common.errors.RuntimeError

sealed class Severity
object Minor : Severity()
object Serious : Severity()


sealed class PDBParseWarning(
    val severity: Severity = Minor,
    parent: RuntimeError? = null
) : ParsingIssue(parent)