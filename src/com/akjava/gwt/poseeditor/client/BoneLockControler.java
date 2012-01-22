package com.akjava.gwt.poseeditor.client;

import java.util.HashMap;
import java.util.Map;

public class BoneLockControler {
	private Map<String,Double> lockMap=new HashMap<String,Double>();
	
	public boolean hasX(String bone){
		return lockMap.get(bone+"-x")!=null;
	}
	
	public void setX(String bone,double value){
		 lockMap.put(bone+"-x",value);
	}
	
	public double getX(String bone){
		return lockMap.get(bone+"-x");
	}
	public void clearX(String name){
		lockMap.remove(name+"-x");
	}
	
	public boolean hasY(String bone){
		return lockMap.get(bone+"-y")!=null;
	}
	
	public void setY(String bone,double value){
		 lockMap.put(bone+"-y",value);
	}
	
	public double getY(String bone){
		return lockMap.get(bone+"-y");
	}
	public void clearY(String name){
		lockMap.remove(name+"-y");
	}
	
	public boolean hasZ(String bone){
		return lockMap.get(bone+"-z")!=null;
	}
	
	public void setZ(String bone,double value){
		 lockMap.put(bone+"-z",value);
	}
	
	public double getZ(String bone){
		return lockMap.get(bone+"-z");
	}
	public void clearZ(String name){
		lockMap.remove(name+"-z");
	}
}
