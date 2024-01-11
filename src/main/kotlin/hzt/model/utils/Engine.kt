package hzt.model.utils

import hzt.model.entity.boid.Boid
import javafx.beans.property.FloatProperty
import javafx.beans.property.SimpleFloatProperty
import javafx.geometry.Point2D

class Engine {
    private val pullFactor: FloatProperty = SimpleFloatProperty()
    private val repelFactor: FloatProperty = SimpleFloatProperty()

    /*
     * Fg = (G * m_self * m_other) / r^2
     * F_res = m_self * a ->
     * <p>
     * Fg = F_res
     * <p>
     * a = (G * m_other) / r^2
     */
    abstract class FlockingSim {
        fun getTotalAcceleration(self: Boid, boidSet: Iterable<Boid>): Point2D {
            var totalAcceleration = Point2D.ZERO
            for (other in boidSet) {
                val acceleration = getAccelerationBetweenTwoBalls(self, other)
                totalAcceleration = totalAcceleration.add(acceleration)
            }
            return totalAcceleration
        }

        abstract fun getAccelerationBetweenTwoBalls(self: Boid, other: Boid): Point2D
        abstract override fun toString(): String
    }

    val type1: FlockingSim = object : FlockingSim() {
        override fun getAccelerationBetweenTwoBalls(self: Boid, other: Boid): Point2D {
            val vectorSelfToOther = other.translation.subtract(self.translation)
            val unitVectorInAccDir = vectorSelfToOther.normalize()
            val distance = vectorSelfToOther.magnitude().toFloat()
            val part2Formula = other.mass.toFloat() / (distance * distance)
            val attractionMagnitude = pullFactor.get() * part2Formula
            val repelMagnitude = repelFactor.get() * part2Formula
            val repelDistance = self.repelRadius + other.repelRadius
            return if (distance <= repelDistance) unitVectorInAccDir.multiply(-repelMagnitude.toDouble()) else unitVectorInAccDir.multiply(
                attractionMagnitude.toDouble()
            )
        }

        override fun toString(): String {
            return "Engine type 1"
        }
    }
    val type2: FlockingSim = object : FlockingSim() {
        override fun getAccelerationBetweenTwoBalls(self: Boid, other: Boid): Point2D {
            val vectorSelfToOther = other.translation.subtract(self.translation)
            val unitVectorInAccDir = vectorSelfToOther.normalize()
            val distance = vectorSelfToOther.magnitude().toFloat()
            val part2Formula = other.mass.toFloat() / (distance * distance)
            val repelDistance = self.repelRadius + other.repelRadius
            val curveFitConstant =
                (other.mass * (pullFactor.get() + repelFactor.get()) / (repelDistance * repelDistance)).toFloat()
            val attractionMagnitude = pullFactor.get() * part2Formula
            val repelMagnitude = -repelFactor.get() * part2Formula + curveFitConstant
            return if (distance <= repelDistance) unitVectorInAccDir.multiply(repelMagnitude.toDouble()) else unitVectorInAccDir.multiply(
                attractionMagnitude.toDouble()
            )
        }

        override fun toString(): String {
            return "Engine type 2"
        }
    }
    val type3: FlockingSim = object : FlockingSim() {
        override fun getAccelerationBetweenTwoBalls(self: Boid, other: Boid): Point2D {
            val multiplier = 10
            val vectorSelfToOther = other.translation.subtract(self.translation)
            val unitVectorInAccDir = vectorSelfToOther.normalize()
            val distance = vectorSelfToOther.magnitude().toFloat()
            val repelDistance = self.repelRadius + other.repelRadius
            return if (distance <= repelDistance) unitVectorInAccDir.multiply((-repelFactor.get() * multiplier).toDouble()) else unitVectorInAccDir.multiply(
                (pullFactor.get() * multiplier).toDouble()
            )
        }

        override fun toString(): String {
            return "Engine type 3"
        }
    }

    fun pullFactorProperty(): FloatProperty {
        return pullFactor
    }

    fun repelFactorProperty(): FloatProperty {
        return repelFactor
    }

    companion object {
        const val DENSITY = 100.0 // kg/m^3
    }
}