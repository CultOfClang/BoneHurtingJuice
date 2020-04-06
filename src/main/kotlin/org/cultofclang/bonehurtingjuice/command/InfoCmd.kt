package org.cultofclang.bonehurtingjuice.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

object InfoCmd : CommandExecutor {
    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<out String>): Boolean {
        sender.sendMessage("We are using bone hurting juice https://github.com/CultOfClang/BoneHurtingJuice/ it make you take fall damage.")
        return true
    }
}