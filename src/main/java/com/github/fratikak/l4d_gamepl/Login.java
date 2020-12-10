package com.github.fratikak.l4d_gamepl;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.Inventory;

public class Login implements Listener {

    private final L4D_gamepl pl;


    //コンストラクタ
    public Login(L4D_gamepl pl) {
        this.pl = pl;
    }

    //op権限を持っているかの判定
    @EventHandler
    public void loginEvent(PlayerLoginEvent event){
        Player player = event.getPlayer();

        if (player.isOp()){
            pl.getLogger().info(player.getDisplayName() + "はop権限を持っています");
        }
    }

    //サーバーjoin時にサバイバルモードにしてロビーアイテムを与える
    @EventHandler
    public void joinEvent(PlayerJoinEvent event){
        Player player = event.getPlayer();
        Inventory inventory = player.getInventory();

        event.setJoinMessage(ChatColor.AQUA + player.getDisplayName() + "がゲームに参加しました！");

        if(player.getGameMode() != GameMode.SURVIVAL){
            player.setGameMode(GameMode.SURVIVAL);
        }

        inventory.clear();

    }



}
