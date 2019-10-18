package com.pxpmc.bcban.listener;

import com.pxpmc.bcban.BcBanMain;

import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class MainListener
implements Listener
{
	@EventHandler
	public void join(LoginEvent e)
	{
//		e.getConnection().getUniqueId()
//		System.out.println("是否还在封禁:; " +BcBanMain.checkPlayer(e.getConnection().getUniqueId()));
		if(BcBanMain.checkPlayer(e.getConnection().getUniqueId())) {
			BcBanMain.kickBan(e.getConnection().getName(), e.getConnection().getUniqueId(), e.getConnection());
//			ProxyServer.getInstance().getScheduler().runAsync(BcBanMain.getPlugin(), ()-> {
//			});
			
		}
	}
}
