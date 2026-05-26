package com.lythen.maidindustry

import com.github.tartaricacid.touhoulittlemaid.block.BlockAltar
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

class AltarArmInteractionPointType : ArmInteractionPointType() {
    override fun canCreatePoint(level: Level, pos: BlockPos, state: BlockState): Boolean {
        return state.block is BlockAltar
    }

    override fun createPoint(level: Level, pos: BlockPos, state: BlockState): ArmInteractionPoint? {
        return AltarArmInteractionPoint(this, level, pos, state)
    }
}
