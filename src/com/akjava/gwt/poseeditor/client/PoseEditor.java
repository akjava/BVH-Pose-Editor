package com.akjava.gwt.poseeditor.client;

import java.util.ArrayList;
import java.util.List;

import com.akjava.bvh.client.AnimationBoneConverter;
import com.akjava.bvh.client.AnimationDataConverter;
import com.akjava.bvh.client.BVH;
import com.akjava.bvh.client.BVHParser;
import com.akjava.bvh.client.BVHParser.ParserListener;
import com.akjava.gwt.html5.client.HTML5InputRange;
import com.akjava.gwt.html5.client.HTML5InputRange.HTML5InputRangeListener;
import com.akjava.gwt.html5.client.extra.HTML5Builder;
import com.akjava.gwt.three.client.THREE;
import com.akjava.gwt.three.client.core.Geometry;
import com.akjava.gwt.three.client.core.Matrix4;
import com.akjava.gwt.three.client.core.Object3D;
import com.akjava.gwt.three.client.core.Quaternion;
import com.akjava.gwt.three.client.core.Vector3;
import com.akjava.gwt.three.client.core.Vector4;
import com.akjava.gwt.three.client.core.Vertex;
import com.akjava.gwt.three.client.extras.GeometryUtils;
import com.akjava.gwt.three.client.extras.ImageUtils;
import com.akjava.gwt.three.client.extras.loaders.JSONLoader;
import com.akjava.gwt.three.client.extras.loaders.JSONLoader.LoadHandler;
import com.akjava.gwt.three.client.gwt.GWTGeometryUtils;
import com.akjava.gwt.three.client.gwt.GWTThreeUtils;
import com.akjava.gwt.three.client.gwt.SimpleDemoEntryPoint;
import com.akjava.gwt.three.client.gwt.ThreeLog;
import com.akjava.gwt.three.client.gwt.animation.AnimationBone;
import com.akjava.gwt.three.client.gwt.animation.AnimationBonesData;
import com.akjava.gwt.three.client.gwt.animation.AnimationData;
import com.akjava.gwt.three.client.gwt.animation.AnimationHierarchyItem;
import com.akjava.gwt.three.client.gwt.animation.AnimationKey;
import com.akjava.gwt.three.client.gwt.animation.NameAndVector3;
import com.akjava.gwt.three.client.gwt.animation.WeightBuilder;
import com.akjava.gwt.three.client.gwt.animation.ik.CDDIK;
import com.akjava.gwt.three.client.lights.Light;
import com.akjava.gwt.three.client.materials.Material;
import com.akjava.gwt.three.client.objects.Mesh;
import com.akjava.gwt.three.client.renderers.WebGLRenderer;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PoseEditor extends SimpleDemoEntryPoint{
	private BVH bvh;
	protected JsArray<AnimationBone> bones;
	private AnimationData animationData;
	@Override
	protected void beforeUpdate(WebGLRenderer renderer) {
		if(root!=null){
			root.setPosition(positionXRange.getValue(), positionYRange.getValue(), positionZRange.getValue());
			
			root.getRotation().set(Math.toRadians(rotationRange.getValue()),Math.toRadians(rotationYRange.getValue()),Math.toRadians(rotationZRange.getValue()));
			}
	}

	@Override
	protected void initializeOthers(WebGLRenderer renderer) {
		scene.add(THREE.AmbientLight(0xffffff));
		
		Light pointLight = THREE.DirectionalLight(0xffffff,1);
		pointLight.setPosition(0, 10, 300);
		scene.add(pointLight);
		
		Light pointLight2 = THREE.DirectionalLight(0xffffff,1);//for fix back side dark problem
		pointLight2.setPosition(0, 10, -300);
		//scene.add(pointLight2);
		
		root=THREE.Object3D();
		scene.add(root);
		
		loadBVH("14_01.bvh");
	}

	private boolean clicked=true;
	@Override
	public void onMouseClick(ClickEvent event) {
		
		//not work correctly on zoom
		//Vector3 pos=GWTUtils.toWebGLXY(event.getX(), event.getY(), camera, screenWidth, screenHeight);
		
	//	targetPos.setX(pos.getX());
		//targetPos.setY(pos.getY());
		
		//doCDDIk();
		//doPoseIkk(0);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		mouseDown=true;
		mouseDownX=event.getX();
		mouseDownY=event.getY();
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		mouseDown=false;
	}
	
	@Override
	public void onMouseOut(MouseOutEvent event) {
		mouseDown=false;
	}
	
	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if(mouseDown){
		double diffX=event.getX()-mouseDownX;
		double diffY=event.getY()-mouseDownY;
		mouseDownX=event.getX();
		mouseDownY=event.getY();
		
		diffX*=0.1;
		diffY*=-0.1;
		targetPos.incrementX(diffX);
		targetPos.incrementY(diffY);
		doPoseIkk(0);
		}
	}
	
	@Override
	public void onMouseWheel(MouseWheelEvent event) {
		if(event.isShiftKeyDown()){//swapped
			//TODO make class
			long t=System.currentTimeMillis();
			if(mouseLast+100>t){
				tmpZoom*=2;
			}else{
				tmpZoom=defaultZoom;
			}
			//GWT.log("wheel:"+event.getDeltaY());
			int tmp=cameraZ+event.getDeltaY()*tmpZoom;
			tmp=Math.max(minCamera, tmp);
			tmp=Math.min(4000, tmp);
			cameraZ=tmp;
			mouseLast=t;
			
		}else{
			onMouseWheelWithShiftKey(event.getDeltaY());
		}
	}
	
	
	public  void onMouseWheelWithShiftKey(int deltaY){
		double dy=deltaY*0.2;
		targetPos.incrementZ(dy);
		doPoseIkk(0);
	}

	private HTML5InputRange positionXRange;
	private HTML5InputRange positionYRange;
	private HTML5InputRange positionZRange;
	private HTML5InputRange frameRange;
	
	private HTML5InputRange rotationRange;
	private HTML5InputRange rotationYRange;
	private HTML5InputRange rotationZRange;
	private HTML5InputRange rotationBoneRange;
	private HTML5InputRange rotationBoneYRange;
	private HTML5InputRange rotationBoneZRange;
	@Override
	public void createControl(Panel parent) {
HorizontalPanel h1=new HorizontalPanel();
		
		rotationRange = new HTML5InputRange(-180,180,0);
		parent.add(HTML5Builder.createRangeLabel("X-Rotate:", rotationRange));
		parent.add(h1);
		h1.add(rotationRange);
		Button reset=new Button("Reset");
		reset.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				rotationRange.setValue(0);
			}
		});
		h1.add(reset);
		
		HorizontalPanel h2=new HorizontalPanel();
		
		rotationYRange = new HTML5InputRange(-180,180,0);
		parent.add(HTML5Builder.createRangeLabel("Y-Rotate:", rotationYRange));
		parent.add(h2);
		h2.add(rotationYRange);
		Button reset2=new Button("Reset");
		reset2.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				rotationYRange.setValue(0);
			}
		});
		h2.add(reset2);
		
		
		HorizontalPanel h3=new HorizontalPanel();
		rotationZRange = new HTML5InputRange(-180,180,0);
		parent.add(HTML5Builder.createRangeLabel("Z-Rotate:", rotationZRange));
		parent.add(h3);
		h3.add(rotationZRange);
		Button reset3=new Button("Reset");
		reset3.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				rotationZRange.setValue(0);
			}
		});
		h3.add(reset3);
		
		HorizontalPanel h4=new HorizontalPanel();
		positionXRange = new HTML5InputRange(-50,50,0);
		parent.add(HTML5Builder.createRangeLabel("X-Position:", positionXRange));
		parent.add(h4);
		h4.add(positionXRange);
		Button reset4=new Button("Reset");
		reset4.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				positionXRange.setValue(0);
			}
		});
		h4.add(reset4);
		
		HorizontalPanel h5=new HorizontalPanel();
		positionYRange = new HTML5InputRange(-50,50,0);
		parent.add(HTML5Builder.createRangeLabel("Y-Position:", positionYRange));
		parent.add(h5);
		h5.add(positionYRange);
		Button reset5=new Button("Reset");
		reset5.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				positionYRange.setValue(0);
			}
		});
		h5.add(reset5);
		
		HorizontalPanel h6=new HorizontalPanel();
		positionZRange = new HTML5InputRange(-50,50,0);
		parent.add(HTML5Builder.createRangeLabel("Z-Position:", positionZRange));
		parent.add(h6);
		h6.add(positionZRange);
		Button reset6=new Button("Reset");
		reset6.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				positionZRange.setValue(0);
			}
		});
		h6.add(reset6);
		
		transparentCheck = new CheckBox();
		parent.add(transparentCheck);
		transparentCheck.setText("transparent");
		transparentCheck.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				updateMaterial();
			}
		});
		
		transparentCheck.setValue(true);
		
		basicMaterialCheck = new CheckBox();
		parent.add(basicMaterialCheck);
		basicMaterialCheck.setText("BasicMaterial");
		basicMaterialCheck.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				updateMaterial();
			}
		});
		
		HorizontalPanel frames=new HorizontalPanel();
		frameRange = new HTML5InputRange(0,1,0);
		parent.add(HTML5Builder.createRangeLabel("Frame:", frameRange));
		parent.add(frames);
		frames.add(frameRange);
		
		frameRange.addListener(new HTML5InputRangeListener() {
			
			@Override
			public void changed(int newValue) {
				doPose(frameRange.getValue());
			}
		});
		
	
		
		parent.add(new Label("Bone"));
		
		boneNamesBox = new ListBox();
		for(int i=0;i<ikTestNames.length;i++){
			boneNamesBox.addItem(ikTestNames[i]);
		}
		boneNamesBox.setSelectedIndex(0);
		boneNamesBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateBoneRanges();
			}
		});
		parent.add(boneNamesBox);
		
		
		HorizontalPanel h1b=new HorizontalPanel();
		rotationBoneRange = new HTML5InputRange(-180,180,0);
		parent.add(HTML5Builder.createRangeLabel("X-Rotate:", rotationBoneRange));
		parent.add(h1b);
		h1b.add(rotationBoneRange);
		Button resetB1=new Button("Reset");
		resetB1.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				rotationBoneRange.setValue(0);
				rangeToBone();
			}
		});
		h1b.add(resetB1);
		rotationBoneRange.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				rangeToBone();
			}
		});
		
		HorizontalPanel h2b=new HorizontalPanel();
		
		rotationBoneYRange = new HTML5InputRange(-180,180,0);
		parent.add(HTML5Builder.createRangeLabel("Y-Rotate:", rotationBoneYRange));
		parent.add(h2b);
		h2b.add(rotationBoneYRange);
		Button reset2b=new Button("Reset");
		reset2b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				rotationBoneYRange.setValue(0);
				rangeToBone();
			}
		});
		h2b.add(reset2b);
		rotationBoneYRange.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				rangeToBone();
			}
		});
		
		
		HorizontalPanel h3b=new HorizontalPanel();
		rotationBoneZRange = new HTML5InputRange(-180,180,0);
		parent.add(HTML5Builder.createRangeLabel("Z-Rotate:", rotationBoneZRange));
		parent.add(h3b);
		h3b.add(rotationBoneZRange);
		Button reset3b=new Button("Reset");
		reset3b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				rotationBoneZRange.setValue(0);
				rangeToBone();
			}
		});
		h3b.add(reset3b);
		rotationBoneZRange.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				rangeToBone();
			}
		});
		
		
		updateMaterial();
		positionYRange.setValue(-14);//for test
		
		showControl();
		
	}
	private void rangeToBone(){
		String name=boneNamesBox.getItemText(boneNamesBox.getSelectedIndex());
		int index=ab.getBoneIndex(name);
		//Matrix4 mx=ab.getBoneMatrix(name);
		Vector3 angles=THREE.Vector3(Math.toRadians(rotationBoneRange.getValue()),
				Math.toRadians(rotationBoneYRange.getValue())
				, Math.toRadians(rotationBoneZRange.getValue()));
		//log("set-angle:"+ThreeLog.get(GWTThreeUtils.radiantToDegree(angles)));
		//mx.setRotationFromEuler(angles, "XYZ");
		
		
		Vector3 pos=GWTThreeUtils.toPositionVec(ab.getBoneMatrix(index));
		//log("pos:"+ThreeLog.get(pos));
		Matrix4 posMx=GWTThreeUtils.translateToMatrix4(pos);
		
		Matrix4 rotMx=GWTThreeUtils.rotationToMatrix4(angles);
		rotMx.multiply(posMx,rotMx);
		
		//log("bone-pos:"+ThreeLog.get(bones.get(index).getPos()));
		
		Vector3 changed=GWTThreeUtils.toDegreeAngle(rotMx);
		//log("seted-angle:"+ThreeLog.get(changed));
		ab.setBoneMatrix(index, rotMx);
		doPoseByMatrix(ab);
	}
	
	private void updateBoneRanges(){
		
		String name=boneNamesBox.getItemText(boneNamesBox.getSelectedIndex());
		int boneIndex=ab.getBoneIndex(name);
		Quaternion q=GWTThreeUtils.jsArrayToQuaternion(bones.get(boneIndex).getRotq());
		//log("bone:"+ThreeLog.get(GWTThreeUtils.radiantToDegree(GWTThreeUtils.rotationToVector3(q))));
				
		Vector3 angles=GWTThreeUtils.toDegreeAngle(ab.getBoneMatrix(name));
		log("update-bone:"+ThreeLog.get(angles));
		int x=(int) angles.getX();
		if(x==180|| x==-180){
			x=0;
		}
		rotationBoneRange.setValue(x);
		int y=(int) angles.getY();
		if(y==180|| y==-180){
			y=0;
		}
		rotationBoneYRange.setValue(y);
	
		int z=(int) angles.getZ();
		if(z==180|| z==-180){
			z=0;
		}
		rotationBoneZRange.setValue(z);
	}
	
	private Material bodyMaterial;
	protected void updateMaterial() {
		
		Material material=null;
		boolean transparent=transparentCheck.getValue();
		double opacity=1;
		if(transparent){
			opacity=0.75;
		}
		if(basicMaterialCheck.getValue()){
			material=THREE.MeshBasicMaterial().map(ImageUtils.loadTexture("men3smart_texture.png")).transparent(transparent).opacity(opacity).build();
			
		}else{
			material=THREE.MeshLambertMaterial().map(ImageUtils.loadTexture("men3smart_texture.png")).transparent(transparent).opacity(opacity).build();
		}
		bodyMaterial=material;
		
		if(bodyMesh!=null){
		bodyMesh.setMaterial(material);
		}
	}

	private void loadBVH(String path){
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(path));

			try {
				builder.sendRequest(null, new RequestCallback() {
					
					@Override
					public void onResponseReceived(Request request, Response response) {
						String bvhText=response.getText();
						parseBVH(bvhText);

					}
					
					
					

@Override
public void onError(Request request, Throwable exception) {
				Window.alert("load faild:");
}
				});
			} catch (RequestException e) {
				log(e.getMessage());
				e.printStackTrace();
			}
	}


	private Geometry baseGeometry;
	protected void parseBVH(String bvhText) {
		final BVHParser parser=new BVHParser();
		
		parser.parseAsync(bvhText, new ParserListener() {

			@Override
			public void onFaild(String message) {
				log(message);
			}
			@Override
			public void onSuccess(BVH bv) {
				bvh=bv;
				
				AnimationBoneConverter converter=new AnimationBoneConverter();
				bones = converter.convertJsonBone(bvh);
				
				AnimationDataConverter dataConverter=new AnimationDataConverter();
				dataConverter.setSkipFirst(false);
				animationData = dataConverter.convertJsonAnimation(bones,bvh);
				frameRange.setMax(animationData.getHierarchy().get(0).getKeys().length());
				
				JSONLoader loader=THREE.JSONLoader();
				loader.load("men3tmp.js", new  LoadHandler() {
					@Override
					public void loaded(Geometry geometry) {
						baseGeometry=geometry;
						doPose(0);
					}
				});
			}
		});	
	}
	
	public static class MatrixAndVector3{
		public MatrixAndVector3(){}
		private Vector3 position;
		public Vector3 getPosition() {
			return position;
		}
		public void setPosition(Vector3 position) {
			this.position = position;
		}
		private Vector3 absolutePosition;
		
		public Vector3 getAbsolutePosition() {
			return absolutePosition;
		}
		public void setAbsolutePosition(Vector3 absolutePosition) {
			this.absolutePosition = absolutePosition;
		}
		public Matrix4 getMatrix() {
			return matrix;
		}
		public void setMatrix(Matrix4 matrix) {
			this.matrix = matrix;
		}
		private Matrix4 matrix;
	}
	private List<MatrixAndVector3> boneMatrix;
	
	
	/*
	private Vector3 calculateBonedPos(Vector3 pos,AnimationBone bone,int animationIndex){
		
	}
	*/
	
	public static List<MatrixAndVector3> boneToBoneMatrix(JsArray<AnimationBone> bones,AnimationData animationData,int index){
		
		List<MatrixAndVector3> boneMatrix=new ArrayList<MatrixAndVector3>();
		//analyze bone matrix
		for(int i=0;i<bones.length();i++){
			AnimationBone bone=bones.get(i);
			AnimationHierarchyItem item=animationData.getHierarchy().get(i);
			AnimationKey motion=item.getKeys().get(index);
			
			//log(bone.getName());
			
			Matrix4 mx=THREE.Matrix4();
			Vector3 motionPos=AnimationBone.jsArrayToVector3(motion.getPos());
			//seems same as bone
		//	LogUtils.log(motionPos);
			mx.setTranslation(motionPos.getX(), motionPos.getY(), motionPos.getZ());
			Matrix4 mx2=THREE.Matrix4();
			mx2.setRotationFromQuaternion(motion.getRot());
			mx.multiplySelf(mx2);
			
			/*
			Vector3 tmpRot=THREE.Vector3();
			tmpRot.setRotationFromMatrix(mx);
			Vector3 tmpPos=THREE.Vector3();
			tmpPos.setPositionFromMatrix(mx);
			*/
			//LogUtils.log(tmpPos.getX()+","+tmpPos.getY()+","+tmpPos.getZ());
			//LogUtils.log(Math.toDegrees(tmpRot.))
			
			MatrixAndVector3 mv=new MatrixAndVector3();
			Vector3 bpos=AnimationBone.jsArrayToVector3(bone.getPos());
			
			mv.setPosition(bpos);//not effected self matrix
			mv.setMatrix(mx);
			
			if(bone.getParent()!=-1){
				MatrixAndVector3 parentMv=boneMatrix.get(bone.getParent());
				Vector3 apos=bpos.clone();
				apos.addSelf(parentMv.getAbsolutePosition());
				mv.setAbsolutePosition(apos);
			}else{
				//root
				mv.setAbsolutePosition(bpos.clone());
			}
			boneMatrix.add(mv);
		}
		return boneMatrix;
	}
	
	private List<List<Integer>> bonePath;
	
	public static  List<List<Integer>> boneToPath(JsArray<AnimationBone> bones){
		List<List<Integer>> data=new ArrayList<List<Integer>>();
		for(int i=0;i<bones.length();i++){
			List<Integer> path=new ArrayList<Integer>();
			AnimationBone bone=bones.get(i);
			path.add(i);
			data.add(path);
			while(bone.getParent()!=-1){
				//path.add(bone.getParent());
				path.add(0,bone.getParent());
				bone=bones.get(bone.getParent());
			}
		}
		return data;
	}
	private JsArray<Vector4> bodyIndices;
	private JsArray<Vector4> bodyWeight;
	Mesh bodyMesh;
	Object3D root;
	Object3D bone3D;
	private CheckBox transparentCheck;
	private CheckBox basicMaterialCheck;
	private void doPose(int index){
		
		
		if(bodyMesh==null){//initial
			bodyIndices = (JsArray<Vector4>) JsArray.createArray();
			bodyWeight = (JsArray<Vector4>) JsArray.createArray();
			WeightBuilder.autoWeight(baseGeometry, bones, 2, bodyIndices, bodyWeight);
			
			
			
			}else{
				root.remove(bodyMesh);
			}
		
		
		
		boneMatrix=boneToBoneMatrix(bones,animationData,index);
		doPose(boneMatrix);
		
		
		
		//test amtrix
		/*
		for(int i=0;i<ab.getLength();i++){
			Vector3 pos=ab.getPosition(i);
			Mesh tmp=THREE.Mesh(THREE.CubeGeometry(1, 1, 1), THREE.MeshBasicMaterial().build());
			tmp.setPosition(pos);
			bone3D.add(tmp);
		}*/
	}
	
	
	
AnimationBonesData ab;
List<Matrix4> baseMatrixs;		




private void applyMatrix(List<Matrix4> matrix,List<NameAndVector3> samples){
for(NameAndVector3 nv:samples){
	int boneIndex=ab.getBoneIndex(nv.getName());
	Matrix4 translates=GWTThreeUtils.translateToMatrix4(GWTThreeUtils.toPositionVec(ab.getBoneMatrix(boneIndex)));
	Matrix4 newMatrix=GWTThreeUtils.rotationToMatrix4(nv.getVector3());
	newMatrix.multiply(translates,newMatrix);
	log("apply-matrix");
	matrix.set(boneIndex, newMatrix);
	}
}


List<List<Matrix4>> nearMatrix;
private void doPoseIkk(int index){
		
		
		if(bodyMesh==null){//initial
			bodyIndices = (JsArray<Vector4>) JsArray.createArray();
			bodyWeight = (JsArray<Vector4>) JsArray.createArray();
			WeightBuilder.autoWeight(baseGeometry, bones, 2, bodyIndices, bodyWeight);
			
			
			
			}else{
				root.remove(bodyMesh);
			}
		
		
		
	//	boneMatrix=boneToBoneMatrix(bones,animationData,index);
		
		
		
		
		
		if(ab==null){
		baseMatrixs=AnimationBonesData.boneToMatrix(bones, animationData, index);	
		ab=new AnimationBonesData(bones,AnimationBonesData.cloneMatrix(baseMatrixs) );
		}
		
		//TODO make automatic
		//this is find base matrix ,because sometime cdd-ik faild from some position
		nearMatrix=new ArrayList<List<Matrix4>>();
		nearMatrix.add(AnimationBonesData.cloneMatrix(baseMatrixs));
		
		
		List<NameAndVector3> sample=new ArrayList<NameAndVector3>();
		sample.add(new NameAndVector3("RightLeg", GWTThreeUtils.degreeToRagiant(THREE.Vector3(90, 0, 0)), 0));
		sample.add(new NameAndVector3("RightUpLeg", GWTThreeUtils.degreeToRagiant(THREE.Vector3(-90, 0, 0)), 0));
		List<Matrix4> bm=AnimationBonesData.cloneMatrix(baseMatrixs);
		applyMatrix(bm, sample);
		nearMatrix.add(bm);
		
		
		List<NameAndVector3> sample1=new ArrayList<NameAndVector3>();
		sample1.add(new NameAndVector3("RightLeg", GWTThreeUtils.degreeToRagiant(THREE.Vector3(0, 0, 0)), 0));
		sample1.add(new NameAndVector3("RightUpLeg", GWTThreeUtils.degreeToRagiant(THREE.Vector3(0, 0, 45)), 0));
		List<Matrix4> bm1=AnimationBonesData.cloneMatrix(baseMatrixs);
		applyMatrix(bm1, sample);
		
		
		
		
		ab.setBonesMatrixs(findStartMatrix("RightFoot",targetPos));//
		//ab.setBonesMatrixs(null);
		
		
		//doCDDIk();
		Vector3 tmp1=null,tmp2=null;
		ikIndex=0;
		for(int i=0;i<11;i++){
		String targetName=ikTestNames[ikIndex];
		int boneIndex=ab.getBoneIndex(targetName);
		Vector3 lastJointPos=ab.getPosition("RightFoot");
		
		
		
		//Vector3 jointPos=ab.getParentPosition(targetName);
		Vector3 jointPos=ab.getPosition(targetName);
		
		
		Matrix4 jointRot=ab.getBoneMatrix(targetName);
		
		Matrix4 newMatrix=cddIk.doStep(lastJointPos, jointPos, jointRot, targetPos);
		
		//limit angles
		Vector3 angles=GWTThreeUtils.rotationToVector3(newMatrix);
		Matrix4 translates=GWTThreeUtils.translateToMatrix4(GWTThreeUtils.toPositionVec(newMatrix));
		if(targetName.equals("RightLeg")){
			//z only accept
			double zMin=Math.toRadians(0);
			double zMax=Math.toRadians(20);
			if(angles.getZ()<zMin){
				angles.setZ(zMin);
			}
			if(angles.getZ()>zMax){
				angles.setZ(zMax);
			}
			angles.setY(0);
			
			double xMin=Math.toRadians(0);
			if(angles.getX()<xMin){
				angles.setX(xMin);
			}
			
		}
		else if(targetName.equals("RightUpLeg")){
			//z only accept
			double zMin=Math.toRadians(-80);
			double zMax=Math.toRadians(40);
			if(angles.getZ()<zMin){
				angles.setZ(zMin);
			}
			if(angles.getZ()>zMax){
				angles.setZ(zMax);
			}
			
			double yMin=Math.toRadians(-35);
			double yMax=Math.toRadians(35);
			
			if(angles.getY()<yMin){
				angles.setY(yMin);
			}
			if(angles.getY()>yMax){
				angles.setY(yMax);
			}
			
			double xMin=Math.toRadians(-85);
			double xMax=Math.toRadians(90);
			
			if(angles.getX()<xMin){
				angles.setX(xMin);
			}
			if(angles.getX()>xMax){
				angles.setX(xMax);
			}
			
		}
		newMatrix=GWTThreeUtils.rotationToMatrix4(angles);
		newMatrix.multiply(translates,newMatrix);
		
		ab.setBoneMatrix(boneIndex, newMatrix);
		
		//log(targetName+":"+ThreeLog.getAngle(jointRot)+",new"+ThreeLog.getAngle(newMatrix));
		//log("parentPos,"+ThreeLog.get(jointPos)+",lastPos,"+ThreeLog.get(lastJointPos));
		ikIndex++;
		if(ikIndex>=ikTestNames.length){
			ikIndex=0;
		}
		tmp1=lastJointPos;
		tmp2=jointPos;
		}
		
		doPoseByMatrix(ab);
		updateBoneRanges();
		//bone3D.add(GWTUtils.createSimpleBox(tmp1, 1, 0x0000ff));
		//bone3D.add(GWTUtils.createSimpleBox(tmp2, 1, 0x000088));
		
		//test amtrix
		/*
		for(int i=0;i<ab.getLength();i++){
			Vector3 pos=ab.getPosition(i);
			Mesh tmp=THREE.Mesh(THREE.CubeGeometry(1, 1, 1), THREE.MeshBasicMaterial().build());
			tmp.setPosition(pos);
			bone3D.add(tmp);
		}*/
	}
private List<Matrix4> findStartMatrix(String boneName,Vector3 targetPos) {
	List<Matrix4> retMatrix=nearMatrix.get(0);
	ab.setBonesMatrixs(retMatrix);
	Vector3 tpos=ab.getPosition(boneName);
	double minlength=targetPos.clone().subSelf(tpos).length();
	for(int i=1;i<nearMatrix.size();i++){
		List<Matrix4> mxs=nearMatrix.get(i);
		ab.setBonesMatrixs(mxs);//TODO change
		Vector3 tmpPos=ab.getPosition(boneName);
		double tmpLength=targetPos.clone().subSelf(tmpPos).length();
		if(tmpLength<minlength){
			minlength=tmpLength;
			retMatrix=mxs;
		}
	}
	return retMatrix;
}

private void doCDDIk(){
	
		String targetName=ikTestNames[ikIndex];
		int boneIndex=ab.getBoneIndex(targetName);
		Vector3 lastJointPos=ab.getPosition("RightFoot");
		Vector3 jointPos=ab.getParentPosition(targetName);
		Matrix4 jointRot=ab.getBoneMatrix(targetName);
		
		Matrix4 newMatrix=cddIk.doStep(lastJointPos, jointPos, jointRot, targetPos);
		ab.setBoneMatrix(boneIndex, newMatrix);
		
		//log(targetName+":"+ThreeLog.getAngle(jointRot)+",new"+ThreeLog.getAngle(newMatrix));
		//log("parentPos,"+ThreeLog.get(jointPos)+",lastPos,"+ThreeLog.get(lastJointPos));
		ikIndex++;
		if(ikIndex>=ikTestNames.length){
			ikIndex=0;
		}
		
		doPoseByMatrix(ab);
}
CDDIK cddIk=new CDDIK();
	int ikIndex=0;
	private String[] ikTestNames={"RightLeg","RightUpLeg"};
	
	Vector3 targetPos=THREE.Vector3(-10, -3, 0);
	private ListBox boneNamesBox;
	
private void doPoseByMatrix(AnimationBonesData animationBonesData){
		
		
	List<Matrix4> boneMatrix=animationBonesData.getBonesMatrixs();
		
		bonePath=boneToPath(bones);
		if(bone3D!=null){
			root.remove(bone3D);
		}
		bone3D=THREE.Object3D();
		root.add(bone3D);
		
		//test ikk
		Mesh cddIk0=THREE.Mesh(THREE.CubeGeometry(.5, .5, .5),THREE.MeshLambertMaterial().color(0x00ff00).build());
		cddIk0.setPosition(targetPos);
		bone3D.add(cddIk0);
		
		
		
		List<Matrix4> moveMatrix=new ArrayList<Matrix4>(); 
		List<Vector3> bonePositions=new ArrayList<Vector3>();
		for(int i=0;i<bones.length();i++){
			Matrix4 mv=boneMatrix.get(i);
			Mesh mesh=THREE.Mesh(THREE.CubeGeometry(.4, .4, .4),THREE.MeshLambertMaterial().color(0xff0000).build());
			bone3D.add(mesh);
			
			Vector3 pos=THREE.Vector3();
			pos.setPositionFromMatrix(boneMatrix.get(i));
			
			Vector3 rot=GWTThreeUtils.rotationToVector3(GWTThreeUtils.jsArrayToQuaternion(bones.get(i).getRotq()));
			
			List<Integer> path=bonePath.get(i);
			String boneName=bones.get(i).getName();
			//log(boneName);
			
			
			
			Matrix4 matrix=THREE.Matrix4();
			for(int j=0;j<path.size()-1;j++){//last is boneself
			//	log(""+path.get(j));
				Matrix4 mx=boneMatrix.get(path.get(j));
				matrix.multiply(matrix, mx);
			}
			matrix.multiplyVector3(pos);
			matrix.multiply(matrix, boneMatrix.get(path.get(path.size()-1)));//last one
			moveMatrix.add(matrix);
			
			
			
			if(bones.get(i).getParent()!=-1){
			Vector3 ppos=bonePositions.get(bones.get(i).getParent());	
			//pos.addSelf(ppos);
			
			Mesh line=GWTGeometryUtils.createLineMesh(pos, ppos, 0xffffff);
			bone3D.add(line);
			
			//test cdd-ik for choose control
			if(boneName.equals("RightFoot")){
				Vector3 ikpos=pos.clone().subSelf(ppos).multiplyScalar(1.5).addSelf(ppos);
				Mesh cddIk=THREE.Mesh(THREE.CubeGeometry(.5, .5, .5),THREE.MeshLambertMaterial().color(0x00ff00).build());
				cddIk.setPosition(ikpos);
				//bone3D.add(cddIk);
				Mesh ikline=GWTGeometryUtils.createLineMesh(pos, ikpos, 0xffffff);
				//bone3D.add(ikline);
			}
			}
			mesh.setRotation(rot);
			mesh.setPosition(pos);
			bonePositions.add(pos);
		}
		
		//Geometry geo=GeometryUtils.clone(baseGeometry);
		

		
		
		//Geometry geo=bodyMesh.getGeometry();
		Geometry geo=GeometryUtils.clone(baseGeometry);
		
		
		for(int i=0;i<baseGeometry.vertices().length();i++){
			Vertex baseVertex=baseGeometry.vertices().get(i);
			Vertex targetVertex=geo.vertices().get(i);
			
			int boneIndex1=(int) bodyIndices.get(i).getX();
			int boneIndex2=(int) bodyIndices.get(i).getY();
			
			Vector3 bonePos=animationBonesData.getBasePosition(boneIndex1);
			Vector3 relatePos=bonePos.clone();
			relatePos.sub(baseVertex.getPosition(),bonePos);
			double length=relatePos.length();
			
			String name=animationBonesData.getBoneName(boneIndex1);
			Matrix4 tmpMatrix=GWTThreeUtils.rotationToMatrix4(GWTThreeUtils.degreeToRagiant(THREE.Vector3(0, 0, 20)));
			if(name.equals("RightLeg")){
				//TODO rotata parent base
				//tmpMatrix.multiplyVector3(relatePos);
			}
			
			moveMatrix.get(boneIndex1).multiplyVector3(relatePos);
			//relatePos.addSelf(bonePos);
			if(boneIndex2!=boneIndex1){
				Vector3 bonePos2=animationBonesData.getBasePosition(boneIndex2);
				Vector3 relatePos2=bonePos2.clone();
				relatePos2.sub(baseVertex.getPosition(),bonePos2);
				double length2=relatePos2.length();
				moveMatrix.get(boneIndex2).multiplyVector3(relatePos2);
				
				
				if(name.equals("RightLeg")){
					//Matrix4 tmpMatrix2=GWTThreeUtils.rotationToMatrix4(GWTThreeUtils.degreeToRagiant(THREE.Vector3(0, 0, -20)));
					//tmpMatrix2.multiplyVector3(relatePos);
				}
				
				//scalar weight
				
				relatePos.multiplyScalar(bodyWeight.get(i).getX());
			
				relatePos2.multiplyScalar(bodyWeight.get(i).getY());
				relatePos.addSelf(relatePos2);
				
				
				//keep distance1 faild
				
				/*
				if(length<1){	//length2
					
					Vector3 abpos=THREE.Vector3();
					abpos.sub(relatePos, bonePositions.get(boneIndex1));
					double scar=abpos.length()/length;
					abpos.multiplyScalar(scar);
					abpos.addSelf(bonePositions.get(boneIndex1));
					relatePos.set(abpos.getX(), abpos.getY(), abpos.getZ());
				}*/
				
				if(length2<1){
				Vector3 abpos=THREE.Vector3();
				abpos.sub(relatePos, bonePositions.get(boneIndex2));
				double scar=abpos.length()/length2;
				abpos.multiplyScalar(scar);
				abpos.addSelf(bonePositions.get(boneIndex2));
				relatePos.set(abpos.getX(), abpos.getY(), abpos.getZ());
				
				}
				/*
				Vector3 diff=THREE.Vector3();
				diff.sub(relatePos2, relatePos);
				diff.multiplyScalar(bodyWeight.get(i).getY());
				relatePos.addSelf(diff);
				*/
			}else{
				if(name.equals("RightLeg")){
				//	Matrix4 tmpMatrix2=GWTThreeUtils.rotationToMatrix4(GWTThreeUtils.degreeToRagiant(THREE.Vector3(0, 0, -20)));
				//	tmpMatrix2.multiplyVector3(relatePos);
				}
			}
			
			
			targetVertex.getPosition().set(relatePos.getX(), relatePos.getY(), relatePos.getZ());
		}
		
		geo.computeFaceNormals();
		geo.computeVertexNormals();
		
		//Material material=THREE.MeshLambertMaterial().map(ImageUtils.loadTexture("men3smart_texture.png")).build();
		
		if(bodyMesh==null){//initial
			bodyIndices = (JsArray<Vector4>) JsArray.createArray();
			bodyWeight = (JsArray<Vector4>) JsArray.createArray();
			WeightBuilder.autoWeight(baseGeometry, bones, 2, bodyIndices, bodyWeight);	
			}else{
				root.remove(bodyMesh);
			}
		
		bodyMesh=THREE.Mesh(geo, bodyMaterial);
		root.add(bodyMesh);
		
		
		
		/*
		geo.setDynamic(true);
		geo.setDirtyVertices(true);
		geo.computeBoundingSphere();
		*/
		
		//
		//bodyMesh.setGeometry(geo);
		
		//bodyMesh.gwtBoundingSphere();
		//geo.computeTangents();
		
		
		/*
		geo.setDynamic(true);
		geo.setDirtyVertices(true);
		geo.computeFaceNormals();
		geo.computeVertexNormals();
		geo.computeTangents();
		*/
		}	
	private void doPose(List<MatrixAndVector3> boneMatrix){
		
		
		
		bonePath=boneToPath(bones);
		if(bone3D!=null){
			root.remove(bone3D);
		}
		bone3D=THREE.Object3D();
		root.add(bone3D);
		
		//test ikk
		Mesh cddIk0=THREE.Mesh(THREE.CubeGeometry(.5, .5, .5),THREE.MeshLambertMaterial().color(0x00ff00).build());
		cddIk0.setPosition(targetPos);
		bone3D.add(cddIk0);
		
		
		List<Matrix4> moveMatrix=new ArrayList<Matrix4>(); 
		List<Vector3> bonePositions=new ArrayList<Vector3>();
		for(int i=0;i<bones.length();i++){
			MatrixAndVector3 mv=boneMatrix.get(i);
			Mesh mesh=THREE.Mesh(THREE.CubeGeometry(.2, .2, .2),THREE.MeshLambertMaterial().color(0xff0000).build());
			bone3D.add(mesh);
			
			Vector3 pos=mv.getPosition().clone();
			List<Integer> path=bonePath.get(i);
			String boneName=bones.get(i).getName();
			//log(boneName);
			
			Matrix4 tmpmx=boneMatrix.get(path.get(path.size()-1)).getMatrix();
			Vector3 tmpp=THREE.Vector3();
			tmpp.setPositionFromMatrix(tmpmx);
			//log(pos.getX()+","+pos.getY()+","+pos.getZ()+":"+tmpp.getX()+","+tmpp.getY()+","+tmpp.getZ());
			
			Matrix4 matrix=THREE.Matrix4();
			for(int j=0;j<path.size()-1;j++){//last is boneself
			//	log(""+path.get(j));
				Matrix4 mx=boneMatrix.get(path.get(j)).getMatrix();
				matrix.multiply(matrix, mx);
			}
			matrix.multiplyVector3(pos);
			matrix.multiply(matrix, boneMatrix.get(path.get(path.size()-1)).getMatrix());//last one
			moveMatrix.add(matrix);
			
			
			
			if(bones.get(i).getParent()!=-1){
			Vector3 ppos=bonePositions.get(bones.get(i).getParent());	
			//pos.addSelf(ppos);
			
			Mesh line=GWTGeometryUtils.createLineMesh(pos, ppos, 0xffffff);
			bone3D.add(line);
			
			//test cdd-ik
			if(boneName.equals("RightFoot")){
				Vector3 ikpos=pos.clone().subSelf(ppos).multiplyScalar(1.5).addSelf(ppos);
				Mesh cddIk=THREE.Mesh(THREE.CubeGeometry(.5, .5, .5),THREE.MeshLambertMaterial().color(0x00ff00).build());
				cddIk.setPosition(ikpos);
				bone3D.add(cddIk);
				Mesh ikline=GWTGeometryUtils.createLineMesh(pos, ikpos, 0xffffff);
				bone3D.add(ikline);
			}
			}else{
				//root action
				Matrix4 mx=boneMatrix.get(0).getMatrix();
				mx.multiplyVector3(pos);
			}
			mesh.setPosition(pos);
			bonePositions.add(pos);
		}
		
		//Geometry geo=GeometryUtils.clone(baseGeometry);
		

		
		
		//Geometry geo=bodyMesh.getGeometry();
		Geometry geo=GeometryUtils.clone(baseGeometry);
		
		
		for(int i=0;i<baseGeometry.vertices().length();i++){
			Vertex baseVertex=baseGeometry.vertices().get(i);
			Vertex targetVertex=geo.vertices().get(i);
			
			int boneIndex1=(int) bodyIndices.get(i).getX();
			int boneIndex2=(int) bodyIndices.get(i).getY();
			
			Vector3 bonePos=boneMatrix.get(boneIndex1).getAbsolutePosition();
			Vector3 relatePos=bonePos.clone();
			relatePos.sub(baseVertex.getPosition(),bonePos);
			double length=relatePos.length();
			
			moveMatrix.get(boneIndex1).multiplyVector3(relatePos);
			//relatePos.addSelf(bonePos);
			if(boneIndex2!=boneIndex1){
				Vector3 bonePos2=boneMatrix.get(boneIndex2).getAbsolutePosition();
				Vector3 relatePos2=bonePos2.clone();
				relatePos2.sub(baseVertex.getPosition(),bonePos2);
				double length2=relatePos2.length();
				moveMatrix.get(boneIndex2).multiplyVector3(relatePos2);
				
				
				//scalar weight
				
				relatePos.multiplyScalar(bodyWeight.get(i).getX());
			
				relatePos2.multiplyScalar(bodyWeight.get(i).getY());
				relatePos.addSelf(relatePos2);
				
				
				//keep distance1 faild
				
				/*
				if(length<1){	//length2
					
					Vector3 abpos=THREE.Vector3();
					abpos.sub(relatePos, bonePositions.get(boneIndex1));
					double scar=abpos.length()/length;
					abpos.multiplyScalar(scar);
					abpos.addSelf(bonePositions.get(boneIndex1));
					relatePos.set(abpos.getX(), abpos.getY(), abpos.getZ());
				}*/
				
				if(length2<1){
				Vector3 abpos=THREE.Vector3();
				abpos.sub(relatePos, bonePositions.get(boneIndex2));
				double scar=abpos.length()/length2;
				abpos.multiplyScalar(scar);
				abpos.addSelf(bonePositions.get(boneIndex2));
				relatePos.set(abpos.getX(), abpos.getY(), abpos.getZ());
				
				}
				/*
				Vector3 diff=THREE.Vector3();
				diff.sub(relatePos2, relatePos);
				diff.multiplyScalar(bodyWeight.get(i).getY());
				relatePos.addSelf(diff);
				*/
			}
			
			
			targetVertex.getPosition().set(relatePos.getX(), relatePos.getY(), relatePos.getZ());
		}
		
		geo.computeFaceNormals();
		geo.computeVertexNormals();
		
		//Material material=THREE.MeshLambertMaterial().map(ImageUtils.loadTexture("men3smart_texture.png")).build();
		
		bodyMesh=THREE.Mesh(geo, bodyMaterial);
		root.add(bodyMesh);
		
		
		
		/*
		geo.setDynamic(true);
		geo.setDirtyVertices(true);
		geo.computeBoundingSphere();
		*/
		
		//
		//bodyMesh.setGeometry(geo);
		
		//bodyMesh.gwtBoundingSphere();
		//geo.computeTangents();
		
		
		/*
		geo.setDynamic(true);
		geo.setDirtyVertices(true);
		geo.computeFaceNormals();
		geo.computeVertexNormals();
		geo.computeTangents();
		*/
		}	
}
