package itasserui.common.extensions

val Boolean.isFalse get() = !this
val Boolean.isTrue get() = this

fun Boolean.ifTrue(action: () -> Unit) = apply { if (this) action() }
fun Boolean.ifFalse(action: () -> Unit) = apply { if (!this) action() }

