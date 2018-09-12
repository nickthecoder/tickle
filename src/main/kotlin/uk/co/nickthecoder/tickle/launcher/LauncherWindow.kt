package uk.co.nickthecoder.tickle.launcher

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.tickle.Game
import uk.co.nickthecoder.tickle.editor.MainWindow
import uk.co.nickthecoder.tickle.editor.resources.DesignJsonResources
import uk.co.nickthecoder.tickle.graphics.Window
import uk.co.nickthecoder.tickle.startGame
import java.io.File
import java.util.*
import java.util.prefs.Preferences


class LauncherWindow(val stage: Stage, val glWindow: Window) {

    val buttonSize = 120.0

    val playButton = createBigButton("Play Game", "play") { onPlay() }
    val editButton = createBigButton("Edit Game", "edit") { onEdit() }
    val newButton = createBigButton("Create Game", "new") { onNew() }

    val buttons = HBox()

    val recentContent = VBox()
    val recentScroll = ScrollPane(recentContent)
    val recentTitle = Label("Recently Opened")
    val recentVBox = VBox()

    val borderPane = BorderPane()

    val scene = Scene(borderPane)

    init {
        val resource = Launcher::class.java.getResource("launcher.css")
        scene.stylesheets.add(resource.toExternalForm())

        with(buttons) {
            children.addAll(newButton, playButton, editButton)
            styleClass.add("big")
            alignment = Pos.CENTER
        }

        recentTitle.styleClass.add("title")

        with(recentVBox) {
            children.addAll(recentTitle, recentContent)
        }

        borderPane.top = buttons
        borderPane.center = recentVBox

        recentScroll.minHeight = 200.0

        stage.scene = scene
        stage.title = "Tickle"
        updateRecent()
        stage.sizeToScene()
        stage.show()

    }

    fun createBigButton(label: String, style: String, action: () -> Unit): Button {
        val button = Button(label)
        with(button) {
            styleClass.add(style)
            wrapTextProperty().value = true
            onAction = EventHandler { action() }
            prefHeight = buttonSize
            prefWidth = buttonSize
        }
        return button
    }

    fun onPlay(resourcesFile: File) {
        addRecent(resourcesFile)
        stage.hide()

        Platform.runLater {
            startGame(resourcesFile)

            glWindow.hide()
            stage.close()
        }
    }

    fun onPlay() {
        val resourcesFile = chooseResourcesFile("Play Game")
        if (resourcesFile != null) {
            onPlay(resourcesFile)
            updateRecent()
        }
    }

    fun onEdit(resourcesFile: File) {
        addRecent(resourcesFile)

        val json = DesignJsonResources(resourcesFile)
        val resources = json.loadResources()
        Game(glWindow, resources)

        MainWindow(stage, glWindow)
    }

    fun onEdit() {
        val resourcesFile = chooseResourcesFile("Edit Game")
        if (resourcesFile != null) {
            onEdit(resourcesFile)
        }
    }

    fun onNew() {
        val task = NewGameWizard()
        task.taskRunner.listen { cancelled ->
            if (!cancelled) {
                Platform.runLater {
                    onEdit(task.resourcesFile())
                }
            }
        }
        TaskPrompter(task).placeOnStage(Stage())
    }

    fun updateRecent() {

        recentContent.children.clear()

        listRecent().forEach { file ->
            val box = HBox()
            box.styleClass.add("recent")
            box.alignment = Pos.CENTER

            val playButton = Button(file.nameWithoutExtension, imageView("play.png"))
            playButton.onAction = EventHandler { onPlay(file) }
            playButton.tooltip = Tooltip(file.path)

            val editButton = Button()
            editButton.graphic = imageView("edit.png")
            editButton.onAction = EventHandler { onEdit(file) }
            editButton.tooltip = Tooltip("edit")

            box.children.addAll(playButton, editButton)
            recentContent.children.add(box)
        }
    }

    fun preferences(): Preferences {
        return Preferences.userRoot().node("uk/co/nickthecoder/tickle/launcher")
    }

    fun recentPreferences(): Preferences {
        return preferences().node("recent")
    }

    fun addRecent(file: File) {
        val rp = recentPreferences()
        rp.putLong(file.path, Date().time)
        rp.flush()
        updateRecent()
    }

    fun listRecent(): List<File> {
        val files = mutableMapOf<File, Long>()
        val rp = recentPreferences()

        rp.keys().forEach { name ->
            val date = rp.getLong(name, 0)
            val file = File(name)
            if (file.exists()) {
                files.put(file, date)
            } else {
                rp.remove(name)
            }
        }

        rp.flush()
        return files.toList().sortedBy { -it.second }.map { it.first }
    }

    fun chooseResourcesFile(title: String): File? {
        val fileChooser = FileChooser()
        fileChooser.title = title
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("Tickle Resource File", "*.tickle"))
        return fileChooser.showOpenDialog(stage)
    }

    fun imageView(name: String): ImageView {
        val imageStream = Launcher::class.java.getResourceAsStream(name)
        return ImageView(if (imageStream == null) null else Image(imageStream))
    }
}
