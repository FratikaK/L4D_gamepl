package com.github.fratikak.l4d_gamepl;

import com.github.fratikak.l4d_gamepl.util.ScoreboardSystem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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

//        //プレイヤーリスト、死亡者リストが空であることを確認
//        if (!L4D_gamepl.getPlayerList().isEmpty() && !L4D_gamepl.getDeathPlayer().isEmpty()) {
//            pl.getServer().broadcastMessage("[L4D]" + ChatColor.RED + "各リストが空ではありません！");
//            return;
//        }
//
//        //参加するプレイヤーを取得する
//        for (Player target : Bukkit.getOnlinePlayers()) {
//            if (target.getGameMode() == GameMode.SURVIVAL) {
//                L4D_gamepl.getPlayerList().add(target);
//                pl.getLogger().info(ChatColor.AQUA + target.getDisplayName());
//            }
//        }

        //参加プレイヤーがいなければreturn
        if (L4D_gamepl.getPlayerList().isEmpty()) {
            Bukkit.broadcastMessage("[L4D]" + ChatColor.RED + "参加するプレイヤーが存在しません");
            L4D_gamepl.setGame(false);
            return;
        }

        pl.getLogger().info("ゲームに参加するプレイヤーを表示します...");
        pl.getLogger().info("参加者は" + ChatColor.AQUA + L4D_gamepl.getSurvivorList());

        Bukkit.broadcastMessage("[L4D]ゲーム参加者を表示します");
        for (Player p : L4D_gamepl.getPlayerList()){
            Bukkit.broadcastMessage(ChatColor.AQUA + p.getDisplayName());
        }

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

        for (Player target : L4D_gamepl.getPlayerList()) {
            if (target.getGameMode() == GameMode.SURVIVAL) {
                new GameWorlds().setTeleportStage(stageId, target);

                //生存者リストに格納
                L4D_gamepl.getSurvivorList().add(target);
            }
        }

        L4D_gamepl.setGame(true);
        new GameWorlds().setStage(stageId);
        pl.getLogger().info("ゲームがスタートしました");
        Bukkit.broadcastMessage("[L4D]" + ChatColor.AQUA + "ゲームがスタートしました");

        //スコアボードをアップデート
        new ScoreboardSystem().showScoreboard();

        //武器などのアイテムを渡す処理
        for (Player target : L4D_gamepl.getPlayerList()) {
            if (target.getGameMode() == GameMode.SURVIVAL) {

                /*
                 * <後で記述するもの>
                 * ステージへテレポート処理
                 * 参加プレイヤーのステータスを設定
                 * アイテム付与
                 */


                pl.giveGameItem(target.getInventory(), target);
                target.setPlayerListName("[" + ChatColor.AQUA + "生存者" + ChatColor.WHITE + "]" + target.getDisplayName());
                target.setFoodLevel(6);
                target.setHealth(20);
                //暗視効果
                target.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 0, true));
            }
        }
    }
}




