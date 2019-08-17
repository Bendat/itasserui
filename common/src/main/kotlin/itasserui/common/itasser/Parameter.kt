@file:Suppress("unused")

package itasserui.common.itasser

sealed class Param(val commandlineParameter: CommandlineParameter) {
    sealed class BooleanParam(commandlineParameter: CommandlineParameter, val value: Boolean) :
        Param(commandlineParameter) {
        object LBS : BooleanParam(CommandlineParameter("LBS"), false)
        object GO : BooleanParam(CommandlineParameter("GO"), false)
        object EC : BooleanParam(CommandlineParameter("EC"), false)
    }

    sealed class RangeParam<T>(commandlineParameter: CommandlineParameter) : Param(commandlineParameter)
            where  T : Number, T : Comparable<T> {
        abstract val defaultValue: T
        abstract val range: ClosedRange<T>

        object IDCut : RangeParam<Double>(CommandlineParameter("idcut")) {
            override val defaultValue: Double = 0.3
            override val range: ClosedFloatingPointRange<Double> = 0.0..1.0
        }

        sealed class IntegerRangeParam(commandlineParameter: CommandlineParameter) :
            RangeParam<Int>(commandlineParameter) {
            object NTemp : IntegerRangeParam(CommandlineParameter("ntemp")) {
                override val defaultValue: Int = 20
                override val range: ClosedRange<Int> = 1..50
            }

            object NModel : IntegerRangeParam(CommandlineParameter("nmodel")) {
                override val defaultValue: Int = 5
                override val range: ClosedRange<Int> = 1..10
            }
        }
    }

    sealed class ComboParam(commandlineParameter: CommandlineParameter, vararg val options: String) :
        Param(commandlineParameter) {
        abstract val defaultValue: String

        object Runstyle : ComboParam(CommandlineParameter("runstyle"), "serial", "parallel", "gnuparallel") {
            override val defaultValue: String = "gnuparallel"
        }

        object HomoFlag : ComboParam(CommandlineParameter("homoflag"), "real", "benchmark") {
            override val defaultValue: String = "real"
        }
    }

    sealed class OptionParam(commandlineParameter: CommandlineParameter) : Param(commandlineParameter) {
        object Restraint1 : OptionParam(CommandlineParameter("restraint1"))
        object Restraint2 : OptionParam(CommandlineParameter("restraint2"))
        object Restraint3 : OptionParam(CommandlineParameter("restraint3"))
        object Restraint4 : OptionParam(CommandlineParameter("restraint4"))
        object Traj : OptionParam(CommandlineParameter("traj"))
    }

    sealed class ArgumentParam(commandlineParameter: CommandlineParameter) : Param(commandlineParameter) {
        object OutDir : ArgumentParam(CommandlineParameter("outdir"))
        object DataDir : ArgumentParam(CommandlineParameter("datadir"))
        object PackageDir : ArgumentParam(CommandlineParameter("packagedir"))
        object Hours : ArgumentParam(CommandlineParameter("hours"))
        object Seqname : ArgumentParam(CommandlineParameter("seqname"))
        object JavaHome : ArgumentParam(CommandlineParameter("java_home"))
    }

    override fun toString(): String {
        return this::class.java.simpleName.toLowerCase()
    }


}

data class CommandlineParameter(val name: String) {
    val param get() = toString()
    override fun toString(): String {
        return "-$name"
    }
}