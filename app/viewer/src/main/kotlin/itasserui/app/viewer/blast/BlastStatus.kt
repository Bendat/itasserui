package itasserui.app.viewer.blast

enum class BlastStatus {
    Stopped, Waiting, Starting, Ready, NoHitsFound, Failed, Cancelled, Unknown, Idle;

    companion object {
        fun get(value: String) =
            values().first { it.name.toLowerCase() == value.toLowerCase() }
    }
}
