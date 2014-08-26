package com.tiksem.media.search.correction;

import com.tiksem.media.data.Identified;
import com.utilsframework.android.ErrorListener;
import com.utilsframework.android.db.RandomAccessDataBase;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Администратор
 * Date: 05.07.13
 * Time: 2:37
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractDataCorrector<Data extends Identified,CorrectedData> {
    private static final AudioDatabaseCorrectionService DATABASE_CORRECTION_SERVICE =
            AudioDatabaseCorrectionService.getInstance();

    private Data data;
    private CorrectedData correctedData;
    private ErrorListener errorListener;
    private Map<String,String> corrections;
    private boolean errorState = false;

    protected AbstractDataCorrector(Data data) {
        this.data = data;
    }

    public boolean dataCanBeCorrected(){
        return correctedData != null;
    }

    protected abstract Map<String,String> generateDataCorrectsMap(Data data, CorrectedData correctedData);
    protected abstract CorrectedData getCorrectedDataForData(Data data)
            throws Exception;
    protected abstract void applyCorrection(Data data, CorrectedData correctedData);
    protected abstract CorrectedData getCorrectedDataFromData(Data data);

    protected boolean isDataCorrect(){
        if(errorState){
            return true;
        }

        if(correctedData == null){
            return false;
        }

        CorrectedData newCorrectedData = getCorrectedDataFromData(data);
        return newCorrectedData.equals(correctedData);
    }

    public boolean wasErrorOccurred(){
        return errorState;
    }

    public void correct(){
        if(!correctIfPossible()){
            throw new IllegalStateException("The data cannot be corrected");
        }
    }

    public boolean correctIfPossible(){
        if(dataCanBeCorrected()){
            applyCorrection(data, correctedData);
            DATABASE_CORRECTION_SERVICE.notifyDataCorrected(data);

            return true;
        }

        return false;
    }

    void writeCorrectionsToDataBase(RandomAccessDataBase<CorrectedData> dataBase){
        CorrectedData correctedData = getCorrectedDataFromData(data);
        long id = data.getId();
        dataBase.set((int) id, correctedData);
    }

    public abstract Class<CorrectedData> getCorrectedDataClass();

    public ErrorListener getErrorListener() {
        return errorListener;
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public CorrectedData getCorrectedData() {
        return correctedData;
    }

    public void setCorrectedData(CorrectedData correctedData) {
        this.correctedData = correctedData;
    }

    public Map<String, String> getCorrections() {
        return corrections;
    }

    boolean updateCorrectedData(){
        try {
            correctedData = getCorrectedDataForData(data);
        } catch (Exception e) {
            if(errorListener != null){
                errorListener.onError(e);
                errorState = true;
            }
            return false;
        }

        errorState = false;

        if(correctedData != null){
            corrections = generateDataCorrectsMap(data, correctedData);
            return true;
        }

        return false;
    }
}
