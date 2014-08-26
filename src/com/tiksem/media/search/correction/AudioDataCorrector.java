package com.tiksem.media.search.correction;

import com.tiksem.media.data.Audio;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.network.CorrectedTrackInfo;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Created with IntelliJ IDEA.
 * User: Администратор
 * Date: 05.07.13
 * Time: 2:39
 * To change this template use File | Settings | File Templates.
 */
public class AudioDataCorrector extends AbstractDataCorrector<Audio, CorrectedTrackInfo>{
    private static final AudioDatabaseCorrectionService DATABASE_CORRECTION_SERVICE =
            AudioDatabaseCorrectionService.getInstance();
    private InternetSearchEngine internetSearchEngine;

    public AudioDataCorrector(Audio audio,
                              InternetSearchEngine internetSearchEngine) {
        super(audio);
        this.internetSearchEngine = internetSearchEngine;
    }

    @Override
    protected Map<String,String> generateDataCorrectsMap(Audio audio, CorrectedTrackInfo correctedTrackInfo) {
        String artistName = audio.getArtistName();
        boolean isArtistNameCorrect = artistName.equals(correctedTrackInfo.artistName);
        Map<String,String> result = new LinkedHashMap<String, String>();
        if(!isArtistNameCorrect){
            result.put(artistName, correctedTrackInfo.artistName);
        }

        String name = audio.getName();
        boolean isNameValid = name.equals(correctedTrackInfo.name);
        if(!isNameValid){
            result.put(name, correctedTrackInfo.name);
        }

        if(result.isEmpty()){
            return null;
        }

        return result;
    }

    @Override
    protected CorrectedTrackInfo getCorrectedDataForData(Audio audio) throws IOException {
        CorrectedTrackInfo correctedTrackInfo = internetSearchEngine.getCorrectedTrackInfo(audio);
        if(correctedTrackInfo == null){
            return null;
        }

        Matcher matcher = CorrectionUtilities.ARTIST_NAME_UNKNOWN_PATTERN.matcher(correctedTrackInfo.artistName);
        if(!matcher.matches()){
            return correctedTrackInfo;
        }

        return null;
    }

    @Override
    protected void applyCorrection(Audio audio, CorrectedTrackInfo correctedTrackInfo) {
        audio.setArtistName(correctedTrackInfo.artistName);
        audio.setName(correctedTrackInfo.name);
    }

    @Override
    protected CorrectedTrackInfo getCorrectedDataFromData(Audio audio) {
        CorrectedTrackInfo correctedTrackInfo = new CorrectedTrackInfo();
        correctedTrackInfo.artistName = audio.getArtistName();
        correctedTrackInfo.name = audio.getName();
        return correctedTrackInfo;
    }

    @Override
    public Class<CorrectedTrackInfo> getCorrectedDataClass() {
        return CorrectedTrackInfo.class;
    }
}
