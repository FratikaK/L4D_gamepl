package com.github.fratikak.l4d_gamepl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LobbyItemGUI implements Listener {

    /**
     * ロビーアイテムをクリック、もしくはインタラクトすることで
     * 専用のGUIを表示する
     * 武器選択の画面を表示する
     *
     * @author FratikaK
     */

    private final L4D_gamepl pl;

    //コンストラクタ
    public LobbyItemGUI(L4D_gamepl pl) {
        this.pl = pl;
    }

    //武器選択画面を表示
    public void openWeaponSelectGUI(Player player){
        ItemStack AK_Leather = new ItemStack(Material.LEATHER);
        ItemStack Chopper_diamond_hoe = new ItemStack(Material.DIAMOND_HOE);
        ItemStack Reinfeld_880_diamond_shovel = new ItemStack(Material.DIAMOND_SHOVEL);

        ItemMeta AK = AK_Leather.getItemMeta();
        AK.setDisplayName("AK47");
        AK_Leather.setItemMeta(AK);

        ItemMeta Chopper = Chopper_diamond_hoe.getItemMeta();
        Chopper.setDisplayName("Chopper");
        Chopper_diamond_hoe.setItemMeta(Chopper);

        ItemMeta Reinfeld_880 = Reinfeld_880_diamond_shovel.getItemMeta();
        Reinfeld_880.setDisplayName("Reinfeld880");
        Reinfeld_880_diamond_shovel.setItemMeta(Reinfeld_880);

        Inventory weaponGUI = Bukkit.createInventory(null,9,"武器選択");
        weaponGUI.setItem(0, AK_Leather);
        weaponGUI.setItem(1,Chopper_diamond_hoe);
        weaponGUI.setItem(2,Reinfeld_880_diamond_shovel);
    }

    @EventHandler
    public void InteractDiamond(PlayerInteractEvent event){
        Player player = event.getPlayer();

    }
}
