package com.lythen.maidindustry

import net.neoforged.fml.common.Mod
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod(MaidIndustry.MOD_ID)
object MaidIndustry {
    const val MOD_ID = "maidindustry"
    val LOGGER: Logger = LogManager.getLogger(MOD_ID)

    init {
        LOGGER.info("Maid Industry mod initializing...")
        // Register event buses here
    }
}
