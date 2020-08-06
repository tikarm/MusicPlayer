package tigran.applications.musicplayer.ui.base;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import tigran.applications.musicplayer.R;
import tigran.applications.musicplayer.data.model.Song;
import tigran.applications.musicplayer.databinding.PlayerFragmentBinding;
import tigran.applications.musicplayer.helpers.TimeConverters;
import tigran.applications.musicplayer.service.PlaySongService;
import tigran.applications.musicplayer.ui.MainActivity;

import static android.content.Context.BIND_AUTO_CREATE;

public class PlayerFragment extends Fragment {

    //song
    private Song mSong;

    //view-model
    private PlayerViewModel mPlayerViewModel;

    //view-binding
    private PlayerFragmentBinding mBinding;

    //service
    private Messenger mMessenger;
    private Messenger mIncomingMessenger = new Messenger(new IncomingHandler());
    private boolean isBound;

    public static PlayerFragment newInstance() {
        return new PlayerFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = PlayerFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModel();

        subscribeObservers();
    }

    @Override
    public void onStart() {      //bind to service when activity is started
        super.onStart();
        Intent intent = new Intent(requireContext(), PlaySongService.class);
        requireActivity().bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        requireActivity().startService(intent);
    }

    private void initViewModel() {
        mPlayerViewModel = PlayerViewModel.getInstance(requireActivity().getApplication());
        mPlayerViewModel.init();
    }

    private void subscribeObservers() {
        mPlayerViewModel.getCurrentSongMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Song>() {
            @Override
            public void onChanged(Song song) {
                mSong = song;
                playSong(song);

                setViews();

                setListeners();
            }
        });

        mPlayerViewModel.getCurrentSongPositionMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mBinding.seekbarPlayerFragment.setProgress(integer, true);
            }
        });
        mPlayerViewModel.getCurrentSongFinishedMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isFinished) {
                if (isFinished) {
                    mBinding.btnPlayPlayerFragment.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_play));
                    mSong.setPlaying(false);
                }
            }
        });
    }

    private void setViews() {
        mBinding.tvSongNamePlayerFragment.setText(mSong.getTitle());
        mBinding.tvSongArtistPlayerFragment.setText(mSong.getArtist());
        mBinding.tvSongAlbumPlayerFragment.setText(mSong.getAlbum());
        mBinding.tvSongCompleteTimePlayerFragment.setText(mSong.getSongProperDuration());
        if (mSong.getAlbumArtUri() != null) {
            Glide.with(requireActivity())
                    .applyDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.ic_song)
                            .error(R.drawable.ic_song))
                    .load(mSong.getAlbumArtUri())
                    .into(mBinding.ivSongPicturePlayerFragment);
        }
    }

    private void setListeners() {
        mBinding.btnPlayPlayerFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSong.isPlaying()) {
                    mBinding.btnPlayPlayerFragment.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_play));
                    mSong.setPlaying(false);
                    pauseSong();
                } else {
                    mBinding.btnPlayPlayerFragment.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_pause));
                    mSong.setPlaying(true);
                    playSong(mSong);
                }
            }
        });

        mBinding.seekbarPlayerFragment.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar.setMax(Integer.parseInt(mSong.getDuration()) / 1000);

                int seconds = TimeConverters.getSecondsFromSongMillis(String.valueOf(progress * 1000));
                long minutes = TimeConverters.getMinutesFromSongMillis(String.valueOf(progress * 1000));

                String secondsString = String.valueOf(seconds);
                if (seconds < 10) {
                    secondsString = "0" + secondsString;
                }
                mBinding.tvSongCurrentTimePlayerFragment.setText(minutes + ":" + secondsString);

                if (fromUser) {
                    updateSongPosition(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {       //argument service is used to communicate between
            mMessenger = new Messenger(service);                                    //    service and activity
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMessenger = null;
            isBound = false;
        }
    };

    static class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

            }
        }
    }

    public void playSong(Song song) {
        if (isBound) {
            Log.d("MAIN", "position sent");
            Message message = new Message();
//            message.arg1 = song.getId();
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
}
