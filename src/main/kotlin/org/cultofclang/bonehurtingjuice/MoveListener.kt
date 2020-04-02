package org.cultofclang.bonehurtingjuice

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.vehicle.VehicleEnterEvent
import org.bukkit.event.vehicle.VehicleExitEvent
import org.bukkit.event.vehicle.VehicleMoveEvent
import java.util.*

object MoveListener : Listener {
    private val fallDistances:MutableMap<UUID, Float> = mutableMapOf()

    @EventHandler
    fun ifThisWorks(e: PlayerMoveEvent) {
        val player = e.player

        if(!player.isInsideVehicle)
        playerFall(player, player.fallDistance)
    }

    @EventHandler
    fun onRespawn(e: PlayerRespawnEvent) {
        val player = e.player
        player.fallDistance = 0f
        fallDistances[player.uniqueId]  = 0f
    }

    private fun playerFall(player:Player, fallDist:Float){
        val lastFallDist = fallDistances.getOrDefault(player.uniqueId, 0f)
        val bonesBroken = (lastFallDist - fallDist).coerceAtLeast(0f)
        fallDistances[player.uniqueId]  = fallDist

        //player.sendMessage("ouch $lastFallDist $fallDist")

        if(bonesBroken > Bones.minFallDist) {
            //if(debugPrint)
            val damage = ((bonesBroken- Bones.minFallDist)* Bones.damageMultiplier)
            player.noDamageTicks = 0
            player.damage(damage)
        }
    }

    const val debugPrint = false

    @EventHandler
    fun onMove(e: VehicleMoveEvent){
        for (rider in e.vehicle.passengers){
            if(rider != null && rider is Player){
                //rider.sendMessage("boat fell ${e.vehicle.fallDistance}")
                rider.fallDistance = e.vehicle.fallDistance
                playerFall(rider, e.vehicle.fallDistance)
            }
        }
    }

    @EventHandler
    fun onExit(e: VehicleExitEvent){
        for (rider in e.vehicle.passengers){
            if(rider != null && rider is Player){
                rider.fallDistance = e.vehicle.fallDistance
            }
        }
    }

    @EventHandler
    fun onEnter(e: VehicleEnterEvent){
        for (rider in e.vehicle.passengers){
            if(rider != null && rider is Player){
                e.vehicle.fallDistance += rider.fallDistance
                rider.fallDistance = 0f
            }
        }
    }

}
