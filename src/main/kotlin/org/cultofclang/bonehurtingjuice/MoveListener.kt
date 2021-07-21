package org.cultofclang.bonehurtingjuice

import org.bukkit.Bukkit
import org.bukkit.Bukkit.getServer
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.data.type.BubbleColumn
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityAirChangeEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
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

    private val beds = setOf(Material.WHITE_BED, Material.ORANGE_BED, Material.MAGENTA_BED,
        Material.LIGHT_BLUE_BED, Material.YELLOW_BED, Material.LIME_BED, Material.PINK_BED,
        Material.GRAY_BED, Material.LIGHT_GRAY_BED ,Material.CYAN_BED, Material.BLUE_BED,
        Material.PURPLE_BED, Material.GREEN_BED , Material.BROWN_BED, Material.RED_BED, Material.BLACK_BED)

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
    fun PlayerInteractEvent.onPlayerInteract() {
        if (player.fallDistance > Bones.minFallDist && clickedBlock?.type in beds)
            isCancelled = true
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
            (inBlock.isFlowing && higherBlock.isFlowing)
        }?.let {

            //bypass armor damage reduction
            player.damage(0.0001) // trigger damage sound effect
            player.health = (player.health - (0.25 * Bones.waterfallDamageMultiplier)).coerceAtLeast(0.0)

            player.world.spawnParticle(Particle.CLOUD, player.location.add(0.0, 0.75, 0.0), 1, 0.5, 0.5, 0.5, 0.3)
            player.velocity = player.velocity.apply {
                x = Random.nextDouble(-Bones.waterfallMoveMultiplier, Bones.waterfallMoveMultiplier)
                y = -0.1
                z = Random.nextDouble(-Bones.waterfallMoveMultiplier, Bones.waterfallMoveMultiplier)
            }
        }
        player.location.findLocationAround(radius = 1, scale = 0.50) {
            //val inBlock = it.block
            val higherBlock = it.add(0.0, 4.0, 0.0).block
            (higherBlock.isBubbleColumn)
        }?.let {
            if (player.maximumAir <= 0) {
                player.remainingAir = player.remainingAir
                player.damage(0.0001) // trigger damage sound effect
                player.health = (player.health - (0.25 * Bones.bubbleColumnBreathMultiplier)).coerceAtLeast(0.0)

            } else {
                player.remainingAir = (player.maximumAir - 5)
                player.maximumAir = player.remainingAir.coerceAtLeast(0)
            }

        }
        player.maximumAir = player.remainingAir
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
