package com.akjava.gwt.poseeditor.client;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.three.client.core.Matrix4;
import com.akjava.gwt.three.client.core.Vector3;
import com.akjava.gwt.three.client.gwt.animation.AngleAndMatrix;
import com.akjava.gwt.three.client.gwt.animation.AnimationBonesData;
import com.akjava.gwt.three.client.gwt.animation.ik.IKData;

public class PoseFrameData {
List<AngleAndMatrix> matrixs;
List<Vector3> targetPositions;
public List<AngleAndMatrix> getAngleAndMatrixs() {
	return matrixs;
}
public void setAngleAndMatrixs(List<AngleAndMatrix> matrixs) {
	this.matrixs = matrixs;
}
public List<Vector3> getTargetPositions() {
	return targetPositions;
}
public void setTargetPositions(List<Vector3> targetPositions) {
	this.targetPositions = targetPositions;
}
public PoseFrameData(List<AngleAndMatrix> matrixs,List<Vector3> targetPositions){
	this.matrixs=matrixs;
	this.targetPositions=targetPositions;
}
public PoseFrameData clone(){
	List<AngleAndMatrix> matrixs=AnimationBonesData.cloneAngleAndMatrix(this.matrixs);
	List<Vector3> targets=new ArrayList<Vector3>();
	for(Vector3 vec:targetPositions){
		targets.add(vec.clone());
	}
	return new PoseFrameData(matrixs,targets);
}
}
