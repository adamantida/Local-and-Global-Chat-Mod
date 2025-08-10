package com.example.examplemod.command;

import com.example.examplemod.ConfigHandler;
import com.example.examplemod.TeamHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandSetFaction extends CommandBase {
    @Override
    public String getCommandName() {
        return "setfaction";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/chat setfaction <игрок> <Свет|Тьма> - Установить фракцию (только для операторов)";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return !(sender instanceof EntityPlayerMP) || MinecraftServer.getServer().getConfigurationManager().func_152596_g(((EntityPlayerMP) sender).getGameProfile());
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!canCommandSenderUseCommand(sender)) {
            sender.addChatMessage(new ChatComponentText("§cУ вас нет прав на эту команду!"));
            return;
        }

        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText("§cНедостаточно аргументов! Использование: " + getCommandUsage(sender)));
            return;
        }

        String username = args[0];
        String faction = args[1].toLowerCase(); // Приводим к нижнему регистру для проверки

        // Проверяем, что фракция только "Свет" или "Тьма"
        if (!faction.equalsIgnoreCase("свет") && !faction.equalsIgnoreCase("тьма") && !faction.equalsIgnoreCase("admin") && !faction.equalsIgnoreCase("наемник")) {
            sender.addChatMessage(new ChatComponentText("§cНедопустимая фракция! Допустимые значения: §eСвет§c или §eТьма§r"));
            return;
        }

        // Возвращаем правильный регистр для отображения
        if (faction.equals("свет")) faction = "Свет";
        if (faction.equals("тьма")) faction = "Тьма";
        if (faction.equals("admin")) faction = "admin";
        if (faction.equals("наемник")) faction = "Наёмник";

        // Сохраняем в конфиг
        Configuration config = new Configuration(ConfigHandler.configFile);
        try {
            config.load();
            Property factionProp = config.get("factions", "PlayerFactions", new String[0]);
            List<String> entries = new ArrayList<>(Arrays.asList(factionProp.getStringList()));

            // Удаляем старые записи
            entries.removeIf(entry -> entry.startsWith(username + "="));

            // Добавляем новую
            entries.add(username + "=" + faction);
            factionProp.set(entries.toArray(new String[0]));

            ConfigHandler.factionMap.put(username, faction);
            config.save();

            EntityPlayerMP targetPlayer = MinecraftServer.getServer().getConfigurationManager().func_152612_a(username);
            if (targetPlayer != null) {
                TeamHandler.addPlayerToFactionTeam(targetPlayer, faction);
            }

            sender.addChatMessage(new ChatComponentText(
                    "§aФракция для §e" + username + "§a установлена: §r" + faction
            ));
        } catch (Exception e) {
            sender.addChatMessage(new ChatComponentText("§cОшибка сохранения конфига!"));
            e.printStackTrace();
        }
    }
}