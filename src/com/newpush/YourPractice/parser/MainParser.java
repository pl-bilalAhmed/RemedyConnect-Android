package com.newpush.YourPractice.parser;

import android.text.TextUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainParser {
    private Document doc;

    public MainParser(String XMLpath) {
        File XMLToParse = new File(XMLpath);
        try {
            doc = Jsoup.parse(XMLToParse, "UTF-8", "");
        } catch (IOException e) {
            throw new ParserException("Parsing error!");
        }
    }

    // Utilities

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


    public Date ParseDateTimeString(String dateTimeString) {
        try {
            return new SimpleDateFormat("M/d/yyyy h:mm:ss a", Locale.US).parse(dateTimeString);
        } catch (ParseException e) {
            return null;
        }
    }

    // Format checkers
    public Boolean isRoot() {
        Elements items = doc.select("mobilefeed practices Practice");
        return items != null && items.size() > 0;
    }

    public Boolean isPage() {
        Elements items = doc.select("mobilefeed PageText");
        return items != null && items.size() > 0;
    }

    public Boolean isMenu() {
        Elements items = doc.select("mobilefeed buttons Button");
        return items != null && items.size() > 0;
    }

    public Boolean isArticleSet() {
        Elements items = doc.select("mobilefeed articles article");
        return items != null && items.size() > 0;
    }

    // Getters

    public HashMap<String, String> getPage() {
        HashMap<String, String> page = new HashMap<String, String>();

        Elements mobileFeed = doc.select("mobilefeed");
        page.put("modified", mobileFeed.select("PageModified").text());
        page.put("title", mobileFeed.select("PageTitle").text());
        page.put("text", mobileFeed.select("PageText").text());

        return page;
    }

    public ArrayList<HashMap<String, String>> getMenu() {
        ArrayList<HashMap<String, String>> menu = new ArrayList<HashMap<String, String>>();
        Elements mobileFeed = doc.select("mobilefeed buttons");
        for (Element buttonElement : mobileFeed.select("Button")) {
            HashMap<String, String> button = new HashMap<String, String>();
            button.put("name", buttonElement.select("ButtonName").text());
            button.put("feed", buttonElement.select("NextFeed").text());
            button.put("externalLink", buttonElement.select("ExternalLink").text());
            menu.add(button);
        }
        return menu;
    }

    public ArrayList<HashMap<String, String>> getArticleSet() {
        ArrayList<HashMap<String, String>> articleSet = new ArrayList<HashMap<String, String>>();
        Elements mobileFeed = doc.select("mobilefeed articles");
        for (Element articleElement : mobileFeed.select("Article")) {
            HashMap<String, String> article = new HashMap<String, String>();
            article.put("modified", articleElement.select("dateModified").text());
            article.put("title", articleElement.select("articleTitle").text());
            article.put("text", articleElement.select("articleText").text());
            articleSet.add(article);
        }
        return articleSet;
    }

    public ArrayList<String> getArticleSetTitles() {
        ArrayList<String> titles = new ArrayList<String>();
        Elements mobileFeed = doc.select("mobilefeed articles");
        for (Element articleElement : mobileFeed.select("Article")) {
            titles.add(articleElement.select("articleTitle").text());
        }
        return titles;
    }

    public String getArticleFromSet(Integer index) {
        Elements article = doc.select("mobilefeed articles Article:eq(" + index.toString() + ") articleText");
        return article.text();
    }

    public ArrayList<String> getSubfeedURLs() {
        ArrayList<String> subfeedURLs = new ArrayList<String>();
        ArrayList<HashMap<String, String>> menu = getMenu();
        for (HashMap<String, String> menuItem : menu) {
            if (!menuItem.get("feed").isEmpty()) {
                subfeedURLs.add(menuItem.get("feed"));
            }
        }
        return subfeedURLs;
    }

    public static String subFeedURLToLocal(String subFeedURL, String feedRoot) {
        subFeedURL = subFeedURL.replaceAll("^http(s)?", "");
        feedRoot = feedRoot.replaceAll("^http(s)?", "");
        return subFeedURL.replaceAll("(?i)" + feedRoot + "/", "");
    }

    public ArrayList<HashMap<String, String>> getRootPractices() {
        ArrayList<HashMap<String, String>> rootPractices = new ArrayList<HashMap<String, String>>();
        Elements mobileFeed = doc.select("mobilefeed practices");
        for (Element practiceElement : mobileFeed.select("Practice")) {
            HashMap<String, String> practice = new HashMap<String, String>();
            practice.put("name", practiceElement.select("PracticeName").text());
            practice.put("location", getRootPracticesLocationInfo(practiceElement));
            practice.put("feed", practiceElement.select("PracticeFeed").text());
            practice.put("designPack", practiceElement.select("PracticeDesignPack").text());
            rootPractices.add(practice);
        }
        return rootPractices;
    }

    public String getRootPracticesLocationInfo(Element practiceElement) {
        // A LinkedHashSet is used to keep order of insertion:
        Set<String> locationSet = new LinkedHashSet<String>();
        for (Element locationElement : practiceElement.select("practicelocation")) {
            locationSet.add(locationElement.select("city").text() + ", " +
                    locationElement.select("state").text());
        }
        return TextUtils.join(" | ", locationSet);
    }
}
