package com.sagakenichi.playerheadslite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerProfile;

public final class PlayerDirectory {
    public List<OfflinePlayer> knownPlayers() {
        Map<UUID, OfflinePlayer> players = new LinkedHashMap<>();
        for (Player online : Bukkit.getOnlinePlayers()) {
            players.put(online.getUniqueId(), online);
        }
        Arrays.stream(Bukkit.getOfflinePlayers())
                .filter(player -> player.getName() != null && !player.getName().isBlank())
                .sorted(Comparator.comparingLong(OfflinePlayer::getLastPlayed).reversed())
                .forEach(player -> players.putIfAbsent(player.getUniqueId(), player));
        return new ArrayList<>(players.values());
    }

    public Optional<OfflinePlayer> findKnownPlayer(String requestedName) {
        if (requestedName == null || requestedName.isBlank()) {
            return Optional.empty();
        }
        Player online = Bukkit.getPlayerExact(requestedName);
        if (online != null) {
            return Optional.of(online);
        }
        String needle = requestedName.toLowerCase(Locale.ROOT);
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .filter(player -> player.getName() != null)
                .filter(player -> player.getName().toLowerCase(Locale.ROOT).equals(needle))
                .findFirst();
    }

    public CompletableFuture<ResolvedProfile> resolveProfile(String requestedName) {
        if (!isSafeInput(requestedName)) {
            return CompletableFuture.completedFuture(null);
        }

        try {
            Optional<OfflinePlayer> known = findKnownPlayer(requestedName);
            PlayerProfile baseProfile;
            String displayName;
            if (known.isPresent()) {
                OfflinePlayer player = known.get();
                baseProfile = player.getPlayerProfile();
                displayName = player.getName() == null ? requestedName : player.getName();
            } else {
                baseProfile = Bukkit.createPlayerProfile(requestedName);
                displayName = requestedName;
            }
            return completeProfile(baseProfile, displayName);
        } catch (IllegalArgumentException ex) {
            return CompletableFuture.completedFuture(null);
        }
    }

    public CompletableFuture<ResolvedProfile> resolveProfile(OfflinePlayer player) {
        String name = player.getName();
        if (name == null || name.isBlank()) {
            return CompletableFuture.completedFuture(null);
        }
        try {
            return completeProfile(player.getPlayerProfile(), name);
        } catch (IllegalArgumentException ex) {
            return CompletableFuture.completedFuture(null);
        }
    }

    public List<String> suggestions(String prefix) {
        String needle = prefix == null ? "" : prefix.toLowerCase(Locale.ROOT);
        return knownPlayers().stream()
                .map(OfflinePlayer::getName)
                .filter(name -> name != null && name.toLowerCase(Locale.ROOT).startsWith(needle))
                .limit(50)
                .toList();
    }

    static boolean isSafeInput(String name) {
        if (name == null) {
            return false;
        }
        String trimmed = name.trim();
        return !trimmed.isEmpty()
                && trimmed.length() <= 64
                && trimmed.indexOf('\n') < 0
                && trimmed.indexOf('\r') < 0;
    }

    private CompletableFuture<ResolvedProfile> completeProfile(PlayerProfile profile, String displayName) {
        if (hasSkin(profile)) {
            return CompletableFuture.completedFuture(new ResolvedProfile(profile, displayName));
        }
        try {
            return profile.update()
                    .thenApply(updated -> hasSkin(updated) ? new ResolvedProfile(updated, displayName) : null)
                    .exceptionally(ignored -> null);
        } catch (RuntimeException ex) {
            return CompletableFuture.completedFuture(null);
        }
    }

    private static boolean hasSkin(PlayerProfile profile) {
        return profile != null && profile.getTextures() != null && profile.getTextures().getSkin() != null;
    }

    public record ResolvedProfile(PlayerProfile profile, String displayName) { }
}
