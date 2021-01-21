package com.github.fratikak.l4d_gamepl.util;


import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.List;

/**
 * スコアボードのシステムを司るクラス
 * 現在はプレイヤー名表示と、mobキル数を表示する
 *
 * @author FratikaK
 */
public class ScoreboardSystem {

    ScoreboardManager manager = Bukkit.getScoreboardManager();
    Scoreboard scoreboard = manager.getNewScoreboard();


    public void showScoreboard() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();

        Objective objective = board.registerNewObjective("test", "mobkills");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("ScoreBoard");
        Score score = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Kills:"));
        score.setScore(0);

        for (Player current : Bukkit.getOnlinePlayers()) {
            current.setScoreboard(board);
        }


    }
}
