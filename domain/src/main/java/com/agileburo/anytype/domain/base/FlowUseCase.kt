package com.agileburo.anytype.domain.base

import kotlinx.coroutines.flow.Flow

abstract class FlowUseCase<out Type, in Params> where Type : Any {
    abstract suspend fun build(params: Params? = null): Flow<Type>
    abstract suspend fun stream(receiver: suspend (Type) -> Unit)
}