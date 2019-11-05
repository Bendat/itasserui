@file:Suppress("unused")

package itasserui.lib.process;

import itasserui.lib.process.ArgParam.*
import itasserui.lib.process.ArgParam.BooleanParam.BooleanRange
import itasserui.lib.process.ArgParam.BooleanParam.Flag
import itasserui.lib.process.ArgParam.Range.*
import itasserui.lib.process.ArgParam.SimpleText.IntParam
import itasserui.lib.process.ArgParam.SimpleText.Text

sealed class ArgParam {
    sealed class Range<T>(val default: T) : ArgParam() {
        class IntegerRange(val range: IntRange, default: Int) : Range<Int>(default)
        class FloatRange(val range: ClosedFloatingPointRange<Double>, default: Double) : Range<Double>(default)

        class TextSelection(val range: List<String>, default: String) : Range<String>(default)
    }

    sealed class SimpleText : ArgParam() {
        object IntParam : ArgParam()
        object Text : ArgParam()

    }

    sealed class BooleanParam : ArgParam() {
        class BooleanRange(val default: Boolean) : ArgParam() {
            val range = arrayOf(true, false)
        }

        object Flag : ArgParam()
    }

    object File : ArgParam()
    object Directory : ArgParam()
    object None : ArgParam()
}

sealed class Args(
    val prefix: String,
    val name: String,
    val useOnNew: Boolean,
    val required: Boolean,
    val argType: ArgParam
) {
    object Perl : Args("", "perl", false, true, None)
    object AutoFlush : Args("", "MDevel::Autoflush", false, true, Flag)
    object PkgDir : Args("-", "pkgdir", false, true, Directory)
    object LibDir : Args("-", "lbdir", false, true, Directory)
    object SeqName : Args("-", "seqname", true, true, Text)
    object DataDir : Args("-", "datadir", true, true, Directory)
    object RunStyle : Args("-", "runstyle", false, false, TextSelection(arrayListOf(), "gnuparallel"))
    object OutDir : Args("-", "outdir", true, false, Directory)
    object HomoFlag : Args("-", "homoflag", true, false, TextSelection(listOf("real", "benchmark"), "real"))
    object IdCut : Args("-", "idcut", true, false, FloatRange(0.0..1.0, 0.3))
    object NTemp : Args("-", "ntemp", true, false, IntegerRange(1..50, 20))
    object NModel : Args("-", "nmodel", true, false, IntegerRange(1..10, 5))
    object EC : Args("-", "EC", true, false, BooleanRange(false))
    object LBS : Args("-", "LBS", true, false, BooleanRange(false))
    object GO : Args("-", "GO", true, false, BooleanRange(false))
    object TempExcl : Args("-", "temp_excl", true, false, File)
    object Restraint1 : Args("-", "restraint1", true, false, File)
    object Restraint2 : Args("-", "restraint2", true, false, File)
    object Restraint3 : Args("-", "restraint3", true, false, File)
    object Restraint4 : Args("-", "restraint4", true, false, File)
    object Traj : Args("-", "traj", true, false, Flag)
    object Light : Args("-", "light", true, false, Flag)
    object Hours : Args("-", "hours", true, false, IntParam)

    open val simpleName get() = javaClass.simpleName

    companion object {
        val values
            get() = Args::class
                .sealedSubclasses
                .mapNotNull { it.objectInstance }
    }
}


enum class ArgNames(val value: String) {
    Perl("perl") {
        override fun toString(): String {
            return "perl"
        }
    },
    AutoFlush("MDevel::Autoflush"),
    LibDir("libdir"),
    PkgDir("pkgdir"),
    SeqName("seqname"),
    DataDir("datadir"),
    RunStyle("runstyle"),
    OutDir("outdir"),
    HomoFlag("homoflag"),
    IdCut("idcut"),
    NTemp("ntemp"),
    NModel("nmodel"),
    EC("EC"),
    LBS("LBS"),
    GO("GO"),
    TempExcl("temp_excl"),
    Restraint1("restraint1"),
    Restraint2("restraint2"),
    Restraint3("restraint3"),
    Restraint4("restraint4"),
    Traj("traj"),
    Light("light"),
    Hours("hours");

    val arg get() = toString()
    override fun toString(): String {
        return "-$value"
    }
}