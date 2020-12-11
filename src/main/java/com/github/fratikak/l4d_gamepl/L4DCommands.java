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

        if (!(sender instanceof Player)){
            return false;
        }

        if (args.length == 1){
            switch (args[0].toLowerCase()) {

                case "start":
                    new Start(pl).startGame((Player) sender);
                    break;

                default:
                    sender.sendMessage("/l4d <start>");
            }
        }
        return true;
    }
}
