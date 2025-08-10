package com.example.examplemod.command;

import com.example.examplemod.ConfigHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

public class CommandReload extends CommandBase {
    @Override
    public String getCommandName() {
        return "reload";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/chat reload - Перезагрузить конфиг";
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

        ConfigHandler.loadConfig();
        sender.addChatMessage(new ChatComponentText("§aКонфиг чата перезагружен!"));
    }
}