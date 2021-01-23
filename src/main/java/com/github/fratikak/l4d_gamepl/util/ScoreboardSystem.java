package com.github.fratikak.l4d_gamepl.util;


import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

/**
 * スコアボードのシステムを司るクラス
 * 現在はプレイヤー名表示と、mobキル数を表示する
 *
 * @author FratikaK
 */
public class ScoreboardSystem {

    //キルカウントキー
    private final static String MOB_KILL_COUNT = "MOB_KILL_COUNT";

    //プレイヤーヘルスキー
    private final static String PLAYER_HEALTH = "PLAYER_HEALTH";

    private final L4D_gamepl plugin;

    private final ScoreboardManager manager = Bukkit.getScoreboardManager();
    private final Scoreboard board = manager.getMainScoreboard();

    public ScoreboardSystem(L4D_gamepl plugin) {
        this.plugin = plugin;
    }

    /**
     * 新しいキルカウントのスコアボードを作成する
     */
    public void setKillScoreBoard() {

        //Objectiveがあるか確認、なければ登録する
        Objective objective = board.getObjective(MOB_KILL_COUNT);
        if (objective == null) {
            objective = board.registerNewObjective(MOB_KILL_COUNT, "totalKillCount");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName(ChatColor.GREEN + "Kills");
        }

        //全プレイヤーの現在のキル数を反映(初期は0)
        for (Player target : L4D_gamepl.getPlayerList()) {
            objective.getScore(target).setScore(0);
        }
    }

    /**
     * 途中参加プレイヤーのキルカウントスコアボードを追加する
     *
     * @param player 　対象のプレイヤー
     */
    public void addBoard(Player player) {

        //Objectiveがあるのが前提
        Objective objective = board.getObjective(MOB_KILL_COUNT);

        //後参加のプレイヤーの初期値を設定
        objective.getScore(player).setScore(0);

        //現在のスコアボードを更新する
        for (Player target : L4D_gamepl.getPlayerList()) {
            objective.getScore(target).setScore(objective.getScore(target).getScore());
        }
    }

    /**
     * プレイヤーの体力を表示するスコアボード
     * ログインしてきた時に使用する
     */
    public void viewHealthBoard() {

        //キルカウントでメインのスコアボードを使用しているので
        //新しくここでScoreboardを宣言する
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        //Objectiveがなければ新規に作成する
        Objective objective = scoreboard.getObjective(PLAYER_HEALTH);
        if (objective == null) {
            objective = scoreboard.registerNewObjective(PLAYER_HEALTH, "health");
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            objective.setDisplayName("HP");
        }

        //全プレイヤーの現在の体力を反映
        for (Player player : Bukkit.getOnlinePlayers()) {
            objective.getScore(player).setScore((int) player.getHealth());
        }
    }
}
