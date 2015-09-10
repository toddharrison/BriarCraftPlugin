package com.goodformentertainment.bc;

import java.util.Collection;

import net.canarymod.commandsys.CommandListener;
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

    public Collection<PluginListener> getPluginListeners() {
        return null;
    }

    public Collection<CommandListener> getCommandListeners() {
        return null;
    }
}
