package com.github.fratikak.l4d_gamepl;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;

public class Stop {

    private final L4D_gamepl pl;

    //コンストラクタ
    public Stop(L4D_gamepl pl) {
        this.pl = pl;
    }

    /**
     * ゲームの終了に関するロジックを記述する
     * <p>
     * stopコマンド（別クラス）が呼び出された時にゲーム終了
     * またはプレイヤーが全員死亡した時
     *
     * @author FratikaK
     */

    public void stopGame() {

        /**
         * コマンドで呼び出されるメソッド
         *
         * @param player コマンドを入力したプレイヤー
         */

        //ゲーム中ではないならreturn
        if (!L4D_gamepl.isGame()) {
            pl.getServer().broadcastMessage(ChatColor.RED + "ゲーム中ではありません！");
            return;
        }

        //プレイヤーが全員死亡している場合
        if (L4D_gamepl.getPlayerList().isEmpty()) {

            for (Player target : Bukkit.getOnlinePlayers()) {
                target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 24);
                target.sendTitle(ChatColor.RED + "GAME OVER!", "", 5, 10, 10);
                pl.getServer().broadcastMessage(ChatColor.WHITE + "プレイヤーが全員死亡しました。ゲームオーバーです");

                //プレイヤーリストを更新。[観戦者]
                target.setPlayerListName(null);
                target.setPlayerListName(ChatColor.WHITE + "[観戦者]" + target.getDisplayName());
            }
        } else {
            //プレイヤーがゴールにたどり着いた時を想定
            for (Player target : Bukkit.getOnlinePlayers()) {
                target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 24);
                target.sendTitle(ChatColor.AQUA + "GAME CLEAR!", "", 5, 10, 10);
                pl.getServer().broadcastMessage(ChatColor.AQUA + "ゴールにたどり着きました！ゲームクリアです！");

                //プレイヤーリストを更新。[観戦者]
                target.setPlayerListName(null);
                target.setPlayerListName(ChatColor.WHITE + "[観戦者]" + target.getDisplayName());

                L4D_gamepl.getPlayerList().clear();
            }
        }

        if (L4D_gamepl.isGame()){
            setRespawn();
        }


    }

    public void setRespawn() {

        /**
         * 10秒カウント、プレイヤーの持ち物をリセットする
         * サーバーリスポーン地点へテレポート
         */

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {

                if (L4D_gamepl.isGame()) {

                    for (int i = 10; i >= 0; i--) {

                        for (Player target : Bukkit.getOnlinePlayers()) {
                            target.playSound(target.getLocation(), Sound.BLOCK_BONE_BLOCK_BREAK, 1, 24);
                            pl.getServer().broadcastMessage(ChatColor.AQUA + "ゲーム終了まで" + i + "秒");
                        }

                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    L4D_gamepl.setGame(false);

                    for (Player target : Bukkit.getOnlinePlayers()) {
                        //アイテム処理
                        Inventory inventory = target.getInventory();
                        pl.giveLobbyItem(inventory);
                        target.teleport(target.getWorld().getSpawnLocation());
                        target.setGameMode(GameMode.SURVIVAL);
                    }
                }else{
                    timer.cancel();
                }
            }
        };
        timer.schedule(task, 0);
    }
}
