package com.github.fratikak.l4d_gamepl;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;

public class PlayerEvent implements Listener {

    private final L4D_gamepl pl;
    private static Location deathLocation;

    //コンストラクタ
    public PlayerEvent(L4D_gamepl pl) {
        this.pl = pl;
    }

    /**
     * プレイヤー関連のイベントを記述する
     *
     * @author FratikaK
     */

    //プレイヤーがログアウトした場合の処理
    @EventHandler
    public void logoutPlayer(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(ChatColor.AQUA + player.getDisplayName() + "がログアウトしました");
        pl.getLogger().info(ChatColor.AQUA + player.getDisplayName() + "がログアウトしました");
        if (L4D_gamepl.isGame()) {
            //リストから削除
            L4D_gamepl.getPlayerList().remove(player);
            L4D_gamepl.getDeathPlayer().remove(player);

            pl.getLogger().info("playerList: " + L4D_gamepl.getPlayerList());
            pl.getLogger().info("deathList: " + L4D_gamepl.getDeathPlayer());

            //ゲームプレイヤーが全員いなくなった場合、ゲームを終了
            if (L4D_gamepl.getPlayerList().isEmpty()) {
                new Stop(pl).runTaskTimer(pl,0, 20);
            }
        }
    }

//    /**
//     * アイテムドロップ（アイテムを捨てる）を禁止。0,1番目のスロットのみ
//     *
//     * @param event
//     */
//    @EventHandler
//    public void onDropItems(PlayerDropItemEvent event) {
//
//        Player player = event.getPlayer();
//
//        if (player.getInventory().getItem(0) != null &&
//                player.getInventory().getItem(1) != null) {
//
//            player.sendMessage(ChatColor.RED + "武器のスロットを弄ることはできません");
//            event.setCancelled(true);
//        }
//    }


//    /**
//     * 武器スロットを弄れないようにする
//     *
//     * @param event
//     */
//    @EventHandler
//    public void setWeaponInventory(InventoryClickEvent event) {
//        Player player = (Player) event.getWhoClicked();
//
//        if (player.getInventory().getItem(0) != null && player.getInventory().getItem(1) != null) {
//            player.sendMessage(ChatColor.RED + "武器スロットを弄ることはできません");
//            event.setCancelled(true);
//        }
//    }

//    /**
//     * 同様に武器スロットを弄れないようにする
//     * @param event
//     */
//    @EventHandler
//    public void setCreativeInventory(InventoryCreativeEvent event) {
//        Player player = (Player) event.getCursor();
//
//        if (event.getHotbarButton() == 0 && event.getHotbarButton() == 1) {
//            event.setCancelled(true);
//            player.sendMessage(ChatColor.RED + "武器スロットを弄ることは出来ません！");
//        }
//    }

    //水に触れると死亡
    @EventHandler
    public void waterDeath(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        if (L4D_gamepl.isGame() && player.getGameMode() == GameMode.SURVIVAL) {
            if (player.getLocation().getBlock().getType() == Material.WATER) {
                player.setHealth(0);
                Bukkit.getLogger().info(player.getDisplayName() + "がwaterdeathで死亡しました");
            }
        }
    }

    //道具、武器の耐久度減少を禁止
    @EventHandler
    public void setItemDamege(PlayerItemDamageEvent event) {
        event.setCancelled(true);
    }

    //空腹度増減を禁止
    @EventHandler
    public void setFoodLevel(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }
}
