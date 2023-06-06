package com.bondevans.chordinator.chunking;

import com.bondevans.chordinator.Log;



class LineChunker {
	private static final String TAG = "TextLineChunker";
	private StringBuilder lyric;
	private StringBuilder chord;
	boolean hasMore;
	
	LineChunker(SongChunk sc){
		this.lyric = new StringBuilder(sc.getLyricText());
		this.chord = new StringBuilder(sc.getChordText());
		hasMore = sc.getChordText().length() > 0 || sc.getLyricText().length() > 0;
			
	}

	LineChunk getNextChunk(int available, int count){
		Log.d(TAG, "available=["+available+"] lyric=["+ lyric.toString()+"] chord=["+ chord.toString()+"]");
		boolean any = false;
		hasMore = false;
		while(lyric.length()>0 || chord.length()>0){
			hasMore = true;
			ChordAndLyric x = getLongestChunk(available, lyric.toString(), chord.toString(), any);
			if(x.isEmpty){
				if(count == 0){
					// no room on current line so start new one and try again
					return new LineChunk("", "", true);
				}
				else if(count==1){
					// We've started a new line and still can't fit anything on so just get whatever
					// characters will fit - never mind whole words
					count++;
					any = true;
				}
				else{
					Log.d(TAG, "OOPS!");
					break;//give up
				}
			}
			else{
				// Got something so return chunk with chord (if there is any) and lyric
				String saveChord = chord.toString();
				// Remove bit we've added from original strings
				lyric.delete(0, x.lyric.length());
				chord.delete(0, chord.length());
				hasMore = (lyric.length()>0 || chord.length()>0);
				if(hasMore){
					// Remove leading spaces from start of remaining lyrics, BUT:
					// make sure that the next word doesn't fit in the available space (without the space)
					// Do this by adding a space onto the end of the chunk we send back - but only if
					// that chunk is less than available
					while( lyric.length()>0 && lyric.charAt(0) == ' '){
						lyric.deleteCharAt(0);
					}
					if(x.lyric.length()<available){
						x.lyric = x.lyric.concat(" ");
					}
				}
				return new LineChunk(saveChord, x.lyric, false);
			}
		}
		return new LineChunk("","",true);
	}
	
	private ChordAndLyric getLongestChunk( int available, String lyric, String chord, boolean anyChars){
		Log.d(TAG, "HELLO getLongestChunk: lyric[" + lyric+"] chord=["+chord+"] available["+available+"]");
		ChordAndLyric ret;
		if(anyChars){
			// Remove chars until chord and lyrics fit
			StringBuilder sb = new StringBuilder(lyric);
			while( sb.toString().length()>available){
				sb.delete(sb.length()-1, sb.length());
			}
			ret = new ChordAndLyric(chord,sb.toString());
		}
		else{
			// Chord and at least 1st word must fit - else new line
			String[] words = lyric.split("\\s");
			int mc = chord.length();
			int mw;
			if(words.length==0){
				// All spaces
				mw = lyric.length();
			}
			else{
				mw = words[0].length();
			}
			Log.d(TAG, "mc=["+mc+"] mw=["+mw+"]");
			if(mc> available ||	mw>available){
				// no room for chord or first word
				ret = new ChordAndLyric("","");
			}
			else if(lyric.length() <= available){
				// the whole thing fits
				ret = new ChordAndLyric(chord, lyric);
			}
			else{
				// We know that at least the chords and the first word of the lyrics fit, so
				// now to find the maximum length of lyrics that will fit the available space
				StringBuilder sb = new StringBuilder(words[0]);
				int i = 1;
				while(i<words.length ){
					// Add another word to a tmp string
					String tmp = sb.toString().concat(" "+words[i]);
					// see if it still fits
					if(tmp.length()<= available){
						// if so add it to the real string
						sb.append(" ").append(words[i++]);
					}
					else{
						break;
					}
					Log.d(TAG, "words=["+sb.toString()+"]=["+sb.toString().length()+"]");
				}
				ret = new ChordAndLyric(chord,sb.toString());
			}
		}
		Log.d(TAG, "HELLO gotLongestString:["+ret.toString()+"]");
		return ret;
	}

	private class ChordAndLyric{
		String chord;
		String lyric;
		boolean isEmpty;
		ChordAndLyric(String c, String l){
			chord = c;
			lyric = l;
			isEmpty = chord.length() == 0 && lyric.length() == 0;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "ChordAndLyric chord=[" + chord + "], lyric=[" + lyric + "]";
		}
	}
}
