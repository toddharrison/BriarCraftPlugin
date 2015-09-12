package com.goodformentertainment.bc.frontier;

import com.goodformentertainment.bc.Module;

import net.canarymod.commandsys.CommandListener;
import net.canarymod.plugin.Plugin;

public class FrontierModule extends Module {
    private FrontierManager manager;
    private FrontierConfig config;
    private FrontierCommand command;

    @Override
    public String getConfigName() {
        return "Frontier";
    }

    @Override
    public void createConfig(final Plugin plugin) {
        config = new FrontierConfig(plugin);
    }

    @Override
    public boolean enable() {
        manager = new FrontierManager(config);
        command = new FrontierCommand(manager);

        manager.resetAllWildernesses();

        return true;
    }

    @Override
    public void disable() {
        config = null;
        manager = null;
        command = null;
    }

    @Override
    public CommandListener[] getCommandListeners() {
        return new CommandListener[] { command };
    }
}
