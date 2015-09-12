package com.goodformentertainment.bc.nav;

import java.util.HashMap;
import java.util.Map;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.position.Location;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandListener;

public class GoCommand implements CommandListener {
    private final Map<String, Map<String, Location>> targets;

    public GoCommand() {
        targets = new HashMap<String, Map<String, Location>>();
    }

    public boolean add(final String context, final Location location) {
        return add(context, null, location);
    }

    public boolean add(final String context, final String target, final Location location) {
        Map<String, Location> contextTargets = targets.get(context);
        if (contextTargets == null) {
            contextTargets = new HashMap<String, Location>();
            targets.put(context, contextTargets);
        }
        final Location oldLocation = contextTargets.put(target, location);
        return oldLocation == null;
    }

    @Command(aliases = { "go" }, description = "Briarcraft Navigation List", permissions = {
            "go.command" }, toolTip = "/go <target> [context]", min = 0, max = 2)
    public void go(final MessageReceiver caller, final String[] parameters) {
        if (caller instanceof Player) {
            final Player player = caller.asPlayer();
            if (parameters.length == 1) {
                final String context = player.getWorld().getFqName();

                // Get Go list
                player.message("Briarcraft Navigation List:");
                for (final String nav : targets.keySet()) {
                    player.message("  /go " + nav);
                }
            } else {
                // Get the context either from parameters or player location
                final String context;
                if (parameters.length == 2) {
                    context = player.getWorld().getFqName();
                } else {
                    context = parameters[3];
                }

                // Get the context targets
                final String nav = parameters[1];
                final Map<String, Location> contextTargets = targets.get(context);
                if (contextTargets == null) {
                    player.message("The context " + context + " does not exist!");
                } else {
                    final Location loc = contextTargets.get(nav);
                    if (loc == null) {
                        player.message("The target " + nav + " does not exist!");
                    } else {
                        // Teleport to the target
                        player.message("Going to " + nav + ".");
                        player.teleportTo(loc);
                    }
                }
            }
        }
    }
}
