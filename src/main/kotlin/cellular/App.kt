package cellular

import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.beans.property.SimpleLongProperty
import javafx.embed.swing.SwingFXUtils
import javafx.geometry.Insets
import javafx.geometry.Rectangle2D
import javafx.scene.Scene
import javafx.scene.layout.HBox
import javafx.scene.robot.Robot
import javafx.stage.Stage
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.io.File
import java.awt.image.BufferedImage
import javax.imageio.ImageIO


fun main() = Application.launch(App::class.java)

class App : Application() {

    val automaton = Automaton(600, 600)
    val view = AutomatonView(automaton)
    val timer = Timer()

    lateinit var stage: Stage

    override fun start(primaryStage: Stage) {
        stage = primaryStage
        automaton.init()
        val box = HBox(10.0, view, SettingsBox(this))
        box.padding = Insets(10.0)
        stage.scene = Scene(box)
        stage.title = "Belousov-Zhabotinsky Reaction"
        stage.show()
        timer.start()
    }

    fun makeSnapshot(width: Double = stage.width) {
        val rect = Rectangle2D(stage.x, stage.y, stage.width, stage.height)
        val img1 = Robot().getScreenCapture(null, rect, true)
        val img2 = SwingFXUtils.fromFXImage(img1, null)
        val img3 = scaleToWidth(img2, width)
        val file = File("src/main/resources/Image.png")
        file.createNewFile()
        ImageIO.write(img3, "png", file)
    }

    fun scaleToWidth(img: BufferedImage, width: Double): BufferedImage {
        val w = img.width.toDouble()
        val h = img.height.toDouble()
        val s = width / w
        val res = BufferedImage((w * s).toInt(), (h * s).toInt() , img.type)
        val trafo = AffineTransform.getScaleInstance(s, s)
        val op = AffineTransformOp(trafo, AffineTransformOp.TYPE_BILINEAR)
        op.filter(img, res)
        return res
    }

    inner class Timer : AnimationTimer() {

        val delay = SimpleLongProperty(0)

        override fun handle(t: Long) {
            automaton.step()
            view.update()
            // println(automaton.oscillation)
            Thread.sleep(delay.value)
        }
    }
}
