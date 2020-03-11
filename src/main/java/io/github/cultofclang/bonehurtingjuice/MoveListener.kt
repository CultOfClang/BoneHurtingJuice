package io.github.cultofclang.bonehurtingjuice

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.util.NumberConversions.floor
import org.bukkit.util.Vector

object MoveListener : Listener{


    @EventHandler
    public fun onStickClick(e: PlayerInteractEvent) {
        if(e.action == Action.PHYSICAL) return; //make sure it isn't a pressure plate or tripwire

        if(e.item?.type == Material.STICK) {
            e.player.sendMessage("You clicked a stick named [${e.item?.itemMeta?.displayName ?: "Unnamed"}]")
        }
    }

    val hurtBlock = setOf(Material.LAVA, Material.SLIME_BLOCK, Material.WATER, Material.HAY_BLOCK, Material.COBWEB,
            Material.LADDER, Material.VINE,

            Material.HONEY_BLOCK, // need to implement sliding

            Material.WHITE_BED,
            Material.ORANGE_BED,
            Material.MAGENTA_BED,
            Material.LIGHT_BLUE_BED,
            Material.YELLOW_BED,
            Material.LIME_BED,
            Material.PINK_BED,
            Material.GRAY_BED,
            Material.LIGHT_GRAY_BED,
            Material.CYAN_BED,
            Material.BLUE_BED,
            Material.PURPLE_BED,
            Material.GREEN_BED,
            Material.BROWN_BED,
            Material.RED_BED,
            Material.BLACK_BED
            )


    @EventHandler
    public fun onMove(e: PlayerMoveEvent){


        // need to get every block i go through in this time frame


        if(e.player.isFlying)
            return;

        val fallDistance = e.player.fallDistance

        if(fallDistance > 3) {
            val start = e.from.toVector()
            val vel = e.player.velocity

            val world = e.from.world!!

            for (p in shittyLine(start, vel)){
                val location = p.toLocation(world)
                val block = location.block
                if(block.type in hurtBlock){

                    val damage = (fallDistance / 2 -3).coerceAtLeast(0f)
                    //e.player.sendMessage("ouch $damage you moved $p ${block.type.name} after $fallDistance")



                    e.player.damage(damage.toDouble())
                    break
                }
            }


        }

    }
}


fun Vector.block(): Vector {
    return Vector(blockX, blockY, blockZ)
}

fun shittyLine(from:Vector, v:Vector) = sequence<Vector> {
    val startY = floor(from.y)
    val endY =floor(from.y+v.y)

    if(startY == endY ) {
        yield(from.block())
    } else {
        val vel = v.clone().multiply(1/v.y)
    val o = from.clone().subtract(vel.clone().multiply(startY))

    for (y in startY downTo endY-1){
        yield(o.clone().add(vel.clone().multiply(y)).block())
    }
    }
}