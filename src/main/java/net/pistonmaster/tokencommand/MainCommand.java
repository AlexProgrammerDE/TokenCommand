package net.pistonmaster.tokencommand;

import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.codec.digest.DigestUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MainCommand implements CommandExecutor, TabExecutor {
    private final TokenCommand plugin;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            try {
                try (Statement statement = plugin.getDb().getConnection().createStatement()) {
                    ResultSet result = statement.executeQuery("SELECT username, token FROM " + plugin.getConfig().getString("table") + ";");

                    while (result.next()) {
                        if (result.getString("username").equals(player.getName())) {
                            String token = result.getString("token");
                            player.sendMessage(new ComponentBuilder("Your token: ").color(ChatColor.GOLD.asBungee()).append(token).event(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, token)).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GOLD + "Click to copy!"))).create());
                            return true;
                        }
                    }

                    // Player has no token!
                    String token = generateToken();

                    statement.executeUpdate("INSERT INTO " + plugin.getConfig().getString("table") + " (username, token) VALUES ('" + player.getName() + "', '" + token + "');");

                    player.sendMessage(new ComponentBuilder("Your token: ").color(ChatColor.GOLD.asBungee()).append(token).event(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, token)).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GOLD + "Click to copy!"))).create());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Player only!");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }

    private String generateToken() {
        return DigestUtils.sha1Hex(String.valueOf(System.currentTimeMillis())).substring(0, 6);
    }
}
