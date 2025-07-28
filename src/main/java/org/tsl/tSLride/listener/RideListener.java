package org.tsl.tSLride.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.tsl.tSLride.TSLride;

public class RideListener implements Listener {
    private final TSLride plugin;

    public RideListener(TSLride plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        // 检查玩家是否有基本权限
        if (!player.hasPermission("tslride.use")) {
            return;
        }

        // 检查玩家是否开启了骑乘功能
        if (!plugin.getPlayerDataManager().isRideEnabled(player)) {
            return;
        }

        // 检查是否为生物实体（排除物品框等）
        if (!entity.getType().isAlive()) {
            return;
        }

        // 检查黑名单
        EntityType entityType = entity.getType();
        if (plugin.getConfigManager().isBlacklisted(entityType)) {
            // 检查是否有绕过黑名单的权限
            if (!player.hasPermission("tslride.bypass.blacklist")) {
                player.sendMessage(plugin.getConfigManager().getMessageWithPrefix("blacklist-entity"));
                return;
            }
        }

        // 检查实体是否已经有乘客
        if (!entity.getPassengers().isEmpty()) {
            return;
        }

        // 检查玩家是否已经在骑乘其他实体
        if (player.getVehicle() != null) {
            return;
        }

        // 使用 Folia 的区域调度器执行骑乘操作
        entity.getScheduler().run(plugin, (task) -> {
            try {
                entity.addPassenger(player);

                if (plugin.getConfigManager().isDebug()) {
                    plugin.getLogger().info("玩家 " + player.getName() + " 骑乘了 " + entityType.name());
                }
            } catch (Exception e) {
                if (plugin.getConfigManager().isDebug()) {
                    plugin.getLogger().warning("骑乘失败: " + e.getMessage());
                }
            }
        }, null);

        // 取消默认的交互行为（防止打开GUI等）
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // 玩家离线时清理数据
        plugin.getPlayerDataManager().removePlayerData(event.getPlayer());
    }
}
