package com.commonsense.android.kotlin.views.databinding.adapters

import android.databinding.ViewDataBinding
import com.commonsense.android.kotlin.test.*
import org.junit.Ignore
import org.junit.Test
import org.robolectric.annotation.Config

/**
 * Created by Kasper Tvede on 28-07-2017.
 */

class TestRender(value: String)
    : RenderModel<String, ViewDataBinding>(value,
        { inflater, parent, attach -> TODO() },
        ViewDataBinding::class.java,
        { view, model, viewHolder -> })


@Config(manifest = Config.NONE)
class AbstractSearchableDataBindingRecyclerAdapterTest : BaseRoboElectricTest() {

    @Test
    fun testAddData() {
        val adapter = BaseSearchableDataBindingRecyclerAdapter<String>(context)
        adapter.addAll(listOf(), 0)
        adapter.addAll(listOf(), 10)
        adapter.addAll(listOf(), 5)
        adapter.sectionCount.assert(0, " //should ignore \"useless\" stuff.")
        adapter.itemCount.assert(0, " //should ignore \"useless\" stuff.")

        addItems(adapter, 10, 10)
        adapter.itemCount.assert(10, "adding 10 items should give 10 items in total.")

        adapter.setSection(listOf(renderWith("11")), 10)
        adapter.itemCount.assert(1)


        addItems(adapter, 10, 0)
        adapter.itemCount.assert(11, "adding 10 items, and leaving the other section should result in more items .")


        adapter.add(renderWith("test2"), 100)
        adapter.itemCount.assert(12)
        adapter.sectionCount.assert(3)
    }


    @Test
    fun testRemoveData() {
        val adapter = createAdapter()
        addItems(adapter, 50, 1)
        adapter.sectionCount.assert(1)
        adapter.itemCount.assert(50)

        adapter.removeAt(0, 1)
        adapter.sectionCount.assert(1)
        adapter.itemCount.assert(49)

        adapter.removeAt(48, 1)
        adapter.sectionCount.assert(1)
        adapter.itemCount.assert(48)

        adapter.getItem(10, 1).assertNotNullApply {
            adapter.remove(this, 1)
        }
        adapter.sectionCount.assert(1)
        adapter.itemCount.assert(47)


        //remeber we have removed element 0, and the last element.
        adapter.getItem(10, 1).assertNotNullApply {
            this.getValue().assertAs("12", "removing element 10 , should make element 11 come at row 10")
        }
        adapter.getItem(9, 1).assertNotNullApply {
            this.getValue().assertAs("10", "item before 10 should not have changed")
        }
        adapter.getItem(11, 1).assertNotNullApply {
            this.getValue().assertAs("13", "item after 10 should not have changed")
        }

        adapter.removeIn(0 until 10, 1)
        adapter.sectionCount.assert(1)
        adapter.itemCount.assert(37)


        val items = (0 until 20 step 2).mapNotNull { adapter.getItem(it, 1) }

        adapter.removeAll(items, 1)//removes 10 items , every 2 of the otherwise remaning onces.
        adapter.sectionCount.assert(1)
        adapter.itemCount.assert(27)

        adapter.removeSection(1)
        adapter.sectionCount.assert(0)
        adapter.itemCount.assert(0)
    }

    @Test
    fun simpleTestSectionsVisiblity() {

        val adapter = createAdapter()
        addItems(adapter, 20, 1)
        adapter.sectionCount.assert(1, "should have 1 section")
        adapter.itemCount.assert(20, "should have 20 items")

        adapter.getSectionSize(1).assertNotNullAndEquals(20, "section size should be same as inserted.")
        adapter.hideSection(1)
        adapter.itemCount.assert(0, "hidden sections does not count in the item count.")
        adapter.getSectionSize(1).assertNotNullAndEquals(20, "hidden sections are not removed.")
        adapter.sectionCount.assert(1, "should have 1 section even when hiding it.")

        adapter.showSection(1)
        adapter.itemCount.assert(20, "visible sections counts in item count.")
        adapter.getSectionSize(1).assertNotNullAndEquals(20, "all should be  back to the beginning")

        adapter.hideSection(0)
        adapter.itemCount.assert(20, "should not have changed")
        adapter.getSectionSize(1).assertNotNullAndEquals(20, "should not have changed")


        addItems(adapter, 30, 15)
        adapter.itemCount.assert(50, "should accumulate")
        adapter.getSectionSize(1).assertNotNullAndEquals(20, "should not have changed")
        adapter.getSectionSize(15).assertNotNullAndEquals(30, "should be as inserted")

        adapter.hideSection(15)
        adapter.itemCount.assert(20, "should count the 30 off")
        adapter.getSectionSize(1).assertNotNullAndEquals(20, "should not have changed")
        adapter.getSectionSize(15).assertNotNullAndEquals(30, "should not have changed")
        adapter.sectionCount.assert(2, "should still have the 2 sections")


        adapter.hideSection(15)
        adapter.itemCount.assert(20, "double hiding should have no effect")
        adapter.getSectionSize(1).assertNotNullAndEquals(20, "double hiding should have no effect")
        adapter.getSectionSize(15).assertNotNullAndEquals(30, "double hiding should have no effect")
        adapter.sectionCount.assert(2, "double hiding should have no effect")


        adapter.hideSection(1)
        adapter.itemCount.assert(0, "should be 0 when no sections are visible")
        adapter.getSectionSize(1).assertNotNullAndEquals(20, "should not have changed")
        adapter.getSectionSize(15).assertNotNullAndEquals(30, "should not have changed")


        adapter.showSection(17)
        adapter.itemCount.assert(0, "should be 0 when no sections are visible (17 does not exists)")
        adapter.getSectionSize(1).assertNotNullAndEquals(20, "should not have changed")
        adapter.getSectionSize(15).assertNotNullAndEquals(30, "should not have changed")


        adapter.showSection(15)
        adapter.itemCount.assert(30, "should be section 15 size.")
        adapter.getSectionSize(1).assertNotNullAndEquals(20, "showing the last section should not change result ")
        adapter.getSectionSize(15).assertNotNullAndEquals(30, "showing the last section should not change result ")
        adapter.sectionCount.assert(2, "showing the last section should not change result ")

        adapter.showSection(1)
        adapter.itemCount.assert(50, "should be section 15 size.")
        adapter.getSectionSize(1).assertNotNullAndEquals(20, "all should be visible")
        adapter.getSectionSize(15).assertNotNullAndEquals(30, "all should be visible")
        adapter.sectionCount.assert(2, "all should be visible")
    }

    @Test
    fun testSectionVisibilityWithManipulations() {
        val adapter = createAdapter()
        addItems(adapter, 18, 4)
        adapter.sectionCount.assert(1, "should have 1 section")
        adapter.itemCount.assert(18, "should have 18 items")

        adapter.hideSection(4)
        adapter.sectionCount.assert(1, "should have 1 section")
        adapter.itemCount.assert(0, "should have 0 items visible")

        adapter.add(renderWith("abc"), 4)
        adapter.getSectionSize(4).assertNotNullAndEquals(19, "real count should change")
        adapter.itemCount.assert(0, "should have not update visible count when its invisible")


        addItems(adapter, 10, 4)
        adapter.getSectionSize(4).assertNotNullAndEquals(29, "real count should change")
        adapter.itemCount.assert(0, "should have not update visible count when its invisible")


        adapter.removeAt(0, 4)
        adapter.getSectionSize(4).assertNotNullAndEquals(28, "real count should change")
        adapter.itemCount.assert(0, "should have not update visible count when its invisible")

        adapter.removeAt(50, 4)
        adapter.getSectionSize(4).assertNotNullAndEquals(28, "real count should not change when data should not change")
        adapter.itemCount.assert(0, "should have not update visible count when its invisible")


        adapter.removeAt(27, 4)
        adapter.getSectionSize(4).assertNotNullAndEquals(27, "real count should change")
        adapter.itemCount.assert(0, "should have not update visible count when its invisible")


        adapter.removeIn(0 until 20, 4)
        adapter.getSectionSize(4).assertNotNullAndEquals(7, "real count should change")
        adapter.itemCount.assert(0, "should have not update visible count when its invisible")

        adapter.removeSection(4)
        adapter.getSectionSize(4).assertNull("removed section should not exists")
        adapter.itemCount.assert(0, "should have not update visible count when it was invisible")


        addItems(adapter, 10, 1)
        addItems(adapter, 20, 2)
        addItems(adapter, 30, 3)

        adapter.itemCount.assert(60, "all new sections should be visible by default")


        adapter.hideSection(2)
        adapter.itemCount.assert(40, "should hide middle only")

        adapter.hideSection(3)
        adapter.itemCount.assert(10, "should hide last item")
        adapter.sectionCount.assert(3, "should be 3 sections")

        adapter.removeSection(3)
        adapter.itemCount.assert(10, "removing the section should not change anything")
        adapter.sectionCount.assert(2, "should be 2 sections after delete one")

        adapter.showSection(2)
        adapter.itemCount.assert(30, "showing section 2 should add item count again.")


        adapter.hideSection(1)

        adapter.setSection(createItems(5), 1)
        adapter.itemCount.assert(20, "only section 2")
        adapter.sectionCount.assert(2, "should be 2 sections after delete one")
        adapter.getSectionSize(1).assertNotNullAndEquals(5, "real count should change")


    }


    @Test
    fun testFilterBefore() {
        val adapter = createAdapter()
        adapter.filterBy("0") //make sure filter is applied before, to avoid "schedualing" issues in tests
        addItems(adapter, 10, 0)
        addItems(adapter, 10, 1)
        addItems(adapter, 10, 2)
        addItems(adapter, 10, 3)

        adapter.itemCount.assert(4, "should only have 4 '0', 1 for each section")
        adapter.sectionCount.assert(4)

        adapter.add(renderWith("0"), 0)

        adapter.itemCount.assert(5, "should still work for exising sections")

        adapter.hideSection(1)
        adapter.itemCount.assert(4, "hiding should hide the element")

        adapter.removeAt(0, 0)
        adapter.itemCount.assert(3, "removing should remove.")

        adapter.removeSection(2)
        adapter.itemCount.assert(2, "removing should remove.")

        adapter.removeSection(1)
        adapter.itemCount.assert(2, "removing hidden section should not change count")


        adapter.setSection(listOf(), 3)
        adapter.itemCount.assert(1, "setting section to nothing should change count ")

        adapter.setSection(listOf(renderWith("0"), renderWith("1"), renderWith("0")), 3)
        adapter.itemCount.assert(3, "adding 2 items with filter should add 2 to count ")

        adapter.setSection(listOf(renderWith("3"), renderWith("1"), renderWith("0")), 3)
        adapter.itemCount.assert(2, "changing items, should be reflected (-1 in total) ")

        adapter.insert(renderWith("0"), 0, 0)
        adapter.itemCount.assert(3, "inserting should also work")

        adapter.insert(renderWith("1"), 0, 0)
        adapter.itemCount.assert(3, "inserting not filter valid item, should not update value")


        adapter.insertAll(createItems(5), 0, 0)
        adapter.itemCount.assert(4, "inserting as list")

        adapter.hideSection(0)
        adapter.itemCount.assert(1, "hiding multiple items that are accepted by the filter")

        adapter.showSection(0)
        adapter.itemCount.assert(4, "all should be as before hiding the section.")


    }

    @Test
    @Ignore
    fun testFilterAsync() {

        //TODO
        //this requiers a dataobserver that "unlocks" us (thus we are to make "changes" )

    }


    @Test
    fun testSectionVisibility() {
        val adapter = createAdapter()
        addItems(adapter, 50, 4)

        adapter.itemCount.assert(50)

        adapter.hideSection(4)
        adapter.isSectionVisible(4).assert(false)
        adapter.itemCount.assert(0)

        adapter.showSection(3)
        adapter.isSectionVisible(4).assert(false)
        adapter.itemCount.assert(0)



        adapter.showSection(4)
        adapter.isSectionVisible(4).assert(true)
        adapter.itemCount.assert(50)


        adapter.toggleSectionVisibility(4)
        adapter.isSectionVisible(4).assert(false)
        adapter.itemCount.assert(0)


        adapter.toggleSectionVisibility(4)
        adapter.isSectionVisible(4).assert(true)
        adapter.itemCount.assert(50)


        adapter.setSectionVisibility(4, true)
        adapter.isSectionVisible(4).assert(true)
        adapter.itemCount.assert(50)


        adapter.setSectionVisibility(4, false)
        adapter.isSectionVisible(4).assert(false)
        adapter.itemCount.assert(0)



        adapter.setSectionVisibility(0, false)
        adapter.itemCount.assert(0)

        addItems(adapter, 20, 0)
        adapter.itemCount.assert(20)

        adapter.setSectionVisibility(0, false)
        adapter.itemCount.assert(0)

        adapter.setSectionVisibility(0, true)
        adapter.itemCount.assert(20)

        adapter.setSection(listOf(), 0)
        adapter.itemCount.assert(0)

        adapter.setSection(createItems(1), 4)
        adapter.itemCount.assert(0)

        adapter.showSection(4)
        adapter.itemCount.assert(1)

        adapter.toggleSectionVisibility(4)
        adapter.itemCount.assert(0)

        addItems(adapter, 5, 1)
        addItems(adapter, 7, 10)
        adapter.itemCount.assert(12)

        adapter.toggleSectionsVisibility(1, 10, 4)
        adapter.setSectionVisibility(0, true)
        adapter.setSectionVisibility(1, false)
        adapter.setSectionVisibility(10, false)
        adapter.itemCount.assert(1)


        adapter.toggleSectionsVisibility(1, 10, 4)
        adapter.setSectionVisibility(0, false)
        adapter.setSectionVisibility(1, true)
        adapter.setSectionVisibility(10, true)
        adapter.itemCount.assert(12)


    }


    private fun addItems(adapter: BaseSearchableDataBindingRecyclerAdapter<String>, itemsSize: Int, inSection: Int) {
        for (i in 0 until itemsSize) {
            adapter.add(renderWith(i.toString()), inSection)
        }
    }

    private fun createItems(itemsSize: Int): List<RenderSearchableModelItem<String, ViewDataBinding, String>> =
            (0 until itemsSize).map { renderWith(it.toString()) }


    private fun createAdapter() = BaseSearchableDataBindingRecyclerAdapter<String>(context)

    private fun renderWith(value: String): RenderSearchableModelItem<String, ViewDataBinding, String> = TestRender(value).toSearchable(String::equals)
}