package org.tsl.tSLride.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tsl.tSLride.TSLride;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TSLrideCommand implements CommandExecutor, TabCompleter {
    private final TSLride plugin;

    public TSLrideCommand(TSLride plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "toggle":
                return handleToggle(sender);
            case "reload":
                return handleReload(sender);
            case "help":
                showHelp(sender);
                return true;
            default:
                sender.sendMessage(plugin.getConfigManager().getMessageWithPrefix("invalid-command"));
                return true;
        }
    }

    private boolean handleToggle(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c此命令只能由玩家执行！");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("tslride.toggle")) {
            player.sendMessage(plugin.getConfigManager().getMessageWithPrefix("no-permission"));
            return true;
        }

        boolean newState = plugin.getPlayerDataManager().toggleRideEnabled(player);

        if (newState) {
            player.sendMessage(plugin.getConfigManager().getMessageWithPrefix("toggle-enabled"));
        } else {
            player.sendMessage(plugin.getConfigManager().getMessageWithPrefix("toggle-disabled"));
        }

        return true;
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("tslride.reload")) {
            sender.sendMessage(plugin.getConfigManager().getMessageWithPrefix("no-permission"));
            return true;
        }

        try {
            plugin.getConfigManager().reloadConfig();
            sender.sendMessage(plugin.getConfigManager().getMessageWithPrefix("config-reloaded"));

            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("配置文件已被 " + sender.getName() + " 重新加载");
            }
        } catch (Exception e) {
            sender.sendMessage("§c重新加载配置文件时发生错误: " + e.getMessage());
            plugin.getLogger().severe("重新加载配置文件失败: " + e.getMessage());
        }

        return true;
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(plugin.getConfigManager().getMessage("help-header"));
        sender.sendMessage(plugin.getConfigManager().getMessage("help-toggle"));

        if (sender.hasPermission("tslride.reload")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("help-reload"));
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("toggle", "help");

            if (sender.hasPermission("tslride.reload")) {
                subCommands = Arrays.asList("toggle", "reload", "help");
            }

            String input = args[0].toLowerCase();
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(input)) {
                    completions.add(subCommand);
                }
            }
        }

        return completions;
    }
}
