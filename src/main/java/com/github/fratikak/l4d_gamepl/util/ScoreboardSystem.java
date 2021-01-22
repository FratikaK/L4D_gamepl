package com.github.fratikak.l4d_gamepl.util;


import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

/**
 * スコアボードのシステムを司るクラス
 * 現在はプレイヤー名表示と、mobキル数を表示する
 *
 * @author FratikaK
 */
public class ScoreboardSystem {

    private final static String integer_KEY = "MOB_KILL_COUNT";
    private final L4D_gamepl plugin;

    private final ScoreboardManager manager = Bukkit.getScoreboardManager();
    private final Scoreboard board = manager.getMainScoreboard();

    public ScoreboardSystem(L4D_gamepl plugin) {
        this.plugin = plugin;
    }

    /**
     * 新しいキルカウントのスコアボードを作成する
     */
    public void setKillScoreBoard(){

        //Objectiveがあるか確認、なければ登録する
        Objective objective = board.getObjective(integer_KEY);
        if (objective == null){
            objective = board.registerNewObjective(integer_KEY, "totalKillCount");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName("Kills debug");
        }

        //全プレイヤーの現在のキル数を反映(初期は0)
        for (Player target : L4D_gamepl.getPlayerList()){
            objective.getScore(target).setScore(0);
        }
    }

    //途中参加プレイヤーのスコアを追加する
    public void addBoard(Player player){

        //Objectiveがあるのが前提
        Objective objective = board.getObjective(integer_KEY);

        //後参加のプレイヤーの初期値を設定
        objective.getScore(player).setScore(0);

        //現在のスコアボードを更新する
        for (Player target : L4D_gamepl.getPlayerList()){
            objective.getScore(target).setScore(objective.getScore(target).getScore());
        }
    }
}
