package com.github.fratikak.l4d_gamepl.command;

import com.github.fratikak.l4d_gamepl.L4D_gamepl;
import com.github.fratikak.l4d_gamepl.task.StopTask;
import com.github.fratikak.l4d_gamepl.task.PreparationTask;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
                        new PreparationTask(pl,1).runTaskTimer(pl,0,20);
                        Bukkit.getLogger().info("veniceが選択されました");
                        return true;

                    case "town":
                        new PreparationTask(pl,2).runTaskTimer(pl,0,20);
                        Bukkit.getLogger().info("townが選択されました");
                        return true;

                    case "novigrad":
                        new PreparationTask(pl,3).runTaskTimer(pl,0,20);
                        Bukkit.getLogger().info("novigradが選択されました");
                        return true;
                }


            } else if (args[0].equalsIgnoreCase("stop")) {
                new StopTask(pl).runTaskTimer(pl,0, 20);
                return true;
            }
        }

        return false;
    }
}
