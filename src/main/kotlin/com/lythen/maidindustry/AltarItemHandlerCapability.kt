package com.lythen.maidindustry

import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityAltar
import com.simibubi.create.content.logistics.box.PackageItem
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Blocks
import net.neoforged.neoforge.items.IItemHandler

class AltarItemHandlerCapability(private val altar: TileEntityAltar) : IItemHandler {
    override fun getSlots(): Int {
        return altar.handler.slots
    }

    override fun getStackInSlot(slot: Int): ItemStack {
        return altar.handler.getStackInSlot(slot)
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (stack.item is PackageItem) {
            // Package can only be inserted into Torii gate (Red Wool)
            if (altar.storageState.block != Blocks.RED_WOOL) {
                return stack
            }

            // Check if pillars are empty
            val posList = altar.canPlaceItemPosList.data
            var hasItems = false
            for (pos in posList) {
                val te = altar.level?.getBlockEntity(pos) as? TileEntityAltar
                if (te != null && !te.handler.getStackInSlot(0).isEmpty) {
                    hasItems = true
                    break
                }
            }
            if (hasItems) return stack

            val contents = PackageItem.getContents(stack)
            val itemsToPlace = mutableListOf<ItemStack>()
            var totalItems = 0
            for (i in 0 until contents.slots) {
                val inSlot = contents.getStackInSlot(i)
                if (!inSlot.isEmpty) {
                    for (count in 0 until inSlot.count) {
                        itemsToPlace.add(inSlot.copyWithCount(1))
                        totalItems++
                    }
                }
            }

            if (totalItems == 0 || totalItems > 6) {
                return stack
            }

            if (!simulate) {
                for (i in itemsToPlace.indices) {
                    val te = altar.level?.getBlockEntity(posList[i]) as? TileEntityAltar
                    if (te != null) {
                        te.handler.setStackInSlot(0, itemsToPlace[i].copyWithCount(1))
                        te.refresh()
                    }
                }

                val level = altar.level
                if (level != null) {
                    level.playSound(null, altar.blockPos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.PLAYERS, 1f, 1f)
                    AltarCraftingManager.tryCrafting(level, altar)
                }
            }

            val remainder = stack.copy()
            remainder.shrink(1)
            return remainder
        } else {
            // Normal single items can only be inserted into pillars
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
