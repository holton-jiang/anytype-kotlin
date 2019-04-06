package com.agileburo.anytype.feature_editor.presentation

import com.agileburo.anytype.core_utils.TrampolineSchedulerProvider
import com.agileburo.anytype.feature_editor.domain.*
import com.agileburo.anytype.feature_editor.presentation.converter.BlockContentTypeConverter
import com.agileburo.anytype.feature_editor.presentation.converter.BlockContentTypeConverterImpl
import com.agileburo.anytype.feature_editor.presentation.mvvm.EditorViewModel
import com.agileburo.anytype.feature_editor.ui.EditBlockAction
import com.agileburo.anytype.feature_editor.ui.EditorState
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

/**
 * Created by Konstantin Ivanov
 * email :  ki@agileburo.com
 * on 31.03.2019.
 */
class EditorViewModelTest {

    var blocks: List<Block> = listOf(
        Block(
            id = "1", contentType = ContentType.P,
            content = Content.Text(
                text = "111", marks = emptyList(),
                param = ContentParam.numberedListDefaultParam()
            ),
            parentId = ""
        ),
        Block(
            id = "2", contentType = ContentType.P,
            content = Content.Text(
                text = "222", marks = emptyList(),
                param = ContentParam.numberedListDefaultParam()
            ),
            parentId = ""
        ),
        Block(
            id = "3", contentType = ContentType.P,
            content = Content.Text(
                text = "333", marks = emptyList(),
                param = ContentParam.numberedListDefaultParam()
            ),
            parentId = ""
        ),
        Block(
            id = "4", contentType = ContentType.P,
            content = Content.Text(
                text = "444", marks = emptyList(),
                param = ContentParam.numberedListDefaultParam()
            ),
            parentId = ""
        )
    )

    lateinit var viewModel: EditorViewModel
    lateinit var contentTypeConverter: BlockContentTypeConverter

    @Mock
    lateinit var interactor: EditorInteractor

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        contentTypeConverter = BlockContentTypeConverterImpl()
        viewModel = EditorViewModel(
            interactor = interactor,
            contentTypeConverter = contentTypeConverter,
            schedulerProvider = TrampolineSchedulerProvider()
        )
    }

    @Test
    fun `should return list of blocks`() {

        Mockito.`when`(interactor.getBlocks()).thenReturn(Single.just(blocks))

        val testObserver = viewModel.observeState().test()

        viewModel.fetchBlocks()

        testObserver.assertNoErrors()
        testObserver.assertValueCount(1)
        testObserver.assertValue(EditorState.Result(blocks = blocks))
    }

    @Test
    fun `should return list with size - 1 after remove block`() {

        Mockito.`when`(interactor.getBlocks()).thenReturn(Single.just(blocks))

        val testObserver = viewModel.observeState().test()

        viewModel.fetchBlocks()
        viewModel.onContentTypeClicked(EditBlockAction.ArchiveBlock(blocks[0].id))
        testObserver.assertNoErrors()
        testObserver.assertValueCount(3)
        testObserver.assertValueAt(1, EditorState.Updates(blocks.subList(1, blocks.size)))
    }

    @Test
    fun `should hide toolbar after delete block`() {

        Mockito.`when`(interactor.getBlocks()).thenReturn(Single.just(blocks))

        val testObserver = viewModel.observeState().test()

        viewModel.fetchBlocks()
        viewModel.onContentTypeClicked(EditBlockAction.ArchiveBlock(blocks[1].id))

        testObserver.assertNoErrors()
        testObserver.assertValueAt(2, EditorState.HideToolbar)
    }
}