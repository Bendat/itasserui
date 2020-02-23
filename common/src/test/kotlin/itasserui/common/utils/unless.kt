package itasserui.common.utils

@Suppress("NOTHING_TO_INLINE")
inline infix fun <T> (() -> T).unless(outcome: Boolean) {
    if (!outcome)
        this()
}

