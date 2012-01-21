package com.akjava.gwt.poseeditor.client;

import java.util.List;

import com.akjava.gwt.three.client.core.Matrix4;
import com.akjava.gwt.three.client.core.Vector3;
import com.akjava.gwt.three.client.gwt.animation.AngleAndMatrix;

public class PoseFrameData {
List<AngleAndMatrix> matrixs;
List<Vector3> targetPositions;
public List<AngleAndMatrix> getMatrixs() {
	return matrixs;
}
public void setMatrixs(List<AngleAndMatrix> matrixs) {
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
}
