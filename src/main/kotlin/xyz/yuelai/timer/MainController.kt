package xyz.yuelai.timer

import javafx.animation.AnimationTimer
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Window
import org.kordamp.ikonli.javafx.FontIcon
import xyz.yuelai.ikonli.win11.Win11
import java.net.URL
import java.util.*

class MainController : Initializable {

    @FXML
    private lateinit var container: HBox

    @FXML
    private lateinit var hour: Label

    @FXML
    private lateinit var minutes: Label

    @FXML
    private lateinit var second: Label

    @FXML
    private lateinit var millSecond: Label

    @FXML
    private lateinit var colon0: Label

    @FXML
    private lateinit var colon1: Label

    @FXML
    private lateinit var colon2: Label

    @FXML
    private lateinit var btn: Button

    @FXML
    private lateinit var stopBtn: Button

    private val font = SimpleObjectProperty(Font(20.0))
    private var offsetX: Double = 0.0
    private var offsetY: Double = 0.0

    private var start: Boolean = false
    private val play = FontIcon(Win11.PLAY)
    private val pause = FontIcon(Win11.PAUSE)
    private val stop = FontIcon(Win11.STOP)
    private val exit = FontIcon(Win11.CLOSE_PANE)
    private var millSeconds = 0L

    // 1、用于毫秒递增
    // 2、用于冒号闪烁
    val timer = Timer()

    // 同于更新 UI 元素，这个定时器可以60帧运行
    lateinit var uiTimer: AnimationTimer

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        // 初始化绑定
        hour.fontProperty().bind(font)
        minutes.fontProperty().bind(font)
        second.fontProperty().bind(font)
        millSecond.fontProperty().bind(font)
        colon0.fontProperty().bind(font)
        colon1.fontProperty().bind(font)
        colon2.fontProperty().bind(font)

        // 样式初始化
        btn.graphic = if (start) pause else play
        stopBtn.graphic = stop
        play.iconColor = Color.WHITE
        pause.iconColor = Color.WHITE
        stop.iconColor = Color.WHITE
        exit.iconColor = Color.WHITE
        exit.iconSize = 16

        // 滚轮缩放
        container.setOnScroll {
            var size = if (it.deltaY > 0) {
                font.value.size.plus(1)
            } else {
                font.value.size.minus(1)
            }
            print(font)
            size = if (size <= 5.0) {
                5.0
            } else if (size >= 100.0) {
                100.0
            } else size

            font.set(Font.font(size))
            play.iconSizeProperty().set(font.value.size.toInt())
            pause.iconSizeProperty().set(font.value.size.toInt())
            stop.iconSizeProperty().set(font.value.size.toInt())
            container.scene.window.sizeToScene()
        }

        // =========================== 无标题栏窗体移动 ===========================
        // 鼠标按下事件
        container.onMousePressed = EventHandler { event: MouseEvent ->
            val window: Window = container.scene.window
            // 鼠标在屏幕中的坐标，窗体在屏幕中的坐标
            this.offsetX = event.screenX - window.x
            this.offsetY = event.screenY - window.y
        }

        // 拖拽事件
        container.onMouseDragged = EventHandler { event: MouseEvent ->
            val window: Window = container.scene.window
            // 新的鼠标位置 - 旧的鼠标位置 + 旧的窗体位置 = 鼠标的偏移量 + 旧的窗体位置
            window.x = event.screenX - this.offsetX
            window.y = event.screenY - this.offsetY
        }
        // =========================== 无标题栏窗体移动 ===========================

        val menuItem = MenuItem("exit")
        menuItem.graphic = exit
        menuItem.setOnAction { Platform.exit() }
        val contextMenu = ContextMenu(menuItem)

        container.setOnMouseClicked {
            if (it.button == MouseButton.SECONDARY) {
                contextMenu.show(container, it.screenX, it.screenY)
            }else{
                contextMenu.hide()
            }
        }

        uiTimer = object : AnimationTimer() {
            override fun handle(now: Long) {
                val h = millSeconds / 1000 / 60 / 60
                val m = millSeconds / 1000 / 60 % 60
                val s = millSeconds / 1000 % 60
                hour.text = String.format("%02d", h)
                minutes.text = String.format("%02d", m)
                second.text = String.format("%02d", s)
                millSecond.text = String.format("%03d", millSeconds % 1000)
            }
        }

        var timerTask: TimerTask? = null
        var twinkleTask: TimerTask? = null

        // 停止
        stopBtn.setOnAction {
            stop()
            timerTask?.cancel()
            twinkleTask?.cancel()
            uiTimer.stop()
        }

        // 暂停/继续
        btn.setOnAction {
            start = start.not()
            btn.graphic = if (start) {
                // 毫秒数
                timerTask = object : TimerTask() {
                    override fun run() {
                        millSeconds++
                    }
                }

                // 冒号闪烁
                twinkleTask = object : TimerTask() {
                    override fun run() {
                        Platform.runLater {
                            if (colon1.isVisible) {
                                colon0.isVisible = false
                                colon1.isVisible = false
                                colon2.isVisible = false
                            } else {
                                colon0.isVisible = true
                                colon1.isVisible = true
                                colon2.isVisible = true
                            }
                        }
                    }
                }
                timer.scheduleAtFixedRate(timerTask, 0, 1)
                timer.scheduleAtFixedRate(twinkleTask, 0, 1000)
                uiTimer.start()
                pause
            } else {
                stopTwinkle()
                timerTask?.cancel()
                twinkleTask?.cancel()
                uiTimer.stop()
                play
            }
        }


    }

    /**
     * 计时器停止
     */
    private fun stop() {
        stopTwinkle()

        hour.text = "00"
        minutes.text = "00"
        second.text = "00"
        millSecond.text = "000"

        millSeconds = 0
        start = false
        btn.graphic = play
    }

    /**
     * 冒号停止闪烁
     */
    private fun stopTwinkle() {
        colon0.isVisible = true
        colon1.isVisible = true
        colon2.isVisible = true
    }
}