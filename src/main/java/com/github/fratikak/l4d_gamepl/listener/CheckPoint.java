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

public class CheckPoint implements Listener {

    /**
     * チェックポイントを実装する
     * 特定のブロックの上に乗ったらレストルーム（？）にテレポート
     * テレポートしたプレイヤーの体力と空腹を回復
     * 死亡したプレイヤーを復活させる
     *
     * @author FratikaK
     */

    private final L4D_gamepl pl;

    //コンストラクタ
    public CheckPoint(L4D_gamepl pl) {
        this.pl = pl;
    }

    //補充アイテム
    public void sendCheckPointItem(Player target, Inventory inventory) {
        ItemStack firework = new ItemStack(Material.FIREWORK_STAR, 5);
        ItemStack clayball = new ItemStack(Material.CLAY_BALL, 5);

        ItemMeta fwmeta = firework.getItemMeta();
        ItemMeta cbmeta = clayball.getItemMeta();
        fwmeta.setDisplayName(ChatColor.YELLOW + "グレネード");
        cbmeta.setDisplayName(ChatColor.YELLOW + "コンカッション");
        firework.setItemMeta(fwmeta);
        clayball.setItemMeta(cbmeta);

        inventory.addItem(firework);
        inventory.addItem(clayball);
        inventory.addItem(new ItemStack(Material.COOKED_BEEF, 3));
        target.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 0, true));
    }

    /**
     * 死亡プレイヤーを復活させる
     *
     * @param player 　対象プレイヤー
     */
    public void resurrectionPlayer(Player player) {

        //生存プレイヤーならreturn
        if (L4D_gamepl.getSurvivorList().contains(player)) {
            return;
        }

        //死亡プレイヤーリストにあれば復帰処理
        if (L4D_gamepl.getDeathPlayerList().contains(player)) {

            //リスト整理
            L4D_gamepl.getSurvivorList().add(player);
            L4D_gamepl.getDeathPlayerList().remove(player);

            player.setPlayerListName("[" + ChatColor.AQUA + "生存者" + ChatColor.WHITE + "]" + player.getDisplayName());

            pl.getLogger().info(ChatColor.AQUA + player.getDisplayName() + "が復活しました");
            pl.getLogger().info(ChatColor.AQUA + "DeathPlayerList :" + L4D_gamepl.getDeathPlayerList());
            pl.getLogger().info(ChatColor.AQUA + "PlayerList :" + L4D_gamepl.getSurvivorList());

            //初期状態に戻す
            player.setGameMode(GameMode.SURVIVAL);
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
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
    public void checkPointTask(Player target, int checkPoint) {
        target.setHealth(20);
        target.setFoodLevel(6);
        sendCheckPointItem(target, target.getInventory());
        target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

        switch (checkPoint) {
            case 1:
                target.sendMessage(ChatColor.AQUA + "1番目のチェックポイントにたどり着きました！");
                target.sendMessage(ChatColor.AQUA + "ゴールドブロックを踏むとゲームが再開します！");
                break;
            case 2:
                target.sendMessage(ChatColor.AQUA + "2番目のチェックポイントにたどり着きました！");
                target.sendMessage(ChatColor.AQUA + "レッドストーンブロックを踏むとゲームが再開します！");
                break;
        }

    }


    /**
     * 花火を打ち上げる処理
     *
     * @param location どこに花火をだすか
     * @param amount   打ち上げる回数
     */
    public static void spawnFireworks(Location location, int amount) {
        Location loc = location;
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();

        for (int i = 0; i < amount; i++) {
            Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }

    //チェックポイント登録
    @EventHandler
    public void setCheckPoint(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (L4D_gamepl.isGame()) {
            if (player.isOnGround() && player.getGameMode() == GameMode.SURVIVAL) {
                Location loc = event.getTo().clone();
                loc.add(0, -0.1, 0);

                //チェックポイントに入る（1回目）
                if (loc.getBlock().getType().equals(Material.DIAMOND_BLOCK)) {
                    for (Player target : L4D_gamepl.getPlayerList()) {
                        Location targetLoc = target.getLocation().clone();
                        switch (GameWorlds.getStageId()) {
                            case 1:
                                targetLoc.setX(1389);
                                targetLoc.setY(42);
                                targetLoc.setZ(928);
                                target.teleport(targetLoc);

                                resurrectionPlayer(target);
                                checkPointTask(target, 1);
                                break;

                            case 2:
                                targetLoc.setX(678);
                                targetLoc.setY(88);
                                targetLoc.setZ(999);
                                target.teleport(targetLoc);

                                resurrectionPlayer(target);
                                checkPointTask(target, 1);
                                break;

                            case 3:
                                targetLoc.setX(913);
                                targetLoc.setY(29);
                                targetLoc.setZ(1415);
                                target.teleport(targetLoc);

                                resurrectionPlayer(target);
                                checkPointTask(target,1);
                        }
                    }
                }

                //チェックポイントから出る（1回目）
                if (loc.getBlock().getType().equals(Material.GOLD_BLOCK)) {
                    for (Player target : L4D_gamepl.getPlayerList()) {
                        Location targetLoc = target.getLocation().clone();
                        switch (GameWorlds.getStageId()) {
                            case 1:
                                targetLoc.setX(1400);
                                targetLoc.setY(42);
                                targetLoc.setZ(918);
                                target.teleport(targetLoc);

                                target.sendMessage(ChatColor.AQUA + "チェックポイントから出ました");
                                target.sendMessage(ChatColor.AQUA + "ゲームを再開します");
                                break;

                            case 2:
                                targetLoc.setX(571);
                                targetLoc.setY(94);
                                targetLoc.setZ(1009);
                                target.teleport(targetLoc);

                                target.sendMessage(ChatColor.AQUA + "チェックポイントから出ました");
                                target.sendMessage(ChatColor.AQUA + "ゲームを再開します");
                                break;

                            case 3:
                                targetLoc.setX(986);
                                targetLoc.setY(29);
                                targetLoc.setZ(1482);
                                target.teleport(targetLoc);

                                target.sendMessage(ChatColor.AQUA + "チェックポイントから出ました");
                                target.sendMessage(ChatColor.AQUA + "ゲームを再開します");
                                break;
                        }
                    }
                }

                //チェックポイントに入る（2回目）
                if (loc.getBlock().getType().equals(Material.EMERALD_BLOCK)) {
                    for (Player target : L4D_gamepl.getPlayerList()) {
                        Location targetLoc = target.getLocation().clone();
                        switch (GameWorlds.getStageId()) {
                            case 1:
                                targetLoc.setX(1376);
                                targetLoc.setY(47);
                                targetLoc.setZ(895);
                                target.teleport(targetLoc);

                                resurrectionPlayer(target);
                                checkPointTask(target, 2);
                                break;

                            case 2:
                                targetLoc.setX(665);
                                targetLoc.setY(132);
                                targetLoc.setZ(1169);
                                target.teleport(targetLoc);

                                resurrectionPlayer(target);
                                checkPointTask(target, 2);
                                break;

                            case 3:
                                targetLoc.setX(1403);
                                targetLoc.setY(28);
                                targetLoc.setZ(1530);
                                target.teleport(targetLoc);

                                resurrectionPlayer(target);
                                checkPointTask(target,2);
                        }
                    }
                }

                //チェックポイントから出る（2回目）
                if (loc.getBlock().getType().equals(Material.REDSTONE_BLOCK)) {
                    for (Player target : L4D_gamepl.getPlayerList()) {
                        Location targetLoc = target.getLocation().clone();
                        switch (GameWorlds.getStageId()) {
                            case 1:
                                targetLoc.setX(1336);
                                targetLoc.setY(42);
                                targetLoc.setZ(877);
                                target.teleport(targetLoc);
                                target.sendMessage(ChatColor.AQUA + "チェックポイントから出ました");
                                target.sendMessage(ChatColor.AQUA + "ゲームを再開します");
                                break;

                            case 2:
                                targetLoc.setX(733);
                                targetLoc.setY(87);
                                targetLoc.setZ(1150);
                                target.teleport(targetLoc);
                                target.sendMessage(ChatColor.AQUA + "チェックポイントから出ました");
                                target.sendMessage(ChatColor.AQUA + "ゲームを再開します");
                                break;

                            case 3:
                                targetLoc.setX(1343);
                                targetLoc.setY(4);
                                targetLoc.setZ(1575);
                                target.teleport(targetLoc);
                                target.sendMessage(ChatColor.AQUA + "チェックポイントから出ました");
                                target.sendMessage(ChatColor.AQUA + "ゲームを再開します");
                                break;

                        }
                    }
                }

                //ゴールする
                if (loc.getBlock().getType().equals(Material.LAPIS_BLOCK)) {
                    for (Player target : L4D_gamepl.getPlayerList()) {
                        Location targetLoc = target.getLocation().clone();
                        targetLoc.setX(914);
                        targetLoc.setY(156);
                        targetLoc.setZ(1033);
                        target.teleport(targetLoc);

                        spawnFireworks(targetLoc, 1);
                    }

                    Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 0));
                    Bukkit.getOnlinePlayers().forEach(p -> p.sendTitle(ChatColor.AQUA + "GAME CLEAR!", null, 5, 100, 5));
                    new StopTask(pl).runTaskTimer(pl, 0, 20);
                }

            }
        }
    }
}
