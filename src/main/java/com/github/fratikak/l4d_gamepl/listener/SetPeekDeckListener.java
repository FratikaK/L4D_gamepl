package com.github.fratikak.l4d_gamepl.listener;

import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import com.github.fratikak.l4d_gamepl.util.PerkDecks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SetPeekDeckListener implements Listener {

    private final L4D_gamepl plugin;

    public SetPeekDeckListener(L4D_gamepl plugin) {
        this.plugin = plugin;
    }

    /**
     * 引数に入れたアイテム説明を
     * 引数のマテリアルに付与してそのアイテムを返す
     *
     * @param material    メタデータをセットしたいマテリアル
     * @param displayName 変更したい名前
     * @param lore        追加したい説明文
     * @return メタデータをセットしたアイテム
     */
    private ItemStack setPeekMeta(Material material, String displayName, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();

        //名前の変更
        itemMeta.setDisplayName(ChatColor.AQUA + displayName);
        //説明文の追加
        List<String> lores = new ArrayList<>();
        lores.add(lore);
        itemMeta.setLore(lores);

        //アイテムメタのセット
        item.setItemMeta(itemMeta);

        return item;
    }

    /**
     * Peek選択ができるGUI
     *
     * @param player GUIを表示させたいプレイヤー
     */
    private void openPeekGUI(Player player) {

        //タンク
        ItemStack tank = setPeekMeta(Material.IRON_CHESTPLATE, "タンク", "防御力が上昇する");
        //グラインダー
        ItemStack grinder = setPeekMeta(Material.NETHER_STAR, "グラインダー", "敵を倒す度に体力を回復するが、" + ChatColor.RED + "防御力が低下する");
        //スカウト
        ItemStack scout = setPeekMeta(Material.FEATHER, "スカウト", "歩くスピードが上昇するが、" + ChatColor.RED + "防御力が低下する");
        //リジュネ
        ItemStack regene = setPeekMeta(Material.HONEYCOMB, "リジェネ", "徐々に体力を回復する");
        //デストロイヤー
        ItemStack destroyer = setPeekMeta(Material.TNT, "デストロイヤー", "グレネードランチャーが使用可能になる");


        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "PEEK選択");
        inventory.setItem(0, tank);
        inventory.setItem(1, grinder);
        inventory.setItem(2, scout);
        inventory.setItem(3, regene);
        inventory.setItem(4, destroyer);

        //インベントリ表示
        player.openInventory(inventory);
    }

    /**
     * エンドクリスタルでインタラクトするとPerk選択画面を表示する
     *
     * @param event
     */
    @EventHandler
    public void openPeeKInventory(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (player.getItemInHand().getType() == Material.END_CRYSTAL) {
                event.setCancelled(true);
                openPeekGUI(player);
            }
        }
    }

    /**
     * 対応するアイテムをクリックすると
     * プレイヤーにメタデータを付与する
     *
     * @param event
     */
    @EventHandler
    public void selectPerk(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryView inventoryView = player.getOpenInventory();

        PerkDecks perkDecks = new PerkDecks(player, plugin);

        switch (event.getCurrentItem().getType()) {
            case IRON_CHESTPLATE://タンク
                event.setCancelled(true);
                perkDecks.setMeta("TANK");
                player.sendMessage(ChatColor.GOLD + "PEEKをタンクに設定しました");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
                inventoryView.close();
                break;

            case NETHER_STAR://グラインダー
                event.setCancelled(true);
                perkDecks.setMeta("GRINDER");
                player.sendMessage(ChatColor.GOLD + "PEEKをグラインダーに設定しました");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
                inventoryView.close();
                break;

            case FEATHER://スカウト
                event.setCancelled(true);
                perkDecks.setMeta("SCOUT");
                player.sendMessage(ChatColor.GOLD + "PEEKをスカウトに設定しました");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
                inventoryView.close();
                break;

            case HONEYCOMB://リジュネ
                event.setCancelled(true);
                perkDecks.setMeta("REGENE");
                player.sendMessage(ChatColor.GOLD + "PEEKをリジェネに設定しました");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
                inventoryView.close();
                break;

            case TNT://デストロイヤー
                event.setCancelled(true);
                perkDecks.setMeta("DESTROYER");
                player.sendMessage(ChatColor.GOLD + "PEEKをデストロイヤーに設定しました");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
                inventoryView.close();
                break;
        }

    }

}
