package com.lythen.maidindustry

import com.github.tartaricacid.touhoulittlemaid.init.InitRecipes
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityAltar
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityMaidBeacon
import com.github.tartaricacid.touhoulittlemaid.util.PosListData
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.level.Level

object AltarCraftingManager {
    fun tryCrafting(level: Level, altar: TileEntityAltar) {
        val arrayList = ArrayList<ItemStack>()
        val posList = altar.canPlaceItemPosList.data
        for (i in posList.indices) {
            val te = level.getBlockEntity(posList[i])
            if (te is TileEntityAltar) {
                arrayList.add(i, te.storageItem)
            }
        }
        if (arrayList.isEmpty()) return

        val craftingInput = CraftingInput.of(6, 1, arrayList)
        level.recipeManager.getRecipeFor(InitRecipes.ALTAR_CRAFTING.get(), craftingInput, level).ifPresent { recipe ->
            val powerCost = recipe.value().power
            val centrePos = getCentrePos(altar.blockPosList, altar.blockPos)
            
            var foundBeacon: TileEntityMaidBeacon? = null
            // Search for Maid Beacon within 8 blocks radius from centrePos
            for (x in -8..8) {
                for (y in -8..8) {
                    for (z in -8..8) {
                        val checkPos = centrePos.offset(x, y, z)
                        val checkTe = level.getBlockEntity(checkPos)
                        if (checkTe is TileEntityMaidBeacon && checkTe.storagePower >= powerCost) {
                            foundBeacon = checkTe
                            break
                        }
                    }
                    if (foundBeacon != null) break
                }
                if (foundBeacon != null) break
            }

            if (foundBeacon != null) {
                foundBeacon.storagePower -= powerCost
                if (level is ServerLevel) {
                    recipe.value().spawnOutputEntity(level, centrePos.above(2), arrayList)
                }
                removeAllAltarItem(level, altar)
                spawnParticleInCentre(level, centrePos)
                level.playSound(null, centrePos, InitSounds.ALTAR_CRAFT.get(), SoundSource.VOICE, 1.0f, 1.0f)
            } else if (powerCost <= 0.0f) {
                // If power cost is 0, we can still craft it without a beacon
                if (level is ServerLevel) {
                    recipe.value().spawnOutputEntity(level, centrePos.above(2), arrayList)
                }
                removeAllAltarItem(level, altar)
                spawnParticleInCentre(level, centrePos)
                level.playSound(null, centrePos, InitSounds.ALTAR_CRAFT.get(), SoundSource.VOICE, 1.0f, 1.0f)
            }
        }
    }

    private fun getCentrePos(posList: PosListData, posClick: BlockPos): BlockPos {
        var x = 0
        val y = posClick.y - 2
        var z = 0
        for (pos in posList.data) {
            if (pos.y == y) {
                x += pos.x
                z += pos.z
            }
        }
        return BlockPos(x / 8, y, z / 8)
    }

    private fun removeAllAltarItem(level: Level, altar: TileEntityAltar) {
        for (pos in altar.canPlaceItemPosList.data) {
            val te = level.getBlockEntity(pos)
            if (te is TileEntityAltar) {
                te.handler.setStackInSlot(0, ItemStack.EMPTY)
                te.refresh()
                spawnParticleInCentre(level, te.blockPos)
            }
        }
    }

    private fun spawnParticleInCentre(level: Level, centrePos: BlockPos) {
        val width = 1
        val height = 1
        for (i in 0..4) {
            val xSpeed = level.random.nextGaussian() * 0.02
            val ySpeed = level.random.nextGaussian() * 0.02
            val zSpeed = level.random.nextGaussian() * 0.02
            level.addParticle(
                ParticleTypes.CLOUD,
                centrePos.x + level.random.nextFloat() * width * 2 - width - xSpeed * 10,
                centrePos.y + level.random.nextFloat() * height - ySpeed * 10,
                centrePos.z + level.random.nextFloat() * width * 2 - width - zSpeed * 10,
                xSpeed, ySpeed, zSpeed
            )
            level.addParticle(
                ParticleTypes.SMOKE,
                centrePos.x + level.random.nextFloat() * width * 2 - width - xSpeed * 10,
                centrePos.y + level.random.nextFloat() * height - ySpeed * 10,
                centrePos.z + level.random.nextFloat() * width * 2 - width - zSpeed * 10,
                xSpeed, ySpeed, zSpeed
            )
        }
    }
}
