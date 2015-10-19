package com.tiksem.media.search.correction;

import android.os.Handler;
import com.tiksem.media.data.Audio;
import com.tiksem.media.data.Identified;
import com.tiksem.media.local.AudioDataBase;
import com.tiksem.media.search.InternetSearchEngine;
import com.tiksem.media.search.network.CorrectedTrackInfo;
import com.utilsframework.android.db.RandomAccessDataBase;
import com.utilsframework.android.db.RandomAccessDatabaseFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 * User: Администратор
 * Date: 05.07.13
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
public class AudioDatabaseCorrectionService{
    private static AudioDatabaseCorrectionService instance;

    private static final int PAUSE_UPDATING_INTERVAL = 100;

    private AudioDataBase audioDataBase;
    private InternetSearchEngine internetSearchEngine;
    private RandomAccessDatabaseFactory databaseFactory;
    private Executor backgroundTaskExecutor;
    private Map<Object, AbstractDataCorrector> correctionMap =
            new ConcurrentHashMap<Object, AbstractDataCorrector>();
    private Map<Class, RandomAccessDataBase> dataBases = new HashMap<Class, RandomAccessDataBase>();
    private volatile boolean paused = false;
    private volatile boolean stopped = false;
    private OnCorrectionReady onCorrectionReady;
    private volatile boolean correctAllDataAutomatically = false;
    private OnDataCorrectedAutomatically onDataCorrectedAutomatically;
    private Handler handler;

    public interface OnFinished{
        void onFinish();
    }

    public interface OnDataCorrectedAutomatically<Data>{
        void onDataCorrected(Data data);
    }

    public interface OnCorrectionReady{
        void onCorrectionReady(Object element, Map<String, String> dataCorrections);
    }

    private AudioDatabaseCorrectionService(){

    }

    public static void init(AudioDataBase audioDataBase,
                            InternetSearchEngine internetSearchEngine,
                            RandomAccessDatabaseFactory databaseFactory,
                            Executor threadPoolExecutor){
        instance = new AudioDatabaseCorrectionService();
        instance.audioDataBase = audioDataBase;
        instance.internetSearchEngine = internetSearchEngine;
        instance.databaseFactory = databaseFactory;
        instance.backgroundTaskExecutor = threadPoolExecutor;
        instance.handler = new Handler();
    }

    public static AudioDatabaseCorrectionService getInstance(){
        if(instance == null){
            throw new IllegalStateException("call init before getInstance");
        }

        return instance;
    }

    private interface DataCorrectorFactory<T extends Identified,Info>{
        AbstractDataCorrector<T,Info> create(T object);
    }

    private <T> RandomAccessDataBase<T> getOrCrateDatabaseFor(Class<T> tClass){
        RandomAccessDataBase dataBase = dataBases.get(tClass);
        if(dataBase == null){
            dataBase = databaseFactory.create(tClass);
            dataBases.put(tClass, dataBase);
        }

        return dataBase;
    }

    private <T extends Identified,Info>
    void updateCorrectionsInfo(List<T> items, DataCorrectorFactory<T, Info> dataCorrectorFactory){
        for(final T item : items){
            if(stopped){
                return;
            }

            while (paused) {
                try {
                    Thread.sleep(PAUSE_UPDATING_INTERVAL);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            final AbstractDataCorrector<T,Info> dataCorrector = dataCorrectorFactory.create(item);
            dataCorrector.updateCorrectedData();
            final boolean isDataCorrect = dataCorrector.isDataCorrect();
            final Map<String, String> dataCorrections = dataCorrector.getCorrections();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isDataCorrect) {
                        correctionMap.remove(item);
                    } else {
                        correctionMap.put(item, dataCorrector);
                        executeOnCorrectionReady(onCorrectionReady, item, dataCorrections);
                    }
                }
            });
        }
    }

    private interface ItemGetter<T>{
        T get(int id);
    }

    private <T extends Identified, CorrectedData> void loadCorrectionsFromDataBase(
            Class<CorrectedData> correctedDataClass,
            final AbstractDataCorrector<T, CorrectedData> dataCorrector,
            final ItemGetter<T> correctedDataGetter)
    {
        RandomAccessDataBase<CorrectedData> dataBase = getOrCrateDatabaseFor(correctedDataClass);

        final Map<Integer,CorrectedData> correctedItemsMap = dataBase.toMap();

        handler.post(new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<Integer, CorrectedData> entry : correctedItemsMap.entrySet()) {
                    int id = entry.getKey();
                    T item = correctedDataGetter.get(id);
                    if (item == null) {
                        continue;
                    }

                    CorrectedData correctedData = entry.getValue();
                    dataCorrector.setCorrectedData(correctedData);
                    dataCorrector.setData(item);
                    dataCorrector.correct();
                }
            }
        });
    }

    private void loadAudioCorrectionsFromDataBase(){
        AbstractDataCorrector<Audio,CorrectedTrackInfo> dataCorrector = new AudioDataCorrector(null, null);
        loadCorrectionsFromDataBase(CorrectedTrackInfo.class, dataCorrector,
                new ItemGetter<Audio>() {
                    @Override
                    public Audio get(int id) {
                        return audioDataBase.getSongById(id);
                    }
                });
    }

    private void updateAudiosCorrectionInfo(){
        List<Audio> audios = audioDataBase.getSongs();
        updateCorrectionsInfo(audios, new DataCorrectorFactory<Audio, CorrectedTrackInfo>() {
            @Override
            public AbstractDataCorrector<Audio, CorrectedTrackInfo> create(Audio audio) {
                return new AudioDataCorrector(audio, internetSearchEngine);
            }
        });
    }

    private void runUpdatingCorrectionsInfo(){
        updateAudiosCorrectionInfo();
    }

    public void notifyDataCorrected(final Identified data){
        final AbstractDataCorrector dataCorrector = getCorrector(data);
        correctionMap.remove(data);

        runOnBackground(new Runnable() {
            @Override
            public void run() {
                RandomAccessDataBase<CorrectedTrackInfo> dataBase =
                        getOrCrateDatabaseFor(dataCorrector.getCorrectedDataClass());
                dataCorrector.writeCorrectionsToDataBase(dataBase);
            }
        });
    }

    public AbstractDataCorrector<Audio, CorrectedTrackInfo> getAudioCorrector(Audio audio){
        return correctionMap.get(audio);
    }

    public <T extends Identified, CorrectedData> AbstractDataCorrector<T,CorrectedData>
    getCorrector(T data){
        return correctionMap.get(data);
    }

    public boolean audioShouldBeCorrected(Audio audio){
        return correctionMap.containsKey(audio);
    }

    private void runLoadingCorrectionsFromDatabase(){
        loadAudioCorrectionsFromDataBase();
    }

    private void runOnBackground(Runnable runnable){
        backgroundTaskExecutor.execute(runnable);
    }

    public void startDataChecking(final OnCorrectionReady onCorrectionReady){
        this.onCorrectionReady = onCorrectionReady;
        runOnBackground(new Runnable() {
            @Override
            public void run() {
                runUpdatingCorrectionsInfo();
            }
        });
    }

    private void executeOnCorrectionReady(final OnCorrectionReady onCorrectionReady,
                                          final Object data,
                                          Map<String,String> corrections){
        if(onCorrectionReady != null){
            onCorrectionReady.onCorrectionReady(data, corrections);
        }
    }

    private void executeOnFinished(final OnFinished onFinished){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (onFinished != null) {
                    onFinished.onFinish();
                }
            }
        });
    }

    public void startLoadingCorrectionsFromDatabase(final OnFinished onFinished){
        runOnBackground(new Runnable() {
            @Override
            public void run() {
                runLoadingCorrectionsFromDatabase();
                executeOnFinished(onFinished);
            }
        });
    }

    private void correctAll(){
        for(AbstractDataCorrector dataCorrector : correctionMap.values()){
            dataCorrector.correctIfPossible();
        }
    }

    public Map<Object, AbstractDataCorrector> getCorrectionMap() {
        return correctionMap;
    }

    public boolean correctAllDataAutomatically() {
        return correctAllDataAutomatically;
    }

    public OnDataCorrectedAutomatically getOnDataCorrectedAutomatically() {
        return onDataCorrectedAutomatically;
    }
    public void setCorrectAllDataAutomatically(boolean correctAllDataAutomatically) {
        this.correctAllDataAutomatically = correctAllDataAutomatically;
        if(correctAllDataAutomatically){
            correctAll();
        }
    }

    public void setCorrectAllDataAutomatically(boolean correctAllDataAutomatically,
                                               OnDataCorrectedAutomatically onDataCorrectedAutomatically){
        if(correctAllDataAutomatically){
            this.onDataCorrectedAutomatically = onDataCorrectedAutomatically;
        }
        setCorrectAllDataAutomatically(correctAllDataAutomatically);
    }

    public void pause(){
        paused = true;
    }

    public void resume(){
        paused = false;
    }

    public void stop(){
        stopped = true;
    }
}
