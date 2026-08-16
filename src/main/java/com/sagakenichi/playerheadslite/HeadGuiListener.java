package com.sagakenichi.playerheadslite;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class HeadGuiListener implements Listener {
    private final PlayerHeadsLitePlugin plugin;
    private final HeadFactory headFactory;
    private final PlayerDirectory directory;
    private final HeadGui gui;

    public HeadGuiListener(PlayerHeadsLitePlugin plugin, HeadFactory headFactory, PlayerDirectory directory, HeadGui gui) {
        this.plugin = plugin;
        this.headFactory = headFactory;
        this.directory = directory;
        this.gui = gui;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof HeadSelectionHolder holder)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;

        int rawSlot = event.getRawSlot();
        if (rawSlot == HeadGui.PREVIOUS_SLOT) {
            gui.open(player, holder.page() - 1);
            return;
        }
        if (rawSlot == HeadGui.NEXT_SLOT) {
            gui.open(player, holder.page() + 1);
            return;
        }
        if (rawSlot < 0 || rawSlot >= HeadGui.CONTENT_SIZE) return;

        int index = holder.page() * HeadGui.CONTENT_SIZE + rawSlot;
        if (index < 0 || index >= holder.players().size()) return;

        OfflinePlayer target = holder.players().get(index);
        player.closeInventory();
        player.sendMessage(ChatColor.GRAY + plugin.getConfig().getString("messages.loading", "Loading the player head..."));
        directory.resolveProfile(target).whenComplete((resolved, error) ->
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (!player.isOnline()) return;
                if (error != null || resolved == null) {
                    if (error != null) plugin.getLogger().warning("Failed to resolve GUI head: " + error.getMessage());
                    player.sendMessage(ChatColor.RED + plugin.getConfig().getString("messages.not-found", "No usable skin profile was found for that player."));
                    return;
                }
                headFactory.giveOrDrop(player, headFactory.createHead(resolved.profile(), resolved.displayName()));
                player.sendMessage(ChatColor.GREEN + plugin.getConfig().getString("messages.given", "Player head added to your inventory."));
            })
        );
    }
}
