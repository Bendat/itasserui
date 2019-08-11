package itasserui.common.`typealias`

import arrow.data.Ior
import arrow.data.Nel
import itasserui.common.errors.RuntimeError

typealias Errors = Ior.Left<Nel<RuntimeError>, *>
typealias SoftErrors = Ior.Both<Nel<RuntimeError>, *>