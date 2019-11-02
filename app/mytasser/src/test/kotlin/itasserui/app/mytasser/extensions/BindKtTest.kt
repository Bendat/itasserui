package itasserui.app.mytasser.extensions

import io.kotlintest.be
import io.kotlintest.should
import io.kotlintest.specs.DescribeSpec
import itasserui.app.mytasser.lib.extensions.bind
import itasserui.app.mytasser.lib.extensions.bindProperty
import javafx.collections.FXCollections
import lk.kotlin.observable.list.ObservableListWrapper
import lk.kotlin.observable.list.sorting
import lk.kotlin.observable.property.StandardObservableProperty

class Foo(val name: String, initial: Int, priority: Int) {
    val prop = StandardObservableProperty<Int>(initial)
    var value by prop

    val priorityProperty = StandardObservableProperty(priority)
    var priority by priorityProperty
    override fun toString(): String {
        return "Foo(name='$name', value=$value, priority=$priority"
    }

    companion object {
        val sorter = fun(
            first: Foo,
            second: Foo
        ): Boolean {
            return if (first.priority == second.priority) {
                val comp1 = first.value
                val comp2 = second.value
                when {
                    comp1 == comp2 -> true
                    comp1 < comp2 -> true
                    else -> false
                }
            } else {
                when {
                    first.priority > second.priority -> true
                    else -> false
                }
            }
        }
    }
}

class BindKtTest : DescribeSpec() {
    init {
        describe("bindPriority") {
            val lkobservable = ObservableListWrapper(
                mutableListOf(
                    Foo("First", 1, 0),
                    Foo("Second", 2, 0),
                    Foo("Third", 3, 0),
                    Foo("Fourth", 4, 0)
                )
            ).sorting(Foo.sorter).bindProperty { it.priorityProperty }

            val fxobservable = FXCollections.observableArrayList(lkobservable)
                .bind(lkobservable)

            it("Verifies thee default state of the list") {
                lkobservable.toList() should be(fxobservable.toList())
                lkobservable[0] should be(fxobservable[0])
                lkobservable[3] should be(fxobservable[3])
            }

            it("Verifies the lists are both reordered when prioity updated") {
                val end = fxobservable[3]
                end.priority = 2
                fxobservable[0] should be(end)
                lkobservable[0] should be(fxobservable[0])
            }
        }
    }
}