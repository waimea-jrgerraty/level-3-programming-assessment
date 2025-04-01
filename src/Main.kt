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
import javax.swing.*
import kotlin.math.pow
import map.Map

// Constants
const val HEALTH_MAX_WIDTH = 450

// Utilities
fun sleep(t: Double) {
    Thread.sleep((t * 1000).toLong())
}

/** Launch the application */
fun main() {
    FlatDarkLaf.setup() // Flat, dark look-and-feel

    print("Select a save slot [1 - 5]: ")
    val slot = (readln().toUByteOrNull() ?: 1u).coerceIn(1u, 5u)

    App.load(slot)
    MainWindow(App) // Create and show the UI, using the app model
}

/**
 * The application class (singleton data-model) This is the place where any application data should
 * be stored, plus any application logic functions
 */
object App {
    // -- Player stats -- //
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
    var onboardingCompleted = false // Short intro to establish the player's name

    // -- Persistence -- //

    /**
     * @param slot The slot to load Marshal the App data into a file to be loaded later slot will be
     *   converted to a string and added to the file name, so we can have multiple save slots
     *
     * project will ask for whatever save slot at the start (maybe from console before the gui is
     * created? not sure if that's allowed.)
     */
    fun marshal(slot: UByte) {
        // TODO()
    }

    fun load(slot: UByte) {
        // TODO()
    }
}

/**
 * Main UI window (view) Defines the UI and responds to events The app model should be passwd as an
 * argument
 */
class MainWindow(val app: App) : JFrame(), ActionListener {
    // Fields to hold the UI elements
    private lateinit var display: JTextArea
    private lateinit var healthLabel: JLabel
    private lateinit var healthBarFrame: JPanel
    private lateinit var healthBar: JPanel
    private lateinit var action: JButton

    /** Configure the UI and display it */
    init {
        configureWindow() // Configure the window
        addControls() // Build the UI

        setLocationRelativeTo(null) // Centre the window
        isVisible = true // Make it visible

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
        display =
            JTextArea(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
            )
        display.lineWrap = true
        display.isEditable = false
        display.bounds = Rectangle(50, 50, 1100, 700)
        display.font = baseFont
        display.isOpaque = true
        display.background = Color(50, 50, 60)
        display.border = BorderFactory.createRaisedSoftBevelBorder()
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
        healthBar.bounds =
            Rectangle(0, 0, (HEALTH_MAX_WIDTH * (App.health / App.maxHealth)).toInt(), 75)
    }

    /**
     * Handle any UI events (e.g. button clicks) Usually this involves updating the application
     * model then refreshing the UI view
     */
    override fun actionPerformed(e: ActionEvent?) {
        when (e?.source) {
            action -> {
                updateView()
            }
        }
    }
}
