package com.github.fratikak.l4d_gamepl.listener;

import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * アイテム取引が可能になるmobのリスナ
 *
 * @author FratikaK
 */
public class MerchantListener implements Listener {

    /**
     * 村人にインタラクトすると取引画面を表示する
     *
     * @param event
     */
    @EventHandler
    public void merchantInteract(PlayerInteractEntityEvent event) {

        //村人でなければreturn
        if (event.getRightClicked().getType() != EntityType.VILLAGER) {
            return;
        }

        event.setCancelled(true);
        showMerchantInventory(event.getPlayer());
    }

    /**
     * 取引画面のアイテムをクリックするとそれに対応した取引をする
     *
     * @param event
     */
    @EventHandler
    public void buyItemEvent(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();

        if (!event.getCurrentItem().getItemMeta().hasCustomModelData()) {
            return;
        }

        event.setCancelled(true);

        switch (Objects.requireNonNull(event.getCurrentItem()).getType()) {
            case FIREWORK_STAR:
                dealings(player, Material.FIREWORK_STAR, 20, "グレネード");
                break;

            case CLAY_BALL:
                dealings(player, Material.CLAY_BALL, 70, "コンカッション");
                break;

            case APPLE:
                dealings(player, Material.APPLE, 50, "リンゴ");
                break;

            case COOKED_BEEF:
                dealings(player, Material.COOKED_BEEF, 120, "ステーキ");
                break;

            case FURNACE_MINECART:
                dealings(player, Material.FURNACE_MINECART, 30, "Landmine");
                break;

            //ここから武器関係
            case WOODEN_SHOVEL:
                dealings(player, Material.WOODEN_SHOVEL, 200, "MAC10");
                break;

            case BLAZE_ROD:
                dealings(player, Material.BLAZE_ROD, 200, "M31");
                break;

            case DIAMOND_AXE:
                dealings(player, Material.DIAMOND_AXE, 450, "M16A1");
                break;

            case CARROT_ON_A_STICK:
                dealings(player, Material.CARROT_ON_A_STICK, 450, "SCARL");
                break;

            case GOLDEN_HOE:
                dealings(player, Material.GOLDEN_HOE, 450, "AK47");
                break;

            case DIAMOND_HORSE_ARMOR:
                dealings(player, Material.DIAMOND_HORSE_ARMOR, 450, "SPAS12");
                break;

            case GOLDEN_HORSE_ARMOR:
                dealings(player,Material.GOLDEN_HORSE_ARMOR,600,"Saiga");
                break;

            case BONE:
                dealings(player, Material.BONE, 600, "Chopper");
                break;

            case GOLDEN_SHOVEL:
                dealings(player, Material.GOLDEN_SHOVEL, 600, "グレネードランチャー");
                break;

            case DIAMOND_SHOVEL:
                dealings(player, Material.DIAMOND_SHOVEL, 200, "50AE");
                break;

            default:
                break;
        }
    }

    /**
     * 引数のプレイヤーに取引画面を表示する
     *
     * @param player
     */
    private void showMerchantInventory(Player player) {

        String traderText = "Trade Items";

        if (!L4D_gamepl.isGame()) {
            traderText = "Trial Weapons";
        }

        Inventory inventory = Bukkit.createInventory(null, 54, traderText);

        ItemStack firework = getMetaItem(Material.FIREWORK_STAR, "グレネード", "$20");
        ItemStack clay = getMetaItem(Material.CLAY_BALL, "コンカッション", "$70");
        ItemStack apple = getMetaItem(Material.APPLE, "回復のリンゴ", "$50");
        ItemStack beef = getMetaItem(Material.COOKED_BEEF, "ステーキ", "$120");
        ItemStack minecart = getMetaItem(Material.FURNACE_MINECART, "Landmine", "$30");

        ItemStack wooden_shovel = getMetaItem(Material.WOODEN_SHOVEL, "MAC10サブマシンガン", "$200");
        ItemStack blaze_rod = getMetaItem(Material.BLAZE_ROD, "M31ショットガン", "$200");
        ItemStack diamond_axe = getMetaItem(Material.DIAMOND_AXE, "M16A1アサルトライフル", "$450");
        ItemStack carrot = getMetaItem(Material.CARROT_ON_A_STICK, "SCARLアサルトライフル", "$450");
        ItemStack golden_hoe = getMetaItem(Material.GOLDEN_HOE, "AK47アサルトライフル", "$450");

        ItemStack diamond_horse = getMetaItem(Material.DIAMOND_HORSE_ARMOR, "SPAS12ショットガン", "$450");
        ItemStack golden_horse = getMetaItem(Material.GOLDEN_HORSE_ARMOR, "Saigaセミオートショットガン", "$600");
        ItemStack bone = getMetaItem(Material.BONE, "Chopperライトマシンガン", "$600");
        ItemStack shovel = getMetaItem(Material.GOLDEN_SHOVEL, "グレネードランチャー", "$600");
        ItemStack diamond_shovel = getMetaItem(Material.DIAMOND_SHOVEL, "50AE", "$200");

        inventory.setItem(0, firework);
        inventory.setItem(1, clay);
        inventory.setItem(2, apple);
        inventory.setItem(3, beef);
        inventory.setItem(4, minecart);

        inventory.setItem(18, wooden_shovel);
        inventory.setItem(19, blaze_rod);
        inventory.setItem(20, diamond_axe);
        inventory.setItem(21, carrot);
        inventory.setItem(22, golden_hoe);
        inventory.setItem(23, diamond_horse);

        inventory.setItem(36, golden_horse);
        inventory.setItem(37, bone);
        inventory.setItem(38, shovel);
        inventory.setItem(39, diamond_shovel);

        player.openInventory(inventory);
    }

    /**
     * メタデータを付与したアイテムを返す
     *
     * @param material    表示したいマテリアル
     * @param displayName アイテム名
     * @param lore        アイテム説明文
     * @return メタデータを付与したアイテム
     */
    private ItemStack getMetaItem(Material material, String displayName, String lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(ChatColor.AQUA + displayName);
        List<String> lores = new ArrayList<>();
        lores.add(ChatColor.GOLD + lore);
        itemMeta.setLore(lores);
        itemMeta.setCustomModelData(1);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    /**
     * 取引を行う処理
     *
     * @param player   取引を行うプレイヤー
     * @param material 購入するアイテムのタイプ
     * @param money    購入するアイテムの値段
     * @param itemName 購入するアイテムの名前
     */
    private void dealings(Player player, Material material, int money, String itemName) {
        //ゲーム中でなければ無料で試せる
        if (!L4D_gamepl.isGame()) {
            ItemStack itemStack = new ItemStack(material);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.YELLOW + itemName);
            itemStack.setItemMeta(itemMeta);

            player.getInventory().addItem(itemStack);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
            return;
        }

        //所持金が足りなければreturn
        if (player.getStatistic(Statistic.ANIMALS_BRED) < money) {
            player.sendMessage(ChatColor.RED + "所持金が足りません！");
            return;
        }

        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + itemName);
        itemStack.setItemMeta(itemMeta);

        player.setStatistic(Statistic.ANIMALS_BRED, player.getStatistic(Statistic.ANIMALS_BRED) - money);
        player.getInventory().addItem(itemStack);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);

        player.sendMessage(ChatColor.AQUA + itemName + "を購入しました");
    }
}
