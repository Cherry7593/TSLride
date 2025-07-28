package org.tsl.tSLride.manager;

import org.bukkit.entity.Player;
import org.tsl.tSLride.TSLride;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    private final TSLride plugin;
    private final Map<UUID, Boolean> playerToggleStates;

    public PlayerDataManager(TSLride plugin) {
        this.plugin = plugin;
        this.playerToggleStates = new HashMap<>();
    }

    /**
     * 获取玩家的骑乘开关状态
     * @param player 玩家
     * @return 是否开启骑乘功能
     */
    public boolean isRideEnabled(Player player) {
        UUID uuid = player.getUniqueId();
        return playerToggleStates.getOrDefault(uuid, plugin.getConfigManager().isDefaultEnabled());
    }

    /**
     * 切换玩家的骑乘开关状态
     * @param player 玩家
     * @return 切换后的状态
     */
    public boolean toggleRideEnabled(Player player) {
        UUID uuid = player.getUniqueId();
        boolean currentState = isRideEnabled(player);
        boolean newState = !currentState;
        playerToggleStates.put(uuid, newState);

        if (plugin.getConfigManager().isDebug()) {
            plugin.getLogger().info("玩家 " + player.getName() + " 的骑乘状态已切换为: " + newState);
        }

        return newState;
    }

    /**
     * 设置玩家的骑乘开关状态
     * @param player 玩家
     * @param enabled 是否开启
     */
    public void setRideEnabled(Player player, boolean enabled) {
        UUID uuid = player.getUniqueId();
        playerToggleStates.put(uuid, enabled);
    }

    /**
     * 移除玩家数据（玩家离线时调用）
     * @param player 玩家
     */
    public void removePlayerData(Player player) {
        playerToggleStates.remove(player.getUniqueId());
    }

    /**
     * 清空所有玩家数据
     */
    public void clearAllData() {
        playerToggleStates.clear();
    }

    /**
     * 获取当前在线玩家中开启骑乘功能的数量
     * @return 开启骑乘功能的玩家数量
     */
    public int getEnabledPlayersCount() {
        return (int) playerToggleStates.values().stream().filter(enabled -> enabled).count();
    }
}
