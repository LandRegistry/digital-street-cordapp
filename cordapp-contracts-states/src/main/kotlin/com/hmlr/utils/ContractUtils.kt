package com.hmlr.utils

import net.corda.core.contracts.Amount
import java.util.*
import kotlin.reflect.KProperty1

fun <T> checkPropertyInvariants(input: T, output: T, properties: Set<KProperty1<T, Any?>>): Boolean {
    return properties.all { property -> property.get(input) == property.get(output) }
}

interface SDLTCalculator {
    fun computeSDLT(purchasePrice: Amount<Currency>): Amount<Currency>
}

class BasicSDLT : SDLTCalculator{

    override fun computeSDLT(purchasePrice: Amount<Currency>): Amount<Currency> {
        // simplest logic is to check the range within which the purchasePrice is residing
        val long = when {
            purchasePrice.quantity in 0..12500000 -> 0
            purchasePrice.quantity in 12500001..25000000 -> computeValue(purchasePrice.quantity, 2)
            purchasePrice.quantity in 25000001..92500000 -> computeValue(purchasePrice.quantity, 5)
            purchasePrice.quantity in 92500001..150000000 -> computeValue(purchasePrice.quantity, 10)
            purchasePrice.quantity >= 150000001 -> computeValue(purchasePrice.quantity, 12)
            else -> throw Exception("Invalid range")
        }

        return Amount(long, Currency.getInstance("GBP"))
    }

    private fun computeValue(amount: Long, slab: Int): Long {
        return when (slab) {
            2 -> {
                (0.02 * (amount - 12500000)).toLong()
            }

            5 -> {
                250000 + (0.05 * (amount - 25000000)).toLong()
            }

            10 -> {
                250000 + 3375000 + (0.01 * (amount - 92500000)).toLong()

            }
            12 -> {
                250000 + 3375000 + 5750000 + (0.12 * (amount - 150000000)).toLong()
            }
            else -> throw Exception("Invalid slab found")
        }
    }
}