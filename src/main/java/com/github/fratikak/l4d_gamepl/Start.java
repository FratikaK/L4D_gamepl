package com.github.fratikak.l4d_gamepl;

import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;

public class Start {

    /**
     * ゲームの開始に関するロジックを記述する
     *
     * startコマンド（別クラス）が呼び出された時、
     * 人数チェック。一人以上いるならゲームを開始する
     *
     * 10秒程度カウント。
     *
     * @author FratikaK
     */

    /*
     * [課題]
     * カウント終了後に、ゲーム進行用のメソッドを呼び出す
     * ゲームが始まった時にプレイタイムをカウントするメソッド作成
     */

    private final L4D_gamepl pl;

    //コンストラクタ
    public Start(L4D_gamepl pl) {
        this.pl = pl;
    }

    public void startGame(Player player) {

        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                pl.getLogger().info("ゲームに参加するプレイヤーを表示します...");

                //参加するプレイヤーを取得する
                for (Player target : Bukkit.getOnlinePlayers()) {
                    if (target.getGameMode() == GameMode.SURVIVAL) {
                        String playerName = target.getDisplayName();
                        L4D_gamepl.getPlayerList().add(playerName);
                        pl.getLogger().info(ChatColor.AQUA + playerName);
                    }
                }

                /*
                 * 参加プレイヤーがいればゲーム開始、
                 * いなければタイマーキャンセル。
                 */
                if (L4D_gamepl.getPlayerList().isEmpty()) {
                    player.sendMessage(ChatColor.RED + "参加するプレイヤーが存在しません！");
                    L4D_gamepl.setGame(false);

                } else {
                    L4D_gamepl.setTime(0);
                    L4D_gamepl.setGame(true);
                    player.sendMessage("参加者が決まりました");
                    player.sendMessage("参加者は" + ChatColor.AQUA + L4D_gamepl.getPlayerList() + ChatColor.WHITE + "です");
                    player.sendMessage("10秒後に開始します...");

                    for (int i = 10; i >= 0; i--) {
                        if (!L4D_gamepl.isGame()) {
                        }

                        //プレイヤーにカウントを表示。不参加者にも表示
                        for (Player target : Bukkit.getOnlinePlayers()) {
                            target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 24);
                            target.sendTitle(ChatColor.WHITE + "ゲーム開始まで" + i + "秒", "", 5, 10, 5);
                            pl.getServer().broadcastMessage(ChatColor.GOLD + "ゲーム開始まで" + i + "秒");
                        }

                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //ここにゲーム進行の処理を入れる。別クラス

                    //武器などのアイテムを渡す処理
                    for (Player target : Bukkit.getOnlinePlayers()) {
                        if (target.getGameMode() == GameMode.SURVIVAL) {
                            /*
                             * <後で記述するもの>
                             * ステージへテレポート処理
                             * 参加プレイヤーのステータスを設定
                             * アイテム付与
                             */
                            target.setPlayerListName("[" + ChatColor.AQUA + "生存者" + ChatColor.WHITE + "]" + target.getDisplayName());
                        }
                    }
                }
            }
        };
    }
}
