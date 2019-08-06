package itasserui.lib.process.process

import itasserui.common.utils.AbstractSealedObject

sealed class STDType : AbstractSealedObject() {
    object Out : STDType()
    object Err : STDType()
    object In : STDType()
}