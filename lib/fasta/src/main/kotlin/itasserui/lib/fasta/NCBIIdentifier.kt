@file:Suppress("unused")

package itasserui.lib.fasta

import com.fasterxml.jackson.annotation.JsonIgnore
import itasserui.common.utils.AbstractSealedObject
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties


sealed class NCBIIdentifier : AbstractSealedObject() {
    override val simpleName: String
        get() = super.simpleName.toLowerCase()

    @get:JsonIgnore
    val rawFormat
        get() = this::class.memberProperties.map { it.name }.let {
            it.toMutableList().also { ls ->
                ls.add(0, simpleName)
                ls.removeAll(listOf("rawFormat", "simpleName", "raw"))
            }
        }.joinToString("|")

    @Suppress("UNCHECKED_CAST")
    val raw
        get() = this::class
            .memberProperties
            .filter {
                when (it.name) {
                    "rawFormat", "simpleName", "raw" -> false
                    else -> true
                }
            }.map {
                it as KProperty1<Any, *>
            }.map {
                it.invoke(this)
            }.let {
                it.toMutableList().also { ls ->
                    ls.add(0, simpleName)
                }
            }.joinToString("|")

    data class GI(val reference: String) : NCBIIdentifier()
    data class LCL(val reference: String) : NCBIIdentifier()
    data class BBS(val reference: String) : NCBIIdentifier()
    data class BBM(val reference: String) : NCBIIdentifier()
    data class GIM(val reference: String) : NCBIIdentifier()

    data class PDB(val entry: String, val chain: String) : NCBIIdentifier()
    data class SP(val accession: String, val name: String) : NCBIIdentifier()
    data class PIR(val accession: String, val name: String) : NCBIIdentifier()

    data class TR(val accession: String, val name: String) : NCBIIdentifier()
    data class REF(val accession: String, val name: String) : NCBIIdentifier()
    data class PRF(val accession: String, val name: String) : NCBIIdentifier()
    data class TGP(val accession: String, val name: String) : NCBIIdentifier()
    data class TGE(val accession: String, val name: String) : NCBIIdentifier()
    data class TGD(val accession: String, val name: String) : NCBIIdentifier()

    data class GB(val accession: String, val locus: String) : NCBIIdentifier()
    data class DBJ(val accession: String, val locus: String) : NCBIIdentifier()
    data class EMB(val accession: String, val locus: String) : NCBIIdentifier()

    data class GNL(val database: String, val reference: String) : NCBIIdentifier()

    data class PAT(
        val country: String,
        val patent: String,
        val sequenceNumber: String
    ) : NCBIIdentifier()

    data class PGP(
        val country: String,
        val applicationNumber: String,
        val sequenceNumber: String
    ) : NCBIIdentifier()

    override fun toString(): String {
        return super.toString().toLowerCase()
    }

    companion object {
        val types
            get() = NCBIIdentifier::class
                .sealedSubclasses
    }
}