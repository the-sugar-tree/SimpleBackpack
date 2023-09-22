package com.sugar_tree.simplebackpack;

import com.sugar_tree.simplebackpack.utils.FileUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public final class SimpleBackpack extends JavaPlugin {

    public static Map<UUID, Inventory> backpacks;
    public static Plugin plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        backpacks = new HashMap<>();
        plugin = this;
        if (getDataFolder().mkdir()) {
            getLogger().info("폴더 생성 완료!");
        }
        Objects.requireNonNull(getCommand("backpack")).setExecutor(this);
        Objects.requireNonNull(getCommand("backpack")).setTabCompleter(this);
        FileUtils.load();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        FileUtils.save();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p) {
            if (!backpacks.containsKey(p.getUniqueId()))
                backpacks.put(p.getUniqueId(), Bukkit.createInventory(null, 9*6, Component.text("Backpack")
                        .color(NamedTextColor.DARK_AQUA)));
            p.openInventory(backpacks.get(p.getUniqueId()));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
