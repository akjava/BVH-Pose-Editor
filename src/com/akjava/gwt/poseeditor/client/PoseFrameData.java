package com.akjava.gwt.poseeditor.client;

import java.util.List;

import com.akjava.gwt.three.client.core.Matrix4;
import com.akjava.gwt.three.client.core.Vector3;

public class PoseFrameData {
List<Matrix4> matrixs;
List<Vector3> targetPositions;
public List<Matrix4> getMatrixs() {
	return matrixs;
}
public void setMatrixs(List<Matrix4> matrixs) {
	this.matrixs = matrixs;
}
public List<Vector3> getTargetPositions() {
	return targetPositions;
}
public void setTargetPositions(List<Vector3> targetPositions) {
	this.targetPositions = targetPositions;
}
public PoseFrameData(List<Matrix4> matrixs,List<Vector3> targetPositions){
	this.matrixs=matrixs;
	this.targetPositions=targetPositions;
}
}
