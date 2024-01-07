package net.aspw.client.features.module.impl.targets

import net.aspw.client.Client
import net.aspw.client.features.module.Module
import net.aspw.client.features.module.ModuleCategory
import net.aspw.client.features.module.ModuleInfo

@ModuleInfo(name = "Players", description = "", category = ModuleCategory.TARGETS, array = false)
class Players : Module() {
    init {
        if (!Client.fileManager.modulesConfig.hasConfig() || !Client.fileManager.valuesConfig.hasConfig())
            state = true
    }
}