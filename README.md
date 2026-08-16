# PlayerHeadsLite

Spigot 1.21.1 / Java 21 plugin for obtaining player heads through `/phead`.

## Version 1.1.0
- `/phead` opens a paginated GUI of known players.
- `/phead <player>` directly gives a player's head.
- Names containing `.` are accepted.
- Previously seen offline players are resolved by their server-side UUID/profile when available.
- Player deaths never cause this plugin to drop heads.

Build with `mvn clean package`.
