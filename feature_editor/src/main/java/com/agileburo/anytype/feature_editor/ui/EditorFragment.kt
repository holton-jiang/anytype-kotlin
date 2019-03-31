package com.agileburo.anytype.feature_editor.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.agileburo.anytype.feature_editor.R
import com.agileburo.anytype.feature_editor.disposedBy
import com.agileburo.anytype.feature_editor.domain.Block
import com.agileburo.anytype.feature_editor.domain.ContentType
import com.agileburo.anytype.feature_editor.presentation.mvvm.EditorViewModel
import com.agileburo.anytype.feature_editor.presentation.mvvm.EditorViewModelFactory
import com.agileburo.anytype.feature_editor.presentation.mapper.BlockViewMapper
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_editor.*
import javax.inject.Inject

abstract class EditorFragment : Fragment() {

    // TODO inject
    private val mapper by lazy { BlockViewMapper() }

    @Inject
    lateinit var factory: EditorViewModelFactory

    private val viewModel by lazy {
        ViewModelProviders.of(this, factory).get(EditorViewModel::class.java)
    }

    private val disposable = CompositeDisposable()

    private val blockAdapter by lazy {
        EditorAdapter(mutableListOf()) { block -> viewModel.onBlockClicked(block.id) }
    }

    abstract fun inject()

    override fun onAttach(context: Context?) {
        inject()
        super.onAttach(context)
        viewModel.observeState()
            .subscribe(this::handleState)
            .disposedBy(disposable)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
    }

    private fun initializeView() = with(blockList) {

        layoutManager = LinearLayoutManager(requireContext())

        adapter = blockAdapter

        editBlockToolbar.setMainActions(
            textClick = { viewModel.onContentTypeClicked(EditBlockAction.TextClick(it)) },
            header1Click = { viewModel.onContentTypeClicked(EditBlockAction.Header1Click(it)) },
            header2Click = { viewModel.onContentTypeClicked(EditBlockAction.Header2Click(it)) },
            header3Click = { viewModel.onContentTypeClicked(EditBlockAction.Header3Click(it)) },
            header4Click = { viewModel.onContentTypeClicked(EditBlockAction.Header4Click(it)) },
            hightLitedClick = { viewModel.onContentTypeClicked(EditBlockAction.HighLightClick(it)) },
            bulletedClick = { viewModel.onContentTypeClicked(EditBlockAction.BulletClick(it)) },
            numberedClick = { viewModel.onContentTypeClicked(EditBlockAction.NumberedClick(it)) },
            checkBoxClick = { viewModel.onContentTypeClicked(EditBlockAction.CheckBoxClick(it)) },
            codeClick = { viewModel.onContentTypeClicked(EditBlockAction.CodeClick(it)) },
            archiveClick = { viewModel.onContentTypeClicked(EditBlockAction.ArchiveBlock(it))}
        )

        setHasFixedSize(true)

        addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
    }

    override fun onPause() {
        disposable.clear()
        super.onPause()
    }

    private fun handleState(state: EditorState) = when (state) {
        is EditorState.Loading -> {}
        is EditorState.Result -> setBlocks(state.blocks)
        is EditorState.Update -> updateBlock(state.block)
        is EditorState.Updates -> render(state.blocks)
        is EditorState.ShowToolbar -> showToolbar(block = state.block, typesToHide = state.typesToHide)
        is EditorState.HideToolbar -> hideToolbar()
        is EditorState.Archive -> {}
    }

    private fun setBlocks(blocks: List<Block>) {
        (blockList.adapter as? EditorAdapter)?.setBlocks(blocks.map(mapper::mapToView))
    }
    private fun updateBlock(block: Block) {
        (blockList.adapter as? EditorAdapter)?.updateBlock(mapper.mapToView(block))
    }

    private fun render(blocks : List<Block>) {
        blockAdapter.update(blocks.map(mapper::mapToView))
    }

    private fun showToolbar(block: Block, typesToHide: Set<ContentType>) = with(editBlockToolbar) {
        show(initialBlock = block, typesToHide = typesToHide)
        visibility = View.VISIBLE
    }

    private fun hideToolbar() = with(editBlockToolbar) {
        visibility = View.INVISIBLE
    }
}