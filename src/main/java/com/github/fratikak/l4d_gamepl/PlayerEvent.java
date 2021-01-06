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
import org.bukkit.event.player.PlayerQuitEvent;
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

    //プレイヤーがログアウトした場合の処理
    @EventHandler
    public void logoutPlayer(PlayerQuitEvent event){
        Player player = event.getPlayer();
        event.setQuitMessage(ChatColor.AQUA + player.getDisplayName() + "がログアウトしました");
        pl.getLogger().info(ChatColor.AQUA + player.getDisplayName() + "がログアウトしました");
        if (L4D_gamepl.isGame()){
            //リストから削除
            L4D_gamepl.getPlayerList().remove(player);
            L4D_gamepl.getDeathPlayer().remove(player);

            pl.getLogger().info("playerList: " + L4D_gamepl.getPlayerList());
            pl.getLogger().info("deathList: " + L4D_gamepl.getDeathPlayer());

            //ゲームプレイヤーが全員いなくなった場合、ゲームを終了
            if (L4D_gamepl.getPlayerList().isEmpty()){
                new Stop(pl).stopGame();
            }

        }
    }

    @EventHandler
    public void changeGamerule(PlayerDeathEvent event) {

        /*
         * プレイヤーの死亡地点をクローンして、リスポーンイベントに渡す
         * ゲームモードをスペクテイターに変更する
         *
         * ゲームプレイヤーが全員死亡すれば初期スポーンへ返す
         */

        Player player = event.getEntity();
        deathLocation = player.getLocation().clone();

        L4D_gamepl.getPlayerList().remove(player);
        L4D_gamepl.getDeathPlayer().add(player);
        player.sendTitle(ChatColor.RED + "あなたは死亡しました", "", 5, 10, 5);
        player.setGameMode(GameMode.SPECTATOR);

        if (L4D_gamepl.getPlayerList().isEmpty()) {
            GameWorlds.setStageId(0);
            pl.getLogger().info("getStageId = " + GameWorlds.getStageId());
            new Stop(pl).stopGame();
        }

    }

    @EventHandler
    public void setSpectator(PlayerRespawnEvent event) {

        /*
         * リスポーン位置をデスイベントから受け取り、設定
         * ロビーアイテムを付与
         */

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

    //水に触れると死亡
    @EventHandler
    public void waterDeath(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        if (L4D_gamepl.isGame()) {
           if (player.getLocation().getBlock().getType() == Material.WATER){
               player.setHealth(0);
           }
        }
    }

}
