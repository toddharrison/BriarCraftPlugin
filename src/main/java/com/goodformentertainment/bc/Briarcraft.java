package com.goodformentertainment.bc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;

import com.goodformentertainment.bc.frontier.FrontierModule;
import com.goodformentertainment.bc.nav.GoCommand;
import com.goodformentertainment.bc.security.BookHackModule;
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

        modules = new ArrayList<Module>();

        try {
            // Copy Logging configuration file
            JarUtil.exportResource(this, "Briarcraft.cfg", new File("config/Briarcraft"));

            modules.add(new BookHackModule());
            modules.add(new FrontierModule());

            for (final Module module : modules) {
                // Copy module configuration file
                final String name = module.getConfigName();
                if (name != null) {
                    JarUtil.exportResource(this, "Briarcraft." + name + ".cfg",
                            new File("config/Briarcraft"));
                }

                // Initialize Module
                module.init();
                log().info("Initialized module " + module.getClass().getName());
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
            // Register Go commands
            final GoCommand goCommand = new GoCommand();
            Canary.commands().registerCommands(goCommand, this, false);

            // For each module
            for (final Module module : modules) {
                // Load config
                module.createConfig(this);

                // Enable Module
                success = module.enable();
                if (success) {
                    // Register Module listeners
                    final PluginListener[] pluginListeners = module.getPluginListeners();
                    if (pluginListeners != null) {
                        for (final PluginListener pluginListener : pluginListeners) {
                            // if (pluginListener != null) {
                            Canary.hooks().registerListener(pluginListener, this);
                            // }
                        }
                    }

                    // Register Module commands
                    final CommandListener[] commandListeners = module.getCommandListeners();
                    if (commandListeners != null) {
                        for (final CommandListener commandListener : commandListeners) {
                            // if (commandListener != null) {
                            Canary.commands().registerCommands(commandListener, this, false);
                            // }
                        }
                    }

                    // // Register Module navigation
                    // final String nav = module.getNavigation();
                    // if (nav != null) {
                    // success = goCommand.add(nav);
                    // if (!success) {
                    // log().error("Error registering nav " + nav);
                    // break;
                    // }
                    // }
                } else {
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
        System.out.println("Disabling " + getName());
        Canary.commands().unregisterCommands(this);
        Canary.hooks().unregisterPluginListeners(this);

        config = null;

        // Disable Modules
        for (final Module module : modules) {
            module.disable();
            log().info("Disabled module " + module.getClass().getName());
            System.out.println("Disabled module " + module.getClass().getName());
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
