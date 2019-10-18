package com.pxpmc.bcban.command;

import com.pxpmc.bcban.BcBanMain;

import net.md_5.bungee.api.CommandSender;
/**
 * 重载
 * @author px
 * 2019年10月18日01:19:51
 */
public class Reload extends Base{
	
	public Reload() {
		super("xbanreload");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!sender.hasPermission("BungeeCord.reload")) {
			this.sendNotPer(sender);
			return;
		}
		BcBanMain.reload();
		BcBanMain.send(sender, "reload plugin");
	}


}
