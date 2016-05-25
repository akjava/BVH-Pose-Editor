package com.akjava.gwt.poseeditor.client;

import com.akjava.gwt.three.client.gwt.core.BoundingBox;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;

public class SkinningVertexUtils {
private SkinningVertexUtils(){}
	public static BoundingBox computeBoundingBox(SkinnedMesh skin){
		BoundingBox box=BoundingBox.createObject().cast();
		Vector3 min=THREE.Vector3(Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE);
		Vector3 max=THREE.Vector3(Double.MIN_VALUE,Double.MIN_VALUE,Double.MIN_VALUE);
		
		for(int i=0;i<skin.getGeometry().getVertices().length();i++){
			Vector3 vec=transformedSkinVertex(skin,i);
			
			if(vec.getX()<min.getX()){
				min.setX(vec.getX());
			}
			if(vec.getY()<min.getY()){
				min.setY(vec.getY());
			}
			if(vec.getZ()<min.getZ()){
				min.setZ(vec.getZ());
			}
			
			if(vec.getX()>max.getX()){
				max.setX(vec.getX());
			}
			if(vec.getY()>max.getY()){
				max.setY(vec.getY());
			}
			if(vec.getZ()>max.getZ()){
				max.setZ(vec.getZ());
			}
		}
		
		box.setMax(max);
		box.setMin(min);
		return box;
	}
	
	//from http://stackoverflow.com/questions/31620194/how-to-calculate-transformed-skin-vertices
	
	public static final native Vector3 transformedSkinVertex (SkinnedMesh skin,int index)/*-{
	
	//var skinIndices = (new $wnd.THREE.Vector4 ()).fromAttribute (skin.geometry.getAttribute ('skinIndex'), index);
    //var skinWeights = (new $wnd.THREE.Vector4 ()).fromAttribute (skin.geometry.getAttribute ('skinWeight'), index);
    //var skinVertex = (new $wnd.THREE.Vector3 ()).fromAttribute (skin.geometry.getAttribute ('position'), index).applyMatrix4 (skin.bindMatrix);
   var skinIndices =skin.geometry.skinIndices[index];
   var skinWeights =skin.geometry.skinWeights[index];
   var skinVertex =skin.geometry.vertices[index].clone().applyMatrix4(skin.bindMatrix);
	
    var result = new $wnd.THREE.Vector3 (), temp = new $wnd.THREE.Vector3 (), tempMatrix = new $wnd.THREE.Matrix4 (); properties = ['x', 'y', 'z', 'w'];
    for (var i = 0; i < 4; i++) {
        var boneIndex = skinIndices[properties[i]];
        tempMatrix.multiplyMatrices (skin.skeleton.bones[boneIndex].matrixWorld, skin.skeleton.boneInverses[boneIndex]);
       // result.add (temp.copy (skinVertex).multiplyScalar (skinWeights[properties[i]]).applyMatrix4 (tempMatrix));
        result.add (temp.copy (skinVertex).applyMatrix4 (tempMatrix).multiplyScalar (skinWeights[properties[i]]));
    }
    return result.applyMatrix4 (skin.bindMatrixInverse);
	}-*/;
}
