package com.pxpmc.bcban.command;

import com.pxpmc.bcban.BcBanMain;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.ConsoleCommandSender;
/**
 *unban人指令
 * @author px
 * 二〇一九年十月十六日 23:36:44
 */
public class UnBan extends Base{
	
	public UnBan() {
		super("xunban");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!sender.hasPermission("BungeeCord.xunban")) {
			this.sendNotPer(sender);
			return;
		}
		if(args.length < 1) {
			this.sendMsg(sender, BcBanMain.getConfig().getString("msg.xunban.player","请输入要封禁的玩家"));
			return;
		}
		String playerName = args[0];
		String name = "";
		String uuid = "";
		if(sender instanceof ConsoleCommandSender) {
			name = ((ConsoleCommandSender) sender).getName();
		}else {
			ProxiedPlayer op = (ProxiedPlayer) sender;
			name = op.getName();
			uuid = op.getUniqueId().toString();
		}
		if(BcBanMain.unban(playerName,name,uuid)) {
			BcBanMain.send(sender, BcBanMain.getConfig().getString("msg.xunban.success", "&6已对玩家 &e{0} &6实施解封"),playerName);
		}
	}


}
