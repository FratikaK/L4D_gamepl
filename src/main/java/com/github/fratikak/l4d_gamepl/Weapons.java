package com.github.fratikak.l4d_gamepl;

import com.shampaggon.crackshot.CSUtility;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
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
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 24);
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

            case "SPAS12":
                new CSUtility().giveWeapon(player,weaponName,1);
                player.sendMessage(ChatColor.AQUA + "SPAS12を装備しました");
                break;

            case "SCARL":
                new CSUtility().giveWeapon(player,weaponName,1);
                player.sendMessage(ChatColor.AQUA + "SCARLを装備しました");
                break;
        }
    }

    //特定のブロックを叩くとそれに対応した武器を装備する
    @EventHandler(priority = EventPriority.HIGH)
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

                case NETHER_GOLD_ORE:
                    setPrimaryWeapon(player,"SPAS12");
                    break;

                case DIAMOND_ORE:
                    setPrimaryWeapon(player,"SCARL");
                    break;
        }
    }
}
