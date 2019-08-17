package itasserui.lib.fasta

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import io.kotlintest.be
import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.common.extensions.remove
import itasserui.common.utils.uuid
import itasserui.lib.filemanager.FS

class ParserTests : DescribeSpec({
    describe("Functions") {
        context("Get Description Indices") {
            context("Single Sequence fastas") {
                context("Determine description index of short fasta") {
                    val fasta = listOf(
                        ">description 1",
                        "AABBCC"
                    )
                    lateinit var index: List<Int>
                    it("Should determine the index of the fasta description") {
                        index = SeqParser.getDescriptionIndices(fasta)
                    }
                    it("Should verify the size is 1") {
                        index.size should be(1)
                    }
                    it("Should verify the index is '0'") {
                        index.first() should be(0)
                    }
                }
            }
            context("Multi Sequence fastas") {
                context("Determine description index of longer fasta") {
                    val fasta = listOf(
                        ">description 1",
                        "AABBCC",
                        ">description 2",
                        "AAABBB"
                    )

                    lateinit var index: List<Int>
                    it("Should determine the index of the fasta description") {
                        index = SeqParser.getDescriptionIndices(fasta)
                    }

                    it("Should verify the index is '0'") {
                        index.first() should be(0)
                    }

                    it("Should verify the second index is '2'") {
                        index[1] should be(2)
                    }
                }
                context("Determine description index of longer fasta with a missing body") {
                    val fasta = listOf(
                        ">description 1",
                        "AABBCC",
                        ">description 2"
                    )

                    lateinit var index: List<Int>
                    it("Should determine the index of the fasta description") {
                        index = SeqParser.getDescriptionIndices(fasta)
                    }

                    it("Should verify the index is '0'") {
                        index.first() should be(0)
                    }

                    it("Should verify the second index is '2'") {
                        index[1] should be(2)
                    }
                }
            }
        }
        context("Map Character Validity") {
            context("Should validate with no bad characters") {
                val sequent = "AAGGCC"
                lateinit var badchars: Option<BadChar>
                it("Should parse the sequence") {
                    badchars = SeqParser.mapCharValidity(sequent, FS[sequent])
                }

                it("Should verify bad chars is $None") {
                    badchars should be<Option<BadChar>>(None)
                }
            }

            context("Should validate with bad characters") {
                val sequent = "]AAG,GC{C-G*l"
                val expected = hashSetOf('l', ',', '{', ']', 'l')
                lateinit var badcharsOpt: Option<BadChar>
                lateinit var badChars: BadChar

                it("Should parse the sequence") {
                    badcharsOpt = SeqParser.mapCharValidity(sequent, FS[sequent])
                }

                it("Should verify bad chars is $None") {
                    badcharsOpt should beInstanceOf<Some<BadChar>>()
                }
                it("Should extract the BadCharError") {
                    val right = badcharsOpt as Some
                    badChars = right.t
                }

                expected.forEach {
                    it("Should verify [$it] is in the returned list of bad characters") {
                        badChars.badChars.contains(it) should be(true)
                    }
                }

            }


        }
        context("Determine Sequence Type") {
            context("An Invalid Sequence") {
                val sequence = arrayListOf("GGC[")
                lateinit var sequenceString: String
                lateinit var sequenceResult: Sequence
                it("Should flatten the sequence") {
                    sequenceString = SeqParser.flatten(sequence)
                }

                it("Should determine the sequence type of [$sequence]") {
                    sequenceResult = SeqParser.determineSequenceType(
                        body = sequenceString,
                        title = "Test Invalid Sequence",
                        file = FS[sequenceString]
                    )
                }

                it("Should verify the result is type InvalidSequence") {
                    sequenceResult.body should beInstanceOf<SequenceChain.InvalidSequenceChain>()
                }
            }
            context("A valid Sequence") {
                val sequence = arrayListOf("GGCG")
                lateinit var sequenceString: String
                lateinit var sequenceResult: Sequence
                it("Should flatten the sequence") {
                    sequenceString = SeqParser.flatten(sequence)
                }

                it("Should determine the sequence type of [$sequence]") {
                    sequenceResult = SeqParser.determineSequenceType(
                        body = sequenceString,
                        title = "Test Valid Sequence",
                        file = FS[sequenceString]
                    )
                }

                it("Should verify the result is type ValidSequence") {
                    sequenceResult.body should beInstanceOf<SequenceChain.ValidSequenceChain>()
                }
            }
            context("An Empty Sequence") {
                val sequence = arrayListOf("")
                lateinit var sequenceString: String
                lateinit var sequenceResult: Sequence
                it("Should flatten the sequence") {
                    sequenceString = SeqParser.flatten(sequence)
                }

                it("Should determine the sequence type of [$sequence]") {
                    sequenceResult = SeqParser.determineSequenceType(
                        body = sequenceString,
                        title = "Test Empty Sequence",
                        file = FS[sequenceString]
                    )
                }

                it("Should verify the result is type EmptySequence") {
                    sequenceResult.body should beInstanceOf<SequenceChain.EmptySequenceChain>()
                }
            }
        }


        context("Map Descriptions to bodies") {
            context("Single fasta") {
                val fasta = ">title 1\nAAGGCC"
                lateinit var sequences: List<Sequence>
                it("Should parse the fasta") {
                    sequences = SeqParser.mapDescriptionToBodies(fasta, FS[fasta]).toList()
                }

                it("Should verify the size of the result") {
                    sequences.size should be(1)
                }

                it("Should verify the sequence type") {
                    sequences[0].body should beInstanceOf<SequenceChain.ValidSequenceChain>()
                }

                it("Should verify the sequence description") {
                    sequences[0].description.value should be("title 1")
                }

                it("Should verify the sequence body") {
                    sequences[0].body.chain should be("AAGGCC")
                }
            }
            context("Longer Single fasta") {
                val fasta = ">title 1\nAAGGCC\nBCGA"
                lateinit var sequences: List<Sequence>
                it("Should parse the fasta") {
                    sequences = SeqParser.mapDescriptionToBodies(fasta, null).toList()
                }

                it("Should verify the size of the result") {
                    sequences.size should be(1)
                }

                it("Should verify the sequence type") {
                    sequences[0].body should beInstanceOf<SequenceChain.ValidSequenceChain>()
                }

                it("Should verify the sequence description") {
                    sequences[0].description.value should be("title 1")
                }

                it("Should verify the sequence body") {
                    sequences[0].body.chain should be("AAGGCCBCGA")
                }
            }

            context("Double fasta") {
                val fasta = ">title 1\nAAGGCC\n>2\nBBGGCC"
                lateinit var sequences: List<Sequence>
                it("Should parse the fasta") {
                    sequences = SeqParser.mapDescriptionToBodies(fasta, FS[fasta]).toList()
                }

                it("Should verify the size of the result") {
                    sequences.size should be(2)
                }

                it("Should verify the sequence type") {
                    sequences[0].body should beInstanceOf<SequenceChain.ValidSequenceChain>()
                }

                it("Should verify the sequence body") {
                    sequences[0].body.chain should be("AAGGCC")
                }

                it("Should verify the sequence description") {
                    sequences[1].description.value should be("2")
                }

                it("Should verify the second sequence body") {
                    sequences[1].body.chain should be("BBGGCC")
                }
            }

            context("Longer Double fasta") {
                val fasta = ">title 1\nAAGGCC\nGGGEEE\n>2\nBBGGCCG\nGGHHH"
                lateinit var sequences: List<Sequence>
                it("Should parse the fasta") {
                    sequences = SeqParser.mapDescriptionToBodies(fasta, FS[fasta]).toList()
                }

                it("Should verify the size of the result") {
                    sequences.size should be(2)
                }

                it("Should verify the sequence type") {
                    sequences[0].body should beInstanceOf<SequenceChain.ValidSequenceChain>()
                }

                it("Should verify the sequence description") {
                    sequences[0].description.value should be("title 1")
                }

                it("Should verify the sequence body") {
                    sequences[0].body.chain should be("AAGGCCGGGEEE")
                }

                it("Should verify the second sequence description") {
                    sequences[1].description.value should be("2")
                }

                it("Should verify the second sequence body") {
                    sequences[1].body.chain should be("BBGGCCGGGHHH")
                }
            }

            context("Empty fasta") {
                val fasta = ""
                lateinit var sequences: List<Sequence>
                it("Should parse the fasta") {
                    sequences = SeqParser.mapDescriptionToBodies(fasta, null).toList()
                }

                it("Should verify the size of the result") {
                    sequences.size should be(0)
                }
            }
        }
    }

    describe("Single Sequence Fasta File Tests") {
        context("Bad IO tests") {
            context("Empty file") {
                val file = empty
                lateinit var parsed: Either<SequenceError, SeqFile>

                it("Should parse the file [$file]") {
                    parsed = SeqParser.parse(file)
                }

                it("Should verify parsing resulted in IOError") {
                    parsed should beInstanceOf<Either.Left<SequenceError>>()
                }

                it("Should verify parsing resulted is EmptyFile") {
                    val left = parsed
                    left as Either.Left
                    left.a should beInstanceOf<NoStartingHeader>()
                }
            }

            context("Null file") {
                val file = nullStart
                lateinit var parsed: Either<SequenceError, SeqFile>

                it("Should parse the file [$file]") {
                    parsed = SeqParser.parse(file)
                }

                it("Should verify parsing resulted in IOError") {
                    parsed should beInstanceOf<Either.Left<SequenceError>>()
                }

                it("Should verify parsing resulted is EmptyFile") {
                    val left = parsed
                    left as Either.Left
                    left.a should beInstanceOf<NoSuchFile>()
                }
            }

            context("Sequence with no starting description") {
                val file = missingDescriptionpPart
                lateinit var parsed: Either<SequenceError, SeqFile>

                it("Should to parse the file [$file]") {
                    parsed = SeqParser.parse(file)
                }

                it("Should verify parsing resulted in IOError") {
                    parsed should beInstanceOf<Either.Left<SequenceError>>()
                }

                it("Should verify parsing resulted is EmptyFile") {
                    val left = parsed
                    left as Either.Left
                    left.a should beInstanceOf<SequenceError.ParseError.NoStartingDescriptionError>()
                }
            }

            context("Bad file") {
                val file = FS["/nowhere$uuid"]
                lateinit var parse: Either<SequenceError, SeqFile>
                it("Should parse the imaginary file [$file]") {
                    parse = SeqParser.parse(file)
                }

                it("Should verify that parse result is type [${Either.Left::class.qualifiedName}]") {
                    parse should beInstanceOf<Either.Left<SequenceError>>()
                }

                it("Should verify the IOError type") {
                    val right = parse
                    right as Either.Left
                    right.a should beInstanceOf<NoSuchFile>()
                }

            }
        }
        context("Valid parse with syntax errors") {
            context("Description with no sequence body") {
                val file = missingSequencePart
                lateinit var parsed: Either<SequenceError, SeqFile>
                lateinit var seqFile: SeqFile
                lateinit var first: Sequence
                it("Should parse the file [$file]") {
                    parsed = SeqParser.parse(file)
                }

                it("Should verify parsing resulted in IOError") {
                    parsed should beInstanceOf<Either.Right<SeqFile>>()
                }

                it("Should verify parsing resulted is EmptyFile") {
                    val right = parsed
                    right as Either.Right
                    seqFile = right.b
                }

                it("Should get the sequence") {
                    first = seqFile.first()
                }

                it("Should verify the sequence is of type InvalidSequenceChain") {
                    first.body should beInstanceOf<SequenceChain.EmptySequenceChain>()
                }

                it("Should have the correct body") {
                    first.body.chain should be("==This Chain Has No Body==")
                }

            }

            context("Bad characters in sequence") {
                val file = badCharacters
                val expectedBadChar = setOf('0', '%', '{')
                lateinit var parsed: Either<SequenceError, SeqFile>
                lateinit var seqFile: SeqFile
                lateinit var first: Sequence
                lateinit var badCharError: SequenceError.ParseError
                it("Should to parse the file [$file]") {
                    parsed = SeqParser.parse(file)
                }

                it("Should that parse result is type [${Either.Left::class.qualifiedName}]") {
                    parsed should beInstanceOf<Either.Right<SeqFile>>()
                }

                it("Should extract the SeqFile}") {
                    val right = parsed
                    right as Either.Right
                    seqFile = right.b
                }

                it("Should get the first sequence") {
                    first = seqFile.first()
                }
                it("Should verify the sequence is of type InvalidSequenceChain") {
                    first.body should beInstanceOf<SequenceChain.InvalidSequenceChain>()
                }

                it("Should retrieve the error") {
                    val chain = first.body
                    chain as SequenceChain.InvalidSequenceChain
                    badCharError = chain.error
                }

                it("Should verify the error is of type BadChar") {
                    badCharError should beInstanceOf<BadChar>()
                }

                it("Should compare the char sets") {
                    val set = badCharError
                    set as BadChar
                    expectedBadChar should be(set.badChars)
                }
            }
        }
        context("Valid parse test") {
            val file = validSequence
            lateinit var parsed: Either<SequenceError, SeqFile>
            lateinit var seqFile: SeqFile
            lateinit var sequence: SequenceChain.ValidSequenceChain
            it("Should to parse the file [$file]") {
                parsed = SeqParser.parse(file)
            }

            it("Should verify parsing resulted in IOError") {
                parsed should beInstanceOf<Either.Right<SeqFile>>()
            }

            it("Should extract the SeqFile") {
                val right = parsed
                right as Either.Right
                seqFile = right.b
            }

            it("Should verify the seqFile has 1 element") {
                seqFile.size should be(1)
            }

            it("Should verify it contains a ValidSequenceChain") {
                seqFile.first().body should beInstanceOf<SequenceChain.ValidSequenceChain>()
            }

            it("Should extract the sequence") {
                sequence = seqFile
                    .first()
                    .body as SequenceChain.ValidSequenceChain
            }

            it("Should verify the chain body") {
                sequence.chain should be(validBody.remove("\n", ",", " "))
            }
        }
    }
})