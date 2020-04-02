package org.cultofclang.bonehurtingjuice

import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByBlockEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.vehicle.VehicleEnterEvent
import org.bukkit.event.vehicle.VehicleExitEvent
import org.bukkit.event.vehicle.VehicleMoveEvent
import org.bukkit.util.NumberConversions.ceil
import org.bukkit.util.NumberConversions.floor
import org.bukkit.util.Vector
import java.util.*
import kotlin.math.abs
import kotlin.random.Random


object MoveListener : Listener {
    private val fallDistances:MutableMap<UUID, Float> = mutableMapOf()


    @EventHandler
    fun ifThisWorks(e: PlayerMoveEvent) {

        val player = e.player
        val lastFallDist = fallDistances.getOrDefault(player.uniqueId, 0f)
        val newFallDist = player.fallDistance

        val bonesBroken = (lastFallDist - newFallDist).coerceAtLeast(0f)


        if(bonesBroken > Bones.minFallDist) {
            if(debugPrint) e.player.sendMessage("ouch x$bonesBroken")
            val damage = ((bonesBroken- Bones.minFallDist)* Bones.damageMultiplier)



        }
        fallDistances[e.player.uniqueId]  = newFallDist
    }

    const val debugPrint = false
    @EventHandler
    fun onMove(e: VehicleMoveEvent){
        for (rider in e.vehicle.passengers){
            if(rider != null && rider is Player){
                var vel = rider.velocity
                doFallDamage(rider, e.vehicle.fallDistance, e.from, to = e.to, allFallsHurt = true, velocity = vel)
                //rider.sendMessage("move ${e.to.y} ${e.from.y} ${vel} ${e.vehicle.fallDistance}")
            }
        }
    }


    @EventHandler
    fun onEnter(e: VehicleExitEvent){
        for (rider in e.vehicle.passengers){
            if(rider != null && rider is Player){
                rider.fallDistance += e.vehicle.fallDistance
            }
        }
    }

    @EventHandler
    fun onExit(e: VehicleEnterEvent){
        for (rider in e.vehicle.passengers){
            if(rider != null && rider is Player){
                e.vehicle.fallDistance += rider.fallDistance
            }
        }
    }

    private val Material.humanName get() = name.replace('_',' ').toLowerCase()

    private fun doFallDamage(player: Player, fallDistance:Float, from:Location,to:Location,velocity:Vector, allFallsHurt:Boolean = false){
        if (player.isInvulnerable || player.gameMode == GameMode.CREATIVE)
            return

        if(fallDistance > 50){


            if(Bones.doApplyForce) {
                val yeet = Vector.getRandom()
                yeet.y = 0.0
                yeet.normalize()
                val max = fallDistance / 100.0

                yeet.multiply(max)

                //val angle = 0f
                //val wind = Vector(sin(angle), 0f, cos(angle))

                val maxHorVel = 3.0
                val newVel = velocity.add(yeet)

                player.velocity = Vector(
                    newVel.x.coerceIn(-maxHorVel, maxHorVel),
                    newVel.y,
                    newVel.z.coerceIn(-maxHorVel, maxHorVel)
                )
            }

            val spawnPos = from
            if(Random.nextFloat() < 1f/20){
                player.playSound(spawnPos, Sound.ITEM_ELYTRA_FLYING, 1f, 1f)
            }

            if(Random.nextFloat() < 1f/5) {
                player.damage(5.0)
            }

            player.spawnParticle(Particle.CLOUD, spawnPos, 10, 0.5,0.5,0.5)

            if(Random.nextFloat() < 1f/10000)
                player.sendMessage("yeet")
        }

        if (fallDistance > 3) {

            val start = from.toVector()
            var vel = player.velocity

            if(to != null){
                val movevel = to.toVector().clone().subtract(from.toVector())
                val velY = velocity.y
                val moveY = to.y - from.y
                val diff = abs(velY - moveY)

                vel.y = minOf(moveY, velY)

                // need to test with

                if(debugPrint){
                player.sendMessage("moving at $velY $moveY $diff")
                }
                    //if(diff<0.1) return
            }



            val world = from.world!!

            for (p in shittyLine(start, vel)) {
                val location = p.toLocation(world)
                val block = location.block

                if(debugPrint){
                player.sendMessage("look at ${block.location.blockY} ${block.type.name}")
                }
                    if ((allFallsHurt && block.type != Material.AIR )||block.type in Bones.hurtBlocks) {

                    val damage = ((fallDistance-3)* Bones.damageMultiplier).coerceAtLeast(0.0)

                    if(damage > 1) {
                        player.sendMessage("ouch! ${block.type.humanName} wasn't as soft as it looked")

                        //todo not sure if im doing double damage
                        player.noDamageTicks = 0
                        player.damage(damage)

                        //player.lastDamage = damage + player.lastDamage
                        //val fallEvent = EntityDamageByBlockEvent(block, player, EntityDamageEvent.DamageCause.FALL, damage)
                        //player.lastDamageCause = fallEvent
                        //Bukkit.getServer().pluginManager.callEvent(fallEvent);

                    }
                    break
                }
            }
        }
    }

    @EventHandler
    fun onMove(e: PlayerMoveEvent) {
        doFallDamage(e.player, e.player.fallDistance, e.from, to=e.to!!, velocity = e.player.velocity)
    }
}

fun Vector.block(): Vector {
    return Vector(blockX, blockY, blockZ)
}

fun shittyLine(from: Vector, v: Vector) = sequence {
        val steps = (abs(v.y)*2+1).toInt()

    val step = v.clone().multiply(1/v.y)

    for (dy in 0..steps) {

        //may be negative
        yield(from.clone().add(step.clone().multiply(-dy)).block())
    }
}