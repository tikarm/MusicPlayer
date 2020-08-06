package tigran.applications.musicplayer.ui.songs;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import tigran.applications.musicplayer.R;
import tigran.applications.musicplayer.data.model.Song;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongViewHolder> {

    //data
    List<Song> mData = new ArrayList<>();

    //context
    Context context;

    public SongsAdapter(Context context) {
        this.context = context;
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
        Song song = mData.get(position);

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
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addItems(List<Song> items) {
        mData.clear();
        mData.addAll(items);
        notifyDataSetChanged();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView artist;

        SongViewHolder(final View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.iv_item_song);
            title = itemView.findViewById(R.id.tv_title_item_song);
            artist = itemView.findViewById(R.id.tv_artist_item_song);
        }
    }
}
