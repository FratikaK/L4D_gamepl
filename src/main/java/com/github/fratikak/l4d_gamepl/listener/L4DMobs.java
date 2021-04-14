package com.github.fratikak.l4d_gamepl.listener;

import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.SlimeSplitEvent;

/**
 * ゲーム中に出現する特殊Mobの処理を記述する
 * ボス系モンスターもここで作成する
 * ClassCastの問題を解決できない為、ゾンビ馬など通常敵対化しないmobを
 * モンスターとして登録するのは避けることにする
 *
 * @author FratikaK
 */
public class L4DMobs implements Listener {

    private final L4D_gamepl pl;

    //コンストラクタ
    public L4DMobs(L4D_gamepl pl) {
        this.pl = pl;
    }


    /**
     * クリーパーが攻撃を受けた時に、即爆発するようにする
     */
    @EventHandler
    public void boomer(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        if (entity.getType() == EntityType.CREEPER) {
            World world = entity.getWorld();
            Location location = entity.getLocation().clone();

            //対象の場所に爆発を起こす
            world.createExplosion(location, 3, false, false);
        }
    }

    /**
     * スライム（マグマキューブ）が分裂する度に爆発が起きる
     */
    @EventHandler
    public void slimeBoomer(SlimeSplitEvent event) {
        World world = event.getEntity().getWorld();
        Location location = event.getEntity().getLocation();

        world.createExplosion(location, 2, false, false);
    }

}
