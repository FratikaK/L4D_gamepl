package com.github.fratikak.l4d_gamepl;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
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

    @EventHandler
    public void changeGamerule(PlayerDeathEvent event) {

        /*
         * プレイヤーの死亡地点をクローンして、リスポーンイベントに渡す
         * ゲームモードをスペクテイターに変更する
         *
         * ゲームプレイヤーが全員死亡すれば初期スポーンへ返す
         */

        Player player = event.getEntity();
        String playername = player.getDisplayName();
        deathLocation = player.getLocation().clone();

        L4D_gamepl.getPlayerList().remove(playername);
        player.sendTitle(ChatColor.RED + "あなたは死亡しました", "", 5, 10, 5);
        player.setGameMode(GameMode.SPECTATOR);

        if (L4D_gamepl.getPlayerList().isEmpty()) {
            new Stop(pl).stopGame();
        }

    }

    @EventHandler
    public void setSpectator(PlayerRespawnEvent event) {

        /*
         * リスポーン位置をデスイベントから受け取り、設定
         * ロビーアイテムを付与
         */

        if (L4D_gamepl.getPlayerList().isEmpty()) {

            return;
        }

        Player player = event.getPlayer();

        Inventory inventory = player.getInventory();

        event.setRespawnLocation(deathLocation);

        //インベントリ処理
        pl.giveLobbyItem(inventory);

    }

    //クリエイティブ時にアイテム取り出しを禁止
    @EventHandler
    public void onCreativeItems(InventoryCreativeEvent event) {
        Player player = (Player) event.getWhoClicked();

        //opじゃなければ無効化
        if (!player.isOp()) {
            event.setCancelled(true);
        }
    }

    //アイテムドロップ（アイテムを捨てる）を禁止
    @EventHandler
    public void onDropItems(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        //opじゃなければ無効化
        if (!player.isOp()) {
            event.setCancelled(true);
        }
    }

//    @EventHandler
//    public void waterDeath(EntityToggleSwimEvent event) {
//
//        Entity entity = event.getEntity();
//
//        if (L4D_gamepl.isGame()) {
//            if (entity instanceof Player){
//                Player player = (Player) entity;
//                if (player.isInWater()) {
//                    player.damage(50);
//                    pl.getServer().broadcastMessage(player.getDisplayName() + "は溺れてしまった");
//                }
//            }
//
//        }
//    }

}
