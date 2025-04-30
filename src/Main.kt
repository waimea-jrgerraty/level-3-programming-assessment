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
import map.Location
import map.Map
import map.Sublocation
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

// UI Utilities
fun containerComponent(bounds: Rectangle): JPanel {
    val frame = JPanel()

    frame.bounds = bounds
    frame.border = BorderFactory.createRaisedSoftBevelBorder()
    frame.isOpaque = true
    frame.background = Color.DARK_GRAY
    frame.layout = null
    frame.isVisible = false

    return frame
}

// Force state before startup for testing
fun testState() {
    App.flags.add("onboarding")
}

/** Launch the application */
fun main() {
    FlatDarkLaf.setup() // Flat, dark look-and-feel

    testState() // Apply debug tests if needed
    MainWindow() // Create and show the UI, using the app model
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
    var currentSublocation: Sublocation? = null

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

    // We use flags to track our progress, used to handle when to start sequences
    val flags = mutableSetOf<String>()

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
class MainWindow() : JFrame(), ActionListener {
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

    private lateinit var movementFrame: JPanel
    private lateinit var placeSelector: JComboBox<String>
    private lateinit var move: JButton

    /** Configure the UI and display it */
    init {
        App.rerender = { updateView() } // Add a callback to allow App to trigger re-renders

        configureWindow() // Configure the window
        addControls() // Build the UI

        setLocationRelativeTo(null) // Centre the window
        isVisible = true // Make it visible

        if (!App.flags.contains("onboarding")) {
            App.sequencer.onboarding()
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

        combatFrame = containerComponent(Rectangle(100 + HEALTH_MAX_WIDTH, 600, 500, 200))
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

        movementFrame = containerComponent(Rectangle(100 + HEALTH_MAX_WIDTH, 600, 500, 200))
        add(movementFrame)

        placeSelector = JComboBox()
        placeSelector.bounds = Rectangle(25, 25, 450, 50)
        placeSelector.addActionListener(this)
        movementFrame.add(placeSelector)

        move = JButton("Move")
        move.bounds = Rectangle(25, 100, 450, 75)
        move.background = Color(200, 100, 100)
        move.font = guiFont
        move.isEnabled = false
        move.addActionListener(this)
        movementFrame.add(move)

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

    /** Update the UI controls based on the current state of the Application model */
    private fun updateView() {
        // Handle input gathering
        textInput.isVisible = App.expectingTextInput
        if (App.expectingTextInput) {
            textInput.text = "> "
            // Defer the focus request until Swing re-renders
            SwingUtilities.invokeLater {
                textInput.requestFocusInWindow()
                textInput.selectAll()
            }
        }

        combatFrame.isVisible = App.expectingCombatInput && App.expectingAction
        movementFrame.isVisible = App.expectingAction && !App.expectingCombatInput
        if (App.expectingAction) {
            if (App.expectingCombatInput) {
                // Set up the list of attacks
                attackSelector.removeAllItems()
                // Find all attacks
                val attacks = mutableListOf<String>()
                for (move in App.primaryWeapon.moves) {
                    if (App.primaryWeapon.cooldown[move] != null) {
                        attacks.add("${move.name} [Cooldown: ${App.primaryWeapon.cooldown[move]} turns]")
                    } else {
                        attacks.add(move.name)
                    }
                }

                attacks.sort()

                for (item in attacks) {
                    attackSelector.addItem(item)
                }
            } else {
                // Set up the list of locations
                placeSelector.removeAllItems()

                // Find sublocations first
                val sublocations = mutableListOf<String>()
                var hadSublocations = false

                for (place in App.currentLocation.sublocations) {
                    hadSublocations = true

                    if (place == App.currentSublocation) {
                        sublocations.add("${place.name} [You are here]")
                        continue
                    }
                    sublocations.add(place.name)
                }

                if (hadSublocations) {
                    sublocations.sort()
                    sublocations.addFirst("[Sublocations]") // Prepend an identifier for sublocations
                }

                // Main locations
                val places = mutableListOf<String>()
                for (place in Map.getAvailableDestinations(App.currentLocation)) {
                    places.add(place.name)
                }

                places.sort()
                places.addFirst("[Locations]") // Prepend an identifier for locations

                val moves = sublocations + places
                for (move in moves) {
                    placeSelector.addItem(move)
                }
            }
        }

        if (App.displayText == "") {
            // While not in a sequence, display text will be empty
            // We will build the standard text for outside of sequences here

            App.displayText += """
                You are in ${App.currentLocation.name}.
                ${App.currentLocation.description}
            """.trimIndent()

            SwingUtilities.invokeLater { App.displayText = "" }
        }
        display.text = App.displayText

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
                if (App.expectingTextInput) {
                    val input = textInput.text.trim()
                    textInput.text = ""
                    App.submitTextInput(input)
                }
            }
            action -> {
                App.expectingAction = !App.expectingAction
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
                if (App.expectingCombatInput) {
                    val move = attackSelector.selectedItem as? String
                    // Find Attack from move
                    if (move != null) {
                        var realMove: Attack? = null
                        for (realAttack in App.primaryWeapon.moves) {
                            if (realAttack.name == move) {
                                realMove = realAttack
                                break
                            }
                        }

                        if (realMove != null) {
                            attack.isEnabled = false
                            App.submitCombatInput(realMove)
                        }
                    }
                }
            }
            placeSelector -> {
                val selectedItem = placeSelector.selectedItem as? String
                if (selectedItem != null) {
                    if (!selectedItem.endsWith("]")) {
                        move.isEnabled = true
                        return
                    }
                }
                move.isEnabled = false
            }
            move -> {
                if (App.expectingAction) {
                    val place = placeSelector.selectedItem as? String
                    if (place != null) {
                        // NOTE: Having a sublocation with the same name as a location will cause issues with this Approach
                        // Make sure to check there are no cases of this
                        var sublocation: Sublocation? = null
                        for (sl in App.currentLocation.sublocations) {
                            if (sl.name == place) {
                                sublocation = sl
                                break
                            }
                        }
                        if (sublocation != null) {
                            App.currentSublocation = sublocation
                        } else {
                            for (l in Map.getAvailableDestinations(App.currentLocation)) {
                                if (l.name == place) {
                                    App.currentLocation = l
                                    App.currentSublocation = null
                                    break
                                }
                            }
                        }

                        App.expectingAction = false
                        updateView()
                    }
                }
            }
        }
    }
}
