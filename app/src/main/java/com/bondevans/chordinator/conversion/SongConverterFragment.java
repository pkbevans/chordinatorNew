package com.bondevans.chordinator.conversion;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.bondevans.chordinator.ChordinatorException;
import com.bondevans.chordinator.Log;
import com.bondevans.chordinator.R;
import com.bondevans.chordinator.SongFile;
import com.bondevans.chordinator.SongUtils;
import com.bondevans.chordinator.prefs.SongPrefs;

import androidx.fragment.app.DialogFragment;

public class SongConverterFragment extends DialogFragment {
	private final static String TAG = "SongConverterFragment";
	private static final String KEY_TITLE = "KEY1";
	private static final String KEY_ARTIST = "KEY2";
	private static final String KEY_COMPOSER = "KEY3";
	public static final String KEY_FILENAME = "KEY4";
	private EditText titleText;
	private EditText artistText;
	private EditText composerText;
	// Song Conversion
	private static SongConverter mSc;
	private static String mFileName;
	private ConvertFinishedListener listener;

	public static SongConverterFragment newInstance(String authority, String fileName) {
		SongConverterFragment frag = new SongConverterFragment();
		Bundle args = new Bundle();
		args.putString(KEY_FILENAME, fileName);
		frag.setArguments(args);
		return frag;
	}
	public interface ConvertFinishedListener {
		void onConvertFinish(boolean success, String title, String artist);
	}
	public void setConvertFinishedListener(ConvertFinishedListener listener){
		this.listener = listener;
	}
	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		SongFile sf;
		mSc = new SongConverter();
		mFileName=getArguments().getString(KEY_FILENAME);
		// Read in contents of file
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
		// Get the full file path to the chosen song from the Intent
		try {
			// Create a new SongFile - this loads up the contents of the file into the Song class
			sf = new SongFile(mFileName, null, settings.getString(SongPrefs.PREF_KEY_DEFAULT_ENCODING, ""));
		} catch (ChordinatorException e) {
			SongUtils.toast(getActivity(), e.getMessage());
			return null;
		}
		if(sf.hasTitle){
			SongUtils.toast(getActivity(), getString(R.string.already_chopro));
			return null;
		}
		else{
			// Convert to intermediate format....
			mSc.convertToIntermediateFormat(sf.getSong().getSongText());
			// The converter has had a guess at the title, so show this in a dialog box with Artist + composer
			// then set the title/artist/composer tags
			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.song_title_dialog, null);

			titleText = (EditText) layout.findViewById(R.id.title);
			titleText.setText(getArguments().getString(KEY_TITLE));
			artistText = (EditText) layout.findViewById(R.id.artist);
			artistText.setText(getArguments().getString(KEY_ARTIST));
			composerText = (EditText) layout.findViewById(R.id.composer);
			composerText.setText(getArguments().getString(KEY_COMPOSER));

			return new AlertDialog.Builder(getActivity())
			.setView(layout)
			.setTitle(R.string.set_song_details)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// User clicked OK so get title/artist/composer from dialog
					String title = titleText.getText().toString().trim();
					String artist = artistText.getText().toString().trim();
					String composer = composerText.getText().toString().trim();
					mSc.setTitle(title);
					mSc.setComposer(composer);
					mSc.setArtist(artist);
					// then convert intermediate to CHOPRO format...
					listener.onConvertFinish(convertToChoPro(mSc), title, artist);
				}
			})
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Cancel pressed so do nothing
				}
			}).create();
		}
	}
	boolean convertToChoPro(SongConverter mSc) {
		try {
			SongUtils.writeFile(mFileName, mSc.createCSF().trim());
			Log.d(TAG, "HELLO CSF written to tmp file");
			return true;
		} catch (ChordinatorException e) {
			Log.e(TAG, "HELLO ERROR writing CSF to tmp file");
			return false;
		}
	}
}