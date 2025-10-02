package com.dtp.fabricate.runtime

sealed interface Either<V, E> {
    data class Value<V, E>(val value: V) : Either<V, E>
    data class Error<V, E>(val value: E) : Either<V, E>
}

inline fun <V, E> Either<V, E>.onValue(block: (V) -> Unit) {
    if (this is Either.Value) block(this.value)
}

inline fun <V, E> Either<V, E>.onError(block: (E) -> Unit) {
    if (this is Either.Error) block(this.value)
}

inline fun <V, E> Either<V, E>.either(
    onValue: (V) -> Unit,
    onError: (E) -> Unit,
) {
    when (this) {
        is Either.Value ->
            onValue(this.value)
        is Either.Error ->
            onError(this.value)
    }
}