package com.github.fratikak.l4d_gamepl;

import com.shampaggon.crackshot.CSUtility;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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

        inventory.addItem(new ItemStack(Material.GOLDEN_APPLE, 32));
        inventory.addItem(new ItemStack(Material.BAKED_POTATO, 64));

        new CSUtility().giveWeapon(target, "GRENADE", 10);
    }

    //死亡プレイヤーを復活させる
    public void resurrectionPlayer(Player player){

        if (player.getGameMode() == GameMode.SPECTATOR && L4D_gamepl.isGame()){
            for (Player target : L4D_gamepl.getDeathPlayer()){
                if (player == target){
                    L4D_gamepl.getDeathPlayer().remove(player);
                    L4D_gamepl.getPlayerList().add(player);
                    pl.getLogger().info(ChatColor.AQUA + player.getDisplayName() + "が復活しました");
                    pl.getLogger().info(ChatColor.AQUA + "DeathPlayerList :" + L4D_gamepl.getDeathPlayer());
                    pl.getLogger().info(ChatColor.AQUA + "PlayerList :" + L4D_gamepl.getPlayerList());

                    player.setGameMode(GameMode.SURVIVAL);
                    pl.giveGameItem(player.getInventory(),player);
                }
            }
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
                    for (Player target : Bukkit.getOnlinePlayers()) {
                        switch (GameWorlds.getStageId()) {
                            case 1:
                                Location targetLoc = target.getLocation().clone();
                                targetLoc.setX(1389);
                                targetLoc.setY(42);
                                targetLoc.setZ(928);
                                target.teleport(targetLoc);

                                target.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 30, 2), true);
                                resurrectionPlayer(target);
                                sendCheckPointItem(target, target.getInventory());
                                target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 25);
                                target.sendMessage(ChatColor.AQUA + "最初のチェックポイントにたどり着きました！");
                                target.sendMessage(ChatColor.AQUA + "ゴールドブロックを踏むとゲームが再開します！");
                        }
                    }
                }

                //チェックポイントから出る（1回目）
                if (loc.getBlock().getType().equals(Material.GOLD_BLOCK)) {
                    for (Player target : Bukkit.getOnlinePlayers()) {
                        switch (GameWorlds.getStageId()) {
                            case 1:
                                Location targetLoc = target.getLocation().clone();
                                targetLoc.setX(1400);
                                targetLoc.setY(42);
                                targetLoc.setZ(918);
                                target.teleport(targetLoc);

                                target.sendMessage(ChatColor.AQUA + "チェックポイントから出ました");
                                target.sendMessage(ChatColor.AQUA + "ゲームを再開します");

                        }
                    }
                }
                
                //チェックポイントに入る（2回目）
                if (loc.getBlock().getType().equals(Material.EMERALD_BLOCK)) {
                    for (Player target : Bukkit.getOnlinePlayers()) {
                        switch (GameWorlds.getStageId()) {
                            case 1:
                                Location targetLoc = target.getLocation().clone();
                                targetLoc.setX(1376);
                                targetLoc.setY(47);
                                targetLoc.setZ(895);
                                target.teleport(targetLoc);

                                target.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 30, 2), true);
                                resurrectionPlayer(target);
                                sendCheckPointItem(target, target.getInventory());
                                target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 25);
                                target.sendMessage(ChatColor.AQUA + "2のチェックポイントにたどり着きました！");
                                target.sendMessage(ChatColor.AQUA + "レッドストーンブロックを踏むとゲームが再開します！");

                        }
                    }
                }

                //チェックポイントから出る（2回目）
                if (loc.getBlock().getType().equals(Material.REDSTONE_BLOCK)) {
                    for (Player target : Bukkit.getOnlinePlayers()) {
                        switch (GameWorlds.getStageId()) {
                            case 1:
                                Location targetLoc = target.getLocation().clone();
                                targetLoc.setX(1336);
                                targetLoc.setY(42);
                                targetLoc.setZ(877);
                                target.teleport(targetLoc);
                                target.sendMessage(ChatColor.AQUA + "チェックポイントから出ました");
                                target.sendMessage(ChatColor.AQUA + "ゲームを再開します");

                        }
                    }
                }

                //ゴールする
                if (loc.getBlock().getType().equals(Material.LAPIS_BLOCK)) {
                    new Stop(pl).stopGame();
                    GameWorlds.setStageId(0);
                }

            }
        }
    }
}
