package com.github.fratikak.l4d_gamepl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * メインクラス
 * イベント、コマンド読み込み
 */

public final class L4D_gamepl extends JavaPlugin {


    @Override
    public void onEnable() {
        getLogger().info("L4DGame START!");
        getServer().getPluginManager().registerEvents(new Login(this),this);
        getServer().getPluginManager().registerEvents(new PlayerEvent(this),this);
    }

    @Override
    public void onDisable() {

    }

    //ロビーアイテムを付与
    public void giveLobbyItem(Inventory inventory){

        ItemStack diamond = new ItemStack(Material.DIAMOND);

        inventory.setItem(0,diamond);
    }
}
