package itasserui.lib.pdb.parser

import io.kotlintest.be
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec
import itasserui.test_utils.matchers.Be
import javafx.geometry.Point3D
import java.nio.file.Files
import java.nio.file.Paths

class ParserTest : DescribeSpec({
    val file = Paths.get(this::class.java.getResource("/1ey4.pdb").file)
    describe("Loading the file") {
        lateinit var pdb: PDB
        it("Verifies the file") {
            Files.exists(file) should be(true)
        }

        it("Parses the file") {
            PDBParser.parse(file)
                .map { pdb = it } should Be.ok()

        }

        context("Atoms should match the reference program") {
            it("Verifies the nodes graph is not empty") {
                pdb.nodes.filterNot { it is EmptyAtom }.size shouldBe be(671)
            }

            it("Verifies the first Atom is accurate") {
                val reference: Atomic = Atom(Point3D(346.0107680744686, -144.98770314365595,
                    -196.34345742613777), Element.N, 6, AminoAcid.LYS, 0)
                val atom = pdb.nodes.first().asAtom
                atom should be(reference)
            }

            it("Verifies the last Atom is accurate") {
                val reference: Atomic = Atom(Point3D(-257.6092319255314, -182.24770314365594,
                    276.3565425738622), Element.CB, 141, AminoAcid.SER, 1108)
                val atom = pdb.nodes.last().asAtom
                atom should be(reference)
            }
        }

        context("Helix Bonds should match the reference program") {
            it("Verifies the first Helix bond") {
                val helix = pdb.helices.first()
                helix.start should be(54)
                helix.end should be(68)
            }
            it("Verifies the last Helix bond") {
                val helix = pdb.helices.last()
                helix.start should be(137)
                helix.end should be(141)
            }

        }

        context("Sheet bonds should match the reference program") {
            it("Verifies the first Sheet bond") {
                val sheet = pdb.sheets.first()
                sheet.start should be(97)
                sheet.end should be(98)
            }

            it("Verifies the last Sheet bond") {
                val sheet = pdb.sheets.last()
                sheet.start should be(110)
                sheet.end should be(111)
            }


        }
        it("Verifies the first residue") {
            val residue = pdb.residues.first()
            residue.sequenceNo should be(6)
        }
    }
})
