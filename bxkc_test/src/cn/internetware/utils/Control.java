package cn.internetware.utils;

import java.util.ArrayList;
import java.util.List;

public class Control {
	private String id;
	private String name;
	private String value;
	private String desc;
	private String type;
	private List<Pair> validValues = new ArrayList<Pair>();
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public void addValidValue(String label, String value){
		if(validValues == null){
			validValues = new ArrayList<Pair>();
		}
		Pair pair = new Pair(label, value);
		validValues.add(pair);
	}
	
	public void addValidValue(Pair pair){
		if(validValues == null){
			validValues = new ArrayList<Pair>();
		}
		validValues.add(pair);
	}
	
	public void setValidValue(List<Pair> pairList){
		validValues = pairList;
	}
}
