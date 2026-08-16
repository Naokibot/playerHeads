package com.sagakenichi.playerheadslite;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class HeadGui {
    public static final int CONTENT_SIZE = 45;
    public static final int PREVIOUS_SLOT = 45;
    public static final int INFO_SLOT = 49;
    public static final int NEXT_SLOT = 53;
    private final PlayerHeadsLitePlugin plugin;
    private final PlayerDirectory directory;

    public HeadGui(PlayerHeadsLitePlugin plugin, PlayerDirectory directory) {
        this.plugin = plugin;
        this.directory = directory;
    }

    public void open(Player viewer, int requestedPage) {
        List<OfflinePlayer> players = directory.knownPlayers();
        int maxPage = players.isEmpty() ? 0 : (players.size() - 1) / CONTENT_SIZE;
        int page = Math.max(0, Math.min(requestedPage, maxPage));
        HeadSelectionHolder holder = new HeadSelectionHolder(page, players);
        String baseTitle = plugin.getConfig().getString("gui.title", "Player Heads");
        Inventory inventory = Bukkit.createInventory(holder, 54, baseTitle + " " + (page + 1) + "/" + (maxPage + 1));
        holder.attach(inventory);

        int start = page * CONTENT_SIZE;
        for (int slot = 0; slot < CONTENT_SIZE && start + slot < players.size(); slot++) {
            OfflinePlayer player = players.get(start + slot);
            String name = player.getName();
            if (name == null) continue;
            inventory.setItem(slot, plugin.getHeadFactory().createGuiEntry(name, player.isOnline()));
        }
        if (page > 0) inventory.setItem(PREVIOUS_SLOT, button(Material.ARROW, ChatColor.YELLOW + "Previous page"));
        inventory.setItem(INFO_SLOT, button(Material.PAPER, ChatColor.WHITE + "Known players: " + players.size()));
        if (page < maxPage) inventory.setItem(NEXT_SLOT, button(Material.ARROW, ChatColor.YELLOW + "Next page"));
        viewer.openInventory(inventory);
    }

    private ItemStack button(Material material, String name) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }
}
