package com.github.fratikak.l4d_gamepl;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
    private static boolean starting = false;
    private static int time = 0;
    private static List<String> playerList = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("L4DGame START!");
        Objects.requireNonNull(getCommand("l4d")).setExecutor(new L4DCommands(this));
        getServer().getPluginManager().registerEvents(new Login(this), this);
        getServer().getPluginManager().registerEvents(new PlayerEvent(this), this);
        getServer().getPluginManager().registerEvents(new DamageOff(this), this);
        getServer().getPluginManager().registerEvents(new GameLogic(this),this);
        getServer().getPluginManager().registerEvents(new L4DMobs(this),this);
    }

    @Override
    public void onDisable() {
    }

    //ロビーアイテムを付与
    public void giveLobbyItem(Inventory inventory) {

        ItemStack diamond = new ItemStack(Material.DIAMOND);
        inventory.clear();
        inventory.setItem(0, diamond);
    }

    //ゲーム用アイテムを付与
    public void giveGameItem(Inventory inventory){
        inventory.clear();

        ItemStack food = new ItemStack(Material.BREAD,64);
        ItemStack healItem = new ItemStack(Material.GOLDEN_APPLE,10);

        inventory.setItem(1,food);
        inventory.setItem(2,healItem);
    }


    //ゲッターセッター
    public static boolean isGame() {
        return game;
    }

    public static void setGame(boolean game) {
        L4D_gamepl.game = game;
    }

    public static boolean isStarting() {
        return starting;
    }

    public static void setStarting(boolean starting) {
        L4D_gamepl.starting = starting;
    }

    public static int getTime() {
        return time;
    }

    public static void setTime(int time) {
        L4D_gamepl.time = time;
    }

    public static List<String> getPlayerList() {
        return playerList;
    }

}
