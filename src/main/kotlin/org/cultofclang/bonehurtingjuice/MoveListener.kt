package org.cultofclang.bonehurtingjuice

import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.vehicle.VehicleMoveEvent
import org.bukkit.util.NumberConversions.ceil
import org.bukkit.util.NumberConversions.floor
import org.bukkit.util.Vector
import kotlin.math.abs
import kotlin.random.Random

object MoveListener : Listener {
    @EventHandler
    fun onMove(e: VehicleMoveEvent){
        for (rider in e.vehicle.passengers){
            if(rider != null && rider is Player){
                doFallDamage(rider, e.vehicle.fallDistance, e.from, allFallsHurt = true)
            }
        }
    }

    private fun doFallDamage(player: Player, fallDistance:Float, from:Location, allFallsHurt:Boolean = false, to:Location? = null){
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
                val newVel = player.velocity.add(yeet)

                player.velocity = Vector(
                    newVel.x.coerceIn(-maxHorVel, maxHorVel),
                    newVel.y,
                    newVel.z.coerceIn(-maxHorVel, maxHorVel)
                )
            }

            val spawnPos = player.location
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

            if(to != null){
                val velY = player.velocity.y
                val moveY = to.y - from.y
                abs(velY - moveY)
            }

            val start = from.toVector()
            val vel = player.velocity

            val world = from.world!!

            for (p in shittyLine(start, vel)) {
                val location = p.toLocation(world)
                val block = location.block

                //player.sendMessage("look at ${block.location.blockY} ${block.type.name}")
                if ((allFallsHurt && block.type != Material.AIR )||block.type in Bones.hurtBlocks) {

                    val damage = ((fallDistance-3)* Bones.damageMultiplier).coerceAtLeast(0.0)
                    player.sendMessage("ouch! ${block.type.name} wasn't as soft as it looked")
                    player.damage(damage)
                    break
                }
            }
        }
    }

    @EventHandler
    fun onMove(e: PlayerMoveEvent) {


        val velY = e.player.velocity.y
        val moveY = e.to!!.y - e.from.y
        //if(e.player.fallDistance > 10) e.player.sendMessage("moving at ${velY} ${moveY}")


        doFallDamage(e.player, e.player.fallDistance, e.from, to=e.to)
    }
}

fun Vector.block(): Vector {
    return Vector(blockX, blockY, blockZ)
}

fun shittyLine(from: Vector, v: Vector) = sequence {
        val steps = -floor(v.y-1)


    for (dy in 0..steps) {
        yield(from.clone().add(Vector(0, -dy, 0)).block())
    }
}