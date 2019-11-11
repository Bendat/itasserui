package itasserui.lib.process

import com.fasterxml.jackson.annotation.JsonIgnore
import itasserui.lib.process.details.ProcessOutput
import itasserui.lib.process.process.STDType
import lk.kotlin.observable.list.ObservableList
import lk.kotlin.observable.list.filtering
import lk.kotlin.observable.list.observableListOf

data class STD(
    val stream: ObservableList<ProcessOutput> = observableListOf()
) {
    @JsonIgnore
    val out = stream.filtering { it.stdType == STDType.Out }
    @JsonIgnore
    val err = stream.filtering { it.stdType == STDType.Err }
}