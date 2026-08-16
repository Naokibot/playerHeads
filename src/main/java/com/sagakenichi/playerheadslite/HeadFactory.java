package com.sagakenichi.playerheadslite;

import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

public final class HeadFactory {
    public ItemStack createHead(PlayerProfile profile, String displayName) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta itemMeta = head.getItemMeta();
        if (!(itemMeta instanceof SkullMeta skullMeta)) {
            throw new IllegalStateException("PLAYER_HEAD did not provide SkullMeta.");
        }
        skullMeta.setOwnerProfile(profile);
        skullMeta.setDisplayName(ChatColor.YELLOW + displayName + "'s Head");
        head.setItemMeta(skullMeta);
        return head;
    }

    public ItemStack createGuiEntry(String displayName, boolean online) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta meta = head.getItemMeta();
        if (meta == null) {
            throw new IllegalStateException("PLAYER_HEAD did not provide item meta.");
        }
        meta.setDisplayName((online ? ChatColor.GREEN : ChatColor.YELLOW) + displayName);
        meta.setLore(java.util.List.of(ChatColor.GRAY + "Click to receive this player's head."));
        head.setItemMeta(meta);
        return head;
    }

    public void giveOrDrop(Player player, ItemStack item) {
        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(item);
        for (ItemStack leftover : leftovers.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }
    }
}
