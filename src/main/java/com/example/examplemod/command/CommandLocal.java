package com.example.examplemod.command;

import com.example.examplemod.ChatHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class CommandLocal extends CommandBase {
    @Override
    public String getCommandName() {
        return "local";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/chat local <сообщение> - Отправить сообщение в локальный чат";
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
        new ChatHandler().sendLocalMessage(player, message, player.worldObj);
    }
}