package org.cultofclang.bonehurtingjuice

import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.vehicle.VehicleMoveEvent
import org.bukkit.util.NumberConversions.floor
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

object MoveListener : Listener {



    @EventHandler
    public fun onStickClick(e: PlayerInteractEvent) {
        if (e.action == Action.PHYSICAL) return; //make sure it isn't a pressure plate or tripwire

        if (e.item?.type == Material.STICK) {
            e.player.sendMessage("You clicked a stick named [${e.item?.itemMeta?.displayName ?: "Unnamed"}]")
        }
    }

    //minecarts when they fall onto track will cancel fall damage, but they arent transported

    @EventHandler
    public fun onMove(e: VehicleMoveEvent){
        for (rider in e.vehicle.passengers){
            if(rider != null && rider is Player){
                doFallDamage(rider, e.vehicle.fallDistance, e.from)
            }
        }
    }

    public fun doFallDamage(player: Player, fallDistance:Float, from:Location){
        if(fallDistance > 50){
            player.damage(fallDistance/100.0)



            if(Bones.doApplyForce) {
                val yeet = Vector.getRandom()
                yeet.y = 0.0
                yeet.normalize()
                val max = fallDistance / 100.0

                yeet.multiply(fallDistance / 100.0)


                val angle = 0f
                val wind = Vector(sin(angle), 0f, cos(angle))

                val maxHorVel = 3.0
                val newVel = player.velocity.add(yeet)


                player.velocity = Vector(
                    newVel.x.coerceIn(-maxHorVel, maxHorVel),
                    newVel.y,
                    newVel.z.coerceIn(-maxHorVel, maxHorVel)
                )
            }

            val spawnPos = player.location

            player.playSound(spawnPos, Sound.ITEM_ELYTRA_FLYING, 1f, 1f)
            player.spawnParticle(Particle.CLOUD, spawnPos, 10, 0.5,0.5,0.5)

            if(Random.nextFloat() < 1f/10000)
                player.sendMessage("yeet")
        }

        if (fallDistance > 3) {
            val start = from.toVector()
            val vel = player.velocity

            val world = from.world!!

            for (p in shittyLine(start, vel)) {
                val location = p.toLocation(world)
                val block = location.block
                if (block.type in Bones.hurtBlocks) {

                    val damage = ((fallDistance-3)* Bones.damageMultiplier).coerceAtLeast(0.0)
                    player.sendMessage("ouch! ${block.type.name} wasn't as soft as it looked")
                    player.damage(damage)
                    break
                }
            }
        }
    }

    @EventHandler
    public fun onMove(e: PlayerMoveEvent) {

        if (e.player.isFlying)
            return;

        doFallDamage(e.player, e.player.fallDistance, e.from)
    }
}


fun Vector.block(): Vector {
    return Vector(blockX, blockY, blockZ)
}

fun shittyLine(from: Vector, v: Vector) = sequence<Vector> {
    val startY = floor(from.y)
    val endY = floor(from.y + v.y)

    if (startY == endY) {
        yield(from.block())
    } else {
        val vel = v.clone().multiply(1 / v.y)
        val o = from.clone().subtract(vel.clone().multiply(startY))

        for (y in startY downTo endY - 1) {
            yield(o.clone().add(vel.clone().multiply(y)).block())
        }
    }
}