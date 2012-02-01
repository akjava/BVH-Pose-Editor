package com.akjava.gwt.poseeditor.client;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.three.client.core.Matrix4;
import com.akjava.gwt.three.client.core.Vector3;
import com.akjava.gwt.three.client.gwt.animation.AngleAndPosition;
import com.akjava.gwt.three.client.gwt.animation.AnimationBonesData;
import com.akjava.gwt.three.client.gwt.animation.ik.IKData;

public class PoseFrameData {
List<AngleAndPosition> matrixs;//for current bone

private List<Vector3> positions;//relate-pos,+bonePos & multiply angles -> matrixs
public List<Vector3> getPositions() {
	return positions;
}
public void setPositions(List<Vector3> positions) {
	this.positions = positions;
}
public List<Vector3> getAngles() {
	return angles;
}
public void setAngles(List<Vector3> angles) {
	this.angles = angles;
}
private List<Vector3> angles;


List<Vector3> ikTargetPositions;	
List<String> ikTargetNames;
public List<AngleAndPosition> getAngleAndMatrixs() {
	return matrixs;
}
public void setAngleAndMatrixs(List<AngleAndPosition> matrixs) {
	this.matrixs = matrixs;
}
/**
 * @deprecated must be name base
 * @return
 */
public List<Vector3> getIkTargetPositions() {
	return ikTargetPositions;
}
/**
 * @deprecated must be name base
 * @return
 */
public void setIkTargetPositions(List<Vector3> targetPositions) {
	this.ikTargetPositions = targetPositions;
}
public PoseFrameData(List<AngleAndPosition> matrixs,List<Vector3> ikTargetPositions,List<String> ikTargetNames){
	this.matrixs=matrixs;
	this.ikTargetPositions=ikTargetPositions;
	this.ikTargetNames=ikTargetNames;
}
public List<String> getIkTargetNames() {
	return ikTargetNames;
}
public void setIkTargetNames(List<String> ikTargetNames) {
	this.ikTargetNames = ikTargetNames;
}
public PoseFrameData(){}
public PoseFrameData clone(){
	List<AngleAndPosition> matrixs=AnimationBonesData.cloneAngleAndMatrix(this.matrixs);
	
	
	
	List<Vector3> ags=new ArrayList<Vector3>();
	for(Vector3 vec:angles){
		ags.add(vec.clone());
	}
	
	List<Vector3> pos=new ArrayList<Vector3>();
	for(Vector3 vec:positions){
		pos.add(vec.clone());
	}
	
	List<Vector3> targets=new ArrayList<Vector3>();
	for(Vector3 vec:ikTargetPositions){
		targets.add(vec.clone());
	}
	
	List<String> names=new ArrayList<String>();
	for(String name:ikTargetNames){
		names.add(name);
	}
	PoseFrameData pdata= new PoseFrameData(matrixs,targets,names);
	pdata.setAngles(ags);
	pdata.setPositions(pos);
	return pdata;
}
}
