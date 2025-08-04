package cellular.util

import kotlin.math.sqrt

// thanks to Stefan Gustavson

open class SimplexNoise {

    protected val grad3 = arrayOf<Grad>(
        Grad(1.0, 1.0, 0.0), Grad(-1.0, 1.0, 0.0), Grad(1.0, -1.0, 0.0), Grad(-1.0, -1.0, 0.0),
        Grad(1.0, 0.0, 1.0), Grad(-1.0, 0.0, 1.0), Grad(1.0, 0.0, -1.0), Grad(-1.0, 0.0, -1.0),
        Grad(0.0, 1.0, 1.0), Grad(0.0, -1.0, 1.0), Grad(0.0, 1.0, -1.0), Grad(0.0, -1.0, -1.0)
    )

    protected val grad4 = arrayOf<Grad>(
        Grad(0.0, 1.0, 1.0, 1.0), Grad(0.0, 1.0, 1.0, -1.0), Grad(0.0, 1.0, -1.0, 1.0), Grad(0.0, 1.0, -1.0, -1.0),
        Grad(0.0, -1.0, 1.0, 1.0), Grad(0.0, -1.0, 1.0, -1.0), Grad(0.0, -1.0, -1.0, 1.0), Grad(0.0, -1.0, -1.0, -1.0),
        Grad(1.0, 0.0, 1.0, 1.0), Grad(1.0, 0.0, 1.0, -1.0), Grad(1.0, 0.0, -1.0, 1.0), Grad(1.0, 0.0, -1.0, -1.0),
        Grad(-1.0, 0.0, 1.0, 1.0), Grad(-1.0, 0.0, 1.0, -1.0), Grad(-1.0, 0.0, -1.0, 1.0), Grad(-1.0, 0.0, -1.0, -1.0),
        Grad(1.0, 1.0, 0.0, 1.0), Grad(1.0, 1.0, 0.0, -1.0), Grad(1.0, -1.0, 0.0, 1.0), Grad(1.0, -1.0, 0.0, -1.0),
        Grad(-1.0, 1.0, 0.0, 1.0), Grad(-1.0, 1.0, 0.0, -1.0), Grad(-1.0, -1.0, 0.0, 1.0), Grad(-1.0, -1.0, 0.0, -1.0),
        Grad(1.0, 1.0, 1.0, 0.0), Grad(1.0, 1.0, -1.0, 0.0), Grad(1.0, -1.0, 1.0, 0.0), Grad(1.0, -1.0, -1.0, 0.0),
        Grad(-1.0, 1.0, 1.0, 0.0), Grad(-1.0, 1.0, -1.0, 0.0), Grad(-1.0, -1.0, 1.0, 0.0), Grad(-1.0, -1.0, -1.0, 0.0)
    )

    protected val p = shortArrayOf(
        151, 160, 137, 91, 90, 15,
        131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23,
        190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33,
        88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166,
        77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244,
        102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196,
        135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123,
        5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42,
        223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9,
        129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228,
        251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107,
        49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254,
        138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180
    )

    // To remove the need for index wrapping, double the permutation table length
    protected val perm = ShortArray(512)
    protected val permMod12 = ShortArray(512)

    // Skewing and unskewing factors for 2, 3, and 4 dimensions
    protected val F2 = (sqrt(3.0) - 1.0) / 2.0  // todo: correct?
    protected val G2 = (3.0 - sqrt(3.0)) / 6.0
    protected val F3 = 1.0 / 3.0
    protected val G3 = 1.0 / 6.0
    protected val F4 = (sqrt(5.0) - 1.0) / 4.0
    protected val G4 = (5.0 - sqrt(5.0)) / 20.0

    init {
        for (i in 0 .. 511) {
            perm[i] = p[i and 255]
            permMod12[i] = (perm[i] % 12).toShort()
        }
    }

    // This method is a *lot* faster than using (int) Math.floor(x)
    fun fastfloor(x: Double): Int {
        val xi = x.toInt()
        return if (x < xi) xi - 1 else xi
    }

    protected fun dot(g: Grad, x: Double, y: Double): Double {
        return g.x * x + g.y * y
    }

    protected fun dot(g: Grad, x: Double, y: Double, z: Double): Double {
        return g.x * x + g.y * y + g.z * z
    }

    protected fun dot(g: Grad, x: Double, y: Double, z: Double, w: Double): Double {
        return g.x * x + g.y * y + g.z * z + g.w * w
    }

    // Inner class to speed up gradient computations (array access is a lot slower than member access)
    class Grad(var x: Double, var y: Double, var z: Double, var w: Double = 0.0)

}