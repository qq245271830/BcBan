package com.pxpmc.bcban.command;

import com.pxpmc.bcban.BcBanMain;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public abstract class Base extends Command{

	public Base(String name) {
		super(name);
	}
	/**
	 * 发送没权限的信息
	 * @param sender
	 */
	protected void sendMsg(CommandSender sender,String text) {
		BcBanMain.send(sender,text);
	}
	/**
	 * 发送没权限的信息
	 * @param sender
	 */
	protected void sendNotPer(CommandSender sender) {
		sendMsg(sender,BcBanMain.getConfig().getString("not-pre", "&c你没有权限这样做"));
	}
}
