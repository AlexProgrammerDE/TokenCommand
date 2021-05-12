package net.pistonmaster.tokencommand;

import co.aikar.idb.BukkitDB;
import co.aikar.idb.Database;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class TokenCommand extends JavaPlugin {
    @Getter
    private Database db;

    @Override
    public void onEnable() {
        getLogger().info(ChatColor.AQUA + "Loading config");
        saveDefaultConfig();

        getLogger().info(ChatColor.AQUA + "Trying to connect to database");
        db = BukkitDB.createHikariDatabase(this, getConfig().getString("username"), getConfig().getString("password"), getConfig().getString("database"), getConfig().getString("hostandport"));

        getLogger().info(ChatColor.AQUA + "Registering command");
        getCommand("token").setExecutor(new MainCommand(this));
        getCommand("token").setTabCompleter(new MainCommand(this));

        getLogger().info(ChatColor.AQUA + "Done! :D");
    }
}
