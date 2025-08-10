package com.example.examplemod;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigHandler {
    public static double localRadius;
    public static String globalPrefix;
    public static String localFormat;
    public static String globalFormat;
    public static Map<String, String> prefixMap = new HashMap<>();
    public static Map<String, String> suffixMap = new HashMap<>();
    public static Map<String, String> factionMap = new HashMap<>();
    public static File configFile;

    public static void init(FMLPreInitializationEvent event) {
        configFile = event.getSuggestedConfigurationFile();
        loadConfig();
    }

    public static void loadConfig() {
        Configuration config = new Configuration(configFile);

        try {
            config.load();

            Property radiusProp = config.get("general", "LocalChatRadius", 50.0);
            radiusProp.comment = "Радиус локального чата (блоки)";
            localRadius = radiusProp.getDouble();

            Property prefixProp = config.get("general", "GlobalPrefix", "!");
            prefixProp.comment = "Префикс для глобального чата";
            globalPrefix = prefixProp.getString();

            // Исправлено: добавлено получение значений форматов
            Property localFormatProp = config.get("general", "LocalFormat",
                    "&e[Локальный] &7<{player}&7> {message} {reset}");
            localFormat = localFormatProp.getString();

            Property globalFormatProp = config.get("general", "GlobalFormat",
                    "&c[Глобальный] &6<{player}&6> {message} {reset}");
            globalFormat = globalFormatProp.getString();

            // Префиксы игроков
            Property prefixesProp = config.get("prefixes", "PlayerPrefixes", new String[0]);
            prefixesProp.comment = "Префиксы игроков в формате: Игрок=Префикс";
            fillMap(prefixesProp, prefixMap);

            // Суффиксы игроков
            Property suffixesProp = config.get("suffixes", "PlayerSuffixes", new String[0]);
            suffixesProp.comment = "Суффиксы игроков в формате: Игрок=Суффикс";
            fillMap(suffixesProp, suffixMap);

            // Фракции игроков
            Property factionsProp = config.get("factions", "PlayerFactions", new String[0]);
            factionsProp.comment = "Фракции игроков в формате: Игрок=Фракция";
            fillMap(factionsProp, factionMap);
        } catch (Exception e) {
            System.err.println("Ошибка загрузки конфига! Используются значения по умолчанию.");
            e.printStackTrace();

            // Установка значений по умолчанию при ошибке
            localRadius = 50.0;
            globalPrefix = "!";
            localFormat = "&e[Локальный] &7<{player}&7> {message}{reset}";
            globalFormat = "&c[Глобальный] &6<{player}&6> {message}{reset}";
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    private static void fillMap(Property prop, Map<String, String> map) {
        map.clear();
        for (String entry : prop.getStringList()) {
            String[] parts = entry.split("=", 2);
            if (parts.length == 2) {
                String playerName = parts[0].trim();
                String value = parts[1].trim();
                if (!playerName.isEmpty()) {
                    map.put(playerName, value);
                }
            }
        }
    }
}