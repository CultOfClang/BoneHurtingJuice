package org.cultofclang.bonehurtingjuice

import com.charleskorn.kaml.Yaml
import com.mineinabyss.idofront.config.IdofrontConfig
import kotlinx.serialization.Serializable
import org.bukkit.Material

object BoneHurtConfig : IdofrontConfig<BoneHurtConfig.Data>(
    Bones, Data.serializer(),
    format = Yaml.default
) {
    @Serializable
    class Data(
        val hurtBlocks: Set<Material> = emptySet(),
        val damageMultiplier: Double = 0.5,
        val minFallDist: Int = 3,
        val waterfallDamageMultiplier: Double = 0.5,
        val waterfallMoveMultiplier: Double = 0.15,
        val bubbleColumnDamageMultiplier: Double = 2.0,
        val bubbleColumnBreathMultiplier: Int = 5,
    )
}
