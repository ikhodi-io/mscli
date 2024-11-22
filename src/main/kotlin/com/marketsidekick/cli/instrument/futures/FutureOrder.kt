package com.marketsidekick.cli.instrument.futures

import kotlinx.serialization.Serializable

enum class OrderType {
    Buy, Sell
}

@Serializable
data class FutureOrder(
    val orderType: OrderType
)