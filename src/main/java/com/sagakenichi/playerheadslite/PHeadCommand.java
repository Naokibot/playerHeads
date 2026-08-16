package com.sagakenichi.playerheadslite;

import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class PHeadCommand implements CommandExecutor, TabCompleter {
    private static final String PERMISSION = "playerheads.use";
    private final PlayerHeadsLitePlugin plugin;
    private final HeadFactory headFactory;
    private final PlayerDirectory directory;
    private final HeadGui gui;

    public PHeadCommand(PlayerHeadsLitePlugin plugin, HeadFactory headFactory, PlayerDirectory directory, HeadGui gui) {
        this.plugin = plugin;
        this.headFactory = headFactory;
        this.directory = directory;
        this.gui = gui;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfig().getString("messages.player-only", "This command can only be used by a player."));
            return true;
        }
        if (!player.hasPermission(PERMISSION)) {
            player.sendMessage(ChatColor.RED + plugin.getConfig().getString("messages.no-permission", "You do not have permission to use this command."));
            return true;
        }
        if (args.length == 0) {
            gui.open(player, 0);
            return true;
        }
        if (args.length != 1 || !PlayerDirectory.isSafeInput(args[0])) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /phead [player]");
            return true;
        }

        String requestedName = args[0].trim();
        player.sendMessage(ChatColor.GRAY + plugin.getConfig().getString("messages.loading", "Loading the player head..."));
        directory.resolveProfile(requestedName).whenComplete((resolved, error) ->
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (!player.isOnline()) return;
                if (error != null) {
                    plugin.getLogger().warning("Failed to resolve head for '" + requestedName + "': " + error.getMessage());
                    player.sendMessage(ChatColor.RED + plugin.getConfig().getString("messages.not-found", "No usable skin profile was found for that player."));
                    return;
                }
                if (resolved == null) {
                    player.sendMessage(ChatColor.RED + plugin.getConfig().getString("messages.not-found", "No usable skin profile was found for that player."));
                    return;
                }
                headFactory.giveOrDrop(player, headFactory.createHead(resolved.profile(), resolved.displayName()));
                player.sendMessage(ChatColor.GREEN + plugin.getConfig().getString("messages.given", "Player head added to your inventory."));
            })
        );
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission(PERMISSION)) return directory.suggestions(args[0]);
        return Collections.emptyList();
    }
}
