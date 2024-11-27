package com.marketsidekick.cli.instrument.futures

import kotlinx.serialization.Serializable

@Serializable
data class FutureOrder(
    val triggerId: String,
    val transactionType: String,
    val orderType: String,
    val quantity: String,
    val price: String,
    val symbol: String,
    val productType: String,
    val time: String,
    val broker: String
)