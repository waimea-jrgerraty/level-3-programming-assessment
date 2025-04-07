package sequencer

import App

// Utilities
fun sleep(t: Double) {
    Thread.sleep((t * 1000).toLong())
}

class Sequencer(private val app: App) {
    fun onboarding() {
        app.displayTextAndReRender(
            "\"You there! What is your name!\" A guard shouts out, chasing after you."
        )
        app.name = app.getTextInput()
        app.displayTextAndReRender("\"${app.name} then, come with me.\" The !!!!!!!!!")
    }
}
