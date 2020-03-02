package itasserui.app.viewer.blast

enum class BlastProgram {
    megablast,
    blastn,
    blastp,
    rpsblast,
    blastX,
    tblastn,
    tblastx;

    override fun toString(): String {
        return name.toLowerCase()
    }


}