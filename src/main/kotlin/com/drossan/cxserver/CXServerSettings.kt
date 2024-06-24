package com.drossan.cxserver

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "CXServerSettings", storages = [Storage("CXServerSettings.xml")])
@Service
class CXServerSettings : PersistentStateComponent<CXServerSettings.MyState> {

    var myState = MyState()

    override fun getState(): MyState {
        return myState
    }

    override fun loadState(state: MyState) {
        this.myState = state
    }

    data class MyState(
        var sitesFolder: String = "",
        var port: String = "5555"
    )

    companion object {
        fun getInstance(): CXServerSettings {
            return com.intellij.openapi.application.ApplicationManager.getApplication().getService(CXServerSettings::class.java)
        }
    }
}
