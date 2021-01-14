package com.github.fratikak.l4d_gamepl;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import org.bukkit.*;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

        if (player.getGameMode() == GameMode.SURVIVAL) {

            //プレイヤーリストから該当のプレイヤーを削除、死亡者リストに追加
            L4D_gamepl.getPlayerList().remove(player);
            L4D_gamepl.getDeathPlayer().add(player);
            player.setPlayerListName("[" + ChatColor.RED + "死亡" + ChatColor.WHITE + "]" + player.getDisplayName());

            Bukkit.broadcastMessage(ChatColor.RED + player.getDisplayName() + "が死亡しました");
            Bukkit.broadcastMessage(ChatColor.WHITE + "プレイヤー数残り" + L4D_gamepl.getPlayerList().size() + "人です");
            Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_WOLF_HOWL, 1, 15));
            player.sendTitle(ChatColor.RED + "あなたは死亡しました", "", 5, 40, 5);

            //プレイヤーが全員死亡した場合
            if (L4D_gamepl.getPlayerList().isEmpty()) {
                new Stop(pl).runTaskTimer(pl, 0, 20);
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
        event.setRespawnLocation(deathLocation);
        pl.giveLobbyItem(inventory);
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
            int mobNum = L4D_gamepl.getPlayerList().size() * 3;
            Location spawnerLocation = event.getSpawner().getLocation().clone();
            spawnerLocation.add(0, 1, 0);

            CreatureSpawner spawner = event.getSpawner();
            spawner.setSpawnCount(1);
            spawner.setSpawnRange(6);
            spawner.setRequiredPlayerRange(30);
            spawner.setMaxNearbyEntities(4);
            spawner.setDelay(0);
            spawner.setMinSpawnDelay(200);
            spawner.setMaxSpawnDelay(200);

            for (int i = 0; i < mobNum; i++) {
                event.getSpawner().getWorld().spawnEntity(spawnerLocation, event.getSpawner().getSpawnedType());
            }

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
                cbmeta.setDisplayName(ChatColor.YELLOW + "フラッシュバン");
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
