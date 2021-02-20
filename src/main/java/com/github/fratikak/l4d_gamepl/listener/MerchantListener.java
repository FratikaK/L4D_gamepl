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

        if (!L4D_gamepl.isCheckPoint()) {
            return;
        }

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

            case GOLDEN_SHOVEL:
                dealings(player, Material.GOLDEN_SHOVEL, 300, "GL");
                break;
        }
    }

    /**
     * 引数のプレイヤーに取引画面を表示する
     *
     * @param player
     */
    private void showMerchantInventory(Player player) {
        if (!L4D_gamepl.isCheckPoint()) {
            return;
        }
        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.AQUA + "アイテム取引");

        ItemStack firework = getMetaItem(Material.FIREWORK_STAR, "グレネード", "$20");
        ItemStack clay = getMetaItem(Material.CLAY_BALL, "コンカッション", "$70");
        ItemStack apple = getMetaItem(Material.APPLE, "回復のリンゴ", "$50");
        ItemStack beef = getMetaItem(Material.COOKED_BEEF, "ステーキ", "$120");
        ItemStack minecart = getMetaItem(Material.FURNACE_MINECART, "Landmine", "$30");
        ItemStack shovel = getMetaItem(Material.GOLDEN_SHOVEL, "GL", "$300");

        inventory.setItem(0, firework);
        inventory.setItem(1, clay);
        inventory.setItem(2, apple);
        inventory.setItem(3, beef);
        inventory.setItem(4, minecart);
        inventory.setItem(5, shovel);

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
