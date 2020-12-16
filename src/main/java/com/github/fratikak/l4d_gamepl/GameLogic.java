package com.github.fratikak.l4d_gamepl;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

public class GameLogic implements Listener {

    private final L4D_gamepl pl;

    //コンストラクタ
    public GameLogic(L4D_gamepl pl) {
        this.pl = pl;
    }

    /**
     * ゲーム開始後から修了までの処理を記述する
     *
     * ・プレイヤーが死亡した場合にstaticプレイヤーリストから削除
     * ・同じくプレイヤーが死亡した場合にプレイヤーリストネームタグを変更する
     * ・サバイバルモードのみスプリント処理を無効化（クリエイティブは可）
     * ・イベント内容がPlayerEventと被る所があるので統一するかもしれない
     *
     * @author FratikaK
     */

    //プレイヤーが死亡時、リストから削除、プロフィール名変更
    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (player.getGameMode() == GameMode.SURVIVAL){
            L4D_gamepl.getPlayerList().remove(player.getDisplayName());
            player.setPlayerListName("[" + ChatColor.RED + "死亡" + ChatColor.WHITE + "]" + player.getDisplayName());

            pl.getLogger().info(ChatColor.GREEN + player.getDisplayName() + ChatColor.WHITE + "が死亡しました");
            pl.getLogger().info("残りのプレイヤーは" + ChatColor.GREEN + L4D_gamepl.getPlayerList() + ChatColor.WHITE + "です");
        }

    }

    //ゲーム参加者のみスプリントを禁止する
    @EventHandler
    public void dontSplint(PlayerToggleSprintEvent event){
        Player player = event.getPlayer();

        if(player.getGameMode() == GameMode.SURVIVAL && L4D_gamepl.isGame()){
            event.setCancelled(true);
        }
    }

}
