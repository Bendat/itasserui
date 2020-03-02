package itasserui.app.viewer.blast

import io.kotlintest.specs.DescribeSpec
import itasserui.lib.pdb.parser.PDBParser
import java.nio.file.Paths

class BlastClientTest : DescribeSpec() {

    init {
        describe("search") {
            val file = BlastClientTest::class.java.getResource("/1ey4.pdb").file
            val path = Paths.get(file)
            val parsed = PDBParser.parse(path)
            val client = BlastClient()
            parsed.map { pdb ->
                client.postSequence(pdb.sequence).map { res ->
                    val status = client.getStatus(res.requestID)
                    println("Status is ${status.map { it }}")
                }
            }
        }
    }

}