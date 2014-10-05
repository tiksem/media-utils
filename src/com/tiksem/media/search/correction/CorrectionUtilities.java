package com.tiksem.media.search.correction;

import com.utils.framework.strings.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Администратор
 * Date: 07.07.13
 * Time: 20:11
 * To change this template use File | Settings | File Templates.
 */
public class CorrectionUtilities {
    public static Pattern ARTIST_NAME_UNKNOWN_PATTERN = Pattern.compile(".unknown.", Pattern.CASE_INSENSITIVE);

    public static boolean matchUnknownPattern(CharSequence charSequence){
        Matcher matcher = ARTIST_NAME_UNKNOWN_PATTERN.matcher(charSequence);
        return matcher.matches();
    }

    public static String replaceArtistNameInName(String artistName, String name){
        String replaceTo = "";
        boolean ignoreSpaces = true;
        return Strings.replaceAllIfNotSuccessNull(artistName, name, replaceTo, ignoreSpaces);
    }
}
