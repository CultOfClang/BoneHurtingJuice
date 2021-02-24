package org.cultofclang.bonehurtingjuice

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin
import org.cultofclang.bonehurtingjuice.command.InfoCmd

val Bones: BoneHurtPlugin by lazy { JavaPlugin.getPlugin(BoneHurtPlugin::class.java) }

class BoneHurtPlugin : JavaPlugin() {
    var hurtBlocks: Set<Material> = emptySet()
    var damageMultiplier = 0.5
    var minFallDist = 3
    var waterfallDamageMultiplier = 0.5

    override fun onEnable() {
        getCommand("boneinfo")?.setExecutor(InfoCmd)
        Bukkit.getPluginManager().registerEvents(MoveListener, this)
        logger.info("Off ouch owe my bones!")

        // kotlin magic
        val c = config

        saveDefaultConfig()

        val hurtBlockRaw = c.getStringList("hurtBlocks")
        hurtBlocks = hurtBlockRaw.map { Material.valueOf(it) }.toSet()
        damageMultiplier = c.getDouble("damageMultiplier", 0.5)
        minFallDist = c.getInt("minFallDist", 3)
        waterfallDamageMultiplier = c.getDouble("waterfallDamageMultiplier",  0.5)
    }

    override fun onDisable() {
        logger.info("I don't think you see the gravity of the situation.")
        saveDefaultConfig()
    }
}
