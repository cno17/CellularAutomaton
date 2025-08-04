package cellular

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import kotlin.math.abs

class AutomatonView(automaton: Automaton) : ImageView() {

    private val si = automaton.si
    private val sj = automaton.sj

    private val a = automaton.a
    private val b = automaton.b
    private val c = automaton.c

    private val p = automaton.p
    private val q = automaton.q

    val grayscale = SimpleBooleanProperty(false)
    val brightness = SimpleDoubleProperty(10.0)

    var update = ::updateImageFromState

    init {
        image = WritableImage(si, sj)
    }

    fun updateImageFromState() {
        val writer = (image as WritableImage).pixelWriter
        val gray = grayscale.value
        for (i in 0 until si) {
            for (j in 0 until sj) {
                val ap = (a[i, j, p] * 255).toInt()
                val bp = (b[i, j, p] * 255).toInt()
                val cp = (c[i, j, p] * 255).toInt()
                writer.setArgb(i, j, if (gray) argb(ap, ap, ap) else argb(ap, bp, cp))
            }
        }
    }

    fun updateImageFromDifference() {
        val writer = (image as WritableImage).pixelWriter
        val gray = grayscale.value
        val bright = brightness.value
        for (i in 0 until si) {
            for (j in 0 until sj) {
                val ad = abs(255 * (a[i, j, q] - a[i, j, p]) * bright).toInt()
                val bd = abs(255 * (b[i, j, q] - b[i, j, p]) * bright).toInt()
                val cd = abs(255 * (c[i, j, q] - c[i, j, p]) * bright).toInt()
                writer.setArgb(i, j, if (gray) argb(ad, ad, ad) else argb(ad, bd, cd))
            }
        }
    }

    private fun argb(r: Int, g: Int, b: Int) = (255 shl 24) or (r shl 16) or (g shl 8) or b
}