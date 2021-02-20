package com.github.fratikak.l4d_gamepl.util;

import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import com.shampaggon.crackshot.CSUtility;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Objects;

/**
 * LobbyInteractListenerで付与したい能力を
 * 与えるメソッドを格納しているクラス
 */
public class PerkDecks {

    private final Player player;
    private final L4D_gamepl plugin;

    private final static String PEEK_KEY = "PEEKDECK";

    public static String getPerkKey() {
        return PEEK_KEY;
    }

    private final static String tank = "TANK";
    private final static String grinder = "GRINDER";
    private final static String scout = "SCOUT";
    private final static String regene = "REGENE";
    private final static String destroyer = "DESTROYER";

    public static String getGrinder() {
        return grinder;
    }

    public static String getScout() {
        return scout;
    }

    public static String getTank() {
        return tank;
    }

    public PerkDecks(Player player, L4D_gamepl plugin) {
        this.player = player;
        this.plugin = plugin;
    }

    public void setMeta(String peekdeck) {

        player.removeMetadata(PEEK_KEY, plugin);

        player.setMetadata(PEEK_KEY, new FixedMetadataValue(plugin, peekdeck));
    }

    /**
     * プレイヤーが持っているメタデータに合わせてpeekdeckの能力を付与する
     */
    public void setPeekDeck() {

        //PEEK_KEYを持っているか
        if (!player.hasMetadata(PEEK_KEY)) {
            plugin.getLogger().info("[PeekDecks]プレイヤーにメタデータが付与されていません");
            return;
        }

        //メタデータはリスト型として返ってくるので、for文で取得する必要がある
        List<MetadataValue> peeks = player.getMetadata(PEEK_KEY);

        MetadataValue value = null;

        for (MetadataValue v : peeks) {
            if (v.getOwningPlugin().getName() == plugin.getName()) {
                value = v;
                break;
            }
        }

        //メタデータが見つからなかった場合はreturn
        if (value == null) {
            return;
        }

        //すでにあるポーション効果を削除
        removePotion();

        //メタデータの持っている値に応じてポーション効果を付与する
        switch (value.asString()) {
            case tank:
                player.addPotionEffect(new PotionEffect
                        (PotionEffectType.DAMAGE_RESISTANCE, 1000000, 1, true));
                break;

            case scout:
                player.addPotionEffect(new PotionEffect
                        (PotionEffectType.SPEED, 1000000, 0, true));
                break;

            case regene:
                player.addPotionEffect(new PotionEffect
                        (PotionEffectType.REGENERATION, 1000000, 0, true));
                break;

            case destroyer:
                new CSUtility().giveWeapon(player, "GL", 1);
                break;
        }
    }

    /**
     * 特定のポーション効果を削除する
     */
    public void removePotion() {
        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.REGENERATION);
    }

    /**
     * 引数のプレイヤーが所持しているMetadataを取得する
     *
     * @param player 調べたいplayer
     * @param key    key
     * @param plugin 自身のplugin
     * @return Metadataの値
     */
    public Object getMetadata(Player player, String key, Plugin plugin) {
        List<MetadataValue> values = player.getMetadata(key);
        for (MetadataValue value : values) {
            if (Objects.requireNonNull(value.getOwningPlugin()).getDescription().getName()
                    .equals(plugin.getDescription().getName())) {
                return value.value();
            }
        }
        return null;
    }
}
