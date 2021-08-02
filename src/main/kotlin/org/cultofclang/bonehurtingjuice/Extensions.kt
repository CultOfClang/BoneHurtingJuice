package org.cultofclang.bonehurtingjuice

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.data.Levelled
import org.bukkit.entity.Player
import org.bukkit.event.vehicle.VehicleEvent

internal fun VehicleEvent.forRidingPlayers(action: (Player) -> Unit) = vehicle.passengers
    .filterIsInstance<Player>()
    .forEach(action)

internal val Block.isFlowing: Boolean
    get() {
        val blockData = blockData
        return (blockData is Levelled) && blockData.level >= 8
    }

internal fun Location.findLocationAround(radius: Int, scale: Double, predicate: (Location) -> Boolean): Location? {
    for (x in -radius..radius) {
        for (z in -radius..radius) {
            val checkLoc = clone().add(x * scale, 0.0, z * scale)
            if (predicate(checkLoc))
                return checkLoc
        }
    }
    return null
}

internal fun Player.resetFallDistance() {
    fallDistance = 0f
    MoveListener.fallDistances[uniqueId] = 0f
}

internal fun Player.hurtBones(fallDist: Float) {
    val lastFallDist = MoveListener.fallDistances.getOrDefault(uniqueId, 0f)
    val bonesBroken = (lastFallDist - fallDist).coerceAtLeast(0f)
    MoveListener.fallDistances[uniqueId] = fallDist

    if (bonesBroken > BoneHurtConfig.data.minFallDist) {
        val damage = ((bonesBroken - BoneHurtConfig.data.minFallDist) * BoneHurtConfig.data.damageMultiplier)
        noDamageTicks = 0
        damage(damage)
        val damageCause = BoneHurtDamageEvent(this, damage)
        lastDamageCause = damageCause
        Bukkit.getPluginManager().callEvent(damageCause)
    }
}
