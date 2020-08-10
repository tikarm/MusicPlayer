package tigran.applications.musicplayer.data.repository;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tigran.applications.musicplayer.R;
import tigran.applications.musicplayer.data.model.Song;

public class SongsRepository {

    private static SongsRepository instance;

    private SongsRepository() {
    }

    public static SongsRepository getInstance() {
        if (instance == null) {
            return new SongsRepository();
        }
        return instance;
    }

    public MutableLiveData<List<Song>> getSongs(Context context) {

        MutableLiveData<List<Song>> songsLiveData = new MutableLiveData<>();
        songsLiveData.setValue(getAllAudioFiles(context));

        return songsLiveData;
    }

    private List<Song> getAllAudioFiles(Context context) {
        List<Song> songList = new ArrayList<>();
        ContentResolver musicResolver = context.getContentResolver();   //is used to get data from storage of phone
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);


        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int albumId = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);


            int songSequenceNumber = 0;
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                long thisAlbumId = musicCursor.getLong(albumId);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                String thisDuration = musicCursor.getString(durationColumn);
                Uri albumArtUri = getAlbumArtUri(thisAlbumId);
                Log.e("TAG", "getAllAudioFiles: " + thisDuration);

                songList.add(new Song(thisId, songSequenceNumber++, thisTitle, thisArtist, thisAlbum, albumArtUri, thisDuration));
            } while (musicCursor.moveToNext());
        }

        return songList;
    }

    public static Uri getAlbumArtUri(long albumId) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
    }
}
