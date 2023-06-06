package com.bondevans.chordinator.grids;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.bondevans.chordinator.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ChordShapeImage {
    private static final String TAG = "ChordShapeImage";
    private final int mBackground;
    private final String mFilePath;
    private Paint mPaint;
    private ChordShapePainter mChordShapePainter;
    private int mMaxGridWidth;
    /**
     * Constructor
     */
    public ChordShapeImage(float textSize, int instrument, String extraShapes, int colour, int backgroundColour, String filePath){
        Log.d(TAG, "HELLO 1");
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setTextSize(textSize);
        mPaint.setColor(colour);
        mBackground = backgroundColour;

        mMaxGridWidth = (int) mPaint.measureText("ABCDEFGHIJKL");
        mChordShapePainter = new ChordShapePainter(mMaxGridWidth, mPaint, extraShapes, instrument);
        mFilePath = filePath;
    }

    public void createPNG(String chord){
        // Strip off any slash - e.g. A/C#
        String chordName = chord.substring(0,chord.indexOf("/"));
        Log.d(TAG, "HELLO createPNG:"+ chordName);
        Bitmap bitmap = Bitmap.createBitmap(mMaxGridWidth, mMaxGridWidth*5/4, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawColor(mBackground);
        // y coord = mPaint
        int y = (int) (mPaint.getTextSize()*1.5);
        mChordShapePainter.drawShape(canvas, chordName, 0, y);

        try {
            String path=mFilePath+chordName+".png";
            bitmap.compress(Bitmap.CompressFormat.PNG, 1, new FileOutputStream(path));
            Log.d(TAG, "HELLO - wrote file: "+path);
        } catch (FileNotFoundException e) {
            Log.d(TAG, e.toString());
            e.printStackTrace();
        }
        bitmap.recycle();
    }
}
