package com.github.fratikak.l4d_gamepl.task;

import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import com.github.fratikak.l4d_gamepl.util.GameWorlds;
import com.github.fratikak.l4d_gamepl.util.PerkDecks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class StopTask extends BukkitRunnable {

    private final L4D_gamepl pl;

    //コンストラクタ
    public StopTask(L4D_gamepl pl) {
        this.pl = pl;
    }

    /**
     * ゲームの終了に関するロジックを記述する
     * stopコマンド（別クラス）が呼び出された時にゲーム終了
     * またはプレイヤーが全員死亡した時
     *
     * @author FratikaK
     */

    //オンラインプレイヤー全て初期位置へ移動させる
    public void targetTeleport(Player target) {
        target.teleport(target.getWorld().getSpawnLocation());
    }

    private int timeLeft = 10;

    @Override
    public void run() {

        //ゲーム中であればreturn
        if (!L4D_gamepl.isGame()) {
            return;
        }

        //0でロビーへテレポート
        if (timeLeft <= 0) {

            //プレイヤーリストを更新。[観戦者]
            for (Player target : Bukkit.getOnlinePlayers()) {
                target.setPlayerListName(null);
                target.setPlayerListName(ChatColor.WHITE + "[観戦者]" + target.getDisplayName());

                //ログイン時の状態に戻す
                target.setGameMode(GameMode.SURVIVAL);
                pl.giveLobbyItem(target.getInventory());
                targetTeleport(target);
                target.setHealth(20);
                target.setFoodLevel(20);
                target.removePotionEffect(PotionEffectType.NIGHT_VISION);
                target.removePotionEffect(PotionEffectType.INVISIBILITY);

                //Peekのポーション効果は削除
                new PerkDecks(target,pl).removePotion();

                //スコアボードリセット
                target.getScoreboard().resetScores(target);
            }

            L4D_gamepl.setGame(false);
            GameWorlds.setStageId(0);

            //リストを空にする
            L4D_gamepl.getPlayerList().clear();
            L4D_gamepl.getSurvivorList().clear();
            L4D_gamepl.getDeathPlayerList().clear();
            GameWorlds.setStageId(0);

            this.cancel();
        }

        if (timeLeft == 10) {
            Bukkit.broadcastMessage(ChatColor.AQUA + "10秒後にゲームを終了します");
        }

        if (timeLeft <= 5) {
            if (timeLeft > 0) {
                Bukkit.broadcastMessage(ChatColor.AQUA + "ゲーム終了まで" + timeLeft + "秒");
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1f, 1f));
            }else if (timeLeft == 0){
                Bukkit.broadcastMessage("ゲームを終了しました");
            }
        }
        timeLeft--;
    }
}
