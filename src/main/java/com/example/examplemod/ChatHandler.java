// ChatHandler.java
package com.example.examplemod;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.ServerChatEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatHandler {

    @SubscribeEvent
    public void onChat(ServerChatEvent event) {
        event.setCanceled(true);
        String message = event.message;
        EntityPlayerMP player = event.player;
        World world = player.worldObj;

        if (message.startsWith(ConfigHandler.globalPrefix)) {
            String globalMsg = message.substring(ConfigHandler.globalPrefix.length()).trim();
            if (!globalMsg.isEmpty()) {
                sendFormattedMessage(player, globalMsg, true);
            }
            return;
        }

        sendLocalMessage(player, message, world);
    }

    public void sendLocalMessage(EntityPlayerMP sender, String message, World world) {
        if (message.isEmpty()) return;

        IChatComponent component = formatMessage(ConfigHandler.localFormat, sender, message);

        List players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
        for (Object obj : players) {
            EntityPlayerMP player = (EntityPlayerMP) obj;
            if (player.worldObj == world &&
                    sender.getDistanceSqToEntity(player) <= ConfigHandler.localRadius * ConfigHandler.localRadius) {
                player.addChatMessage(component);
            }
        }
    }

    public void sendFormattedMessage(EntityPlayerMP player, String message, boolean isGlobal) {
        if (message.isEmpty()) return;

        String format = isGlobal ? ConfigHandler.globalFormat : ConfigHandler.localFormat;
        IChatComponent component = formatMessage(format, player, message);
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(component);
    }

    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "\\{player\\}|\\{message\\}|\\{reset\\}|(?i)&[0-9A-FK-OR]"
    );

    private IChatComponent formatMessage(String format, EntityPlayerMP player, String messageText) {
        // 1) Подготовка динамических частей
        String name    = player.getCommandSenderName();
        String prefix  = ChatUtils.getPlayerPrefix(name);
        String suffix  = ChatUtils.getPlayerSuffix(name);
        String faction = ChatUtils.getPlayerFaction(name);
        String factionDisplay = faction.isEmpty() ? "" : "[" + faction + "] ";
        String playerText = factionDisplay + prefix + name + suffix;

        // 2) Собираем корневой компонент и стартовый стиль
        IChatComponent root = new ChatComponentText("");
        ChatStyle currentStyle = new ChatStyle();

        // 3) Проходим по всему формату через Matcher
        Matcher m = TOKEN_PATTERN.matcher(format);
        int lastEnd = 0;
        while (m.find()) {
            // 3a) всё, что между последним совпадением и этим — «чистый» статический текст
            if (m.start() > lastEnd) {
                appendText(root, format.substring(lastEnd, m.start()), currentStyle);
            }

            String token = m.group();
            if ("{player}".equals(token)) {
                appendText(root, playerText, currentStyle);
            } else if ("{message}".equals(token)) {
                // для текста сообщения — свой цвет
                ChatStyle msgStyle = currentStyle.createShallowCopy().setColor(EnumChatFormatting.GRAY);
                appendText(root, messageText, msgStyle);
                currentStyle = msgStyle;  // сохраняем, если после сообщения идут &‑коды
            } else if ("{reset}".equals(token)) {
                currentStyle = new ChatStyle();
            } else if (token.charAt(0) == '&') {
                // цветовой/форматный код
                char code = Character.toLowerCase(token.charAt(1));
                EnumChatFormatting fmt = getByCode(code);
                if (fmt != null) {
                    if (fmt == EnumChatFormatting.RESET) {
                        currentStyle = new ChatStyle();
                    } else if (fmt.isColor()) {
                        currentStyle.setColor(fmt)
                                .setBold(false).setItalic(false)
                                .setUnderlined(false).setStrikethrough(false)
                                .setObfuscated(false);
                    } else {
                        // стили: bold/italic/underline/...
                        switch (fmt) {
                            case BOLD:          currentStyle.setBold(true);         break;
                            case ITALIC:        currentStyle.setItalic(true);       break;
                            case UNDERLINE:     currentStyle.setUnderlined(true);   break;
                            case STRIKETHROUGH: currentStyle.setStrikethrough(true);break;
                            case OBFUSCATED:    currentStyle.setObfuscated(true);   break;
                            default: break;
                        }
                    }
                }
            }
            lastEnd = m.end();
        }

        // 4) хвост после последнего токена
        if (lastEnd < format.length()) {
            appendText(root, format.substring(lastEnd), currentStyle);
        }

        return root;
    }

    /** Вспомогательный: создаёт новый ChatComponentText с указанным стилем и добавляет в root */
    private void appendText(IChatComponent root, String text, ChatStyle style) {
        if (text.isEmpty()) return;
        ChatComponentText part = new ChatComponentText(text);
        part.setChatStyle(style.createShallowCopy());
        root.appendSibling(part);
    }

    /** Переводим символ к EnumChatFormatting */
    private EnumChatFormatting getByCode(char code) {
        for (EnumChatFormatting f : EnumChatFormatting.values()) {
            if (f.getFormattingCode() == code) return f;
        }
        return null;
    }
    private ChatStyle updateChatStyle(ChatStyle style, EnumChatFormatting format) {
        ChatStyle newStyle = style.createShallowCopy();
        if (format == EnumChatFormatting.RESET) {
            return new ChatStyle();
        }

        if (format.isColor()) {
            newStyle.setColor(format);
            // Сбрасываем дополнительные стили при установке цвета
            newStyle.setBold(false);
            newStyle.setItalic(false);
            newStyle.setUnderlined(false);
            newStyle.setStrikethrough(false);
            newStyle.setObfuscated(false);
        } else {
            switch (format) {
                case BOLD: newStyle.setBold(true); break;
                case ITALIC: newStyle.setItalic(true); break;
                case UNDERLINE: newStyle.setUnderlined(true); break;
                case STRIKETHROUGH: newStyle.setStrikethrough(true); break;
                case OBFUSCATED: newStyle.setObfuscated(true); break;
                default: break;
            }
        }

        return newStyle;
    }

    // Вспомогательный метод для получения форматирования по символу
    private EnumChatFormatting getFormattingByChar(char code) {
        // Перебираем все возможные значения форматирования
        for (EnumChatFormatting formatting : EnumChatFormatting.values()) {
            // Используем публичный метод getFormattingCode() вместо доступа к приватному полю
            if (formatting.getFormattingCode() == code) {
                return formatting;
            }
        }
        return null;
    }
}