package com.github.fratikak.l4d_gamepl.listener;

import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import com.github.fratikak.l4d_gamepl.Stop;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class PlayerEvent implements Listener {

    private final L4D_gamepl pl;

    //コンストラクタ
    public PlayerEvent(L4D_gamepl pl) {
        this.pl = pl;
    }

    /**
     * プレイヤー関連のイベントを記述する
     *
     * @author FratikaK
     */

    /**
     * プレイヤーがログアウトした場合の処理
     * ゲーム中にログアウトする場合、リストから削除する
     *
     * @param event
     */
    @EventHandler
    public void logoutPlayer(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(ChatColor.AQUA + player.getDisplayName() + "がログアウトしました");
        pl.getLogger().info(ChatColor.AQUA + player.getDisplayName() + "がログアウトしました");
        if (L4D_gamepl.isGame()) {
            //リストから削除
            L4D_gamepl.getPlayerList().remove(player);
            L4D_gamepl.getSurvivorList().remove(player);
            L4D_gamepl.getDeathPlayerList().remove(player);

            pl.getLogger().info("survivorList: " + L4D_gamepl.getSurvivorList());
            pl.getLogger().info("deathList: " + L4D_gamepl.getDeathPlayerList());

            //ゲームプレイヤーが全員いなくなった場合、ゲームを終了
            if (L4D_gamepl.getSurvivorList().isEmpty()) {
                new Stop(pl).runTaskTimer(pl, 0, 20);
            }
        }
    }

    /**
     * 水に触れると死亡する処理
     *
     * @param event
     */
    @EventHandler
    public void waterDeath(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        if (L4D_gamepl.isGame() && player.getGameMode() == GameMode.SURVIVAL) {
            if (player.getLocation().getBlock().getType() == Material.WATER) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER,20,10000,true));
            }
        }
    }

    /**
     * プレイヤーに特定のポーション効果を付与する時、それをキャンセルする
     * 主にフラッシュバンによるフレンドリーファイア対策
     *
     * @param event
     */
    @EventHandler
    public void noPlayerEffect(EntityPotionEffectEvent event) {

        Entity entity = event.getEntity();

        if (entity.getType() == EntityType.PLAYER) {

            if (event.getModifiedType() == PotionEffectType.SLOW) {
                event.setCancelled(true);
            } else if (event.getModifiedType() == PotionEffectType.BAD_OMEN) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * 道具類の耐久減少をキャンセルする
     *
     * @param event
     */
    @EventHandler
    public void setItemDamege(PlayerItemDamageEvent event) {
        event.setCancelled(true);
    }

    /**
     * 空腹度の増減をキャンセルする
     *
     * @param event
     */
    @EventHandler
    public void setFoodLevel(FoodLevelChangeEvent event) {
        event.setCancelled(true);
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

