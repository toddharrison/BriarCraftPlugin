package com.goodformentertainment.bc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;

import com.goodformentertainment.bc.util.JarUtil;

import net.canarymod.Canary;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.commandsys.CommandListener;
import net.canarymod.logger.Logman;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginListener;

public class Briarcraft extends Plugin {
    private static Logman LOG;
    private static Plugin plugin;

    public static Logman log() {
        return LOG;
    }

    public static Plugin plugin() {
        return plugin;
    }

    private BriarcraftConfig config;
    private final List<Module> modules;

    public Briarcraft() {
        Briarcraft.LOG = getLogman();
        Briarcraft.plugin = this;

        // Create Modules
        modules = new ArrayList<Module>();
        // modules.add(new FrontierModule());

        try {
            // Copy Logging configuration file
            JarUtil.exportResource(this, "Briarcraft.cfg", new File("config/Briarcraft"));

            for (final Module module : modules) {
                // Copy module configuration file
                final String name = module.getConfigName();
                if (name != null) {
                    JarUtil.exportResource(this, name + ".cfg", new File("config/Briarcraft"));
                }

                // Initialize Module
                module.init();
            }
        } catch (final IOException e) {
            log().warn("Failed to create the default configuration files.", e);
        }
    }

    @Override
    public boolean enable() {
        boolean success = true;

        config = new BriarcraftConfig(this);
        setLoggingLevel(config.getLoggingLevel());

        log().info("Enabling " + getName() + " Version " + getVersion());
        log().info("Authored by " + getAuthor());

        try {
            // Register Module listeners
            for (final Module module : modules) {
                final Collection<PluginListener> listeners = module.getPluginListeners();
                if (listeners != null) {
                    for (final PluginListener listener : listeners) {
                        Canary.hooks().registerListener(listener, this);
                    }
                }
            }

            // Register Module commands
            for (final Module module : modules) {
                final Collection<CommandListener> listeners = module.getCommandListeners();
                if (listeners != null) {
                    for (final CommandListener commandListener : listeners) {
                        Canary.commands().registerCommands(commandListener, this, false);
                    }
                }
            }

            // Enable Modules
            for (final Module module : modules) {
                success = module.enable();
                if (!success) {
                    log().error("Error starting " + module.getClass().getName());
                    break;
                }
            }
        } catch (final CommandDependencyException e) {
            log().error("Error registering commands", e);
            success = false;
        } catch (final Exception e) {
            log().error("Error starting Briarcraft", e);
            success = false;
        }

        return success;
    }

    @Override
    public void disable() {
        log().info("Disabling " + getName());
        Canary.commands().unregisterCommands(this);
        Canary.hooks().unregisterPluginListeners(this);

        config = null;

        // Disable Modules
        for (final Module module : modules) {
            module.disable();
        }
    }

    private void setLoggingLevel(final String level) {
        if (level != null) {
            final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            final org.apache.logging.log4j.core.config.Configuration config = ctx
                    .getConfiguration();
            final LoggerConfig loggerConfig = config.getLoggerConfig(log().getName());
            loggerConfig.setLevel(Level.toLevel(level));
            ctx.updateLoggers();
        }
    }
}
