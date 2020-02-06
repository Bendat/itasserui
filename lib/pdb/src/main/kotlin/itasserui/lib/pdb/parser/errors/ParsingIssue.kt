package itasserui.lib.pdb.parser.errors

import itasserui.common.errors.RuntimeError

abstract class ParsingIssue(parent: RuntimeError? = null) :
    RuntimeError(parent)