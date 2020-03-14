package io.github.cultofclang.bonehurtingjuice

import org.bukkit.Material


data class Config (
    val hurtBlocks: Set<Material>,
    val doApplyForce: Boolean,
    val damageMultiplier: Double
)