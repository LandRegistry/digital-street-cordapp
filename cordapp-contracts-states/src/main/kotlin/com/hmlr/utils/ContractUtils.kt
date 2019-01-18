package com.hmlr.utils

import kotlin.reflect.KProperty1

fun <T> checkPropertyInvariants(input: T, output: T, properties: Set<KProperty1<T, Any?>>): Boolean {
    return properties.all { property -> property.get(input) == property.get(output) }
}