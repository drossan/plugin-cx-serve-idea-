package com.drossan.cxserver

import com.intellij.openapi.project.Project
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JPanel

class CXServerPanel(project: Project) : JPanel() {
    private val runCXServerAction = RunCXServerAction()

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        val runButton = JButton("Run CX Server")
        runButton.alignmentX = LEFT_ALIGNMENT
        runButton.addActionListener {
            runCXServerAction.execute(project)
        }
        add(runButton)

        val stopButton = JButton("Stop CX Server")
        stopButton.alignmentX = LEFT_ALIGNMENT
        stopButton.addActionListener {
            runCXServerAction.stopServer()
        }
        add(stopButton)
    }
}
