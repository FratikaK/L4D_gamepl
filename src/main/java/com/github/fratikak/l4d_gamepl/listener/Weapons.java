package com.github.fratikak.l4d_gamepl.listener;

import com.shampaggon.crackshot.CSUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.Inventory;

public class Weapons implements Listener {

    /**
     * 武器の種類クラス
     * @author FratikaK
     */


    /**
     * 特定のブロックを叩くとそれに対応した武器を装備する
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void getWeaponInteract(BlockDamageEvent event) {
        Player player = event.getPlayer();
        switch (event.getBlock().getType()) {

            /*
            ここからプライマリ武器
             */

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
                setPrimaryWeapon(player, "SPAS12");
                break;

            case DIAMOND_ORE:
                setPrimaryWeapon(player, "SCARL");
                break;

            case IRON_ORE:
                setPrimaryWeapon(player,"M16A1");
                break;

                /*
                ここからセカンダリ武器
                 */

            case GILDED_BLACKSTONE:
                setSecondaryWeapon(player, "50AE");
                break;
        }
    }

    /**
     * プライマリ武器を取得する
     *
     * @param player     対象のプレイヤー
     * @param weaponName 取得する武器名
     */
    private void setPrimaryWeapon(Player player, String weaponName) {
        removePrimaryWeapon(player.getInventory());
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 24);
        player.sendMessage(ChatColor.AQUA + "プライマリとして" + weaponName + "を装備しました");
        new CSUtility().giveWeapon(player, weaponName, 1);
    }

    /**
     * セカンダリ武器を取得する
     *
     * @param player     対象のプレイヤー
     * @param weaponname 取得する武器名
     */
    private void setSecondaryWeapon(Player player, String weaponname) {
        removeSecondaryWeapon(player.getInventory());
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 24);
        player.sendMessage(ChatColor.AQUA + "セカンダリとして" + weaponname + "を装備しました");
        new CSUtility().giveWeapon(player, weaponname, 1);
    }

    /**
     * 武器交換の為に交換用のプライマリ武器を削除する
     *
     * @param playerInventory 対象プレイヤーのインベントリ
     */
    private void removePrimaryWeapon(Inventory playerInventory) {
        playerInventory.remove(Material.GOLDEN_HOE);
        playerInventory.remove(Material.BLAZE_ROD);
        playerInventory.remove(Material.WOODEN_SHOVEL);
        playerInventory.remove(Material.CARROT_ON_A_STICK);
        playerInventory.remove(Material.DIAMOND_HORSE_ARMOR);
        playerInventory.remove(Material.BONE);
        playerInventory.remove(Material.DIAMOND_AXE);
    }

    /**
     * 武器交換の為に交換用のセカンダリ武器を削除する
     *
     * @param playerInventory 対象プレイヤーのインベントリ
     */
    private void removeSecondaryWeapon(Inventory playerInventory) {
        playerInventory.remove(Material.WOODEN_HOE);
        playerInventory.remove(Material.DIAMOND_SHOVEL);
    }
}
