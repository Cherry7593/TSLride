package org.tsl.tSLride.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.tsl.tSLride.TSLride;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigManager {
    private final TSLride plugin;
    private FileConfiguration config;

    // 配置缓存
    private boolean debug;
    private boolean defaultEnabled;
    private Set<EntityType> blacklistedEntities;

    public ConfigManager(TSLride plugin) {
        this.plugin = plugin;
        this.blacklistedEntities = new HashSet<>();
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();

        // 加载配置项
        debug = config.getBoolean("debug", false);
        defaultEnabled = config.getBoolean("default-enabled", true);

        // 加载黑名单
        loadBlacklist();

        if (debug) {
            plugin.getLogger().info("配置文件已加载，调试模式已开启");
        }
    }

    private void loadBlacklist() {
        blacklistedEntities.clear();
        List<String> blacklistConfig = config.getStringList("blacklist");

        for (String entityName : blacklistConfig) {
            try {
                EntityType entityType = EntityType.valueOf(entityName.toUpperCase());
                blacklistedEntities.add(entityType);
                if (debug) {
                    plugin.getLogger().info("已添加实体到黑名单: " + entityType.name());
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("无效的实体类型: " + entityName);
            }
        }
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isDefaultEnabled() {
        return defaultEnabled;
    }

    public boolean isBlacklisted(EntityType entityType) {
        return blacklistedEntities.contains(entityType);
    }

    public String getMessage(String key) {
        String message = config.getString("messages." + key, "消息未找到: " + key);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getMessageWithPrefix(String key) {
        return getMessage("prefix") + getMessage(key);
    }

    public void reloadConfig() {
        loadConfig();
    }
}
