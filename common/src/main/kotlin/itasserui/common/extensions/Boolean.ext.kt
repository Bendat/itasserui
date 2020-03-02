package itasserui.common.extensions

val Boolean?.isFalse get() = this == false || this == null
val Boolean?.isTrue get() = this == true

fun <TReturn> Boolean.ifTrue(action: () -> TReturn) =
    apply { if (this) action() }

fun <TReturn> Boolean.ifFalse(action: () -> TReturn) =
    apply { if (!this) action() }

infix fun Boolean.ior(other: () -> Boolean) =
    if (this.isFalse)
        false
    else this or other()
