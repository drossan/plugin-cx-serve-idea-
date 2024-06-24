package com.drossan.cxserver

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import java.io.File
import java.net.NetworkInterface

class RunCXServerAction : AnAction() {
    private var serverProcess: Process? = null

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        execute(project)
    }

    fun execute(project: Project) {
        val settings = CXServerSettings.getInstance().myState
        val projectBasePath = project.basePath ?: return

        val sitesFolder = if (settings.sitesFolder.isEmpty()) {
            "$projectBasePath/exports/sites"
        } else {
            settings.sitesFolder
        }

        val port = if (settings.port.isEmpty()) {
            "5555"
        } else {
            settings.port
        }

        // Ejecutar la lógica principal
        execute(projectBasePath, sitesFolder, port)
    }

    private fun execute(projectBasePath: String, sitesFolder: String, port: String) {
        val folders = getFolders(sitesFolder)
        if (folders.isEmpty()) {
            Messages.showMessageDialog(
                "No folders were found in $sitesFolder",
                "Error",
                Messages.getErrorIcon()
            )
            return
        }

        val selectedFolder = selectFolder(folders) ?: return
        copyConfiguration(projectBasePath, sitesFolder, selectedFolder, port)
    }

    private fun getFolders(pathFolder: String): List<String> {
        val folder = File(pathFolder)
        return if (folder.exists() && folder.isDirectory) {
            folder.listFiles()?.filter { it.isDirectory }?.map { it.name } ?: emptyList()
        } else {
            emptyList()
        }
    }

    private fun selectFolder(folders: List<String>): String? {
        return Messages.showEditableChooseDialog(
            "Select a folder:",
            "Select Folder",
            Messages.getQuestionIcon(),
            folders.toTypedArray(),
            folders.first(),
            null
        )
    }

    private fun copyConfiguration(projectBasePath: String, sitesFolder: String, selectedFolder: String, port: String) {
        val source = File("$projectBasePath/serve.json")
        val destination = File("$sitesFolder/$selectedFolder/dist/serve.json")

        if (!source.exists()) {
            Messages.showMessageDialog(
                "The source file doesn't exist: ${source.absolutePath}",
                "Error",
                Messages.getErrorIcon()
            )
            return
        }

        source.copyTo(destination, overwrite = true)
        val localIP = getLocalIPAddress()
        Messages.showMessageDialog(
            "Copied server configuration\nServing from the folder: $sitesFolder/$selectedFolder/dist\n" +
                    "http://localhost:$port/\n" +
                    "http://$localIP:$port/",
            "Info",
            Messages.getInformationIcon()
        )

        launchServer(sitesFolder, selectedFolder, port)
    }

    private fun getLocalIPAddress(): String {
        return NetworkInterface.getNetworkInterfaces().toList()
            .flatMap { it.inetAddresses.toList() }
            .firstOrNull { !it.isLoopbackAddress && it.isSiteLocalAddress }
            ?.hostAddress ?: "localhost"
    }

    private fun launchServer(sitesFolder: String, selectedFolder: String, port: String) {
        val processBuilder = ProcessBuilder("serve", "-l", "tcp://0.0.0.0:$port", "$sitesFolder/$selectedFolder/dist")
        processBuilder.inheritIO()

        try {
            serverProcess = processBuilder.start()
            val localIP = getLocalIPAddress()
            println("INFO  Accepting connections at http://localhost:$port and http://$localIP:$port")
        } catch (e: Exception) {
            Messages.showMessageDialog(
                "Error starting the server: ${e.message}",
                "Error",
                Messages.getErrorIcon()
            )
        }
    }

    fun stopServer() {
        serverProcess?.destroy()
        serverProcess = null
        println("INFO  Server stopped")
    }
}
