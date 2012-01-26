package com.akjava.gwt.poseeditor.client;

import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.gwt.animation.AnimationBone;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class PoseEditorData {
public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<PoseFrameData> getPoseFrameDatas() {
		return poseFrameDatas;
	}
	public void setPoseFrameDatas(List<PoseFrameData> poseFrameDatas) {
		this.poseFrameDatas = poseFrameDatas;
	}
	public List<AnimationBone> getBones() {
		return bones;
	}
	public void setBones(List<AnimationBone> bones) {
		this.bones = bones;
	}
	public double getCdate() {
		return cdate;
	}
	public void setCdate(double cdate) {
		this.cdate = cdate;
	}
private String name;
private List<PoseFrameData> poseFrameDatas;
private List<AnimationBone> bones;
private double cdate;
//TODO read & write

public static PoseEditorData readData(String jsonString){
	JSONValue value=JSONParser.parseLenient(jsonString);
	
	JSONObject poseData=value.isObject();
	if(poseData==null){
		LogUtils.log("invalid-data");
		return null;
	}
	PoseEditorData data=new PoseEditorData();
	
	JSONValue nameValue=poseData.get("name");
	JSONString name=nameValue.isString();
	if(name==null){
		LogUtils.log("invalid-name");
		return null;
	}
	data.setName(name.stringValue());
	
	return data;
}
}
