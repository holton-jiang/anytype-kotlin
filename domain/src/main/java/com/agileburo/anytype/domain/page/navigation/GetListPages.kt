package com.agileburo.anytype.domain.page.navigation

import com.agileburo.anytype.domain.base.BaseUseCase
import com.agileburo.anytype.domain.base.Either
import com.agileburo.anytype.domain.block.repo.BlockRepository

open class GetListPages(private val repo: BlockRepository) :
    BaseUseCase<GetListPages.Response, Unit>() {

    override suspend fun run(params: Unit): Either<Throwable, Response> = safe {
        repo.getListPages().filterNot { it.fields.isArchived == true }.let { result ->
            Response(
                listPages = result
            )
        }
    }

    data class Response(
        val listPages: List<DocumentInfo>
    )
}