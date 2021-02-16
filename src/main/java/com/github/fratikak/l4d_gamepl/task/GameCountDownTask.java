package com.github.fratikak.l4d_gamepl.task;

import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import com.github.fratikak.l4d_gamepl.util.ScoreboardSystem;
import org.bukkit.scheduler.BukkitRunnable;

public class GameCountDownTask extends BukkitRunnable {
    ScoreboardSystem system = new ScoreboardSystem();
    @Override
    public void run() {

        system.updateScoreBoard();

        //ゲームが終了すればストップ
        if (!L4D_gamepl.isGame()){
            new ScoreboardSystem().clearSideBar();
            this.cancel();
        }
    }
}
