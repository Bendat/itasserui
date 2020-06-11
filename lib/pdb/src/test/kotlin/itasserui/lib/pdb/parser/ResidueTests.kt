package itasserui.lib.pdb.parser

import arrow.data.Valid
import io.kotlintest.be
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.lib.pdb.parser.AminoAcid.*
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.ArrayList

class ResidueTests : DescribeSpec({
    val url = ResidueTests::class.java.getResource("/1ey4.pdb").file
    val pdbFile = Paths.get(url)
    if (!Files.exists(pdbFile))
        throw FileNotFoundException(url)
    val parsed = PDBParser.parse(pdbFile)
    var pdb: PDB
    describe("Verifies the file is parsed successful") {
        parsed as Valid
    }
    describe("The list of Amino Acids should exactly match the reference list.") {
        it("Verifies the amino acid lists are the same") {
            parsed as Valid
            parsed.a.residues.map { it.acid } should be(expectedList)
        }
    }

    describe("The generated secondary structures should hav residues matching the refernce list") {
        it("Compares the betasheet list") {
            parsed as Valid
            pdb = parsed.a
            val s: List<AminoAcid> = pdb.sheetStructures.flatMap { it.map{it.acid} }
            val s1: List<AminoAcid> = expectedStructures["betasheet"] ?: listOf()
            s should be(s1)
        }
    }

//    describe()
})

val expectedStructures = listOf(
    "betasheet" to listOf(LYS, GLU, PRO, ALA, THR, LEU, ILE, LYS, ALA, ILE, ASP),
    "alphahelix" to listOf(TYR, GLY, PRO, GLU, ALA, ALA, ALA, PHE, THR, LYS, LYS, MET, VAL, GLU, ASN),
    "null" to listOf(
        LYS,
        LEU,
        HIS,
        GLY,
        ASP,
        LYS,
        GLY,
        LEU,
        LEU,
        THR,
        PRO,
        GLU,
        THR,
        LYS,
        HIS,
        PRO,
        LYS,
        LYS,
        GLY,
        VAL,
        GLU,
        LYS,
        ALA,
        LYS,
        LYS,
        ASP,
        LYS,
        GLY,
        GLN,
        ARG,
        THR,
        ASP,
        LYS,
        TYR,
        GLY,
        ARG,
        ASP,
        GLY,
        GLY,
        LEU,
        ALA,
        ALA,
        TYR,
        VAL,
        TYR,
        LYS,
        PRO,
        ASN,
        ASN,
        THR,
        LYS)
).toMap()
val expectedList = listOf(
    LYS,
    LEU,
    HIS,
    LYS,
    GLU,
    PRO,
    ALA,
    THR,
    LEU,
    ILE,
    LYS,
    ALA,
    ILE,
    ASP,
    GLY,
    ASP,
    THR,
    VAL,
    LYS,
    LEU,
    MET,
    TYR,
    LYS,
    GLY,
    GLN,
    PRO,
    MET,
    THR,
    PHE,
    ARG,
    LEU,
    LEU,
    LEU,
    VAL,
    ASP,
    THR,
    PRO,
    GLU,
    THR,
    LYS,
    HIS,
    PRO,
    LYS,
    LYS,
    GLY,
    VAL,
    GLU,
    LYS,
    TYR,
    GLY,
    PRO,
    GLU,
    ALA,
    ALA,
    ALA,
    PHE,
    THR,
    LYS,
    LYS,
    MET,
    VAL,
    GLU,
    ASN,
    ALA,
    LYS,
    LYS,
    ILE,
    GLU,
    VAL,
    GLU,
    PHE,
    ASP,
    LYS,
    GLY,
    GLN,
    ARG,
    THR,
    ASP,
    LYS,
    TYR,
    GLY,
    ARG,
    GLY,
    LEU,
    ALA,
    TYR,
    ILE,
    TYR,
    ALA,
    ASP,
    GLY,
    LYS,
    MET,
    VAL,
    ASN,
    GLU,
    ALA,
    LEU,
    VAL,
    ARG,
    GLN,
    GLY,
    LEU,
    ALA,
    LYS,
    VAL,
    ALA,
    TYR,
    VAL,
    TYR,
    LYS,
    PRO,
    ASN,
    ASN,
    THR,
    HIS,
    GLU,
    GLN,
    HIS,
    LEU,
    ARG,
    LYS,
    SER,
    GLU,
    ALA,
    GLN,
    ALA,
    LYS,
    LYS,
    GLU,
    LYS,
    LEU,
    ASN,
    ILE,
    TRP,
    SER
)