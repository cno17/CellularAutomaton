package cellular.util

class FloatArray3(val si: Int, val sj: Int, val sk: Int) {

    private val sij = si * sj
    private val data = FloatArray(sij * sk)

    operator fun get(i: Int, j: Int, k: Int): Float {
        return data[flatten(i, j, k)]
    }

    operator fun set(i: Int, j: Int, k: Int, v: Float) {
        data[flatten(i, j, k)] = v
    }

    private fun flatten(i: Int, j: Int, k: Int) = i + j * si + k * sij

    override fun toString() = data.toList().toString()
}