package com.velocitypowered.crossstitch;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrossStitch implements ModInitializer {
    private static final Logger LOGGER = LogManager.getLogger(CrossStitch.class);

    @Override
    public void onInitialize() {
        LOGGER.info("CrossStitch at your service, binding together your mods and your proxy!");
    }
}
