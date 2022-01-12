package org.cultofclang.bonehurtingjuice

import com.mineinabyss.idofront.platforms.IdofrontPlatforms
import com.mineinabyss.idofront.plugin.registerEvents
import org.bukkit.plugin.java.JavaPlugin
import org.cultofclang.bonehurtingjuice.command.InfoCmd

val Bones: BoneHurtPlugin by lazy { JavaPlugin.getPlugin(BoneHurtPlugin::class.java) }

class BoneHurtPlugin : JavaPlugin() {

    override fun onLoad() {
        IdofrontPlatforms.load(this, "mineinabyss")
    }

    override fun onEnable() {
        saveDefaultConfig()
        BoneHurtConfig.load()
        getCommand("boneinfo")?.setExecutor(InfoCmd)
        registerEvents(
            MoveListener
        )
        logger.info("Off ouch owe my bones!")
    }

    override fun onDisable() {
        logger.info("I don't think you see the gravity of the situation.")
    }
}
