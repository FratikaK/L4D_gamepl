package com.github.fratikak.l4d_gamepl;

import com.github.fratikak.l4d_gamepl.task.GameCountDownTask;
import com.github.fratikak.l4d_gamepl.util.DistributionItems;
import com.github.fratikak.l4d_gamepl.util.GameWorlds;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

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


    private final L4D_gamepl pl;

    //コンストラクタ
    public Start(L4D_gamepl pl) {
        this.pl = pl;
    }

    public void startGame(int stageId) {

        //ゲーム中であればreturn
        if (L4D_gamepl.isGame()) {
            pl.getServer().broadcastMessage("[L4D]" + ChatColor.RED + "ゲーム中であるため実行できません！");
            return;
        }

        //ステージIdが0であることを確認
        if (GameWorlds.getStageId() != 0) {
            pl.getServer().broadcastMessage("[L4D]" + ChatColor.RED + "ステージIdがデフォルトではありません！");
            return;
        }

        //参加プレイヤーがいなければreturn
        if (L4D_gamepl.getPlayerList().isEmpty()) {
            Bukkit.broadcastMessage("[L4D]" + ChatColor.RED + "参加するプレイヤーが存在しません");
            L4D_gamepl.setGame(false);
            return;
        }

        pl.getLogger().info("ゲームに参加するプレイヤーを表示します...");
        pl.getLogger().info("参加者は" + ChatColor.AQUA + L4D_gamepl.getSurvivorList());

        Bukkit.broadcastMessage("[L4D]ゲーム参加者を表示します");
        for (UUID p : L4D_gamepl.getPlayerList()) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (target.getUniqueId().equals(p)) {
                    Bukkit.broadcastMessage(ChatColor.AQUA + target.getDisplayName());
                    break;
                }
            }
        }

        for (int i = 5; i >= 0; i--) {

            //プレイヤーにカウントを表示。不参加者にも表示
            for (Player target : Bukkit.getOnlinePlayers()) {
                target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 24);
                target.sendTitle(ChatColor.GOLD + String.valueOf(i), "", 5, 10, 5);

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

        for (UUID playerId : L4D_gamepl.getPlayerList()) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (target.getUniqueId().equals(playerId)) {
                    if (target.getGameMode() == GameMode.SURVIVAL) {
                        new GameWorlds().setTeleportStage(stageId, target);

                        //生存者リストに格納
                        L4D_gamepl.getSurvivorList().add(target.getUniqueId());
                        break;
                    }
                }
            }
        }

        L4D_gamepl.setGame(true);
        new GameWorlds().setStage(stageId);
        pl.getLogger().info("ゲームがスタートしました");
        Bukkit.broadcastMessage("[L4D]" + ChatColor.AQUA + "ゲームがスタートしました");

        //プレイヤーに勝利条件をだすだけ
        Bukkit.broadcastMessage(ChatColor.GOLD + "------------------------------------------------");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("[" + ChatColor.AQUA + "勝利条件" + ChatColor.WHITE + "]ラピスラズリブロックまで辿り着く");
        Bukkit.broadcastMessage("[L4D]チェックポイントブロックは" + ChatColor.AQUA + "ダイアモンドブロック" + ChatColor.WHITE + "と"
                + ChatColor.GREEN + "エメラルドブロック" + ChatColor.WHITE + "です");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.GOLD + "------------------------------------------------");

        //武器などのアイテムを渡す処理
        for (UUID playerId : L4D_gamepl.getPlayerList()) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (target.getUniqueId().equals(playerId)) {
                    if (target.getGameMode() == GameMode.SURVIVAL) {
                        new DistributionItems(pl).giveGameItem(target);
                        target.setPlayerListName("[" + ChatColor.AQUA + "生存者" + ChatColor.WHITE + "]" + target.getDisplayName());
                        target.setFoodLevel(6);
                        target.setHealth(20);
                        //暗視効果
                        target.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 0, true));

                        //キル数と所持金をリセットする
                        target.setStatistic(Statistic.MOB_KILLS, 0);
                        target.setStatistic(Statistic.ANIMALS_BRED, 200);
                        break;
                    }
                }
            }
        }

        //GameCountDownTaskを開始
        new GameCountDownTask().runTaskTimer(pl, 0, 20);
    }
}




