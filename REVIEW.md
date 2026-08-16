# Source review — PlayerHeadsLite 1.1.0

## Fixed

1. Removed `PlayerDeathListener` registration and all death-drop behavior.
2. Removed the restrictive player-name regex, so names containing `.` are accepted.
3. Added `/phead` GUI with 45 player entries per page and next/previous navigation.
4. Added offline-player lookup using `Bukkit.getOfflinePlayers()` and UUID-backed `OfflinePlayer` profiles.
5. Player profile completion uses `PlayerProfile.update()` asynchronously; profile network completion is not awaited on the server main thread.
6. GUI opening does not resolve every player's skin. A profile is resolved only after the player is selected.
7. GUI clicks are scoped with a custom `InventoryHolder` rather than matching inventory titles.
8. Inventory overflow is dropped at the requesting player's location rather than silently discarded.
9. Newline/control-style command injection through a supplied name is rejected while `.` remains valid.

## Environment-dependent behavior

- Offline skin availability depends on profile information retained by Spigot/Geyser/Floodgate and upstream profile services.
- A dot-containing name that has never joined this server may not map to a resolvable Java profile; the command reports that no usable skin was found instead of creating a fake result.

## Verification

- Java 21 compilation with `-Xlint:all` using compile-time Bukkit API stubs: PASS.
- Dot-name policy test (`.Bedrock.User`): PASS.
- JAR compressed-data integrity: PASS.
- Java class major version 65: PASS.
- Release JAR contains no Bukkit test stubs and no `PlayerDeathListener`: PASS.
- GitHub Actions is configured to compile against the real Spigot 1.21.1 API with Maven.
