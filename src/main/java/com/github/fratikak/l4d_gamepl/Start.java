package com.github.fratikak.l4d_gamepl;

import org.bukkit.*;
import org.bukkit.entity.Player;

import static java.lang.Thread.sleep;

public class Start {

    /**
     * ゲームの開始に関するロジックを記述する
     * <p>
     * startコマンド（別クラス）が呼び出された時、
     * 人数チェック。一人以上いるならゲームを開始する
     * <p>
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
        //参加するプレイヤーを取得する
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.getGameMode() == GameMode.SURVIVAL) {
                String playerName = target.getDisplayName();
                L4D_gamepl.getPlayerList().add(playerName);
                pl.getLogger().info(ChatColor.AQUA + playerName);
            }
        }

        //参加プレイヤーがいなければreturn
        if (L4D_gamepl.getPlayerList().isEmpty()) {
            player.sendMessage(ChatColor.RED + "参加するプレイヤーが存在しません！");
            L4D_gamepl.setGame(false);
            return;
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.getGameMode() == GameMode.SURVIVAL) {
                Location loc = target.getLocation();
                loc.setX(1198);
                loc.setY(4);
                loc.setZ(1018);
                target.teleport(loc);
            }
        }


        pl.getLogger().info("ゲームに参加するプレイヤーを表示します...");
        L4D_gamepl.setStarting(true);
        L4D_gamepl.setTime(0);
        player.sendMessage("参加者が決まりました");
        player.sendMessage("参加者は" + ChatColor.AQUA + L4D_gamepl.getPlayerList() + ChatColor.WHITE + "です");
        player.sendMessage("5秒後に開始します...");

        for (int i = 5; i >= 0; i--) {

            //プレイヤーにカウントを表示。不参加者にも表示
            for (Player target : Bukkit.getOnlinePlayers()) {
                target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 24);
                target.sendTitle(ChatColor.WHITE + "ゲーム開始まで" + i + "秒", "", 5, 10, 5);
                target.sendMessage(ChatColor.GOLD + "ゲーム開始まで" + i + "秒");

                if (i == 0) {
                    target.playSound(target.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 24);
                }
            }

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        L4D_gamepl.setStarting(false);
        L4D_gamepl.setGame(true);

        //武器などのアイテムを渡す処理
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.getGameMode() == GameMode.SURVIVAL) {

                /*
                 * <後で記述するもの>
                 * ステージへテレポート処理
                 * 参加プレイヤーのステータスを設定
                 * アイテム付与
                 */
                pl.giveGameItem(target.getInventory(), target);
                target.setPlayerListName("[" + ChatColor.AQUA + "生存者" + ChatColor.WHITE + "]" + target.getDisplayName());

            }
        }
    }
}




