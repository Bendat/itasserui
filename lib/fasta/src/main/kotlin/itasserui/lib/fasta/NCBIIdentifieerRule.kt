@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package itasserui.lib.fasta

import itasserui.common.utils.AbstractSealedObject
import kotlin.reflect.full.primaryConstructor


sealed class NCBIIdentifierRule(val items: Int) : AbstractSealedObject() {
    object LCL : NCBIIdentifierRule(1)
    object BBS : NCBIIdentifierRule(1)
    object BBM : NCBIIdentifierRule(1)
    object GIM : NCBIIdentifierRule(1)
    object GB : NCBIIdentifierRule(2)
    object EMB : NCBIIdentifierRule(2)
    object PIR : NCBIIdentifierRule(2)
    object SP : NCBIIdentifierRule(2)
    object PAT : NCBIIdentifierRule(3)
    object PGP : NCBIIdentifierRule(3)
    object REF : NCBIIdentifierRule(2)
    object GNL : NCBIIdentifierRule(2)
    object GI : NCBIIdentifierRule(1)
    object DBJ : NCBIIdentifierRule(2)
    object PRF : NCBIIdentifierRule(2)
    object PDB : NCBIIdentifierRule(2)
    object TGP : NCBIIdentifierRule(2)
    object TGE : NCBIIdentifierRule(2)
    object TGD : NCBIIdentifierRule(2)
    object TR : NCBIIdentifierRule(2)

    fun identifierClass() =
        NCBIIdentifier
            .types
            .first { it.simpleName == simpleName }


    fun identifierInstance(vararg params: String) =
        identifierClass().primaryConstructor
            ?.call(*params)


    companion object {
        val types
            get() = NCBIIdentifierRule::class
                .sealedSubclasses
                .mapNotNull { it.objectInstance }
    }
}