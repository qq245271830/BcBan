package com.pxpmc.bcban.command;

import com.pxpmc.bcban.BcBanMain;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.ConsoleCommandSender;
/**
 * ban人指令
 * @author px
 * 二〇一九年十月十六日 23:36:44
 */
public class Ban extends Base{
	
	public Ban() {
		super("xban");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!sender.hasPermission("BungeeCord.xban")) {
			this.sendNotPer(sender);
			return;
		}
		if(args.length < 1) {
			this.sendMsg(sender, BcBanMain.getConfig().getString("msg.xban.player","请输入要封禁的玩家"));
			return;
		}
		if(args.length < 2) {
			this.sendMsg(sender, BcBanMain.getConfig().getString("msg.xban.reason","请输入封禁理由"));
			return;
		}
		ProxiedPlayer player = BcBanMain.getPlayer(args[0]);
		if(sender == player) {
			this.sendMsg(sender, BcBanMain.getConfig().getString("msg.xban.me","请不要封禁自己"));
			return;
		}
		if(player == null) {
			this.sendMsg(sender, BcBanMain.getConfig().getString("msg.xban.not-online","玩家不在线或不存在"));
			return;
		}
		String reason = args[1];
		long time = 0;
		if(args.length > 2) {
			try {
				time = Long.valueOf(args[2]);
			} catch (NumberFormatException e) {
				this.sendMsg(sender, BcBanMain.getConfig().getString("msg.xban.not-number","时间参数只能是数字"));
				return;
			}
		}
		String name = "";
		String uuid = "";
		if(sender instanceof ConsoleCommandSender) {
			name = ((ConsoleCommandSender) sender).getName();
		}else {
			ProxiedPlayer op = (ProxiedPlayer) sender;
			name = op.getName();
			uuid = op.getUniqueId().toString();
		}
		if(BcBanMain.xban(player, reason,time,name,uuid)) {
			BcBanMain.send(sender, BcBanMain.getConfig().getString("msg.xban.success", "&6对玩家 &e{0} &6实施封禁,时长: &e{1}&6 ,原因: &e{2}"),player.getName(),time,reason);
		}
	}


}
