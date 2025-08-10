package com.example.examplemod;

import com.example.examplemod.command.CommandChat;
import com.example.examplemod.command.CommandChooseFaction;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = "localchat", name = "Local Chat Mod", version = "1.2")
public class ExampleMod {

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.init(event);
        MinecraftForge.EVENT_BUS.register(new ChatHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerEventHandler()); // Добавлено
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandChat());
        event.registerServerCommand(new CommandChooseFaction()); // Добавлено
    }
}