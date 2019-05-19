package com.daviapps.numeros.respack;
import java.io.*;
import android.util.*;
import android.content.res.*;
import com.daviapps.numeros.utils.*;

public class CopyAssets {
	AssetManager assetManager;
	File externalPath;
	
	public CopyAssets(AssetManager assetManager, File externalPath){
		this.assetManager = assetManager;
		this.externalPath = externalPath;
	}
	
	
	public void copy(){
		// TODO: Refactore this method
		
		
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
				
				IOUtils.copy(in, out);

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
