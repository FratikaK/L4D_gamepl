package com.github.fratikak.l4d_gamepl;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

    //ゲームカウント中は動けないようにする
    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        if (L4D_gamepl.isStarting()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void changeGamerule(PlayerDeathEvent event) {

        /*
         * プレイヤーの死亡地点をクローンして、リスポーンイベントに渡す
         * ゲームモードをクリエイティブに変更する
         */

        Player player = event.getEntity();
        String playername = player.getDisplayName();
        deathLocation = player.getLocation().clone();

        L4D_gamepl.getPlayerList().remove(playername);
        player.setGameMode(GameMode.CREATIVE);

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

        Player player = event.getPlayer();

        Inventory inventory = player.getInventory();

        event.setRespawnLocation(deathLocation);

        //透明化処理とインベントリ処理
        player.hidePlayer(pl, player);
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


}
