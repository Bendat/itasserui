package itasserui.lib.process

import itasserui.lib.process.details.TrackingList

data class STD(
    val output: TrackingList<String> = TrackingList(),
    val err: TrackingList<String> = TrackingList()
)