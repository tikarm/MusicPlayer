package tigran.applications.musicplayer.ui.songs;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import tigran.applications.musicplayer.R;
import tigran.applications.musicplayer.data.model.Song;
import tigran.applications.musicplayer.ui.base.PlayerViewModel;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongViewHolder> {

    //data
    List<Song> mData = new ArrayList<>();

    //view-model
    PlayerViewModel mPlayerViewModel;

    //context
    Context context;

    //application
    Application application;

    public SongsAdapter(Context context, Application application) {
        this.context = context;
        this.application = application;
        initViewModels();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        final Song song = mData.get(position);

        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtist());
        if (song.getAlbumArtUri() != null) {
            Glide.with(context)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.ic_song)
                            .error(R.drawable.ic_song))
                    .load(song.getAlbumArtUri())
                    .into(holder.image);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayerViewModel.setCurrentSong(song);
                NavController navController = Navigation.findNavController((Activity) context, R.id.nav_host_fragment);
                navController.navigate(R.id.action_songsFragment_to_playerFragment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private void initViewModels() {
        mPlayerViewModel = PlayerViewModel.getInstance(application);
        mPlayerViewModel.init();
    }

    public void addItems(List<Song> items) {
        mData.clear();
        mData.addAll(items);
        notifyDataSetChanged();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layout;
        ImageView image;
        TextView title;
        TextView artist;

        SongViewHolder(final View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.cl_item_song);
            image = itemView.findViewById(R.id.iv_item_song);
            title = itemView.findViewById(R.id.tv_title_item_song);
            artist = itemView.findViewById(R.id.tv_artist_item_song);
        }
    }
}
