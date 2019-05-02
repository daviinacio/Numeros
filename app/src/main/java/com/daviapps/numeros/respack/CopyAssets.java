package com.daviapps.numeros.respack;
import java.io.*;
import android.util.*;
import android.content.res.*;

public class CopyAssets {
	AssetManager assetManager;
	File externalPath;
	
	public CopyAssets(AssetManager assetManager, File externalPath){
		this.assetManager = assetManager;
		this.externalPath = externalPath;
	}
	
	
	public void copy(){
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
			Log.e("tag", "Failed to get asset file list.", e);
		}
		if (files != null) for (String filename : files) {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = assetManager.open(filename);
				File outFile = new File(externalPath, filename);
				out = new FileOutputStream(outFile);
				byte[] buffer = new byte[1024];
				int read;
				while((read = in.read(buffer)) != -1){
					out.write(buffer, 0, read);
				}

			} catch(IOException e) {
				Log.e("tag", "Failed to copy asset file: " + filename, e);
			}     
			finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// NOOP
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						// NOOP
					}
				}
			}
		}
	}
}
