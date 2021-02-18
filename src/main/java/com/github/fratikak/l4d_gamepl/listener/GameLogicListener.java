package com.github.fratikak.l4d_gamepl.listener;

import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import com.github.fratikak.l4d_gamepl.task.StopTask;
import com.github.fratikak.l4d_gamepl.util.PerkDecks;
import org.bukkit.*;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

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
public class GameLogicListener implements Listener {

    private final L4D_gamepl pl;

    //プレイヤー死亡地点
    private static Location deathLocation;

    //コンストラクタ
    public GameLogicListener(L4D_gamepl pl) {
        this.pl = pl;
    }

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

            //生存プレイヤーリストから該当のプレイヤーを削除、死亡者リストに追加
            L4D_gamepl.getSurvivorList().remove(player.getUniqueId());
            L4D_gamepl.getDeathPlayerList().add(player.getUniqueId());
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

    /**
     * ゲーム中であれば観戦者として、死亡地点からリスポーンする
     *
     * @param event
     */
    @EventHandler
    public void setRespawnLocation(PlayerRespawnEvent event) {

        Player player = event.getPlayer();
        Inventory inventory = player.getInventory();

        //ゲーム中でなければロビーへ
        if (!L4D_gamepl.isGame()) {
            event.setRespawnLocation(player.getWorld().getSpawnLocation().add(0, 2, 0));
            player.setGameMode(GameMode.SURVIVAL);
            return;
        }


        player.setGameMode(GameMode.SPECTATOR);

        //近くのプレイヤーのところへリスポーン、いなければ死んだ場所でリスポーンする
        if (!L4D_gamepl.getSurvivorList().isEmpty()) {
            for (UUID playerId : L4D_gamepl.getSurvivorList()) {
                for (Player target : Bukkit.getOnlinePlayers()) {
                    if (target.getUniqueId().equals(playerId)) {
                        event.setRespawnLocation(target.getLocation());
                        break;
                    }
                }
            }
        } else {
            event.setRespawnLocation(deathLocation);
        }

        player.sendMessage("[L4D]" + ChatColor.RED + "あなたは死亡しました。他のプレイヤーがチェックポイントにたどり着けば復帰できます");
        inventory.clear();
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

    //村人レイドを発生させない
    @EventHandler
    public void notRaid(RaidTriggerEvent event) {
        event.setCancelled(true);
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
        if (player == null) {
            return;
        }

        //所持金を加算
        player.setStatistic(Statistic.ANIMALS_BRED, player.getStatistic(Statistic.ANIMALS_BRED) + 5);

        //音をならす
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);

        //体力が19以上であればreturn
        if (player.getHealth() == 20) {
            return;
        }

        //grinderのmetadataが付与されていれば1ポイント回復する
        PerkDecks perkDecks = new PerkDecks(player, pl);
        if (perkDecks.getMetadata(player, PerkDecks.getPerkKey(), pl).equals(PerkDecks.getGrinder())) {
            player.setHealth(player.getHealth() + 1);
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

            //ランダム関数で特殊mobをevent1回につき1匹スポーンさせる
            Random random = new Random();
            event.getSpawner().getWorld().spawnEntity(spawnerLocation, spawnSpecialMob(random.nextInt(8)));

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
        int randomValue = random.nextInt(15);
        Location entityLocation = event.getEntity().getLocation().clone();

        switch (randomValue) {
            case 0:
                setDropItem(new ItemStack(Material.FIREWORK_STAR), "グレネード", entityLocation);
                break;

            case 1:
                setDropItem(new ItemStack(Material.CLAY_BALL), "コンカッション", entityLocation);
                break;

            case 2:
                setDropItem(new ItemStack(Material.COAL), "クラスターボム", entityLocation);
                break;

            case 3:
                setDropItem(new ItemStack(Material.APPLE), "リンゴ", entityLocation);
                break;

            case 4:
                setDropItem(new ItemStack(Material.FURNACE_MINECART), "Landmine", entityLocation);
                break;
        }
    }

    /**
     * 引数に応じた特殊mobを返す
     *
     * @param random ランダム関数で生成された整数
     * @return　スポーンさせたいmobのタイプ
     */
    private EntityType spawnSpecialMob(int random) {

        switch (random) {
            case 1:
                return EntityType.MAGMA_CUBE;

            case 2:
                return EntityType.CREEPER;

            case 3:
                return EntityType.RAVAGER;

            default:
                return EntityType.ZOMBIE_VILLAGER;
        }
    }

    /**
     * 引数のLocationにメタデータを付与したItemStackをドロップさせる
     *
     * @param item     アイテム
     * @param name     つけたい名前
     * @param location ドロップさせたい場所
     */
    private void setDropItem(ItemStack item, String name, Location location) {
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(ChatColor.YELLOW + name);
        item.setItemMeta(itemMeta);

        Objects.requireNonNull(location.getWorld()).dropItem(location, item);
    }
}
