package org.cultofclang.bonehurtingjuice

import org.cultofclang.bonehurtingjuice.command.InfoCmd
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

val Bones: BoneHurtPlugin by lazy { JavaPlugin.getPlugin(
    BoneHurtPlugin::class.java) }


class BoneHurtPlugin : JavaPlugin() {
    public var hurtBlocks: Set<Material> = emptySet()
    public var doApplyForce = false
    public var damageMultiplier = 0.5

    public override fun onEnable(){
        getCommand("boneinfo")?.setExecutor(InfoCmd)
        Bukkit.getPluginManager().registerEvents(MoveListener, this)
        logger.info("Off ouch owe my bones!")



        val c = config


        saveDefaultConfig()

        //c.addDefault("hurtBlocks",hurtBlocks.toList())

        val hurtBlockRaw=c.getStringList("hurtBlocks")
        if(hurtBlockRaw != null)
        hurtBlocks = hurtBlockRaw.map {Material.valueOf(it)}.toSet()
        doApplyForce = c.getBoolean("applyFallForce", false)
        damageMultiplier = c.getDouble("damageMultiplier", 0.5)
    }

    public override fun onDisable() {
        logger.info("I don't think you see the gravity of the situation.")
        saveDefaultConfig()
    }
}