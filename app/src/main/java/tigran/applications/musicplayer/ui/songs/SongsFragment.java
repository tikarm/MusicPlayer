package tigran.applications.musicplayer.ui.songs;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;

import tigran.applications.musicplayer.R;
import tigran.applications.musicplayer.data.model.Song;
import tigran.applications.musicplayer.databinding.SongsFragmentBinding;


public class SongsFragment extends Fragment {

    private SongsViewModel mSongsViewModel;

    private SongsFragmentBinding mBinding;

    //views
    RecyclerView mRecyclerView;

    //adapter
    SongsAdapter mSongsAdapter;

    public static SongsFragment newInstance() {
        return new SongsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = SongsFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModel();

        requestPermissions();
    }

    private void initViewModel() {
        mSongsViewModel = SongsViewModel.getInstance(requireActivity().getApplication());
        mSongsViewModel.init();
    }

    private void initRecyclerView() {
        mRecyclerView = mBinding.rvSongsFragmentSongs;

        mSongsAdapter = new SongsAdapter(getContext(), requireActivity().getApplication());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mSongsAdapter);
    }

    private void subscribeObservers() {
        mSongsViewModel.getSongsObservable().observe(getViewLifecycleOwner(), new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {
                if (songs != null && !songs.isEmpty()) {
                    mSongsAdapter.addItems(songs);
                }
            }
        });
    }

    private void requestPermissions() {
        Dexter.withContext(getContext())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        initRecyclerView();

                        mSongsViewModel.loadSongs();

                        subscribeObservers();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();
    }


}
