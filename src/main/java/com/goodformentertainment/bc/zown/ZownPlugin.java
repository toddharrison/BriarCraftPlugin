package com.goodformentertainment.bc.zown;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import com.goodformentertainment.bc.util.JarUtil;
import com.goodformentertainment.bc.zown.api.ITemplateManager;
import com.goodformentertainment.bc.zown.api.IZownManager;
import com.goodformentertainment.bc.zown.api.impl.TemplateManager;
import com.goodformentertainment.bc.zown.api.impl.ZownManager;
import com.goodformentertainment.bc.zown.command.ZownCommand;
import com.goodformentertainment.bc.zown.dao.DataManager;
import com.goodformentertainment.bc.zown.listener.CommandListener;
import com.goodformentertainment.bc.zown.listener.EntityListener;
import com.goodformentertainment.bc.zown.listener.ModifyWorldListener;
import com.goodformentertainment.bc.zown.listener.PlayerListener;

import net.canarymod.Canary;
import net.canarymod.api.world.World;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.system.LoadWorldHook;
import net.canarymod.hook.system.UnloadWorldHook;
import net.canarymod.logger.Logman;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginListener;
import net.canarymod.plugin.Priority;

public class ZownPlugin extends Plugin implements PluginListener {
    public static Logman LOG;

    private static ITemplateManager templateManager;
    private static IZownManager zownManager;

    /**
     * Get the TemplateManager from the ZownPlugin.
     *
     * @return The TemplateManager.
     */
    public static ITemplateManager getTemplateManager() {
        return templateManager;
    }

    /**
     * Get the ZownManager from the ZownPlugin.
     *
     * @return The ZownManager.
     */
    public static IZownManager getZownManager() {
        return zownManager;
    }

    private ZownConfiguration config;
    private ZownCommand zownCommand;

    public ZownPlugin() {
        ZownPlugin.LOG = getLogman();
    }

    @Override
    public boolean enable() {
        boolean success = true;

        try {
            JarUtil.exportResource(this, "Zown.cfg", new File("config/Zown"));
        } catch (final IOException e) {
            LOG.warn("Failed to create the default configuration file.", e);
        }

        try {
            JarUtil.exportResource(this, "zown_template.xml", new File("db"));
        } catch (final IOException e) {
            LOG.warn("Failed to create the default template database xml file.", e);
        }

        config = new ZownConfiguration(this);
        setLoggingLevel(config.getLoggingLevel());

        LOG.info("Enabling " + getName() + " Version " + getVersion());
        LOG.info("Authored by " + getAuthor());

        final DataManager dataManager = new DataManager();
        templateManager = new TemplateManager(dataManager);
        zownManager = new ZownManager(dataManager, templateManager);

        // Load the zowns for worlds already loaded on the server
        for (final World world : Canary.getServer().getWorldManager().getAllWorlds()) {
            zownManager.loadZowns(world);
        }

        Canary.hooks().registerListener(this, this);
        Canary.hooks().registerListener(new CommandListener(zownManager), this);
        Canary.hooks().registerListener(new ModifyWorldListener(zownManager), this);
        Canary.hooks().registerListener(new EntityListener(zownManager), this);
        Canary.hooks().registerListener(new PlayerListener(zownManager), this);

        zownCommand = new ZownCommand(config, templateManager, zownManager);

        try {
            Canary.commands().registerCommands(zownCommand, this, false);
        } catch (final CommandDependencyException e) {
            LOG.error("Error registering commands: ", e);
            success = false;
        }

        return success;
    }

    @Override
    public void disable() {
        LOG.info("Disabling " + getName());

        templateManager = null;
        zownManager = null;

        Canary.commands().unregisterCommands(this);
        Canary.hooks().unregisterPluginListeners(this);
    }

    @HookHandler(priority = Priority.PASSIVE)
    public void onWorldLoad(final LoadWorldHook hook) {
        final World world = hook.getWorld();
        if (zownManager.isLoaded(world)) {
            LOG.debug("Zowns are already loaded for world " + world.getFqName() + ", skipping.");
        } else {
            zownManager.loadZowns(world);
        }
    }

    @HookHandler(priority = Priority.PASSIVE)
    public void onWorldUnload(final UnloadWorldHook hook) {
        zownManager.unloadZowns(hook.getWorld());
    }

    private void setLoggingLevel(final String level) {
        if (level != null) {
            final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            final Configuration config = ctx.getConfiguration();
            final LoggerConfig loggerConfig = config.getLoggerConfig(LOG.getName());
            loggerConfig.setLevel(Level.toLevel(level));
            ctx.updateLoggers();
        }
    }
}
