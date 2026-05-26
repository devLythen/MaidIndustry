package com.lythen.maidindustry

import com.github.tartaricacid.touhoulittlemaid.init.InitBlocks
import com.simibubi.create.api.registry.CreateRegistries
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.registries.DeferredRegister
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import java.util.function.Supplier

@Mod(MaidIndustry.MOD_ID)
object MaidIndustry {
    const val MOD_ID = "maidindustry"
    val LOGGER: Logger = LogManager.getLogger(MOD_ID)

    val ARM_INTERACTION_POINT_TYPES: DeferredRegister<ArmInteractionPointType> =
        DeferredRegister.create(CreateRegistries.ARM_INTERACTION_POINT_TYPE, MOD_ID)

    init {
        LOGGER.info("Maid Industry mod initializing...")
        ARM_INTERACTION_POINT_TYPES.register("altar", Supplier { AltarArmInteractionPointType() })
        ARM_INTERACTION_POINT_TYPES.register(MOD_BUS)

        MOD_BUS.addListener(this::registerCapabilities)
    }

    private fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            InitBlocks.ALTAR_TE.get()
        ) { te, _ ->
            AltarItemHandlerCapability(te)
        }
    }
}
