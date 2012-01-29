package com.akjava.gwt.poseeditor.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.akjava.bvh.client.BVH;
import com.akjava.bvh.client.BVHMotion;
import com.akjava.bvh.client.BVHNode;
import com.akjava.bvh.client.BVHParser;
import com.akjava.bvh.client.BVHParser.ParserListener;
import com.akjava.bvh.client.BVHWriter;
import com.akjava.bvh.client.threejs.AnimationBoneConverter;
import com.akjava.bvh.client.threejs.AnimationDataConverter;
import com.akjava.bvh.client.threejs.BVHConverter;
import com.akjava.gwt.html5.client.HTML5InputRange;
import com.akjava.gwt.html5.client.extra.HTML5Builder;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileHandler;
import com.akjava.gwt.html5.client.file.FileReader;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.three.client.THREE;
import com.akjava.gwt.three.client.core.Geometry;
import com.akjava.gwt.three.client.core.Intersect;
import com.akjava.gwt.three.client.core.Matrix4;
import com.akjava.gwt.three.client.core.Object3D;
import com.akjava.gwt.three.client.core.Projector;
import com.akjava.gwt.three.client.core.Vector3;
import com.akjava.gwt.three.client.core.Vector4;
import com.akjava.gwt.three.client.core.Vertex;
import com.akjava.gwt.three.client.extras.GeometryUtils;
import com.akjava.gwt.three.client.extras.ImageUtils;
import com.akjava.gwt.three.client.extras.loaders.JSONLoader;
import com.akjava.gwt.three.client.extras.loaders.JSONLoader.LoadHandler;
import com.akjava.gwt.three.client.gwt.GWTGeometryUtils;
import com.akjava.gwt.three.client.gwt.GWTThreeUtils;
import com.akjava.gwt.three.client.gwt.ThreeLog;
import com.akjava.gwt.three.client.gwt.animation.AngleAndPosition;
import com.akjava.gwt.three.client.gwt.animation.AnimationBone;
import com.akjava.gwt.three.client.gwt.animation.AnimationBonesData;
import com.akjava.gwt.three.client.gwt.animation.AnimationData;
import com.akjava.gwt.three.client.gwt.animation.AnimationHierarchyItem;
import com.akjava.gwt.three.client.gwt.animation.AnimationKey;
import com.akjava.gwt.three.client.gwt.animation.BoneLimit;
import com.akjava.gwt.three.client.gwt.animation.NameAndVector3;
import com.akjava.gwt.three.client.gwt.animation.WeightBuilder;
import com.akjava.gwt.three.client.gwt.animation.ik.CDDIK;
import com.akjava.gwt.three.client.gwt.animation.ik.IKData;
import com.akjava.gwt.three.client.gwt.ui.SimpleTabDemoEntryPoint;
import com.akjava.gwt.three.client.lights.Light;
import com.akjava.gwt.three.client.materials.Material;
import com.akjava.gwt.three.client.objects.Mesh;
import com.akjava.gwt.three.client.renderers.WebGLRenderer;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PoseEditor extends SimpleTabDemoEntryPoint{
	private BVH bvh;
	protected JsArray<AnimationBone> bones;
	private AnimationData animationData;
	public static DateTimeFormat dateFormat=DateTimeFormat.getFormat("yy/MM/dd HH:mm");
	@Override
	protected void beforeUpdate(WebGLRenderer renderer) {
		if(root!=null){
			
			root.setPosition((double)positionXRange.getValue()/10, (double)positionYRange.getValue()/10, (double)positionZRange.getValue()/10);
			
			root.getRotation().set(Math.toRadians(rotationRange.getValue()),Math.toRadians(rotationYRange.getValue()),Math.toRadians(rotationZRange.getValue()));
			}
	}

	@Override
	public void resized(int width, int height) {
		super.resized(width, height);
		leftBottom(bottomPanel);
	}
	
	
	private WebGLRenderer renderer;

	public static final String KEY_INDEX="DATA_INDEX";
	public static final String KEY_DATA="DATA_VALUE";
	public static final String KEY_IMAGE="DATA_IMAGE";
	public static final String KEY_HEAD="DATA_HEAD";
	
	@Override
	protected void initializeOthers(WebGLRenderer renderer) {
		
		preference = new StorageControler();
		
		this.renderer=renderer;
		canvas.setClearColorHex(0x333333);
	
	
		scene.add(THREE.AmbientLight(0xffffff));
		
		Light pointLight = THREE.DirectionalLight(0xffffff,1);
		pointLight.setPosition(0, 10, 300);
		scene.add(pointLight);
		
		Light pointLight2 = THREE.DirectionalLight(0xffffff,1);//for fix back side dark problem
		pointLight2.setPosition(0, 10, -300);
		//scene.add(pointLight2);
		
		root=THREE.Object3D();
		scene.add(root);
		
		Geometry geo=THREE.PlaneGeometry(100, 100,10,10);
		Mesh mesh=THREE.Mesh(geo, THREE.MeshBasicMaterial().color(0xaaaaaa).wireFrame().build());
		mesh.setRotation(Math.toRadians(-90), 0, 0);
		root.add(mesh);
		
		//line removed,because of flicking
		Mesh xline=GWTGeometryUtils.createLineMesh(THREE.Vector3(-50, 0, 0.001), THREE.Vector3(50, 0, 0.001), 0x880000,3);
		//root.add(xline);
		
		Mesh zline=GWTGeometryUtils.createLineMesh(THREE.Vector3(0, 0, -50), THREE.Vector3(0, 0, 50), 0x008800,3);
		//root.add(zline);
		
		
		selectionMesh=THREE.Mesh(THREE.CubeGeometry(2, 2, 2), THREE.MeshBasicMaterial().color(0x00ff00).wireFrame(true).build());
		
		root.add(selectionMesh);
		selectionMesh.setVisible(false);
		
		//line flicked think something
		
		
		
		loadBVH("pose.bvh");
		
		
		IKData ikdata1=new IKData("LowerBack-Neck1");
		//ikdata1.setTargetPos(THREE.Vector3(0, 20, 0));
		ikdata1.setLastBoneName("Head");
		ikdata1.setBones(new String[]{"Neck1","Neck","Spine","LowerBack"});
		//ikdata1.setBones(new String[]{"Neck1","Neck","Spine1","Spine","LowerBack"});
		ikdata1.setIteration(9);
		ikdatas.add(ikdata1);
		
		
		
		IKData ikdata0=new IKData("RightArm-RightForeArm");
		//ikdata0.setTargetPos(THREE.Vector3(-10, 5, 0));
		ikdata0.setLastBoneName("RightHand");
		ikdata0.setBones(new String[]{"RightForeArm","RightArm"});
	//	ikdata0.setBones(new String[]{"RightForeArm","RightArm","RightShoulder"});
		ikdata0.setIteration(7);
		ikdatas.add(ikdata0);
		
		
		//
		IKData ikdata=new IKData("RightUpLeg-RightLeg");
		//ikdata.setTargetPos(THREE.Vector3(0, -10, 0));
		ikdata.setLastBoneName("RightFoot");
		ikdata.setBones(new String[]{"RightLeg","RightUpLeg"});
		ikdata.setIteration(5);
		ikdatas.add(ikdata);
		
		
		IKData ikdata2=new IKData("LeftArm-LeftForeArm");
		//ikdata0.setTargetPos(THREE.Vector3(-10, 5, 0));
		ikdata2.setLastBoneName("LeftHand");
		//ikdata2.setBones(new String[]{"LeftForeArm","LeftArm","LeftShoulder"});
		ikdata2.setBones(new String[]{"LeftForeArm","LeftArm"});
		ikdata2.setIteration(7);
		ikdatas.add(ikdata2);
		
		
		//
		IKData ikdata3=new IKData("LeftUpLeg-LeftLeg");
		//ikdata.setTargetPos(THREE.Vector3(0, -10, 0));
		ikdata3.setLastBoneName("LeftFoot");
		ikdata3.setBones(new String[]{"LeftLeg","LeftUpLeg"});
		ikdata3.setIteration(5);
		ikdatas.add(ikdata3);
		
		//updateIkLabels();
		
		
		//calcurate by bvh 80_*
		/*
		boneLimits.put("RightForeArm",BoneLimit.createBoneLimit(-118, 0, 0, 60, -170, 0));
		boneLimits.put("RightArm",BoneLimit.createBoneLimit(-180, 180, -60, 91, -180, 180));
		boneLimits.put("RightShoulder",BoneLimit.createBoneLimit(0, 0, 0, 0,0, 0));
		
		boneLimits.put("LeftForeArm",BoneLimit.createBoneLimit(-40, 10, -170, 0, 0, 0));
		boneLimits.put("LeftArm",BoneLimit.createBoneLimit(-80, 60, -91, 40, -120, 50));
		boneLimits.put("LeftShoulder",BoneLimit.createBoneLimit(-15, 25, -20, 20,-10, 10));
		
		
		
		boneLimits.put("RightLeg",BoneLimit.createBoneLimit(0, 160, 0, 0, 0, 20));
		boneLimits.put("RightUpLeg",BoneLimit.createBoneLimit(-85, 91, -35, 5, -80, 40));
		
		boneLimits.put("LeftLeg",BoneLimit.createBoneLimit(0, 160, 0, 0, -20, 0));
		boneLimits.put("LeftUpLeg",BoneLimit.createBoneLimit(-85, 91, -5, 35, -40, 80));
		
		
		boneLimits.put("LowerBack",BoneLimit.createBoneLimit(-30, 30, -60, 60, -30, 30));
		boneLimits.put("Spine",BoneLimit.createBoneLimit(-30, 30, -40, 40, -40, 40));
		//boneLimits.put("Spine1",BoneLimit.createBoneLimit(-30, 30, -30, 30, -30, 30));
		boneLimits.put("Neck",BoneLimit.createBoneLimit(-45, 45, -45, 45, -45, 45));
		boneLimits.put("Neck1",BoneLimit.createBoneLimit(-15, 15, -15, 15, -15, 15));
		*/
		
		
		//there are gimbal lock problem angle must be under 90
		/*
		 * to manual change to joint angle,keep under 90 is better.
		 	but gimbal lock problem happend alreay at IK result converted to eular angle
		 */
		/*
		boneLimits.put("RightForeArm",BoneLimit.createBoneLimit(-89, 10, 0, 89, -10, 10));
		boneLimits.put("RightArm",BoneLimit.createBoneLimit(-80, 60, -40, 89, -50,89));
		
		boneLimits.put("LeftForeArm",BoneLimit.createBoneLimit(-89, 10, -89.9, 0, -10, 10));
		boneLimits.put("LeftArm",BoneLimit.createBoneLimit(-80, 60, -89, 40, -89, 50));

		
		boneLimits.put("RightLeg",BoneLimit.createBoneLimit(0, 89, 0, 0, 0, 40));
		boneLimits.put("RightUpLeg",BoneLimit.createBoneLimit(-85, 89, -35, 5, -80, 40));
		
		boneLimits.put("LeftLeg",BoneLimit.createBoneLimit(0, 89, 0, 0, -40, 0));
		boneLimits.put("LeftUpLeg",BoneLimit.createBoneLimit(-85, 89, -5, 35, -40, 80));
		
		
		boneLimits.put("LowerBack",BoneLimit.createBoneLimit(-30, 30, -60, 60, -30, 30));
		boneLimits.put("Spine",BoneLimit.createBoneLimit(-30, 30, -40, 40, -40, 40));
		//boneLimits.put("Spine1",BoneLimit.createBoneLimit(-30, 30, -30, 30, -30, 30));
		boneLimits.put("Neck",BoneLimit.createBoneLimit(-35, 35, -35, 35, -35, 35));
		boneLimits.put("Neck1",BoneLimit.createBoneLimit(-5, 5, -5, 5, -5, 5));
		*/
		
		//manual
		
		boneLimits.put("RightForeArm",BoneLimit.createBoneLimit(-40, 10, 0, 140, -10, 10));
		boneLimits.put("RightArm",BoneLimit.createBoneLimit(-120, 60, -91, 91, -70, 120));
		
		boneLimits.put("LeftForeArm",BoneLimit.createBoneLimit(-40, 10, -140, 0, -10, 10));
		boneLimits.put("LeftArm",BoneLimit.createBoneLimit(-120, 60, -91, 91, -120, 70));

		
		boneLimits.put("RightLeg",BoneLimit.createBoneLimit(0, 160, 0, 0, 0, 20));
		boneLimits.put("RightUpLeg",BoneLimit.createBoneLimit(-120, 60, -35, 5, -80, 40));
		
		
		boneLimits.put("LeftLeg",BoneLimit.createBoneLimit(0, 160, 0, 0, -20, 0));
		boneLimits.put("LeftUpLeg",BoneLimit.createBoneLimit(-120, 60, -5, 35, -40, 80));
		
		
		boneLimits.put("LowerBack",BoneLimit.createBoneLimit(-30, 30, -60, 60, -30, 30));
		boneLimits.put("Spine",BoneLimit.createBoneLimit(-30, 30, -40, 40, -40, 40));
		//boneLimits.put("Spine1",BoneLimit.createBoneLimit(-30, 30, -30, 30, -30, 30));
		boneLimits.put("Neck",BoneLimit.createBoneLimit(-29, 29, -29, 29, -29, 29));
		boneLimits.put("Neck1",BoneLimit.createBoneLimit(-5, 5, -5, 5, -5, 5));
		
		createTabs();
		
		updateDatasPanel();
	}
	
	private void updateDatasPanel(){
		datasPanel.clear();
		int index=preference.getValue(KEY_INDEX, 0);
		for(int i=index;i>=0;i--){
			String b64=preference.getValue(KEY_IMAGE+i,null);
			String json=preference.getValue(KEY_DATA+i,null);
			String head=preference.getValue(KEY_HEAD+i,null);
			if(b64!=null && json!=null){
			DataPanel dp=new DataPanel(i,head,b64,json);
			//dp.setSize("200px", "200px");
			datasPanel.add(dp);
			}
		}
	}
	
	public class DataPanel extends HorizontalPanel{
		private int index;
		private String name;
		private long cdate;
		private String json;
		public DataPanel(final int ind,String head,String base64, String text){
			json=text;
			this.index=ind;
			Image img=new Image();
			img.setUrl(base64);
			
			
			String name_cdate[]=head.split("\t");
			name=name_cdate[0];
			cdate=(long)(Double.parseDouble(name_cdate[1]));
			
			String dlabel=dateFormat.format(new Date(cdate));
			add(new Label(dlabel));
			add(img);
			
			final Label nameLabel=new Label(name);
			nameLabel.setWidth("160px");
			add(nameLabel);
			
			
			
			Button loadBt=new Button("Load");
			add(loadBt);
			loadBt.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					int loadedIndex=isLoaded(ind);
					if(loadedIndex!=-1){
						selectPoseEditorDatas(loadedIndex);
						tabPanel.selectTab(0);//datas
						return;
					}
					PoseEditorData ped=PoseEditorData.readData(json);
					
					
					if(ped!=null){
					ped.setFileId(ind);
					doLoad(ped);
					}else{
						//TODO error catch
						Window.alert("load faild");
					}
				}
			});
			
			Button editBt=new Button("Edit Name");
			add(editBt);
			editBt.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					
					
					JSONValue jsonValue=JSONParser.parseLenient(json);
					JSONObject ped=jsonValue.isObject();
					if(ped!=null){
						String newName=Window.prompt("Change Name",name);
						//ped.setName(newName);
						
						name=newName;
						ped.put("name", new JSONString(name));
						json=ped.toString();
						nameLabel.setText(name);
						//JSONObject data=PoseEditorData.writeData(ped);
						preference.setValue(KEY_DATA+index, json);
						preference.setValue(KEY_HEAD+index, name+"\t"+cdate);
						//rewrite
					}else{
						//TODO error catch
						Window.alert("load faild");
					}
				}
			});
			
			Button removeBt=new Button("Delate");
			add(removeBt);
			removeBt.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					boolean ret=Window.confirm("Delete data:"+name);
					if(ret){
					doRemoveData(index);
					}
				}
			});
			
			Button exportBt=new Button("Export");
			add(exportBt);
			exportBt.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					PoseEditorData ped=PoseEditorData.readData(json);
					doExport(ped);
				}
			});
			
		}
	}
	
	private int  isLoaded(int index){
		for(int i=0;i<poseEditorDatas.size();i++){
			PoseEditorData data=poseEditorDatas.get(i);
			if(data.getFileId()==index){
				return i;
			}
		}
		return -1;
	}
	
	private void doRemoveData(int index){
		preference.setValue(KEY_DATA+index, null);
		preference.setValue(KEY_IMAGE+index, null);
		preference.setValue(KEY_HEAD+index, null);
		updateDatasPanel();
	}
	
	private void createTabs(){
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				int selection=event.getSelectedItem();
				if(selection==0){
					stats.setVisible(true);
					showControl();
					bottomPanel.setVisible(true);
					dialog2.setVisible(true);
				}else{
				stats.setVisible(false);
				bottomPanel.setVisible(false);
				hideControl();
				dialog2.setVisible(false);
				}
				resized(screenWidth,screenHeight);//for some blackout;
			}
		});
		VerticalPanel datasRoot=new VerticalPanel();
		tabPanel.add(datasRoot,"Datas");
		
		
		
		datasPanel = new VerticalPanel();
		
		datasPanel.setStyleName("debug");
		ScrollPanel scroll=new ScrollPanel(datasPanel);
		scroll.setSize("550px", "500px");
		datasRoot.add(scroll);
		
		
		//about
		String html="";
		html+="<br/>"+"[Howto]<br/>Select Nothing:Mouse Drag=Rotatation-XY,Mouse Wheel= Zoom, +ALT Move-XY Camera";
		html+="<br/>"+"Select IK(Green Box):Mouse Drag=Move IK-XY,Mouse Wheel=Move IK-Z +Shift=smoth-change +Alt=Rapid-change";
		html+="<br/>"+"Select Bone(Red Box):Mouse Drag=Rotate-XY,Mouse Wheel=Rotate-Z";
		html+="<br/>"+"Select Root(Red Large Box):Mouse Drag=Rotate-XY,Mouse Wheel=Rotate-Z +Shift=Follow IK +Alt=Move Position";
		html+="<br/>"+"yello box means under Y:0,orange box means near Y:0";
		html+="<br/>"+"<a href='http://webgl.akjava.com'>More info at webgl.akjava.com</a>";
		HTML htmlP=new HTML(html);
		VerticalPanel aboutRoot=new VerticalPanel();
		//TODO credit
		aboutRoot.add(htmlP);
		tabPanel.add(aboutRoot,"About");
	}
	
	Map<String,BoneLimit> boneLimits=new HashMap<String,BoneLimit>();
	
	private void updateIkLabels(){
		//log(""+boneNamesBox);
		boneNamesBox.clear();
		if(currentSelectionName!=null){
			setEnableBoneRanges(true,false);//no root
		for(int i=0;i<getCurrentIkData().getBones().size();i++){
			boneNamesBox.addItem(getCurrentIkData().getBones().get(i));
		}
		boneNamesBox.setSelectedIndex(0);
		}else if(selectedBone!=null){
			setEnableBoneRanges(true,true);
			boneNamesBox.addItem(selectedBone);
			boneNamesBox.setSelectedIndex(0);
			updateBoneRanges();
		}else{
			setEnableBoneRanges(false,false);
		}
	}
	
	private void setEnableBoneRanges(boolean rotate,boolean pos){
		rotationBoneXRange.setEnabled(rotate);
		rotationBoneYRange.setEnabled(rotate);
		rotationBoneZRange.setEnabled(rotate);
		
		positionXBoneRange.setEnabled(pos);
		positionYBoneRange.setEnabled(pos);
		positionZBoneRange.setEnabled(pos);
	}
	
	int ikdataIndex=1;
	List<IKData> ikdatas=new ArrayList<IKData>();

	private String currentSelectionName;
	Mesh selectionMesh;
	final Projector projector=THREE.Projector();
	@Override
	public void onMouseClick(ClickEvent event) {
		
		//not work correctly on zoom
		//Vector3 pos=GWTUtils.toWebGLXY(event.getX(), event.getY(), camera, screenWidth, screenHeight);
		
	//	targetPos.setX(pos.getX());
		//targetPos.setY(pos.getY());
		
		//doCDDIk();
		//doPoseIkk(0);
	}
	
	private boolean isSelectedIk(){
		return currentSelectionName!=null;
	}
	
	private void switchSelectionIk(String name){
		currentSelectionName=name;
		currentMatrixs=AnimationBonesData.cloneAngleAndMatrix(ab.getBonesAngleAndMatrixs());
		
		if(currentSelectionName!=null){
		List<List<NameAndVector3>> result=createBases(getCurrentIkData());
		//log("switchd:"+result.size());
		
		List<NameAndVector3> tmp=result.get(result.size()-1);
		
		for(NameAndVector3 value:tmp){
			log(value.getName()+":"+ThreeLog.get(value.getVector3()));
		}
		
		
		if(candiateAngleAndMatrixs!=null){
			candiateAngleAndMatrixs.clear();
		}else{
			candiateAngleAndMatrixs=new ArrayList<List<AngleAndPosition>>();
		}
		//log("result-size:"+result.size());
		int index=0;
		for(List<NameAndVector3> nv:result){
			//log("candiate:"+index);
			List<AngleAndPosition> bm=AnimationBonesData.cloneAngleAndMatrix(currentMatrixs);
			applyMatrix(bm, nv);
			
			//deb
			for(String bname:getCurrentIkData().getBones()){
				Vector3 angle=bm.get(ab.getBoneIndex(bname)).getAngle();
				//log(bname+":"+ThreeLog.get(angle));
			}
			
			candiateAngleAndMatrixs.add(bm);
			index++;
		}
		}else{
		//	log("null selected");
		}
		
		updateIkLabels();
	}
	
	public List<List<NameAndVector3>> createBases(IKData data){
		int angle=30;
		if(data.getLastBoneName().equals("RightFoot") || data.getLastBoneName().equals("LeftFoot")){
			//something special for foot
			angle=20;
		}
		List<List<NameAndVector3>> all=new ArrayList();
		List<List<NameAndVector3>> result=new ArrayList();
		for(int i=0;i<data.getBones().size();i++){
			String name=data.getBones().get(i);
			List<NameAndVector3> patterns=createBases(name,angle); //90 //60 is slow
			all.add(patterns);
			//log(name+"-size:"+patterns.size());
		}
		//log(data.getLastBoneName()+"-joint-size:"+all.size());
		addBase(all,result,data.getBones(),0,null,2);
		return result;
	}
	
	private void addBase(List<List<NameAndVector3>> all,
			List<List<NameAndVector3>> result, List<String> boneNames, int index,List<NameAndVector3> tmp,int depth) {
		if(index>=boneNames.size() || index==depth){
			result.add(tmp);
			return;
		}
		if(index==0){
			tmp=new ArrayList<NameAndVector3>();
		}
		for(NameAndVector3 child:all.get(index)){
			//copied
			List<NameAndVector3> list=new ArrayList<NameAndVector3>();
			for(int i=0;i<tmp.size();i++){
				list.add(tmp.get(i));
			}
			
			
			list.add(child);
			addBase(all,result,boneNames,index+1,list,2);
		}
	}

	private List<NameAndVector3> createBases(String name,int step){
		Set<NameAndVector3> patterns=new HashSet<NameAndVector3>();
		BoneLimit limit=boneLimits.get(name);
		/*
		for(int x=-180;x<180;x+=step){
			for(int y=-180;y<180;y+=step){
				for(int z=-180;z<180;z+=step){
					boolean pass=true;
					if(limit!=null){
						if(limit.getMinXDegit()>x || limit.getMaxXDegit()<x){
							pass=false;
						}
						if(limit.getMinYDegit()>y || limit.getMaxYDegit()<y){
							pass=false;
						}
						if(limit.getMinZDegit()>z || limit.getMaxZDegit()<z){
							pass=false;
						}
					}
					if(x==180||x==-180 || y==180||y==-180||z==180||z==-180){
						//pass=false;//no need to limit?
					}
					
					if(pass){
						log(name+" pass:"+x+","+y+","+z);
					NameAndVector3 nvec=new NameAndVector3(name, Math.toRadians(x),Math.toRadians(y),Math.toRadians(z));
					patterns.add(nvec);
					}
				}
			}
		}*/
		
		//0 - -180
		for(int x=0;x>=-180;x-=step){
			for(int y=0;y>=-180;y-=step){
				for(int z=0;z>=-180;z-=step){
					boolean pass=true;
					if(limit!=null){
						if(limit.getMinXDegit()>x || limit.getMaxXDegit()<x){
							pass=false;
						}
						if(limit.getMinYDegit()>y || limit.getMaxYDegit()<y){
							pass=false;
						}
						if(limit.getMinZDegit()>z || limit.getMaxZDegit()<z){
							pass=false;
						}
					}
					if(x==180||x==-180 || y==180||y==-180||z==180||z==-180){
						//pass=false;//no need to limit?
					}
					
					if(pass){
					//	log(name+" pass:"+x+","+y+","+z);
					NameAndVector3 nvec=new NameAndVector3(name, Math.toRadians(x),Math.toRadians(y),Math.toRadians(z));
					patterns.add(nvec);
					}
				}
			}
		}
		//step - 179
		for(int x=0;x<180;x+=step){
			for(int y=0;y<180;y+=step){
				for(int z=0;z<180;z+=step){
					boolean pass=true;
					if(limit!=null){
						if(limit.getMinXDegit()>x || limit.getMaxXDegit()<x){
							pass=false;
						}
						if(limit.getMinYDegit()>y || limit.getMaxYDegit()<y){
							pass=false;
						}
						if(limit.getMinZDegit()>z || limit.getMaxZDegit()<z){
							pass=false;
						}
					}
					if(x==180||x==-180 || y==180||y==-180||z==180||z==-180){
						//pass=false;//no need to limit?
					}
					
					if(pass){
					//	log(name+" pass:"+x+","+y+","+z);
					NameAndVector3 nvec=new NameAndVector3(name, Math.toRadians(x),Math.toRadians(y),Math.toRadians(z));
					patterns.add(nvec);
					}
				}
			}
		}
		
		
		if(patterns.size()==0){
			log(name+":use zero base");
			patterns.add(new NameAndVector3(name,0,0,0));//empty not allowd
		}
		
		return new ArrayList<NameAndVector3>(patterns);
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		mouseDown=true;
		mouseDownX=event.getX();
		mouseDownY=event.getY();
		
		

		//log("mouse-click:"+event.getX()+"x"+event.getY());
JsArray<Intersect> intersects=projector.gwtPickIntersects(event.getX(), event.getY(), screenWidth, screenHeight, camera,scene);
		//log("intersects-length:"+intersects.length());
		for(int i=0;i<intersects.length();i++){
			Intersect sect=intersects.get(i);
			
			Object3D target=sect.getObject();
			if(!target.getName().isEmpty()){
				if(target.getName().startsWith("ik:")){
					String bname=target.getName().substring(3);
					for(int j=0;j<ikdatas.size();j++){
						if(ikdatas.get(j).getLastBoneName().equals(bname)){
							ikdataIndex=j;
							selectionMesh.setVisible(true);
							selectionMesh.setPosition(target.getPosition());
							selectionMesh.getMaterial().getColor().setHex(0x00ff00);
							
							if(!bname.equals(currentSelectionName)){
								switchSelectionIk(bname);
							}
							selectedBone=null;
							return;//ik selected
						}
					}
				}else{
					//maybe bone or root
					log("select:"+target.getName());
					selectedBone=target.getName();
					selectionMesh.setVisible(true);
					selectionMesh.setPosition(target.getPosition());
					selectionMesh.getMaterial().getColor().setHex(0xff0000);
					switchSelectionIk(null);
					
					return;
				}
				
			}
		}
		
		selectedBone=null;
		selectionMesh.setVisible(false);
		switchSelectionIk(null);
	}
	private String selectedBone;

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
			if(isSelectedIk()){
				double diffX=event.getX()-mouseDownX;
				double diffY=event.getY()-mouseDownY;
				mouseDownX=event.getX();
				mouseDownY=event.getY();
				
				diffX*=0.1;
				diffY*=-0.1;
				getCurrentIkData().getTargetPos().incrementX(diffX);
				getCurrentIkData().getTargetPos().incrementY(diffY);
				if(event.isShiftKeyDown()){//slow
					doPoseIkk(0,false,1,getCurrentIkData(),1);
				}else if(event.isAltKeyDown()){//rapid
					doPoseIkk(0,true,1,getCurrentIkData(),1);
				}else{
					doPoseIkk(0,true,1,getCurrentIkData(),5);
				}
				
				
			}else if(isSelectedBone()){
				if(event.isAltKeyDown()){
					int diffX=event.getX()-mouseDownX;
					int diffY=event.getY()-mouseDownY;
					mouseDownX=event.getX();
					mouseDownY=event.getY();
					
					positionXBoneRange.setValue(positionXBoneRange.getValue()+diffX);
					positionYBoneRange.setValue(positionYBoneRange.getValue()-diffY);
					positionToBone();
					if(event.isShiftKeyDown()){
					//	switchSelectionIk(null);
					//effect-ik
					for(IKData ik:ikdatas){
						
						doPoseIkk(0,false,5,ik,1);
						}
					}
				}else{
				
				
				int diffX=event.getX()-mouseDownX;
				int diffY=event.getY()-mouseDownY;
				mouseDownX=event.getX();
				mouseDownY=event.getY();
				
				rotationBoneXRange.setValue(rotationBoneXRange.getValue()+diffY);
				rotationBoneYRange.setValue(rotationBoneYRange.getValue()+diffX);
				
				rotToBone();
				if(event.isShiftKeyDown()){
				//	switchSelectionIk(null);
				//effect-ik
				for(IKData ik:ikdatas){
					
					doPoseIkk(0,false,5,ik,1);
					}
				}
				}
			}
			else{//global
			
			int diffX=event.getX()-mouseDownX;
			int diffY=event.getY()-mouseDownY;
			mouseDownX=event.getX();
			mouseDownY=event.getY();
			
			if(event.isShiftKeyDown()){
				//do rotate Z?
				
			}else if(event.isAltKeyDown()){//pos
				positionXRange.setValue(positionXRange.getValue()+diffX);
				positionYRange.setValue(positionYRange.getValue()-diffY);
			}else{//rotate
				rotationRange.setValue(rotationRange.getValue()+diffY);
				rotationYRange.setValue(rotationYRange.getValue()+diffX);
			}
			
			}
			
		
		}
	}
	private boolean isSelectedBone(){
		return !isSelectedIk() && selectedBone!=null;
	}
	private IKData getCurrentIkData(){
		return ikdatas.get(ikdataIndex);
	}
	
	@Override
	public void onMouseWheel(MouseWheelEvent event) {
		if(isSelectedIk()){
			double dy=event.getDeltaY()*0.2;
			getCurrentIkData().getTargetPos().incrementZ(dy);
			
			if(event.isShiftKeyDown()){//slow
				doPoseIkk(0,false,1,getCurrentIkData(),1);
			}else if(event.isAltKeyDown()){//rapid
				doPoseIkk(0,true,1,getCurrentIkData(),1);
			}else{
				doPoseIkk(0,true,1,getCurrentIkData(),5);
			}
			
		}else if(isSelectedBone()){
			if(event.isAltKeyDown()){
			int diff=event.getDeltaY();
			positionZBoneRange.setValue(positionZBoneRange.getValue()+diff);
			positionToBone();
			if(event.isShiftKeyDown()){
				//switchSelectionIk(null);
				//effect-ik
				for(IKData ik:ikdatas){
					doPoseIkk(0,false,5,ik,1);
					}
				}
			
			}else{
			int diff=event.getDeltaY();
			rotationBoneZRange.setValue(rotationBoneZRange.getValue()+diff);
			rotToBone();
				if(event.isShiftKeyDown()){
				//	switchSelectionIk(null);
				//effect-ik
				for(IKData ik:ikdatas){
					
					doPoseIkk(0,false,5,ik,1);
					}
				}
			}
		}
		else{
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
		}
		
	}
	
	

	private HTML5InputRange positionXRange;
	private HTML5InputRange positionYRange;
	private HTML5InputRange positionZRange;
	//private HTML5InputRange frameRange;
	
	private HTML5InputRange rotationRange;
	private HTML5InputRange rotationYRange;
	private HTML5InputRange rotationZRange;
	private HTML5InputRange rotationBoneXRange;
	private HTML5InputRange rotationBoneYRange;
	private HTML5InputRange rotationBoneZRange;
	private PopupPanel bottomPanel;
	private HTML5InputRange currentFrameRange;
	private Label currentFrameLabel;
	private HTML5InputRange positionXBoneRange;
	private HTML5InputRange positionYBoneRange;
	private HTML5InputRange positionZBoneRange;
	private CheckBox ylockCheck;
	private CheckBox xlockCheck;
	private List<String> ikLocks=new ArrayList<String>();
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
		positionXRange = new HTML5InputRange(-300,300,0);
		parent.add(HTML5Builder.createRangeLabel("X-Position:", positionXRange,10));
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
		positionYRange = new HTML5InputRange(-300,300,0);
		parent.add(HTML5Builder.createRangeLabel("Y-Position:", positionYRange,10));
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
		
		//maybe z no need,there are whell-zoom
		HorizontalPanel h6=new HorizontalPanel();
		positionZRange = new HTML5InputRange(-300,300,0);
		//parent.add(HTML5Builder.createRangeLabel("Z-Position:", positionZRange,10));
		//parent.add(h6);
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
		
		//dont need now
		/*
		HorizontalPanel frames=new HorizontalPanel();
		frameRange = new HTML5InputRange(0,1,0);
		parent.add(HTML5Builder.createRangeLabel("Frame:", frameRange));
		//parent.add(frames);
		frames.add(frameRange);
		*/
		/*
		frameRange.addListener(new HTML5InputRangeListener() {
			
			@Override
			public void changed(int newValue) {
				doPose(frameRange.getValue());
			}
		});
		*/
		
	
		//
		HorizontalPanel boneInfo=new HorizontalPanel();
		parent.add(boneInfo);
		boneInfo.add(new Label("Bone"));
		rotateAndPosList = new ListBox();
		boneInfo.add(rotateAndPosList);
		rotateAndPosList.addItem("Rotation");
		rotateAndPosList.addItem("Position");
		rotateAndPosList.setSelectedIndex(0);
		rotateAndPosList.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				switchRotateAndPosList();
			}
		});
	
		HorizontalPanel boneNames=new HorizontalPanel();
		parent.add(boneNames);
		boneNamesBox = new ListBox();
		
		boneNamesBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				updateBoneRanges();
			}
		});
		boneNames.add(boneNamesBox);
		ikLockCheck = new CheckBox("ik-lock");
		boneNames.add(ikLockCheck);
		ikLockCheck.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				//String name=boneNameBox.get
				if(ikLockCheck.getValue()){
					ikLocks.add(getSelectedBoneName());
				}else{
					ikLocks.remove(getSelectedBoneName());
				}
				
			}
		});
		
		
		
		
		//positions
		bonePositionsPanel = new VerticalPanel();
		parent.add(bonePositionsPanel);
		bonePositionsPanel.setVisible(false);
		
		HorizontalPanel h1bpos=new HorizontalPanel();
		positionXBoneRange = new HTML5InputRange(-300,300,0);
		bonePositionsPanel.add(HTML5Builder.createRangeLabel("X-Pos:", positionXBoneRange,10));
		bonePositionsPanel.add(h1bpos);
		h1bpos.add(positionXBoneRange);
		Button resetB1pos=new Button("Reset");
		resetB1pos.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				positionXBoneRange.setValue(0);
				positionToBone();
			}
		});
		h1bpos.add(resetB1pos);
		positionXBoneRange.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				positionToBone();
			}
		});
		
		HorizontalPanel h2bpos=new HorizontalPanel();
		
		positionYBoneRange = new HTML5InputRange(-300,300,0);
		bonePositionsPanel.add(HTML5Builder.createRangeLabel("Y-Pos:", positionYBoneRange,10));
		bonePositionsPanel.add(h2bpos);
		h2bpos.add(positionYBoneRange);
		Button reset2bpos=new Button("Reset");
		reset2bpos.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				positionYBoneRange.setValue(0);
				positionToBone();
			}
		});
		h2bpos.add(reset2bpos);
		positionYBoneRange.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				positionToBone();
			}
		});
		
		
		HorizontalPanel h3bpos=new HorizontalPanel();
		positionZBoneRange = new HTML5InputRange(-300,300,0);
		bonePositionsPanel.add(HTML5Builder.createRangeLabel("Z-Pos:", positionZBoneRange,10));
		bonePositionsPanel.add(h3bpos);
		h3bpos.add(positionZBoneRange);
		Button reset3bpos=new Button("Reset");
		reset3bpos.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				positionZBoneRange.setValue(0);
				positionToBone();
			}
		});
		h3bpos.add(reset3bpos);
		positionZBoneRange.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				positionToBone();
			}
		});
		
		
		
		boneRotationsPanel = new VerticalPanel();
		parent.add(boneRotationsPanel);
		
		HorizontalPanel h1b=new HorizontalPanel();
		
		xlockCheck = new CheckBox();
		h1b.add(xlockCheck);
		xlockCheck.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(xlockCheck.getValue()){
					boneLock.setX(getSelectedBoneName(), rotationBoneXRange.getValue());
					rotationBoneXRange.setEnabled(false);
				
				}else{
					boneLock.clearX(getSelectedBoneName());
					rotationBoneXRange.setEnabled(true);
				}
				
			}
		});
		
		rotationBoneXRange = new HTML5InputRange(-180,180,0);
		boneRotationsPanel.add(HTML5Builder.createRangeLabel("X-Rotate:", rotationBoneXRange));
		boneRotationsPanel.add(h1b);
		h1b.add(rotationBoneXRange);
		Button resetB1=new Button("Reset");
		resetB1.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				rotationBoneXRange.setValue(0);
				rotToBone();
			}
		});
		h1b.add(resetB1);
		rotationBoneXRange.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				rotToBone();
			}
		});
		
		
		HorizontalPanel h2b=new HorizontalPanel();
		ylockCheck = new CheckBox();
		h2b.add(ylockCheck);
		ylockCheck.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(ylockCheck.getValue()){
					boneLock.setY(getSelectedBoneName(), rotationBoneYRange.getValue());
					rotationBoneYRange.setEnabled(false);
				
				}else{
					boneLock.clearY(getSelectedBoneName());
					rotationBoneYRange.setEnabled(true);
				}
				
			}
		});
		rotationBoneYRange = new HTML5InputRange(-180,180,0);
		boneRotationsPanel.add(HTML5Builder.createRangeLabel("Y-Rotate:", rotationBoneYRange));
		boneRotationsPanel.add(h2b);
		h2b.add(rotationBoneYRange);
		Button reset2b=new Button("Reset");
		reset2b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				rotationBoneYRange.setValue(0);
				rotToBone();
			}
		});
		h2b.add(reset2b);
		rotationBoneYRange.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				rotToBone();
			}
		});
		
		
		HorizontalPanel h3b=new HorizontalPanel();
		zlockCheck = new CheckBox();
		h3b.add(zlockCheck);
		zlockCheck.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(zlockCheck.getValue()){
					boneLock.setZ(getSelectedBoneName(), rotationBoneZRange.getValue());
					rotationBoneZRange.setEnabled(false);
				
				}else{
					boneLock.clearZ(getSelectedBoneName());
					rotationBoneZRange.setEnabled(true);
				}
				
			}
		});
		rotationBoneZRange = new HTML5InputRange(-180,180,0);
		boneRotationsPanel.add(HTML5Builder.createRangeLabel("Z-Rotate:", rotationBoneZRange));
		boneRotationsPanel.add(h3b);
		h3b.add(rotationBoneZRange);
		Button reset3b=new Button("Reset");
		reset3b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				rotationBoneZRange.setValue(0);
				rotToBone();
			}
		});
		h3b.add(reset3b);
		rotationBoneZRange.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				rotToBone();
			}
		});
		
		
		FileUpload meshUpload=new FileUpload();
		parent.add(meshUpload);
		meshUpload.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				JsArray<File> files = FileUtils.toFile(event.getNativeEvent());
				
				final FileReader reader=FileReader.createFileReader();
				reader.setOnLoad(new FileHandler() {
					@Override
					public void onLoad() {
						LoadJsonModel(reader.getResultAsString());
					}
				});
				reader.readAsText(files.get(0),"utf-8");
			}
		});
		
		
		Button test=new Button("screen-shot");
		parent.add(test);
		test.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String url=renderer.gwtPngDataUrl();
				log(url);
				Window.open(url, "newwin", null);
			}
		});
		
		
		
		
		
		
		updateMaterial();
		positionYRange.setValue(-140);//for test
		
		updateIkLabels();
		createBottomPanel();
		showControl();
		
	}
	private void LoadJsonModel(String jsonText){
		GWTGeometryUtils.loadJsonModel(jsonText,new  LoadHandler() {
			@Override
			public void loaded(Geometry geometry) {
				if(bodyMesh!=null){
					root.remove(bodyMesh);//for initialzie
					bodyMesh=null;
				}
				ab=null;//for remake.
				
				baseGeometry=geometry;//change body mesh
				//TODO set bone
				
				doRePose(0);
			}
		});
		
	}
	private String getSelectedBoneName(){
		if(boneNamesBox.getSelectedIndex()==-1){
			return "";
		}
		return boneNamesBox.getValue(boneNamesBox.getSelectedIndex());
	}
	protected void positionToBone() {
		String name=boneNamesBox.getItemText(boneNamesBox.getSelectedIndex());
		int index=ab.getBoneIndex(name);
		if(index!=0){
			//limit root only 
			//TODO limit by bvh channel
			return;
		}

		Vector3 angles=GWTThreeUtils.rotationToVector3(ab.getBoneAngleAndMatrix(index).getMatrix());
				
		
		Vector3 pos=THREE.Vector3(positionXBoneRange.getValue(),
				positionYBoneRange.getValue()
				, positionZBoneRange.getValue()).multiplyScalar(0.1);
		
		Matrix4 posMx=GWTThreeUtils.translateToMatrix4(pos);
		Matrix4 rotMx=GWTThreeUtils.rotationToMatrix4(angles);
		rotMx.multiply(posMx,rotMx);
		ab.getBoneAngleAndMatrix(index).setMatrix(rotMx);
		//ab.setBoneAngleAndMatrix(index, rotMx);
		doPoseByMatrix(ab);
		
		if( isSelectedBone()){
			selectionMesh.setPosition(pos);
		}
	}

	protected void switchRotateAndPosList() {
		int index=rotateAndPosList.getSelectedIndex();
		if(index==0){
			bonePositionsPanel.setVisible(false);
			boneRotationsPanel.setVisible(true);
		}else{
			bonePositionsPanel.setVisible(true);
			boneRotationsPanel.setVisible(false);
		}
	}

	private String getNewName(){
		return "Untitled"+(fileIndex);
	}
	
	private int fileIndex;
	private void createBottomPanel(){
		bottomPanel = new PopupPanel();
		bottomPanel.setVisible(true);
		bottomPanel.setSize("650px", "40px");
		VerticalPanel main=new VerticalPanel();
		bottomPanel.add(main);
		bottomPanel.show();
		
		
		//upper
		HorizontalPanel topPanel=new HorizontalPanel();
		main.add(topPanel);
		
		fileNames = new ListBox();
		
		fileNames.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				selectPoseEditorDatas(fileNames.getSelectedIndex());
			}
		});
		
		
		topPanel.add(fileNames);
		Button newFile=new Button("New");
		topPanel.add(newFile);
		newFile.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doNewFile();
			}
		});
		saveButton = new Button("Save");
		topPanel.add(saveButton);
		saveButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doSaveFile();
			}
		});
		
		Button saveAsButton = new Button("SaveAs");
		topPanel.add(saveAsButton);
		saveAsButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				doSaveAsFile(getSelectedPoseEditorData());
			}
		});
		
		HorizontalPanel upperPanel=new HorizontalPanel();
		upperPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		main.add(upperPanel);
		
		Button snap=new Button("Add");//TODO before,after
		upperPanel.add(snap);
		snap.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				insertFrame(getSelectedPoseEditorData().getPoseFrameDatas().size(),false);
			}
		});
		Button replace=new Button("Replace");//TODO before,after
		upperPanel.add(replace);
		replace.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				insertFrame(currentFrameRange.getValue(),true);
			}
		});
		
		/*
		 *should think system
		Button cut=new Button("Cut");
		upperPanel.add(cut);
		cut.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				doCut();
			}
		});
		*/
		Button copy=new Button("Copy");
		upperPanel.add(copy);
		copy.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				doCopy();
			}
		});
		
		Button paste=new Button("Paste");
		upperPanel.add(paste);
		paste.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				doPaste();
			}
		});
		
		Button remove=new Button("Remove");
		upperPanel.add(remove);
		remove.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				getSelectedPoseEditorData().getPoseFrameDatas().remove(poseFrameDataIndex);
				getSelectedPoseEditorData().setModified(true);
				updatePoseIndex(Math.max(0,poseFrameDataIndex-1));
				
				updateSaveButtons();
			}
		});
		
		
		
		HorizontalPanel pPanel=new HorizontalPanel();
		main.add(pPanel);
		pPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		
		currentFrameRange = new HTML5InputRange(0,0,0);
		currentFrameRange.setWidth("420px");
		pPanel.add(currentFrameRange);
		
		currentFrameRange.addMouseUpHandler(new MouseUpHandler() {
			
			@Override
			public void onMouseUp(MouseUpEvent event) {
				
				updatePoseIndex(currentFrameRange.getValue());
			}
		});
		
		Button prev=new Button("Prev");
		pPanel.add(prev);
		prev.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int value=currentFrameRange.getValue();
				if(value>0){
					value--;
					currentFrameRange.setValue(value);
					updatePoseIndex(value);
				}
			}
		});
		Button next=new Button("Next");
		pPanel.add(next);
		next.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int value=currentFrameRange.getValue();
				if(value<getSelectedPoseEditorData().getPoseFrameDatas().size()-1){
					value++;
					currentFrameRange.setValue(value);
					updatePoseIndex(value);
				}
			}
		});
		
		currentFrameLabel = new Label();
		pPanel.add(currentFrameLabel);
		
		super.leftBottom(bottomPanel);
	}
	
	
	
	protected void doSaveAsFile(PoseEditorData pdata) {
		String result=Window.prompt("Save File", pdata.getName());
		if(result!=null){
			pdata.setName(result);
			JSONObject data=PoseEditorData.writeData(pdata);
			fileNames.setItemText(poseEditorDataSelection, result);
			
			//TODO
			if(!preference.isAvailable()){
				//TODO just export
				return;
			}
			
			
			//save database
			int dataIndex=preference.getValue(KEY_INDEX, 0);
			
			
			//TODO method?
			Canvas canvas=Canvas.createIfSupported();
			int thumbW=32;
			int thumbH=32;
			canvas.setSize(thumbW+"px", thumbH+"px");
			canvas.setCoordinateSpaceWidth(thumbW);
			canvas.setCoordinateSpaceHeight(thumbH);
			canvas.getContext2d().drawImage(renderer.gwtCanvas(),0,0,screenWidth,screenHeight,0,0,thumbW,thumbH);
			String thumbnail=canvas.toDataUrl();
			//Window.open(thumbnail, "tmp", null);
			
			preference.setValue(KEY_DATA+dataIndex, data.toString());
			preference.setValue(KEY_IMAGE+dataIndex, thumbnail);
			preference.setValue(KEY_HEAD+dataIndex, pdata.getName()+"\t"+pdata.getCdate());
			pdata.setFileId(dataIndex);
			
			//increment
			dataIndex++;
			preference.setValue(KEY_INDEX, dataIndex);
			
			
			pdata.setModified(false);
			updateSaveButtons();
			updateDatasPanel();
			

			
			tabPanel.selectTab(1);//datas
		}
		
		
	}
	protected void doSaveFile() {
		
		
		PoseEditorData pdata=getSelectedPoseEditorData();
		
		int fileId=pdata.getFileId();
		if(fileId!=-1){
			JSONObject data=PoseEditorData.writeData(pdata);
			preference.setValue(KEY_DATA+fileId, data.toString());
			pdata.setModified(false);
			updateSaveButtons();
			updateDatasPanel();//
		}else{
			doSaveAsFile(pdata);
		}
		
		//log(data.toString());
		
		
		
		/*
		//test
		PoseEditorData readData=PoseEditorData.readData(data.toString());
		readData.updateMatrix(ab);
		readData.setName("tmp1");
		doLoad(readData);
		*/
		
	}


	private int poseEditorDataSelection;
	List<PoseEditorData> poseEditorDatas=new ArrayList<PoseEditorData>();
	
	public void selectPoseEditorDatas(int index){
		poseEditorDataSelection=index;
		fileNames.setSelectedIndex(index);
		
		updatePoseIndex(0);
		updateSaveButtons();
	}
	private PoseEditorData getSelectedPoseEditorData(){
		return poseEditorDatas.get(poseEditorDataSelection);
	}
	
	protected void doNewFile() {
		fileIndex++;
		String newName=getNewName();
		PoseEditorData ped=new PoseEditorData();
		ped.setModified(true);//new always
		ped.setName(newName);
		ped.setCdate(System.currentTimeMillis());
		
		List<PoseFrameData> pfList=new ArrayList<PoseFrameData>();
		ped.setPoseFrameDatas(pfList);
		pfList.add(initialPoseFrameData.clone());
		
		ped.setBones(boneList);
		
		
		poseEditorDatas.add(ped);
		fileNames.addItem(newName);
		fileNames.setSelectedIndex(fileNames.getItemCount()-1);
		selectPoseEditorDatas(fileNames.getItemCount()-1);
		
		updateSaveButtons();
	}
	
	public void doLoad(PoseEditorData ped){
		ped.updateMatrix(ab);//need bone data
		poseEditorDatas.add(ped);
		fileNames.addItem(ped.getName());
		
		selectPoseEditorDatas(poseEditorDatas.size()-1);
		
		tabPanel.selectTab(0);//datas
		//called addChangeHandler
		updateSaveButtons();
	}

	protected void doPaste() {
		if(clipboard!=null){
			getSelectedPoseEditorData().getPoseFrameDatas().add(currentFrameRange.getValue()+1,clipboard.clone());
			updatePoseIndex(currentFrameRange.getValue()+1);
		}
	}

	protected void doCut() {
		// TODO Auto-generated method stub
		
	}

	
	PoseFrameData clipboard;
	protected void doCopy() {
		// TODO Auto-generated method stub
		clipboard=snapCurrentFrameData();
	}

	private PoseFrameData initialPoseFrameData;
	
	private PoseFrameData snapCurrentFrameData(){
		List<AngleAndPosition> matrixs=AnimationBonesData.cloneAngleAndMatrix(ab.getBonesAngleAndMatrixs());
		
		List<Vector3> angles=new ArrayList<Vector3>();
		List<Vector3> positions=new ArrayList<Vector3>();
		for(int i=0;i<matrixs.size();i++){
			Vector3 angle=matrixs.get(i).getAngle().clone();
			angles.add(angle);
			
			Vector3 position=ab.getMatrixPosition(i);//TODO getPosition()?
			position.subSelf(ab.getBaseBoneRelativePosition(i));
			
			positions.add(position);
		//	log(ab.getBoneName(i)+" pos="+ThreeLog.get(position)+",base="+ThreeLog.get(ab.getBaseBoneRelativePosition(i)));
		}
		
		List<Vector3> targets=new ArrayList<Vector3>();
		List<String> names=new ArrayList<String>();
		
		for(IKData ikdata:ikdatas){
			Vector3 pos=ikdata.getTargetPos().clone();
			pos.subSelf(ab.getBonePosition(ikdata.getLastBoneName()));//relative path
			targets.add(pos);
			names.add(ikdata.getName());
		}
		
		
		
		PoseFrameData ps=new PoseFrameData(matrixs, targets,names);
		ps.setAngles(angles);
		ps.setPositions(positions);
		return ps;
	}
	
	private void insertFrame(int index,boolean overwrite){
		if(index<0){
			index=0;
		}
		PoseFrameData ps=snapCurrentFrameData();
		if(overwrite){
			getSelectedPoseEditorData().getPoseFrameDatas().set(index,ps);
			updatePoseIndex(index);
		}else{
			getSelectedPoseEditorData().getPoseFrameDatas().add(index,ps);
			updatePoseIndex(getSelectedPoseEditorData().getPoseFrameDatas().size()-1);
		}
		
		getSelectedPoseEditorData().setModified(true);
		updateSaveButtons();
	}
	
	private void updateSaveButtons() {
		if(getSelectedPoseEditorData().isModified()){
			saveButton.setEnabled(true);
		}else{
			saveButton.setEnabled(false);
		}
	}

	protected void doExport(PoseEditorData ped) {
		ped.updateMatrix(ab);//current-bone
		
		BVH exportBVH=new BVH();
		
		BVHConverter converter=new BVHConverter();
		BVHNode node=converter.convertBVHNode(bones);
		
		exportBVH.setHiearchy(node);
		
		converter.setChannels(node,0,"XYZ");	//TODO support other order
		
		
		BVHMotion motion=new BVHMotion();
		motion.setFrameTime(.25);
		
		for(PoseFrameData pose:ped.getPoseFrameDatas()){
			double[] values=converter.angleAndMatrixsToMotion(pose.getAngleAndMatrixs(),BVHConverter.ROOT_POSITION_ROTATE_ONLY,"XYZ");
			motion.add(values);
		}
		motion.setFrames(motion.getMotions().size());//
		
		exportBVH.setMotion(motion);
		//log("frames:"+exportBVH.getFrames());
		BVHWriter writer=new BVHWriter();
		
		String bvhText=writer.writeToString(exportBVH);
		
		//log(bvhText);
		exportTextChrome(bvhText,"poseeditor"+exportIndex);
		exportIndex++;
		
		
		
	}
	public native final void exportTextChrome(String text,String wname)/*-{
	win = $wnd.open("", wname)
	win.document.body.innerText =""+text+"";
	}-*/;

	
	private int exportIndex=0;

	
	
	private int poseFrameDataIndex=0;
	//private List<PoseFrameData> poseFrameDatas=new ArrayList<PoseFrameData>();
	
	
	private void updatePoseIndex(int index){
		if(index==-1){
		currentFrameRange.setMax(0);
		currentFrameRange.setValue(0);
		currentFrameLabel.setText("");	
		}else{
		//poseIndex=index;
		currentFrameRange.setMax(getSelectedPoseEditorData().getPoseFrameDatas().size()-1);
		currentFrameRange.setValue(index);
		currentFrameLabel.setText((index+1)+"/"+getSelectedPoseEditorData().getPoseFrameDatas().size());
		selectFrameData(index);
		}
	}
	
	private void selectFrameData(int index) {
		
		poseFrameDataIndex=index;
		PoseFrameData pfd=getSelectedPoseEditorData().getPoseFrameDatas().get(index);
		
		currentMatrixs=AnimationBonesData.cloneAngleAndMatrix(pfd.getAngleAndMatrixs());
		ab.setBonesAngleAndMatrixs(currentMatrixs);
		//update
		
		for(int i=0;i<ikdatas.size();i++){
			Vector3 vec=pfd.getIkTargetPositions().get(i).clone();
			vec.addSelf(ab.getBonePosition(ikdatas.get(i).getLastBoneName()));//relative path
			
			ikdatas.get(i).getTargetPos().set(vec.getX(), vec.getY(), vec.getZ());
		}
		
		if(isSelectedIk()){
		switchSelectionIk(getCurrentIkData().getLastBoneName());
		}
		
		
		doPoseByMatrix(ab);
		updateBoneRanges();
	}

	private void rotToBone(){
		String name=boneNamesBox.getItemText(boneNamesBox.getSelectedIndex());
		int index=ab.getBoneIndex(name);
		//Matrix4 mx=ab.getBoneMatrix(name);
		Vector3 degAngles=THREE.Vector3(rotationBoneXRange.getValue(),rotationBoneYRange.getValue(),rotationBoneZRange.getValue());
		Vector3 angles=GWTThreeUtils.degreeToRagiant(degAngles);
		//log("set-angle:"+ThreeLog.get(GWTThreeUtils.radiantToDegree(angles)));
		//mx.setRotationFromEuler(angles, "XYZ");
		
		
		Vector3 pos=GWTThreeUtils.toPositionVec(ab.getBoneAngleAndMatrix(index).getMatrix());
		//log("pos:"+ThreeLog.get(pos));
		Matrix4 posMx=GWTThreeUtils.translateToMatrix4(pos);
		
		Matrix4 rotMx=GWTThreeUtils.rotationToMatrix4(angles);
		rotMx.multiply(posMx,rotMx);
		
		//log("bone-pos:"+ThreeLog.get(bones.get(index).getPos()));
		
		ab.getBoneAngleAndMatrix(index).setMatrix(rotMx);
		ab.getBoneAngleAndMatrix(index).setAngle(degAngles);
	
		doPoseByMatrix(ab);
	}
	
	private void updateBoneRanges(){
	updateBoneRotationRanges();
	updateBonePositionRanges();
	
	}
	private void updateBoneRotationRanges(){
		if(boneNamesBox.getSelectedIndex()==-1){
			return;
		}
		String name=boneNamesBox.getItemText(boneNamesBox.getSelectedIndex());
		
		if(ikLocks.contains(name)){
			ikLockCheck.setValue(true);
		}else{
			ikLockCheck.setValue(false);
		}
		
		
		int boneIndex=ab.getBoneIndex(name);
		if(boneIndex!=0){//only root has position
			rotateAndPosList.setSelectedIndex(0);
			switchRotateAndPosList();
		}
		//Quaternion q=GWTThreeUtils.jsArrayToQuaternion(bones.get(boneIndex).getRotq());
		//log("bone:"+ThreeLog.get(GWTThreeUtils.radiantToDegree(GWTThreeUtils.rotationToVector3(q))));
				
		Vector3 mAngles=GWTThreeUtils.toDegreeAngle(ab.getBoneAngleAndMatrix(name).getMatrix());
		//log("updateBoneRotationRanges():"+ThreeLog.get(mAngles));
		
		Vector3 angles=ab.getBoneAngleAndMatrix(name).getAngle();
		int x=(int) angles.getX();
		
		rotationBoneXRange.setValue(x);
		if(boneLock.hasX(name)){
			xlockCheck.setValue(true);
			rotationBoneXRange.setEnabled(false);
		}else{
			xlockCheck.setValue(false);
			rotationBoneXRange.setEnabled(true);
		}
		
		int y=(int) angles.getY();
		
		rotationBoneYRange.setValue(y);
		if(boneLock.hasY(name)){
			ylockCheck.setValue(true);
			rotationBoneYRange.setEnabled(false);
		}else{
			ylockCheck.setValue(false);
			rotationBoneYRange.setEnabled(true);
		}
	
		int z=(int) angles.getZ();
		
		rotationBoneZRange.setValue(z);
		if(boneLock.hasZ(name)){
			zlockCheck.setValue(true);
			rotationBoneZRange.setEnabled(false);
		}else{
			zlockCheck.setValue(false);
			rotationBoneZRange.setEnabled(true);
		}
		
	}
	
	private void updateBonePositionRanges(){
		if(boneNamesBox.getSelectedIndex()==-1){
			return;
		}
		String name=boneNamesBox.getItemText(boneNamesBox.getSelectedIndex());
		
		Vector3 values=GWTThreeUtils.toPositionVec(ab.getBoneAngleAndMatrix(name).getMatrix());
		values.multiplyScalar(10);

		int x=(int) values.getX();
		positionXBoneRange.setValue(x);
		
		int y=(int) values.getY();
		
		positionYBoneRange.setValue(y);
	
		int z=(int) values.getZ();
		positionZBoneRange.setValue(z);
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
			material=THREE.MeshBasicMaterial().map(ImageUtils.loadTexture("men3smart_texture2.png")).transparent(transparent).opacity(opacity).build();
			
		}else{
			material=THREE.MeshLambertMaterial().map(ImageUtils.loadTexture("men3smart_texture2.png")).transparent(transparent).opacity(opacity).build();
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
	
private List<String> boneList=new ArrayList<String>();
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
				for(int i=0;i<bones.length();i++){
					boneList.add(bones.get(i).getName());
				}
				
				AnimationDataConverter dataConverter=new AnimationDataConverter();
				dataConverter.setSkipFirst(false);
				animationData = dataConverter.convertJsonAnimation(bones,bvh);
				//frameRange.setMax(animationData.getHierarchy().get(0).getKeys().length());
				
				JSONLoader loader=THREE.JSONLoader();
				loader.load("men3menb.js", new  LoadHandler() {
					@Override
					public void loaded(Geometry geometry) {
						baseGeometry=geometry;
						
						
						//doPose(0);
						doPose(0);//bug i have no idea,need call twice to better initial pose
						
						initialPoseFrameData=snapCurrentFrameData();
						doNewFile();
						//insertFrame(getSelectedPoseEditorData().getPoseFrameDatas().size(),false);//initial pose-frame
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
			mx.setPosition(motionPos);
			mx.setRotationFromQuaternion(motion.getRot());
			/*
			Matrix4 mx2=THREE.Matrix4();
			mx2.setRotationFromQuaternion(motion.getRot());
			mx.multiplySelf(mx2);
			*/
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

	
	/**
	 * called after load
	 * @param index
	 */
	private void doPose(int index){
		
		//initial bone names
		log(bones);
		for(int i=0;i<bones.length();i++){
			//log(bones.get(i).getName());

		}
		
	
	initializeBodyMesh();
	initializeAnimationData(index,false);
	//stepCDDIk();	
	log("do-pose");
	doPoseByMatrix(ab);
	
	updateBoneRanges();
	
	
	/*
	 * trying to fix leg problem
	Vector3 rootOffset=GWTThreeUtils.jsArrayToVector3(animationData.getHierarchy().get(0).getKeys().get(index).getPos());
	//initial pose is base for motions
	baseGeometry=GeometryUtils.clone(bodyMesh.getGeometry());
	for(int i=0;i<baseGeometry.vertices().length();i++){
		Vertex vertex=baseGeometry.vertices().get(i);
		vertex.getPosition().subSelf(rootOffset);
	}
	*/
	
	}
	
	//for after loading
private void doRePose(int index){
	initializeBodyMesh();
	initializeAnimationData(index,true);
	//stepCDDIk();	
	doPoseByMatrix(ab);
	
	updateBoneRanges();
	log("update-bone-range");
	}
	
	
AnimationBonesData ab;
List<AngleAndPosition> baseMatrixs;		




private void applyMatrix(List<AngleAndPosition> matrix,List<NameAndVector3> samples){
for(NameAndVector3 nv:samples){
	int boneIndex=ab.getBoneIndex(nv.getName());
	Matrix4 translates=GWTThreeUtils.translateToMatrix4(GWTThreeUtils.toPositionVec(ab.getBoneAngleAndMatrix(boneIndex).getMatrix()));
	Matrix4 newMatrix=GWTThreeUtils.rotationToMatrix4(nv.getVector3());
	newMatrix.multiply(translates,newMatrix);
	//log("apply-matrix");
	matrix.get(boneIndex).setAngle(GWTThreeUtils.radiantToDegree(nv.getVector3()));
	matrix.get(boneIndex).setMatrix(newMatrix);
	}
}


List<List<AngleAndPosition>> candiateAngleAndMatrixs;

private void initializeBodyMesh(){
			//initializeBodyMesh
			if(bodyMesh==null){//initial Indices & weight,be careful bodyMesh create in doPoseByMatrix
				bodyIndices = (JsArray<Vector4>) JsArray.createArray();
				bodyWeight = (JsArray<Vector4>) JsArray.createArray();

				//geometry initialized 0 indices & weights
				if(baseGeometry.getSkinIndices().length()!=0 && baseGeometry.getSkinWeight().length()!=0){
					log("auto-weight from geometry:");
					WeightBuilder.autoWeight(baseGeometry, bones, WeightBuilder.MODE_FROM_GEOMETRY, bodyIndices, bodyWeight);
					
				}else{
					//WeightBuilder.autoWeight(baseGeometry, bones, WeightBuilder.MODE_NearParentAndChildren, bodyIndices, bodyWeight);
					
					WeightBuilder.autoWeight(baseGeometry, bones, WeightBuilder.MODE_NearParentAndChildren, bodyIndices, bodyWeight);
					
					
				}
				//WeightBuilder.autoWeight(baseGeometry, bones, WeightBuilder.MODE_NearAgressive, bodyIndices, bodyWeight);
				log("initialized-weight:"+bodyIndices.length());
				for(int i=0;i<bodyIndices.length();i++){
					log(bodyIndices.get(i).getX()+" x "+bodyIndices.get(i).getY());
				}
				
				}else{
					root.remove(bodyMesh);
				}
}
List<AngleAndPosition> currentMatrixs;
private void initializeAnimationData(int index,boolean resetMatrix){

	//initialize AnimationBone
	if(ab==null){
	baseMatrixs=AnimationBonesData.boneToAngleAndMatrix(bones, animationData, index);
	ab=new AnimationBonesData(bones,AnimationBonesData.cloneAngleAndMatrix(baseMatrixs) );
	
	for(int i=0;i<bones.length();i++){
	//	log(bones.get(i).getName()+":"+ThreeLog.get(baseMatrixs.get(i).getPosition()));
	}
	}
	
	//TODO make automatic
	//this is find base matrix ,because sometime cdd-ik faild from some position
	//nearMatrix=new ArrayList<List<Matrix4>>();
	//nearMatrix.add(AnimationBonesData.cloneMatrix(baseMatrixs));
	
	/*
	 * for foot
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
	
	//ab.setBonesMatrixs(findStartMatrix("RightFoot",getCurrentIkData().getTargetPos()));//
	*/
	if(currentMatrixs!=null && resetMatrix){
		if(candiateAngleAndMatrixs!=null){
			//need bone limit
			ab.setBonesAngleAndMatrixs(AnimationBonesData.cloneAngleAndMatrix(findStartMatrix(getCurrentIkData().getLastBoneName(),getCurrentIkData().getTargetPos())));//)
		}else{
			ab.setBonesAngleAndMatrixs(AnimationBonesData.cloneAngleAndMatrix(currentMatrixs));	
		}
		//TODO only need?
	}else{
		
	}
	
}
private BoneLockControler boneLock=new BoneLockControler();


//this new position base ikk faild
private Vector3 findNextStep(int boneIndex,int lastBoneIndex,Vector3 targetPos){
	Vector3 lastTrans=ab.getMatrixPosition(lastBoneIndex);
	List<Integer> path=ab.getBonePath(lastBoneIndex);
	Matrix4 matrix=THREE.Matrix4();
	for(int i=0;i<path.size()-1;i++){
		int bindex=path.get(i);
		AngleAndPosition am=ab.getBoneAngleAndMatrix(bindex);
		matrix.multiplySelf(am.getMatrix());
	}
	Vector3 base=THREE.Vector3(0,0,0);
	Vector3 pos=matrix.multiplyVector3(lastTrans.clone());
	double length=pos.subSelf(targetPos).length();
	//log("length:"+length+","+0+"x"+0+"x"+0);
	Vector3 tmpVec=THREE.Vector3();
	for(int x=-1;x<=1;x++){
		for(int y=-1;y<=1;y++){
			for(int z=-1;z<=1;z++){
				if(x==0 && y==0 && z==0){
					continue;
				}
				tmpVec.set(x*5, y*5, z*5);
				matrix=THREE.Matrix4();
				for(int i=0;i<path.size()-1;i++){
					int bindex=path.get(i);
					AngleAndPosition am=ab.getBoneAngleAndMatrix(bindex);
					Matrix4 m=am.getMatrix();
					if(bindex==boneIndex){
						Vector3 newAngle=am.getAngle().clone().addSelf(tmpVec);
						Vector3 pv=GWTThreeUtils.toPositionVec(m);
						
						m=THREE.Matrix4();
						m.setPosition(pv);
						m.setRotationFromEuler(newAngle, "XYZ");
						
					}
					matrix.multiplySelf(m);
				}
				
				pos=matrix.multiplyVector3(lastTrans.clone());
				double tmpl=pos.subSelf(targetPos).length();
				//log("length:"+tmpl+","+x+"x"+y+"x"+z);
				if(tmpl<length){
					base.set(x*5,y*5,z*5);
					length=tmpl;
				}
			}
		}
	}
	//log("mutch:"+ThreeLog.get(base));
	return base.addSelf(ab.getBoneAngleAndMatrix(boneIndex).getAngle());
}

private void stepCDDIk(int perLimit,IKData ikData,int cddLoop){

	//do CDDIK
	//doCDDIk();
	Vector3 tmp1=null,tmp2=null;
	currentIkJointIndex=0;
	
	for(int i=0;i<ikData.getIteration()*cddLoop;i++){
	String targetBoneName=ikData.getBones().get(currentIkJointIndex);
	
	if(ikLocks.contains(targetBoneName)){
		currentIkJointIndex++;
		if(currentIkJointIndex>=ikData.getBones().size()){
			currentIkJointIndex=0;
		}
		continue;
	}
	
	int boneIndex=ab.getBoneIndex(targetBoneName);
	
	
	Vector3 ikkedAngle=null;
	Matrix4 jointRot=ab.getBoneAngleAndMatrix(targetBoneName).getMatrix();
	Matrix4 translates=GWTThreeUtils.translateToMatrix4(GWTThreeUtils.toPositionVec(jointRot));
	Vector3 currentAngle=ab.getBoneAngleAndMatrix(targetBoneName).getAngle().clone();
	//log("current:"+ThreeLog.get(currentAngle));
	if(perLimit>0){
	Vector3 lastJointPos=ab.getBonePosition(ikData.getLastBoneName());
	
	
	
	//Vector3 jointPos=ab.getParentPosition(targetName);
	Vector3 jointPos=ab.getBonePosition(targetBoneName);
	
	
	
	//Vector3 beforeAngles=GWTThreeUtils.radiantToDegree(GWTThreeUtils.rotationToVector3(jointRot));
	Vector3 beforeAngle=ab.getBoneAngleAndMatrix(targetBoneName).getAngle().clone();
	
	//Matrix4 newMatrix=cddIk.doStep(lastJointPos, jointPos, jointRot, ikData.getTargetPos());
	
	//TODO add parent bone angles
	AngleAndPosition root=ab.getBoneAngleAndMatrix(0);
	Vector3 parentAngle=ab.getParentAngles(boneIndex);
	Matrix4 newMatrix=cddIk.getStepAngleMatrix(parentAngle,lastJointPos, jointPos, jointRot, ikData.getTargetPos());
	if(newMatrix==null){//invalid value
		continue;
	}
	
	
	
	
	
	//limit per angles
	/*
	 * if angle value over 90 usually value is invalid.
	 * but i dont know how to detect or fix it.
	 */
	ikkedAngle=GWTThreeUtils.rotationToVector3(newMatrix);
	
	
	//Vector3 diffAngles=GWTThreeUtils.radiantToDegree(ikkedAngle).subSelf(currentAngle);
	Vector3 diffAngles=GWTThreeUtils.radiantToDegree(ikkedAngle);
	if(perLimit==1){
	diffAngles.normalize();
	}
	//diffAngles.normalize().addScalar(perLimit);//miss choice
	
	//log("diff:"+ThreeLog.get(diffAngles));
	if(Math.abs(diffAngles.getX())>perLimit){
		double diff=perLimit;
		if(diffAngles.getX()<0){
			diff*=-1;
		}
		diffAngles.setX(diff);
	}
	if(Math.abs(diffAngles.getY())>perLimit){
		double diff=perLimit;
		if(diffAngles.getY()<0){
			diff*=-1;
		}
		diffAngles.setY(diff);
	}
	if(Math.abs(diffAngles.getZ())>perLimit){
		double diff=perLimit;
		if(diffAngles.getZ()<0){
			diff*=-1;
		}
		diffAngles.setZ(diff);
	}
	
	currentAngle.addSelf(diffAngles);
	//log("added:"+ThreeLog.get(currentAngle));
	
	//currentAngle.setX(0);//keep x
	
	
	
	ikkedAngle=GWTThreeUtils.degreeToRagiant(currentAngle);
	}else{
		//faild TODO fix it
		Vector3 angle=findNextStep(boneIndex, ab.getBoneIndex(ikData.getLastBoneName()), ikData.getTargetPos());
		//log(targetBoneName+" before:"+ThreeLog.get(ab.getBoneAngleAndMatrix(boneIndex).getAngle())+" after:"+ThreeLog.get(angle));
		ikkedAngle=GWTThreeUtils.degreeToRagiant(angle);
		
	}
	//log("before:"+ThreeLog.get(beforeAngle)+" after:"+ThreeLog.get(currentAngle));
	
	
	//limit max
	BoneLimit blimit=boneLimits.get(targetBoneName);
	//log(targetBoneName);
	//log("before-limit:"+ThreeLog.get(GWTThreeUtils.radiantToDegree(angles)));
	if(blimit!=null){
		blimit.apply(ikkedAngle);
	}
	//invalid ignore
	if("NaN".equals(""+ikkedAngle.getX())){
		continue;
	}
	if("NaN".equals(""+ikkedAngle.getY())){
		continue;
	}
	if("NaN".equals(""+ikkedAngle.getZ())){
		continue;
	}
	
	
	if(boneLock.hasX(targetBoneName)){
		ikkedAngle.setX(Math.toRadians(boneLock.getX(targetBoneName)));	
	}
	
	if(boneLock.hasY(targetBoneName)){
		ikkedAngle.setY(Math.toRadians(boneLock.getY(targetBoneName)));	
	}
	if(boneLock.hasZ(targetBoneName)){
		ikkedAngle.setZ(Math.toRadians(boneLock.getZ(targetBoneName)));	
	}
	
	//log("after-limit:"+ThreeLog.get(GWTThreeUtils.radiantToDegree(angles)));
	Matrix4 newMatrix=GWTThreeUtils.rotationToMatrix4(ikkedAngle);
	
	newMatrix.multiply(translates,newMatrix);
	
	ab.getBoneAngleAndMatrix(boneIndex).setMatrix(newMatrix);
	ab.getBoneAngleAndMatrix(boneIndex).setAngle(GWTThreeUtils.radiantToDegree(ikkedAngle));
	
	
	//log(targetName+":"+ThreeLog.getAngle(jointRot)+",new"+ThreeLog.getAngle(newMatrix));
	//log("parentPos,"+ThreeLog.get(jointPos)+",lastPos,"+ThreeLog.get(lastJointPos));
	currentIkJointIndex++;
	if(currentIkJointIndex>=ikData.getBones().size()){
		currentIkJointIndex=0;
	}
	//tmp1=lastJointPos;
	//tmp2=jointPos;
	}
}

private void doPoseIkk(int index,boolean resetMatrix,int perLimit,IKData ikdata,int cddLoop){
		
	initializeBodyMesh();
	initializeAnimationData(index,resetMatrix);
	stepCDDIk(perLimit,ikdata,cddLoop);	
	doPoseByMatrix(ab);
	updateBoneRanges();
	
	}
private List<AngleAndPosition> findStartMatrix(String boneName,Vector3 targetPos) {
	List<AngleAndPosition> retMatrix=candiateAngleAndMatrixs.get(0);
	ab.setBonesAngleAndMatrixs(retMatrix);//TODO without set
	Vector3 tpos=ab.getBonePosition(boneName);
	double minlength=targetPos.clone().subSelf(tpos).length();
	for(int i=1;i<candiateAngleAndMatrixs.size();i++){
		List<AngleAndPosition> mxs=candiateAngleAndMatrixs.get(i);
		ab.setBonesAngleAndMatrixs(mxs);//TODO change
		Vector3 tmpPos=ab.getBonePosition(boneName);
		double tmpLength=targetPos.clone().subSelf(tmpPos).length();
		if(tmpLength<minlength){
			minlength=tmpLength;
			retMatrix=mxs;
		}
	}
	
	for(String name:getCurrentIkData().getBones()){
		//Matrix4 mx=retMatrix.get(ab.getBoneIndex(name));
	//	log(name+":"+ThreeLog.get(GWTThreeUtils.rotationToVector3(mx)));
	//	log(name+":"+ThreeLog.get(GWTThreeUtils.toDegreeAngle(mx)));
	}
	
	return retMatrix;
}

/*
private void doCDDIk(){
	
		String targetName=getCurrentIkData().getBones().get(currentIkJointIndex);
		int boneIndex=ab.getBoneIndex(targetName);
		Vector3 lastJointPos=ab.getPosition("RightFoot");
		Vector3 jointPos=ab.getParentPosition(targetName);
		Matrix4 jointRot=ab.getBoneMatrix(targetName);
		
		Matrix4 newMatrix=cddIk.doStep(lastJointPos, jointPos, jointRot, getCurrentIkData().getTargetPos());
		ab.setBoneMatrix(boneIndex, newMatrix);
		
		//log(targetName+":"+ThreeLog.getAngle(jointRot)+",new"+ThreeLog.getAngle(newMatrix));
		//log("parentPos,"+ThreeLog.get(jointPos)+",lastPos,"+ThreeLog.get(lastJointPos));
		currentIkJointIndex++;
		if(currentIkJointIndex>=getCurrentIkData().getBones().size()){
			currentIkJointIndex=0;
		}
		
		doPoseByMatrix(ab);
}
*/
CDDIK cddIk=new CDDIK();
	int currentIkJointIndex=0;
	//private String[] ikTestNames={"RightLeg","RightUpLeg"};
	
	//Vector3 targetPos=THREE.Vector3(-10, -3, 0);
	private ListBox boneNamesBox;
	
private void doPoseByMatrix(AnimationBonesData animationBonesData){
		
		
	List<AngleAndPosition> boneMatrix=animationBonesData.getBonesAngleAndMatrixs();
		
		bonePath=boneToPath(bones);
		if(bone3D!=null){
			root.remove(bone3D);
		}
		bone3D=THREE.Object3D();
		root.add(bone3D);
		
		//selection
		
		//test ikk
		/*
		Mesh cddIk0=THREE.Mesh(THREE.CubeGeometry(1.5, 1.5, 1.5),THREE.MeshLambertMaterial().color(0x00ff00).build());
		cddIk0.setPosition(getCurrentIkData().getTargetPos());
		bone3D.add(cddIk0);
		*/
		
		
		List<Matrix4> moveMatrix=new ArrayList<Matrix4>(); 
		List<Vector3> bonePositions=new ArrayList<Vector3>();
		for(int i=0;i<bones.length();i++){
			Matrix4 mv=boneMatrix.get(i).getMatrix();
			double bsize=.7;
			if(i==0){
				bsize=1.5;
			}
			Mesh mesh=THREE.Mesh(THREE.CubeGeometry(bsize,bsize, bsize),THREE.MeshLambertMaterial().color(0xff0000).build());
			bone3D.add(mesh);
			
			Vector3 pos=THREE.Vector3();
			pos.setPositionFromMatrix(boneMatrix.get(i).getMatrix());
			
			Vector3 rot=GWTThreeUtils.rotationToVector3(GWTThreeUtils.jsArrayToQuaternion(bones.get(i).getRotq()));
			
			List<Integer> path=bonePath.get(i);
			String boneName=bones.get(i).getName();
			//log(boneName);
			mesh.setName(boneName);
			
			
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
			
			//log(boneName+":"+ThreeLog.get(pos)+","+ThreeLog.get(ppos));	
			Mesh line=GWTGeometryUtils.createLineMesh(pos, ppos, 0xffffff);
			bone3D.add(line);
			
			//cylinder
			/* better bone faild
			Vector3 halfPos=pos.clone().subSelf(ppos).multiplyScalar(0.5).addSelf(ppos);
			Mesh boneMesh=THREE.Mesh(THREE.CylinderGeometry(.1,.1,.2,6), THREE.MeshLambertMaterial().color(0xffffff).build());
			boneMesh.setPosition(halfPos);
			boneMesh.setName(boneName);
			bone3D.add(boneMesh);
			
			BoxData data=boxDatas.get(boneName);
			if(data!=null){
				boneMesh.setScale(data.getScaleX(), data.getScaleY(), data.getScaleZ());
				boneMesh.getRotation().setZ(Math.toRadians(data.getRotateZ()));
			}
			*/
			
			for(IKData ik:ikdatas){
				if(ik.getLastBoneName().equals(boneName)){
					Mesh ikMesh=targetMeshs.get(boneName);
					
					if(ikMesh==null){//at first call this from non-ik stepped.
						//log("xxx");
						//initial
						Vector3 ikpos=pos.clone().subSelf(ppos).multiplyScalar(1.5).addSelf(ppos);
						//ikpos=pos.clone();
						ikMesh=THREE.Mesh(THREE.CubeGeometry(1.5, 1.5, 1.5),THREE.MeshLambertMaterial().color(0x00ff00).build());
						ikMesh.setPosition(ikpos);
						ikMesh.setName("ik:"+boneName);
					//	log(boneName+":"+ThreeLog.get(ikpos));
						//log(ThreeLog.get(pos));
						ik.getTargetPos().set(ikpos.getX(), ikpos.getY(), ikpos.getZ());
						targetMeshs.put(boneName, ikMesh);
						
					}else{
						ikMesh.getParent().remove(ikMesh);
					}
					bone3D.add(ikMesh);
					ikMesh.setPosition(ik.getTargetPos());
					Mesh ikline=GWTGeometryUtils.createLineMesh(pos, ik.getTargetPos(), 0xffffff);
					bone3D.add(ikline);
				}
			}
			
			}
			mesh.setRotation(rot);
			mesh.setPosition(pos);
			
			//mesh color
			if(pos.getY()<0){
				mesh.getMaterial().setColor(THREE.Color(0xffee00));//over color
			}else if(pos.getY()<1){
				mesh.getMaterial().setColor(THREE.Color(0xff8800));//over color
			}
			
			bonePositions.add(pos);
		}
		
		//Geometry geo=GeometryUtils.clone(baseGeometry);
		

		
		
		//Geometry geo=bodyMesh.getGeometry();
		Geometry geo=GeometryUtils.clone(baseGeometry);
		
		log("bi-length:"+bodyIndices.length());
		
		for(int i=0;i<baseGeometry.vertices().length();i++){
			Vertex baseVertex=baseGeometry.vertices().get(i);
			Vector3 vertexPosition=baseVertex.getPosition().clone();
			
			
			Vertex targetVertex=geo.vertices().get(i);
			
			int boneIndex1=(int) bodyIndices.get(i).getX();
			int boneIndex2=(int) bodyIndices.get(i).getY();
			String name=animationBonesData.getBoneName(boneIndex1);
			log(boneIndex1+"x"+boneIndex2);
			
			/*
			 * 
			if(name.equals("RightLeg")){//test parent base
				Vector3 parentPos=animationBonesData.getBaseParentBonePosition(boneIndex1);
				Matrix4 tmpMatrix=GWTThreeUtils.rotationToMatrix4(GWTThreeUtils.degreeToRagiant(THREE.Vector3(0, 0, 20)));
				vertexPosition.subSelf(parentPos);
				tmpMatrix.multiplyVector3(vertexPosition);
				vertexPosition.addSelf(parentPos);
				boneIndex2=boneIndex1; //dont work without this
			}*/
			
			Vector3 bonePos=animationBonesData.getBaseBonePosition(boneIndex1);
			Vector3 relatePos=bonePos.clone();
			relatePos.sub(vertexPosition,bonePos);
			//double length=relatePos.length();
			
			
			
			moveMatrix.get(boneIndex1).multiplyVector3(relatePos);
			/*
			
			if(name.equals("RightLeg")){
				Vector3 parentPos=animationBonesData.getParentPosition(boneIndex1);
				relatePos.subSelf(parentPos);
				Matrix4 tmpMatrix2=GWTThreeUtils.rotationToMatrix4(GWTThreeUtils.degreeToRagiant(THREE.Vector3(0, 0, -20)));
				tmpMatrix2.multiplyVector3(relatePos);
				relatePos.addSelf(parentPos);
			}*/
			
			//relatePos.addSelf(bonePos);
			if(boneIndex2!=boneIndex1){
				Vector3 bonePos2=animationBonesData.getBaseBonePosition(boneIndex2);
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
			
			//log("body mesh initialize"); //called by doPose();
			/*
			bodyIndices = (JsArray<Vector4>) JsArray.createArray();
			bodyWeight = (JsArray<Vector4>) JsArray.createArray();
			WeightBuilder.autoWeight(baseGeometry, bones, 2, bodyIndices, bodyWeight);	//3 is not good
			*/
			}else{
				root.remove(bodyMesh);
			}
		
		bodyMesh=THREE.Mesh(geo, bodyMaterial);
		root.add(bodyMesh);
		
		
		//selection
		//selectionMesh=THREE.Mesh(THREE.CubeGeometry(2, 2, 2), THREE.MeshBasicMaterial().color(0x00ff00).wireFrame(true).build());
		if(isSelectedIk()){
		selectionMesh.setPosition(getCurrentIkData().getTargetPos());
		}
		//bone3D.add(selectionMesh);
		//selectionMesh.setVisible(false);
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


private Map<String,Mesh> targetMeshs=new HashMap<String,Mesh>();
private ListBox rotateAndPosList;
private VerticalPanel bonePositionsPanel;
private VerticalPanel boneRotationsPanel;
private CheckBox zlockCheck;
private CheckBox ikLockCheck;
private ListBox fileNames;
private StorageControler preference;
private VerticalPanel datasPanel;
private Button saveButton;

/**
 * @deprecated
 */
	private void doPose(List<MatrixAndVector3> boneMatrix){
		
		
		
		bonePath=boneToPath(bones);
		if(bone3D!=null){
			root.remove(bone3D);
		}
		bone3D=THREE.Object3D();
		root.add(bone3D);
		
		//test ikk
		Mesh cddIk0=THREE.Mesh(THREE.CubeGeometry(.5, .5, .5),THREE.MeshLambertMaterial().color(0x00ff00).build());
		cddIk0.setPosition(getCurrentIkData().getTargetPos());
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
	@Override
	public String getHtml(){
	String html=super.getHtml();

	return html;	
	}

	@Override
	public String getTabTitle() {
		return "Editor";
	}
}
