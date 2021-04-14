package com.github.fratikak.l4d_gamepl;

import com.github.fratikak.l4d_gamepl.command.L4DCommands;
import com.github.fratikak.l4d_gamepl.listener.*;
import com.github.fratikak.l4d_gamepl.task.LagFixTask;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * メインクラス
 * イベント、コマンド読み込み
 *
 * @author FratikaK
 */
public final class L4D_gamepl extends JavaPlugin {

    //ゲーム中か
    private static boolean game = false;
    //準備Task中か
    private static boolean preparation = false;
    //チェックポイントの中にいるか
    private static boolean checkPoint = false;
    //ゲームに参加している全てのプレイヤーIDのリスト
    private static final List<UUID> playerList = new ArrayList<>();
    //生存しているプレイヤーIDのリスト
    private static final List<UUID> survivor = new ArrayList<>();
    //死亡したプレイヤーIDのリスト
    private static final List<UUID> deathPlayer = new ArrayList<>();


    @Override
    public void onEnable() {
        getLogger().info("L4DGame START!");

        //コマンドを扱うクラスの登録
        Objects.requireNonNull(getCommand("l4d")).setExecutor(new L4DCommands(this));

        //イベントリスナの登録
        getServer().getPluginManager().registerEvents(new Login(this), this);
        getServer().getPluginManager().registerEvents(new PlayerEvent(this), this);
        getServer().getPluginManager().registerEvents(new GameLogicListener(this), this);
        getServer().getPluginManager().registerEvents(new L4DMobs(this), this);
        getServer().getPluginManager().registerEvents(new CheckPoint(this), this);
        getServer().getPluginManager().registerEvents(new Items(), this);
        getServer().getPluginManager().registerEvents(new LobbyItemListener(this), this);
        getServer().getPluginManager().registerEvents(new SetPeekDeckListener(this), this);
        getServer().getPluginManager().registerEvents(new MerchantListener(),this);

        //タスクの実行
        new LagFixTask().runTaskTimer(this, 0, 20 * 60);
    }

    @Override
    public void onDisable() {
    }

    public static boolean isGame() {
        return game;
    }

    public static void setGame(boolean game) {
        L4D_gamepl.game = game;
    }

    public static boolean isPreparation() {
        return preparation;
    }

    public static void setPreparation(boolean preparation) {
        L4D_gamepl.preparation = preparation;
    }

    public static boolean isCheckPoint() {
        return checkPoint;
    }

    public static void setCheckPoint(boolean checkPoint) {
        L4D_gamepl.checkPoint = checkPoint;
    }

    public static List<UUID> getPlayerList() {
        return playerList;
    }

    public static List<UUID> getSurvivorList() {
        return survivor;
    }

    public static List<UUID> getDeathPlayerList() {
        return deathPlayer;
    }
}