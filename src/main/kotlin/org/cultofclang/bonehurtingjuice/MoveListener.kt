package org.cultofclang.bonehurtingjuice

import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.vehicle.VehicleEnterEvent
import org.bukkit.event.vehicle.VehicleExitEvent
import org.bukkit.event.vehicle.VehicleMoveEvent
import org.checkerframework.checker.index.qual.Positive
import java.util.*
import kotlin.random.Random


class BoneHurtDamageEvent(player: Player, damage: Double) : EntityDamageEvent(player, DamageCause.FALL, damage) {

}

internal object MoveListener : Listener {
    val fallDistances: MutableMap<UUID, Float> = mutableMapOf()

    private val beds = setOf(
        Material.WHITE_BED, Material.ORANGE_BED, Material.MAGENTA_BED,
        Material.LIGHT_BLUE_BED, Material.YELLOW_BED, Material.LIME_BED, Material.PINK_BED,
        Material.GRAY_BED, Material.LIGHT_GRAY_BED, Material.CYAN_BED, Material.BLUE_BED,
        Material.PURPLE_BED, Material.GREEN_BED, Material.BROWN_BED, Material.RED_BED, Material.BLACK_BED
    )

    @EventHandler
    fun EntityDamageEvent.onPlayerDamage() {
        if (this is BoneHurtDamageEvent) return

        if (entity is Player && cause == EntityDamageEvent.DamageCause.FALL) {
            isCancelled = true
            (entity as Player).hurtBones(0f)
        }
    }

    @EventHandler
    fun PlayerInteractEvent.onPlayerInteract() {
        if (player.fallDistance > BoneHurtConfig.data.minFallDist && clickedBlock?.type in beds)
            isCancelled = true
    }

    @EventHandler
    fun EntityMoveEvent.entityMove() {
        if(entity.passengers.isNotEmpty()) {
            entity.passengers.filterIsInstance<Player>().forEach { rider ->
                rider.fallDistance = entity.fallDistance
                rider.hurtBones(entity.fallDistance)
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun PlayerTeleportEvent.playerTeleport() {
        if (player.gameMode == GameMode.CREATIVE) return
        if (this.cause != PlayerTeleportEvent.TeleportCause.ENDER_PEARL && this.cause != PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT) return
        if (this.isCancelled) return //Cancels the falldamage if another plugin cancels the event.
        val teleportDistance = player.location.y - to.toBlockLocation().y
        if (teleportDistance <= 0) return
        player.fallDistance = teleportDistance.toFloat()
        player.hurtBones(player.fallDistance)
    }

    @EventHandler
    fun PlayerMoveEvent.playerMove() {
        if (player.gameMode != GameMode.SURVIVAL && player.gameMode != GameMode.ADVENTURE) return

        if (!player.isInsideVehicle)
            player.hurtBones(player.fallDistance)

        player.location.findLocationAround(radius = 1, scale = 0.30) {
            val inBlock = it.block
            val higherBlock = it.add(0.0, 4.0, 0.0).block
            inBlock.isFlowing && higherBlock.isFlowing
        }?.let {

            //bypass armor damage reduction
            player.damage(0.0001) // trigger damage sound effect
            player.health = (player.health - (0.25 * BoneHurtConfig.data.waterfallDamageMultiplier)).coerceAtLeast(0.0)

            player.world.spawnParticle(Particle.CLOUD, player.location.add(0.0, 0.75, 0.0), 1, 0.5, 0.5, 0.5, 0.3)
            player.velocity = player.velocity.apply {
                x = Random.nextDouble(
                    -BoneHurtConfig.data.waterfallMoveMultiplier,
                    BoneHurtConfig.data.waterfallMoveMultiplier
                )
                y = -0.1
                z = Random.nextDouble(
                    -BoneHurtConfig.data.waterfallMoveMultiplier,
                    BoneHurtConfig.data.waterfallMoveMultiplier
                )
            }
        }
        player.location.findLocationAround(radius = 1, scale = 0.30) {
            val higherBlock = it.add(0.0, 4.0, 0.0).block
            higherBlock.isBubbleColumn
        }?.let {
            if (player.maximumAir <= 0) {
                player.remainingAir = player.remainingAir
                player.damage(0.0001) // trigger damage sound effect
                player.health =
                    (player.health - (0.25 * BoneHurtConfig.data.bubbleColumnDamageMultiplier)).coerceAtLeast(0.0)

            } else {
                player.remainingAir = (player.maximumAir - BoneHurtConfig.data.bubbleColumnBreathMultiplier)
                player.maximumAir = player.remainingAir.coerceAtLeast(0)
            }

        }
        player.maximumAir = player.remainingAir
    }

    @EventHandler
    fun PlayerRespawnEvent.onRespawn() {
        player.resetFallDistance()
    }

    @EventHandler
    fun VehicleMoveEvent.onVehicleMove() = forRidingPlayers { rider ->
        rider.fallDistance = vehicle.fallDistance
        rider.hurtBones(vehicle.fallDistance)
    }

    @EventHandler
    fun VehicleExitEvent.onExit() = forRidingPlayers { rider ->
        rider.fallDistance = vehicle.fallDistance
    }

    @EventHandler
    fun VehicleEnterEvent.onEnter() = forRidingPlayers { rider ->
        vehicle.fallDistance += rider.fallDistance
        rider.fallDistance = 0f
    }
}
