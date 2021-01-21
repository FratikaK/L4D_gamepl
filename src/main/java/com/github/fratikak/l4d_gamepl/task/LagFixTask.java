package com.github.fratikak.l4d_gamepl.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Entityの増えすぎ、放置や、
 * CrackShotのEntity増えすぎによるサーバー負荷軽減用に
 * Entityを削除するタスク
 *
 * @author FratikaK
 */
public class LagFixTask extends BukkitRunnable {

    @Override
    public void run() {

        long removed = Bukkit.getWorlds().stream()
                //ワールド内の生存しているエンティティ
                .flatMap(world -> world.getEntitiesByClasses(Projectile.class, Explosive.class, LivingEntity.class).stream())
                //20秒以上生存している、プレイヤーではないエンティティ
                .filter(livingEntity -> livingEntity.getTicksLived() > 20 * 20 && livingEntity.getType() != EntityType.PLAYER)
                //エンティティを削除する
                .peek(Entity::remove)
                //カウント
                .count();

        if (removed > 0) {
            Bukkit.getLogger().info("[LagFixTask] 不要な" + removed + "体のエンティティが削除されました");
        }
    }
}
