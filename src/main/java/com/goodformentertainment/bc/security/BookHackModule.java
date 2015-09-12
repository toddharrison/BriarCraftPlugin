package com.goodformentertainment.bc.security;

import com.goodformentertainment.bc.Module;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.chat.ChatFormat;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.BookEditHook;
import net.canarymod.hook.player.CraftHook;
import net.canarymod.hook.player.ItemUseHook;
import net.canarymod.plugin.PluginListener;

public class BookHackModule extends Module implements PluginListener {
    private static final String message = ChatFormat.RED
            + "For security reasons, writing custom books is prohibited";

    @Override
    public PluginListener[] getPluginListeners() {
        return new PluginListener[] { this };
    }

    @HookHandler
    public void onCraft(final CraftHook hook) {
        if (hook.getRecipeResult() != null
                && hook.getRecipeResult().getType() == ItemType.BookAndQuill) {
            final Player player = hook.getPlayer();
            hook.setCanceled();
            player.message(message);
        }
    }

    @HookHandler
    public void onEditBook(final BookEditHook hook) {
        final Player player = hook.getPlayer();
        final Item item = hook.getBook();
        player.getInventory().removeItem(item);
        hook.setCanceled();
        player.message(message);
    }

    @HookHandler
    public void onItemUse(final ItemUseHook hook) {
        final Item item = hook.getItem();
        if (item.getType() == ItemType.BookAndQuill) {
            final Player player = hook.getPlayer();
            player.getInventory().removeItem(item);
            hook.setCanceled();
            player.message(message);
        }
    }
}
