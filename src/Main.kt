@file:Suppress("ktlint:standard:no-wildcard-imports")

/**
 * =====================================================================
 * Programming Project for NCEA Level 3, Standard 91906
 * ---------------------------------------------------------------------
 * Project Name: PROJECT NAME HERE Project Author: James Gerraty GitHub Repo:
 * https://github.com/waimea-jrgerraty/level-3-programming-assessment
 * ---------------------------------------------------------------------
 * Notes: The 'map' will be a undirected graph of locations
 * =====================================================================
 */
import com.formdev.flatlaf.FlatDarkLaf
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Rectangle
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference
import javax.swing.*
import kotlin.math.pow
import map.Map
import sequencer.Sequencer

// Constants
const val HEALTH_MAX_WIDTH = 450

// Utilities
fun sleep(t: Double) {
    Thread.sleep((t * 1000).toLong())
}

/** Launch the application */
fun main() {
    FlatDarkLaf.setup() // Flat, dark look-and-feel

    App.load()
    MainWindow(App) // Create and show the UI, using the app model
}

/**
 * The application class (singleton data-model) This is the place where any application data should
 * be stored, plus any application logic functions
 */
object App {
    lateinit var rerender: (() -> Unit)

    // -- Player stats -- //
    var name = "USER"

    var level: UByte = 1u
        private set

    var exp: ULong = 0u
        private set

    val neededExp
        get() = 1024u + 2.0.pow(level.toInt()).toULong()

    var health = 100.0
        private set

    val maxHealth
        get() = 98.0 + (2.0.pow(level.toInt()))

    var currentLocation = Map.Balmoral
        private set

    fun takeDamage(damage: Double) {
        assert(damage >= 0.0) { "Use the heal method instead of takeDamage" }
        health = (health - damage).coerceIn(0.0, maxHealth)
    }

    fun heal(amount: Double) {
        assert(amount <= 0.0) { "Use the takeDamage method instead of heal" }
        health = (health + amount).coerceIn(0.0, maxHealth)
    }

    // -- Game State -- //
    val sequencer = Sequencer(this)

    var onboardingCompleted = false // Short intro to establish the player's name

    var displayText = ""

    // Show text with a typewriter effect
    fun displayTextAndReRender(text: String) {
        displayText = ""
        rerender()

        for (char in text) {
            displayText += char
            sleep(0.02)
            rerender()
        }
    }

    var expectingTextInput = false

    // We have to use a synchronous wait mechanism to be able to return the user's input
    private var textInputLatch: CountDownLatch? = null
    private val textInputRef = AtomicReference<String>()

    fun getTextInput(): String {
        expectingTextInput = true
        rerender()

        textInputLatch = CountDownLatch(1)
        textInputLatch!!.await() // Halt execution here while waiting for the input

        // The user has input something, remove the input field
        expectingTextInput = false
        rerender()

        return textInputRef.get()
    }

    internal fun submitTextInput(input: String) {
        // Set the atomic reference as the input text and open the count-down latch to resume
        // execution in getTextInput
        textInputRef.set(input)
        textInputLatch?.countDown()
    }

    // -- Persistence -- //

    fun marshal() {
        // TODO()
    }

    fun load() {
        // TODO()
    }
}

/**
 * Main UI window (view) Defines the UI and responds to events The app model should be passwd as an
 * argument
 */
class MainWindow(private val app: App) : JFrame(), ActionListener {
    // Fields to hold the UI elements
    private lateinit var display: JTextArea
    private lateinit var healthLabel: JLabel
    private lateinit var healthBarFrame: JPanel
    private lateinit var healthBar: JPanel
    private lateinit var action: JButton
    private lateinit var textInput: JTextField

    /** Configure the UI and display it */
    init {
        app.rerender = { updateView() } // Add a callback to allow app to trigger re-renders

        configureWindow() // Configure the window
        addControls() // Build the UI

        setLocationRelativeTo(null) // Centre the window
        isVisible = true // Make it visible

        if (!app.onboardingCompleted) {
            app.sequencer.onboarding()
        }

        updateView() // Initialise the UI
    }

    /** Configure the main window */
    private fun configureWindow() {
        title = "Kotlin Swing GUI Demo"
        contentPane.preferredSize = Dimension(1200, 900)
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        isResizable = false
        layout = null

        pack()
    }

    /** Populate the UI with UI controls */
    private fun addControls() {
        val baseFont = Font(Font.SANS_SERIF, Font.PLAIN, 24)
        val guiFont = Font(Font.SANS_SERIF, Font.PLAIN, 32)

        // We will use html tags to control the display text
        display = JTextArea("")
        display.lineWrap = true
        display.isEditable = false
        display.bounds = Rectangle(50, 50, 1100, 700)
        display.font = baseFont
        display.isOpaque = true
        display.background = Color(50, 50, 60)
        display.border = BorderFactory.createRaisedSoftBevelBorder()

        textInput = JTextField()
        textInput.bounds = Rectangle(50, 650, 1100, 100)
        textInput.font = baseFont
        textInput.isOpaque = true
        textInput.background = Color(25, 25, 25)
        // Raised Soft Bevel border with left and right insets for text padding
        textInput.border = BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedSoftBevelBorder(),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        )
        textInput.isVisible = false
        textInput.addActionListener(this)
        add(textInput)
        add(display)

        healthLabel = JLabel("Health:")
        healthLabel.font = guiFont
        healthLabel.bounds = Rectangle(50, 800, 100, 75)
        add(healthLabel)

        healthBarFrame = JPanel()
        healthBarFrame.bounds = Rectangle(175, 800, HEALTH_MAX_WIDTH, 75)
        healthBarFrame.border = BorderFactory.createRaisedSoftBevelBorder()
        healthBarFrame.isOpaque = true
        healthBarFrame.background = Color.BLACK
        healthBarFrame.layout = null
        add(healthBarFrame)

        healthBar = JPanel()
        healthBar.bounds = Rectangle(0, 0, HEALTH_MAX_WIDTH, 75)
        healthBar.border = BorderFactory.createRaisedSoftBevelBorder()
        healthBar.isOpaque = true
        healthBar.background = Color(200, 100, 100)
        healthBarFrame.add(healthBar)

        action = JButton("Action")
        action.bounds = Rectangle(200 + HEALTH_MAX_WIDTH, 800, 300, 75)
        action.font = guiFont
        action.addActionListener(this) // Handle any clicks
        add(action)
    }

    /** Update the UI controls based on the current state of the application model */
    private fun updateView() {
        // Handle input gathering
        textInput.isVisible = app.expectingTextInput
        if (app.expectingTextInput) {
            textInput.text = "> "
            // Defer the focus request until Swing re-renders
            SwingUtilities.invokeLater {
                textInput.requestFocusInWindow()
                textInput.selectAll()
            }
        }

        display.text = app.displayText

        healthBar.bounds =
            Rectangle(0, 0, (HEALTH_MAX_WIDTH * (App.health / App.maxHealth)).toInt(), 75)
    }

    /**
     * Handle any UI events (e.g. button clicks) Usually this involves updating the application
     * model then refreshing the UI view
     */
    override fun actionPerformed(e: ActionEvent?) {
        when (e?.source) {
            textInput -> {
                if (app.expectingTextInput) {
                    val input = textInput.text.trim()
                    textInput.text = ""
                    app.submitTextInput(input)
                }
            }
            action -> {
                updateView()
            }
        }
    }
}
