package com.drossan.cxserver

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.ProjectManager
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.JLabel
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets

class CXServerConfigurable : Configurable {

    private var panel: JPanel? = null
    private var sitesFolderField: JTextField? = null
    private var portField: JTextField? = null

    override fun createComponent(): JComponent? {
        val settings = CXServerSettings.getInstance().myState
        panel = JPanel(GridBagLayout())
        val constraints = GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            anchor = GridBagConstraints.NORTHWEST
            insets = Insets(5, 5, 5, 5)
            gridx = 0
            gridy = 0
            weightx = 1.0
        }

        panel!!.add(JLabel("Sites Folder"), constraints)
        constraints.gridx = 1
        constraints.weightx = 1.0
        sitesFolderField = JTextField(settings.sitesFolder)
        panel!!.add(sitesFolderField, constraints)

        constraints.gridx = 0
        constraints.gridy = 1
        constraints.weightx = 0.0
        panel!!.add(JLabel("Port"), constraints)
        constraints.gridx = 1
        constraints.weightx = 1.0
        portField = JTextField(settings.port)
        panel!!.add(portField, constraints)

        return panel
    }

    override fun isModified(): Boolean {
        val settings = CXServerSettings.getInstance().myState
        return sitesFolderField!!.text != settings.sitesFolder || portField!!.text != settings.port
    }

    override fun apply() {
        val settings = CXServerSettings.getInstance().myState
        settings.sitesFolder = sitesFolderField!!.text
        settings.port = portField!!.text
    }

    override fun getDisplayName(): String {
        return "CX Server"
    }

    override fun reset() {
        val settings = CXServerSettings.getInstance().myState
        val projectBasePath = ProjectManager.getInstance().openProjects.firstOrNull()?.basePath ?: ""
        sitesFolderField!!.text = if (settings.sitesFolder.isEmpty()) "$projectBasePath/exports/sites" else settings.sitesFolder
        portField!!.text = if (settings.port.isEmpty()) "5555" else settings.port
    }

    override fun disposeUIResources() {
        panel = null
        sitesFolderField = null
        portField = null
    }
}
