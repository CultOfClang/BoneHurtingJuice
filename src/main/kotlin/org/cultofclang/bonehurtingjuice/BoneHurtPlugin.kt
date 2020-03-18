package org.cultofclang.bonehurtingjuice

import org.cultofclang.bonehurtingjuice.command.InfoCmd
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

val Bones: BoneHurtPlugin by lazy { JavaPlugin.getPlugin(
    BoneHurtPlugin::class.java) }


class BoneHurtPlugin : JavaPlugin() {
    var hurtBlocks: Set<Material> = emptySet()
    var doApplyForce = false
    var damageMultiplier = 0.5

    override fun onEnable(){
        getCommand("boneinfo")?.setExecutor(InfoCmd)
        Bukkit.getPluginManager().registerEvents(MoveListener, this)
        logger.info("Off ouch owe my bones!")

        // kotlin magic
        val c = config

        saveDefaultConfig()

        val hurtBlockRaw=c.getStringList("hurtBlocks")
        hurtBlocks = hurtBlockRaw.map {Material.valueOf(it)}.toSet()
        doApplyForce = c.getBoolean("applyFallForce", false)
        damageMultiplier = c.getDouble("damageMultiplier", 0.5)
    }

    override fun onDisable() {
        logger.info("I don't think you see the gravity of the situation.")
        saveDefaultConfig()
    }
}