package itasserui.common.extensions

val Boolean.isFalse get() = !this
val Boolean.isTrue get() = this

fun <TReturn> Boolean.ifTrue(action: () -> TReturn) =
    apply { if (this) action() }

fun <TReturn> Boolean.ifFalse(action: () -> TReturn) =
    apply { if (!this) action() }
