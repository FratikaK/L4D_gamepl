package com.github.fratikak.l4d_gamepl;

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
                if (args[1].equalsIgnoreCase("venice")){
                    new Start(pl).startGame((Player) sender,"venice");
                    return true;
                }

            } else if (args[0].equalsIgnoreCase("stop")) {
                new Stop(pl).stopGame();
                return true;
            }
        }

        return false;
    }
}
