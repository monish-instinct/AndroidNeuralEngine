@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package com.skynetbee.neuralengine

operator fun String.get(index: Int): Char {
    return this[index]
}
