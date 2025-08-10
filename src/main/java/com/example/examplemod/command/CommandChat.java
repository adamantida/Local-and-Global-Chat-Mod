package com.example.examplemod.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CommandChat extends CommandBase {
    @Override
    public String getCommandName() {
        return "chat";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/chat <local|global|reload|prefix|suffix|setfaction> [аргументы] - Управление чатом";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        String subCommand = args[0].toLowerCase();
        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, subArgs.length);

        switch (subCommand) {
            case "local":
                new CommandLocal().processCommand(sender, subArgs);
                break;
            case "global":
                new CommandGlobal().processCommand(sender, subArgs);
                break;
            case "reload":
                new CommandReload().processCommand(sender, subArgs);
                break;
            case "prefix":
                new CommandSetPrefix().processCommand(sender, subArgs);
                break;
            case "suffix":
                new CommandSetSuffix().processCommand(sender, subArgs);
                break;
            case "setfaction":
                new CommandSetFaction().processCommand(sender, subArgs);
                break;
            default:
                sendUsage(sender);
                break;
        }
    }

    private void sendUsage(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText("§cИспользование:"));
        sender.addChatMessage(new ChatComponentText("§6/chat local <сообщение> §f- Локальный чат"));
        sender.addChatMessage(new ChatComponentText("§6/chat global <сообщение> §f- Глобальный чат"));
        sender.addChatMessage(new ChatComponentText("§6/chat reload §f- Перезагрузить конфиг"));
        sender.addChatMessage(new ChatComponentText("§6/chat prefix <игрок> <префикс> §f- Установить префикс"));
        sender.addChatMessage(new ChatComponentText("§6/chat suffix <игрок> <суффикс> §f- Установить суффикс"));
        sender.addChatMessage(new ChatComponentText("§6/chat setfaction <игрок> <Свет|Тьма> §f- Установить фракцию (операторы)"));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}