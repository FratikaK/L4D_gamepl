package com.github.fratikak.l4d_gamepl.listener;

import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import com.github.fratikak.l4d_gamepl.task.StopTask;
import com.github.fratikak.l4d_gamepl.util.PerkDecks;
import org.bukkit.*;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
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

        pl.getLogger().info(event.getDeathMessage());

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
        player.setGameMode(GameMode.CREATIVE);
        event.setRespawnLocation(deathLocation);
        player.sendMessage("[L4D]" + ChatColor.RED + "あなたは死亡しました。他のプレイヤーがチェックポイントにたどり着けば復帰できます");
        inventory.clear();
    }

    /**
     * プレイヤーがクリエイティブモードの時、透明になる
     *
     * @param event
     */
    @EventHandler
    public void setSpectatorEffect(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 0, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 0, true));
        }
    }

    /**
     * クリエイティブからサバイバルに変更時、透明化を削除
     *
     * @param event
     */
    @EventHandler
    public void changeGamemode(PlayerGameModeChangeEvent event) {
        if (event.getNewGameMode() == GameMode.SURVIVAL) {
            event.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
        }
    }

    /**
     * クリエイティブを禁止する
     *
     * @param event
     */
    @EventHandler
    public void noCreativeInventory(InventoryCreativeEvent event) {
        if (!event.getWhoClicked().isOp()) {
            event.setCancelled(true);
        }
    }

    /**
     *
     * プレイヤー同士の押し合いを禁止する
     *
     * @param event
     */
    @EventHandler
    public void noEntityInteract(EntityInteractEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            event.setCancelled(true);
        }
    }

    /**
     * mobをkillした時に対象プレイヤーに音をならす
     *
     * @param event
     */
    @EventHandler
    public void playerKillMobs(EntityDeathEvent event) {

        //プレイヤーが死亡の場合はreturn
        if (event.getEntity().getType() == EntityType.PLAYER) {
            return;
        }

        Player player = event.getEntity().getKiller();

        //爆発等による死亡はnullが渡されるのでreturnする
        if (player == null){
            return;
        }

        //音をならす
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);

        //体力が19以上であればreturn
        if (player.getHealth() >= 19) {
            return;
        }

        //メタデータを持ってるか
        if (player.hasMetadata(PerkDecks.getPeekKey())) {

            //メタデータはリスト型として返ってくるので、for文で取得する必要がある
            List<MetadataValue> peeks = player.getMetadata(PerkDecks.getPeekKey());

            MetadataValue value = null;

            for (MetadataValue v : peeks) {
                if (v.getOwningPlugin().getName() == pl.getName()) {
                    value = v;
                    break;
                }
            }

            //メタデータが見つからなかった場合はreturn
            if (value == null) {
                return;
            }

            //体力を1ポイント回復する
            if (value.asString().equals("GRINDER")) {
                player.setHealth(player.getHealth() + 1);
            }
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
            int mobNum = L4D_gamepl.getSurvivorList().size() * 4;

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
     * 速度低下エフェクトも削除する
     *
     * @param event
     */
    @EventHandler
    public void removeRaid(PlayerMoveEvent event) {
        if (event.getPlayer().hasPotionEffect(PotionEffectType.BAD_OMEN)) {
            event.getPlayer().removePotionEffect(PotionEffectType.BAD_OMEN);
        }

        if (event.getPlayer().hasPotionEffect(PotionEffectType.SLOW)) {
            event.getPlayer().removePotionEffect(PotionEffectType.SLOW);
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
        int randomValue = random.nextInt(22);
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
                cbmeta.setDisplayName(ChatColor.YELLOW + "コンカッション");
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

            case 4:
                ItemStack minecart = new ItemStack(Material.FURNACE_MINECART);
                ItemMeta minemeta = minecart.getItemMeta();
                minemeta.setDisplayName(ChatColor.YELLOW + "Landmine");
                minecart.setItemMeta(minemeta);
                entityLocation.getWorld().dropItem(entityLocation, minecart);
                break;


        }
    }
}
