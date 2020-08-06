package tigran.applications.musicplayer.service;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import tigran.applications.musicplayer.data.model.Song;
import tigran.applications.musicplayer.ui.MainActivity;
import tigran.applications.musicplayer.ui.base.PlayerViewModel;

//bindService() --> onBind() --> onServiceConnected()

public class PlaySongService extends IntentService implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    public static final String TAG = PlaySongService.class.getSimpleName();

    public static final int MESSAGE_PLAY = 100;
    public static final int MESSAGE_STOP = 101;
    public static final int MESSAGE_PREV = 102;
    public static final int MESSAGE_NEXT = 103;
    public static final int MESSAGE_ADD_LIST = 104;
    public static final int MESSAGE_UPDATE_POSITION = 105;

    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private Messenger mMessenger = new Messenger(new MessengerHandler()); //Any Message objects sent through this Messenger
    // will appear in the Handler as if Handler.sendMessage(Message) had been called
    // directly.

    private int continueFromMillis = 0;

    //view-model
    private PlayerViewModel mPlayerViewModel;

    public PlaySongService() {
        super(PlaySongService.class.getSimpleName());
        mPlayerViewModel = PlayerViewModel.getInstance(getApplication());
        mPlayerViewModel.init();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        //Ibinder is passed from activity by intent
        //here we get ibinder, which messenger is using to communicate.
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initMusicPlayer();
    }


    private void playMusic(Object object) {
        Song song = (Song) object;
        mMediaPlayer.reset();

        //get id
        long currSongId = song.getId();
        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSongId);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        mMediaPlayer.prepareAsync();
    }

    private void stopMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            continueFromMillis = mMediaPlayer.getCurrentPosition();
        }
    }

    private void updateSongPosition(int position) {
        continueFromMillis = position * 1000;
        mMediaPlayer.seekTo(position * 1000);
    }

    public void initMusicPlayer() {
        //set player properties
        mMediaPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioAttributes(
                new AudioAttributes
                        .Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());
        Log.d(TAG, "Setting up music Player");

        mMediaPlayer.setOnPreparedListener((MediaPlayer.OnPreparedListener) this);
        mMediaPlayer.setOnCompletionListener((MediaPlayer.OnCompletionListener) this);
        mMediaPlayer.setOnErrorListener((MediaPlayer.OnErrorListener) this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mPlayerViewModel.setCurrentSongFinished(true);

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        mp.seekTo(continueFromMillis);
        startPositionUpdates();
    }

    class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // Get msg.replyTo to be able to have bidirectional messaging
            switch (msg.what) {
                case MESSAGE_PLAY:
                    playMusic(msg.obj);
                    Log.d("SERVICE", "PLAY");
                    break;
                case MESSAGE_STOP:
                    stopMusic();
                    break;
                case MESSAGE_PREV:
                case MESSAGE_ADD_LIST:
                case MESSAGE_NEXT:
                    break;
                case MESSAGE_UPDATE_POSITION:
                    updateSongPosition(msg.arg1);
                    break;
            }
        }
    }

    private Handler mHandler = new Handler();

    private void startPositionUpdates() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (mMediaPlayer != null) {
                    int currentPosition = mMediaPlayer.getCurrentPosition() / 1000;
                    mPlayerViewModel.setCurrentSongPosition(currentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        });
        thread.start();
    }
}
