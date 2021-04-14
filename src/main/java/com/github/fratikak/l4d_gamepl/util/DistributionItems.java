package com.github.fratikak.l4d_gamepl.util;

import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import com.shampaggon.crackshot.CSUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * 武器やロビーのアイテムについてのクラス
 */
public class DistributionItems {

    private final L4D_gamepl pl;
    public DistributionItems(L4D_gamepl pl){
        this.pl = pl;
    }

    /**
     * 引数のプレイヤーインベントリにロビーアイテムを付与する
     *
     * @param player アイテムを与えたいプレイヤー
     */
    public void giveLobbyItem(Player player) {
        Inventory inventory = player.getInventory();

        //ステージ選択のダイアモンド
        ItemStack diamond = setDisplayMetaItem(new ItemStack(Material.DIAMOND),ChatColor.AQUA,"ステージ選択");

        //途中参加ができるエメラルド
        ItemStack emerald = setDisplayMetaItem(new ItemStack(Material.EMERALD),ChatColor.GREEN,"ゲームに参加する");

        //PERKDECKを選択できるエンドクリスタル
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
     * @param player アイテムを与えたいプレイヤー
     */
    public void giveGameItem(Player player) {
        Inventory inventory = player.getInventory();
        inventory.clear();

        ItemStack firework = setDisplayMetaItem(new ItemStack(Material.FIREWORK_STAR,2),ChatColor.YELLOW,"グレネード");
        ItemStack clayball = setDisplayMetaItem(new ItemStack(Material.CLAY_BALL,1),ChatColor.YELLOW,"コンカッション");

        CSUtility csUtility = new CSUtility();
        csUtility.giveWeapon(player, "MAC10", 1);
        csUtility.giveWeapon(player, "P226", 1);

        inventory.addItem(firework);
        inventory.addItem(clayball);
        inventory.addItem(new ItemStack(Material.COOKED_BEEF, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 0, true));

        //対応したメタデータの能力を付与する
        new PerkDecks(player, pl).setPeekDeck();

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
    private static ItemStack setDisplayMetaItem(ItemStack item, ChatColor color, String name) {
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(color + name);
        item.setItemMeta(itemMeta);
        return item;
    }
}
