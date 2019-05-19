package com.daviapps.numeros.utils;
import java.io.*;

public final class IOUtils {
	public static void copy(InputStream is, OutputStream os) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while((read = is.read(buffer)) != -1){
			os.write(buffer, 0, read);
		}
	}
}
