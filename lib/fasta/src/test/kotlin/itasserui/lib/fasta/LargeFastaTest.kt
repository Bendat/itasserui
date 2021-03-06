package itasserui.lib.fasta

import arrow.core.Either
import io.kotlintest.specs.DescribeSpec
import itasserui.common.serialization.Serializer
import itasserui.lib.filemanager.FS

class LargeFastaTest : DescribeSpec({
    val file = FS[javaClass.getResource("/seq.fasta").file]
    describe("A very large fasta file") {
        lateinit var parsed: Either<SequenceError, SeqFile>

        it("Should parse the file [$file]") {
            parsed = SeqParser.parse(file)
        }

        it("Should verify parsing resulted in IOError") {
            Serializer.toJson(parsed.map { it.map { seq -> seq.description } })
        }
    }
})