package com.github.fratikak.l4d_gamepl;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import java.util.Random;

public class GameLogic implements Listener {

    private final L4D_gamepl pl;

    //コンストラクタ
    public GameLogic(L4D_gamepl pl) {
        this.pl = pl;
    }

    /**
     * ゲーム開始後から修了までの処理を記述する
     *
     * ・プレイヤーが死亡した場合にstaticプレイヤーリストから削除
     * ・同じくプレイヤーが死亡した場合にプレイヤーリストネームタグを変更する
     * ・サバイバルモードのみスプリント処理を無効化（クリエイティブは可）
     * ・イベント内容がPlayerEventと被る所があるので統一するかもしれない
     * ・敵mobを沸かせる処理を実装する
     *
     * @author FratikaK
     */

    //プレイヤーが死亡時、リストから削除、プロフィール名変更
    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (player.getGameMode() == GameMode.SURVIVAL){
            L4D_gamepl.getPlayerList().remove(player.getDisplayName());
            player.setPlayerListName("[" + ChatColor.RED + "死亡" + ChatColor.WHITE + "]" + player.getDisplayName());

            pl.getLogger().info(ChatColor.GREEN + player.getDisplayName() + ChatColor.WHITE + "が死亡しました");
            pl.getLogger().info("残りのプレイヤーは" + ChatColor.GREEN + L4D_gamepl.getPlayerList() + ChatColor.WHITE + "です");
        }

    }

    //ゲーム参加者のみスプリントを禁止する
    @EventHandler
    public void dontSplint(PlayerToggleSprintEvent event){
        Player player = event.getPlayer();

        if(player.getGameMode() == GameMode.SURVIVAL && L4D_gamepl.isGame()){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void zombieSpawn(PlayerMoveEvent event){

        /**
         * 基本となるゾンビがスポーンするロジックを記述する
         * 参加者が歩くことで確率でスポーンする
         *
         * サーバーの負荷具合をみてスポーン確率を調整する
         */

        Random random = new Random();
        //スポーン確率変更はspawnValueの数値を弄ればOK
        int spawnValue =  random.nextInt(10);

        Player player = event.getPlayer();

        //参加者の周りのみスポーンさせる
        if (player.getGameMode() == GameMode.SURVIVAL && L4D_gamepl.isGame()){
            Location playerLocation = player.getLocation().clone();
            Location zombieLocation = playerLocation.add(10,10,10);
            World world = player.getWorld();

            if(spawnValue == 5){
                world.spawnEntity(zombieLocation, EntityType.ZOMBIE);
            }else if(spawnValue == 1){
                world.spawnEntity(zombieLocation, EntityType.ZOMBIE_VILLAGER);
            }
        }
    }
}
