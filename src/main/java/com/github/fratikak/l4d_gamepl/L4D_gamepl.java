package com.github.fratikak.l4d_gamepl;

import com.shampaggon.crackshot.CSUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * メインクラス
 * イベント、コマンド読み込み
 *
 * @author FratikaK
 */

public final class L4D_gamepl extends JavaPlugin {

    private static boolean game = false;
    private static List<Player> playerList = new ArrayList<>();
    private static List<Player> deathPlayer = new ArrayList<>();


    @Override
    public void onEnable() {
        getLogger().info("L4DGame START!");

        //コマンドを扱うクラスの登録
        Objects.requireNonNull(getCommand("l4d")).setExecutor(new L4DCommands(this));

        //イベントリスナの登録
        getServer().getPluginManager().registerEvents(new Login(this), this);
        getServer().getPluginManager().registerEvents(new PlayerEvent(this), this);
        getServer().getPluginManager().registerEvents(new DamageOff(this), this);
        getServer().getPluginManager().registerEvents(new GameLogic(this), this);
        getServer().getPluginManager().registerEvents(new L4DMobs(this), this);
        getServer().getPluginManager().registerEvents(new CheckPoint(this), this);
        getServer().getPluginManager().registerEvents(new Weapons(), this);
        getServer().getPluginManager().registerEvents(new Items(), this);
    }

    @Override
    public void onDisable() {
    }

    //ロビーアイテムを付与 今はインベントリ削除のみ
    public void giveLobbyItem(Inventory inventory) {

//        ItemStack diamond = new ItemStack(Material.DIAMOND);
        inventory.clear();
//        inventory.setItem(0, diamond);
    }

    /**
     * ゲーム用初期アイテムを付与する
     *
     * @param inventory 対象プレイヤーのインベントリ
     * @param player    対象プレイヤー
     */
    public void giveGameItem(Inventory inventory, Player player) {
        inventory.clear();
        ItemStack firework = new ItemStack(Material.FIREWORK_STAR, 5);
        ItemStack clayball = new ItemStack(Material.CLAY_BALL, 5);

        ItemMeta fwmeta = firework.getItemMeta();
        ItemMeta cbmeta = clayball.getItemMeta();
        fwmeta.setDisplayName(ChatColor.YELLOW + "グレネード");
        cbmeta.setDisplayName(ChatColor.YELLOW + "フラッシュバン");
        firework.setItemMeta(fwmeta);
        clayball.setItemMeta(cbmeta);

        new CSUtility().giveWeapon(player, "MAC10", 1);
        new CSUtility().giveWeapon(player, "P226", 1);

        inventory.addItem(firework);
        inventory.addItem(clayball);
        inventory.addItem(new ItemStack(Material.COOKED_BEEF, 3));

    }

    //ゲッターセッター
    public static boolean isGame() {
        return game;
    }

    public static void setGame(boolean game) {
        L4D_gamepl.game = game;
    }

    public static List<Player> getPlayerList() {
        return playerList;
    }

    public static List<Player> getDeathPlayer() {
        return deathPlayer;
    }
}