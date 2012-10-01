package com.newpush.greenwoodpediatrics;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
	public ArrayList<String> groups;
	public ArrayList<ArrayList<String>> childs;
	private final Context context;

	public CustomExpandableListAdapter(Context context, ArrayList<String> groups, ArrayList<ArrayList<String>> childs) {
		this.context = context;
		this.groups = groups;
		this.childs = childs;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	public int getGroupCount() {
		return groups.size();
	}

	public int getChildrenCount(int groupPosition) {
		return childs.get(groupPosition).size();
	}

	public String getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	public ArrayList<String> getChild(int groupPosition, int childPosition) {
		return childs.get(groupPosition);
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public boolean hasStableIds() {
		return true;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String group = getGroup(groupPosition);
		// @TODO What does LayoutInflater do here?
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.custom_expandable_list_view_group, null);
		}
		TextView header = (TextView)convertView.findViewById(R.id.custom_expandable_list_view_group_header);
		header.setText(group);
		return convertView;
	}

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		String child = getChild(groupPosition, childPosition).get(0);
		// TODO What does LayoutInflater do here?
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.custom_expandable_list_view_child, null);
		}
		WebView content = (WebView)convertView.findViewById(R.id.listChildContentWebView);
		content.loadData(child, "text/html", "utf-8");
		return convertView;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
