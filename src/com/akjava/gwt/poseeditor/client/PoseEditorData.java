package com.akjava.gwt.poseeditor.client;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.THREE;
import com.akjava.gwt.three.client.core.Vector3;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
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
	public List<String> getBones() {
		return bones;
	}
	public void setBones(List<String> bones) {
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
private List<String> bones;
private double cdate;
//TODO read & write


public static String writeData(PoseEditorData data){
	JSONObject poseData=new JSONObject();
	
	//name
	poseData.put("name", new JSONString(data.getName()));
	poseData.put("cdate", new JSONNumber(data.getCdate()));
	
	//bones
	JSONArray bones=new JSONArray();
	for(int i=0;i<data.getBones().size();i++){
		bones.set(i, new JSONString(data.getBones().get(i)));
	}
	
	JSONArray frames=new JSONArray();
	for(int i=0;i<data.getPoseFrameDatas().size();i++){
		JSONObject frameValue=new JSONObject();
		PoseFrameData fdata=data.getPoseFrameDatas().get(i);
		
		//angles
		List<Vector3> angles=fdata.getAngles();
		JSONArray anglesValue=new JSONArray();
		for(int j=0;j<angles.size();j++){
			anglesValue.set(j, toJSONArray(angles.get(j)));
		}
		frameValue.put("angles", anglesValue);
		
		//positions
		List<Vector3> positions=fdata.getAngles();
		JSONArray positionValue=new JSONArray();
		for(int j=0;j<positions.size();j++){
			positionValue.set(j, toJSONArray(positions.get(j)));
		}
		frameValue.put("positions", positionValue);
		
		List<String> ikNames=fdata.getIkTargetNames();
		JSONArray ikNameValue=new JSONArray();
		for(int j=0;j<ikNames.size();j++){
			ikNameValue.set(j, new JSONString(ikNames.get(j)));
		}
		frameValue.put("ik-names", ikNameValue);
		
		
		//positions
		List<Vector3> ikpositions=fdata.getIkTargetPositions();
		JSONArray ikpositionsValue=new JSONArray();
			for(int j=0;j<ikpositions.size();j++){
			positionValue.set(j, toJSONArray(ikpositions.get(j)));
			}
			frameValue.put("ik-positions", ikpositionsValue);
	}
	poseData.put("frames", frames);
	
	return poseData.toString();
}

private static JSONArray toJSONArray(Vector3 vec){
	JSONArray nums=new JSONArray();
	nums.set(0, new JSONNumber(vec.getX()));
	nums.set(1, new JSONNumber(vec.getY()));
	nums.set(2, new JSONNumber(vec.getZ()));
	return nums;
}
//TODO check
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
	
	
	JSONValue cdateValue=poseData.get("cdate");
	JSONNumber cdate=cdateValue.isNumber();
	if(cdate==null){
		LogUtils.log("invalid-cdate");
		return null;
	}
	data.setCdate(cdate.doubleValue());
	
	//bone-names
	JSONArray boneNames=poseData.get("bones").isArray();
	if(boneNames==null){
		LogUtils.log("invalid-bones");
		return null;
	}
	List<String> bones=new ArrayList<String>();
	int bs=boneNames.size();
	for(int i=0;i<bs;i++){
		JSONValue js=boneNames.get(i);
		JSONString jsn=js.isString();
		if(jsn!=null){
			bones.add(jsn.stringValue());
		}else{
			LogUtils.log("invalid-bones-name:"+i);
		}
		
	}
	JSONArray frames=poseData.get("frames").isArray();
	if(frames==null){
		LogUtils.log("invalid-frames");
		return null;
	}
	
	List<PoseFrameData> frameDatas=new ArrayList<PoseFrameData>();
	
	
	int fs=frames.size();
	for(int i=0;i<fs;i++){
		PoseFrameData frameData=new PoseFrameData();
		JSONValue js=frames.get(i);
		JSONObject jsn=js.isObject();
		if(jsn!=null){
			//angle
			List<Vector3> angles=readAngles(jsn.get("angles"));
			if(angles==null){
				LogUtils.log("invalid-frames-angle:"+i);
				return null;
			}
			frameData.setAngles(angles);
			
			
			List<Vector3> positions=readPositions(jsn.get("positions"));
			if(positions==null){
				LogUtils.log("invalid-frames-positions:"+i);
				return null;
			}
			frameData.setPositions(positions);
			
			
			List<String> nameList=readIkNames(jsn.get("ik-names"));
			if(nameList==null){
				LogUtils.log("invalid-frames-nameList:"+i);
				return null;
			}
			frameData.setIkTargetNames(nameList);
			
			
			
			List<Vector3> ikPositions=readIkPositions(jsn.get("ik-positions"));
			if(ikPositions==null){
				LogUtils.log("invalid-frames-ikPositions:"+i);
				return null;
			}
			frameData.setIkTargetPositions(ikPositions);
			
		}else{
			LogUtils.log("invalid-frames:"+i);
			return null;
		}
		frameDatas.add(frameData);
	}
	data.setPoseFrameDatas(frameDatas);
	//frames
		//angle
		//position -->AngleAndMatrix relate-path of bone
		//ikdata
	
	return data;
}


private static  List<Vector3> readAngles(JSONValue value){
	if(value==null){
		return null;
	}
	List<Vector3> angles=new ArrayList<Vector3>();

	JSONArray anglesArray=value.isArray();
	if(anglesArray!=null){
	int as=anglesArray.size();
	for(int j=0;j<as;j++){
	JSONValue angleV=anglesArray.get(j);
	JSONArray angle=angleV.isArray();
	if(angle==null){
		LogUtils.log("invalid-frames-angle:");
		return null;
	}
	if(angle.size()==3){
		Vector3 angleVec=THREE.Vector3();
		JSONNumber xv=angle.get(0).isNumber();
		JSONNumber yv=angle.get(1).isNumber();
		JSONNumber zv=angle.get(2).isNumber();
		if(xv!=null && yv!=null &&zv!=null){
			angleVec.set(xv.doubleValue(), yv.doubleValue(), zv.doubleValue());
			angles.add(angleVec);
		}else{
			LogUtils.log("invalid-frames-angle-v:");
			return null;
		}
	}else{
		LogUtils.log("invalid-frames-angle-size:");
		return null;
	}
	}
	}else{
		LogUtils.log("invalid-frames-angles:");
		return null;
	}
	return angles;
}

private static  List<Vector3> readPositions(JSONValue value){
	if(value==null){
		return null;
	}
	List<Vector3> positions=new ArrayList<Vector3>();
	
	JSONArray positionsArray=value.isArray();
	if(positionsArray!=null){
	int as=positionsArray.size();
	for(int j=0;j<as;j++){
	JSONValue positionV=positionsArray.get(j);
	JSONArray position=positionV.isArray();
	if(position==null){

		return null;
	}
	if(position.size()==3){
		Vector3 positionVec=THREE.Vector3();
		JSONNumber xv=position.get(0).isNumber();
		JSONNumber yv=position.get(1).isNumber();
		JSONNumber zv=position.get(2).isNumber();
		if(xv!=null && yv!=null &&zv!=null){
			positionVec.set(xv.doubleValue(), yv.doubleValue(), zv.doubleValue());
			positions.add(positionVec);
		}else{

			return null;
		}
	}else{
	
		return null;
	}
	}
	}else{
		return null;
	}
	return positions;
}
	
private static  List<Vector3> readIkPositions(JSONValue value){
	if(value==null){
		return null;
	}
	List<Vector3> ikpositions=new ArrayList<Vector3>();
	
	//ik-positions
	
	JSONArray ikpositionsArray=value.isArray();
	if(ikpositionsArray!=null){
	int iks=ikpositionsArray.size();
	for(int j=0;j<iks;j++){
	JSONValue positionV=ikpositionsArray.get(j);
	JSONArray position=positionV.isArray();
	if(position==null){
		return null;
	}
	if(position.size()==3){
		Vector3 positionVec=THREE.Vector3();
		JSONNumber xv=position.get(0).isNumber();
		JSONNumber yv=position.get(1).isNumber();
		JSONNumber zv=position.get(2).isNumber();
		if(xv!=null && yv!=null &&zv!=null){
			positionVec.set(xv.doubleValue(), yv.doubleValue(), zv.doubleValue());
			ikpositions.add(positionVec);
		}else{
			
			return null;
		}
	}else{
		return null;
	}
	}
	}
	
	return ikpositions;
}
private  static List<String> readIkNames(JSONValue value){
	if(value==null){
		return null;
	}
	List<String> names=new ArrayList<String>();
	JSONArray ikNamesArray=value.isArray();
	if(ikNamesArray==null){
		//LogUtils.log("invalid-framesposition:"+i);
		return null;
	}
	
	for(int j=0;j<ikNamesArray.size();j++){
		JSONValue ikn=ikNamesArray.get(j);
		JSONString iknv=ikn.isString();
		if(iknv!=null){
			names.add(iknv.stringValue());
		}else{
			//LogUtils.log("invalid-frames-targetname:"+i+","+j);
			return null;
		}
	}
	return names;
}

}
