package com.lythen.maidindustry

import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityAltar
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

class AltarArmInteractionPoint(type: ArmInteractionPointType, level: Level, pos: BlockPos, state: BlockState) :
    ArmInteractionPoint(type, level, pos, state) {

    override fun insert(armBlockEntity: ArmBlockEntity, stack: ItemStack, simulate: Boolean): ItemStack {
        val te = level.getBlockEntity(pos)
        if (te is TileEntityAltar) {
            if (te.isCanPlaceItem && te.handler.getStackInSlot(0).isEmpty) {
                if (!simulate) {
                    te.handler.setStackInSlot(0, stack.copyWithCount(1))
                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.PLAYERS, 1f, 1f)
                    AltarCraftingManager.tryCrafting(level, te)
                    te.refresh()
                }
                val remainder = stack.copy()
                remainder.shrink(1)
                return remainder
            }
        }
        return stack
    }
}
