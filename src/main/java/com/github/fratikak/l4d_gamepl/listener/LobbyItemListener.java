package com.github.fratikak.l4d_gamepl.listener;

import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import com.github.fratikak.l4d_gamepl.task.PreparationTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

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

        ItemStack venice = new ItemStack(Material.BRICKS);
        ItemMeta bricks = venice.getItemMeta();
        bricks.setDisplayName(ChatColor.AQUA + "Venice");
        venice.setItemMeta(bricks);

        ItemStack town = new ItemStack(Material.WHITE_WOOL);
        ItemMeta white_wool = town.getItemMeta();
        white_wool.setDisplayName(ChatColor.AQUA + "Town");
        town.setItemMeta(white_wool);

        //インベントリ作成、メタデータのあるアイテムをセットする
        Inventory inventory;
        inventory = Bukkit.createInventory(null, 9, "ステージを選択してください");

        inventory.setItem(0, venice);
        inventory.setItem(1, town);

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
        if (player.getItemInHand().getType() == Material.DIAMOND) {

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
        
        //どのアイテム（ステージ）をクリックしたか
        switch (event.getCurrentItem().getType()) {
            case BRICKS: //Venice
                event.setCancelled(true);

                if(L4D_gamepl.isPreparation()){
                    return;
                }

                new PreparationTask(pl, 1).runTaskTimer(pl, 0, 20);
                break;

            case WHITE_WOOL: //Town
                event.setCancelled(true);

                if(L4D_gamepl.isPreparation()){
                    return;
                }

                new PreparationTask(pl, 2).runTaskTimer(pl, 0, 20);
                break;

            default:
                return;
        }

        //イベントがキャンセルされた（ステージ選択に成功した）
        if (event.isCancelled()){
            L4D_gamepl.getPlayerList().add((Player) event.getWhoClicked());
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
    public void EmeraldInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        //エメラルドでインタラクトした
        if (player.getItemInHand().getType() == Material.EMERALD) {

            //ゲーム中の場合は途中参加。
            if (L4D_gamepl.isGame()) {

                //ゲームに参加しているならばreturn
                if (L4D_gamepl.getPlayerList().contains(player)){
                    player.sendMessage("[L4D]" + ChatColor.RED + "すでにゲームに参加しています");
                    return;
                }

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 24);
                player.sendMessage("[L4D]" + ChatColor.AQUA + "ゲームに途中参加します");

                //生存プレイヤーの誰かの所へテレポート
                for (Player target : L4D_gamepl.getSurvivorList()) {
                    player.teleport(target);
                    break;
                }

                //プレイヤーリストに格納、初期ゲームアイテムを付与
                L4D_gamepl.getPlayerList().add(player);
                L4D_gamepl.getSurvivorList().add(player);
                pl.giveGameItem(player.getInventory(), player);
                return;
            }

            //PreparationTask中の場合はプレイヤーリストに格納するだけ
            if (L4D_gamepl.isPreparation()){

                //ゲームに参加していた
                if (L4D_gamepl.getPlayerList().contains(player)){
                    player.sendMessage("[L4D]" + ChatColor.RED + "すでにゲームに参加しています");
                    return;
                }

                Bukkit.broadcastMessage("[L4D]" + ChatColor.AQUA + player.getDisplayName() + "がゲームに参加します");
                Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 24));

                L4D_gamepl.getPlayerList().add(player);

            }else {
                player.sendMessage("[L4D]" + ChatColor.RED + "ステージを選択してください");
            }
        }
    }
}
