package com.pxpmc.bcban;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.pxpmc.bcban.command.Ban;
import com.pxpmc.bcban.command.Reload;
import com.pxpmc.bcban.command.UnBan;
import com.pxpmc.bcban.listener.MainListener;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BcBanMain
extends Plugin
{
	private static BcBanMain plugin;
	private static String path;
	public static BcBanMain getPlugin() {
		return plugin;
	}
	private static Configuration config;
	private static Configuration banFile;
	private static BanLog log;
	public static SimpleDateFormat SDF;
	public static Configuration getBanFile() {
		return banFile;
	}
	public static Configuration getConfig() {
		return config;
	}
	public BcBanMain() {
		plugin = this;
	}
	/**
	 * 根据名字获取玩家,失败返回null
	 * @param name
	 * @return
	 */
	public static ProxiedPlayer getPlayer(String name) {

		//		String uid = new UUIDManager().getUUID(name);
		//		System.out.println(name+":1 " + uid);
		//		UUID uuid = UUID.nameUUIDFromBytes(name.getBytes());
		//		UUID uuid = UUID.fromString(uid);
		//		System.out.println(name+": " + uuid);
		ProxiedPlayer pp = BungeeCord.getInstance().getPlayer(name);
		if(pp != null) {
			//			System.out.println(pp.getClass().toString());
			if(pp instanceof UserConnection) {
				UserConnection uc = (UserConnection) pp;
				//				System.out.println(uc);
				return uc;
			}
		}
		//		for (ProxiedPlayer player : BungeeCord.getInstance().getPlayers()) {
		//			if(player.getName().equals(name)) {
		return pp;
		//			}
		//		}
		//		return null;
	}
	public static String reColor(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
	public static void send(CommandSender sender,String text,Object...objects) {
		sender.sendMessage(reColor(MessageFormat.format(text,objects)));
	}
	public void onLoad()
	{
		super.onLoad();
	}
	public static void info(String text) {
		plugin.getLogger().info(text);
	}
	/**
	 * 写ban日志
	 * @param msg
	 */
	public static void logXban(String targetName,String targetUUID,long time,String name,String uuid) {
		log(config.getString("log.xban", "使用xban,目标玩家: {0} uuid: {1},封禁时间: {2},操作玩家: {3} uuid: {4}"),targetName,targetUUID,time*1000,name,uuid);
	}
	/**
	 * 写解封日志
	 * @param msg
	 */
	public static void logXUnban(String targetName,String targetUUID,String name,String uuid) {
		log(config.getString("log.xunban", "使用xunban,目标玩家: {0} uuid: {1},操作玩家: {2} uuid: {3}"),targetName,targetUUID,name,uuid);
	}
	/**
	 * 写日志
	 * @param msg
	 */
	public static void log(String msg,Object ...arguments ) {
		log.write(msg, arguments);
	}
	public void onEnable()
	{
		super.onEnable();
		File f = this.getDataFolder();
		this.saveResource("config.yml", false);
		path = f.getPath();
		ProxyServer.getInstance().getPluginManager().registerListener(this, new MainListener());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new Ban());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new UnBan());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new Reload());
		//		Date date = new Date();
		String strDateFormat = "yyyy-MM-dd HH:mm:ss";
		SDF = new SimpleDateFormat(strDateFormat);
		//        SDF = SDF.format(date);
		//        logFile = new FileWriter()\
		log = new BanLog();
		try {
			log.open(new File(path + File.separator + "log.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		reload();
	}

	public static void reload() {
		ConfigurationProvider yaml = ConfigurationProvider.getProvider(YamlConfiguration.class);
		try {
			File f = new File(path + File.separator + "config.yml");
			if(!f.exists()) {
				plugin.saveResource("config.yml", false);
			}
			config = yaml.load(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		File bans = new File(path + File.separator + "bans.yml");
		if(!bans.exists()) {
			try {
				if(!bans.createNewFile()) {
					info("'banfile.yml' file create faild");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			if(bans.exists()) {
				banFile = yaml.load(bans);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * ban人
	 * @param player 玩家
	 * @param reason 理由
	 * @param time 时长,单位: 秒 默认 0  0=永久
	 * @param operatorName 操作员名字
	 * @param operatorUUID 操作员UUID
	 */
	public static synchronized void xban(ProxiedPlayer player,String reason,String operatorName,String operatorUUID) {
		xban(player, reason,0,operatorName,operatorUUID);
	}
	/**
	 * unban人
	 * @param name 解ban的玩家
	 * @param operatorName 操作玩家
	 * @param operatorUUID 操作玩家uuid
	 */
	public static synchronized boolean unban(String name,String operatorName,String operatorUUID) {
		for (String key : banFile.getKeys()) {
			if(name.equals(banFile.getString(key + ".name"))) {
				banFile.set(key+".unlock", true);
				saveBans();
				logXUnban(name, key, operatorName, operatorUUID);
				return true;
			}
		}
		return false;
	}
	/**
	 * 清除玩家的ban禁信息
	 * @param uuid 玩家的uuid
	 */
	public static synchronized void clearPlayer(UUID uuid) {
		banFile.set(uuid.toString(), null);
		saveBans();
	}


	/**
	 * 保存ban文件
	 */
	public static void saveBans() {
		ConfigurationProvider yaml = ConfigurationProvider.getProvider(YamlConfiguration.class);
		File bans = new File(BcBanMain.path + File.separator + "bans.yml");
		if(!bans.exists()) {
			try {
				if(bans.createNewFile()) {
					banFile = yaml.load(bans);
				}else {
					info("'banfile.yml' file create faild");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			yaml.save(banFile, bans);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * ban ip
	 * @param player 玩家
	 * @param reason 理由
	 * @param time 时长,单位: 秒 默认 0  0=永久
	 * @param operatorName 操作员名字
	 * @param operatorUUID 操作员UUID
	 */
	public static synchronized boolean xbanip(ProxiedPlayer player,String reason,long time,String operatorName,String operatorUUID) {
		
		String path = player.getUniqueId().toString()+".";
		banFile.set(path+"start", System.currentTimeMillis());
		banFile.set(path+"name", player.getName());
		banFile.set(path+"time", time);
		banFile.set(path+"reason", reason);
		banFile.set(path+"unlock", false);
		logXban(player.getName(), player.getUniqueId().toString(), time, operatorName, operatorUUID);
		kickBan(player.getName(),player.getUniqueId(),player);
		saveBans();
		return true;
	}
	/**
	 * ban人
	 * @param player 玩家
	 * @param reason 理由
	 * @param time 时长,单位: 秒 默认 0  0=永久
	 * @param operatorName 操作员名字
	 * @param operatorUUID 操作员UUID
	 */
	public static synchronized boolean xban(ProxiedPlayer player,String reason,long time,String operatorName,String operatorUUID) {

		String path = player.getUniqueId().toString()+".";
		banFile.set(path+"start", System.currentTimeMillis());
		banFile.set(path+"name", player.getName());
		banFile.set(path+"time", time);
		banFile.set(path+"reason", reason);
		banFile.set(path+"unlock", false);
		logXban(player.getName(), player.getUniqueId().toString(), time, operatorName, operatorUUID);
		kickBan(player.getName(),player.getUniqueId(),player);
		saveBans();
		return true;
	}
	/**
	 * 获取玩家的封禁的起始时间
	 * @param player 玩家
	 * @return 开始时间戳,单位: 毫秒
	 */
	public static synchronized long getPlayerBanStartTime(UUID uuid) {
		String path = uuid.toString();
		if(!banFile.contains(path)) {
			return 0;
		}
		long time = banFile.getLong(path + ".start", 0);
		return time;
	}

	/**
	 * 获取玩家的是否手动解封
	 * @param player 玩家
	 * @return 手动解封返回true
	 */
	public static synchronized boolean getPlayerUnlock(UUID uuid) {
		String path = uuid.toString();
		if(!banFile.contains(path)) {
			return false;
		}
		return banFile.getBoolean(path + ".unlock",false);
	}
	/**
	 * 获取玩家的封禁时间
	 * @param player 玩家
	 * @return 剩余时间, 单位: 秒,返回0表示永久
	 */
	public static synchronized long getPlayerBanTime(UUID uuid) {
		String path = uuid.toString();
		if(!banFile.contains(path)) {
			return 0;
		}
		long time = banFile.getLong(path + ".time", 0);
		return time;
	}

	/**
	 * 检查玩家是否被封禁
	 * @param player 玩家
	 * @return 被封禁返回true
	 */
	public static synchronized boolean checkPlayer(UUID uuid) {
		//		UUID uuid = player.getUniqueId();
		if(!banFile.contains(uuid.toString())) {
			return false;
		}
		long start = getPlayerBanStartTime(uuid);
		long rset = getPlayerBanTime(uuid) * 1000;
		if(rset == 0 || start + rset > System.currentTimeMillis()) {
			if(!getPlayerUnlock(uuid)) {
				return true;
			}
		}
		clearPlayer(uuid);
		ProxyServer.getInstance().getScheduler().runAsync(plugin, new Runnable() {
			private boolean close = false;
			@Override
			public void run() {
				while (!close) {
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e) {
					}
					ProxiedPlayer p = ProxyServer.getInstance().getPlayer(uuid);
					if(p != null) {
						if(p.isConnected()){
							for (String msg : config.getStringList("msg.unlock")) {
								send(p, msg);
							}
							close = true;
						}
					}
				}
			}
		});
		return false;
	}

	/**
	 * 提出被ban的玩家
	 * @param player 玩家
	 */
	public static void kickBan(String name,UUID uuid,Connection conn) {
		String path = uuid.toString();
		if(!banFile.contains(path)) {
			info("该玩家不在封禁时间内: " + name + " uuid: " + uuid.toString());
			return;
		}
		String reason = banFile.getString(path + ".reason", "none");
		long start = getPlayerBanStartTime(uuid);
		long rset = getPlayerBanTime(uuid) * 1000;



		String text = BcBanMain.getConfig().getString("msg.never", "永久");
		StringBuffer sb = new StringBuffer();
		for (String line : BcBanMain.getConfig().getStringList("msg.ban-msg-list")) {
			line = line.replace("{reason}", reason).replace("{time}", rset == 0 ? text : SDF.format(new Date(start + rset)));
			sb.append(BcBanMain.reColor(line)).append("\n");
		}
		conn.disconnect(sb.toString());

	}


	public void saveResource(String name, boolean replaceExisting) {
		Path target = this.getDataFolder().toPath().resolve(name);
		if (replaceExisting || !Files.isRegularFile(target, new LinkOption[0])) {
			InputStream in = this.getClass().getResourceAsStream("/".concat(name));
			if (in == null) {
				throw new IllegalArgumentException("Resource '" + name + "' not found!");
			} else {
				try {
					BufferedInputStream bin = new BufferedInputStream(in);
					Throwable var6 = null;

					try {
						Files.createDirectories(target.getParent());
						Files.copy(bin, target,
								replaceExisting
								? new CopyOption[0]
										: new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
					} catch (Throwable var16) {
						var6 = var16;
						throw var16;
					} finally {
						if (bin != null) {
							if (var6 != null) {
								try {
									bin.close();
								} catch (Throwable var15) {
									var6.addSuppressed(var15);
								}
							} else {
								bin.close();
							}
						}

					}

				} catch (IOException var18) {
					throw new UncheckedIOException(var18);
				}
			}
		}
	}
	public void onDisable()
	{
		super.onDisable();
		log.close();
	}
}
