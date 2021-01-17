package com.github.fratikak.l4d_gamepl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GameWorlds {

    /**
     * コマンドで入力したステージへテレポートする
     * 複数ステージに対応
     *
     * @author FratikaK
     */


    /*
    0   デフォルト
    1   venice
    2   town
     */
    private static int stageId = 0;

    public static int getStageId() {
        return stageId;
    }

    public static void setStageId(int stageId) {
        GameWorlds.stageId = stageId;
    }

    //コマンドで入力されたステージ名に対応した場所へテレポート
    public void setTeleportStage(int stageId, Player target) {

        Location loc = target.getLocation();

        switch (stageId) {
            case 1: //venice
                loc.setX(1403);
                loc.setY(58);
                loc.setZ(1027);
                break;

            case 2:
                loc.setX(672);
                loc.setY(123);
                loc.setZ(1023);
                break;

            default:
                Bukkit.getLogger().info(ChatColor.RED + "stageIDに不具合が起きています");
                return;
        }

        target.teleport(loc);
        Bukkit.getLogger().info(target.getDisplayName() + "をステージID :" + stageId + "へ移動させました");

    }

    //どのステージが稼働しているか確認、セッターでtrueにしたりfalseにする
    public void setStage(int stageId) {

        switch (stageId) {
            case 1: //venice
                if (getStageId() == 1) {
                    setStageId(0);
                    Bukkit.getLogger().info("veniceがfalseになりました");
                    break;
                }
                setStageId(1);
                Bukkit.getLogger().info("veniceがtrueになりました");
                break;

            case 2:
                if (getStageId() == 2){
                    setStageId(0);
                    Bukkit.getLogger().info("townがfalseになりました");
                    break;
                }
                setStageId(2);
                Bukkit.getLogger().info("townがtrueになりました");
                break;
        }
    }
}
