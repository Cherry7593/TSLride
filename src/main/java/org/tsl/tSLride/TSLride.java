package org.tsl.tSLride;

import org.bukkit.plugin.java.JavaPlugin;
import org.tsl.tSLride.command.TSLrideCommand;
import org.tsl.tSLride.config.ConfigManager;
import org.tsl.tSLride.listener.RideListener;
import org.tsl.tSLride.manager.PlayerDataManager;

public final class TSLride extends JavaPlugin {

    private ConfigManager configManager;
    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        // 初始化配置管理器
        configManager = new ConfigManager(this);

        // 初始化玩家数据管理器
        playerDataManager = new PlayerDataManager(this);

        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new RideListener(this), this);

        // 注册命令
        TSLrideCommand commandExecutor = new TSLrideCommand(this);
        if (getCommand("tslride") != null) {
            getCommand("tslride").setExecutor(commandExecutor);
            getCommand("tslride").setTabCompleter(commandExecutor);
        } else {
            getLogger().severe("无法注册命令 'tslride'，请检查 plugin.yml 配置！");
        }

        getLogger().info("TSLride 插件已启用！支持 Folia 多线程环境");

        if (configManager.isDebug()) {
            getLogger().info("调试模式已开启");
        }
    }

    @Override
    public void onDisable() {
        // 清理玩家数据
        if (playerDataManager != null) {
            playerDataManager.clearAllData();
        }

        getLogger().info("TSLride 插件已禁用！");
    }

    // Getter 方法
    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
}
