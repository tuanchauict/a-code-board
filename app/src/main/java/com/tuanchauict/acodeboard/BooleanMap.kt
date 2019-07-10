package com.tuanchauict.acodeboard

/**
 * Specialized [Map], where the key type is boolean. This map guarantees that values are defined for
 * both of the keys, `true` and `false`. It also means the size of this map is always 2.
 * With this constraint, this class assures that [get] returns a non-null value if [V] is non-null
 * in contrast to [Map.get].
 */
data class BooleanMap<V>(
    private val valueForTrue: V,
    private val valueForFalse: V
) : Map<Boolean, V> {

    override val entries: Set<Map.Entry<Boolean, V>> by publicationLazy {
        setOf(ImmutableEntry(true, valueForTrue), ImmutableEntry(false, valueForFalse))
    }

    override val keys: Set<Boolean> by publicationLazy { setOf(true, false) }

    override val size: Int = 2

    override val values: Collection<V> by publicationLazy { listOf(valueForTrue, valueForFalse) }

    override fun containsKey(key: Boolean): Boolean = true

    override fun containsValue(value: V): Boolean = value == valueForTrue || value == valueForFalse

    override fun get(key: Boolean): V = if (key) valueForTrue else valueForFalse

    override fun isEmpty(): Boolean = false

    override fun toString(): String = "{true=$valueForTrue, false=$valueForFalse}"

    private data class ImmutableEntry<out V>(
        override val key: Boolean,
        override val value: V
    ) : Map.Entry<Boolean, V>

    companion object {
        private fun <T> publicationLazy(initializer: () -> T): Lazy<T> =
            lazy(LazyThreadSafetyMode.PUBLICATION, initializer)
    }
}
