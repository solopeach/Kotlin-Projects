import javafx.application.Application
import javafx.beans.value.ObservableValue
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*


class Main : Application() {

    private val home = File("${System.getProperty("user.dir")}/test")
    private val blankBackground = Background(BackgroundFill(Color.valueOf("#ffffff"), null, null))
    private val badTypeBackground = Background(BackgroundFill(Color.valueOf("#000000"), null, null))
    private val unreadableFileText = "File cannot be read"
    private val unsupportedFileText = "Unsupported type"

    private lateinit var files: Array<File>
    private lateinit var fileNames: Array<String>
    private lateinit var leftFiles: ListView<String>
    private lateinit var fileMap: HashMap<String, File>
    private lateinit var statusLabel: Label
    private lateinit var selectedFileObject: File
    private lateinit var currentDirectory: File
    private lateinit var contentPane: Pane
    private lateinit var scroll: ScrollPane

    fun start() {

        // renameButtonDisabled = true
        scroll = ScrollPane()
        statusLabel = Label()
        fileMap = HashMap<String, File>()
        contentPane = VBox()
        contentPane.apply {
            prefWidth = 100.0
            VBox.setVgrow(this, Priority.ALWAYS)
        }

        leftFiles = ListView<String>().apply {

            selectionModel.selectionMode = SelectionMode.SINGLE
            selectionModel.select(0);

            setOnMouseClicked {
                if (leftFiles.selectionModel.selectedItem != null) {
                    if (it.getButton().equals(MouseButton.PRIMARY) && it.getClickCount() == 2) {
                        goDeeperDirectory()
                    } else {
                        handleFileNamesClicked()
                    }
                }
            }


            setOnKeyPressed { event ->
                if (event.code == KeyCode.DOWN || event.code == KeyCode.UP) {
                    handleFileNamesClicked()
                }
                if (event.code == KeyCode.DELETE || event.code == KeyCode.BACK_SPACE) {
                    // Go into parent directory
                    goParentDirectory()
                }
                if (event.code == KeyCode.ENTER) {
                    // Go into a directory if selected
                    goDeeperDirectory()
                }
            }
        }

        goHomeDirectory()
    }

    fun goParentDirectory() {
        val parentDirectory = currentDirectory.getParentFile()
        if (currentDirectory != home) { // don't let user go beyond home
            // println("move up a dir")
            directoryChange(parentDirectory)
            updateContentPane(selectedFileObject)
        }
    }

    fun goHomeDirectory() {
        directoryChange(home)
        updateContentPane(selectedFileObject)
    }

    fun goDeeperDirectory() {
        if (selectedFileObject.isDirectory()) {
            // println("it's a directory")
            directoryChange(selectedFileObject)
        }
        updateContentPane(selectedFileObject)
    }

    private fun handleFileNamesClicked() {
        val newLabel = fileMap[leftFiles.selectionModel.selectedItem]
        selectedFileObject = fileMap[leftFiles.selectionModel.selectedItem]!!
        statusLabel.text = "${newLabel}"

        updateContentPane(selectedFileObject)
    }

    private fun updateFiles(directory: File) {
        fileNames = directory.list()
        files = directory.listFiles()
        fileMap.clear()
        for ((index, value) in fileNames.withIndex()) {
            var folderFormat = value
            if (files[index].isDirectory()) {
                folderFormat = value + "/"
                fileNames[index] = folderFormat
            }
            fileMap[folderFormat] = files[index]
        }

        leftFiles.items.clear()
        leftFiles.items.addAll(fileNames)
    }

    private fun directoryChange(directory: File) {
        // renameButtonDisabled = true
        currentDirectory = directory

        updateFiles(currentDirectory)

        statusLabel.text = "${currentDirectory.path}"
        selectedFileObject = currentDirectory

        // println(fileMap)
    }

    fun refreshCurrentDirectory() {
        updateFiles(currentDirectory)
        updateContentPane(currentDirectory)
        statusLabel.text = "${currentDirectory.path}"
    }

    private fun setUnreadableStyle() {
        contentPane.setBackground(badTypeBackground)
        val unsupportedText = Text(0.0, 10.0, unreadableFileText)
        unsupportedText.setFill(Color.RED)
        contentPane.children.add(unsupportedText)
    }

    private fun updateContentPane(file: File) {
        // ** need to do smth else if file is unreadable
        contentPane.setStyle(null)
        contentPane.children.clear()

        if (file.isDirectory()) {
            // display blank page for content pane
            contentPane.setBackground(blankBackground)

        } else if (file.path.endsWith("png") || file.path.endsWith("jpg") || file.path.endsWith("bmp")) {
            // display image sized to fit the contents pane
            contentPane.setBackground(blankBackground)
            try {
                val stream = FileInputStream(file.path)
                val image = Image(stream)
                val displayImage = ImageView()
                displayImage.setImage(image)
                displayImage.fitHeightProperty().bind(contentPane.heightProperty())
                displayImage.fitWidthProperty().bind(contentPane.widthProperty())
                displayImage.setPreserveRatio(true)
                contentPane.children.add(displayImage)

                if (image.isError) {
                    setUnreadableStyle()
                }

                stream.close()

            } catch (ex: Exception) {
                setUnreadableStyle()

            }

        } else if (file.path.endsWith("txt") || file.path.endsWith("md")) {
            // scrollbar if need
            contentPane.setBackground(blankBackground)

            val textFile = TextArea()
            textFile.prefWidthProperty().bind(contentPane.widthProperty())
            textFile.prefHeightProperty().bind(contentPane.heightProperty())
            textFile.setWrapText(true)

            try {
                val input = Scanner(file)

                while (input.hasNext()) {
                    textFile.appendText(input.nextLine() + '\n')
                }
                input.close()

                textFile.selectPositionCaret(0)  // for some reason the scrollbar is at the bottom by default
                contentPane.children.add(textFile)

            } catch (ex: Exception) {
                println(ex.message)
                println("Error during parsing text file")
                setUnreadableStyle()
            }

        } else {
            // unsupported file type
            contentPane.setBackground(badTypeBackground)
            val unsupportedText = Text(0.0, 10.0, unsupportedFileText)
            unsupportedText.setFill(Color.RED)
            contentPane.children.add(unsupportedText)
        }
    }

    override fun start(primaryStage: Stage?) {

        start()

        // create panels
        val leftPane = leftFiles

        // dialogs for 3 actions
        val renameDialog = TextInputDialog()
        renameDialog.setContentText("Enter the new name for the file.")

        val choices = listOf<String>("True", "False")
        val deleteDialog = ChoiceDialog(choices[0], choices)
        deleteDialog.setContentText("Are you sure you want to delete this?")

        val fileChooser = DirectoryChooser()
        fileChooser.title = "Open Resource File"

        // functions for 3 actions
        fun renameFile(newName: String) {
            if (leftFiles.selectionModel.selectedItem == null) throw Exception("no file selected!")
            // println("selected: " + leftFiles.selectionModel.selectedItem)

            val oldFile = selectedFileObject
            // print(oldFile)

            val newFile = File(oldFile?.getParent(), newName)
            // println(newFile.isFile())
            // println(newFile.exists())
            if (newFile.exists()) {
                throw IOException("file exists")
            }

            // println(oldFile.isFile())
            val success = oldFile.renameTo(newFile)
            if (success == false) {
                throw IOException("renaming failed!")
            }

            // reset files list
            selectedFileObject = newFile
            // println(newFile.getName())

            updateContentPane(selectedFileObject)
            updateFiles(currentDirectory)
            statusLabel.text = "${selectedFileObject}"

            leftFiles.selectionModel.select(newFile.getName())
        }

        fun renameAction() {
            if (leftFiles.selectionModel.selectedItem != null) {
                val newName = renameDialog.showAndWait()
                val restrictedChars = arrayOf<Char>('\\', '/', ':', '*', '?', '"', '<', '>', '|')
                val newNameString = newName.get()

                if (newNameString.isEmpty() || Arrays.stream(restrictedChars).anyMatch { c -> newNameString.contains(c)}) {
                    // display error message
                    val errorAlert = Alert(Alert.AlertType.ERROR, "Bad file name!")
                    errorAlert.showAndWait()
                } else {
                    renameFile(newNameString)
                }

                renameDialog.getEditor().clear()
            }
        }

        fun deleteAction() {
            if (leftFiles.selectionModel.selectedItem != null) {
                val toDelete = deleteDialog.showAndWait()
                val toDeleteString = toDelete.get()

                if (toDeleteString == choices[0]) {
                    // println(selectedFileObject)
                    // println(selectedFileObject.isDirectory())

                    if (selectedFileObject.isDirectory()) {
                        selectedFileObject.deleteRecursively()
                        refreshCurrentDirectory()

                    } else if (selectedFileObject.isFile()) {
                        val deleteSuccess = selectedFileObject.delete()
                        // println(deleteSuccess)
                        refreshCurrentDirectory()
                    }
                }
            }
        }

        fun moveAction() {
            if (leftFiles.selectionModel.selectedItem != null) {

                val destDirectory = fileChooser.showDialog(primaryStage)
                // println(destDirectory)

                var cantMove = false

                if (destDirectory != null) {
                    if (selectedFileObject.isDirectory() && destDirectory == selectedFileObject) {
                        cantMove = true
                        val errorAlert = Alert(Alert.AlertType.ERROR, "Can't move a folder into itself!")
                        errorAlert.showAndWait()
                    }

                    // check if file/directory with same name is inside the dest directory
                    if (destDirectory.list().contains(selectedFileObject.getName())) {
                        cantMove = true
                        val errorAlert =
                            Alert(Alert.AlertType.ERROR, "Failed to move: directory contains file of same name!")
                        errorAlert.showAndWait()
                    }

                    if (!cantMove) {
                        // println("moving file/directory")
                        val source = selectedFileObject.toPath()
                        val fileName = selectedFileObject.getName()
                        val destDirectoryPath = destDirectory.toPath()
                        Files.move(source, destDirectoryPath.resolve(fileName), StandardCopyOption.ATOMIC_MOVE)

                        refreshCurrentDirectory()
                    }
                }
            }
            renameDialog.getEditor().clear()
        }

        val topPane = VBox().apply {
            prefHeight = 30.0
            background = Background(BackgroundFill(Color.valueOf("#00ffff"), null, null))

            val homeButton = Button("Home")
            val prevButton = Button("Prev")
            val nextButton = Button("Next")
            val deleteButton = Button("Delete")
            val renameButton = Button("Rename")
            val moveButton = Button("Move")

            val actions = Menu("Actions")
            val actionPrev = MenuItem("Prev")
            val actionNext = MenuItem("Next")
            val actionDelete = MenuItem("Delete")
            val actionRename = MenuItem("Rename")
            val actionMove = MenuItem("Move")
            actions.getItems().addAll(actionPrev, actionNext, actionDelete, actionRename, actionMove)
            actionDelete.setOnAction { deleteAction() }
            actionRename.setOnAction { renameAction() }
            actionMove.setOnAction { moveAction() }
            actionPrev.setOnAction { goParentDirectory() }
            actionNext.setOnAction { goDeeperDirectory() }

            children.addAll(
                MenuBar().apply {
                    menus.add(Menu("File"))
                    menus.add(Menu("View"))
                    menus.add(actions)
                    menus.add(Menu("Options"))
                },
                ToolBar().apply {
                    items.add(homeButton)
                    items.add(prevButton)
                    items.add(nextButton)
                    items.add(deleteButton)
                    items.add(renameButton)
                    items.add(moveButton)
                })

            homeButton.setOnAction { goHomeDirectory() }
            prevButton.setOnAction { goParentDirectory() }
            nextButton.setOnAction { goDeeperDirectory() }
            deleteButton.setOnAction { deleteAction() }
            renameButton.setOnAction { renameAction()}
            moveButton.setOnAction { moveAction() }
        }

        // put the panels side-by-side in a container
        val root = BorderPane().apply {
            left = leftPane
            center = contentPane
            top = topPane
            bottom = statusLabel
        }

        // create the scene and show the stage
        primaryStage?.run {
            scene = Scene(root, 750.0, 430.0)
            title = "File Browser"
            show()
        }
    }
}