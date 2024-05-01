package com.ronelgazar.touchtunes.adapter;

import java.util.List;

import com.ronelgazar.touchtunes.model.Song;

import com.ronelgazar.touchtunes.R;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SongsAdapter extends BaseAdapter {

    private Context context;
    private List<Song> songs;
    private MediaPlayer mediaPlayer;

    public SongsAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
        this.mediaPlayer = new MediaPlayer();
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.song_item, parent, false);
        }

        Song song = songs.get(position);

        TextView songTitleTextView = view.findViewById(R.id.song_title_text_view);
        songTitleTextView.setText(song.getTitle());

        view.setOnClickListener(view1 -> {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(song.getUrl());
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

        // Release the MediaPlayer object if it is still in use.
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
