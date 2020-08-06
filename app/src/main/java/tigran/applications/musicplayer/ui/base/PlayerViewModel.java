package tigran.applications.musicplayer.ui.base;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import tigran.applications.musicplayer.data.model.Song;
import tigran.applications.musicplayer.data.repository.PlayerRepository;

public class PlayerViewModel extends AndroidViewModel {

    private static PlayerViewModel instance;

    //live-data
    MutableLiveData<Song> currentSongMutableLiveData = new MutableLiveData<>();
    MutableLiveData<Integer> currentSongPositionMutableLiveData = new MutableLiveData<>();
    MutableLiveData<Boolean> currentSongFinishedMutableLiveData = new MutableLiveData<>();

    //repo
    PlayerRepository playerRepository;

    public PlayerViewModel(@NonNull Application application) {
        super(application);
    }

    public void init() {
        playerRepository = PlayerRepository.getInstance();
    }

    public static PlayerViewModel getInstance(Application application) {
        if (instance == null) {
            instance = new PlayerViewModel(application);
        }
        return instance;
    }


    public MutableLiveData<Song> getCurrentSongMutableLiveData() {
        return currentSongMutableLiveData;
    }

    public void setCurrentSong(Song currentSong) {
        currentSongMutableLiveData.setValue(currentSong);
    }

    public MutableLiveData<Integer> getCurrentSongPositionMutableLiveData() {
        return currentSongPositionMutableLiveData;
    }

    public void setCurrentSongPosition(Integer currentSongPosition) {
        currentSongPositionMutableLiveData.postValue(currentSongPosition);
    }

    public MutableLiveData<Boolean> getCurrentSongFinishedMutableLiveData() {
        return currentSongFinishedMutableLiveData;
    }

    public void setCurrentSongFinished(Boolean finished) {
        currentSongFinishedMutableLiveData.postValue(finished);
    }
}
