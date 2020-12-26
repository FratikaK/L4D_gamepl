package com.github.fratikak.l4d_gamepl;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.raid.RaidTriggerEvent;

public class L4DMobs implements Listener {

    private final L4D_gamepl pl;

    //コンストラクタ
    public L4DMobs(L4D_gamepl pl) {
        this.pl = pl;
    }

    /**
     * ゲーム中に出現する特殊Mobの処理を記述する
     * ボス系モンスターもここで作成する
     *
     * ClassCastの問題を解決できない為、ゾンビ馬など通常敵対化しないmobを
     * モンスターとして登録するのは避けることにする
     *
     * @author FratikaK
     */

    //クリーパーが攻撃を受けた時に、即爆発するようにする
    @EventHandler
    public void bommer(EntityDamageByEntityEvent event){
        Entity entity = event.getEntity();

        if (entity.getType() == EntityType.CREEPER){
            World world = entity.getWorld();
            Location location = entity.getLocation().clone();

            //対象の場所に爆発を起こす
            world.createExplosion(location,3,false,false);
            pl.getServer().broadcastMessage("クリーパーによる爆発がおきました");
        }
    }

    //スライムが分裂する度に爆発が起きる
    @EventHandler
    public void slimeBommer(SlimeSplitEvent event){
        World world = event.getEntity().getWorld();
        Location location = event.getEntity().getLocation();

        world.createExplosion(location,2,false,false);
    }

    //村人レイドを発生させない
    @EventHandler
    public void notRaid(RaidTriggerEvent event){
        event.setCancelled(true);
    }

//    //通常敵対しないmobをスポーンさせた場合、プレイヤーに対して敵対化させる
//    @EventHandler
//    public void specialMobLogic(EntitySpawnEvent event) {
//
//        if (L4D_gamepl.isGame()) {
//            Entity entity = event.getEntity();
//            EntityType type = entity.getType();
//            World world = entity.getWorld();
//            LivingEntity livingEntity = (LivingEntity) entity;
//
//            //ターゲットとなるプレイヤー
//            Entity player = (Entity) world.getPlayers();
//
//            livingEntity.setAI(true);
//
//            /*
//            今はゾンビホースのみ。今後追加する予定
//            攻撃力の設定が不明
//            EntityDamageByEntityEventで処理するかも
//             */
//            switch (type) {
//                case ZOMBIE_HORSE:
//                    livingEntity.attack(player);
//                    break;
//            }
//        }
//    }
}
