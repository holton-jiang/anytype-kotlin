package com.agileburo.anytype.core_ui

import com.agileburo.anytype.core_ui.common.Markup
import com.agileburo.anytype.core_ui.features.page.BlockView
import com.agileburo.anytype.core_ui.features.page.BlockViewDiffUtil
import org.junit.Test
import kotlin.test.assertEquals

class BlockViewDiffUtilTest {

    @Test
    fun `two blocks should be considered different based on their id`() {

        val index = 0

        val oldBlock = BlockView.Paragraph(
            id = MockDataFactory.randomUuid(),
            text = MockDataFactory.randomString()
        )

        val newBlock = oldBlock.copy(id = MockDataFactory.randomUuid())

        val old = listOf(oldBlock)

        val new = listOf(newBlock)

        val diff = BlockViewDiffUtil(old = old, new = new)

        assertEquals(expected = false, actual = diff.areItemsTheSame(index, index))
    }

    @Test
    fun `two blocks should be considered the same by their id but different by their content`() {

        val index = 0

        val id = MockDataFactory.randomUuid()

        val oldBlock: BlockView = BlockView.Paragraph(
            id = id,
            text = MockDataFactory.randomString()
        )

        val newBlock: BlockView = BlockView.Paragraph(
            id = id,
            text = MockDataFactory.randomString()
        )

        val old = listOf(oldBlock)

        val new = listOf(newBlock)

        val diff = BlockViewDiffUtil(old = old, new = new)

        assertEquals(expected = true, actual = diff.areItemsTheSame(index, index))
        assertEquals(expected = false, actual = diff.areContentsTheSame(index, index))
    }

    @Test
    fun `two blocks should be considered different based only on their UI-representation`() {

        val index = 0

        val id = MockDataFactory.randomUuid()
        val text = MockDataFactory.randomString()

        val oldBlock = BlockView.Paragraph(
            id = id,
            text = text
        )

        val newBlock = BlockView.HeaderOne(
            id = id,
            text = text
        )

        val old = listOf(oldBlock)

        val new = listOf(newBlock)

        val diff = BlockViewDiffUtil(old = old, new = new)

        assertEquals(expected = true, actual = diff.areItemsTheSame(index, index))
        assertEquals(expected = false, actual = diff.areContentsTheSame(index, index))
    }

    @Test
    fun `should return change payload containing only marks because text did not change`() {

        val index = 0

        val id = MockDataFactory.randomUuid()

        val text = MockDataFactory.randomString()

        val oldBlock: BlockView = BlockView.Paragraph(
            id = id,
            text = text,
            marks = emptyList()
        )

        val newBlock: BlockView = BlockView.Paragraph(
            id = id,
            text = text,
            marks = listOf(
                Markup.Mark(
                    type = Markup.Type.BOLD,
                    from = MockDataFactory.randomInt(),
                    to = MockDataFactory.randomInt()
                )
            )
        )

        val old = listOf(oldBlock)

        val new = listOf(newBlock)

        val diff = BlockViewDiffUtil(old = old, new = new)

        val payload = diff.getChangePayload(index, index)

        assertEquals(
            actual = payload,
            expected = BlockViewDiffUtil.MARKUP_CHANGED
        )
    }

    @Test
    fun `should return change payload containing only text because marks did not change`() {

        val index = 0

        val id = MockDataFactory.randomUuid()

        val marks = listOf(
            Markup.Mark(
                type = Markup.Type.BOLD,
                from = MockDataFactory.randomInt(),
                to = MockDataFactory.randomInt()
            )
        )

        val oldBlock: BlockView = BlockView.Paragraph(
            id = id,
            text = MockDataFactory.randomString(),
            marks = marks
        )

        val newBlock: BlockView = BlockView.Paragraph(
            id = id,
            text = MockDataFactory.randomString(),
            marks = marks
        )

        val old = listOf(oldBlock)

        val new = listOf(newBlock)

        val diff = BlockViewDiffUtil(old = old, new = new)

        val payload = diff.getChangePayload(index, index)

        assertEquals(
            actual = payload,
            expected = BlockViewDiffUtil.TEXT_CHANGED
        )
    }

    @Test
    fun `should return change payload containing text and marks because both changed`() {

        val index = 0

        val id = MockDataFactory.randomUuid()

        val oldBlock: BlockView = BlockView.Paragraph(
            id = id,
            text = MockDataFactory.randomString(),
            marks = emptyList()
        )

        val newBlock: BlockView = BlockView.Paragraph(
            id = id,
            text = MockDataFactory.randomString(),
            marks = listOf(
                Markup.Mark(
                    type = Markup.Type.BOLD,
                    from = MockDataFactory.randomInt(),
                    to = MockDataFactory.randomInt()
                )
            )
        )

        val old = listOf(oldBlock)

        val new = listOf(newBlock)

        val diff = BlockViewDiffUtil(old = old, new = new)

        val payload = diff.getChangePayload(index, index)

        assertEquals(
            actual = payload,
            expected = BlockViewDiffUtil.TEXT_AND_MARKUP_CHANGED
        )
    }
}