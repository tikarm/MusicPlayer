package tigran.applications.musicplayer.data.model;

import android.net.Uri;
import android.util.Log;

import tigran.applications.musicplayer.helpers.TimeConverters;

public class Song {

    private long id;
    private String title;
    private String artist;
    private String album;
    private Uri albumArtUri;
    private String duration;

    private long minutes;
    private int seconds;

    private boolean isPlaying;

    public Song(long id, String title, String artist, String album, Uri albumArtUri, String duration) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.albumArtUri = albumArtUri;
        this.duration = duration;

        setMinutes(TimeConverters.getMinutesFromSongMillis(duration));
        setSeconds(TimeConverters.getSecondsFromSongMillis(duration));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Uri getAlbumArtUri() {
        return albumArtUri;
    }

    public void setAlbumArtUri(Uri albumArtUri) {
        this.albumArtUri = albumArtUri;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public String getSongProperDuration() {
        String result = minutes + ":";
        if (seconds < 10) {
            result += 0;
        }
        return result + seconds;
    }

    private void convertDurationToMinutes(String duration) {
        long durationInMillis = Long.parseLong(duration);
        long minutes = (durationInMillis / 1000) / 60;
        int seconds = (int) ((durationInMillis / 1000) % 60);

        setMinutes(minutes);
        setSeconds(seconds);
    }
}
