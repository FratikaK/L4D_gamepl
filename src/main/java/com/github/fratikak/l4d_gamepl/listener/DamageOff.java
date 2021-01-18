package com.github.fratikak.l4d_gamepl.listener;

import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class DamageOff implements Listener {

    private final L4D_gamepl pl;

    //コンストラクタ
    public DamageOff(L4D_gamepl pl) {
        this.pl = pl;
    }

    /**
     * プレイヤーがある条件下であらゆるダメージイベント、
     * 破壊イベントを発生させた時にそれをキャンセルさせる
     *
     * @author FratikaK
     */


//    @EventHandler
//    public void dmOffEvent(EntityDamageByEntityEvent event) {
//
//        /*
//         * 観戦者がダメージを与えてもそれをキャンセルさせる
//         * ただし、op権限がないプレイヤーのみ
//         */
//
//        Entity damager = event.getDamager();
//
//        if (damager.getType() == EntityType.PLAYER) {
//            Player spectator = (Player) damager;
//
//            if (!spectator.isOp()) {
//                if (spectator.getGameMode() != GameMode.SURVIVAL) {
//                    event.setCancelled(true);
//                }
//            }
//        }
//    }

    @EventHandler
    public void blockBreakEvent(BlockBreakEvent event) {

        /*
         * ブロック破壊を無効にする
         * ただし、op権限がないプレイヤーのみ
         */

        Player player = event.getPlayer();

        if (!player.isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockPlaceEvent(BlockPlaceEvent event) {

        //ブロック破壊と同様、置けないようにする

        Player player = event.getPlayer();

        if (!player.isOp()) {
            event.setCancelled(true);
        }
    }
}
