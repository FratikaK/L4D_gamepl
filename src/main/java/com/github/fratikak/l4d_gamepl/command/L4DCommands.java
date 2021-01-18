package com.github.fratikak.l4d_gamepl.command;

import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import com.github.fratikak.l4d_gamepl.Start;
import com.github.fratikak.l4d_gamepl.Stop;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class L4DCommands implements CommandExecutor {

    private final L4D_gamepl pl;

    //コンストラクタ
    public L4DCommands(L4D_gamepl pl) {
        this.pl = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("l4d")) {

            if (args.length == 0) return false;

            if (args[0].equalsIgnoreCase("start")) {

                switch (args[1]){
                    case "venice":
                        new Start(pl).startGame((Player) sender, 1);
                        Bukkit.getLogger().info("veniceが選択されました");
                        return true;

                    case "town":
                        new Start(pl).startGame((Player) sender,2);
                        Bukkit.getLogger().info("townが選択されました");
                        return true;
                }


            } else if (args[0].equalsIgnoreCase("stop")) {
                new Stop(pl).runTaskTimer(pl,0, 20);
                return true;
            }
        }

        return false;
    }
}
