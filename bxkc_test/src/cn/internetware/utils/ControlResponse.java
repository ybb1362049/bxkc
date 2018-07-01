package cn.internetware.utils;

import java.util.ArrayList;
import java.util.List;

import cn.internetware.phone.extension.response.impl.TxtBaseResponse;

public class ControlResponse extends TxtBaseResponse{
	String id;
	String name;
	String value;
	String desc;
	String type;
	List<Pair> validValues = new ArrayList<Pair>();
	
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

	public void addValue(String label, String value){
		if(validValues == null){
			validValues = new ArrayList<Pair>();
		}
		Pair pair = new Pair(label, value);
		validValues.add(pair);
	}
	
	public class Pair {
		String label;
		String value;
		public Pair(){}
		public Pair(String label, String value) {
			this.label = label;
			this.value = value;
		}
	}
}

