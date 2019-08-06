@file:Suppress("unused")

package itasserui.lib.process;

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

    override fun toString(): String {
        return "-$value"
    }
}