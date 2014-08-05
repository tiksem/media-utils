package com.tiksem.media.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 *
 * Date: 09.03.13
 * Time: 4:21
 * To change this template use File | Settings | File Templates.
 */
public final class Mood {
    private static Map<String, String[]> moods = new TreeMap();
    private static List<String> moodsNamesList;

    static {
        putMood("love", "love","love mood");
        putMood("ambitions", "ambitions");
        putMood("admiration", "admiration");
        putMood("cheerful", "cheerful");
        putMood("romantic", "romantic");
        putMood("naughty", "naughty");
        putMood("enthusiastic", "enthusiastic");
        putMood("active", "active");
        putMood("hope,hopefulness", "hope","hopefulness");
        putMood("amorousness", "amorousness");
        putMood("amusement", "amusement");
        putMood("anger", "anger");
        putMood("angry", "angry");
        putMood("fun", "fun","fun mood");
        putMood("helpless", "helpless");
        putMood("inspiration", "inspiration");
        putMood("interest", "interest");
        putMood("irritation", "irritation");
        putMood("joy", "joy");
        putMood("jubilation", "jubilation");
        putMood("lazy", "lazy");
        putMood("lightness", "lightness");
        putMood("longing", "longing");
        putMood("lust", "lust");
        putMood("melancholy", "melancholy");
        putMood("pity", "pity");
        putMood("riot", "riot");
        putMood("smirk", "smirk");
        putMood("suffering", "suffering");
        putMood("sympathy", "sympathy");
        putMood("thankful", "thankful");
        putMood("thrill", "thrill");
        putMood("troubled", "troubled");
        putMood("trust", "trust");
        putMood("worried", "worried");
        putMood("desire", "desire");
        putMood("good", "good","good mood");
        putMood("pain", "pain");
        putMood("sadness", "sadness");
        putMood("confidence", "confidence");
        putMood("enthusiasm", "enthusiasm");
        putMood("euphoric", "euphoric");
        putMood("sarcastic", "sarcastic");
        putMood("panic", "panic");
        putMood("sleepy", "sleepy");
        putMood("sad", "sad mood");
        putMood("tired", "tired");
        putMood("disappointed", "disappointed");
        putMood("sad", "sad");
        putMood("anxiety", "anxiety");
        putMood("indifference", "indifference");
        putMood("animate", "animate");
        putMood("contemplative", "contemplative");
        putMood("stress", "stress");
        putMood("depression", "depression");
        putMood("boring", "boring");

        moodsNamesList = new ArrayList<String>(moods.keySet());
    }

    private static void putMood(String name, String... tags){
        moods.put(name, tags);
    }

    public static String[] getMoodTags(String name){
        return moods.get(name);
    }

    public static List<String> getMoods(){
        return moodsNamesList;
    }
}
