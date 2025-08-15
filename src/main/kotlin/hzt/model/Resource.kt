package hzt.model

import java.io.InputStream
import java.net.URL
import java.util.*

class Resource
@JvmOverloads constructor(private val name: String, val url: URL? = null) : Comparable<Resource> {

    override fun compareTo(other: Resource): Int {
        return name.compareTo(other.name)
    }

    override fun equals(other: Any?): Boolean {
        return this === other || other is Resource && name == other.name
    }

    @Throws(exceptionClasses = [IllegalStateException::class])
    fun <R> useInputStream(useInputStream: (InputStream) -> R) = url?.openStream()?.use {
        useInputStream(it)
    }

    override fun hashCode(): Int {
        return Objects.hash(name)
    }

    override fun toString(): String {
        return name
    }
}