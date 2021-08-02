package org.cultofclang.bonehurtingjuice

import com.mineinabyss.idofront.plugin.registerEvents
import com.mineinabyss.idofront.slimjar.LibraryLoaderInjector
import org.bukkit.plugin.java.JavaPlugin
import org.cultofclang.bonehurtingjuice.command.InfoCmd

val Bones: BoneHurtPlugin by lazy { JavaPlugin.getPlugin(BoneHurtPlugin::class.java) }

class BoneHurtPlugin : JavaPlugin() {

    override fun onEnable() {
        LibraryLoaderInjector.inject(this)
        getCommand("boneinfo")?.setExecutor(InfoCmd)
        registerEvents(
            MoveListener
        )
        logger.info("Off ouch owe my bones!")
    }

    override fun onDisable() {
        logger.info("I don't think you see the gravity of the situation.")
        saveDefaultConfig()
    }
}
