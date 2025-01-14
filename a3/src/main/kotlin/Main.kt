// package ui.lectures.javafx.mvc.javafxmvc

import Global.MAX_BULLETS_ON_SCREEN
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import controller.GameOverScene
import controller.Scene2
import controller.TitleScene
import javafx.animation.AnimationTimer
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.layout.*
import javafx.scene.paint.Color
import java.io.File
import java.util.Random
import javax.sound.sampled.AudioSystem
import kotlin.math.min
import kotlin.math.roundToInt

class Main : Application() {

    val aliens = mutableListOf<Enemy>()
    val alienBullets = mutableListOf<EnemyBullet>()
    val playerBullets = mutableListOf<PlayerBullet>()
    val player = Player()
    val gameCanvas = Pane()
    val alienBulletsToRemove = mutableListOf<EnemyBullet>()
    val playerBulletsToRemove = mutableListOf<PlayerBullet>()

    // unset
    var ALIEN_SPEED: Double = 0.0
    var ALIEN_BULLET_SPEED: Double = 0.0
    var ALIEN_BULLET_PROB: Int = 0
    var LAST_PLAYER_BULLET: Long = 0L
    var ALIEN_MOVE_SOUND_GAP: Double = 0.0

    var isPlayerShooting = false
    var moveLeftRight = "right"
    var wallCollision = false
    var alienSoundCounter = 1

    val myModel = Model() // model
    var LAST_ALIEN_BULLET: Long = 0L
    var LAST_ALIEN_MOVE_SOUND: Long = 0L
    var ALIEN_BULLET_TIME_LIMIT: Double = 0.0

    // scene setting up
    val canvas = Scene2(myModel)
    val scene1 = Scene(TitleScene(myModel), Global.windowWidth, Global.windowHeight)
    val scene2 = Scene(canvas, Global.windowWidth, Global.windowHeight)
    val scene3 = Scene(GameOverScene(myModel), Global.windowWidth, Global.windowHeight)

    override fun start(stage: Stage) {

        stage.setResizable(false)

        canvas.setBackground(Background(BackgroundFill(Color.valueOf("#000000"), CornerRadii(0.0), Insets.EMPTY)))
        canvas.children.addAll(gameCanvas, TopLine(myModel))

        // add player
        gameCanvas.children.add(player)

        scene1.setOnKeyPressed { event ->

            if (event.getCode() == KeyCode.Q) {
                Platform.exit()
            }
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.DIGIT1) {
                // println("level 1")
                myModel.restartGame(1)
                myModel.setScene(stage, Model.SCENES.SCENE2, scene2)
                launchGame(1, stage)
            }
            if (event.getCode() == KeyCode.DIGIT2) {
                // println("level 2")
                myModel.restartGame(2)
                myModel.setScene(stage, Model.SCENES.SCENE2, scene2)
                launchGame(2, stage)
            }
            if (event.getCode() == KeyCode.DIGIT3) {
                // println("level 3")
                myModel.restartGame(3)
                myModel.setScene(stage, Model.SCENES.SCENE2, scene2)
                launchGame(3, stage)
            }
        }

        scene2.setOnKeyPressed { event ->
            if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) {
                player.moveLeft()

            } else if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) {
                player.moveRight()

            } else if (event.getCode() == KeyCode.SPACE) {
                isPlayerShooting = true
            }
        }

        scene2.setOnKeyReleased { event ->
            if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) {
                player.stopLeft()

            } else if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) {
                player.stopRight()

            } else if (event.getCode() == KeyCode.SPACE) {
                isPlayerShooting = false
            }
        }

        scene3.setOnKeyPressed { event ->

            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.DIGIT1) {
                // println("level 1")
                myModel.restartGame(1)
                launchGame(1, stage)
                myModel.setScene(stage, Model.SCENES.SCENE2, scene2)
            }
            if (event.getCode() == KeyCode.DIGIT2) {
                // println("level 2")
                myModel.restartGame(2)
                launchGame(2, stage)
                myModel.setScene(stage, Model.SCENES.SCENE2, scene2)
            }
            if (event.getCode() == KeyCode.DIGIT3) {
                // println("level 3")
                myModel.restartGame(3)
                launchGame(3, stage)
                myModel.setScene(stage, Model.SCENES.SCENE2, scene2)
            }
            if (event.getCode() == KeyCode.I) {
                myModel.setScene(stage, Model.SCENES.SCENE1, scene1)
            }
            if (event.getCode() == KeyCode.Q) {
                Platform.exit()
            }
        }

        myModel.setScene(stage, Model.SCENES.SCENE1, scene1)

    }

    fun playSound(file: String) {
        var clipper = AudioSystem.getClip()
        clipper.close()
        clipper.open(AudioSystem.getAudioInputStream(File(file)))
        clipper.start()
    }

    fun createPlayerBullet() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - LAST_PLAYER_BULLET >= Global.PLAYER_BULLET_TIME_LIMIT) {
            val newPlayerBullet = PlayerBullet(player.x + player.width / 2, player.y)
            playerBullets.add(newPlayerBullet)
            gameCanvas.children.add(newPlayerBullet)
            LAST_PLAYER_BULLET = currentTime

            playSound(Sounds.shoot)
        }
    }

    fun createAlienBullet() {
        val currentTime = System.currentTimeMillis()
        val random = Random()
        val chanceToFire = random.nextInt(1000)
        if (chanceToFire <= ALIEN_BULLET_PROB && alienBullets.size < MAX_BULLETS_ON_SCREEN) {
            val j = random.nextInt(aliens.size)
            val newAlienBullet = EnemyBullet(aliens[j].boundsInParent.centerX, aliens[j].boundsInParent.centerY, Image(aliens[j].bulletURL))
            alienBullets.add(newAlienBullet)
            gameCanvas.children.add(newAlienBullet)
            LAST_ALIEN_BULLET = currentTime
        }
    }

    fun launchGame(level: Int, stage: Stage) {

        if (level == 1) {
            ALIEN_SPEED = Global.ALIEN_SPEED1
            ALIEN_BULLET_SPEED = Global.ALIEN_BULLET_SPEED1
            ALIEN_BULLET_PROB = Global.ALIEN_BULLET_PROB1
            ALIEN_BULLET_TIME_LIMIT = Global.ALIEN_BULLET_TIME_LIMIT1
            ALIEN_MOVE_SOUND_GAP = Global.ALIEN_MOVE_SOUND_GAP1

        } else if (level == 2) {
            ALIEN_SPEED = Global.ALIEN_SPEED2
            ALIEN_BULLET_SPEED = Global.ALIEN_BULLET_SPEED2
            ALIEN_BULLET_PROB = Global.ALIEN_BULLET_PROB2
            ALIEN_BULLET_TIME_LIMIT = Global.ALIEN_BULLET_TIME_LIMIT2
            ALIEN_MOVE_SOUND_GAP = Global.ALIEN_MOVE_SOUND_GAP2

        } else if (level == 3) {
            ALIEN_SPEED = Global.ALIEN_SPEED3
            ALIEN_BULLET_SPEED = Global.ALIEN_BULLET_SPEED3
            ALIEN_BULLET_PROB = Global.ALIEN_BULLET_PROB3
            ALIEN_BULLET_TIME_LIMIT = Global.ALIEN_BULLET_TIME_LIMIT3
            ALIEN_MOVE_SOUND_GAP = Global.ALIEN_MOVE_SOUND_GAP3
        }

        isPlayerShooting = false
        moveLeftRight = "right"
        wallCollision = false
        alienSoundCounter = 1

        player.restartPosition()
        alienBullets.forEach {
            gameCanvas.children.remove(it)
        }
        playerBullets.forEach {
            gameCanvas.children.remove(it)
        }
        aliens.forEach {
            gameCanvas.children.remove(it)
        }
        aliens.clear()
        alienBullets.clear()
        playerBullets.clear()

        myModel.nextLevel(level)

        var count = 0
        var alienUrl = ""
        var alienBulletUrl = ""

        for (i in 0..4) {
            for (j in 0..9) {

                if (count % 3 == 0) {
                    alienUrl = "images/enemy1.png"
                    alienBulletUrl = "images/bullet1.png"
                } else if (count % 3 == 1) {
                    alienUrl = "images/enemy2.png"
                    alienBulletUrl = "images/bullet2.png"
                } else {
                    alienUrl = "images/enemy3.png"
                    alienBulletUrl = "images/bullet3.png"
                }

                val alien = Enemy(
                    Global.startx + Global.xgap * j,
                    Global.starty + Global.ygap * i, Image(alienUrl), alienBulletUrl
                )

                count++
                aliens.add(alien)
                gameCanvas.children.add(alien)
            }
        }

        animationTimerDemo(stage)
    }

    fun removeAlien(alien: Enemy?) {
        aliens.remove(alien)
        gameCanvas.children.remove(alien)
    }

    fun removePlayerBullet(bullet: PlayerBullet?) {
        playerBullets.remove(bullet)
        gameCanvas.children.remove(bullet)
    }

    fun removeAlienBullet(bullet: EnemyBullet?) {
        alienBullets.remove(bullet)
        gameCanvas.children.remove(bullet)
    }

    fun checkPlayerBulletHitAlien() {
        var hitAliens: MutableList<Enemy> = mutableListOf<Enemy>()
        var usedBullet: PlayerBullet? = null
        var hit = false

        for (alien in aliens) {
            for (bullet in playerBullets) {
                if (alien.contains(bullet.boundsInParent.maxX, bullet.boundsInParent.minY) ||
                    alien.contains(bullet.boundsInParent.minX, bullet.boundsInParent.minY))
                {
                    hitAliens.add(alien)
                    usedBullet = bullet
                    hit = true
                }
            }
        }

        hitAliens.forEach {
            // println("player bullet hit alien")

            playSound(Sounds.invaderkilled)
            removeAlien(it)
            removePlayerBullet(usedBullet)
            ALIEN_SPEED *= Global.ALIEN_SPEED_INCREASE_RATIO // increase aliens' speed bc hit
            myModel.updateScore()
        }

        // if (hit) println("player bullet hit alien ${hitAliens.size} ${aliens.size} ${myModel.score}")
    }

    fun checkAlienBulletHitPlayer() {
        var usedBullets: MutableList<EnemyBullet> = mutableListOf<EnemyBullet>()
        var hit = false

        for (bullet in alienBullets) {
            if (bullet.boundsInParent.maxY >= player.boundsInParent.minY &&
                (((bullet.boundsInParent.minX >= player.boundsInParent.minX) &&
                        (bullet.boundsInParent.minX <= player.boundsInParent.maxX)) ||
                        ((bullet.boundsInParent.maxX <= player.boundsInParent.maxX) &&
                                (bullet.boundsInParent.maxX >= player.boundsInParent.minX)))) {
                hit = true
                usedBullets.add(bullet)
            }
        }

        usedBullets.forEach {
            // println("alien bullet hit player")
            playSound(Sounds.explosion)

            removeAlienBullet(it)
            myModel.lostLife()
        }

        if (hit) playerRandomPosition() // restart player position at random
    }

    fun checkAlienHitPlayer() {
        var usedAliens: MutableList<Enemy> = mutableListOf<Enemy>()
        var hit = false

        for (alien in aliens) {
            if (player.boundsInParent.minY <= alien.boundsInParent.maxY &&
                (((player.boundsInParent.minX >= alien.boundsInParent.minX) &&
                        (player.boundsInParent.minX <= alien.boundsInParent.maxX)) ||
                        ((player.boundsInParent.maxX <= alien.boundsInParent.maxX) &&
                                (player.boundsInParent.maxX >= alien.boundsInParent.minX)))) {
                hit = true
                usedAliens.add(alien)
            }
        }

        usedAliens.forEach {
            // ("alien hit player")
            playSound(Sounds.explosion)

            removeAlien(it)
            myModel.lostLife()
        }

        if (hit) playerRandomPosition() // restart player position at random
    }

    fun playerRandomPosition() {
        var maxX = 0.0
        var minX = 90000.0
        var lowestHeight = 90000.0
        for (alien in aliens) {
            if (alien.boundsInParent.maxY < lowestHeight) {
                lowestHeight = alien.boundsInParent.maxY
            }
            if (alien.boundsInParent.maxX > maxX) {
                maxX = alien.boundsInParent.maxX
            }
            if (alien.boundsInParent.minX < minX) {
                minX = alien.boundsInParent.minX
            }
        }

        if (lowestHeight >= Global.LOWEST_POINT) {

            if (maxX + Global.PLAYER_WIDTH <= Global.RIGHT_WALL_BUFFER) {
                player.x = maxX
            } else {
                player.x = minX
            }
        } else {
            var random = Random()
            val randomX = random.nextInt(Global.RIGHT_WALL_BUFFER.roundToInt() - Global.LEFT_WALL_BUFFER.roundToInt())
            + Global.RIGHT_WALL_BUFFER.roundToInt()
            player.x = randomX.toDouble()
        }
    }

    fun moveAliens() {
        for (alien in aliens) {

            if (moveLeftRight == "left") {
                alien.moveLeft(ALIEN_SPEED)
            } else if (moveLeftRight == "right") {
                alien.moveRight(ALIEN_SPEED)
            }
        }

        createAlienBullet()

        for (alien in aliens) {

            if (moveLeftRight == "left") {
                if (alien.x - ALIEN_SPEED < Global.LEFT_WALL_BUFFER) {
                    moveLeftRight = "right"
                    wallCollision = true
                    break
                }

            } else if (moveLeftRight == "right") {
                if (alien.x + ALIEN_SPEED > Global.RIGHT_WALL_BUFFER) {
                    moveLeftRight = "left"
                    wallCollision = true
                    break
                }
            }
        }

        if (wallCollision) {

            for (alien in aliens) {
                alien.moveDown()
            }
            wallCollision = false
        }

    }

    fun removeBoundsBullets() {
        for (bullet in alienBullets) {
            if (bullet.y >= Global.windowHeight + 50.0) {
                alienBulletsToRemove.add(bullet)
            }
            bullet.y += ALIEN_BULLET_SPEED
        }

        for (bullet in alienBulletsToRemove) {
            removeAlienBullet(bullet)
        }

        for (bullet in playerBullets) {
            if (bullet.y <= 0.0) {
                playerBulletsToRemove.add(bullet)
            }
            bullet.y -= Global.PLAYER_BULLET_SPEED
        }

        for (bullet in playerBulletsToRemove) {
            removePlayerBullet(bullet)
        }
    }

    fun playAlienMoveSound() { // sounds for alien movement
        val currentTime = System.currentTimeMillis()
        if (currentTime - LAST_ALIEN_MOVE_SOUND >= ALIEN_MOVE_SOUND_GAP) {
            LAST_ALIEN_MOVE_SOUND = currentTime
            if (alienSoundCounter % 4 == 1) {
                playSound(Sounds.fastinvader3)
            } else if (alienSoundCounter % 4 == 2) {
                playSound(Sounds.fastinvader2)
            } else if (alienSoundCounter % 4 == 3) {
                playSound(Sounds.fastinvader1)
            } else {
                playSound(Sounds.fastinvader4)
            }
            alienSoundCounter++
        }
    }


    fun animationTimerDemo(stage: Stage) {

        // create timer using JavaFX 60FPS execution thread
        val timer: AnimationTimer = object : AnimationTimer() {
            override fun handle(now: Long) {

                if (myModel.lives == 0) {
                    stop()
                    myModel.updateGameFailed()
                    myModel.setScene(stage, Model.SCENES.SCENE2, scene3)
                }

                if (isPlayerShooting) {
                    createPlayerBullet()
                }

                player.update() // update player movement

                for (alien in aliens) {
                    if (alien.boundsInParent.maxY >= Global.LOWEST_POINT) {
                        // println("lowest point reached")
                        stop()
                        myModel.updateGameFailed()
                        myModel.setScene(stage, Model.SCENES.SCENE2, scene3)
                        break
                    }
                }

                if (aliens.size > 0) {
                    moveAliens()
                    checkAlienHitPlayer()
                }

                removeBoundsBullets()
                checkPlayerBulletHitAlien()
                checkAlienBulletHitPlayer()

                if (aliens.size == 0) { // all aliens destroyed
                    stop()
                    when (myModel.level) {
                        1 -> launchGame(2, stage)
                        2 -> launchGame(3, stage)
                        3 -> {
                            myModel.updateGameCleared()
                            myModel.setScene(stage, Model.SCENES.SCENE2, scene3)
                        }
                    }
                }

                playAlienMoveSound()
            }
        }
        // start timer
        timer.start()
    }
}