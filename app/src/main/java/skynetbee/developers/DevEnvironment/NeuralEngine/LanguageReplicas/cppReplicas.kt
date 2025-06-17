@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package skynetbee.developers.DevEnvironment

operator fun String.get(index: Int): Char {
    return this[index]
}
