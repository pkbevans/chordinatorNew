package com.bondevans.chordinator.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.bondevans.chordinator.Log;
import com.bondevans.chordinator.R;

import androidx.fragment.app.DialogFragment;


public class FirstRunFragment extends DialogFragment{

	protected static final String TAG = "FirstRunFragment";

	public static FirstRunFragment newInstance() {
		Log.d(TAG, "HELLO newInstance");
		FirstRunFragment frag = new FirstRunFragment();
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.d(TAG, "HELLO onCreateDialog");

		return new AlertDialog.Builder(getActivity())
		.setTitle(getString(R.string.chord_aug_firstrun_heading))
		.setMessage(R.string.chord_aug_firstrun_text)
		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {
				//DEPRECATED
			}
		})
		.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Cancel pressed - do nothing
			}
		})
		.create();
	}
}
