package com.github.fratikak.l4d_gamepl.listener;

import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import com.github.fratikak.l4d_gamepl.task.StopTask;
import com.github.fratikak.l4d_gamepl.util.PerkDecks;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class PlayerEvent implements Listener {

    private final L4D_gamepl pl;

    //コンストラクタ
    public PlayerEvent(L4D_gamepl pl) {
        this.pl = pl;
    }

    /**
     * プレイヤー関連のイベントを記述する
     *
     * @author FratikaK
     */

    /**
     * プレイヤーがログアウトした場合の処理
     * ゲーム中にログアウトする場合、リストから削除する
     *
     * @param event
     */
    @EventHandler
    public void logoutPlayer(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(ChatColor.AQUA + player.getDisplayName() + "がログアウトしました");
        pl.getLogger().info(ChatColor.AQUA + player.getDisplayName() + "がログアウトしました");
        if (L4D_gamepl.isGame()) {
            //リストから削除
            L4D_gamepl.getPlayerList().remove(player);
            L4D_gamepl.getSurvivorList().remove(player);
            L4D_gamepl.getDeathPlayerList().remove(player);

            pl.getLogger().info("survivorList: " + L4D_gamepl.getSurvivorList());
            pl.getLogger().info("deathList: " + L4D_gamepl.getDeathPlayerList());

            //peakの効果は削除
            new PerkDecks(player, pl).removePotion();

            //ゲームプレイヤーが全員いなくなった場合、ゲームを終了
            if (L4D_gamepl.getSurvivorList().isEmpty()) {
                new StopTask(pl).runTaskTimer(pl, 0, 20);
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
    public void noOverDamage(EntityDamageEvent event) {

        //落下ダメージは無効
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
            return;
        }

        Entity entity = event.getEntity();

        if (entity.getType() != EntityType.PLAYER) {
            return;
        }
        Player player = (Player) entity;
        //メタデータを持ってるか
        if (player.hasMetadata(PerkDecks.getPerkKey())) {

            //メタデータはリスト型として返ってくるので、for文で取得する必要がある
            List<MetadataValue> peeks = player.getMetadata(PerkDecks.getPerkKey());

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

            //グラインダーかスカウトであればダメージ増加
            if (value.asString() == "GRINDER" || value.asString() == "SCOUT") {
                event.setDamage(6);
                return;
            }
        }
        event.setDamage(4);
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
     * プレイヤーに特定のポーション効果を付与する時、それをキャンセルする
     * 主にフラッシュバンによるフレンドリーファイア対策
     *
     * @param event
     */
    @EventHandler
    public void noPlayerEffect(EntityPotionEffectEvent event) {

        Entity entity = event.getEntity();

        if (entity.getType() == EntityType.PLAYER) {

            if (event.getModifiedType() == PotionEffectType.SLOW) {
                event.setCancelled(true);
            } else if (event.getModifiedType() == PotionEffectType.BAD_OMEN) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * クリエイティブ（観戦者）時にアイテムを拾わせない
     * @param event
     */
    @EventHandler
    public void onCreativePickUpItem(PlayerPickupItemEvent event){

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE){
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


