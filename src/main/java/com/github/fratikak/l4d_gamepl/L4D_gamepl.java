package com.github.fratikak.l4d_gamepl;

import com.github.fratikak.l4d_gamepl.command.L4DCommands;
import com.github.fratikak.l4d_gamepl.listener.*;
import com.github.fratikak.l4d_gamepl.task.LagFixTask;
import com.github.fratikak.l4d_gamepl.util.PerkDecks;
import com.shampaggon.crackshot.CSUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
    private static boolean preparation = false;
    private static final List<Player> playerList = new ArrayList<>();
    private static final List<Player> survivor = new ArrayList<>();
    private static final List<Player> deathPlayer = new ArrayList<>();


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
        getServer().getPluginManager().registerEvents(new LobbyItemListener(this), this);
        getServer().getPluginManager().registerEvents(new SetPeekDeckListener(this), this);

        //タスクの実行
        new LagFixTask().runTaskTimer(this, 0, 20 * 60);
    }

    @Override
    public void onDisable() {
    }

    /**
     * 引数のプレイヤーインベントリにロビーアイテムを付与する
     *
     * @param inventory 対象プレイヤーのインベントリ
     */
    public void giveLobbyItem(Inventory inventory) {

        //ステージ選択のダイアモンド
        ItemStack diamond = setDisplayMetaItem(new ItemStack(Material.DIAMOND),ChatColor.AQUA,"ステージ選択");

        //途中参加ができるエメラルド
        ItemStack emerald = setDisplayMetaItem(new ItemStack(Material.EMERALD),ChatColor.GREEN,"ゲームに参加する");

        //PEEKDECKを選択できるエンドクリスタル
        ItemStack end = setDisplayMetaItem(new ItemStack(Material.END_CRYSTAL),ChatColor.GOLD,"PERK選択");

        //メタデータをつけたアイテムを付与
        inventory.clear();
        inventory.setItem(0, diamond);
        inventory.setItem(8, emerald);
        inventory.setItem(1, end);
    }

    /**
     * ゲーム用初期アイテムを付与する
     *
     * @param inventory 対象プレイヤーのインベントリ
     * @param player    対象プレイヤー
     */
    public void giveGameItem(Inventory inventory, Player player) {
        inventory.clear();

        ItemStack firework = setDisplayMetaItem(new ItemStack(Material.FIREWORK_STAR,3),ChatColor.YELLOW,"グレネード");
        ItemStack clayball = setDisplayMetaItem(new ItemStack(Material.CLAY_BALL,3),ChatColor.YELLOW,"コンカッション");

        new CSUtility().giveWeapon(player, "MAC10", 1);
        new CSUtility().giveWeapon(player, "P226", 1);

        inventory.addItem(firework);
        inventory.addItem(clayball);
        inventory.addItem(new ItemStack(Material.COOKED_BEEF, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 0, true));

        //対応したメタデータの能力を付与する
        new PerkDecks(player, this).setPeekDeck();

        //tabリストを変更
        player.setPlayerListName("[" + ChatColor.AQUA + "生存者" + ChatColor.WHITE + "]" + player.getDisplayName());

    }

    /**
     * メタデータを付与したアイテムを返す
     *
     * @param item  メタデータを付与したいアイテム
     * @param color 名前につけたい色
     * @param name  付与したい名前
     * @return　メタデータをセットしたアイテム
     */
    private ItemStack setDisplayMetaItem(ItemStack item, ChatColor color, String name) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(color + name);
        item.setItemMeta(itemMeta);
        return item;
    }

    //ゲッターセッター
    public static boolean isGame() {
        return game;
    }

    public static void setGame(boolean game) {
        L4D_gamepl.game = game;
    }

    public static boolean isPreparation() {
        return preparation;
    }

    public static void setPreparation(boolean preparation) {
        L4D_gamepl.preparation = preparation;
    }

    public static List<Player> getPlayerList() {
        return playerList;
    }

    public static List<Player> getSurvivorList() {
        return survivor;
    }

    public static List<Player> getDeathPlayerList() {
        return deathPlayer;
    }
}