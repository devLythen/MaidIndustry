package com.lythen.maidindustry

import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityAltar
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandler

class AltarItemHandlerCapability(private val altar: TileEntityAltar) : IItemHandler {
    override fun getSlots(): Int {
        return altar.handler.slots
    }

    override fun getStackInSlot(slot: Int): ItemStack {
        return altar.handler.getStackInSlot(slot)
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (!altar.isCanPlaceItem || !altar.handler.getStackInSlot(slot).isEmpty) {
            return stack
        }

        if (!simulate) {
            altar.handler.setStackInSlot(slot, stack.copyWithCount(1))
            val level = altar.level
            if (level != null) {
                level.playSound(null, altar.blockPos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.PLAYERS, 1f, 1f)
                AltarCraftingManager.tryCrafting(level, altar)
            }
            altar.refresh()
        }
        val remainder = stack.copy()
        remainder.shrink(1)
        return remainder
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        return altar.handler.extractItem(slot, amount, simulate)
    }

    override fun getSlotLimit(slot: Int): Int {
        return altar.handler.getSlotLimit(slot)
    }

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        return altar.handler.isItemValid(slot, stack)
    }
}
