package tigran.applications.musicplayer.ui.songs;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import tigran.applications.musicplayer.data.model.Song;
import tigran.applications.musicplayer.data.repository.SongsRepository;

public class SongsViewModel extends AndroidViewModel {
    //live data
    MutableLiveData<List<Song>> songsObservable = new MutableLiveData<>();

    private SongsRepository songsRepository;

    private Application application;

    public SongsViewModel(Application application) {
        super(application);
        this.application = application;
    }

    public void init() {
        songsRepository = SongsRepository.getInstance();
    }

    public void loadSongs() {
        songsObservable = songsRepository.getSongs(application);
    }

    public LiveData<List<Song>> getSongsObservable() {
        return songsObservable;
    }

    public void setSongsObservableValue(List<Song> songs) {
        songsObservable.setValue(songs);
    }
}
