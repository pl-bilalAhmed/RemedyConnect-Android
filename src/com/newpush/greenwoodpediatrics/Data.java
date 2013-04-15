package com.newpush.greenwoodpediatrics;

import java.util.HashMap;
import java.util.Map;

public class Data {
	public static final String LINK_BASE = "http://greenwoodpedstest.pediatricweb.com/feed/1B8EC3EB-3101-4C5D-9746-7E877F4A5DF7/";
	
	// List the files which should be downloaded first here. (Only for those which should be parsed on download for further downloads.)
	public static final Map<String, String> preloadDataFiles = new HashMap<String, String>();
	static {
		preloadDataFiles.put("iycs.xml", LINK_BASE + "iycs");
	}
	
	public static final Map<String, String> dataFiles = new HashMap<String, String>();
	static {
		dataFiles.put("office.xml", LINK_BASE + "office");
		dataFiles.put("location.xml", LINK_BASE + "location");
		dataFiles.put("news.xml", LINK_BASE + "news");
	}
}
