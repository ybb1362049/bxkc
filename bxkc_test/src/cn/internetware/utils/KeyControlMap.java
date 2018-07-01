package cn.internetware.utils;

import java.util.ArrayList;
import java.util.List;

public class KeyControlMap {
	private String key = "";
	private List<Control> controlList = new ArrayList<Control>();
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public List<Control> getControlList() {
		return controlList;
	}
	public void setControlList(List<Control> controlList) {
		this.controlList = controlList;
	}
}
