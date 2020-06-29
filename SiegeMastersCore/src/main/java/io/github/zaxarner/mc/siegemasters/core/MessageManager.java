package io.github.zaxarner.mc.siegemasters.core;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 5/8/2020.
 */
public class MessageManager {

    public enum Message {
        VERSION, INVALID_ARGUMENT
    }

    private SiegeMastersCore plugin;

    private String prefix = "";
    private Map<Message, String> messages = new HashMap<>();

    public MessageManager(SiegeMastersCore plugin) {
        this.plugin = plugin;

        load();
    }

    public void load() {
        messages.clear();
        String prefix = plugin.getConfig().getString("message-manager.prefix");
        if(prefix != null) {
            this.prefix = ChatColor.translateAlternateColorCodes('&', prefix);
        }

        ConfigurationSection messageSection = plugin.getConfig().getConfigurationSection("message-manager.messages");
        if(messageSection != null) {
            for(Message key : Message.values()) {
                String configName = key.name().toLowerCase().replace("_", "-");
                Object message = messageSection.get(configName);
                if(message instanceof String) {
                    messages.put(key, ChatColor.translateAlternateColorCodes('&', (String) message));
                }
            }
        }
    }

    @NotNull
    private Map<String, String> getPlaceholders(@Nullable Player player) {
        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{prefix}", prefix);
        placeholders.put("{version}", plugin.getDescription().getVersion());

        if(player != null) {
            placeholders.put("{player}", player.getName());
            placeholders.put("{display-name}", player.getDisplayName());
        }

        return placeholders;
    }

    @NotNull
    private String replacePlaceholders(@NotNull String string, @Nullable Player player) {
        Map<String, String> placeholders = getPlaceholders(player);

        for(String placeholder : placeholders.keySet()) {
            String value = placeholders.get(placeholder);
            string = string.replace(placeholder, value);
        }

        return string;
    }

    public void message(@NotNull CommandSender receiver, @NotNull Message message) {
        receiver.sendMessage(replacePlaceholders(messages.get(message), null));
    }

    public void message(@NotNull CommandSender receiver, @NotNull Message message, @NotNull Player target) {
        receiver.sendMessage(replacePlaceholders(messages.get(message), target));
    }

    public void message(@NotNull CommandSender receiver, @NotNull String message) {
        receiver.sendMessage(replacePlaceholders(message, null));
    }

    public void message(@NotNull CommandSender receiver, @NotNull String message, @NotNull Player target) {
        receiver.sendMessage(replacePlaceholders(message, target));
    }
}
