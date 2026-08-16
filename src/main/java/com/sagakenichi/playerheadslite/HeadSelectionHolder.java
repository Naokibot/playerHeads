package com.sagakenichi.playerheadslite;

import java.util.List;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class HeadSelectionHolder implements InventoryHolder {
    private final int page;
    private final List<OfflinePlayer> players;
    private Inventory inventory;

    public HeadSelectionHolder(int page, List<OfflinePlayer> players) {
        this.page = page;
        this.players = List.copyOf(players);
    }

    public int page() { return page; }
    public List<OfflinePlayer> players() { return players; }
    void attach(Inventory inventory) { this.inventory = inventory; }

    @Override
    public Inventory getInventory() { return inventory; }
}
