package com.sagakenichi.playerheadslite;

import java.util.Objects;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlayerHeadsLitePlugin extends JavaPlugin {
    private HeadFactory headFactory;
    private PlayerDirectory playerDirectory;
    private HeadGui headGui;

    public HeadFactory getHeadFactory() { return headFactory; }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.headFactory = new HeadFactory();
        this.playerDirectory = new PlayerDirectory();
        this.headGui = new HeadGui(this, playerDirectory);

        PHeadCommand command = new PHeadCommand(this, headFactory, playerDirectory, headGui);
        PluginCommand phead = Objects.requireNonNull(getCommand("phead"), "Command 'phead' is missing from plugin.yml");
        phead.setExecutor(command);
        phead.setTabCompleter(command);
        getServer().getPluginManager().registerEvents(new HeadGuiListener(this, headFactory, playerDirectory, headGui), this);

        getLogger().info("PlayerHeadsLite 1.1.0 enabled. Death head drops are disabled by design.");
    }
}
