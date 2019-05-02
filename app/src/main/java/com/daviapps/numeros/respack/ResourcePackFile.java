package com.daviapps.numeros.respack;
import java.io.*;
import java.util.*;
import com.daviapps.numeros.domain.*;
import java.util.zip.*;
import org.json.*;

public abstract class ResourcePackFile {
	private static final String HeaderFileName = "resource.json";

	// Properties
	private String resName;
	private String resType;
	private Version resVersion;
	private String resAuthor;

	private String appName;
	private Version appMinVersion;

	// Assets
	protected Map<String, File> assets = new HashMap<>();

	public ResourcePackFile(File file){
		try {
			ZipFile zip = new ZipFile(file);

			if(zip.getEntry(HeaderFileName) != null){
				ZipEntry zHeader = zip.getEntry(HeaderFileName);

				InputStream is = zip.getInputStream(zHeader);
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				OutputStream os;

				// Read resource header
				StringBuilder sb = new StringBuilder();
				while(br.ready()){
					sb.append(br.readLine());
				}

				JSONObject jHeader = new JSONObject(sb.toString());
				this.resName = jHeader.getString("res-name");
				this.resType = jHeader.getString("res-type");
				this.resVersion = new Version(jHeader.getJSONArray("res-version").join("."));
				this.resAuthor = jHeader.getString("res-author");
				this.appName = jHeader.getString("app-name");
				this.appMinVersion = new Version(jHeader.getJSONArray("app-min-version").join("."));

				is.close();


				System.out.println(toString());
				System.out.println();
			}

			// Load assets
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
                ZipEntry zEntry = entries.nextElement();

				if(zEntry.getName().contains(getResourceType())){
					String name = zEntry.getName();
					String key = name.substring(name.indexOf("/") + 1, name.lastIndexOf("."));
					String tempSuffix = name.substring(name.lastIndexOf(".") + 1, name.length());
					String tempPrefix = String.format("%s-%s-%s", 
													  file.getName().substring(0, file.getName().lastIndexOf(".")),
													  getResourceType(),
													  key
													  );

					System.out.println("Prefix:\t" + tempPrefix);
					System.out.println("Sufix:\t" + tempSuffix);
					System.out.println("Key:\t" + key);
					System.out.println("zPath:\t" + zEntry.getName());
					System.out.println();


					File temp = File.createTempFile(tempPrefix, tempSuffix);

					InputStream is = zip.getInputStream(zEntry);
					OutputStream os = new FileOutputStream(temp);
					byte data[] = new byte[1024];
					int count = 0;

					// Write file
					while((count = is.read(data)) != -1){
						os.write(data, 0, count);
					}

					this.assets.put(key, temp);
				}
            }
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public String getResourceName(){ return resName; }
	public String getResourceType(){ return resType; }
	public Version getResourceVersion(){ return resVersion; }
	public String getResourceAuthor(){ return resAuthor; }
	public String getApplicationName(){ return appName; }
	public Version getMinApplicationVersion(){ return appMinVersion; }

	@Override
	public String toString(){
		return String.format("ResName:\t%s\nResType:\t%s\nResVersion:\t%s\nResAuthor:\t%s\nAppName:\t%s\nAppMinVersion:\t%s",
							 resName, resType, resVersion, resAuthor, appName, appMinVersion);
	}
}
