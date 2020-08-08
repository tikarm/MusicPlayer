package tigran.applications.musicplayer.data.repository;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import tigran.applications.musicplayer.data.model.Song;
import tigran.applications.musicplayer.service.PlaySongService;

public class PlayerRepository {
    private static PlayerRepository instance;

    private Messenger mMessenger;
    private Messenger mIncomingMessenger = new Messenger(new IncomingHandler());
    private boolean isBound = false;

    private PlayerRepository() {
    }

    public static PlayerRepository getInstance() {
        if (instance == null) {
            return new PlayerRepository();
        }
        return instance;
    }

    public void setMessenger(Messenger mMessenger) {
        this.mMessenger = mMessenger;
    }

    public void setBound(boolean bound) {
        isBound = bound;
    }

    public void playSong(Song song) {
        if (isBound) {
            song.setPlaying(true);
            Log.d("MAIN", "position sent");
            Message message = new Message();
            message.what = PlaySongService.MESSAGE_PLAY;
            message.obj = song;
            message.replyTo = mIncomingMessenger;

            try {
                mMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        //unbindService(mServiceConnection);
    }

    public void continueSong() {
        if (isBound) {
            Log.d("MAIN", "position sent");
            Message message = new Message();
            message.what = PlaySongService.MESSAGE_CONTINUE;
            message.obj = "String";
            message.replyTo = mIncomingMessenger;

            try {
                mMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void pauseSong() {
        if (isBound) {
            Message message = new Message();
            message.what = PlaySongService.MESSAGE_STOP;
            message.obj = "String";
            message.replyTo = mIncomingMessenger;
            try {
                mMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        //unbindService(mServiceConnection);
    }

    public void updateSongPosition(int position) {
        if (isBound) {
            Message message = new Message();
            message.arg1 = position;
            message.what = PlaySongService.MESSAGE_UPDATE_POSITION;
            message.obj = "String";
            message.replyTo = mIncomingMessenger;
            try {
                mMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    //state param shows whether song position updates should start or stop
    public void positionUpdates(boolean state) {
        if (isBound) {
            Message message = new Message();
            if (state) {
                message.what = PlaySongService.MESSAGE_START_UPDATES;
            } else {
                message.what = PlaySongService.MESSAGE_STOP_UPDATES;
            }
            message.obj = "String";
            message.replyTo = mIncomingMessenger;
            try {
                mMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    static class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

            }
        }
    }
}
