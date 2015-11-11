package com.tiksem.media.search.network;

import com.tiksem.media.data.Audio;
import com.tiksem.media.search.parsers.UrlQueryData;
import com.utilsframework.android.time.TimeUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by stykhonenko on 27.10.15.
 */
public class UrlReport {
    private String queryName;
    private String queryArtistName;
    private int queryDuration;
    private String vkName;
    private String vkArtistName;
    private int vkDuration;
    private String url;
    private String message;
    private long time;

    public UrlReport(Audio audio, UrlQueryData data, String message) {
        time = TimeUtils.getCurrentHumanReadableDateAndTimeAsLong();
        this.message = message;
        this.queryArtistName = audio.getArtistName();
        this.queryDuration = audio.getDuration();
        this.queryName = audio.getName();
        this.url = data.getUrl();
        this.vkArtistName = data.getArtistName();
        this.vkName = data.getName();
        this.vkDuration = data.getDuration();
    }

    public UrlReport(Audio audio, UrlQueryData data) {
        this(audio, data, null);
    }
    
    public Map<String, Object> toQueryArgs() {
        Map<String, Object> args = new HashMap<>();
        args.put("query_name", queryName);
        args.put("query_artistname", queryArtistName);
        args.put("time", time);
        if (message != null) {
            args.put("message", message);
        }
        args.put("vk_name", vkName);
        args.put("vk_artistname", vkArtistName);
        args.put("query_duration", queryDuration);
        args.put("vk_duration", vkDuration);
        args.put("url", url);
        return args;
    }

    @Override
    public String toString() {
        return "UrlReport{" +
                "queryName='" + queryName + '\'' +
                ", queryArtistName='" + queryArtistName + '\'' +
                ", queryDuration=" + queryDuration +
                ", vkName='" + vkName + '\'' +
                ", vkArtistName='" + vkArtistName + '\'' +
                ", vkDuration=" + vkDuration +
                ", url='" + url + '\'' +
                ", message='" + message + '\'' +
                ", time=" + time +
                '}';
    }
}
