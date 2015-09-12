package com.goodformentertainment.bc;

import static com.goodformentertainment.bc.Briarcraft.*;

import net.canarymod.config.Configuration;
import net.canarymod.plugin.Plugin;
import net.visualillusionsent.utils.PropertiesFile;

public class BriarcraftConfig {
    private static final String LOG_LEVEL = "log.level";

    private final PropertiesFile cfg;

    public BriarcraftConfig(final Plugin plugin) {
        cfg = Configuration.getPluginConfig(plugin(), "Briarcraft");
    }

    public String getLoggingLevel() {
        String level = null;
        if (cfg.containsKey(LOG_LEVEL)) {
            level = cfg.getString(LOG_LEVEL);
        }
        return level;
    }
}
