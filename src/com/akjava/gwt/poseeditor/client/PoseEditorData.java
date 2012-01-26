package com.akjava.gwt.poseeditor.client;

import java.util.List;

import com.akjava.gwt.three.client.gwt.animation.AnimationBone;

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

}
