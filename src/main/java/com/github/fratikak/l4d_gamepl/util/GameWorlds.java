package com.github.fratikak.l4d_gamepl.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GameWorlds {

    /**
     * コマンドで入力したステージへテレポートする
     * 複数ステージに対応
     * ステージを増やした場合、checkpointクラスも編集すること
     *
     * @author FratikaK
     */


    /*
    0   デフォルト
    1   venice
    2   town
    3   novigrad
    4   tokyo
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

        Location loc;

        switch (stageId) {
            case 1: //venice
                loc = new Location(target.getWorld(), 1403, 58, 1027);
                break;

            case 2: //town
                loc = new Location(target.getWorld(), 672, 123, 1023);
                break;

            case 3:
                loc = new Location(target.getWorld(), 1191, 29, 1546);
                break;

            case 4:
                loc = new Location(target.getWorld(), 1056, 61, 388,-90,1);
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
                if (getStageId() == 2) {
                    setStageId(0);
                    Bukkit.getLogger().info("townがfalseになりました");
                    break;
                }
                setStageId(2);
                Bukkit.getLogger().info("townがtrueになりました");
                break;

            case 3:
                if (getStageId() == 3) {
                    setStageId(0);
                    Bukkit.getLogger().info("novigradがfalseになりました");
                    break;
                }
                setStageId(3);
                Bukkit.getLogger().info("novigradがtrueになりました");
                break;

            case 4:
                if (getStageId() == 4) {
                    setStageId(0);
                    Bukkit.getLogger().info("tokyoがfalseになりました");
                    break;
                }
                setStageId(4);
                Bukkit.getLogger().info("tokyoがtrueになりました");
                break;
        }
    }
}
