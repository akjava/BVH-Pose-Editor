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

List<Vector3> positions;//relate-pos
List<Vector3> angles;


List<Vector3> targetPositions;	//TODO must be relative
List<String> targetNames;
public List<AngleAndPosition> getAngleAndMatrixs() {
	return matrixs;
}
public void setAngleAndMatrixs(List<AngleAndPosition> matrixs) {
	this.matrixs = matrixs;
}
public List<Vector3> getTargetPositions() {
	return targetPositions;
}
public void setTargetPositions(List<Vector3> targetPositions) {
	this.targetPositions = targetPositions;
}
public PoseFrameData(List<AngleAndPosition> matrixs,List<Vector3> targetPositions,List<String> targetNames){
	this.matrixs=matrixs;
	this.targetPositions=targetPositions;
	this.targetNames=targetNames;
}
public PoseFrameData clone(){
	List<AngleAndPosition> matrixs=AnimationBonesData.cloneAngleAndMatrix(this.matrixs);
	List<Vector3> targets=new ArrayList<Vector3>();
	for(Vector3 vec:targetPositions){
		targets.add(vec.clone());
	}
	List<String> names=new ArrayList<String>();
	for(String name:targetNames){
		names.add(name);
	}
	return new PoseFrameData(matrixs,targets,names);
}
}
