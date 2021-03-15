package com.github.fratikak.l4d_gamepl.listener;

import com.github.fratikak.l4d_gamepl.util.GameWorlds;
import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import com.github.fratikak.l4d_gamepl.task.StopTask;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;
import java.util.UUID;

/**
 * チェックポイントを実装する
 * 特定のブロックの上に乗ったらレストルーム（？）にテレポート
 * テレポートしたプレイヤーの体力と空腹を回復
 * 死亡したプレイヤーを復活させる
 *
 * @author FratikaK
 */
public class CheckPoint implements Listener {

    private final L4D_gamepl pl;

    //コンストラクタ
    public CheckPoint(L4D_gamepl pl) {
        this.pl = pl;
    }

    //補充アイテム
    private void sendCheckPointItem(Player target, Inventory inventory) {
        target.setStatistic(Statistic.ANIMALS_BRED, target.getStatistic(Statistic.ANIMALS_BRED) + 200);
        inventory.addItem(new ItemStack(Material.COOKED_BEEF, 2));
        target.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 0, true));
    }

    /**
     * 死亡プレイヤーを復活させる
     *
     * @param player 　対象プレイヤー
     */
    private void resurrectionPlayer(Player player) {

        //生存プレイヤーならreturn
        if (L4D_gamepl.getSurvivorList().contains(player.getUniqueId())) {
            return;
        }

        //死亡プレイヤーリストにあれば復帰処理
        if (L4D_gamepl.getDeathPlayerList().contains(player.getUniqueId())) {

            //リスト整理
            L4D_gamepl.getSurvivorList().add(player.getUniqueId());
            L4D_gamepl.getDeathPlayerList().remove(player.getUniqueId());

            player.setPlayerListName("[" + ChatColor.AQUA + "生存者" + ChatColor.WHITE + "]" + player.getDisplayName());

            //初期状態に戻す
            player.setGameMode(GameMode.SURVIVAL);
            player.setFoodLevel(6);
            pl.giveGameItem(player.getInventory(), player);
        }
    }

    /**
     * チェックポイント到達時のタスクを実行する
     *
     * @param target     対象プレイヤー
     * @param checkPoint 何番目のチェックポイントか
     */
    private void checkPointTask(Player target, int checkPoint) {
        target.setHealth(20);
        target.setFoodLevel(6);
        sendCheckPointItem(target, target.getInventory());
        resurrectionPlayer(target);
        target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

        target.sendMessage(ChatColor.AQUA + String.valueOf(checkPoint) + "番目のチェックポイントにたどり着きました！");
        target.sendMessage(ChatColor.AQUA + "特定のブロックを踏むとゲームを再開します");

    }

    //チェックポイント登録
    @EventHandler
    public void setCheckPoint(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (L4D_gamepl.isGame()) {

            Location targetLoc;

            if (player.isOnGround() && player.getGameMode() == GameMode.SURVIVAL) {
                Location loc = event.getTo().clone();
                loc.add(0, -0.1, 0);

                //チェックポイントに入る（1回目）
                if (loc.getBlock().getType().equals(Material.DIAMOND_BLOCK)) {
                    L4D_gamepl.setCheckPoint(true);
                    switch (GameWorlds.getStageId()) {
                        case 1:
                            targetLoc = new Location(player.getWorld(), 1389, 42, 928);
                            break;

                        case 2:
                            targetLoc = new Location(player.getWorld(), 678, 88, 999);
                            break;

                        case 3:
                            targetLoc = new Location(player.getWorld(), 913, 29, 1415);
                            break;

                        case 4:
                            targetLoc = new Location(player.getWorld(), 1283, 74, 370, 0, 0);
                            break;

                        default:
                            pl.getLogger().info("[CheckPoint]stageIdに不具合が起きたので実行できませんでした");
                            return;
                    }
                    for (UUID playerId : L4D_gamepl.getPlayerList()) {
                        for (Player target : Bukkit.getOnlinePlayers()) {
                            if (target.getUniqueId().equals(playerId)) {
                                target.teleport(targetLoc);
                                checkPointTask(target, 1);
                            }
                        }
                    }
                }

                //チェックポイントから出る（1回目）
                if (loc.getBlock().getType().equals(Material.GOLD_BLOCK)) {
                    L4D_gamepl.setCheckPoint(false);
                    switch (GameWorlds.getStageId()) {
                        case 1:
                            targetLoc = new Location(player.getWorld(), 1400, 42, 918);
                            break;

                        case 2:
                            targetLoc = new Location(player.getWorld(), 571, 94, 1009);
                            break;

                        case 3:
                            targetLoc = new Location(player.getWorld(), 986, 29, 1482, -91, 0);
                            break;

                        case 4:
                            targetLoc = new Location(player.getWorld(), 1027, 76, 275, 0, 0);
                            break;

                        default:
                            pl.getLogger().info("[CheckPoint]stageIdに不具合が起きたので実行できませんでした");
                            return;
                    }
                    exitCheckPoint(targetLoc);
                }

                //チェックポイントに入る（2回目）
                if (loc.getBlock().getType().equals(Material.EMERALD_BLOCK)) {
                    L4D_gamepl.setCheckPoint(true);
                    switch (GameWorlds.getStageId()) {
                        case 1:
                            targetLoc = new Location(player.getWorld(), 1376, 47, 895);
                            break;

                        case 2:
                            targetLoc = new Location(player.getWorld(), 665, 132, 1169);
                            break;

                        case 3:
                            targetLoc = new Location(player.getWorld(), 1403, 28, 1530);
                            break;

                        case 4:
                            targetLoc = new Location(player.getWorld(), 1207.5, 76, 270.5, -90, 0);
                            break;

                        default:
                            pl.getLogger().info("[CheckPoint]stageIdに不具合が起きたので実行できませんでした");
                            return;
                    }
                    for (UUID playerId : L4D_gamepl.getPlayerList()) {
                        for (Player target : Bukkit.getOnlinePlayers()) {
                            if (target.getUniqueId().equals(playerId)) {
                                target.teleport(targetLoc);
                                checkPointTask(target, 2);
                            }
                        }
                    }
                }

                //チェックポイントから出る（2回目）
                if (loc.getBlock().getType().equals(Material.REDSTONE_BLOCK)) {
                    L4D_gamepl.setCheckPoint(false);
                    switch (GameWorlds.getStageId()) {
                        case 1:
                            targetLoc = new Location(player.getWorld(), 1336, 42, 877);
                            break;

                        case 2:
                            targetLoc = new Location(player.getWorld(), 733, 87, 1150);
                            break;

                        case 3:
                            targetLoc = new Location(player.getWorld(), 1343, 6, 1575, 173, 0);
                            break;

                        case 4:
                            targetLoc = new Location(player.getWorld(), 1008, 93, 283, -120, 3);
                            break;

                        default:
                            pl.getLogger().info("[ChackPoint]stageIdに不具合が起きたので実行できませんでした");
                            return;
                    }
                    exitCheckPoint(targetLoc);
                }

                //ゴールする
                if (loc.getBlock().getType().equals(Material.LAPIS_BLOCK)) {
                    for (UUID playerId : L4D_gamepl.getPlayerList()) {
                        for (Player target : Bukkit.getOnlinePlayers()) {
                            if (target.getUniqueId().equals(playerId)) {
                                targetLoc = new Location(target.getWorld(), 914, 156, 1033);
                                target.teleport(targetLoc);

                                spawnFireworks(targetLoc, 1);
                            }
                        }
                    }
                    Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 0));
                    Bukkit.getOnlinePlayers().forEach(p -> p.sendTitle(ChatColor.AQUA + "GAME CLEAR!", null, 5, 100, 5));
                    new StopTask(pl).runTaskTimer(pl, 0, 20);
                }

            }
        }
    }

    /**
     * チェックポイントから出る時の処理
     * 生存プレイヤー全員テレポート、メッセージを送信する
     *
     * @param targetLoc
     */
    private void exitCheckPoint(Location targetLoc) {
        for (UUID playerId : L4D_gamepl.getPlayerList()) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (target.getUniqueId().equals(playerId)) {
                    target.teleport(targetLoc);
                    target.sendMessage(ChatColor.AQUA + "チェックポイントから出ました");
                    target.sendMessage(ChatColor.AQUA + "ゲームを再開します");
                }
            }
        }
    }

    /**
     * 花火を打ち上げる処理
     *
     * @param location どこに花火をだすか
     * @param amount   打ち上げる回数
     */
    private static void spawnFireworks(Location location, int amount) {
        Firework fw = (Firework) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();

        for (int i = 0; i < amount; i++) {
            Firework fw2 = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }

}
