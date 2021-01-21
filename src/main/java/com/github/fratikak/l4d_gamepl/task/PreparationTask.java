package com.github.fratikak.l4d_gamepl.task;

import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import com.github.fratikak.l4d_gamepl.Start;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * ゲームスタートする前に他にも参加者がいないか確認
 * プレイヤーの準備時間をカウントするタスク
 *
 * @author FratikaK
 */
public class PreparationTask extends BukkitRunnable {

    private final L4D_gamepl pl;
    private final int stageId;
    private int timeLeft = 20;

    /**
     * コンストラクタ
     *
     * @param pl      このプラグイン
     * @param stageId 使用するステージのID
     */
    public PreparationTask(L4D_gamepl pl, int stageId) {
        this.pl = pl;
        this.stageId = stageId;
    }

    @Override
    public void run() {
        //timeLeftが0の場合、Startクラスのメソッドを使用してゲームスタート
        if (timeLeft <= 0) {
            L4D_gamepl.setPreparation(false);
            new Start(pl).startGame(stageId);
            this.cancel();
            return;
        }

        if (timeLeft == 20){
            L4D_gamepl.setPreparation(true);
            Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 0));
            Bukkit.broadcastMessage("[L4D]" + ChatColor.AQUA + "20秒後にゲームを開始します。参加する場合はエメラルドをインタラクトしてください");
        }

        if (timeLeft <= 19){
            Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1f, 1f));
        }

        if (timeLeft == 10){
            Bukkit.broadcastMessage("ゲーム開始まで残り" + timeLeft + "秒");
        }

        if (timeLeft <= 5){
            Bukkit.broadcastMessage("ゲーム開始まで残り" + timeLeft + "秒");
        }

        timeLeft--;

    }
}
