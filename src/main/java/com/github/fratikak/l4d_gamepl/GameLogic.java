package com.github.fratikak.l4d_gamepl;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
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
     * ・mobスポーンを沸かせる条件に確率でRandom関数を採用しているが、
     * 　もっといい方法がないか模索する必要がある
     *
     * @author FratikaK
     */

    //プレイヤーが死亡時、リストから削除、プロフィール名変更
    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (player.getGameMode() == GameMode.SURVIVAL) {
            L4D_gamepl.getPlayerList().remove(player.getDisplayName());
            player.setPlayerListName("[" + ChatColor.RED + "死亡" + ChatColor.WHITE + "]" + player.getDisplayName());

            pl.getLogger().info(ChatColor.GREEN + player.getDisplayName() + ChatColor.WHITE + "が死亡しました");
            pl.getLogger().info("残りのプレイヤーは" + ChatColor.GREEN + L4D_gamepl.getPlayerList() + ChatColor.WHITE + "です");
        }

    }

    //ゲーム参加者のみスプリントを禁止する
    @EventHandler
    public void dontSplint(PlayerToggleSprintEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.SURVIVAL && L4D_gamepl.isGame()) {
            event.setCancelled(true);
            player.setSprinting(false);
        }
    }

    @EventHandler
    public void zombieSpawn(SpawnerSpawnEvent event) {

        /*
         * 基本となるゾンビがスポーンするロジックを記述する
         * スポナーが稼働した場合、追加でプレイヤー一人につき10匹程度追加でゾンビをスポーンさせる
         *
         * サーバーの負荷具合をみてスポーン量を調整する
         */

        if (L4D_gamepl.isGame()) {
            //スポナーの場所を取得する
            Location location = event.getLocation().clone();
            event.setCancelled(true);

            //プレイヤー数を取得して、プレイヤー数×指定した数のゾンビをスポーンさせる
            World world = event.getSpawner().getWorld();
            int players = L4D_gamepl.getPlayerList().size();
            for (int i = 0; i < players * 10; i++) {
                world.spawnEntity(location, EntityType.ZOMBIE);
            }
        }


    }

    @EventHandler
    public void specialMobSpawn(EntityDeathEvent event) {

        /*
         * ゾンビが死亡すると、一定確率で特殊なMobが出現する
         * スポーンするMobの挙動は別クラスにて記述する
         */

        Random random = new Random();
        int spawnValue = random.nextInt(10);

        EntityType type = event.getEntity().getType();

        if (L4D_gamepl.isGame()) {
            if (type == EntityType.ZOMBIE || type == EntityType.ZOMBIE_VILLAGER) {
                Location deathLocation = event.getEntity().getLocation().clone();
                Location specialLocation = deathLocation.add(20, 1, 0);
                World world = event.getEntity().getWorld();

                switch (spawnValue) {
//                    case 1:
//                        //ゾンビホース
//                        world.spawnEntity(specialLocation, EntityType.ZOMBIE_HORSE);
//                        break;

                    case 2:
                        //クリーパー
                        world.spawnEntity(specialLocation, EntityType.CREEPER);
                        break;

                    case 3:
                        //エヴォーカー
                        world.spawnEntity(specialLocation, EntityType.EVOKER);
                        break;

                    case 4:
                        //ラヴェジャー
                        world.spawnEntity(specialLocation, EntityType.RAVAGER);
                        break;

                    case 5:
                        //スライム
                        world.spawnEntity(specialLocation, EntityType.SLIME);
                        break;
                }
            }
        }
    }
}
