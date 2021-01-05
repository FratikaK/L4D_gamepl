package com.github.fratikak.l4d_gamepl;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GameWorlds {

    /**
     * コマンドで入力したステージへテレポートする
     * 複数ステージに対応
     *
     * @author FratikaK
     */

    private final L4D_gamepl pl;

    public GameWorlds(L4D_gamepl pl) {
        this.pl = pl;
    }

    /*
    0   デフォルト
    1   venice
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

        switch (stageId) {
            case 1: //venice
                Location loc = target.getLocation();
                loc.setX(1403);
                loc.setY(58);
                loc.setZ(1027);
                target.teleport(loc);
                pl.getLogger().info(target.getDisplayName() + "をveniceへ移動させました");
                break;
        }
    }

    //どのステージが稼働しているか確認、セッターでtrueにしたりfalseにする
    public void setStage(int stageId) {

        switch (stageId) {
            case 1: //venice
                if (getStageId() == 1) {
                    setStageId(0);
                    pl.getLogger().info("veniceがfalseになりました");
                    break;
                }
                setStageId(1);
                pl.getLogger().info("veniceがtrueになりました");
                break;
        }
    }
}
