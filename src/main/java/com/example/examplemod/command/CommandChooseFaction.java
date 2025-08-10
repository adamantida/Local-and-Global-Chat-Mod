package com.example.examplemod.command;

import com.example.examplemod.ConfigHandler;
import com.example.examplemod.TeamHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandChooseFaction extends CommandBase {
    @Override
    public String getCommandName() {
        return "faction";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/faction <Свет|Тьма> - Выбрать фракцию";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayerMP;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) {
            sender.addChatMessage(new ChatComponentText("§cКоманда только для игроков!"));
            return;
        }

        if (args.length < 1) {
            sender.addChatMessage(new ChatComponentText("§cНе указана фракция! Использование: " + getCommandUsage(sender)));
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) sender;
        String username = player.getCommandSenderName();
        String faction = args[0].toLowerCase(); // Приводим к нижнему регистру для проверки

        // Проверяем, что фракция только "Свет" или "Тьма"
        if (!faction.equalsIgnoreCase("свет") && !faction.equalsIgnoreCase("тьма")) {
            sender.addChatMessage(new ChatComponentText("§cНедопустимая фракция! Допустимые значения: §eСвет§c или §eТьма"));
            return;
        }

        // Возвращаем правильный регистр для отображения
        faction = faction.equalsIgnoreCase("свет") ? "Свет" : "Тьма";

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

            TeamHandler.addPlayerToFactionTeam(player, faction);

            sender.addChatMessage(new ChatComponentText(
                    "§aВы выбрали фракцию: §r" + faction
            ));
        } catch (Exception e) {
            sender.addChatMessage(new ChatComponentText("§cОшибка сохранения конфига!"));
            e.printStackTrace();
        }
    }
}