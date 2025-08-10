package com.example.examplemod.command;

import com.example.examplemod.ChatHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class CommandGlobal extends CommandBase {
    @Override
    public String getCommandName() {
        return "global";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/chat global <сообщение> - Отправить сообщение в глобальный чат";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText("§cНе указано сообщение! Использование: " + getCommandUsage(sender)));
            return;
        }

        if (!(sender instanceof EntityPlayerMP)) {
            sender.addChatMessage(new ChatComponentText("§cКоманда только для игроков!"));
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) sender;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(args[i]);
        }
        String message = sb.toString();
        new ChatHandler().sendFormattedMessage(player, message, true);
    }
}