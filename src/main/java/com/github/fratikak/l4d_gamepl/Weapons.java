package com.github.fratikak.l4d_gamepl;

import com.shampaggon.crackshot.CSUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class Weapons implements Listener {

    /**
     * 武器の種類クラス
     */

    //プライマリ武器を変更する
    public void setPrimaryWeapon(Player player, String weaponName) {
        Inventory inventory = player.getInventory();
        inventory.clear(0);

        switch (weaponName) {
            case "AK47":
                new CSUtility().giveWeapon(player, weaponName, 1);
                player.sendMessage(ChatColor.AQUA + "AK47を装備しました");
                break;

            case "Chopper":
                new CSUtility().giveWeapon(player, weaponName, 1);
                player.sendMessage(ChatColor.AQUA + "Chopperを装備しました");
                break;

            case "M31":
                new CSUtility().giveWeapon(player, weaponName, 1);
                player.sendMessage(ChatColor.AQUA + "M31を装備しました");
                break;

            case "Mac10":
                new CSUtility().giveWeapon(player, weaponName, 1);
                player.sendMessage(ChatColor.AQUA + "Mac10を装備しました");
                break;
        }
    }

    @EventHandler
    public void getWeaponInteract(BlockDamageEvent event) {
        Player player = event.getPlayer();
            switch (event.getBlock().getType()) {
                case REDSTONE_ORE:
                    setPrimaryWeapon(player, "M31");
                    break;

                case COAL_ORE:
                    setPrimaryWeapon(player, "Mac10");
                    break;

                case NETHER_QUARTZ_ORE:
                    setPrimaryWeapon(player, "AK47");
                    break;

                case EMERALD_ORE:
                    setPrimaryWeapon(player, "Chopper");
                    break;
        }
    }
}
