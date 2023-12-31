package com.lampalon.lifeban.utils;

import com.google.common.io.ByteStreams;
import com.lampalon.lifeban.Lifeban;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    Configuration configuration = null;
    public void init()
    {
        saveDefaultConfig();
        try
        {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile());
            Lifeban.PREFIX = getString("messages.prefix");
            Lifeban.CONSOLE_PREFIX = getString("messages.console_prefix");
            Lifeban.host = this.configuration.getString("mysql.host");
            Lifeban.port = this.configuration.getInt("mysql.port");
            Lifeban.username = this.configuration.getString("mysql.username");
            Lifeban.password = this.configuration.getString("mysql.password");
            Lifeban.database = this.configuration.getString("mysql.database");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public String timeFormat(int days, int hours, int minutes, int seconds)
    {
        return
                getString("messages.time_format").replace("{DAYS}", "" + days).replace("{HOURS}", "" + hours).replace("{MINUTES}", "" + minutes).replace("{SECONDS}", "" + seconds);
    }

    public String getString(String key, String... replace)
    {
        String str = this.configuration.getString(key);
        str = ChatColor.translateAlternateColorCodes('&', str);
        for (String repl : replace)
        {
            String[] r = repl.split("~");
            str = str.replace(r[0], r[1]);
        }
        return str;
    }

    public String getString(String key)
    {
        String str = this.configuration.getString(key);
        str = ChatColor.translateAlternateColorCodes('&', str);
        return str;
    }

    public List<String> getStringList(String key, String... replace)
    {
        List<String> list = getStringList(key);
        List<String> avail = new ArrayList();
        for (String str : list)
        {
            for (String repl : replace)
            {
                String[] r = repl.split("~");
                str = str.replace(r[0], r[1]);
            }
            avail.add(str);
        }
        return avail;
    }

    public List<String> getStringList(String key)
    {
        List<String> list = this.configuration.getStringList(key);
        List<String> avail = new ArrayList();
        for (String str : list) {
            avail.add(ChatColor.translateAlternateColorCodes('&', str));
        }
        return avail;
    }

    public File getFile()
    {
        return new File(Lifeban.getInstance().getDataFolder(), "config.yml");
    }

    public void save()
    {
        try
        {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.configuration, getFile());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void saveDefaultConfig()
    {
        if (!Lifeban.getInstance().getDataFolder().exists()) {
            Lifeban.getInstance().getDataFolder().mkdir();
        }
        File file = getFile();
        if (!file.exists()) {
            try
            {
                file.createNewFile();
                InputStream is = Lifeban.getInstance().getResourceAsStream("config.yml");Throwable localThrowable6 = null;
                try
                {
                    OutputStream os = new FileOutputStream(file);Throwable localThrowable7 = null;
                    try
                    {
                        ByteStreams.copy(is, os);
                        os.close();
                        is.close();
                    }
                    catch (Throwable localThrowable1)
                    {
                        localThrowable7 = localThrowable1;throw localThrowable1;
                    }
                    finally {}
                }
                catch (Throwable localThrowable4)
                {
                    localThrowable6 = localThrowable4;throw localThrowable4;
                }
                finally
                {
                    if (is != null) {
                        if (localThrowable6 != null) {
                            try
                            {
                                is.close();
                            }
                            catch (Throwable localThrowable5)
                            {
                                localThrowable6.addSuppressed(localThrowable5);
                            }
                        } else {
                            is.close();
                        }
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
