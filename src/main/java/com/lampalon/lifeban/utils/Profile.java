package com.lampalon.lifeban.utils;

import com.lampalon.lifeban.Lifeban;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Profile {
    private String playerName;

    private String banReason;
    private String banBy;
    private boolean isBanned;
    private long banEnd;

    private String muteReason;
    private String mutedBy;
    private boolean isMuted;
    private long muteEnd;

    public Profile(String player) {
        this.playerName = player;
        init();
    }
    private long parseTime(String timeString) {
        if (timeString.matches("^\\d+m$")) {
            long minutes = Long.parseLong(timeString.replaceAll("[^\\d]", ""));
            return minutes * 60 * 1000;
        }
        return 0;
    }

    private void init() {
        try {
            ResultSet rs = Lifeban.getMySQL().getResult("SELECT * FROM LifeBan WHERE Playername='" + this.playerName + "'");
            if(rs.next()) {
                this.isBanned = true;
                this.banEnd = rs.getLong("banEnd");
                this.banReason = rs.getString("banReason");
                this.banBy = rs.getString("banBy");
            }
            else {
                this.isBanned = false;
                this.banBy = "";
                this.banEnd = 0;
                this.banReason = "";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            ResultSet rs = Lifeban.getMySQL().getResult("SELECT * FROM LifeMutes WHERE Playername='" + this.playerName + "'");
            if(rs.next()) {
                this.isMuted = true;
                this.muteEnd = rs.getLong("muteEnd");
                this.muteReason = rs.getString("muteReason");
                this.mutedBy = rs.getString("muteBy");
            }
            else {
                this.isMuted = false;
                this.mutedBy = "";
                this.muteEnd = 0;
                this.muteReason = "";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void refreshPlayerData() {
        init();
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(String reason, String by, long seconds) {
        long end;
        if (seconds == -1L) {
            end = -1L;
        }
        else {
            end = System.currentTimeMillis() + seconds * 1000L;
        }
        this.isBanned = true;
        setBanEnd(end);
        setBanReason(reason);
        setBanBy(by);
        if(toProxiedPlayer() != null)
        {
            toProxiedPlayer().disconnect(getBanKickMessage());
        }
        Lifeban.getMySQL().update("INSERT INTO LifeBan(Playername, banEnd, banBy, banReason) VALUES ('" + this.playerName + "', '" + getBanEnd() + "', '" + getBanBy() + "', '" + getBanReason() + "')");
    }

    public void unban() {
        Lifeban.getMySQL().update("DELETE FROM LifeBan WHERE Playername='" + this.playerName + "'");
    }

    public long getBanEnd() {
        return banEnd;
    }

    public void setBanEnd(long banEnd) {
        this.banEnd = banEnd;
    }

    public String getBanReason() {
        return banReason;
    }

    public void setBanReason(String banReason) {
        this.banReason = banReason;
    }

    public String getBanBy() {
        return banBy;
    }

    public void setBanBy(String banBy) {
        this.banBy = banBy;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMute(String reason, String by, long seconds) {
        long end;
        if (seconds == -1L) {
            end = -1L;
        }
        else {
            end = System.currentTimeMillis() + seconds * 1000L;
        }
        this.isMuted = true;
        setMuteEnd(end);
        setMuteReason(reason);
        setMutedBy(by);
        toProxiedPlayer().sendMessage(getMuteMessage());
        Lifeban.getMySQL().update("INSERT INTO LifeMutes(Playername, muteEnd, muteBy, muteReason) VALUES('" + this.playerName + "', '" + getMuteEnd() + "', '" + getMutedBy() + "', '" + getMuteReason() + "')");
    }

    public void unmute() {
        Lifeban.getMySQL().update("DELETE FROM LifeMutes WHERE Playername='" + this.playerName + "'");
    }

    public ProxiedPlayer toProxiedPlayer() {
        return Lifeban.getInstance().getProxy().getPlayer(playerName);
    }


    public long getMuteEnd() {
        return muteEnd;
    }

    public void setMuteEnd(long muteEnd) {
        this.muteEnd = muteEnd;
    }

    public String getMuteReason() {
        return muteReason;
    }

    public void setMuteReason(String muteReason) {
        this.muteReason = muteReason;
    }

    public String getMutedBy() {
        return mutedBy;
    }

    public void setMutedBy(String mutedBy) {
        this.mutedBy = mutedBy;
    }

    public String getRemainingbanTime()
    {
        if (isBanned())
        {
            long end = getBanEnd();
            if (end > 0L)
            {
                long millis = end - System.currentTimeMillis();
                int days = 0;
                int hours = 0;
                int minutes = 0;
                int seconds = 0;
                while (millis >= 1000L)
                {
                    seconds++;
                    millis -= 1000L;
                }
                while (seconds >= 60)
                {
                    minutes++;
                    seconds -= 60;
                }
                while (minutes >= 60)
                {
                    hours++;
                    minutes -= 60;
                }
                while (hours >= 24)
                {
                    days++;
                    hours -= 24;
                }
                return Lifeban.getConfigManager().timeFormat(days, hours, minutes, seconds);
            }
            return Lifeban.getConfigManager().getString("messages.time_format_permanent");
        }
        return null;
    }

    public String getRemainingmuteTime()
    {
        if (isMuted())
        {
            long end = getMuteEnd();
            if (end > 0L)
            {
                long millis = end - System.currentTimeMillis();
                int days = 0;
                int hours = 0;
                int minutes = 0;
                int seconds = 0;
                while (millis >= 1000L)
                {
                    seconds++;
                    millis -= 1000L;
                }
                while (seconds >= 60)
                {
                    minutes++;
                    seconds -= 60;
                }
                while (minutes >= 60)
                {
                    hours++;
                    minutes -= 60;
                }
                while (hours >= 24)
                {
                    days++;
                    hours -= 24;
                }
                return Lifeban.getConfigManager().timeFormat(days, hours, minutes, seconds);
            }
            return Lifeban.getConfigManager().getString("messages.time_format_permanent");
        }
        return null;
    }

    public String getMuteMessage()
    {
        List<String> lines = Lifeban.getConfigManager().getStringList("messages.mutemessage", new String[] {"{REASON}~" +
                getMuteReason(), "{BY}~" +
                getMutedBy(), "{REMAININGTIME}~" +
                getRemainingmuteTime() });
        String str = "";
        for (String line : lines) {
            str = str + line + "\n";
        }
        return str;
    }

    public String getBanKickMessage()
    {
        List<String> lines = Lifeban.getConfigManager().getStringList("messages.banmessage", new String[] {"{REASON}~" +
                getBanReason(), "{BY}~" +
                getBanBy(), "{REMAININGTIME}~" +
                getRemainingbanTime() });
        String str = "";
        for (String line : lines) {
            str = str + line + "\n";
        }
        return str;
    }
}
