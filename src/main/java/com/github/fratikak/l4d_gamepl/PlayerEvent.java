package com.github.fratikak.l4d_gamepl;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;

public class PlayerEvent implements Listener {

    private final L4D_gamepl pl;
    private static Location deathlocation;

    //コンストラクタ
    public PlayerEvent(L4D_gamepl pl) {
        this.pl = pl;
    }

    /**
     * プレイヤー関連のイベントを記述する
     * @author FratikaK
     */

    @EventHandler
    public void changeGamerule(PlayerDeathEvent event){

        /**
         * プレイヤーの死亡地点をクローンして、リスポーンイベントに渡す
         * ゲームモードをクリエイティブに変更する
         */

        Player player = event.getEntity();
        deathlocation = player.getLocation().clone();

        player.setGameMode(GameMode.CREATIVE);

    }

    @EventHandler
    public void setSpectator(PlayerRespawnEvent event){

        /**
         * リスポーン位置をデスイベントから受け取り、設定
         * ロビーアイテムを付与
         */

        Player player = event.getPlayer();

        Inventory inventory = player.getInventory();

        event.setRespawnLocation(deathlocation);

        inventory.clear();

        pl.giveLobbyItem(player.getInventory());

    }

}
