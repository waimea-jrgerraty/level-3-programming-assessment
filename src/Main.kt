@file:Suppress("ktlint:standard:no-wildcard-imports")

/**
 * =====================================================================
 * Programming Project for NCEA Level 3, Standard 91906
 * ---------------------------------------------------------------------
 * Project Name:    Beneath Blackened Skys
 * Project Author:  James Gerraty
 * GitHub Repo:     https://github.com/waimea-jrgerraty/level-3-programming-assessment
 * ---------------------------------------------------------------------
 * Notes: The 'map' will be a undirected graph of locations
 * =====================================================================
 */
import com.formdev.flatlaf.FlatDarkLaf
import map.Map
import sequencer.Attack
import sequencer.Dictionary
import sequencer.Sequencer
import sequencer.Weapon
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

// Constants
const val HEALTH_MAX_WIDTH = 450

// Utilities
fun sleep(t: Double) {
    Thread.sleep((t * 1000).toLong())
}

/** Launch the application */
fun main() {
    FlatDarkLaf.setup() // Flat, dark look-and-feel

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

    var currentLocation = Map.balmoral
        private set

    var primaryWeapon: Weapon = Dictionary.batteredSword

    fun takeDamage(damage: Double) {
        assert(damage >= 0.0) { "Use the heal method instead of takeDamage" }
        health = (health - damage).coerceIn(0.0, maxHealth)
    }

    fun heal(amount: Double) {
        assert(amount <= 0.0) { "Use the takeDamage method instead of heal" }
        health = (health + amount).coerceIn(0.0, maxHealth)
    }

    fun giveExp(amount: Double) {
        exp += amount.toULong()
        // FIXME: Properly handle level ups
    }

    // -- Game State -- //
    val sequencer = Sequencer(this)

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
    var expectingCombatInput = false
    var expectingAction = false

    // We have to use a synchronous wait mechanism to be able to return the user's input
    private var inputLatch: CountDownLatch? = null
    private val inputTextRef = AtomicReference<String>()
    private val inputCombatRef = AtomicReference<Attack>()

    fun getTextInput(): String {
        expectingTextInput = true
        rerender()

        inputLatch = CountDownLatch(1)
        inputLatch!!.await() // Halt execution here while waiting for the input

        // The user has input something, remove the input field
        expectingTextInput = false
        rerender()

        return inputTextRef.get()
    }

    internal fun submitTextInput(input: String) {
        // Set the atomic reference as the input text and open the count-down latch to resume
        // execution in getTextInput
        inputTextRef.set(input)
        inputLatch?.countDown()
    }

    fun getCombatAction(): Attack {
        expectingCombatInput = true
        rerender()

        inputLatch = CountDownLatch(1)
        inputLatch!!.await()

        expectingCombatInput = false
        rerender()

        return inputCombatRef.get()
    }

    internal fun submitCombatInput(input: Attack) {
        inputCombatRef.set(input)
        inputLatch?.countDown()
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

    private lateinit var combatFrame: JPanel
    private lateinit var attackSelector: JComboBox<String>
    private lateinit var attack: JButton

    /** Configure the UI and display it */
    init {
        app.rerender = { updateView() } // Add a callback to allow app to trigger re-renders

        configureWindow() // Configure the window
        addControls() // Build the UI

        setLocationRelativeTo(null) // Centre the window
        isVisible = true // Make it visible

        app.sequencer.onboarding()

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

        combatFrame = JPanel()
        combatFrame.bounds = Rectangle(100 + HEALTH_MAX_WIDTH, 600, 500, 200)
        combatFrame.border = BorderFactory.createRaisedSoftBevelBorder()
        combatFrame.isOpaque = true
        combatFrame.background = Color.DARK_GRAY
        combatFrame.layout = null
        combatFrame.isVisible = false
        add(combatFrame)

        attackSelector = JComboBox()
        attackSelector.bounds = Rectangle(25, 25, 450, 50)
        attackSelector.addActionListener(this)
        combatFrame.add(attackSelector)

        attack = JButton("Attack")
        attack.bounds = Rectangle(25, 100, 450, 75)
        attack.background = Color(200, 100, 100)
        attack.font = guiFont
        attack.isEnabled = false
        attack.addActionListener(this)
        combatFrame.add(attack)

        display = JTextArea("")
        display.lineWrap = true
        display.isEditable = false
        display.bounds = Rectangle(50, 50, 1100, 700)
        display.font = baseFont
        display.isOpaque = true
        display.background = Color(50, 50, 60)
        display.lineWrap = true
        display.wrapStyleWord = true
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

        combatFrame.isVisible = app.expectingCombatInput && app.expectingAction
        if (app.expectingAction) {
            if (app.expectingCombatInput) {
                // Set up the list of attacks
                attackSelector.removeAllItems()
                // Find all attacks
                val attacks = mutableListOf<String>()
                for (move in app.primaryWeapon.moves) {
                    if (app.primaryWeapon.cooldown[move] != null) {
                        attacks.add("${move.name} [Cooldown: ${app.primaryWeapon.cooldown[move]} turns]")
                    } else {
                        attacks.add(move.name)
                    }
                }

                attacks.sort()

                for (item in attacks) {
                    attackSelector.addItem(item)
                }
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
                app.expectingAction = !app.expectingAction
                updateView()
            }
            attackSelector -> {
                val selectedItem = attackSelector.selectedItem as? String
                if (selectedItem != null) {
                    if (!selectedItem.endsWith("]")) {
                        attack.isEnabled = true
                        return
                    }
                }
                attack.isEnabled = false
            }
            attack -> {
                if (app.expectingCombatInput) {
                    val move = attackSelector.selectedItem as? String
                    // Find Attack from move
                    if (move != null) {
                        var realMove: Attack? = null
                        for (realAttack in app.primaryWeapon.moves) {
                            if (realAttack.name == move) {
                                realMove = realAttack
                                break
                            }
                        }

                        if (realMove != null) {
                            attack.isEnabled = false
                            app.submitCombatInput(realMove)
                        }
                    }
                }
            }
        }
    }
}
