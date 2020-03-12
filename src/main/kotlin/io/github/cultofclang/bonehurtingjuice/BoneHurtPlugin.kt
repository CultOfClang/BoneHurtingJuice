package io.github.cultofclang.bonehurtingjuice

import io.github.cultofclang.bonehurtingjuice.command.InfoCmd
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

val boneHurtPlugin: BoneHurtPlugin by lazy { JavaPlugin.getPlugin(BoneHurtPlugin::class.java) }


class BoneHurtPlugin : JavaPlugin() {
    public override fun onEnable(){
        getCommand("boneinfo")?.setExecutor(InfoCmd)
        Bukkit.getPluginManager().registerEvents(MoveListener, this)
        Bukkit.getLogger().info("Config Val: ${config.getString("configVal") ?: "[no val listed]"}")
        logger.info("Off ouch owe my bones!")
    }

    public override fun onDisable() {
        logger.info("I don't think you see the gravity of the situation.")
    }
}