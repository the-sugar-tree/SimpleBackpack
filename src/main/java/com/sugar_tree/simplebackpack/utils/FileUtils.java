package com.sugar_tree.simplebackpack.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.sugar_tree.simplebackpack.SimpleBackpack.backpacks;
import static com.sugar_tree.simplebackpack.SimpleBackpack.plugin;

public class FileUtils {

    @SuppressWarnings("unchecked")
    public static void load() {
        File[] files = plugin.getDataFolder().listFiles(File::isFile);
        if (files == null) return;
        for (File file : files) {
            if (!file.getName().endsWith(".yml")) return;
            Inventory inventory = Bukkit.createInventory(null, 9*6, Component.text("Backpack").color(NamedTextColor.DARK_AQUA));
            UUID uuid;
            try {
                uuid = UUID.fromString(file.getName().substring(0, file.getName().length() - 4));
            } catch (IllegalArgumentException e) {
                continue;
            }
            List<Map<?, ?>> itemsList = YamlConfiguration.loadConfiguration(file).getMapList("items");
            for(int i = 0; i <= itemsList.size(); i++) {
                try {
                    if (itemsList.get(i).isEmpty()) {
                        continue;
                    }
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
                //noinspection deprecation
                if (itemsList.get(i).containsKey("v") && Integer.parseInt(itemsList.get(i).get("v").toString()) > Bukkit.getUnsafe().getDataVersion()) {
                    plugin.getLogger().severe("Newer version! Server downgrades are not supported!");
                    return;
                }
                inventory.setItem(i, ItemStack.deserialize((Map<String, Object>) itemsList.get(i)));
            }
            backpacks.put(uuid, inventory);
        }
    }

    public static void save() {
        for (UUID uuid : backpacks.keySet()) {
            File file = new File(plugin.getDataFolder(), uuid.toString() + ".yml");
            if (!file.exists()) {
                try {
                    if (!file.createNewFile()) {
                        plugin.getLogger().severe("파일 저장 중 오류 발생: " + file.getName());
                        continue;
                    }
                } catch (IOException e) {
                    plugin.getLogger().severe("파일 저장 중 오류 발생: " + file.getName());
                    continue;
                }
            }
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            List<Map<?, ?>> itemsList = new ArrayList<>();
            for (ItemStack itemStack : backpacks.get(uuid).getContents()) {
                itemsList.add(Objects.requireNonNullElseGet(itemStack, ItemStack::empty).serialize());
            }
            config.set("items", itemsList);
            try {
                config.save(file);
            } catch (IOException e) {
                plugin.getLogger().severe("파일 저장 중 오류 발생: " + file.getName());
            }
        }
    }
}
