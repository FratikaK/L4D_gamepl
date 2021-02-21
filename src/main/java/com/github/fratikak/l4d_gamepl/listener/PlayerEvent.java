package com.github.fratikak.l4d_gamepl.listener;

import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import com.github.fratikak.l4d_gamepl.util.PerkDecks;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * プレイヤー関連のイベントを記述する
 *
 * @author FratikaK
 */
public class PlayerEvent implements Listener {

    private final L4D_gamepl pl;

    //コンストラクタ
    public PlayerEvent(L4D_gamepl pl) {
        this.pl = pl;
    }

    /**
     * 特定のダメージを無効化する
     * 現在は落下ダメージのみ
     *
     * @param event
     */
    @EventHandler
    public void invalidDamage(EntityDamageEvent event) {

        if (event.getEntity() instanceof Player) {

            if (event.getCause() == EntityDamageEvent.DamageCause.FALL || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                event.setCancelled(true);
            }
        }

        if (event.getEntity().getType() == EntityType.VILLAGER) {
            if (L4D_gamepl.isGame()) {
                event.setCancelled(true);
            }
        }
    }


    /**
     * プレイヤーが謎の一撃死バグで死ぬのを防ぐ処理
     * ヴィンディゲーターなど強力なmobからのダメージも軽減する
     *
     * @param event
     */
    @EventHandler
    public void noOverDamage(EntityDamageByEntityEvent event) {

        Entity entity = event.getEntity();

        if (event.getDamager().getType() == EntityType.PRIMED_TNT && entity.getType() == EntityType.PLAYER) {
            return;
        }

        if (entity.getType() != EntityType.PLAYER) {
            return;
        }
        Player player = (Player) entity;

        event.setCancelled(true);
        player.setNoDamageTicks(20);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1000, 0);
        player.playSound(player.getLocation(), Sound.ENTITY_WOLF_HURT, 100, 1);

        //付与されているPerkがGrinderかScoutであれば受けるダメージが増える
        PerkDecks perkDecks = new PerkDecks(player, pl);
        if (perkDecks.getMetadata(player, PerkDecks.getPerkKey(), pl) == PerkDecks.getGrinder()
                || perkDecks.getMetadata(player, PerkDecks.getPerkKey(), pl) == PerkDecks.getScout()) {
            if (player.getHealth() < 6) {
                player.setHealth(0);
                return;
            }
            player.setHealth(player.getHealth() - 6);
        } else if (perkDecks.getMetadata(player, PerkDecks.getPerkKey(), pl) == PerkDecks.getTank()) {
            if (player.getHealth() < 2.5) {
                player.setHealth(0);
                return;
            }
            player.setHealth(player.getHealth() - 2.5);
        } else {
            if (player.getHealth() < 4) {
                player.setHealth(0);
                return;
            }
            player.setHealth(player.getHealth() - 4);
        }
    }

    /**
     * 水に触れると死亡する処理
     *
     * @param event
     */
    @EventHandler
    public void waterDeath(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        if (L4D_gamepl.isGame() && player.getGameMode() == GameMode.SURVIVAL) {
            if (player.getLocation().getBlock().getType() == Material.WATER) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20, 10000, true));
            }
        }
    }

    /**
     * ブロック破壊を無効にする
     *
     * @param event
     */
    @EventHandler
    public void blockBreakEvent(BlockBreakEvent event) {

        Player player = event.getPlayer();

        if (!player.isOp()) {
            event.setCancelled(true);
        }
    }

    /**
     * ブロック設置を無効にする
     *
     * @param event
     */
    @EventHandler
    public void blockPlaceEvent(BlockPlaceEvent event) {

        Player player = event.getPlayer();

        if (!player.isOp()) {
            event.setCancelled(true);
        }
    }

    /**
     * 道具類の耐久減少をキャンセルする
     *
     * @param event
     */
    @EventHandler
    public void setItemDamege(PlayerItemDamageEvent event) {
        event.setCancelled(true);
    }

    /**
     * 空腹度の増減をキャンセルする
     *
     * @param event
     */
    @EventHandler
    public void setFoodLevel(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }
}


