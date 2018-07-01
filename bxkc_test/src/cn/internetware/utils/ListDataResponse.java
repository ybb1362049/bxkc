package cn.internetware.utils;

import java.util.ArrayList;
import java.util.List;

public class ListDataResponse {
	private List<List<String>> titleList = new ArrayList<List<String>>();
	private List<List<String>> dataList = new ArrayList<List<String>>();
	public List<List<String>> getTitleList() {
		return titleList;
	}
	
	public List<List<String>> getDataList() {
		return dataList;
	}
	
}
