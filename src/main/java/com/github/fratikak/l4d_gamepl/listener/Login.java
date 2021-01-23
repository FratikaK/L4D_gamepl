package com.github.fratikak.l4d_gamepl.listener;

import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import com.github.fratikak.l4d_gamepl.util.ScoreboardSystem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffectType;

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
            if (!player.isOp()) {
                player.setGameMode(GameMode.SURVIVAL);
            }
        }

        //ステータスリセット
        player.setHealth(20);
        player.setFoodLevel(20);

        //ゲームで付与される暗視効果が残っていれば削除
        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }

        //体力表示スコアボードの登録
        new ScoreboardSystem(pl).viewHealthBoard();

        pl.giveLobbyItem(inventory); //ロビーアイテム追加
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

        player.teleport(location); //スポーン地点固定

    }

}
