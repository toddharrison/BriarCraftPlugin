package com.goodformentertainment.bc.zown.command;

import static com.goodformentertainment.bc.zown.ZownMessenger.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import com.goodformentertainment.bc.util.Tree;
import com.goodformentertainment.bc.util.region.Point;
import com.goodformentertainment.bc.zown.Flag;
import com.goodformentertainment.bc.zown.ZownConfiguration;
import com.goodformentertainment.bc.zown.api.IConfiguration;
import com.goodformentertainment.bc.zown.api.ITemplate;
import com.goodformentertainment.bc.zown.api.ITemplateManager;
import com.goodformentertainment.bc.zown.api.IZown;
import com.goodformentertainment.bc.zown.api.IZownManager;
import com.goodformentertainment.bc.zown.api.impl.MinecraftMapper;

import net.canarymod.Canary;
import net.canarymod.api.entity.Entity;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.factory.PacketFactory;
import net.canarymod.api.world.World;
import net.canarymod.api.world.WorldManager;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandListener;
import net.visualillusionsent.utils.TaskManager;

// TODO This class is a beast, it needs refactoring
public class ZownCommand implements CommandListener {
    private final ZownConfiguration config;
    private final WorldManager worldManager;
    private final IZownManager zownManager;
    private final ITemplateManager templateManager;

    public ZownCommand(final ZownConfiguration config, final ITemplateManager templateManager,
            final IZownManager zownManager) {
        worldManager = Canary.getServer().getWorldManager();
        this.config = config;
        this.zownManager = zownManager;
        this.templateManager = templateManager;
    }

    @Command(aliases = {
            "apply" }, parent = "template", description = "zown template apply", permissions = {
                    "zown.template.apply" }, toolTip = "/zown template apply <zown> <template>")
    public void applyTemplateToZown(final MessageReceiver caller, final String[] parameters) {
        World world = null;
        String zown = null;
        String template = null;

        if (caller instanceof Player) {
            final Player player = caller.asPlayer();
            switch (parameters.length) {
                case 3:
                    world = player.getWorld();
                    zown = parameters[1];
                    template = parameters[2];
                    break;
                default:
                    sendMessage(caller, "Usage: /zown template apply <zown> <template>");
            }
        } else {
            switch (parameters.length) {
                case 4:
                    world = worldManager.getWorld(parameters[1], false);
                    zown = parameters[2];
                    template = parameters[3];
                    break;
                default:
                    sendMessage(caller, "Usage: /zown template apply <world> <zown> <template>");
            }
        }

        if (world != null && zown != null && template != null) {
            final ITemplate t = templateManager.getTemplate(template);
            if (t != null) {
                if (zownManager.applyTemplate(world, zown, t)) {
                    sendMessage(caller,
                            "Applied template '" + template + "' to zown '" + zown + "'.");
                } else {
                    sendMessage(caller, "Failed to apply template '" + template + "'.");
                }
            } else {
                sendMessage(caller, "No template '" + template + "' exists.");
            }
        }
    }

    @Command(aliases = {
            "copy" }, parent = "template", description = "zown template copy", permissions = {
                    "zown.template.copy" }, toolTip = "/zown template copy <fromTemplate> <template>")
    public void copyTemplate(final MessageReceiver caller, final String[] parameters) {
        if (parameters.length != 3) {
            sendMessage(caller, "Usage: /zown template copy <fromTemplate> <template>");
        } else {
            final ITemplate template = templateManager.copyTemplate(parameters[1], parameters[2]);
            if (template == null) {
                sendMessage(caller, "Template '" + parameters[2]
                        + "' already exists or from template '" + parameters[1] + "' does not.");
            } else {
                sendMessage(caller,
                        "Copied template '" + parameters[1] + "' into '" + parameters[2] + "'.");
            }
        }
    }

    @Command(aliases = {
            "create" }, parent = "template", description = "zown template create", permissions = {
                    "zown.template.create" }, toolTip = "/zown template create <template>")
    public void createTemplate(final MessageReceiver caller, final String[] parameters) {
        if (parameters.length != 2) {
            sendMessage(caller, "Usage: /zown template create <template>");
        } else {
            final ITemplate template = templateManager.createTemplate(parameters[1]);
            if (template == null) {
                sendMessage(caller, "Template '" + parameters[1] + "' already exists.");
            } else {
                sendMessage(caller, "Created template '" + parameters[1] + "'.");
            }
        }
    }

    @Command(aliases = { "create" }, parent = "zown", description = "zown create", permissions = {
            "zown.zown.manage.create" }, toolTip = "/zown create <zown> [template] <x1 y1 z1> <x2 y2 z2>")
    public void createZown(final MessageReceiver caller, final String[] parameters) {
        final ParameterTokenizer pTokens = new ParameterTokenizer(parameters);
        World world = null;
        Player player = null;
        String zown = null;
        ITemplate template = null;
        Point p1 = null;
        Point p2 = null;

        if (caller instanceof Player) {
            player = caller.asPlayer();
            if (player.isOperator()) {
                switch (parameters.length) {
                    case 8:
                        world = player.getWorld();
                        zown = pTokens.readString();
                        p1 = pTokens.readPoint();
                        p2 = pTokens.readPoint();
                        break;
                    case 9:
                        world = player.getWorld();
                        zown = pTokens.readString();
                        template = pTokens.readTemplate(templateManager);
                        p1 = pTokens.readPoint();
                        p2 = pTokens.readPoint();
                        break;
                    default:
                        sendMessage(caller,
                                "Usage: /zown create <zown> [template] <x1 y1 z1> <x2 y2 z2>");
                }
            } else {
                switch (parameters.length) {
                    case 8:
                        world = player.getWorld();
                        zown = pTokens.readString();
                        template = templateManager
                                .getTemplate(config.getDefaultWorldTemplate(world));
                        p1 = pTokens.readPoint();
                        p2 = pTokens.readPoint();
                        break;
                    default:
                        sendMessage(caller, "Usage: /zown create <zown> <x1 y1 z1> <x2 y2 z2>");
                }
            }
        } else {
            switch (parameters.length) {
                case 9:
                    world = pTokens.readWorld(worldManager);
                    zown = pTokens.readString();
                    p1 = pTokens.readPoint();
                    p2 = pTokens.readPoint();
                    break;
                case 10:
                    world = pTokens.readWorld(worldManager);
                    zown = pTokens.readString();
                    template = pTokens.readTemplate(templateManager);
                    p1 = pTokens.readPoint();
                    p2 = pTokens.readPoint();
                    break;
                default:
                    sendMessage(caller,
                            "Usage: /zown create <world> <zown> [template] <x1 y1 z1> <x2 y2 z2>");
            }
        }

        if (world != null && zown != null && p1 != null && p2 != null) {
            final Tree<? extends IZown> zownTree = zownManager.createZown(world, zown, template, p1,
                    p2, player);
            if (zownTree == null) {
                sendMessage(caller, "Failed to create zown '" + zown + "'.");
            } else {
                sendMessage(caller, "Created zown '" + zown + "'.");
            }
        }
    }

    @Command(aliases = {
            "delete" }, parent = "template", description = "zown template delete", permissions = {
                    "zown.template.delete" }, toolTip = "/zown template delete <template>")
    public void deleteTemplate(final MessageReceiver caller, final String[] parameters) {
        if (parameters.length != 2) {
            sendMessage(caller, "Usage: /zown template delete <template>");
        } else {
            if (templateManager.removeTemplate(parameters[1])) {
                sendMessage(caller, "Deleted template '" + parameters[1] + "'.");
            } else {
                sendMessage(caller, "Template '" + parameters[1] + "' doesn't exist.");
            }
        }
    }

    @Command(aliases = { "delete" }, parent = "zown", description = "zown delete", permissions = {
            "zown.zown.manage.delete" }, toolTip = "/zown delete <zown>")
    public void deleteZown(final MessageReceiver caller, final String[] parameters) {
        World world = null;
        Player player = null;
        String zown = null;

        if (caller instanceof Player) {
            player = caller.asPlayer();
            switch (parameters.length) {
                case 2:
                    world = player.getWorld();
                    zown = parameters[1];
                    break;
                default:
                    sendMessage(caller, "Usage: /zown delete <zown>");
            }
        } else {
            switch (parameters.length) {
                case 3:
                    world = worldManager.getWorld(parameters[1], false);
                    zown = parameters[2];
                    break;
                default:
                    sendMessage(caller, "Usage: /zown delete <world> <zown>");
            }
        }

        if (world != null && zown != null) {
            if (zownManager.removeZown(world, zown, player)) {
                sendMessage(caller, "Deleted zown '" + zown + "'.");
            } else {
                sendMessage(caller, "Failed to delete zown '" + zown + "'.");
            }
        }
    }

    @Command(aliases = {
            "editpoints" }, parent = "zown", description = "zown editpoints", permissions = {
                    "zown.zown.manage.editpoints" }, toolTip = "/zown editpoints <zown> <x1 y1 z1> <x2 y2 z2>")
    public void editZownPoints(final MessageReceiver caller, final String[] parameters) {
        final ParameterTokenizer pTokens = new ParameterTokenizer(parameters);
        World world = null;
        Player player = null;
        String zown = null;
        Point p1 = null;
        Point p2 = null;

        if (caller instanceof Player) {
            player = caller.asPlayer();
            switch (parameters.length) {
                case 8:
                    world = player.getWorld();
                    zown = pTokens.readString();
                    p1 = pTokens.readPoint();
                    p2 = pTokens.readPoint();
                    break;
                default:
                    sendMessage(caller, "Usage: /zown editpoints <zown> <x1 y1 z1> <x2 y2 z2>");
            }
        } else {
            switch (parameters.length) {
                case 9:
                    world = pTokens.readWorld(worldManager);
                    zown = pTokens.readString();
                    p1 = pTokens.readPoint();
                    p2 = pTokens.readPoint();
                    break;
                default:
                    sendMessage(caller,
                            "Usage: /zown editpoints <world> <zown> <x1 y1 z1> <x2 y2 z2>");
            }
        }

        if (world != null && zown != null && p1 != null && p2 != null) {
            final Tree<? extends IZown> zownTree = zownManager.getZown(world, zown);
            if (zownTree != null) {
                if (zownManager.resizeZown(world, zown, p1, p2, player)) {
                    sendMessage(caller, "Resized zown '" + zown + "'.");
                } else {
                    sendMessage(caller, "Failed to resize zown '" + zown + "'.");
                }
            } else {
                sendMessage(caller, "No zown '" + zown + "' exists.");
            }
        }
    }

    @Command(aliases = { "expand" }, parent = "zown", description = "zown expand", permissions = {
            "zown.zown.manage.expand" }, toolTip = "/zown expand <zown>")
    public void expandZown(final MessageReceiver caller, final String[] parameters) {
        World world = null;
        Player player = null;
        String zown = null;

        if (caller instanceof Player) {
            player = caller.asPlayer();
            switch (parameters.length) {
                case 2:
                    world = player.getWorld();
                    zown = parameters[1];
                    break;
                default:
                    sendMessage(caller, "Usage: /zown expand <zown>");
            }
        } else {
            switch (parameters.length) {
                case 3:
                    world = worldManager.getWorld(parameters[1], false);
                    zown = parameters[2];
                    break;
                default:
                    sendMessage(caller, "Usage: /zown expand <world> <zown>");
            }
        }

        if (world != null && zown != null) {
            final Tree<? extends IZown> zownTree = zownManager.getZown(world, zown);
            if (zownTree != null) {
                final Point p1 = zownTree.getData().getMinPoint().clone();
                p1.y = 0;
                final Point p2 = zownTree.getData().getMaxPoint().clone();
                p2.y = 255;
                if (zownManager.resizeZown(world, zown, p1, p2, player)) {
                    sendMessage(caller, "Expanded zown '" + zown + "'.");
                } else {
                    sendMessage(caller, "Failed to expand zown '" + zown + "'.");
                }
            } else {
                sendMessage(caller, "No zown '" + zown + "' exists.");
            }
        }
    }

    @Command(aliases = {
            "flag" }, parent = "template", description = "zown template flag", permissions = {
                    "zown.template.flag" }, toolTip = "/zown template flag <template> <flag>:<ALLOW | DENY>...")
    public void flagTemplate(final MessageReceiver caller, final String[] parameters) {
        if (parameters.length < 3) {
            sendMessage(caller, "Usage: /zown template flag <template> <flag>:<ALLOW | DENY>...");
        } else {
            final String name = parameters[1];
            final ITemplate template = templateManager.getTemplate(name);
            if (template == null) {
                sendMessage(caller, "Template '" + parameters[1] + "' doesn't exist.");
            } else {
                for (int i = 2; i < parameters.length; i++) {
                    final String[] flag = parameters[i].split(":");
                    if (flag.length == 2) {
                        final IConfiguration config = template.getConfiguration();
                        config.setFlag(flag[0], "allow".equalsIgnoreCase(flag[1]));
                    } else {
                        sendMessage(caller, "Bad flag: " + parameters[i]);
                    }
                }
                templateManager.saveTemplateConfiguration(name);
                sendMessage(caller, "Added flags to template '" + parameters[1] + "'.");
            }
        }
    }

    @Command(aliases = {
            "flagaccess" }, parent = "template", description = "zown template flagaccess", permissions = {
                    "zown.template.flagaccess" }, toolTip = "/zown template flagaccess <template> <add | remove> <flag>")
    public void flagTemplateAccess(final MessageReceiver caller, final String[] parameters) {
        if (parameters.length != 4) {
            sendMessage(caller,
                    "Usage: /zown template flagaccess <template> <add | remove> <flag>");
        } else {
            final String name = parameters[1];
            final ITemplate template = templateManager.getTemplate(name);
            if (template == null) {
                sendMessage(caller, "Template '" + parameters[1] + "' doesn't exist.");
            } else {
                if ("add".equalsIgnoreCase(parameters[2])) {
                    final String flag = parameters[3];
                    final IConfiguration config = template.getConfiguration();
                    if (config.addOwnerPermission(flag)) {
                        sendMessage(caller, "Added owner permission '" + flag + "' to template '"
                                + parameters[1] + "'.");
                        templateManager.saveTemplateConfiguration(name);
                    } else {
                        sendMessage(caller,
                                "Owner permission '" + flag + "' already exists on template.");
                    }
                } else if ("remove".equalsIgnoreCase(parameters[2])) {
                    final String flag = parameters[3];
                    final IConfiguration config = template.getConfiguration();
                    if (config.removeOwnerPermission(flag)) {
                        sendMessage(caller, "Removed owner permission '" + flag
                                + "' from template '" + parameters[1] + "'.");
                        templateManager.saveTemplateConfiguration(name);
                    } else {
                        sendMessage(caller,
                                "Owner permission '" + flag + "' does not exist on template.");
                    }
                } else {
                    sendMessage(caller,
                            "Usage: /zown template flagaccess <template> <add | remove> <flag>");
                }
            }
        }
    }

    @Command(aliases = { "flag" }, parent = "zown", description = "zown flag", permissions = {
            "zown.zown.flag" }, toolTip = "/zown flag <zown> <flag>:<ALLOW | DENY>...")
    public void flagZown(final MessageReceiver caller, final String[] parameters) {
        World world = null;
        String zown = null;
        int index = 0;

        Player player = null;

        if (caller instanceof Player) {
            player = caller.asPlayer();
            if (parameters.length >= 3) {
                world = player.getWorld();
                zown = parameters[1];
                index = 2;
            } else {
                sendMessage(caller, "Usage: /zown flag <zown> <flag>:<ALLOW | DENY>...");
            }
        } else {
            if (parameters.length >= 4) {
                world = worldManager.getWorld(parameters[1], false);
                zown = parameters[2];
                index = 3;
            } else {
                sendMessage(caller, "Usage: /zown flag <world> <zown> <flag>:<ALLOW | DENY>...");
            }
        }

        if (world != null && zown != null) {
            final Tree<? extends IZown> zownTree = zownManager.getZown(world, zown);
            if (zownTree == null) {
                sendMessage(caller, "Zown '" + zown + "' doesn't exist.");
            } else {

                if (!zownTree.getData().overridesConfiguration()) {
                    // TODO put in zownManager
                    zownTree.getData().setOverridesConfiguration(true);
                    zownManager.saveZownConfiguration(world, zown);
                }

                boolean changed = false;
                final IConfiguration config = zownTree.getData().getConfiguration();
                for (int i = index; i < parameters.length; i++) {
                    final String[] flag = parameters[i].split(":");
                    if (flag.length == 2) {
                        if (player != null) {
                            if (player.isOperator()
                                    || player.safeHasPermission("zown.admin.flag")) {
                                config.setFlag(flag[0], "allow".equalsIgnoreCase(flag[1])
                                        || "true".equalsIgnoreCase(flag[1]));
                                changed = true;
                            } else {
                                if (zownTree.getData().isOwner(player) && zownTree.getData()
                                        .getConfiguration().hasOwnerPermission(flag[0])) {
                                    config.setFlag(flag[0], "allow".equalsIgnoreCase(flag[1])
                                            || "true".equalsIgnoreCase(flag[1]));
                                    changed = true;
                                }
                            }
                        } else {
                            config.setFlag(flag[0], "allow".equalsIgnoreCase(flag[1]));
                            changed = true;
                        }
                    } else {
                        sendMessage(caller, "Bad flag: " + parameters[i]);
                    }
                }

                if (changed) {
                    zownManager.saveZownConfiguration(world, zown);
                    sendMessage(caller, "Added flags to zown '" + zown + "'.");
                } else {
                    sendMessage(caller, "No flags set in zown '" + zown + "'.");
                }
            }
        }
    }

    @Command(aliases = {
            "flagaccess" }, parent = "zown", description = "zown flagaccess", permissions = {
                    "zown.admin.flagaccess" }, toolTip = "/zown flagaccess <zown> <add | remove> <flag>")
    public void flagZownAccess(final MessageReceiver caller, final String[] parameters) {
        World world = null;
        String zown = null;
        String action = null;
        String flag = null;

        if (caller instanceof Player) {
            final Player player = caller.asPlayer();
            switch (parameters.length) {
                case 4:
                    world = player.getWorld();
                    zown = parameters[1];
                    action = parameters[2];
                    flag = parameters[3];
                    break;
                default:
                    sendMessage(caller, "Usage: /zown flag access <zown> <add | remove> <flag>");
            }
        } else {
            switch (parameters.length) {
                case 5:
                    world = worldManager.getWorld(parameters[1], false);
                    zown = parameters[2];
                    action = parameters[3];
                    flag = parameters[4];
                    break;
                default:
                    sendMessage(caller,
                            "Usage: /zown flag access <world> <zown> <add | remove> <flag>");
            }
        }

        if (world != null && zown != null && action != null && flag != null) {
            final Tree<? extends IZown> zownTree = zownManager.getZown(world, zown);
            if (zownTree == null) {
                sendMessage(caller, "No zown '" + zown + "' exists.");
            } else {
                if ("add".equalsIgnoreCase(action)) {

                    if (!zownTree.getData().overridesConfiguration()) {
                        // TODO put in zownManager
                        zownTree.getData().setOverridesConfiguration(true);
                        zownManager.saveZownConfiguration(world, zown);
                    }

                    if (zownTree.getData().getConfiguration().addOwnerPermission(flag)) {
                        sendMessage(caller,
                                "Added owner permission '" + flag + "' to zown '" + zown + "'.");
                        zownManager.saveZownConfiguration(world, zown);
                    } else {
                        sendMessage(caller,
                                "Owner permission '" + flag + "' already exists on zown.");
                    }
                } else if ("remove".equalsIgnoreCase(action)) {
                    if (zownTree.getData().overridesConfiguration()) {
                        if (zownTree.getData().getConfiguration().removeOwnerPermission(flag)) {
                            sendMessage(caller, "Removed owner permission '" + flag
                                    + "' from zown '" + zown + "'.");
                            zownManager.saveZownConfiguration(world, zown);
                        } else {
                            sendMessage(caller,
                                    "Owner permission '" + flag + "' does not exist on zown.");
                        }
                    } else {
                        sendMessage(caller, "Zown '" + zown + "' inherits from template '"
                                + zownTree.getData().getTemplate().getName() + "'.");
                    }
                } else {
                    sendMessage(caller,
                            "Unrecognized action '" + action + "' must be <add | remove>.");
                }
            }
        }
    }

    @Command(aliases = {
            "list" }, parent = "template", description = "zown template list", permissions = {
                    "zown.template.list" }, toolTip = "/zown template list")
    public void listTemplates(final MessageReceiver caller, final String[] parameters) {
        if (parameters.length > 1) {
            sendMessage(caller, "Usage: /zown template list");
        } else {
            final Collection<? extends ITemplate> templates = templateManager.getTemplates();
            if (templates.isEmpty()) {
                sendMessage(caller, "No templates.");
            } else {
                for (final ITemplate template : templates) {
                    sendMessage(caller, template.getName());
                }
            }
        }
    }

    @Command(aliases = { "list" }, parent = "zown", description = "zown list", permissions = {
            "zown.zown.list" }, toolTip = "/zown list")
    public void listZowns(final MessageReceiver caller, final String[] parameters) {
        World world = null;

        if (caller instanceof Player) {
            final Player player = caller.asPlayer();
            switch (parameters.length) {
                case 1:
                    world = player.getWorld();
                    break;
                default:
                    sendMessage(caller, "Usage: /zown list");
            }
        } else {
            switch (parameters.length) {
                case 2:
                    world = worldManager.getWorld(parameters[1], false);
                    break;
                default:
                    sendMessage(caller, "Usage: /zown list <world>");
            }
        }

        if (world != null) {
            final Tree<? extends IZown> zownRootTree = zownManager.getZown(world);
            for (final Tree<? extends IZown> zownTree : zownRootTree) {
                sendMessage(caller, zownTree.getData().getName());
            }
        }
    }

    @Command(aliases = {
            "rename" }, parent = "template", description = "zown template rename", permissions = {
                    "zown.template.rename" }, toolTip = "/zown template rename <template> <newTemplate>")
    public void renameTemplate(final MessageReceiver caller, final String[] parameters) {
        if (parameters.length != 3) {
            sendMessage(caller, "Usage: /zown template rename <template> <newTemplate>");
        } else {
            final String curName = parameters[1];
            final String newName = parameters[2];
            if (templateManager.renameTemplate(curName, newName)) {
                sendMessage(caller, "Renamed template '" + curName + "' to '" + newName + "'.");
            } else {
                sendMessage(caller,
                        "Could not rename template '" + curName + "' to '" + newName + "'.");
            }
        }
    }

    @Command(aliases = { "rename" }, parent = "zown", description = "zown rename", permissions = {
            "zown.zown.manage.rename" }, toolTip = "/zown rename <zown> <newZown>")
    public void renameZown(final MessageReceiver caller, final String[] parameters) {
        World world = null;
        Player player = null;
        String zown = null;
        String newZown = null;

        if (caller instanceof Player) {
            player = caller.asPlayer();
            switch (parameters.length) {
                case 3:
                    world = player.getWorld();
                    zown = parameters[1];
                    newZown = parameters[2];
                    break;
                default:
                    sendMessage(caller, "Usage: /zown rename <zown> <newZown>");
            }
        } else {
            switch (parameters.length) {
                case 4:
                    world = worldManager.getWorld(parameters[1], false);
                    zown = parameters[2];
                    newZown = parameters[3];
                    break;
                default:
                    sendMessage(caller, "Usage: /zown rename <world> <zown> <newZown>");
            }
        }

        if (world != null && zown != null && newZown != null) {
            if (zownManager.renameZown(world, zown, newZown, player)) {
                sendMessage(caller, "Renamed zown to '" + newZown + "'.");
            } else {
                sendMessage(caller, "Failed to rename zown '" + zown + "'.");
            }
        }
    }

    @Command(aliases = {
            "restrictcommand" }, parent = "template", description = "zown template restrictcommand", permissions = {
                    "zown.template.restrictcommand" }, toolTip = "/zown template restrictcommand <template> <add | remove> <command>")
    public void restrictTemplateCommand(final MessageReceiver caller, final String[] parameters) {
        if (parameters.length != 4) {
            sendMessage(caller,
                    "Usage: /zown template restrictcommand <template> <add | remove> <command>");
        } else {
            final String name = parameters[1];
            final ITemplate template = templateManager.getTemplate(name);
            if (template == null) {
                sendMessage(caller, "Template '" + parameters[1] + "' doesn't exist.");
            } else {
                if ("add".equalsIgnoreCase(parameters[2])) {
                    final String command = parameters[3].toLowerCase();
                    final IConfiguration config = template.getConfiguration();
                    if (config.addCommandRestriction(command)) {
                        sendMessage(caller, "Added command restriction '" + command
                                + "' to template '" + parameters[1] + "'.");
                        templateManager.saveTemplateConfiguration(name);
                    } else {
                        sendMessage(caller, "Command restriction '" + command
                                + "' already exists on template.");
                    }
                } else if ("remove".equalsIgnoreCase(parameters[2])) {
                    final String command = parameters[3].toLowerCase();
                    final IConfiguration config = template.getConfiguration();
                    if (config.removeCommandRestriction(command)) {
                        sendMessage(caller, "Removed command restriction '" + command
                                + "' from template '" + parameters[1] + "'.");
                        templateManager.saveTemplateConfiguration(name);
                    } else {
                        sendMessage(caller, "Command restriction '" + command
                                + "' does not exist on template.");
                    }
                } else {
                    sendMessage(caller,
                            "Usage: /zown template restrictcommand <template> <add | remove> <command>");
                }
            }
        }
    }

    @Command(aliases = {
            "restrictcommand" }, parent = "zown", description = "zown restrictcommand", permissions = {
                    "zown.admin.restrictcommand" }, toolTip = "/zown restrictcommand <zown> <add | remove> <command>")
    public void restrictZownCommand(final MessageReceiver caller, final String[] parameters) {
        World world = null;
        String zown = null;
        String action = null;
        String command = null;

        if (caller instanceof Player) {
            final Player player = caller.asPlayer();
            switch (parameters.length) {
                case 4:
                    world = player.getWorld();
                    zown = parameters[1];
                    action = parameters[2];
                    command = parameters[3];
                    break;
                default:
                    sendMessage(caller,
                            "Usage: /zown restrictcommand <zown> <add | remove> <command>");
            }
        } else {
            switch (parameters.length) {
                case 5:
                    world = worldManager.getWorld(parameters[1], false);
                    zown = parameters[2];
                    action = parameters[3];
                    command = parameters[4];
                    break;
                default:
                    sendMessage(caller,
                            "Usage: /zown restrictcommand <world> <zown> <add | remove> <command>");
            }
        }

        if (world != null && zown != null && action != null && command != null) {
            final Tree<? extends IZown> zownTree = zownManager.getZown(world, zown);
            if (zownTree == null) {
                sendMessage(caller, "No zown '" + zown + "' exists.");
            } else {
                if ("add".equalsIgnoreCase(action)) {

                    if (!zownTree.getData().overridesConfiguration()) {
                        // TODO put in zownManager
                        zownTree.getData().setOverridesConfiguration(true);
                        zownManager.saveZownConfiguration(world, zown);
                    }

                    if (zownTree.getData().getConfiguration().addCommandRestriction(command)) {
                        sendMessage(caller, "Added command restriction '" + command + "' to zown '"
                                + zown + "'.");
                        zownManager.saveZownConfiguration(world, zown);
                    } else {
                        sendMessage(caller,
                                "Command restriction '" + command + "' already exists on zown.");
                    }
                } else if ("remove".equalsIgnoreCase(action)) {
                    if (zownTree.getData().overridesConfiguration()) {
                        if (zownTree.getData().getConfiguration()
                                .removeCommandRestriction(command)) {
                            sendMessage(caller, "Removed command restriction '" + command
                                    + "' from zown '" + zown + "'.");
                            zownManager.saveZownConfiguration(world, zown);
                        } else {
                            sendMessage(caller, "Command restriction '" + command
                                    + "' does not exist on zown.");
                        }
                    } else {
                        sendMessage(caller, "Zown '" + zown + "' inherits from template '"
                                + zownTree.getData().getTemplate().getName() + "'.");
                    }
                } else {
                    sendMessage(caller,
                            "Unrecognized action '" + action + "' must be <add | remove>.");
                }
            }
        }
    }

    @Command(aliases = { "show" }, parent = "zown", description = "zown show", permissions = {
            "zown.zown.show" }, toolTip = "/zown show [zown]")
    public void showZown(final MessageReceiver caller, final String[] parameters) {
        String zown = null;
        Player player = null;

        if (caller instanceof Player) {
            player = caller.asPlayer();
            switch (parameters.length) {
                case 2:
                    zown = parameters[1];
                    break;
                default:
                    sendMessage(caller, "Usage: /zown show <zown>");
            }
        }

        if (zown != null && player != null) {
            final Tree<? extends IZown> zownTree = zownManager.getZown(player.getWorld(), zown);
            if (zownTree == null) {
                sendMessage(caller, "No zown '" + zown + "' exists.");
            } else {
                final Point minPoint = zownTree.getData().getMinPoint();
                final Point maxPoint = zownTree.getData().getMaxPoint();
                if (minPoint != null && maxPoint != null) {
                    drawTemporaryZownBorder(player, minPoint, maxPoint);
                } else {
                    sendMessage(caller, "You cannot show a global zown.");
                }
            }
        }
    }

    @Command(aliases = {
            "template" }, parent = "zown", description = "zown template", permissions = {
                    "zown.template" }, toolTip = "/zown template <list | info | copy | create | delete | rename | apply | flagaccess | flag | placeexception | interactexception>")
    public void template(final MessageReceiver caller, final String[] parameters) {
        sendMessage(caller,
                "Usage: /zown template <list | info | copy | create | delete | rename | apply | flagaccess | flag | placeexception | interactexception>");
    }

    @Command(aliases = {
            "info" }, parent = "template", description = "zown template info", permissions = {
                    "zown.template.info" }, toolTip = "/zown template info <template>")
    public void templateInfo(final MessageReceiver caller, final String[] parameters) {
        if (parameters.length != 2) {
            sendMessage(caller, "Usage: /zown template info <template>");
        } else {
            final ITemplate template = templateManager.getTemplate(parameters[1]);
            if (template == null) {
                sendMessage(caller, "No template '" + parameters[1] + "' exists.");
            } else {
                sendMessage(caller, template.getDisplay());
            }
        }
    }

    @Command(aliases = {
            "interactexception" }, parent = "template", description = "zown template interactexception", permissions = {
                    "zown.template.exception.interact" }, toolTip = "/zown template interactexception <template> <add | remove> minecraft:<exception>")
    public void templateInteractExclusion(final MessageReceiver caller, final String[] parameters) {
        if (parameters.length != 4) {
            sendMessage(caller,
                    "Usage: /zown template interactexception <template> <add | remove> minecraft:<exception>");
        } else {
            final String name = parameters[1];
            final String action = parameters[2];
            final String exclusion = parameters[3];

            final ITemplate template = templateManager.getTemplate(name);
            if (template == null) {
                sendMessage(caller, "Template '" + name + "' doesn't exist.");
            } else {
                if ("add".equalsIgnoreCase(action)) {
                    final BlockType blockType = MinecraftMapper.getBlockType(exclusion);
                    if (blockType == null) {
                        final Class<? extends Entity> entityClass = MinecraftMapper
                                .getEntityClass(exclusion);
                        if (entityClass == null) {
                            sendMessage(caller,
                                    "The specified exception '" + exclusion + "' is unrecognized.");
                        } else {
                            if (template.getConfiguration()
                                    .addEntityInteractExclusion(entityClass)) {
                                sendMessage(caller, "Added interact exception '" + exclusion
                                        + "' to template '" + name + "'.");
                                templateManager.saveTemplateConfiguration(name);
                            } else {
                                sendMessage(caller, "Interact exception '" + exclusion
                                        + "' already exists on template.");
                            }
                        }
                    } else {
                        if (template.getConfiguration().addBlockInteractExclusion(blockType)) {
                            sendMessage(caller, "Added interact exception '" + exclusion
                                    + "' to template '" + name + "'.");
                            templateManager.saveTemplateConfiguration(name);
                        } else {
                            sendMessage(caller, "Interact exception '" + exclusion
                                    + "' already exists on template.");
                        }
                    }
                } else if ("remove".equalsIgnoreCase(action)) {
                    final BlockType blockType = MinecraftMapper.getBlockType(exclusion);
                    if (blockType == null) {
                        final Class<? extends Entity> entityClass = MinecraftMapper
                                .getEntityClass(exclusion);
                        if (entityClass == null) {
                            sendMessage(caller,
                                    "The specified exception '" + exclusion + "' is unrecognized.");
                        } else {
                            if (template.getConfiguration()
                                    .removeEntityInteractExclusion(entityClass)) {
                                sendMessage(caller, "Removed interact exception '" + exclusion
                                        + "' from template '" + name + "'.");
                                templateManager.saveTemplateConfiguration(name);
                            } else {
                                sendMessage(caller, "Interact exception '" + exclusion
                                        + "' does not exist on template.");
                            }
                        }
                    } else {
                        if (template.getConfiguration().removeBlockInteractExclusion(blockType)) {
                            sendMessage(caller, "Removed interact exception '" + exclusion
                                    + "' from template '" + name + "'.");
                            templateManager.saveTemplateConfiguration(name);
                        } else {
                            sendMessage(caller, "Interact exception '" + exclusion
                                    + "' does not exist on template.");
                        }
                    }
                } else {
                    sendMessage(caller,
                            "Unrecognized action '" + action + "' must be <add | remove>.");
                }
            }
        }
    }

    @Command(aliases = {
            "placeexception" }, parent = "template", description = "zown template placeexception", permissions = {
                    "zown.template.exception.place" }, toolTip = "/zown template placeexception <template> <add | remove> minecraft:<exception>")
    public void templatePlaceExclusion(final MessageReceiver caller, final String[] parameters) {
        if (parameters.length != 4) {
            sendMessage(caller,
                    "Usage: /zown template placeexception <template> <add | remove> minecraft:<exception>");
        } else {
            final String name = parameters[1];
            final String action = parameters[2];
            final String exclusion = parameters[3];

            final ITemplate template = templateManager.getTemplate(name);
            if (template == null) {
                sendMessage(caller, "Template '" + name + "' doesn't exist.");
            } else {
                if ("add".equalsIgnoreCase(action)) {
                    final BlockType blockType = MinecraftMapper.getBlockType(exclusion);
                    if (blockType == null) {
                        final Class<? extends Entity> entityClass = MinecraftMapper
                                .getEntityClass(exclusion);
                        if (entityClass == null) {
                            sendMessage(caller,
                                    "The specified exception '" + exclusion + "' is unrecognized.");
                        } else {
                            if (template.getConfiguration().addEntityCreateExclusion(entityClass)) {
                                sendMessage(caller, "Added place exception '" + exclusion
                                        + "' to template '" + name + "'.");
                                templateManager.saveTemplateConfiguration(name);
                            } else {
                                sendMessage(caller, "Place exception '" + exclusion
                                        + "' already exists on template.");
                            }
                        }
                    } else {
                        if (template.getConfiguration().addBlockBuildExclusion(blockType)) {
                            sendMessage(caller, "Added place exception '" + exclusion
                                    + "' to template '" + name + "'.");
                            templateManager.saveTemplateConfiguration(name);
                        } else {
                            sendMessage(caller, "Place exception '" + exclusion
                                    + "' already exists on template.");
                        }
                    }
                } else if ("remove".equalsIgnoreCase(action)) {
                    final BlockType blockType = MinecraftMapper.getBlockType(exclusion);
                    if (blockType == null) {
                        final Class<? extends Entity> entityClass = MinecraftMapper
                                .getEntityClass(exclusion);
                        if (entityClass == null) {
                            sendMessage(caller,
                                    "The specified exception '" + exclusion + "' is unrecognized.");
                        } else {
                            if (template.getConfiguration()
                                    .removeEntityCreateExclusion(entityClass)) {
                                sendMessage(caller, "Removed place exception '" + exclusion
                                        + "' from template '" + name + "'.");
                                templateManager.saveTemplateConfiguration(name);
                            } else {
                                sendMessage(caller, "Place exception '" + exclusion
                                        + "' does not exist on template.");
                            }
                        }
                    } else {
                        if (template.getConfiguration().removeBlockBuildExclusion(blockType)) {
                            sendMessage(caller, "Removed place exception '" + exclusion
                                    + "' from template '" + name + "'.");
                            templateManager.saveTemplateConfiguration(name);
                        } else {
                            sendMessage(caller, "Place exception '" + exclusion
                                    + "' does not exist on template.");
                        }
                    }
                } else {
                    sendMessage(caller,
                            "Unrecognized action '" + action + "' must be <add | remove>.");
                }
            }
        }
    }

    @Command(aliases = { "zown" }, description = "zown", permissions = {
            "zown.zown" }, toolTip = "/zown <list | info | show | create | expand | delete | rename | editpoints | template | flagaccess | flag | restrictcommand | placeexception | interactexception | owner | member>")
    public void zown(final MessageReceiver caller, final String[] parameters) {
        sendMessage(caller,
                "Usage: /zown <list | info | show | create | expand | delete | rename | editpoints | template | flagaccess | flag | restrictcommand | placeexception | interactexception | owner | member>");
    }

    @Command(aliases = { "info" }, parent = "zown", description = "zown info", permissions = {
            "zown.zown.info" }, toolTip = "/zown info [zown]")
    public void zownInfo(final MessageReceiver caller, final String[] parameters) {
        World world = null;
        String zown = null;
        Tree<? extends IZown> zownTree = null;

        if (caller instanceof Player) {
            final Player player = caller.asPlayer();
            switch (parameters.length) {
                case 1:
                    world = player.getWorld();
                    zownTree = zownManager.getZown(player.getLocation());
                    break;
                case 2:
                    world = player.getWorld();
                    zown = parameters[1];
                    break;
                default:
                    sendMessage(caller, "Usage: /zown info [zown]");
            }
        } else {
            switch (parameters.length) {
                case 3:
                    world = worldManager.getWorld(parameters[1], false);
                    zown = parameters[2];
                    break;
                default:
                    sendMessage(caller, "Usage: /zown info <world> <zown>");
            }
        }

        if (world != null && zown != null) {
            zownTree = zownManager.getZown(world, zown);
        }
        if (zownTree == null) {
            sendMessage(caller,
                    "There is no zown '" + zown + "' in world " + world.getFqName() + ".");
        } else {
            sendMessage(caller, zownTree.getData().getDisplay());
        }
    }

    @Command(aliases = {
            "interactexception" }, parent = "zown", description = "zown interactexception", permissions = {
                    "zown.zown.exception.interact" }, toolTip = "/zown interactexception <zown> <add | remove> minecraft:<exception>")
    public void zownInteractExclusion(final MessageReceiver caller, final String[] parameters) {
        World world = null;
        Player player = null;
        String zown = null;
        String action = null;
        String exclusion = null;

        if (caller instanceof Player) {
            player = caller.asPlayer();
            switch (parameters.length) {
                case 4:
                    world = player.getWorld();
                    zown = parameters[1];
                    action = parameters[2];
                    exclusion = parameters[3];
                    break;
                default:
                    sendMessage(caller,
                            "Usage: /zown interactexception <zown> <add | remove> minecraft:<exception>");
            }
        } else {
            switch (parameters.length) {
                case 5:
                    world = worldManager.getWorld(parameters[1], false);
                    zown = parameters[2];
                    action = parameters[3];
                    exclusion = parameters[4];
                    break;
                default:
                    sendMessage(caller,
                            "Usage: /zown interactexception <world> <zown> <add | remove> minecraft:<exception>");
            }
        }

        if (world != null && zown != null && action != null && exclusion != null) {
            final Tree<? extends IZown> zownTree = zownManager.getZown(world, zown);
            if (zownTree == null) {
                sendMessage(caller, "Zown '" + zown + "' doesn't exist.");
            } else {
                if (player == null || player.isOperator()
                        || player.safeHasPermission("zown.admin.exception.interact")
                        || zownTree.getData().isOwner(player) && zownTree.getData()
                                .getConfiguration().hasOwnerPermission(Flag.interact.name())) {
                    if ("add".equalsIgnoreCase(action)) {
                        if (!zownTree.getData().overridesConfiguration()) {
                            // TODO put in zownManager
                            zownTree.getData().setOverridesConfiguration(true);
                            zownManager.saveZownConfiguration(world, zown);
                        }

                        final BlockType blockType = MinecraftMapper.getBlockType(exclusion);
                        if (blockType == null) {
                            final Class<? extends Entity> entityClass = MinecraftMapper
                                    .getEntityClass(exclusion);
                            if (entityClass == null) {
                                sendMessage(caller, "The specified exception '" + exclusion
                                        + "' is unrecognized.");
                            } else {
                                if (zownTree.getData().getConfiguration()
                                        .addEntityInteractExclusion(entityClass)) {
                                    sendMessage(caller, "Added interact exception '" + exclusion
                                            + "' to zown '" + zown + "'.");
                                    zownManager.saveZownConfiguration(world, zown);
                                } else {
                                    sendMessage(caller, "Interact exception '" + exclusion
                                            + "' already exists on zown.");
                                }
                            }
                        } else {
                            if (zownTree.getData().getConfiguration()
                                    .addBlockInteractExclusion(blockType)) {
                                sendMessage(caller, "Added interact exception '" + exclusion
                                        + "' to zown '" + zown + "'.");
                                zownManager.saveZownConfiguration(world, zown);
                            } else {
                                sendMessage(caller, "Interact exception '" + exclusion
                                        + "' already exists on zown.");
                            }
                        }
                    } else if ("remove".equalsIgnoreCase(action)) {
                        if (!zownTree.getData().overridesConfiguration()) {
                            // TODO put in zownManager
                            zownTree.getData().setOverridesConfiguration(true);
                            zownManager.saveZownConfiguration(world, zown);
                        }

                        final BlockType blockType = MinecraftMapper.getBlockType(exclusion);
                        if (blockType == null) {
                            final Class<? extends Entity> entityClass = MinecraftMapper
                                    .getEntityClass(exclusion);
                            if (entityClass == null) {
                                sendMessage(caller, "The specified exception '" + exclusion
                                        + "' is unrecognized.");
                            } else {
                                if (zownTree.getData().getConfiguration()
                                        .removeEntityInteractExclusion(entityClass)) {
                                    sendMessage(caller, "Removed interact exception '" + exclusion
                                            + "' from zown '" + zown + "'.");
                                    zownManager.saveZownConfiguration(world, zown);
                                } else {
                                    sendMessage(caller, "Interact exception '" + exclusion
                                            + "' does not exist on zown.");
                                }
                            }
                        } else {
                            if (zownTree.getData().getConfiguration()
                                    .removeBlockInteractExclusion(blockType)) {
                                sendMessage(caller, "Removed interact exception '" + exclusion
                                        + "' from zown '" + zown + "'.");
                                zownManager.saveZownConfiguration(world, zown);
                            } else {
                                sendMessage(caller, "Interact exception '" + exclusion
                                        + "' does not exist on zown.");
                            }
                        }
                    } else {
                        sendMessage(caller,
                                "Unrecognized action '" + action + "' must be <add | remove>.");
                    }
                } else {
                    sendMessage(caller, "You are not an owner of this zown");
                }
            }
        }
    }

    @Command(aliases = { "member" }, parent = "zown", description = "zown member", permissions = {
            "zown.zown.player.member" }, toolTip = "/zown member <zown> <add | remove> <player>")
    public void zownMember(final MessageReceiver caller, final String[] parameters) {
        World world = null;
        String zown = null;
        String action = null;
        String playerName = null;

        Player callingPlayer = null;

        if (caller instanceof Player) {
            callingPlayer = caller.asPlayer();
            switch (parameters.length) {
                case 4:
                    world = callingPlayer.getWorld();
                    zown = parameters[1];
                    action = parameters[2];
                    playerName = parameters[3];
                    break;
                default:
                    sendMessage(caller, "Usage: /zown member <zown> <add | remove> <player>");
            }
        } else {
            switch (parameters.length) {
                case 5:
                    world = worldManager.getWorld(parameters[1], false);
                    zown = parameters[2];
                    action = parameters[3];
                    playerName = parameters[4];
                    break;
                default:
                    sendMessage(caller,
                            "Usage: /zown member <world> <zown> <add | remove> <player>");
            }
        }

        if (world != null && zown != null && action != null && playerName != null) {
            final Tree<? extends IZown> zownTree = zownManager.getZown(world, zown);
            final Player player = Canary.getServer().getPlayer(playerName);
            if (zownTree == null) {
                sendMessage(caller, "No zown '" + zown + "' exists.");
            } else if (player == null) {
                sendMessage(caller, "No player '" + playerName + "' is online.");
            } else {
                if (callingPlayer == null || callingPlayer.isOperator()
                        || player.safeHasPermission("zown.admin.player.member")
                        || zownTree.getData().isOwner(callingPlayer)) {
                    if ("add".equalsIgnoreCase(action)) {
                        if (zownTree.getData().addMember(player)) {
                            sendMessage(caller,
                                    "Added member '" + playerName + "' to zown '" + zown + "'.");
                            zownManager.saveZownConfiguration(world, zown);
                        } else {
                            sendMessage(caller,
                                    "Member '" + playerName + "' already exists on zown.");
                        }
                    } else if ("remove".equalsIgnoreCase(action)) {
                        if (zownTree.getData().removeMember(player)) {
                            sendMessage(caller, "Removed member '" + playerName + "' from zown '"
                                    + zown + "'.");
                            zownManager.saveZownConfiguration(world, zown);
                        } else {
                            sendMessage(caller,
                                    "Member '" + playerName + "' does not exist on zown.");
                        }
                    } else {
                        sendMessage(caller,
                                "Unrecognized action '" + action + "' must be <add | remove>.");
                    }
                } else {
                    sendMessage(caller, "You are not an owner of this zown");
                }
            }
        }
    }

    @Command(aliases = { "owner" }, parent = "zown", description = "zown owner", permissions = {
            "zown.zown.player.owner" }, toolTip = "/zown owner <zown> <add | remove> <player>")
    public void zownOwner(final MessageReceiver caller, final String[] parameters) {
        World world = null;
        String zown = null;
        String action = null;
        String playerName = null;

        Player callingPlayer = null;

        if (caller instanceof Player) {
            callingPlayer = caller.asPlayer();
            switch (parameters.length) {
                case 4:
                    world = callingPlayer.getWorld();
                    zown = parameters[1];
                    action = parameters[2];
                    playerName = parameters[3];
                    break;
                default:
                    sendMessage(caller, "Usage: /zown owner <zown> <add | remove> <player>");
            }
        } else {
            switch (parameters.length) {
                case 5:
                    world = worldManager.getWorld(parameters[1], false);
                    zown = parameters[2];
                    action = parameters[3];
                    playerName = parameters[4];
                    break;
                default:
                    sendMessage(caller,
                            "Usage: /zown owner <world> <zown> <add | remove> <player>");
            }
        }

        if (world != null && zown != null && action != null && playerName != null) {
            final Tree<? extends IZown> zownTree = zownManager.getZown(world, zown);
            final Player player = Canary.getServer().getPlayer(playerName);
            if (zownTree == null) {
                sendMessage(caller, "No zown '" + zown + "' exists.");
            } else if (player == null) {
                sendMessage(caller, "No player '" + playerName + "' is online.");
            } else {
                if (callingPlayer == null || callingPlayer.isOperator()
                        || player.safeHasPermission("zown.admin.player.owner")
                        || zownTree.getData().isOwner(callingPlayer)) {
                    if ("add".equalsIgnoreCase(action)) {
                        if (zownTree.getData().addOwner(player)) {
                            sendMessage(caller,
                                    "Added owner '" + playerName + "' to zown '" + zown + "'.");
                            zownManager.saveZownConfiguration(world, zown);
                        } else {
                            sendMessage(caller,
                                    "Owner '" + playerName + "' already exists on zown.");
                        }
                    } else if ("remove".equalsIgnoreCase(action)) {
                        if (zownTree.getData().removeOwner(player)) {
                            sendMessage(caller,
                                    "Removed owner '" + playerName + "' from zown '" + zown + "'.");
                            zownManager.saveZownConfiguration(world, zown);
                        } else {
                            sendMessage(caller,
                                    "Owner '" + playerName + "' does not exist on zown.");
                        }
                    } else {
                        sendMessage(caller,
                                "Unrecognized action '" + action + "' must be <add | remove>.");
                    }
                } else {
                    sendMessage(caller, "You are not an owner of this zown");
                }
            }
        }
    }

    @Command(aliases = { "message" }, parent = "zown", description = "zown message", permissions = {
            "zown.zown.message" }, toolTip = "/zown message <zown> <welcome | farewell | restrictentry> \"<message>\"")
    public void zownMessage(final MessageReceiver caller, final String[] parameters) {
        World world = null;
        String zown = null;
        String type = null;
        String message = null;

        Player player = null;

        if (caller instanceof Player) {
            player = caller.asPlayer();
            if (parameters.length > 3) {
                world = player.getWorld();
                zown = parameters[1];
                type = parameters[2];
                message = parseMessage(parameters, 3);
            } else {
                sendMessage(caller,
                        "Usage: /zown message <zown> <welcome | farewell | restrictentry> \"<message>\"");
            }
        } else {
            if (parameters.length > 4) {
                world = worldManager.getWorld(parameters[1], false);
                zown = parameters[2];
                type = parameters[3];
                message = parseMessage(parameters, 4);
            } else {
                sendMessage(caller,
                        "Usage: /zown message <world> <zown> <welcome | farewell | restrictentry> \"<message>\"");
            }
        }

        if (world != null && zown != null && type != null) {
            if (message == null) {
                sendMessage(caller, "Bad message specified.");
            } else {
                final Tree<? extends IZown> zownTree = zownManager.getZown(world, zown);
                if (zownTree == null) {
                    sendMessage(caller, "No zown '" + zown + "' exists.");
                } else {
                    if (player == null || player.isOperator()
                            || player.safeHasPermission("zown.admin.message")
                            || zownTree.getData().isOwner(player)) {
                        if ("welcome".equalsIgnoreCase(type)) {
                            zownTree.getData().setWelcomeMessage(message);
                            sendMessage(caller, "Added welcome message to zown '" + zown + "'.");
                            zownManager.saveZownConfiguration(world, zown);
                        } else if ("farewell".equalsIgnoreCase(type)) {
                            zownTree.getData().setFarewellMessage(message);
                            sendMessage(caller, "Added farewell message to zown '" + zown + "'.");
                            zownManager.saveZownConfiguration(world, zown);
                        } else if ("restrictentry".equalsIgnoreCase(type)) {
                            // TODO Add support for restrictentry message
                            throw new UnsupportedOperationException(
                                    "Do not yet support restrictentry messages");
                        } else {
                            sendMessage(caller, "Unrecognized message type '" + type
                                    + "' must be <welcome | farewell | restrictentry>.");
                        }
                    } else {
                        sendMessage(caller, "You are not an owner of this zown");
                    }
                }
            }
        }
    }

    @Command(aliases = {
            "placeexception" }, parent = "zown", description = "zown placeexception", permissions = {
                    "zown.zown.exception.place" }, toolTip = "/zown placeexception <zown> <add | remove> minecraft:<exception>")
    public void zownPlaceExclusion(final MessageReceiver caller, final String[] parameters) {
        World world = null;
        Player player = null;
        String zown = null;
        String action = null;
        String exclusion = null;

        if (caller instanceof Player) {
            player = caller.asPlayer();
            switch (parameters.length) {
                case 4:
                    world = player.getWorld();
                    zown = parameters[1];
                    action = parameters[2];
                    exclusion = parameters[3];
                    break;
                default:
                    sendMessage(caller,
                            "Usage: /zown placeexception <zown> <add | remove> minecraft:<exception>");
            }
        } else {
            switch (parameters.length) {
                case 5:
                    world = worldManager.getWorld(parameters[1], false);
                    zown = parameters[2];
                    action = parameters[3];
                    exclusion = parameters[4];
                    break;
                default:
                    sendMessage(caller,
                            "Usage: /zown placeexception <world> <zown> <add | remove> minecraft:<exception>");
            }
        }

        if (world != null && zown != null && action != null && exclusion != null) {
            final Tree<? extends IZown> zownTree = zownManager.getZown(world, zown);
            if (zownTree == null) {
                sendMessage(caller, "Zown '" + zown + "' doesn't exist.");
            } else {
                if (player == null || player.isOperator()
                        || player.safeHasPermission("zown.admin.exception.interact")
                        || zownTree.getData().isOwner(player) && zownTree.getData()
                                .getConfiguration().hasOwnerPermission(Flag.build.name())) {
                    if ("add".equalsIgnoreCase(action)) {
                        if (!zownTree.getData().overridesConfiguration()) {
                            // TODO put in zownManager
                            zownTree.getData().setOverridesConfiguration(true);
                            zownManager.saveZownConfiguration(world, zown);
                        }

                        final BlockType blockType = MinecraftMapper.getBlockType(exclusion);
                        if (blockType == null) {
                            final Class<? extends Entity> entityClass = MinecraftMapper
                                    .getEntityClass(exclusion);
                            if (entityClass == null) {
                                sendMessage(caller, "The specified exception '" + exclusion
                                        + "' is unrecognized.");
                            } else {
                                if (zownTree.getData().getConfiguration()
                                        .addEntityCreateExclusion(entityClass)) {
                                    sendMessage(caller, "Added place exception '" + exclusion
                                            + "' to zown '" + zown + "'.");
                                    zownManager.saveZownConfiguration(world, zown);
                                } else {
                                    sendMessage(caller, "Place exception '" + exclusion
                                            + "' already exists on zown.");
                                }
                            }
                        } else {
                            if (zownTree.getData().getConfiguration()
                                    .addBlockBuildExclusion(blockType)) {
                                sendMessage(caller, "Added place exception '" + exclusion
                                        + "' to zown '" + zown + "'.");
                                zownManager.saveZownConfiguration(world, zown);
                            } else {
                                sendMessage(caller, "Place exception '" + exclusion
                                        + "' already exists on zown.");
                            }
                        }
                    } else if ("remove".equalsIgnoreCase(action)) {
                        if (!zownTree.getData().overridesConfiguration()) {
                            // TODO put in zownManager
                            zownTree.getData().setOverridesConfiguration(true);
                            zownManager.saveZownConfiguration(world, zown);
                        }

                        final BlockType blockType = MinecraftMapper.getBlockType(exclusion);
                        if (blockType == null) {
                            final Class<? extends Entity> entityClass = MinecraftMapper
                                    .getEntityClass(exclusion);
                            if (entityClass == null) {
                                sendMessage(caller, "The specified exception '" + exclusion
                                        + "' is unrecognized.");
                            } else {
                                if (zownTree.getData().getConfiguration()
                                        .removeEntityCreateExclusion(entityClass)) {
                                    sendMessage(caller, "Removed build exception '" + exclusion
                                            + "' from zown '" + zown + "'.");
                                    zownManager.saveZownConfiguration(world, zown);
                                } else {
                                    sendMessage(caller, "Build exception '" + exclusion
                                            + "' does not exist on zown.");
                                }
                            }
                        } else {
                            if (zownTree.getData().getConfiguration()
                                    .removeBlockBuildExclusion(blockType)) {
                                sendMessage(caller, "Removed build exception '" + exclusion
                                        + "' from zown '" + zown + "'.");
                                zownManager.saveZownConfiguration(world, zown);
                            } else {
                                sendMessage(caller, "Build exception '" + exclusion
                                        + "' does not exist on zown.");
                            }
                        }
                    } else {
                        sendMessage(caller,
                                "Unrecognized action '" + action + "' must be <add | remove>.");
                    }
                } else {
                    sendMessage(caller, "You are not an owner of this zown");
                }
            }
        }
    }

    private void drawTemporaryZownBorder(final Player player, final Point minPoint,
            final Point maxPoint) {
        final World world = player.getWorld();

        final Collection<Point> points = new LinkedList<Point>();

        int x = minPoint.x;
        int z = minPoint.z;
        int y = world.getHighestBlockAt(x, z) - 1;
        points.add(new Point(x, y, z));

        x = minPoint.x;
        z = maxPoint.z;
        y = world.getHighestBlockAt(x, z) - 1;
        points.add(new Point(x, y, z));

        x = maxPoint.x;
        z = minPoint.z;
        y = world.getHighestBlockAt(x, z) - 1;
        points.add(new Point(x, y, z));

        x = maxPoint.x;
        z = maxPoint.z;
        y = world.getHighestBlockAt(x, z) - 1;
        points.add(new Point(x, y, z));

        x = minPoint.x;
        z = minPoint.z;
        for (x = x + 1; x < maxPoint.x; x++) {
            if (x % 10 == 0) {
                y = world.getHighestBlockAt(x, z) - 1;
                points.add(new Point(x, y, z));
            }
        }

        x = minPoint.x;
        z = maxPoint.z;
        for (x = x + 1; x < maxPoint.x; x++) {
            if (x % 10 == 0) {
                y = world.getHighestBlockAt(x, z) - 1;
                points.add(new Point(x, y, z));
            }
        }

        x = minPoint.x;
        z = minPoint.z;
        for (z = z + 1; z < maxPoint.z; z++) {
            if (z % 10 == 0) {
                y = world.getHighestBlockAt(x, z) - 1;
                points.add(new Point(x, y, z));
            }
        }

        x = maxPoint.x;
        z = minPoint.z;
        for (z = z + 1; z < maxPoint.z; z++) {
            if (z % 10 == 0) {
                y = world.getHighestBlockAt(x, z) - 1;
                points.add(new Point(x, y, z));
            }
        }

        final PacketFactory packetFactory = Canary.factory().getPacketFactory();

        // Show the zown to the player
        for (final Point point : points) {
            player.sendPacket(
                    packetFactory.blockChange(point.x, point.y, point.z, BlockType.GlowStone));
        }

        // Wait for the delay, then return the blocks to normal
        TaskManager.scheduleDelayedTask(new Runnable() {
            @Override
            public void run() {
                for (final Point point : points) {
                    final BlockType type = world.getBlockAt(point.x, point.y, point.z).getType();
                    player.sendPacket(packetFactory.blockChange(point.x, point.y, point.z, type));
                }
            }
        }, 10, TimeUnit.SECONDS);
    }

    private String parseMessage(final String[] parameters, final int startIndex) {
        String message = null;
        if (parameters[startIndex].startsWith("\"")
                && parameters[parameters.length - 1].endsWith("\"")) {
            final StringBuilder sb = new StringBuilder();
            for (int i = startIndex; i < parameters.length; i++) {
                sb.append(parameters[i]);
                if (i < parameters.length - 1) {
                    sb.append(" ");
                }
            }
            sb.deleteCharAt(0);
            sb.deleteCharAt(sb.length() - 1);
            message = sb.toString();
        }
        return message;
    }
}
