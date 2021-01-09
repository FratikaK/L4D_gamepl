package com.github.fratikak.l4d_gamepl;

import com.shampaggon.crackshot.CSUtility;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class GameLogic implements Listener {

    private final L4D_gamepl pl;

    //コンストラクタ
    public GameLogic(L4D_gamepl pl) {
        this.pl = pl;
    }

    /**
     * ゲーム開始後から修了までの処理を記述する
     * <p>
     * ・プレイヤーが死亡した場合にstaticプレイヤーリストから削除
     * ・同じくプレイヤーが死亡した場合にプレイヤーリストネームタグを変更する
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
            L4D_gamepl.getPlayerList().remove(player);
            L4D_gamepl.getDeathPlayer().add(player);
            player.setPlayerListName("[" + ChatColor.RED + "死亡" + ChatColor.WHITE + "]" + player.getDisplayName());

            pl.getLogger().info(ChatColor.GREEN + player.getDisplayName() + ChatColor.WHITE + "が死亡しました");
            pl.getLogger().info("残りのプレイヤーは" + ChatColor.GREEN + L4D_gamepl.getPlayerList() + ChatColor.WHITE + "です");
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
            EntityType entityType = event.getSpawner().getSpawnedType();

            //プレイヤー数を取得して、プレイヤー数×指定した数のゾンビをスポーンさせる
            World world = event.getSpawner().getWorld();
            int players = L4D_gamepl.getPlayerList().size();
            for (int i = 0; i < players * 4; i++) {
                world.spawnEntity(location, entityType);
            }
        } else {
            event.setCancelled(true);
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
        Location deathLocation = event.getEntity().getLocation().clone();
        World world = event.getEntity().getWorld();


        if (L4D_gamepl.isGame()) {
            if (type == EntityType.ZOMBIE) {

                
                for (Player target:Bukkit.getOnlinePlayers()){
                    if (target.getGameMode() !=GameMode.SURVIVAL){
                        return;
                    }
                    //プレイヤーとの距離が近ければreturn
                    if (Math.abs(target.getLocation().getX() - deathLocation.getX()) <= 4 || Math.abs(target.getLocation().getZ() - deathLocation.getZ()) <= 4) {
                        return;
                    }

                    switch (spawnValue) {
                        case 1:
                            //ピリジャー
                            world.spawnEntity(deathLocation, EntityType.PILLAGER);
                            pl.getServer().getLogger().info("ピリジャーがスポーンしました");
                            break;

                        case 2:
                            //クリーパー
                            world.spawnEntity(deathLocation, EntityType.CREEPER);
                            pl.getServer().getLogger().info("クリーパーがスポーンしました");
                            break;

                        case 3:
                            //ヴィンディケーター
                            world.spawnEntity(deathLocation, EntityType.VINDICATOR);
                            pl.getServer().getLogger().info("ヴィンディケーターがスポーンしました");
                            break;

                        case 4:
                            //ラヴェジャー
                            world.spawnEntity(deathLocation, EntityType.RAVAGER);
                            pl.getServer().getLogger().info("ラベンジャーがスポーンしました");
                            break;

                        case 5:
                            //マグマキューブ
                            world.spawnEntity(deathLocation, EntityType.MAGMA_CUBE);
                            pl.getServer().getLogger().info("マグマキューブがスポーンしました");
                            break;
                    }
                }
            }
        }
    }

    //特殊mobを倒したときに特定のアイテムを付与する
    @EventHandler
    public void specialDropItem(EntityDeathEvent event) {

        if (!L4D_gamepl.isGame()) {
            return;
        }

        EntityType entityType = event.getEntity().getType();
        Player player = event.getEntity().getKiller();

        switch (entityType) {
            case PILLAGER:
                assert player != null;
                player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 30, 2), true);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 24);
                player.sendMessage(ChatColor.AQUA + "回復ブーストを付与しました");
                break;

            case EVOKER:
                assert player != null;
                new CSUtility().giveWeapon(player, "C4", 1);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 24);
                player.sendMessage(ChatColor.AQUA + "C4を入手しました");
                break;

            case MAGMA_CUBE:
                assert player != null;
                new CSUtility().giveWeapon(player, "GRENADE", 2);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 24);
                player.sendMessage(ChatColor.AQUA + "グレネードを入手しました");
                break;
        }
    }
}
