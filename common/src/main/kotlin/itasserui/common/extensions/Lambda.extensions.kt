package itasserui.common.extensions

infix fun (() -> Unit).unless(condition: Boolean) {
    println("Condition is $condition")
    if (!condition)
        this().apply { "Executed action" }
}


