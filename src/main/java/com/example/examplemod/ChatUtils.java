// ChatUtils.java
package com.example.examplemod;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class ChatUtils {

    public static String getPlayerPrefix(String username) {
        String prefix = ConfigHandler.prefixMap.get(username);
        return prefix != null ? replaceAmpersand(prefix) : "";
    }

    public static String getPlayerSuffix(String username) {
        String suffix = ConfigHandler.suffixMap.get(username);
        return suffix != null ? replaceAmpersand(suffix) : "";
    }

    private static String replaceAmpersand(String input) {
        return input.replace('&', '§');
    }

    public static String getPlayerFaction(String username) {
        String faction = ConfigHandler.factionMap.get(username);
        return faction != null ? faction : "";
    }
}