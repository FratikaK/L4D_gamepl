package com.github.fratikak.l4d_gamepl.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Items implements Listener {

    /**
     * ゲーム内アイテムに関するクラス
     * @author FratikaK
     */

    @EventHandler
    public void foodItems(PlayerItemConsumeEvent event){
        Player player = event.getPlayer();
        switch (event.getItem().getType()){
            case COOKED_BEEF:
                player.setHealth(20);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 24);
                player.sendMessage(ChatColor.GOLD + "ステーキを食べたことにより体力が全快しました");
                break;

            case APPLE:
                player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL,1,1,false));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 24);
                player.getInventory().remove(Material.HONEY_BOTTLE);
                player.sendMessage(ChatColor.GOLD + "リンゴを食べたことにより体力が少量回復しました");
                break;
        }
    }
}
