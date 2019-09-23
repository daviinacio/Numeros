package com.daviapps.numeros.domain;
import java.util.*;
import java.net.*;

public class ApkVersion {
	private Version version;
	private Date release_date;
	private URL link;
	private URL google_play;
	private int minSdkVersion;
	
	public ApkVersion(){}

	public ApkVersion(Version version, Date release_date, URL link, int minSdkVersion){
		this.version = version;
		this.release_date = release_date;
		this.link = link;
		this.minSdkVersion = minSdkVersion;
	}

	public void setVersion(Version version){ this.version = version; }
	public Version getVersion(){ return version; }

	public void setReleaseDate(Date release_date){ this.release_date = release_date; }
	public Date getReleaseDate(){ return release_date; }

	public void setLink(URL link){ this.link = link; }
	public URL getLink(){ return link; }

	public void setMinSdkVersion(int minSdkVersion){ this.minSdkVersion = minSdkVersion; }
	public Integer getMinSdkVersion(){ return minSdkVersion; }
	
	public void setGooglePlay(URL gp_link){
		this.google_play = gp_link;
	}
	
	public URL getGooglePlay(){
		return this.google_play;
	}
}
