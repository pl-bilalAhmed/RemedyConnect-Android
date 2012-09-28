package com.newpush.greenwoodpediatrics.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class DefaultParser {
	File XMLToParse;
	Document doc;

	public DefaultParser(String XMLpath) {
		XMLToParse = new File(XMLpath);
		try {
			doc = Jsoup.parse(XMLToParse, "UTF-8", "");
		} catch (IOException e) {

		}
	}

	public ArrayList<String> Parse(String query) {
		ArrayList<String> result = new ArrayList<String>();
		for (Element element : doc.select(query)) {
			result.add(element.text());
		}
		return result;
	}

	public String ParseSingle(String query) {
		return doc.select(query).first().text();
	}
}
