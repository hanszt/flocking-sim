package hzt.model

import java.util.*
import kotlin.jvm.JvmOverloads

class Resource @JvmOverloads constructor(private val name: String, val pathToResource: String = "") :
    Comparable<Resource> {
    override fun compareTo(other: Resource): Int {
        return name.compareTo(other.name)
    }

    override fun equals(other: Any?): Boolean {
        return this === other || other is Resource && name == other.name
    }

    override fun hashCode(): Int {
        return Objects.hash(name)
    }

    override fun toString(): String {
        return name
    }
}
