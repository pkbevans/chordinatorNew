package com.bondevans.chordinator.setlist;

import android.net.Uri;

public class SetSong{
    // v6.0.0 - Removed Parcelable  - not used.
	public long id;
	public String title;
	public String artist;
	public String composer;
	public Uri fileUri;
	public int setOrder;

	public SetSong(long id, String title, Uri fileUri, int setOrder){
		this.id = id;
		this.title = title;
		this.fileUri = fileUri;
		this.setOrder = setOrder;
	}
	public SetSong(long id, String title, String artist, String composer, Uri fileUri, int setOrder){
		this.id = id;
		this.title = title;
		this.artist = artist;
		this.composer = composer;
		this.fileUri = fileUri;
		this.setOrder = setOrder;
	}

	public String toString(){
		return title;
	}
}
