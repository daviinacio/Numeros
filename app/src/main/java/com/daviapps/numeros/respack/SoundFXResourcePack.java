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
	
	public MediaPlayer getMediaPlayer(String key){
		try {
			MediaPlayer soundfx = new MediaPlayer();
			//soundfx.setDataSource(super.assets.get(key).getAbsolutePath());
			soundfx.setDataSource(this.find(key).getAbsolutePath());
			soundfx.prepare();

			return soundfx;
			
		} catch(IOException | NullPointerException ex){
			System.err.println("SoundFXResourcePack: Error on get soundfx");
			//ex.printStackTrace();
			return null;
		}
	}
	
	public void play(String key){
		MediaPlayer sound = this.getMediaPlayer(key);
		
		if(sound != null)
			sound.start();
	}
}
