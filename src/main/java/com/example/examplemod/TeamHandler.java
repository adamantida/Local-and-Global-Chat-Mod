package com.example.examplemod;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import java.lang.reflect.Method;

public class TeamHandler {

    public static void addPlayerToFactionTeam(EntityPlayerMP player, String faction) {
        try {
            if (faction == null || faction.isEmpty()) {
                removePlayerFromCurrentTeam(player);
                return;
            }

            Scoreboard scoreboard = getScoreboard();
            if (scoreboard == null) {
                System.err.println("Не удалось получить scoreboard!");
                return;
            }

            // Нормализация названия фракции
            faction = normalizeFactionName(faction);

            ScorePlayerTeam team = scoreboard.getTeam(faction);

            if (team == null) {
                team = scoreboard.createTeam(faction);
                setTeamPrefix(team, faction, player); // Передаем игрока
            }

            removePlayerFromCurrentTeam(player);
            addPlayerToTeam(scoreboard, player, faction);

        } catch (Exception e) {
            System.err.println("Ошибка при добавлении игрока в команду фракции:");
            e.printStackTrace();
        }
    }

    private static String normalizeFactionName(String faction) {
        if (faction.equalsIgnoreCase("свет")) return "Свет";
        if (faction.equalsIgnoreCase("тьма")) return "Тьма";
        if (faction.equalsIgnoreCase("admin")) return "admin";
        if (faction.equalsIgnoreCase("наемник")) return "Наёмник";
        return faction;
    }

    private static void setTeamPrefix(ScorePlayerTeam team, String faction, EntityPlayerMP player)
            throws Exception {
        Method setPrefixMethod = ScorePlayerTeam.class.getDeclaredMethod(
                "func_96666_b", String.class
        );
        setPrefixMethod.setAccessible(true);

        String username = player.getCommandSenderName();
        String prefix = ChatUtils.getPlayerPrefix(username);
        String suffix = ChatUtils.getPlayerSuffix(username);

        String fullPrefix;
        switch (faction) {
            case "Свет":
                fullPrefix = "§e[Свет] " + prefix;
                break;
            case "Тьма":
                fullPrefix = "§8[Тьма] " + prefix;
                break;
            case "admin":
                fullPrefix = "§4[Admin] " + prefix;
                break;
            case "Наёмник":
                fullPrefix = "§5[Наёмник] " + prefix;
                break;
            default:
                fullPrefix = "[" + faction + "] " + prefix;
                break;
        }

        setPrefixMethod.invoke(team, fullPrefix);

        // Обновляем суффикс команды
        Method setSuffixMethod = ScorePlayerTeam.class.getDeclaredMethod(
                "func_96662_c", String.class
        );
        setSuffixMethod.setAccessible(true);
        setSuffixMethod.invoke(team, suffix);
    }

    private static void addPlayerToTeam(Scoreboard scoreboard, EntityPlayerMP player, String faction)
            throws Exception {
        Method addToTeamMethod = Scoreboard.class.getDeclaredMethod(
                "func_151392_a", String.class, String.class
        );
        addToTeamMethod.setAccessible(true);
        addToTeamMethod.invoke(scoreboard, player.getCommandSenderName(), faction);
    }

    public static void removePlayerFromCurrentTeam(EntityPlayerMP player) {
        try {
            Scoreboard scoreboard = getScoreboard();
            if (scoreboard == null) return;

            ScorePlayerTeam team = scoreboard.getPlayersTeam(player.getCommandSenderName());
            if (team != null) {
                scoreboard.removePlayerFromTeam(player.getCommandSenderName(), team);
            }
        } catch (Exception e) {
            System.err.println("Ошибка при удалении игрока из команды:");
            e.printStackTrace();
        }
    }

    private static Scoreboard getScoreboard() {
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null) return null;

        WorldServer world = server.worldServerForDimension(0);
        if (world == null) return null;

        return world.getScoreboard();
    }
}