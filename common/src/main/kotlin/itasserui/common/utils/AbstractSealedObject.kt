package itasserui.common.utils

/**
 * Sealed classes are often used in this codebase instead of enums
 * to enable pattern matching and generic type checking.
 *
 * The main purpose of this abstract class is to implement
 * a toString method that behaves like an Enums default toString
 */
abstract class AbstractSealedObject {
    open val simpleName get() = javaClass.simpleName
    override fun toString(): String {
        return javaClass.simpleName
    }
}