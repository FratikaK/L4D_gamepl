package com.github.fratikak.l4d_gamepl.listener;

import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import com.github.fratikak.l4d_gamepl.task.PreparationTask;
import com.github.fratikak.l4d_gamepl.util.ScoreboardSystem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

/**
 * ロビーアイテムをインタラクトした場合、
 * 特定のアイテムにあわせて処理をする
 *
 * @author FratikaK
 */
public class LobbyItemListener implements Listener {


    private final L4D_gamepl pl;

    /**
     * コンストラクタ
     *
     * @param pl このプラグイン
     */
    public LobbyItemListener(L4D_gamepl pl) {
        this.pl = pl;
    }

    /**
     * 引数のプレイヤーにステージ選択画面を表示する
     *
     * @param player 対象プレイヤー
     */
    private void openStageGUI(Player player) {

        ItemStack venice = setStageMeta(new ItemStack(Material.BRICKS), "Venice");

        ItemStack town = setStageMeta(new ItemStack(Material.WHITE_WOOL), "Town");

        ItemStack novigrad = setStageMeta(new ItemStack(Material.COBBLESTONE), "Novigrad");

        //インベントリ作成、メタデータのあるアイテムをセットする
        Inventory inventory;
        inventory = Bukkit.createInventory(null, 9, "ステージを選択してください");

        inventory.setItem(0, venice);
        inventory.setItem(1, town);
        inventory.setItem(2, novigrad);

        //インベントリ表示
        player.openInventory(inventory);
    }

    /**
     * ダイアモンドでインタラクトした場合に、ステージ選択画面を表示する
     *
     * @param event
     */
    @EventHandler
    public void diamondInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        //ダイアモンドでインタラクトした
        if (player.getItemInHand().getType() == Material.DIAMOND && event.getAction() == Action.RIGHT_CLICK_AIR) {

            //ゲーム中の場合はreturn
            if (L4D_gamepl.isGame()) {
                player.sendMessage("[L4D]" + ChatColor.RED + "ゲーム中です");
                return;
            }

            //準備フェーズ中はreturn
            if (L4D_gamepl.isPreparation()) {
                player.sendMessage("[L4D]" + ChatColor.RED + "準備フェーズ中にステージを選択することは出来ません");
                return;
            }

            //問題なければステージ選択のGUIを表示する
            openStageGUI(player);
        }
    }

    /**
     * ステージ選択画面のアイテムをクリックすると
     * そのアイテムに対応したステージを選択し、ゲームを開始する
     *
     * @param event
     */
    @EventHandler
    public void stageClick(InventoryClickEvent event) {

        //preparationタスク中はreturn
        if (L4D_gamepl.isPreparation()) {
            return;
        }

        //どのアイテム（ステージ）をクリックしたか
        switch (event.getCurrentItem().getType()) {
            case BRICKS: //Venice
                event.setCancelled(true);
                new PreparationTask(pl, 1).runTaskTimer(pl, 0, 20);
                event.getWhoClicked().closeInventory();
                break;

            case WHITE_WOOL: //Town
                event.setCancelled(true);
                new PreparationTask(pl, 2).runTaskTimer(pl, 0, 20);
                event.getWhoClicked().closeInventory();
                break;

            case COBBLESTONE:
                event.setCancelled(true);
                new PreparationTask(pl, 3).runTaskTimer(pl, 0, 20);
                event.getWhoClicked().closeInventory();
                break;

            default:
                return;
        }

        //イベントがキャンセルされた（ステージ選択に成功した）
        if (event.isCancelled()) {
            L4D_gamepl.getPlayerList().add(event.getWhoClicked().getUniqueId());
        }
    }

    /**
     * エメラルドをインタラクトした場合
     * PreparationTask中であればプレイヤーリストに入れる
     * ゲーム進行中であればゲーム中のプレイヤーの所へテレポート、
     * ゲームアイテムを付与して途中参加させる
     *
     * @param event
     */
    @EventHandler
    public void emeraldInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        //エメラルドでインタラクトした
        if (player.getItemInHand().getType() == Material.EMERALD && event.getAction() == Action.RIGHT_CLICK_AIR) {

            //ゲーム中の場合は途中参加。
            if (L4D_gamepl.isGame()) {

                //ゲームに参加しているならばreturn
                if (L4D_gamepl.getPlayerList().contains(player.getUniqueId())) {
                    player.sendMessage("[L4D]" + ChatColor.RED + "すでにゲームに参加しています");
                    return;
                }

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 24);
                player.sendMessage("[L4D]" + ChatColor.AQUA + "ゲームに途中参加します");

                //生存プレイヤーの誰かの所へテレポート
                for (UUID playerId : L4D_gamepl.getSurvivorList()) {
                    for (Player target : Bukkit.getOnlinePlayers()) {
                        if (target.getUniqueId().equals(playerId)) {
                            player.teleport(target);
                            break;
                        }
                    }
                }

                //プレイヤーリストに格納、初期ゲームアイテムを付与
                L4D_gamepl.getPlayerList().add(player.getUniqueId());
                L4D_gamepl.getSurvivorList().add(player.getUniqueId());
                pl.giveGameItem(player.getInventory(), player);

                player.setFoodLevel(6);
                player.setHealth(20);

                //キル数、所持金リセット
                player.setStatistic(Statistic.MOB_KILLS, 0);
                player.setStatistic(Statistic.ANIMALS_BRED, 0);

                return;
            }

            //PreparationTask中の場合はプレイヤーリストに格納するだけ
            if (L4D_gamepl.isPreparation()) {

                //ゲームに参加していた場合はreturn
                if (L4D_gamepl.getPlayerList().contains(player.getUniqueId())) {
                    player.sendMessage("[L4D]" + ChatColor.RED + "すでにゲームに参加しています");
                    return;
                }

                Bukkit.broadcastMessage("[L4D]" + ChatColor.AQUA + player.getDisplayName() + "がゲームに参加します");
                Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 24));

                //ゲームプレイヤーとして登録
                L4D_gamepl.getPlayerList().add(player.getUniqueId());

            } else {
                player.sendMessage("[L4D]" + ChatColor.RED + "ステージを選択してください");
            }
        }
    }

    /**
     * 名前をつけたItemStackを返す
     *
     * @param item メタデータを付与したいアイテム
     * @param name つけたい名前
     * @return メタデータを付与したItemStack
     */
    private ItemStack setStageMeta(ItemStack item, String name) {
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(ChatColor.AQUA + name);
        item.setItemMeta(itemMeta);
        return item;
    }
}
