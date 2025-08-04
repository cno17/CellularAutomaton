package cellular

import cellular.util.FloatArray3
import cellular.util.SimplexNoise2
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleFloatProperty
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class Automaton(val si: Int, val sj: Int) {

    val a = FloatArray3(si, sj, 2)
    val b = FloatArray3(si, sj, 2)
    val c = FloatArray3(si, sj, 2)

    val alpha = SimpleFloatProperty(rnd(0.0, 1.0).toFloat())
    val beta = SimpleFloatProperty(rnd(0.0, 1.0).toFloat())
    val gamma = SimpleFloatProperty(rnd(0.0, 1.0).toFloat())

    val noiseFrequency = SimpleDoubleProperty(rnd(0.001, 0.010))

    var p = 0
    var q = 1

    val oscillation get(): Int {
        var res = 0f
        for (i in 0 until si) {
            for (j in 0 until sj) {
                res += abs(a[i, j, 1] - a[i, j, 0])
                res += abs(b[i, j, 1] - b[i, j, 0])
                res += abs(c[i, j, 1] - c[i, j, 0])
            }
        }
        return res.toInt()
    }


    fun init() {
        val xa = rnd(0.0, 100.0)
        val xb = rnd(0.0, 100.0)
        val xc = rnd(0.0, 100.0)
        val ya = rnd(0.0, 100.0)
        val yb = rnd(0.0, 100.0)
        val yc = rnd(0.0, 100.0)
        val fv = noiseFrequency.value
        for (i in 0 until si) {
            val x = i * fv
            for (j in 0 until sj) {
                val y = j * fv
                a[i, j, p] = noise(xa + x, ya + y)
                b[i, j, p] = noise(xb + x, yb + y)
                c[i, j, p] = noise(xc + x, yc + y)
            }
        }
    }

    fun step() {
        val av = alpha.value
        val bv = beta.value
        val cv = gamma.value
        for (i in 0 until si) {
            for (j in 0 until sj) {
                val ma = mean(i, j, a)
                val mb = mean(i, j, b)
                val mc = mean(i, j, c)
                a[i, j, q] = combine(ma, mb, mc, av, cv)
                b[i, j, q] = combine(mb, mc, ma, bv, av)
                c[i, j, q] = combine(mc, ma, mb, cv, bv)
            }
        }
        p = (p + 1) % 2
        q = (q + 1) % 2
    }

    private fun mean(i: Int, j: Int, mat: FloatArray3): Float {
        var res = 0f
        for (x in i - 1 .. i + 1) {
            val u = (x + si) % si
            for (y in j - 1 .. j + 1) {
                val v = (y + sj) % sj
                res += mat[u, v, p]
            }
        }
        return res / 9
    }

    private fun combine(a: Float, b: Float, c: Float, f1: Float, f2: Float): Float {
        val v = a + a * (f1 * b - f2 * c)
        return max(0f, min(1f, v))
    }

    private fun rnd(min: Double, max: Double) = min + (max - min) * Random.nextDouble()
    private fun noise(x: Double, y: Double) = ((SimplexNoise2(x, y) + 1.0) * 0.5).toFloat()
}