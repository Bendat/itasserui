package itasserui.app.viewer.parser

import io.kotlintest.specs.DescribeSpec
import java.nio.file.Paths

class PDBParserTest : DescribeSpec({
    val ls = arrayListOf(1 to 2, 1 to 1, 1 to 2).groupBy { it.second }
    describe("What"){
        println("Ls is $ls")
    }
    describe("Hello") {
        val file = Paths.get(PDBParserTest::class.java.getResource("/1ey4.pdb").file)
        PDBParser.parse(file)
    }
})