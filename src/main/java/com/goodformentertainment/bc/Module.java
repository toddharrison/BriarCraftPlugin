package com.goodformentertainment.bc;

import net.canarymod.commandsys.CommandListener;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginListener;

public abstract class Module {
    public void init() {
    }

    public String getConfigName() {
        return null;
    }

    public boolean enable() {
        return true;
    }

    public void disable() {
    }

    public void createConfig(final Plugin plugin) {
    }

    public PluginListener[] getPluginListeners() {
        return null;
    }

    public CommandListener[] getCommandListeners() {
        return null;
    }

    public String getNavigation() {
        return null;
    }
}
