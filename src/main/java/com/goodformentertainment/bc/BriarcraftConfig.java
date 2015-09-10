package com.goodformentertainment.bc;

import static com.goodformentertainment.bc.Briarcraft.*;

import net.canarymod.config.Configuration;
import net.canarymod.plugin.Plugin;
import net.visualillusionsent.utils.PropertiesFile;

public class BriarcraftConfig {
    private final PropertiesFile cfg;

    public BriarcraftConfig(final Plugin plugin) {
        cfg = Configuration.getPluginConfig(plugin(), "Briarcraft");
    }

    public String getLoggingLevel() {
        String level = null;
        final String key = "log.level";
        if (cfg.containsKey(key)) {
            level = cfg.getString(key);
        }
        return level;
    }
}
