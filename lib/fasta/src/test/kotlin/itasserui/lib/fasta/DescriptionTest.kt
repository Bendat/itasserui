package itasserui.lib.fasta

import arrow.core.Either
import io.kotlintest.be
import io.kotlintest.matchers.beGreaterThan
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.common.extensions.map
import itasserui.common.utils.Fake
import itasserui.lib.fasta.description.Description
import itasserui.lib.fasta.description.NCBIIdentifier
import itasserui.lib.fasta.description.NCBIIdentifierRule
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

class DescriptionParserTest : DescribeSpec({
    describe("NCBI Identifier Rules should map correctly to their corresponding NCBI Identifier.") {
        it("Verifies there are rules to test") {
            NCBIIdentifierRule.types.size should beGreaterThan(5)
        }
        NCBIIdentifierRule.types.forEach { rule ->
            context("Sealed class mapping for $rule") {
                lateinit var identifier: KClass<out NCBIIdentifier>
                it("Should verify rule $rule should match to an ${NCBIIdentifier::class.simpleName}") {
                    identifier = rule.identifierClass()
                }

                it("Should verify the discovered identifier has the correct number of fields") {
                    identifier.memberProperties.size.minus(3) should be(rule.items)
                }

                it("Should construct an object instance for each") {
                    val params = rule.items.map { Fake.ancient().primordial() }.toTypedArray()
                    println(rule.identifierInstance(*  params)?.raw)
                }
            }
        }
    }


    describe("Description Parser tests") {
        context("Two known identifiers") {
            val parser =
                Description(">gi|158333234|ref|YP_001514406.1| NUDIX hydrolase [Acaryochloris marina MBIC11017]")

            it("Runs the parser") {
                parser.parse() as Either.Right
            }

            it("Checks the description") {
                parser.value should be("NUDIX hydrolase [Acaryochloris marina MBIC11017]")
            }
        }

        context("Pipe but unknown identifier") {
            val parser = Description(">NUDIX | hydrolase [Acaryochloris marina MBIC11017]")
            it("Runs the parser") {
                parser.parse() as Either.Right
            }

            it("Checks the description") {
                parser.value should be ("NUDIX | hydrolase [Acaryochloris marina MBIC11017]")
            }
        }

        context("One known, one unknown identifiers") {
            val parser =
                Description(">it|158333234|ref|YP_001514406.1| NUDIX hydrolase [Acaryochloris marina MBIC11017]")

            it("Runs the parser") {
                parser.parse() as Either.Right
            }

            it("Checks the description") {
                parser.value should be("NUDIX hydrolase [Acaryochloris marina MBIC11017]")
            }
        }

        context("Empty Pipes") {
            val parser =
                Description(">| | | |")

            it("Runs the parser") {
                parser.parse() as Either.Right
            }

            it("Checks the description") {
                parser.value should be("| | | |")
            }
        }
    }
})