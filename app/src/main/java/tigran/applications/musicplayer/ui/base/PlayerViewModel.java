package tigran.applications.musicplayer.ui.base;

import android.app.Application;
import android.os.Messenger;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

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

    public void playSong(Song song) {
        playerRepository.playSong(song);
    }

    public void continueSong() {
        playerRepository.continueSong();
    }

    public void pauseSong() {
        playerRepository.pauseSong();
    }

    public void updateSongPosition(int position) {
        playerRepository.updateSongPosition(position);
    }

    public void positionUpdates(boolean state) {
        playerRepository.positionUpdates(state);
    }

    public void playNextSong(int songSequenceNumber){
        playerRepository.playNextSong(songSequenceNumber);
    }

    public void initMessenger(Messenger messenger, boolean isBound) {
        playerRepository.setMessenger(messenger);
        playerRepository.setBound(isBound);
    }

    public void setNewSong(Song song) {
        positionUpdates(false);
        setCurrentSong(song);
        setCurrentSongPosition(0);
    }

    public void sendSongList(List<Song> songList){
        playerRepository.sendSongList(songList);
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
