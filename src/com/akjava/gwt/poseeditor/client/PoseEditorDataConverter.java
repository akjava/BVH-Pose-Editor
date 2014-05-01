package com.akjava.gwt.poseeditor.client;

import java.util.ArrayList;
import java.util.List;

import com.akjava.bvh.client.BVH;
import com.akjava.bvh.client.BVHNode;
import com.akjava.bvh.client.Channels;
import com.akjava.bvh.client.NameAndChannel;
import com.akjava.gwt.bvh.client.threejs.AnimationDataConverter;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.gwt.animation.AnimationData;
import com.google.common.base.Converter;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class PoseEditorDataConverter extends Converter<BVH,JSONObject>{

	/**
	 * TODO set name and cdate by yourself
	 */
	@Override
	protected JSONObject doForward(BVH bvh) {
		List<BVHNode> nodes=bvh.getNodeList();
		
		List<String> boneNameList=new ArrayList<String>();//for find Index
		JsArrayString boneNames=(JsArrayString) JsArrayString.createArray();
		for(BVHNode node:nodes){
			boneNames.push(node.getName());
			boneNameList.add(node.getName());
		}
		
		
		
		List<NameAndChannel> channels=bvh.getNameAndChannels();
		
		JsArray<JavaScriptObject> frames=(JsArray<JavaScriptObject>) JsArray.createArray();
		
		for(int i=0;i<bvh.getFrames();i++){
			//re create FrameData 
			JsArray<JsArrayNumber> positions=(JsArray<JsArrayNumber>) JsArray.createArray();
			JsArray<JsArrayNumber> rots=(JsArray<JsArrayNumber>) JsArray.createArray();
			for(int j=0;j<boneNameList.size();j++){
				JsArrayNumber pos=(JsArrayNumber) JsArrayNumber.createArray();
				pos.push(0);
				pos.push(0);
				pos.push(0);
				positions.push(pos);
				
				JsArrayNumber rot=(JsArrayNumber) JsArrayNumber.createArray();
				rot.push(0);
				rot.push(0);
				rot.push(0);
				rots.push(rot);
			}
			
			double[] values=bvh.getFrameAt(i);//must same channels size
			int valueIndex=0;
			
			
			
			for(NameAndChannel channel:channels){
				
				int index=boneNameList.indexOf(channel.getName());
				if(index==-1){
					LogUtils.log("not found:maybe invalid-channel:"+channel.getName());
					continue;
				}
				
				boolean targetPos=false;
				int targetIndex=0;
				
				switch(channel.getChannel()){
				case Channels.XPOSITION:
					targetPos=true;
					targetIndex=0;
					break;
				case Channels.YPOSITION:
					targetPos=true;
					targetIndex=1;
					break;
				case Channels.ZPOSITION:
					targetPos=true;
					targetIndex=2;
					break;
				case Channels.XROTATION:
					targetPos=false;
					targetIndex=0;
					break;
				case Channels.YROTATION:
					targetPos=false;
					targetIndex=1;
					break;
				case Channels.ZROTATION:
					targetPos=false;
					targetIndex=2;
					break;
				default:
					LogUtils.log("invalid type:"+channel.getChannel());
				}
				
				if(targetPos){
					positions.get(index).set(targetIndex, values[valueIndex]);
				}else{
					rots.get(index).set(targetIndex, values[valueIndex]);
				}
				
				valueIndex++;
			}
			
			JSONObject frame=new JSONObject();
			frame.put("angles", new JSONArray(rots));
			frame.put("positions", new JSONArray(positions));
			
			frames.push(frame.getJavaScriptObject());
		}
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("frames", new JSONArray(frames));
		jsonObject.put("bones", new JSONArray(boneNames));
		
		return jsonObject;
	}
	
	

	/**
	 * use BVHTools convertPoseEditorDataToBVH
	 */
	@Override
	protected BVH doBackward(JSONObject b) {
		throw new RuntimeException("not support yet");
	}

}
