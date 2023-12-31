package com.lampalon.lifeban;

import com.lampalon.lifeban.commands.BanCommand;
import com.lampalon.lifeban.commands.CheckCommand;
import com.lampalon.lifeban.commands.MuteCommand;
import com.lampalon.lifeban.commands.UnbanCommand;
import com.lampalon.lifeban.events.ChatEvent;
import com.lampalon.lifeban.events.LoginEvent;
import com.lampalon.lifeban.sql.MySQL;
import com.lampalon.lifeban.utils.ConfigManager;
import com.lampalon.lifeban.utils.bstats.CustomChart;
import com.lampalon.lifeban.utils.bstats.Metrics;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public final class Lifeban extends Plugin {
    private static ConfigManager configManager;
    private static Lifeban instance = null;
    private static MySQL mySQL;
    public static String host = "";
    public static String username = "";
    public static String password = "";
    public static String database = "";
    public static String PREFIX = "", CONSOLE_PREFIX = "";
    public static int port = 3306;
    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager();
        configManager.init();
        MySQL.MySQLCredentials credentials = new MySQL.MySQLCredentials(host, port, username, password, database);
        mySQL = new MySQL(credentials);
        mySQL.openConnection();
        if(mySQL.isConnected()) {
            logToConsole("§a§lMySQL connection success, creating tables..");
            mySQL.update("CREATE TABLE IF NOT EXISTS LifeBan(Playername VARCHAR(16), banEnd LONG, banReason VARCHAR(256), banBy VARCHAR(16))");
            mySQL.update("CREATE TABLE IF NOT EXISTS LifeMutes(Playername VARCHAR(16), muteEnd LONG, muteReason VARCHAR(256), muteBy VARCHAR(16))");
        }
        PluginManager pm = Lifeban.getInstance().getProxy().getPluginManager();
        pm.registerListener(this, new ChatEvent());
        pm.registerListener(this, new LoginEvent());
        pm.registerCommand(getInstance(), new BanCommand("ban"));
        pm.registerCommand(getInstance(), new UnbanCommand("unban"));
        pm.registerCommand(getInstance(), new CheckCommand("check"));
        pm.registerCommand(getInstance(), new MuteCommand("mute"));
        utils();
    }
    public static Lifeban getInstance() {
        return instance;
    }
    @SuppressWarnings("deprecation")
    private static void logToConsole(String string) {
        Lifeban.getInstance().getProxy().getConsole().sendMessage(string);
    }
    public static MySQL getMySQL() {
        return mySQL;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }
    public enum TimeUnit
    {
        SECOND(new String[] { "s", "sec", "secs", "second", "seconds" }, 1L),  MINUTE(new String[] { "m", "min", "mins", "minute", "minutes" }, 60L),  HOUR(new String[] { "h", "hs", "hour", "hours" }, 3600L),  DAY(new String[] { "d", "ds", "day", "days" }, 86400L);

        private String[] names;
        private long seconds;

        private TimeUnit(String[] names, long seconds)
        {
            this.names = names;
            this.seconds = seconds;
        }

        public long getSeconds()
        {
            return this.seconds;
        }

        public String[] getNames()
        {
            return this.names;
        }

        public static TimeUnit getByString(String str)
        {
            for (TimeUnit timeUnit : TimeUnit.values()) {
                for (String name : timeUnit.getNames()) {
                    if (name.equalsIgnoreCase(str)) {
                        return timeUnit;
                    }
                }
            }
            return null;
        }
    }

    public void utils(){
        new Metrics(this, 20054);
    }
}
