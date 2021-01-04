package com.github.fratikak.l4d_gamepl;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;

public class Login implements Listener {

    private final L4D_gamepl pl;


    //コンストラクタ
    public Login(L4D_gamepl pl) {
        this.pl = pl;
    }

    //op権限を持っているかの判定
    @EventHandler
    public void loginEvent(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        if (player.isOp()) {
            pl.getLogger().info(player.getDisplayName() + "はop権限を持っています");
        }
    }

    //サーバーjoin時にサバイバルモードにしてロビーアイテムを与える
    @EventHandler
    public void joinEvent(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        Inventory inventory = player.getInventory();
        World world = player.getWorld();
        Location location = world.getSpawnLocation(); //プレイヤーのいるワールドのスポーン地点を取得する

        event.setJoinMessage(ChatColor.AQUA + player.getDisplayName() + "がゲームに参加しました！");

        //ゲームモードを固定。サバイバル
        if (player.getGameMode() != GameMode.SURVIVAL) {
            if (!player.isOp()){
                player.setGameMode(GameMode.SURVIVAL);
            }
        }

        inventory.clear(); //インベントリ内を削除

        pl.giveLobbyItem(inventory); //ロビーアイテム追加

        player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN); //スポーン地点固定

    }

}
