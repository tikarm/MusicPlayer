package tigran.applications.musicplayer.ui.base;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import java.io.IOException;
import java.util.List;

import tigran.applications.musicplayer.R;
import tigran.applications.musicplayer.data.model.Song;
import tigran.applications.musicplayer.databinding.PlayerFragmentBinding;
import tigran.applications.musicplayer.helpers.TimeConverters;
import tigran.applications.musicplayer.service.PlaySongService;
import tigran.applications.musicplayer.ui.songs.SongsViewModel;

import static android.content.Context.BIND_AUTO_CREATE;

public class PlayerFragment extends Fragment {

    public static final int PLAY_NEXT = 1;
    public static final int PLAY_PREV = 2;

    //song
    private Song mSong;
    private List<Song> mSongList;
    private boolean isSongChangedManually = false;

    //view-model
    private PlayerViewModel mPlayerViewModel;
    private SongsViewModel mSongsViewModel;

    //view-binding
    private PlayerFragmentBinding mBinding;

    //service
    private Messenger mMessenger;
    private boolean isBound = false;

    private boolean isNextClicked = false;
    private boolean isPrevClicked = false;

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

        initToolbar();
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
        mSongsViewModel = SongsViewModel.getInstance(requireActivity().getApplication());
        mPlayerViewModel.init();
    }

    private void subscribeObservers() {
        mPlayerViewModel.getCurrentSongMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Song>() {
            @Override
            public void onChanged(Song song) {

                //TODO save current song and its progress in prefs and when clicking on current song continue where left and don't start over
                mSong = song;

                //TODO create another live data that will be responsible for setting views and animations
                setViews(mSong);

                setListeners();

                mPlayerViewModel.playSong(song);
                mPlayerViewModel.updateSongPosition(0);
                togglePlayView(true);
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
//                    mBinding.btnPlayPlayerFragment.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_play));
//                    mSong.setPlaying(false);
                }
            }
        });

        mSongsViewModel.getSongsObservable().observe(getViewLifecycleOwner(), new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {
                mSongList = songs;
                mPlayerViewModel.sendSongList(songs);
            }
        });
    }

    private void setListeners() {
        mBinding.btnPlayPlayerFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSong.isPlaying()) {
                    mSong.setPlaying(false);
                    mPlayerViewModel.pauseSong();
                    mPlayerViewModel.positionUpdates(false);
                } else {
                    mSong.setPlaying(true);
                    mPlayerViewModel.continueSong();
                    mPlayerViewModel.positionUpdates(true);
                }
                togglePlayView(mSong.isPlaying());
            }
        });

        mBinding.btnRightPlayerFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSongList != null && !mSongList.isEmpty() && mSong.getSequenceNumber() + 1 < mSongList.size()) {
                    mSong.setPlaying(false);
                    isNextClicked = true;
                    mPlayerViewModel.setNewSong(mSongList.get(mSong.getSequenceNumber() + 1));
                }
            }
        });

        mBinding.btnLeftPlayerFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSongList != null && !mSongList.isEmpty() && mSong.getSequenceNumber() - 1 >= 0) {
                    mSong.setPlaying(false);
                    isPrevClicked = true;
                    mPlayerViewModel.setNewSong(mSongList.get(mSong.getSequenceNumber() - 1));
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
                    mPlayerViewModel.updateSongPosition(progress);
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

    private void setViews(Song song) {
        mBinding.tvSongNamePlayerFragment.setText(song.getTitle());
        mBinding.tvSongArtistPlayerFragment.setText(song.getArtist());
        mBinding.tvSongAlbumPlayerFragment.setText(song.getAlbum());
        mBinding.tvSongCompleteTimePlayerFragment.setText(song.getSongProperDuration());
        if (song.getAlbumArtUri() != null) {
//            DrawableCrossFadeFactory factory =
//                    new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
//            GlideApp.with(requireActivity())
//                    .applyDefaultRequestOptions(new RequestOptions()
//                            .placeholder(R.drawable.ic_song)
//                            .error(R.drawable.ic_song))
//                    .load(mSong.getAlbumArtUri())
//                    .transition(DrawableTransitionOptions.withCrossFade(factory))
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(mBinding.ivSongPicturePlayerFragment);

//            Picasso.get()
//                    .load(song.getAlbumArtUri())
//                    .fit()
//                    .error(R.drawable.ic_song)
//                    .into(mBinding.ivSongPicturePlayerFragment, new com.squareup.picasso.Callback() {
//
//                        @Override
//                        public void onSuccess() {
//                            Animation animSlide = null;
//                            if (isNextClicked) {
//                                isNextClicked = false;
//                                animSlide = AnimationUtils.loadAnimation(getContext(),
//                                        R.anim.slide_in_right);
//                            } else if (isPrevClicked) {
//                                isPrevClicked = false;
//                                animSlide = AnimationUtils.loadAnimation(getContext(),
//                                        R.anim.slide_in_left);
//                                mBinding.ivSongPicturePlayerFragment.startAnimation(animSlide);
//                            }
//                            if (animSlide != null) {
//                                mBinding.ivSongPicturePlayerFragment.startAnimation(animSlide);
//                            }
//
//                        }
//
//                        @Override
//                        public void onError(Exception e) {
//                            Animation animSlide = null;
//                            if (isNextClicked) {
//                                isNextClicked = false;
//                                animSlide = AnimationUtils.loadAnimation(getContext(),
//                                        R.anim.slide_in_right);
//                            } else if (isPrevClicked) {
//                                isPrevClicked = false;
//                                animSlide = AnimationUtils.loadAnimation(getContext(),
//                                        R.anim.slide_in_left);
//                                mBinding.ivSongPicturePlayerFragment.startAnimation(animSlide);
//                            }
//                            if (animSlide != null) {
//                                mBinding.ivSongPicturePlayerFragment.startAnimation(animSlide);
//                            }
//                        }
//                    });

            ImageDecoder.Source source = null;
            Bitmap bitmap = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                source = ImageDecoder.createSource(requireActivity().getContentResolver(), song.getAlbumArtUri());
                try {
                    bitmap = ImageDecoder.decodeBitmap(source);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (bitmap != null) {
                mBinding.ivSongPicturePlayerFragment.setImageBitmap(bitmap);
            } else {
                mBinding.ivSongPicturePlayerFragment.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_song));
            }

            Animation animSlide = null;
            if (isNextClicked) {
                isNextClicked = false;
                animSlide = AnimationUtils.loadAnimation(getContext(),
                        R.anim.slide_in_right);
            } else if (isPrevClicked) {
                isPrevClicked = false;
                animSlide = AnimationUtils.loadAnimation(getContext(),
                        R.anim.slide_in_left);
                mBinding.ivSongPicturePlayerFragment.startAnimation(animSlide);
            }
            if (animSlide != null) {
                mBinding.ivSongPicturePlayerFragment.startAnimation(animSlide);
            }
        }
    }

    private void initToolbar() {
        mBinding.appbarPlayerFragment.bringToFront();
        mBinding.toolbarPlayerFragment.inflateMenu(R.menu.menu_player);
        mBinding.toolbarPlayerFragment.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {       //argument service is used to communicate between
            mMessenger = new Messenger(service);                                    //service and activity
            isBound = true;
            mPlayerViewModel.initMessenger(mMessenger, isBound);
            if (mSong != null) {
                mPlayerViewModel.updateSongPosition(0);
                mPlayerViewModel.playSong(mSong);
                togglePlayView(true);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMessenger = null;
            isBound = false;
        }
    };

    private void togglePlayView(boolean isPlaying) {
        AnimatedVectorDrawable d = (AnimatedVectorDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.btn_click);
        AnimatedVectorDrawable d1 = (AnimatedVectorDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.btn_click1);

        AnimatedVectorDrawable drawable = isPlaying ? d : d1;
        mBinding.btnPlayPlayerFragment.setBackground(drawable);
        drawable.start();
    }
}
