package com.github.fratikak.l4d_gamepl.listener;

import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import com.github.fratikak.l4d_gamepl.task.StopTask;
import org.bukkit.*;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class GameLogic implements Listener {

    private final L4D_gamepl pl;

    //プレイヤー死亡地点
    private static Location deathLocation;

    //コンストラクタ
    public GameLogic(L4D_gamepl pl) {
        this.pl = pl;
    }

    /**
     * ゲーム開始後から修了までの処理を記述する
     * ・プレイヤーが死亡した場合にstaticプレイヤーリストから削除
     * ・同じくプレイヤーが死亡した場合にプレイヤーリストネームタグを変更する
     * ・イベント内容がPlayerEventと被る所があるので統一するかもしれない
     * ・敵mobを沸かせる処理を実装する
     * ・mobスポーンを沸かせる条件に確率でRandom関数を採用しているが、
     * 　もっといい方法がないか模索する必要がある
     *
     * @author FratikaK
     */

    /**
     * プレイヤーが死亡時、リストから削除、プロフィール名変更
     *
     * @param event
     */
    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        deathLocation = player.getLocation().clone().add(0, 1, 0);

        if (L4D_gamepl.isGame()) {
            if (player.getGameMode() == GameMode.SURVIVAL) {

                //生存プレイヤーリストから該当のプレイヤーを削除、死亡者リストに追加
                L4D_gamepl.getSurvivorList().remove(player);
                L4D_gamepl.getDeathPlayerList().add(player);
                player.setPlayerListName("[" + ChatColor.RED + "死亡" + ChatColor.WHITE + "]" + player.getDisplayName());

                Bukkit.broadcastMessage(ChatColor.RED + player.getDisplayName() + "が死亡しました");
                Bukkit.broadcastMessage(ChatColor.WHITE + "プレイヤー数残り" + L4D_gamepl.getSurvivorList().size() + "人です");
                Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_WOLF_HOWL, 1, 15));
                player.sendTitle(ChatColor.RED + "あなたは死亡しました", "", 5, 40, 5);

                //プレイヤーが全員死亡した場合
                if (L4D_gamepl.getSurvivorList().isEmpty()) {
                    new StopTask(pl).runTaskTimer(pl, 0, 20);
                }
            }
        }
    }

    /**
     * ゲーム中であれば観戦者として、死亡地点からリスポーンする
     *
     * @param event
     */
    @EventHandler
    public void setSpectator(PlayerRespawnEvent event) {

        Player player = event.getPlayer();
        Inventory inventory = player.getInventory();

        //ゲーム中でなければロビーへ
        if (!L4D_gamepl.isGame()) {
            event.setRespawnLocation(player.getWorld().getSpawnLocation().add(0, 2, 0));
            player.setGameMode(GameMode.SURVIVAL);
            return;
        }

        //死亡した地点にリスポーン、インベントリ整理
        player.setGameMode(GameMode.SPECTATOR);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 0, true));
        event.setRespawnLocation(deathLocation);
        player.sendMessage("[L4D]" + ChatColor.RED + "あなたは死亡しました。他のプレイヤーがチェックポイントにたどり着けば復帰できます");
        inventory.clear();
    }

    /**
     * mobをkillした時に対象プレイヤーに音をならす
     *
     * @param event
     */
    @EventHandler
    public void playerKillMobs(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
            //音をならす
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
        }
    }

    /**
     * スポナーから沸くmobを調整する
     *
     * @param event
     */
    @EventHandler
    public void zombieSpawn(SpawnerSpawnEvent event) {

        event.setCancelled(true);

        if (L4D_gamepl.isGame()) {
            //プレイヤー数 * 任意の数字分沸かせる
            int mobNum = L4D_gamepl.getSurvivorList().size() * 3;

            Location spawnerLocation = event.getSpawner().getLocation().clone();
            spawnerLocation.add(0.5, 1, 0.5);

            CreatureSpawner spawner = event.getSpawner();
            spawner.setSpawnCount(1);
            spawner.setSpawnRange(20);
            spawner.setRequiredPlayerRange(40);
            spawner.setMaxNearbyEntities(10);
            spawner.setDelay(0);
            spawner.setMinSpawnDelay(200);
            spawner.setMaxSpawnDelay(200);

            for (int i = 0; i < mobNum; i++) {
                event.getSpawner().getWorld().spawnEntity(spawnerLocation, event.getSpawner().getSpawnedType());
            }

        }
    }

    /**
     * 不吉のポーションエフェクトを解除する
     *
     * @param event
     */
    @EventHandler
    public void removeRaid(PlayerMoveEvent event) {
        if (event.getPlayer().hasPotionEffect(PotionEffectType.BAD_OMEN)) {
            event.getPlayer().removePotionEffect(PotionEffectType.BAD_OMEN);
        }
    }

    /**
     * mobを倒したときに確率でアイテムをドロップする
     *
     * @param event
     */
    @EventHandler
    public void specialDropItem(EntityDeathEvent event) {

        if (!L4D_gamepl.isGame()) {
            return;
        }

        Random random = new Random();
        int randomValue = random.nextInt(15);
        Location entityLocation = event.getEntity().getLocation().clone();

        switch (randomValue) {
            case 0:
                ItemStack firework = new ItemStack(Material.FIREWORK_STAR);
                ItemMeta fwmeta = firework.getItemMeta();
                fwmeta.setDisplayName(ChatColor.YELLOW + "グレネード");
                firework.setItemMeta(fwmeta);
                entityLocation.getWorld().dropItem(entityLocation, firework);
                break;

            case 1:
                ItemStack clayball = new ItemStack(Material.CLAY_BALL);
                ItemMeta cbmeta = clayball.getItemMeta();
                cbmeta.setDisplayName(ChatColor.YELLOW + "火炎瓶");
                clayball.setItemMeta(cbmeta);
                entityLocation.getWorld().dropItem(entityLocation, clayball);
                break;

            case 2:
                ItemStack coal = new ItemStack(Material.COAL);
                ItemMeta cmeta = coal.getItemMeta();
                cmeta.setDisplayName(ChatColor.YELLOW + "クラスターボム");
                coal.setItemMeta(cmeta);
                entityLocation.getWorld().dropItem(entityLocation, coal);
                break;

            case 3:
                ItemStack potion = new ItemStack(Material.APPLE);
                entityLocation.getWorld().dropItem(entityLocation, potion);
                break;


        }
    }
}
