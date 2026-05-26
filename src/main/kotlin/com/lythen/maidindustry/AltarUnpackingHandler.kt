package com.lythen.maidindustry

import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityAltar
import com.simibubi.create.api.packager.unpacking.UnpackingHandler
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState

object AltarUnpackingHandler : UnpackingHandler {
    override fun unpack(
        level: Level,
        pos: BlockPos,
        state: BlockState,
        side: Direction,
        items: MutableList<ItemStack>,
        orderContext: PackageOrderWithCrafts?,
        simulate: Boolean
    ): Boolean {
        val te = level.getBlockEntity(pos) as? TileEntityAltar ?: return false

        // Only Torii gate (Red Wool) accepts packages
        if (te.storageState.block != Blocks.RED_WOOL) {
            return false
        }

        val posList = te.canPlaceItemPosList.data
        var hasItems = false
        for (p in posList) {
            val pillar = level.getBlockEntity(p) as? TileEntityAltar
            if (pillar != null && !pillar.handler.getStackInSlot(0).isEmpty) {
                hasItems = true
                break
            }
        }
        if (hasItems) return false

        val itemsToPlace = mutableListOf<ItemStack>()
        for (stack in items) {
            for (i in 0 until stack.count) {
                itemsToPlace.add(stack.copyWithCount(1))
            }
        }

        if (itemsToPlace.isEmpty() || itemsToPlace.size > 6) {
            return false
        }

        if (simulate) {
            for (i in items.indices) {
                items[i] = ItemStack.EMPTY
            }
            return true
        }

        for (i in itemsToPlace.indices) {
            val pillar = level.getBlockEntity(posList[i]) as? TileEntityAltar
            if (pillar != null) {
                pillar.handler.setStackInSlot(0, itemsToPlace[i].copyWithCount(1))
                pillar.refresh()
            }
        }

        level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.PLAYERS, 1f, 1f)
        AltarCraftingManager.tryCrafting(level, te)

        for (i in items.indices) {
            items[i] = ItemStack.EMPTY
        }

        return true
    }
}
