package com.bondevans.chordinator.asynctask;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.OpenableColumns;
import android.widget.Toast;

import com.bondevans.chordinator.ChordinatorException;
import com.bondevans.chordinator.Log;
import com.bondevans.chordinator.R;
import com.bondevans.chordinator.SongFile;
import com.bondevans.chordinator.db.DBUtils;

import java.util.List;

/**
 * Imports song file URIs in background.
 */
public class ImportSongTask extends AsyncTask<Void, Void, Void> {
	private static final String TAG = "ImportSongTask";
	private Activity mActivity;
	private String mAuthority;
	private List<Uri> mUris;

	public ImportSongTask(Activity activity, String authority, List<Uri> uris) {
		mActivity = activity;
		mAuthority = authority;
		mUris = uris;
	}

	@Override
	protected Void doInBackground(Void... voids) {
		importFiles();
		return null;
	}

	void importFiles() {
		Log.d(TAG, "HELLO - importing files");
		// then iterate thru new list and give each one a set_order
		for (Uri uri: mUris) {
			importSongFile(uri);
		}
	}
	void importSongFile(Uri uri){
		SongFile sf = null;
		try {
			// Create a new SongFile - this loads up the contents of the file into the Song class
			sf = new SongFile(mActivity, uri);
		} catch (ChordinatorException e) {
			Log.d(TAG, "HELLO ERROR!!!!!:"+getFileName(uri));
			return;
		}
		if(sf.hasTitle){
			// Need to compare the correct path - i.e. the same that will be logged when a file is opened from the
			// file browser - this is all handled in DBUtils.
			Log.d(TAG, "HELLO IS chopro");
			if(DBUtils.getSongIdFromUri(mActivity.getContentResolver(), mActivity.getString(R.string.authority), uri)==0){
				Log.d(TAG, "HELLO adding to DB");
				DBUtils.addSong(mActivity.getContentResolver(),
						mActivity.getString(R.string.authority),
						sf.getSongUri(),
						sf.getTitleTitleCase(),
						sf.getArtistTitleCase(),
						sf.getComposerTitleCase());
			}
			else{
				Toast.makeText(mActivity, sf.getTitle()+ " ignored. Already in Chordinator", Toast.LENGTH_SHORT).show();
				Log.d(TAG, "HELLO Already in DB");
			}
		}
		else{
			Log.d(TAG, "HELLO NOT chopro");
			// TODO - convert to chopro
		}
	}
	public String getFileName(Uri uri) {
		String result = null;
		if (uri.getScheme().equals("content")) {
			Cursor cursor = mActivity.getContentResolver().query(uri, null, null, null, null);
			try {
				if (cursor != null && cursor.moveToFirst()) {
					int x=cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
					if(x>=0){
						result = cursor.getString(x);
					}else{
						result = "";
					}
				}
			} finally {
				cursor.close();
			}
		}
		if (result == null) {
			result = uri.getPath();
			int cut = result.lastIndexOf('/');
			if (cut != -1) {
				result = result.substring(cut + 1);
			}
		}
		return result;
	}
}
