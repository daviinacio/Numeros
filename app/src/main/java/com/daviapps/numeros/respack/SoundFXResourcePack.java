package com.daviapps.numeros.respack;
import java.io.*;
import android.media.*;

public class SoundFXResourcePack extends ResourcePackFile {

	public SoundFXResourcePack(File root, String fileName){
		super(new File(root, fileName));
	}

	public SoundFXResourcePack(File file){
		super(file);
	}

	public MediaPlayer find(String key){
		try {
			MediaPlayer soundfx = new MediaPlayer();
			soundfx.setDataSource(super.assets.get(key).getAbsolutePath());
			soundfx.prepare();

			return soundfx;
		}
		catch(IOException ex){ ex.printStackTrace(); }

		return null;
	}
}
