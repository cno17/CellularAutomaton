package cellular

import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

// todo: AnchorPane?

class SettingsBox(val app: App) : VBox(10.0) {

    val automaton = app.automaton
    val view = app.view
    val timer = app.timer

    init {
        children += initPane()
        children += stepPane()
        children += viewPane()
        children += buttonBox()
    }

    fun initPane(): TitledPane {
        val frequencySlider = Slider(0.001, 0.010, automaton.noiseFrequency.value)
        frequencySlider.valueProperty().bindBidirectional(automaton.noiseFrequency)
        val box = VBox(10.0)
        box.children.addAll(listOf(Label("frequency"), frequencySlider))
        return TitledPane("Init", box)
    }

    fun stepPane(): TitledPane {
        val alphaSlider = Slider(0.0, 1.0, automaton.alpha.value.toDouble())
        val betaSlider = Slider(0.0, 1.0, automaton.beta.value.toDouble())
        val gammaSlider = Slider(0.0, 1.0, automaton.gamma.value.toDouble())
        val delaySlider = Slider(0.0, 100.0, timer.delay.value.toDouble())
        alphaSlider.valueProperty().bindBidirectional(automaton.alpha)
        betaSlider.valueProperty().bindBidirectional(automaton.beta)
        gammaSlider.valueProperty().bindBidirectional(automaton.gamma)
        delaySlider.valueProperty().bindBidirectional(timer.delay)
        val box = VBox(10.0)
        box.children.addAll(Label("alpha"), alphaSlider)
        box.children.addAll(Label("beta"), betaSlider)
        box.children.addAll(Label("gamma"), gammaSlider)
        box.children.addAll(Label("delay"), delaySlider)
        return TitledPane("Step", box)
    }

    fun viewPane(): TitledPane {
        val group = ToggleGroup()
        val grayscaleBox = CheckBox("grayscale")
        val stateButton = RadioButton("state")
        val differenceButton = RadioButton("difference")
        val brightnessSlider = Slider(0.0, 100.0, view.brightness.value)
        grayscaleBox.selectedProperty().bindBidirectional(view.grayscale)
        stateButton.isSelected = true
        stateButton.toggleGroup = group
        stateButton.onAction = EventHandler { view.update = view::updateImageFromState }
        differenceButton.toggleGroup = group
        differenceButton.onAction = EventHandler { view.update = view::updateImageFromDifference }
        brightnessSlider.valueProperty().bindBidirectional(view.brightness)
        val box = VBox(10.0)
        box.children.add(grayscaleBox)
        box.children.add(Separator())
        box.children.addAll(stateButton, differenceButton)
        box.children.add(Separator())
        box.children.addAll(Label("brightness"), brightnessSlider)
        return TitledPane("View", box)
    }

    fun buttonBox(): HBox {
        val initButton = Button("Initialize")
        val snapshotButton = Button("Snapshot")
        initButton.onAction = EventHandler { timer.stop(); automaton.init(); timer.start() }
        snapshotButton.onAction = EventHandler { app.makeSnapshot() }
        val box = HBox(10.0)
        box.children.addAll(initButton, snapshotButton)
        return box
    }
}