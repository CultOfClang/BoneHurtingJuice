package org.cultofclang.bonehurtingjuice

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.data.Levelled
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.vehicle.VehicleEnterEvent
import org.bukkit.event.vehicle.VehicleExitEvent
import org.bukkit.event.vehicle.VehicleMoveEvent
import java.util.*


class BoneHurtDamageEvent(player: Player, damage: Double) : EntityDamageEvent(player, DamageCause.FALL, damage) {

}

object MoveListener : Listener {
    private val fallDistances:MutableMap<UUID, Float> = mutableMapOf()

    @EventHandler
    fun onPlayerDamage(event: EntityDamageEvent) {
        val ent = event.entity

        if(event is BoneHurtDamageEvent) {
            //ent.sendMessage("off ouch owie my bones")
            return;
        }

        if (ent is Player && event.cause == EntityDamageEvent.DamageCause.FALL) {
            //ent.sendMessage("off my bones")
            event.isCancelled = true
            playerFall(ent, 0f)
        }
    }

    @EventHandler
    fun cantUseBedsIfYouAreFalling(e: PlayerBedEnterEvent) {
        val player = e.player

        if(player.isInsideVehicle || player.fallDistance > Bones.minFallDist ){
            e.isCancelled = true
        }

    }

    @EventHandler
    fun ifThisWorks(e: PlayerMoveEvent) {
        val player = e.player

        if(!player.isInsideVehicle)
        playerFall(player, player.fallDistance)

        val inBlock = player.location.block
        val data =inBlock.blockData
        if(data is Levelled){
           if(data.level >= 8){
               //player.sendMessage("in moving water")
               player.velocity = player.velocity.setY(-0.1)
           }
        }
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

        if(bonesBroken > Bones.minFallDist) {
            //if(debugPrint)
            val damage = ((bonesBroken- Bones.minFallDist)* Bones.damageMultiplier)
            player.noDamageTicks = 0
            player.damage(damage)
            val damageCause =  BoneHurtDamageEvent(player, damage)
            player.lastDamageCause = damageCause
            Bukkit.getPluginManager().callEvent(damageCause)
        }
    }

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
