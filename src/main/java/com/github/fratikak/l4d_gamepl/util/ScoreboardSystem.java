package com.github.fratikak.l4d_gamepl.util;


import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * スコアボードのシステムを司るクラス
 * 現在はプレイヤー名表示と、mobキル数を表示する
 *
 * @author FratikaK
 */
public class ScoreboardSystem {

    private List<String> boardLine() {

        if (L4D_gamepl.isGame()) {

            //生存者の人数
            int survive = L4D_gamepl.getSurvivorList().size();

            List<String> messageList = new ArrayList<>();
            messageList.add("");
            messageList.add("生存者の数" + ChatColor.AQUA + survive + ChatColor.WHITE + "人");
            messageList.add("");

            for (Player target : L4D_gamepl.getSurvivorList()) {
                int health = (int) target.getHealth();
                int kills = target.getStatistic(Statistic.MOB_KILLS);

                messageList.add(ChatColor.GOLD + "HP" + health + ChatColor.AQUA + target.getDisplayName() + "   " + ChatColor.GOLD + kills);
            }

            return messageList;
        }
        return null;
    }

    private final Scoreboard scoreBoard = Bukkit.getScoreboardManager().getNewScoreboard();

    public void updateScoreBoard() {

        if (Bukkit.getOnlinePlayers().size() <= 0){
            return;
        }

        // Objectiveを取得
        Objective obj = scoreBoard.getObjective("side");

        // Objectiveが存在しなかった場合は作成
        if ( obj == null ) {
            obj = scoreBoard.registerNewObjective("side", "dummy");
        }

        // Slotを設定
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(ChatColor.GREEN + "L4DGame");

        // 行を取得
        List<String> lines = boardLine();
        // nullが返ってきた場合は非表示にしてreturn
        if ( lines == null ) {
            scoreBoard.clearSlot(DisplaySlot.SIDEBAR);
            return;
        }
        // リスト反転
        Collections.reverse(lines);

        // 現在指定されているEntryを全て解除
        clearEntries();

        int currentValue = 0;
        for ( String msg : lines ) {

            // 行が0の場合は空白にする
            if ( msg == null ) {
                msg = "";
            }

            // すでに値が設定されている場合は最後に空白を足していく
            while ( obj.getScore(msg).isScoreSet() ) {
                msg = msg + " ";
            }

            // 値を設定
            obj.getScore(msg).setScore(currentValue);
            currentValue++;
        }

        // スコアボードを設定する
        Bukkit.getOnlinePlayers().forEach(p -> {
            if ( p.getScoreboard() != scoreBoard ) {
                p.setScoreboard(scoreBoard);
            }
        });
    }

    /**
     * 現在設定されているEntryを全てリセットする
     */
    private void clearEntries() {
        scoreBoard.getEntries().forEach(scoreBoard::resetScores);
    }

    public void clearSideBar() {
        // boardがnullでなければSIDEBARを削除
        scoreBoard.clearSlot(DisplaySlot.SIDEBAR);

        for (Player target : Bukkit.getOnlinePlayers()){
            target.setStatistic(Statistic.MOB_KILLS,0);
        }
    }
}
