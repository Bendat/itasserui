package itasserui.common.extensions

infix fun (()->Unit).unless(condition: Boolean){
    if(!condition)
        this()
}


