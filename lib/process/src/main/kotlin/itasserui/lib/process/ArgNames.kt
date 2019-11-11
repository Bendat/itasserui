@file:Suppress("unused")

package itasserui.lib.process;

import itasserui.common.utils.AbstractSealedObject
import itasserui.lib.process.ArgParam.*
import itasserui.lib.process.ArgParam.BooleanParam.BooleanRange
import itasserui.lib.process.ArgParam.BooleanParam.Flag
import itasserui.lib.process.ArgParam.Range.*
import itasserui.lib.process.ArgParam.SimpleText.IntParam
import itasserui.lib.process.ArgParam.SimpleText.Text

interface DefaultParamType<T> {
    val default: T
}

sealed class ArgParam : AbstractSealedObject() {
    sealed class Range<T>(override val default: T) : ArgParam(), DefaultParamType<T> {
        class IntegerRange(val range: IntRange, default: Int) : Range<Int>(default)
        class DecimalRange(val range: ClosedFloatingPointRange<Double>, default: Double) : Range<Double>(default)

        class TextSelection(val range: List<String>, default: String) : Range<String>(default)
    }

    sealed class SimpleText : ArgParam() {
        object IntParam : ArgParam()
        object Text : ArgParam()

    }

    sealed class BooleanParam : ArgParam() {
        class BooleanRange(override val default: Boolean) : ArgParam(), DefaultParamType<Boolean> {
            val range = arrayOf(true, false)
        }

        object Flag : ArgParam()
    }

    object File : ArgParam()
    object Directory : ArgParam()
    object None : ArgParam()
}

sealed class Arg<out T : ArgParam>(
    val prefix: String,
    val name: String,
    val useOnNew: Boolean,
    val required: Boolean,
    val argType: T
) : AbstractSealedObject() {
    object Perl : Arg<None>("", "perl", false, true, None)
    object AutoFlush : Arg<Flag>("", "MDevel::Autoflush", false, true, Flag)
    object PkgDir : Arg<Directory>("-", "pkgdir", false, true, Directory)
    object LibDir : Arg<Directory>("-", "lbdir", false, true, Directory)
    object JavaHome : Arg<Directory>("-", "java_home", false, true, Directory)
    object SeqName : Arg<Text>("-", "seqname", true, true, Text)
    object DataDir : Arg<Directory>("-", "datadir", true, true, Directory)
    object RunStyle : Arg<TextSelection>("-", "runstyle", false, false, TextSelection(arrayListOf(), "gnuparallel"))
    object OutDir : Arg<Directory>("-", "outdir", true, false, Directory)
    object HomoFlag : Arg<TextSelection>(
        "-", "homoflag", true, false,
        TextSelection(listOf("", "real", "benchmark"), "")
    )

    object IdCut : Arg<DecimalRange>("-", "idcut", true, false, DecimalRange(0.0..1.0, 0.3))
    object NTemp : Arg<IntegerRange>("-", "ntemp", true, false, IntegerRange(1..50, 20))
    object NModel : Arg<IntegerRange>("-", "nmodel", true, false, IntegerRange(1..10, 5))
    object EC : Arg<BooleanRange>("-", "EC", true, false, BooleanRange(false))
    object LBS : Arg<BooleanRange>("-", "LBS", true, false, BooleanRange(false))
    object GO : Arg<BooleanRange>("-", "GO", true, false, BooleanRange(false))
    object TempExcl : Arg<File>("-", "temp_excl", true, false, File)
    object Restraint1 : Arg<File>("-", "restraint1", true, false, File)
    object Restraint2 : Arg<File>("-", "restraint2", true, false, File)
    object Restraint3 : Arg<File>("-", "restraint3", true, false, File)
    object Restraint4 : Arg<File>("-", "restraint4", true, false, File)
    object Traj : Arg<Flag>("-", "traj", true, false, Flag)
    object Light : Arg<Flag>("-", "light", true, false, Flag)
    object Hours : Arg<IntParam>("-", "hours", true, false, IntParam)

    val arg get() = prefix + name

    companion object {
        val values
            get() = Arg::class
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