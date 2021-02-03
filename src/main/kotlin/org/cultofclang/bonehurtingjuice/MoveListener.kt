package org.cultofclang.bonehurtingjuice

import org.bukkit.GameMode
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.vehicle.VehicleEnterEvent
import org.bukkit.event.vehicle.VehicleExitEvent
import org.bukkit.event.vehicle.VehicleMoveEvent
import java.util.*
import kotlin.random.Random


class BoneHurtDamageEvent(player: Player, damage: Double) : EntityDamageEvent(player, DamageCause.FALL, damage) {

}

internal object MoveListener : Listener {
    val fallDistances: MutableMap<UUID, Float> = mutableMapOf()

    @EventHandler
    fun onPlayerDamage(event: EntityDamageEvent) {
        if (event is BoneHurtDamageEvent) return

        val entity = event.entity

        if (entity is Player && event.cause == EntityDamageEvent.DamageCause.FALL) {
            event.isCancelled = true
            entity.hurtBones(0f)
        }
    }

    @EventHandler
    fun onSleepWhileFalling(e: PlayerBedEnterEvent) {
        val player = e.player

        if (player.isInsideVehicle || player.fallDistance > Bones.minFallDist) {
            e.isCancelled = true
        }

    }

    @EventHandler
    fun onSwimInWaterfall(e: PlayerMoveEvent) {
        val player = e.player
        if(player.gameMode != GameMode.SURVIVAL && player.gameMode != GameMode.ADVENTURE) return

        if (!player.isInsideVehicle)
            player.hurtBones(player.fallDistance)

        player.location.findLocationAround(radius = 1, scale = 0.25) {
            val inBlock = it.block
            val higherBlock = it.add(0.0, 4.0, 0.0).block
            inBlock.isFlowing && higherBlock.isFlowing
        }?.let {
            player.damage(0.0001)
            player.health = player.health - (0.25 * Bones.wetDamageMultiplier)
            player.world.spawnParticle(Particle.CLOUD, player.location.add(0.0, 0.75, 0.0), 1, 0.5, 0.5, 0.5, 0.3)
            player.velocity = player.velocity.apply {
                x = Random.nextDouble(-0.15, 0.15) //TODO make configurable
                y = -0.1
                z = Random.nextDouble(-0.15, 0.15)
            }
        }
    }

    @EventHandler
    fun onRespawn(e: PlayerRespawnEvent) {
        val player = e.player
        player.resetFallDistance()
    }

    @EventHandler
    fun onVehicleMove(e: VehicleMoveEvent) = e.forRidingPlayers { rider ->
        rider.fallDistance = e.vehicle.fallDistance
        rider.hurtBones(e.vehicle.fallDistance)
    }

    @EventHandler
    fun onExit(e: VehicleExitEvent) = e.forRidingPlayers { rider ->
        rider.fallDistance = e.vehicle.fallDistance
    }

    @EventHandler
    fun onEnter(e: VehicleEnterEvent) = e.forRidingPlayers { rider ->
        e.vehicle.fallDistance += rider.fallDistance
        rider.fallDistance = 0f
    }
}
