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

public class CommandSetSuffix extends CommandBase {
    @Override
    public String getCommandName() {
        return "suffix";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/chat suffix <игрок> <суффикс> - Установить суффикс для игрока";
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
        StringBuilder suffixBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) suffixBuilder.append(" ");
            suffixBuilder.append(args[i]);
        }
        String suffix = suffixBuilder.toString();

        // Сохраняем в конфиг
        Configuration config = new Configuration(ConfigHandler.configFile);

        try {
            config.load();
            Property suffixProp = config.get("suffixes", "PlayerSuffixes", new String[0]);
            List<String> entries = new ArrayList<>(Arrays.asList(suffixProp.getStringList()));

            // Удаляем старые записи
            entries.removeIf(entry -> entry.startsWith(username + "="));

            // Добавляем новую
            entries.add(username + "=" + suffix);
            suffixProp.set(entries.toArray(new String[0]));

            ConfigHandler.suffixMap.put(username, suffix);
            config.save();

            // Обновляем команду игрока
            EntityPlayerMP targetPlayer = MinecraftServer.getServer().getConfigurationManager().func_152612_a(username);
            if (targetPlayer != null) {
                String faction = ConfigHandler.factionMap.get(username);
                TeamHandler.addPlayerToFactionTeam(targetPlayer, faction != null ? faction : "");
            }

            sender.addChatMessage(new ChatComponentText(
                    "§aСуффикс для §e" + username + "§a установлен: §r" + suffix
            ));
        } catch (Exception e) {
            sender.addChatMessage(new ChatComponentText("§cОшибка сохранения конфига!"));
            e.printStackTrace();
        }
    }
}