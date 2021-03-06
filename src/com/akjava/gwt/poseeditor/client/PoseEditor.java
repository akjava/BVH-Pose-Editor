package com.akjava.gwt.poseeditor.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.akjava.bvh.client.BVH;
import com.akjava.bvh.client.BVHMotion;
import com.akjava.bvh.client.BVHNode;
import com.akjava.bvh.client.BVHParser;
import com.akjava.bvh.client.BVHParser.InvalidLineException;
import com.akjava.bvh.client.BVHParser.ParserListener;
import com.akjava.bvh.client.BVHWriter;
import com.akjava.gwt.bvh.client.poseframe.PoseEditorData;
import com.akjava.gwt.bvh.client.poseframe.PoseFrameData;
import com.akjava.gwt.bvh.client.threejs.AnimationBoneConverter;
import com.akjava.gwt.bvh.client.threejs.AnimationDataConverter;
import com.akjava.gwt.bvh.client.threejs.BVHConverter;
import com.akjava.gwt.html5.client.InputRangeListener;
import com.akjava.gwt.html5.client.InputRangeWidget;
import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.extra.HTML5Builder;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.file.ui.DropVerticalPanelBase;
import com.akjava.gwt.jsgif.client.GifAnimeBuilder;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.IStorageControler;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.JsonValueUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.lib.client.datalist.SimpleTextData;
import com.akjava.gwt.poseeditor.client.PreferenceTabPanel.PreferenceListener;
import com.akjava.gwt.poseeditor.client.resources.PoseEditorBundles;
import com.akjava.gwt.three.client.examples.renderers.Projector;
import com.akjava.gwt.three.client.examples.utils.GeometryUtils;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.gwt.JSONModelFile;
import com.akjava.gwt.three.client.gwt.JSParameter;
import com.akjava.gwt.three.client.gwt.boneanimation.AngleAndPosition;
import com.akjava.gwt.three.client.gwt.boneanimation.AnimationBone;
import com.akjava.gwt.three.client.gwt.boneanimation.AnimationBonesData;
import com.akjava.gwt.three.client.gwt.boneanimation.AnimationData;
import com.akjava.gwt.three.client.gwt.boneanimation.AnimationHierarchyItem;
import com.akjava.gwt.three.client.gwt.boneanimation.AnimationKey;
import com.akjava.gwt.three.client.gwt.boneanimation.BoneLimit;
import com.akjava.gwt.three.client.gwt.boneanimation.NameAndVector3;
import com.akjava.gwt.three.client.gwt.boneanimation.ik.CDDIK;
import com.akjava.gwt.three.client.gwt.boneanimation.ik.IKData;
import com.akjava.gwt.three.client.gwt.core.BoundingBox;
import com.akjava.gwt.three.client.gwt.core.Intersect;
import com.akjava.gwt.three.client.gwt.materials.MeshBasicMaterialParameter;
import com.akjava.gwt.three.client.gwt.materials.MeshLambertMaterialParameter;
import com.akjava.gwt.three.client.java.GWTDragObjectControler;
import com.akjava.gwt.three.client.java.ThreeLog;
import com.akjava.gwt.three.client.java.animation.WeightBuilder;
import com.akjava.gwt.three.client.java.ui.SimpleTabDemoEntryPoint;
import com.akjava.gwt.three.client.java.utils.GWTGeometryUtils;
import com.akjava.gwt.three.client.java.utils.GWTThreeUtils;
import com.akjava.gwt.three.client.java.utils.GWTThreeUtils.InvalidModelFormatException;
import com.akjava.gwt.three.client.java.utils.Object3DUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.animation.AnimationClip;
import com.akjava.gwt.three.client.js.animation.AnimationMixer;
import com.akjava.gwt.three.client.js.animation.KeyframeTrack;
import com.akjava.gwt.three.client.js.animation.tracks.QuaternionKeyframeTrack;
import com.akjava.gwt.three.client.js.animation.tracks.VectorKeyframeTrack;
import com.akjava.gwt.three.client.js.cameras.Camera;
import com.akjava.gwt.three.client.js.core.Clock;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.core.Object3D;
import com.akjava.gwt.three.client.js.extras.ImageUtils;
import com.akjava.gwt.three.client.js.extras.geometries.BoxGeometry;
import com.akjava.gwt.three.client.js.extras.helpers.GridHelper;
import com.akjava.gwt.three.client.js.extras.helpers.SkeletonHelper;
import com.akjava.gwt.three.client.js.lights.Light;
import com.akjava.gwt.three.client.js.loaders.JSONLoader.JSONLoadHandler;
import com.akjava.gwt.three.client.js.materials.Material;
import com.akjava.gwt.three.client.js.math.Euler;
import com.akjava.gwt.three.client.js.math.Matrix4;
import com.akjava.gwt.three.client.js.math.Quaternion;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.math.Vector4;
import com.akjava.gwt.three.client.js.objects.Group;
import com.akjava.gwt.three.client.js.objects.Line;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.akjava.gwt.three.client.js.renderers.WebGLRenderer;
import com.akjava.gwt.three.client.js.scenes.Scene;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.akjava.lib.common.utils.ColorUtils;
import com.akjava.lib.common.utils.FileNames;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PoseEditor extends SimpleTabDemoEntryPoint implements PreferenceListener{
	public static Logger logger = Logger.getLogger(PoseEditor.class.getName());
	private BVH bvh;
	protected JsArray<AnimationBone> animationBones;
	private AnimationData animationData;
	public static DateTimeFormat dateFormat=DateTimeFormat.getFormat("yy/MM/dd HH:mm");
	private String version="7.0(for three.r74)";
	
	private static boolean debug;

	private static final String KEY_TRANSPARENT="poseeditor_key_transparent";
	private static final String KEY_BASIC_MATERIAL="poseeditor_key_basicmaterial";
	@Override
	protected void beforeUpdate(WebGLRenderer renderer) {
		
		if(rootObject!=null){
			/*
			root.getScale().set(upscale,upscale,upscale);
			
			
			if(bone3D!=null){
				//bone3D.getScale().set(upscale,upscale,upscale);
				double itemScale=(1.0/upscale);
				for(int i=0;i<bone3D.getChildren().length();i++){
					if(bone3D.getChildren().get(i) instanceof Mesh){
						bone3D.getChildren().get(i).getScale().set(itemScale,itemScale,itemScale);	
					}
					
				}
			}
			*/
			//usually positionZRange is zero ,controlled by camera pos
			setRootPositionByRange(positionXRange.getValue(),positionYRange.getValue(),positionZRange.getValue());
			rootObject.getRotation().set(Math.toRadians(rotationXRange.getValue()),Math.toRadians(rotationYRange.getValue()),Math.toRadians(rotationZRange.getValue()),Euler.XYZ);
			
			
			//camera rotation style
			//cameraHolder.setPosition((double)positionXRange.getValue()/10, (double)positionYRange.getValue()/10, (double)positionZRange.getValue()/10);
			//cameraHolder.getRotation().set(Math.toRadians(rotationXRange.getValue()),-Math.toRadians(rotationYRange.getValue()),Math.toRadians(rotationZRange.getValue()));
			//camera.lookAt(zero);
			//camera.getPosition().set(cameraX, cameraY, cameraZ);
			
			/*
			Vector3 newPos=THREE.Vector3(0, 0, 100);
			
			Quaternion prev=GWTThreeUtils.degreeRotationQuaternion(THREE.Vector3(rotationXRange.getValue(),0,0));
			Quaternion next=GWTThreeUtils.degreeRotationQuaternion(THREE.Vector3(rotationXRange.getValue(),180,0));
			
			Quaternion result=THREE.Quaternion();
			double scale=1.0/360*rotationYRange.getValue() ;
			//log("scale:"+scale);
			THREE.Quaternion().slerp( prev, next, result, scale);
			
			Matrix4 mx=GWTThreeUtils.rotationToMatrix4(result);
			//log("newangle:"+GWTThreeUtils.rotationToDegreeVector3(mx));
			mx.multiplyVector3(newPos);
			*/
			//newPos=GWTThreeUtils.rotationToVector3(result);
			//newPos=GWTThreeUtils.degreeToRagiant(newPos);
			
			
			
		}
	}
	
	public void setRootPositionRangeValues(int x,int y,int z){
		positionXRange.setValue(x);
		positionYRange.setValue(y);
		
		setCameraZ((double)z/10);//z is special
		//positionZRange.setValue(z);
	}
	
	public void setRootPositionByRange(int x,int y,int z){
		rootObject.setPosition((double)x/posDivided, (double)y/posDivided, (double)z/posDivided);
	}
	public int getRootPositionXRange(){
		return positionXRange.getValue();
	}
	public int getRootPositionYRange(){
		return positionYRange.getValue();
	}
	
	/*right now ze not used
	public int getRootPositionZRange(){
		return positionZRange.getValue();
	}
	*/
	
	
	private Object3D cameraHolder;
	@Override
	protected void updateCamera(Scene scene,int width,int height){
		if(cameraHolder==null){
			cameraHolder=THREE.Object3D();
			scene.add(cameraHolder);
		}
		if(camera!=null){
			//TODO find update way.
			cameraHolder.remove(camera);
			camera=null;
		}
		Camera camera = THREE.PerspectiveCamera(45,(double)width/height,0.001,600);
		//camera.getPosition().set(0, 0, cameraZ);
		cameraHolder.add(camera); //some kind of trick.
		this.camera=camera;
	}
	
	Clock clock;
	public void render(){
		renderer.clear();
		if(mixer!=null){
			mixer.update(clock.getDelta());
		}
		if(skeltonHelper!=null){
			skeltonHelper.update();
		}
		
		//some how this way not work,I'll do with scene2
		
		renderer.render(scene, camera);
		
		
	}
	
	@Override
	public void update(WebGLRenderer renderer) {
		
		if(isUsingRenderer){//create gifanime or video
			return;
		}
		
		beforeUpdate(renderer);
		camera.getPosition().set(cameraX, cameraY, cameraZ);
		//LogUtils.log("camera:"+ThreeLog.get(camera.getPosition()));
		
		render();
		
		//it's better to do in render update-loop
		if(reservedScreenshot){
			doScreenShot(renderer);
			reservedScreenshot=false;
		}
		
		if(reservedCreateGifAnime){
			doGifAnime();
			reservedCreateGifAnime=false;
		}
		
		if(reservedSettingPreview){
			
			updateBackgroundVisible(settingPanel.isPreviewGifShowBackground());
			updateBonesVisible(settingPanel.isPreviewGifShowBone());
			updateIKVisible(settingPanel.isPreviewGifShowIk());
			
			renderer.render(scene, camera);
			String url=canvas.getRenderer().gwtPngDataUrl();
			settingPanel.setPreviewImage(url);
			
			updateBackgroundVisible(showBackgroundCheck.getValue());
			updateBonesVisible(showBonesCheck.getValue());
			updateIKVisible(ikVisibleCheck.getValue());
			
			reservedSettingPreview=false;
		}
	}
	
	

	public boolean isReservedSettingPreview() {
		return reservedSettingPreview;
	}

	public void setReservedSettingPreview(boolean reservedSettingPreview) {
		this.reservedSettingPreview = reservedSettingPreview;
	}

	public void selectMainTab(){
		tabPanel.selectTab(0);
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
	
	public class ContextMenu implements ContextMenuHandler{

		@Override
		public void onContextMenu(ContextMenuEvent event) {
			 event.preventDefault();
			 event.stopPropagation();
			 showContextMenu(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());
		}
		
	}
	private GWTDragObjectControler dragObjectControler;
	
	public static class Logger {
		boolean enabled=false;
		public static Logger getLogger(String name){
			return new Logger();
		}
		public void fine(String log){
			if(enabled){
				LogUtils.log(log);
			}
		}
		public void info(String log) {
			if(enabled){
			LogUtils.log(log);
			}
		}
		
		
	}
	
	
	private BoneLimit oppositeRL(BoneLimit baseLimit){
		BoneLimit limit=new BoneLimit();
		limit.setMinXDegit(baseLimit.getMinXDegit());
		limit.setMaxXDegit(baseLimit.getMaxXDegit());
		
		limit.setMinYDegit(baseLimit.getMaxYDegit()*-1);
		limit.setMaxYDegit(baseLimit.getMinYDegit()*-1);
		
		limit.setMinZDegit(baseLimit.getMaxZDegit()*-1);
		limit.setMaxZDegit(baseLimit.getMinZDegit()*-1);
		
		limit.setMinX(Math.toRadians(limit.getMinXDegit()));
		limit.setMaxX(Math.toRadians(limit.getMaxXDegit()));
		limit.setMinY(Math.toRadians(limit.getMinYDegit()));
		limit.setMaxY(Math.toRadians(limit.getMaxYDegit()));
		limit.setMinZ(Math.toRadians(limit.getMinZDegit()));
		limit.setMaxZ(Math.toRadians(limit.getMaxZDegit()));
		return limit;
	}
	
	private void createIKAndLimitBone(){
		//TODO load from file
		
		//bone limit is very important otherwise ik really slow
		
		//make human1.0 bones
		
		//head ik
		
		
				//this is for rese pose and not work correctly
		/*
				ikdatas.add(createIKData(Lists.newArrayList("head","neck","chest","abdomen"),9));
				boneLimits.put("abdomen",BoneLimit.createBoneLimit(-30, 30, -60, 60, -30, 30));
				boneLimits.put("chest",BoneLimit.createBoneLimit(-30, 30, -40, 40, -40, 40));
				boneLimits.put("neck",BoneLimit.createBoneLimit(-34, 34, -34, 34, -34, 34));
				
				//righ-hands,now modifiing
				ikdatas.add(createIKData(Lists.newArrayList("rHand","rForeArm","rShldr","rCollar"),9));
				boneLimits.put("rForeArm",BoneLimit.createBoneLimit(-30, 60, -60, 90, 0,0));
				boneLimits.put("rShldr",BoneLimit.createBoneLimit(-90, 60, -75, 80, -120, 60));
				boneLimits.put("rCollar",BoneLimit.createBoneLimit(0,0,-20,0,-40,0));
				
				//left-hand
				ikdatas.add(createIKData(Lists.newArrayList("lHand","lForeArm","lShldr","lCollar"),9));
				boneLimits.put("lForeArm",oppositeRL(boneLimits.get("rForeArm")));
				boneLimits.put("lShldr",oppositeRL(boneLimits.get("rShldr")));
				boneLimits.put("lCollar",oppositeRL(boneLimits.get("rCollar")));

				//right leg
				ikdatas.add(createIKData(Lists.newArrayList("rFoot","rShin","rThigh"),5));
				boneLimits.put("rShin",BoneLimit.createBoneLimit(0, 160, 0, 0, -0, 0));
				boneLimits.put("rThigh",BoneLimit.createBoneLimit(-90, 90, -30, 30, -60, 45));

				
				ikdatas.add(createIKData(Lists.newArrayList("lFoot","lShin","lThigh"),5));
				boneLimits.put("lShin",oppositeRL(boneLimits.get("rShin")));
				boneLimits.put("lThigh",oppositeRL(boneLimits.get("rThigh")));
			*/
				
				
				
				
				
				
				
				
			
		//mbl3d		
				
		ikdatas.add(createIKData(Lists.newArrayList("head","neck","spine03","spine02","spine01"),9));//,"spline03","spline02","spline01"
		boneLimits.put("neck",BoneLimit.createBoneLimit(-30, 30, 0,0, -30, 30));
		boneLimits.put("spine03",BoneLimit.createBoneLimit(-30, 30, 0, 0, -30, 30));
		boneLimits.put("spine02",BoneLimit.createBoneLimit(-30, 30, 0, 0, -30, 30));
		boneLimits.put("spine01",BoneLimit.createBoneLimit(-30, 30, 0, 0, -30, 30));
		
		ikdatas.add(createIKData(Lists.newArrayList("hand_R","lowerarm_R","upperarm_R","clavicle_R"),9));
		boneLimits.put("lowerarm_R",BoneLimit.createBoneLimit(-30, 15, 0, 140, 0, 0));
		boneLimits.put("upperarm_R",BoneLimit.createBoneLimit(-90, 80, -60, 91, -20, 100));
		boneLimits.put("clavicle_R",BoneLimit.createBoneLimit(0,0,-20,10,-60,0));
		
		ikdatas.add(createIKData(Lists.newArrayList("hand_L","lowerarm_L","upperarm_L","clavicle_L"),9));
		boneLimits.put("lowerarm_L",oppositeRL(boneLimits.get("lowerarm_R")));
		boneLimits.put("upperarm_L",oppositeRL(boneLimits.get("upperarm_R")));
		boneLimits.put("clavicle_L",oppositeRL(boneLimits.get("clavicle_R")));
		
		
		ikdatas.add(createIKData(Lists.newArrayList("foot_R","calf_R","thigh_R"),9));
		boneLimits.put("calf_R",BoneLimit.createBoneLimit(0, 160, 0, 0, 0, 0));
		boneLimits.put("thigh_R",BoneLimit.createBoneLimit(-120, 60, -35, 5, -80, 40));
		
		ikdatas.add(createIKData(Lists.newArrayList("foot_L","calf_L","thigh_L"),9));
		boneLimits.put("calf_L",oppositeRL(boneLimits.get("calf_R")));
		boneLimits.put("thigh_L",oppositeRL(boneLimits.get("thigh_R")));
		
		//head
		
		/*
		ikdatas.add(createIKData(Lists.newArrayList("head","neck","chest","abdomen"),9));
		boneLimits.put("abdomen",BoneLimit.createBoneLimit(-30, 30, -60, 60, -30, 30));
		boneLimits.put("chest",BoneLimit.createBoneLimit(-30, 30, -40, 40, -40, 40));//chest not stable if have more Y
		boneLimits.put("neck",BoneLimit.createBoneLimit(-34, 34, -34, 34, -34, 34));
		
		//righ-hand
		ikdatas.add(createIKData(Lists.newArrayList("rHand","rForeArm","rShldr","rCollar"),9));
		boneLimits.put("rForeArm",BoneLimit.createBoneLimit(0, 0, 0, 140, 0, 0));
		boneLimits.put("rShldr",BoneLimit.createBoneLimit(-80, 60, -60, 91, -40, 100));
		boneLimits.put("rCollar",BoneLimit.createBoneLimit(0,0,-20,0,-40,0));
		
		//left-hand
		ikdatas.add(createIKData(Lists.newArrayList("lHand","lForeArm","lShldr","lCollar"),9));
		boneLimits.put("lForeArm",oppositeRL(boneLimits.get("rForeArm")));
		boneLimits.put("lShldr",oppositeRL(boneLimits.get("rShldr")));
		boneLimits.put("lCollar",oppositeRL(boneLimits.get("rCollar")));

		//right leg
		ikdatas.add(createIKData(Lists.newArrayList("rFoot","rShin","rThigh"),5));
		boneLimits.put("rShin",BoneLimit.createBoneLimit(0, 160, 0, 0, 0, 0));
		boneLimits.put("rThigh",BoneLimit.createBoneLimit(-120, 60, -35, 5, -80, 40));

		
		ikdatas.add(createIKData(Lists.newArrayList("lFoot","lShin","lThigh"),5));
		boneLimits.put("lShin",oppositeRL(boneLimits.get("rShin")));
		boneLimits.put("lThigh",oppositeRL(boneLimits.get("rThigh")));
		
		
				
		
				
				
				
				//old datas,right now just catch critibal bug
				IKData ikdata3=new IKData("LeftUpLeg-LeftLeg");
				//ikdata.setTargetPos(THREE.Vector3(0, -10, 0));
				ikdata3.setLastBoneName("LeftFoot");
				ikdata3.setBones(new String[]{"LeftLeg","LeftUpLeg"});
				ikdata3.setIteration(5);
				ikdatas.add(ikdata3);
				
				
				
				boneLimits.put("RightForeArm",BoneLimit.createBoneLimit(-40, 10, 0, 140, -30, 10));
				boneLimits.put("RightArm",BoneLimit.createBoneLimit(-80, 60, -75, 91, -70, 115));
				
				boneLimits.put("LeftForeArm",BoneLimit.createBoneLimit(-40, 10, -140, 0, -10, 30));
				boneLimits.put("LeftArm",BoneLimit.createBoneLimit(-80, 60, -91, 75, -115, 70));

				
				boneLimits.put("RightLeg",BoneLimit.createBoneLimit(0, 160, 0, 0, 0, 20));
				boneLimits.put("RightUpLeg",BoneLimit.createBoneLimit(-120, 60, -35, 5, -80, 40));
				
				
				boneLimits.put("LeftLeg",BoneLimit.createBoneLimit(0, 160, 0, 0, -20, 0));
				boneLimits.put("LeftUpLeg",BoneLimit.createBoneLimit(-120, 60, -5, 35, -40, 80));
				
				
				boneLimits.put("LowerBack",BoneLimit.createBoneLimit(-30, 30, -60, 60, -30, 30));
				boneLimits.put("Spine",BoneLimit.createBoneLimit(-30, 30, -40, 40, -40, 40));
				//boneLimits.put("Spine1",BoneLimit.createBoneLimit(-30, 30, -30, 30, -30, 30));
				boneLimits.put("Neck",BoneLimit.createBoneLimit(-29, 29, -29, 29, -29, 29));
				boneLimits.put("Neck1",BoneLimit.createBoneLimit(-5, 5, -5, 5, -5, 5));
				*/
	}
	
	private Scene scene2;
	public static PoseEditor poseEditor;
	@Override
	protected void initializeOthers(WebGLRenderer renderer) {
		
		scene2=THREE.Scene();
		
		renderer.setAutoClear(false);
		
		poseEditor=this;
		cameraZ=500/posDivided;
		
		
		clock=THREE.Clock();
		
		this.renderer=renderer;
		
		canvas.addDomHandler(new ContextMenu(), ContextMenuEvent.getType());
		
		storageControler = new StorageControler();
		
		this.renderer=renderer;
		
		//maybe canvas is transparent
		
		
		
		
		//renderer has already setted 0x333333.this is somekind confirm?
		
		//canvas.setClearColorHex(0x333333);//qustion,what happen if no canvas.
		
		//recently i feel this is good,less flick and 
		renderer.setClearColor(0, 0);
		canvas.getElement().getStyle().setBackgroundColor("#333");
		
		dragObjectControler=new GWTDragObjectControler(scene,projector);
		
		
		//scene.add(THREE.AmbientLight(0xffffff));
		
		Light pointLight = THREE.DirectionalLight(0xffffff,1);
		pointLight.setPosition(0, 10, 300);
		scene.add(pointLight);
		
		Light pointLight2 = THREE.DirectionalLight(0xffffff,1);//for fix back side dark problem
		pointLight2.setPosition(0, 10, -300);
		//scene.add(pointLight2);
		
		rootObject=THREE.Object3D();
		scene.add(rootObject);
		
		
		group1 = THREE.Group();
		group2 = THREE.Group();
		rootObject.add(group1);
		rootObject.add(group2);
		
		
		//background;
		Geometry geo=THREE.PlaneGeometry(1000/posDivided*2, 1000/posDivided*2,20,20);
		
		planeMesh = THREE.Mesh(geo, THREE.MeshBasicMaterial(GWTParamUtils.MeshBasicMaterial().color(0xaaaaaa).side(THREE.DoubleSide)
				.transparent(true).opacity(0.5)));
		group1.add(planeMesh);
		planeMesh.getRotation().set(Math.toRadians(-90), 0, 0);
		
		
		int size=1000/posDivided;
		int step=size/20;
		step=Math.max(1, step);
		
		backgroundGrid = THREE.GridHelper(1000/posDivided, step);
		group1.add(backgroundGrid);
		
		//line removed,because of flicking
		//Object3D xline=GWTGeometryUtils.createLineMesh(THREE.Vector3(-50, 0, 0.001), THREE.Vector3(50, 0, 0.001), 0x880000,3);
		//root.add(xline);
		
		//Object3D zline=GWTGeometryUtils.createLineMesh(THREE.Vector3(0, 0, -50), THREE.Vector3(0, 0, 50), 0x008800,3);
		//root.add(zline);
		
		double selectionSize=baseBoneCoreSize*2.5/posDivided;
		
		selectionMesh=THREE.Mesh(THREE.BoxGeometry(selectionSize,selectionSize,selectionSize), THREE.MeshBasicMaterial(
				GWTParamUtils.MeshBasicMaterial().color(0x00ff00).wireframe(true)
				));
		
		group2.add(selectionMesh);
		selectionMesh.setVisible(false);
		
		
		createIKAndLimitBone();
		//line flicked think something
		
		
		
		//delay make problem
		//loadBVH("pose.bvh");//initial bone
		
		/*
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
		*/
		
		//
		
		
		
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
		
		
		
		
		//for initialize texture
		texture=ImageUtils.loadTexture("blondhair_tshirt.png");//initial one   //TODO change this.
		//generateTexture();
		
		//initial model to avoid async use clientbundle same as "tpose.bvh"
		parseInitialBVHAndLoadModels(PoseEditorBundles.INSTANCE.pose().getText());
		
		//model is loaded usually -1 index in modelName.txt on Bundles.
		
		
		createTabs();
		
		updateDatasPanel();
		
		
		//
		addShortcuts();
		
	}
	
	private void addShortcuts() {
		
		canvas.addKeyDownHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode()==KeyCodes.KEY_SHIFT || event.getNativeKeyCode()==KeyCodes.KEY_ALT){
					return;//ignore them.
				}
				
				if(event.getNativeKeyCode()==KeyCodes.KEY_ENTER){
					
					if(bone3D!=null && bone3D.getChildren().length()>0){
						selectBone(bone3D.getChildren().get(0),0,0,false);
					}
					
				}else if(event.getNativeKeyCode()==KeyCodes.KEY_PAGEUP){
					doPrevFrame();
				}else if(event.getNativeKeyCode()==KeyCodes.KEY_PAGEDOWN){
					doNextFrame();
				}else if(event.getNativeKeyCode()==KeyCodes.KEY_HOME){
					doFirstFrame();
				}else if(event.getNativeKeyCode()==KeyCodes.KEY_TAB){
					loopTabSelection(event.isShiftKeyDown());//shift key has problem
				}else{
					int code=event.getNativeKeyCode();
					if(code==45){//Add last
						insertFrame(getSelectedPoseEditorData().getPoseFrameDatas().size(),false);
					}else if(code==32){//space
						touchGroundZero();
					}else{
						//LogUtils.log(event.getNativeKeyCode());
					}
					
				}
				
			}
		});
		
	}

	
	/**
	 * bone mesh's name is same as bone name;
	 * @param boneName
	 * @return
	 */
	private Mesh getBoneMesh(String boneName){
		if(bone3D==null){
			return null;
		}
		for(int i=0;i<bone3D.getChildren().length();i++){
			if(bone3D.getChildren().get(i).getName().equals(boneName)){
				return (Mesh)bone3D.getChildren().get(i);
			}
		}
		return null;
	}
	
	protected void loopTabSelection(boolean shiftKeyDown) {
		if(isSelectedIk()){
			List<IKData> ikLists=Lists.newArrayList(getAvaiableIkdatas());
			int index=ikLists.indexOf(getCurrentIkData());
			
			if(index==-1){
				LogUtils.log("loopTabSelection() invalid ik selected:"+getCurrentIkData());
				return;
			}
			if(shiftKeyDown){
				index--;
				if(index<0){
					index=ikLists.size()-1;
				}
			}else{
				index++;
				if(index>=ikLists.size()){
					index=0;
				}
			}
			
			Object3D object=getIkObject3D(ikLists.get(index));
			selectIk(object, 0, 0,false);
			
		}else if(isSelectedBone()){
			int index=boneList.indexOf(selectedBone);
			LogUtils.log("loop:"+index);
			if(index==-1){
				LogUtils.log("loopTabSelection() invalid bone selected:"+selectedBone);
				return;
			}
			if(shiftKeyDown){
				index--;
				if(index<0){
					index=boneList.size()-1;
				}
			}else{
				index++;
				if(index>=boneList.size()){
					index=0;
				}
			}
			
			String newName=boneList.get(index);
			
			Mesh boneMesh=getBoneMesh(newName);
			if(boneMesh==null){
				LogUtils.log("loopTabSelection() boneMesh not exist:"+newName);
				return;
			}
			selectBone(boneMesh,0,0,false);
			
			//loop bone
		}else{
			for(IKData ik:getAvaiableIkdatas()){
				Object3D object=getIkObject3D(ik);
				selectIk(object, 0, 0,false);
				break;
			}
			//List<IKData> iks=Lists.newArrayList(getAvaiableIkdatas());
		}	
	}
	
	private Object3D getIkObject3D(IKData ik){
		for(int i=0;i<ik3D.getChildren().length();i++){
			Object3D object=ik3D.getChildren().get(i);
			if(object.getName().equals("ik:"+ik.getLastBoneName())){
				return object;
			}
		}
		return null;
	}
	

	private 	IKData createIKData(List<String> names,int iteration){
		List<String> boneNames=Lists.newArrayList(names);
		String last=boneNames.remove(0);//something name is strange
		IKData mhikdata3=new IKData(Joiner.on("-").join(boneNames));
		//ikdata.setTargetPos(THREE.Vector3(0, -10, 0));
		mhikdata3.setLastBoneName(last);
		mhikdata3.setBones(boneNames.toArray(new String[0]));//what is this?
		mhikdata3.setIteration(iteration);//what is this?
		return mhikdata3;
	}
	
	
	private int defaultOffSetY=-40;
	
	private void updateDatasPanel(){
		datasPanel.clear();
		try{
		int index=storageControler.getValue(KEY_INDEX, 0);
		for(int i=index;i>=0;i--){
			//String b64=storageControler.getValue(KEY_IMAGE+i,null);
			String json=storageControler.getValue(KEY_DATA+i,null);
			String head=storageControler.getValue(KEY_HEAD+i,null);
			if(json!=null){
			DataPanel dp=new DataPanel(i,head,null,json);
			//dp.setSize("200px", "200px");
			datasPanel.add(dp);
			}
		}
		}catch (StorageException e) {
			LogUtils.log("updateDatasPanel faild:"+e.getMessage());
		}
	}
	
	public class DataPanel extends HorizontalPanel{
		private int index;
		private String name;
		private long cdate;
		private String json;
		public DataPanel(final int dataIndex,String head,String base64, String text){
			this.setSpacing(4);
			json=text;
			this.index=dataIndex;
			
			//right now stop using image.
			Image img=new Image();
			if(base64!=null){
			img.setUrl(base64);
			}else{
				img.setVisible(false);
			}
			
			this.setVerticalAlignment(ALIGN_MIDDLE);
			
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
					int loadedIndex=isLoaded(dataIndex);
					LogUtils.log("loadedIndex:"+loadedIndex);
					if(loadedIndex!=-1){
						//if already exist remove from list & alwasy recrete.because possiblly model's bone is alway difference.
						poseEditorDatas.remove(loadedIndex);
						LogUtils.log("old data is removed");
					}
					PoseEditorData ped=PoseEditorData.readData(json);
					
					
					if(ped!=null){
					ped.setFileId(dataIndex);
					doLoad(ped);
					}else{
						//TODO error catch
						Window.alert("load faild");
					}
				}
			});
			
			Button cloneBt=new Button("Clone");
			add(cloneBt);
			cloneBt.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					doClone(json);
				}
			});
			
			Button editBt=new Button("Edit Name");
			add(editBt);
			editBt.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					
					
					JSONValue jsonValue=JSONParser.parseStrict(json);
					JSONObject ped=jsonValue.isObject();
					if(ped!=null){
						String newName=Window.prompt("Change Name",name);
						//ped.setName(newName);
						
						name=newName;
						ped.put("name", new JSONString(name));
						json=ped.toString();
						nameLabel.setText(name);
						//JSONObject data=PoseEditorData.writeData(ped);
						try{
						storageControler.setValue(KEY_DATA+index, json);
						storageControler.setValue(KEY_HEAD+index, name+"\t"+cdate);
						}catch (StorageException e) {
							LogUtils.log("Edit name faild:"+e.getMessage());
						}
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
			//add(exportBt); //stop support ,TODO replace json
			exportBt.addClickHandler(new ClickHandler() {
				
				private Anchor anchor;

				@Override
				public void onClick(ClickEvent event) {
					PoseEditorData ped=PoseEditorData.readData(json);
					String bvhText=convertBVHText(ped);
					if(anchor!=null){
						anchor.removeFromParent();
					}
					HTML5Download html5=new HTML5Download();
					anchor = html5.generateTextDownloadLink(bvhText, nameLabel.getText()+".bvh", "Click to download",true);
					add(anchor);
					
				}
			});
			
			
			Button rawBt=new Button("Raw Json");
			//add(rawBt);	//this is for debug
			rawBt.addClickHandler(new ClickHandler() {
				
				private Anchor anchor;

				@Override
				public void onClick(ClickEvent event) {
					
					if(anchor!=null){
						anchor.removeFromParent();
					}
					HTML5Download html5=new HTML5Download();
					JSONValue jsonValue=JSONParser.parseLenient(json);//possible error if json is invalid
					anchor = html5.generateTextDownloadLink(JsonValueUtils.stringify(jsonValue.isObject().getJavaScriptObject()), "raw"+".json", "Click to download",true);
					add(anchor);
					
				}
			});
			
		}
		protected void doClone(String json) {
			PoseEditorData pdata=PoseEditorData.readData(json);
			pdata.setName(pdata.getName()+"-copy");
			
			JSONObject data=PoseEditorData.convertToJson(pdata);
			//TODO
			if(!storageControler.isAvailable()){
				//TODO just export
				Window.alert("not saved because your browser not supoort HTML5 storage");
				return;
			}
			
		//	Window.alert("hello");
			//save database
			
			int dataIndex=0;
			try {
				dataIndex = getNewDataIndex();
			} catch (StorageException e) {
				alert("save faild:"+e.getMessage());
				return;
			}
			
			//TODO method?
			//Canvas canvas=Canvas.createIfSupported();
			/*
			int thumbW=32;
			int thumbH=32;
			canvas.setSize(thumbW+"px", thumbH+"px");
			canvas.setCoordinateSpaceWidth(thumbW);
			canvas.setCoordinateSpaceHeight(thumbH);
			//log(renderer.gwtCanvas());
			//now stop write image.
			//canvas.getContext2d().drawImage(renderer.gwtCanvas(),0,0,screenWidth,screenHeight,0,0,thumbW,thumbH);
			
			String thumbnail=canvas.toDataUrl();
			LogUtils.log(thumbnail);
			*/
			
		//	Window.alert("hello1");
			//Window.alert("hello1");
			//Window.open(thumbnail, "tmp", null);
			try{
			storageControler.setValue(KEY_DATA+dataIndex, data.toString());
		//	Window.alert("hello2");
			//storageControler.setValue(KEY_IMAGE+dataIndex, thumbnail);
			storageControler.setValue(KEY_HEAD+dataIndex, pdata.getName()+"\t"+pdata.getCdate());
			
		//	Window.alert("hello3:"+dataIndex);
			pdata.setFileId(dataIndex);
			
			//increment
			dataIndex++;
			storageControler.setValue(KEY_INDEX, dataIndex);
			}catch (Exception e) {
				Window.alert("storage error:"+e.getMessage());
			}
			
			updateDatasPanel();
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
		try{
		storageControler.setValue(KEY_DATA+index, null);
		storageControler.setValue(KEY_IMAGE+index, null);
		storageControler.setValue(KEY_HEAD+index, null);
	}catch (StorageException e) {
		LogUtils.log("do remove data faild:"+e.getMessage());
	}
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
					popupPanel.setVisible(true);
				}else{
				stats.setVisible(false);
				bottomPanel.setVisible(false);
				hideControl();
				popupPanel.setVisible(false);
				
				if(selection==3){//settings
					
					settingPanel.synchUI();
					reservedSettingPreview=true;
					
					//isUsingRenderer=true;
				}
				
				}
				resized(screenWidth,screenHeight);//for some blackout;
			}
		});
		VerticalPanel datasRoot=new VerticalPanel();
		tabPanel.add(datasRoot,"Motion & Pose");
		
		HorizontalPanel dataButtons=new HorizontalPanel();
		datasRoot.add(dataButtons);
		
		FileUploadForm importBVH=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {	
			@Override
			public void uploaded(File file, String value) {
				BVHParser parser=new BVHParser();
				
				int dataIndex;
				try {
					dataIndex = getNewDataIndex();
				} catch (StorageException e1) {
					alert("faild getnewdataindex");
					return;
				}
				JSONObject object=null;
				try {
					BVH bvh=parser.parse(value);
					object=new PoseEditorDataConverter().convert(bvh);
					
				} catch (InvalidLineException e) {
					alert("invalid bvh:"+file.getFileName());
					return;
				} 
				
				String name=FileNames.getRemovedExtensionName(file.getFileName());
				long ctime=System.currentTimeMillis();
				object.put("name", new JSONString(name));
				object.put("cdate", new JSONNumber(ctime));//
				
				
				
				try {
					storageControler.setValue(KEY_DATA+dataIndex, object.toString());
					storageControler.setValue(KEY_HEAD+dataIndex,name+"\t"+ctime);
					
					dataIndex++;
					storageControler.setValue(KEY_INDEX, dataIndex);
				} catch (StorageException e) {
					try {
						storageControler.removeValue(KEY_DATA+dataIndex);
						storageControler.removeValue(KEY_HEAD+dataIndex);
					} catch (StorageException e1) {
					}
					alert("data store faild:"+e.getMessage());
				}
				
				
				updateDatasPanel();
			}
		}, true);
		importBVH.setAccept(".bvh");
		
		/*
		stop support
		dataButtons.add(new Label("Import BVH"));
		dataButtons.add(importBVH);
		*/
		
		datasPanel = new VerticalPanel();
		
		//datasPanel.setStyleName("debug");
		ScrollPanel scroll=new ScrollPanel(datasPanel);
		scroll.setSize("800px", "500px");
		datasRoot.add(scroll);
		
		try {
			logger.fine("selection:"+storageControler.getValue(PreferenceTabPanel.KEY_MODEL_SELECTION, 0));
		} catch (StorageException e) {
			e.printStackTrace();
		}
		
		//storageControler.setValue(PreferenceTabPanel.KEY_MODEL_SELECTION, 0);
		preferencePanel=new PreferenceTabPanel(storageControler,this);
		tabPanel.add(preferencePanel,"Model & Texture");
		
		
		
		
		
		settingPanel=new SettingPanel();
		tabPanel.add(settingPanel,"Settings");
		
		
		//about
		String html="";
		html+="<br/>"+"[Howto Move]<br/><b>Select Nothing:</b><br/>Mouse Drag=Cammera Rotatation-XY<br/>Mouse Wheel= Zoom<br/> +ALT Move-XY Camera";
		html+="<br/><br/>"+"<b>Select IK(Green Box):</b><br/>Mouse Drag=Move IK-XYZ <br/>Mouse Wheel=Move IK-Z <br/>+ALT=smoth-change <br/>+Shift=Move Only<br/>+ALT+Shift Follow other IK";
		html+="<br/><br/>"+"<b>Select Bone(Red Box):</b><br/>Mouse Drag=Rotate-XY<br/>Mouse Wheel=Rotate-Z";
		html+="<br/><br/>"+"<b>Select Root(Red Large Box):</b><br/>Mouse Drag=Rotate-XYZ<br/>Mouse Wheel=Rotate-Z +ALT=Follow IK +Shift=Move Position";
		html+="<br/><br/>"+"yello box means under Y:0,orange box means near Y:0";
		html+="<br/>On IK-Moving switching normal & +Alt(Smooth) is good tactics.";
		html+="<br/>"+"<a href='http://webgl.akjava.com'>More info at webgl.akjava.com</a>";
		
		HTML htmlP=new HTML(html);
		VerticalPanel aboutRoot=new VerticalPanel();
		//TODO credit
		aboutRoot.add(htmlP);
		tabPanel.add(aboutRoot,"About");
		
		
		
	}
	public boolean isUsingRenderer() {
		return isUsingRenderer;
	}

	public void setUsingRenderer(boolean isUsingRenderer) {
		this.isUsingRenderer = isUsingRenderer;
	}


	SettingPanel settingPanel;
	PreferenceTabPanel preferencePanel;
	
	


	public static Map<String,BoneLimit> boneLimits=new HashMap<String,BoneLimit>();
	
	NumberFormat numberFormat= NumberFormat.getFormat("0.0");
	 
	private void updateIkPositionLabel(){
		//i'm not sure why value x 10 times;
		ikPositionLabelX.setText("Ik-X:"+numberFormat.format(getCurrentIkData().getTargetPos().getX()*10));
		ikPositionLabelY.setText("Ik-Y:"+numberFormat.format(getCurrentIkData().getTargetPos().getY()*10));
		ikPositionLabelZ.setText("Ik-Z:"+numberFormat.format(getCurrentIkData().getTargetPos().getZ()*10));
	}
	private void updateIkLabels(){
		//log(""+boneNamesBox);
		boneNamesBox.clear();
		if(currentSelectionIkName!=null){
			setEnableBoneRanges(false,false,true);//no root
			
			updateIkPositionLabel();
			
			//getCurrentIkData().getTargetPos().getX()
			boneNamesBox.addItem("");
		for(int i=0;i<getCurrentIkData().getBones().size();i++){
			boneNamesBox.addItem(getCurrentIkData().getBones().get(i));
		}
		
		boneNamesBox.addItem(getCurrentIkData().getLastBoneName());//need this too
		
		boneNamesBox.setSelectedIndex(0);
		}else if(selectedBone!=null){
			setEnableBoneRanges(true,true,false);
			boneNamesBox.addItem(selectedBone);
			boneNamesBox.setSelectedIndex(0);
			updateBoneRanges();
		}else{
			setEnableBoneRanges(false,false,false);//no selection
		}
		
		
		if(boneNamesBox.getItemCount()==0){
			rotateAndPosList.setEnabled(false);
			boneRotationsPanel.setVisible(false);
			bonePositionsPanel.setVisible(false);
		}else{
			rotateAndPosList.setEnabled(true);
			if(rotateAndPosList.getSelectedIndex()==0){
				boneRotationsPanel.setVisible(true);
			}else{
				bonePositionsPanel.setVisible(true);
			}
			
		}
	}
	
	private void setEnableBoneRanges(boolean rotate,boolean pos,boolean ikPos){
		bonePositionsPanel.setVisible(pos);
		boneRotationsPanel.setVisible(rotate);
		
		rotationBoneXRange.setEnabled(rotate);
		rotationBoneYRange.setEnabled(rotate);
		rotationBoneZRange.setEnabled(rotate);
		
		positionXBoneRange.setEnabled(pos);
		positionYBoneRange.setEnabled(pos);
		positionZBoneRange.setEnabled(pos);
		
		//ik pos
		ikPositionsPanel.setVisible(ikPos);

		//TODO x,y
	}
	
	int ikdataIndex=1;
	private List<IKData> ikdatas=new ArrayList<IKData>();

	private String currentSelectionIkName;//TODO not use last bone name
	Mesh selectionMesh;
	final Projector projector=Projector.createProjector();
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
		return currentSelectionIkName!=null;
	}
	
	//TODO fix
	//here is so slow.
	private void switchSelectionIk(String name){
		
		
		currentSelectionIkName=name;
		currentMatrixs=AnimationBonesData.cloneAngleAndMatrix(ab.getBonesAngleAndMatrixs());
		
		
		
			
		
		
		
		if(currentSelectionIkName!=null){
		
			List<List<NameAndVector3>> result=createIKBases(getCurrentIkData());
			//log("switchd:"+result.size());
			
			/*for debug
			List<NameAndVector3> tmp=result.get(result.size()-1);
			for(NameAndVector3 value:tmp){
			//	log(value.getName()+":"+ThreeLog.get(value.getVector3()));
			}
			*/
		
		if(candiateAngleAndMatrixs!=null){
			candiateAngleAndMatrixs.clear();
		}else{
			candiateAngleAndMatrixs=new ArrayList<List<AngleAndPosition>>();
		}
		
		//must be lower .to keep lower add limit bone inside IK
		
		//LogUtils.log("safe heresome how in danger:"+name);
		
		
		int index=0;
		for(List<NameAndVector3> nv:result){
			//log("candiate:"+index);
			List<AngleAndPosition> bm=mergeMeshMatrix(ab);
			applyMatrix(bm, nv);
			
			//for debug;
			for(String bname:getCurrentIkData().getBones()){
				Vector3 angle=bm.get(ab.getBoneIndex(bname)).getDegreeAngle();
				//log(bname+":"+ThreeLog.get(angle));
			}
			
			candiateAngleAndMatrixs.add(bm);
			index++;
		}
		
		}else{
			//LogUtils.log("currentSelectionIkName not selected yet:"+name);
		}
		
		
		
		/*
		LogUtils.log("end switchSelectionIk:"+name);
		if(true){
			return;
		}
		*/
		
		updateIkLabels();
	}
	
	Map<IKData,List<List<NameAndVector3>>> ikBasesMap=new HashMap<IKData,List<List<NameAndVector3>>>();
	
	
	public  List<List<NameAndVector3>> createIKBases(IKData data){
		
		if(ikBasesMap.get(data)!=null){
			return ikBasesMap.get(data);
		}
		
		//int angle=30;
		int angle=25;//how smooth?
		
		//need change angle step if need more 
		if(data.getLastBoneName().equals("rShldr") || data.getLastBoneName().equals("lShldr")  ){
			angle=10;	//chest is important?,tried but not so effected.
		}
		List<List<NameAndVector3>> all=new ArrayList<List<NameAndVector3>>();
		List<List<NameAndVector3>> result=new ArrayList<List<NameAndVector3>>();
		
		for(int i=0;i<data.getBones().size();i++){
			String name=data.getBones().get(i);
			List<NameAndVector3> patterns=createIKBases(name,angle); //90 //60 is slow
			all.add(patterns);
			//log(name+"-size:"+patterns.size());
		}
		//log(data.getLastBoneName()+"-joint-size:"+all.size());
		addBase(all,result,data.getBones(),0,null,2);
		
		ikBasesMap.put(data,result);//store
		
		if(result.size()>1500){
			LogUtils.log("warn many result-size:"+result.size()+" this almost freeze everything. are you forget limit bone.");
		}
		
		return result;
	}
	
	private static void addBase(List<List<NameAndVector3>> all,
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

	private static List<NameAndVector3> createIKBases(String name,int step){
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
			logger.fine(name+":use zero base");
			patterns.add(new NameAndVector3(name,0,0,0));//empty not allowd
		}
		
		return new ArrayList<NameAndVector3>(patterns);
	}
	
	
	PopupPanel contextMenu;
	
	
	private void showContextMenu(int left,int top){
		if(contextMenu==null){
			createContextMenu();
		}
		
	
		
		contextMenu.setPopupPosition(left, top);
		updateContextMenu();
		contextMenu.show();
	}
	
	
	private List<MenuItem> dynamicMenues=new ArrayList<MenuItem>();
	
	
	//use enabled ,strange behavior when use visible
	private void updateContextMenu() {
		
		//remove old
		for(MenuItem item:dynamicMenues){
			if(item.getParentMenu()!=null){//usually never possible
			item.getParentMenu().removeItem(item);
			}
		}
		dynamicMenues.clear();
		
		
		
		
		if(isSelectedIk()){
			dynamicMenues.add(rootBar.addItem("selected Ik",ikSelectedBar));
		}else if(isSelectedBone()){
			dynamicMenues.add(rootBar.addItem("selected Bone",boneSelecteddBar));
		}else{
			
		}
		
	}

	private void hideContextMenu(){
		if(contextMenu!=null){
			contextMenu.hide();
		}
	}
	
	private Predicate<IKData> existIkPredicate=new Predicate<IKData>() {
		@Override
		public boolean apply(IKData input) {
			return existBone(input.getLastBoneName());
		}
	};
	private Iterable<IKData> getAvaiableIkdatas(){
		return Iterables.filter(ikdatas,existIkPredicate);
	}
	
	
	CopiedIk copiedIk;
	CopiedBone copiedBone;
	
	private class CopiedBone{
		private String name;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Vector3 getAngle() {
			return angle;
		}
		public void setAngle(Vector3 angle) {
			this.angle = angle;
		}
		public Vector3 getPosition() {
			return position;
		}
		public void setPosition(Vector3 position) {
			this.position = position;
		}
		public CopiedBone(String name, Vector3 angle, Vector3 position) {
			super();
			this.name = name;
			this.angle = angle;
			this.position = position;
		}
		private Vector3 angle;
		private Vector3 position;
	}
	
	private class CopiedIk{
		private String ikName;
		public String getIkName() {
			return ikName;
		}
		public void setIkName(String ikName) {
			this.ikName = ikName;
		}
		public List<String> getNames() {
			return names;
		}
		public List<Vector3> getAngles() {
			return angles;
		}
		private List<String> names=new ArrayList<String>();//bone name
		private List<Vector3> angles=new ArrayList<Vector3>();
		private void clear(){
			names.clear();
			angles.clear();
		}
		private void add(String boneName,Vector3 angle){
			names.add(boneName);
			angles.add(angle.clone());
		}
	}
	
	private void doPasteIk(){
		if(copiedIk==null){
			return;
		}
		boolean needSwap=false;
		if(isSelectedIk()){
			String ikName=getCurrentIkData().getLastBoneName();//TODO fix handle last bone name;
			if(!copiedIk.getIkName().equals(ikName)){
				if(copiedIk.getIkName().equals(getMirroredName(ikName))){
				needSwap=true;
				}else{
					Window.alert("not supported paste selection ik selected ="+ikName+" copied "+copiedIk.getIkName());
					return;
				}
			}
		}
		
		//do paste
		for(int i=0;i<copiedIk.getNames().size();i++){
			
			String targetName;
			if(needSwap){
				targetName=getMirroredName(copiedIk.getNames().get(i));
			}else{
				targetName=copiedIk.getNames().get(i);
			}
			if(targetName==null){
				continue;
			}
			int index=ab.getBoneIndex(targetName);
			
			if(index!=-1 ){
				if(needSwap){
					rotToBone(targetName, copiedIk.getAngles().get(i).getX(), -copiedIk.getAngles().get(i).getY(), -copiedIk.getAngles().get(i).getZ(),true);
				}else{
					rotToBone(targetName, copiedIk.getAngles().get(i).getX(), copiedIk.getAngles().get(i).getY(), copiedIk.getAngles().get(i).getZ(),true);
				}
				
			}
		}
	}
	
	
	
	private void doPasteBone(){
		if(copiedBone==null){
			LogUtils.log("not copied bone yet");
			return;
		}
		int srcIndex=ab.getBoneIndex(copiedBone.getName());
		if(srcIndex!=-1){
			ab.getBoneAngleAndMatrix(srcIndex).getPosition().copy(copiedBone.getPosition());
			ab.getBoneAngleAndMatrix(srcIndex).getDegreeAngle().copy(copiedBone.getAngle());
			ab.getBoneAngleAndMatrix(srcIndex).updateMatrix();
			fitIkOnBone();
			doPoseByMatrix(ab);
		}else{
			LogUtils.log("invalid bone selected:"+copiedBone.getName());
		}
	}
	private void doCopyBone(){
		if(isSelectedBone()){
			String name=getSelectedBoneName();
			int srcIndex=ab.getBoneIndex(name);
			if(srcIndex!=-1){
				Vector3 angle=ab.getBoneAngleAndMatrix(srcIndex).getDegreeAngle().clone();
				Vector3 pos=ab.getBoneAngleAndMatrix(srcIndex).getPosition().clone();
				
				if(copiedBone==null){
					copiedBone=new CopiedBone(name, angle, pos);
				}else{
					copiedBone.setName(name);
					copiedBone.setAngle(angle);
					copiedBone.setPosition(pos);
				}
			}else{
				LogUtils.log("invalid bone selected:"+name);
			}
		}
	}
	private void doCopyIk(boolean copyLastBone){
		
		
		if(isSelectedIk()){
			
			if(copiedIk==null){
				copiedIk=new CopiedIk();
			}else{
				copiedIk.clear();
			}
			
			IKData ik=getCurrentIkData();
			copiedIk.setIkName(ik.getLastBoneName());//TODO fix better ik-name by preset
			
			List<String> boneNames=Lists.newArrayList(ik.getBones());
			if(copyLastBone){
				boneNames.add(ik.getLastBoneName());
			}
			
			for(String name:boneNames){
				
				
				int srcIndex=ab.getBoneIndex(name);
				if(srcIndex!=-1){
					Vector3 angle=ab.getBoneAngleAndMatrix(srcIndex).getDegreeAngle();
					
					copiedIk.add(name,angle);
					
				}
			}
		}
	}
	
	
	private void createBoneSelectedMenu(MenuBar parent){
		parent.addItem("Copy", new Command(){
			@Override
			public void execute() {
				doCopyBone();
				hideContextMenu();
			}});
	}
	private void createIkSelectedMenu(MenuBar parent){
		
		parent.addItem("Copy", new Command(){
			@Override
			public void execute() {
				doCopyIk(false);
				hideContextMenu();
			}});
		
		parent.addItem("Copy with lastBone", new Command(){
			@Override
			public void execute() {
				doCopyIk(true);
				hideContextMenu();
			}});
		
		
		parent.addItem("Paste", new Command(){
			@Override
			public void execute() {
				doPasteIk();
				fitIkOnBone();
				hideContextMenu();
			}});
		

		parent.addItem("Move to Prev Frame Ik-pos", new Command(){
			@Override
			public void execute() {
				
				if(isSelectedIk() && isCurrentHasPrevFrame()){	
					String boneName=getCurrentIkData().getLastBoneName();
					Vector3 pos=getBonePositionAtFrame(boneName,poseFrameDataIndex-1);
					if(pos!=null){
						getCurrentIkData().getTargetPos().copy(pos);
						syncIkPosition();
					}
				}
				hideContextMenu();
				
			}});
		
		parent.addItem("Move to Current Frame Ik-pos", new Command(){
			@Override
			public void execute() {
				
				if(isSelectedIk()){	
					String boneName=getCurrentIkData().getLastBoneName();
					Vector3 pos=getBonePositionAtFrame(boneName,poseFrameDataIndex);
					if(pos!=null){
						getCurrentIkData().getTargetPos().copy(pos);
						syncIkPosition();
					}
				}
				hideContextMenu();
				
			}});
		
		parent.addItem("Move to Next Frame Ik-pos", new Command(){
			@Override
			public void execute() {
				
				if(isSelectedIk() && isCurrentHasNextFrame()){	
					String boneName=getCurrentIkData().getLastBoneName();
					Vector3 pos=getBonePositionAtFrame(boneName,poseFrameDataIndex+1);
					if(pos!=null){
						getCurrentIkData().getTargetPos().copy(pos);
						syncIkPosition();
					}
				}
				hideContextMenu();
				
			}});
		
		parent.addItem("Move to Between Frame Ik-pos", new Command(){
			@Override
			public void execute() {
				
				if(isSelectedIk() && isCurrentHasNextFrame()  && isCurrentHasPrevFrame()){	
					String boneName=getCurrentIkData().getLastBoneName();
					Vector3 pos=getBonePositionAtFrame(boneName,poseFrameDataIndex-1).clone();//to modify to clone
					Vector3 next=getBonePositionAtFrame(boneName,poseFrameDataIndex+1);
					
					if(pos!=null && next!=null){
						pos.add(next).divideScalar(2);
						getCurrentIkData().getTargetPos().copy(pos);
						syncIkPosition();
					}
				}
				hideContextMenu();
				
			}});
		

		MenuBar moveToIk=new MenuBar(true);
		parent.addItem("Root move to selection IK-Pos", moveToIk);

		moveToIk.addItem("Pos-X", new Command(){
			@Override
			public void execute() {
				if(!isSelectedIk()){
					hideContextMenu();
					return;
				}
				Vector3 target=getCurrentIkData().getTargetPos();
				Vector3 rootPos=ab.getBonePosition(0);
				Vector3 diff=target.clone().sub(rootPos);
				diff.setY(0);
				diff.setZ(0);
				
				ab.getBoneAngleAndMatrix(0).setPosition(rootPos.add(diff));
				ab.getBoneAngleAndMatrix(0).updateMatrix();
				doPoseByMatrix(ab);
				hideContextMenu();
			}});
		moveToIk.addItem("Pos-Y", new Command(){
			@Override
			public void execute() {
				if(!isSelectedIk()){
					hideContextMenu();
					return;
				}
				Vector3 target=getCurrentIkData().getTargetPos();
				Vector3 rootPos=ab.getBonePosition(0);
				Vector3 diff=target.clone().sub(rootPos);
				diff.setX(0);
				diff.setZ(0);
				
				ab.getBoneAngleAndMatrix(0).setPosition(rootPos.add(diff));
				ab.getBoneAngleAndMatrix(0).updateMatrix();
				doPoseByMatrix(ab);
				hideContextMenu();
			}});
		moveToIk.addItem("Pos-Z", new Command(){
			@Override
			public void execute() {
				if(!isSelectedIk()){
					hideContextMenu();
					return;
				}
				Vector3 target=getCurrentIkData().getTargetPos();
				Vector3 rootPos=ab.getBonePosition(0);
				Vector3 diff=target.clone().sub(rootPos);
				diff.setY(0);
				diff.setX(0);
				
				ab.getBoneAngleAndMatrix(0).setPosition(rootPos.add(diff));
				ab.getBoneAngleAndMatrix(0).updateMatrix();
				doPoseByMatrix(ab);
				hideContextMenu();
			}});
		
		moveToIk.addItem("Pos-All", new Command(){
			@Override
			public void execute() {
				if(!isSelectedIk()){
					hideContextMenu();
					return;
				}
				Vector3 target=getCurrentIkData().getTargetPos();
				Vector3 rootPos=ab.getBonePosition(0);
				Vector3 diff=target.clone().sub(rootPos);
				
				ab.getBoneAngleAndMatrix(0).setPosition(rootPos.add(diff));
				ab.getBoneAngleAndMatrix(0).updateMatrix();
				doPoseByMatrix(ab);
				hideContextMenu();
				/*
				for(IKData ik:getAvaiableIkdatas()){
					ik.getTargetPos().addSelf(diff);
					doPoseByMatrix(ab);
					hideContextMenu();
				}
				*/
			}});
	}
	
	private MenuBar ikSelectedBar,boneSelecteddBar;
	
	private void createContextMenu(){
		contextMenu=new PopupPanel();
		rootBar = new MenuBar(true);
		contextMenu.add(rootBar);
		rootBar.setAutoOpen(true);
		
		
		 ikSelectedBar=new MenuBar(true);
		
		createIkSelectedMenu(ikSelectedBar);
		
		 boneSelecteddBar=new MenuBar(true);
		 
		 createBoneSelectedMenu(boneSelecteddBar);
		
		
		
		
		MenuBar ikBar=new MenuBar(true);
		rootBar.addItem("Ik",ikBar);
		
		
	
		ikBar.addItem("Exec hard", new Command(){
			@Override
			public void execute() {
				execIk();
				hideContextMenu();
			}});
		ikBar.addItem("Exec mild", new Command(){
			@Override
			public void execute() {
				execIk(5,5);
				hideContextMenu();
			}});
		
		
		
		
		ikBar.addItem("Fit ik on bone", new Command(){
			@Override
			public void execute() {
				
				if(isSelectedIk()){
					AnimationBonesData merged=new AnimationBonesData(animationBones,mergeMeshMatrix(ab));
					//only do selected ik
					Vector3 pos=merged.getBonePosition(getCurrentIkData().getLastBoneName());
					getCurrentIkData().getTargetPos().set(pos.getX(), pos.getY(), pos.getZ());
				}else{
					fitIkOnBone();//do all
				}
				
				
				
				
				doPoseByMatrix(ab);//really need?
				hideContextMenu();	
			}});
		
		ikBar.addItem("Follow target", new Command(){
			@Override
			public void execute() {
				if(isSelectedIk()){
					AnimationBonesData merged=getMergedAnimationBonesData(ab);
					if(existBone(getCurrentIkData().getLastBoneName())){
						getCurrentIkData().getTargetPos().copy(getDefaultIkPos(merged.getBoneIndex(getCurrentIkData().getLastBoneName())));
						//doPoseByMatrix(ab);
					}
				}else{
					followTarget();//do all
				}
				
				doPoseByMatrix(ab);
				hideContextMenu();
			}});
		
		
		ikBar.addItem("Paste", new Command(){
			@Override
			public void execute() {
				doPasteIk();
				fitIkOnBone();
				hideContextMenu();
			}});

	
		ikBar.addItem("Y-Zero", new Command(){
			@Override
			public void execute() {
				for(IKData ik:getAvaiableIkdatas()){
					String name=ik.getLastBoneName();
					Vector3 pos=ab.getBonePosition(name);
					ik.getTargetPos().setY(0);
					doPoseByMatrix(ab);
					hideContextMenu();
				}
			}});
		ikBar.addItem("Move to First-IK-XZ", new Command(){
			@Override
			public void execute() {
				for(IKData ik:getAvaiableIkdatas()){
					String name=ik.getBones().get(0);
					Vector3 pos=ab.getBonePosition(name);
					ik.getTargetPos().setX(pos.getX());
					ik.getTargetPos().setZ(pos.getZ());
					doPoseByMatrix(ab);
					hideContextMenu();
				}
			}});
		ikBar.addItem("Move to Last-IK-XZ", new Command(){
			@Override
			public void execute() {
				for(IKData ik:getAvaiableIkdatas()){
					String name=ik.getBones().get(ik.getBones().size()-1);
					Vector3 pos=ab.getBonePosition(name);
					ik.getTargetPos().setX(pos.getX());
					ik.getTargetPos().setZ(pos.getZ());
					doPoseByMatrix(ab);
					hideContextMenu();
				}
			}});
		
		
		
		createContextMenuRoot(rootBar);

		MenuBar boneBar=new MenuBar(true);
		MenuItem boneLimitMenuItem = new MenuItem("Bone",boneBar);//menu item can change label dynamic
		
		boneBar.addItem("Paste",new Command(){

			@Override
			public void execute() {
				doPasteBone();
				updateBoneRanges();
				hideContextMenu();
			}});
		
		rootBar.addItem(boneLimitMenuItem);
		
		boneBar.addItem("Change bones'limit to none", new Command(){
			@Override
			public void execute() {
				if(!isSelectedIk()){
					hideContextMenu();
					return;
				}
				IKData ik=getCurrentIkData();
				for(String boneName:ik.getBones()){
					boneLock.clearX(boneName);
					boneLock.clearY(boneName);
					boneLock.clearZ(boneName);
					
				}
				updateBoneRotationRanges();
				hideContextMenu();
			}});
		boneBar.addItem("Change bones'limit to X", new Command(){
			@Override
			public void execute() {
				if(!isSelectedIk()){
					hideContextMenu();
					return;
				}
				IKData ik=getCurrentIkData();
				for(String boneName:ik.getBones()){
					boneLock.clearY(boneName);
					boneLock.clearZ(boneName);
					boneLock.setX(boneName,ab.getBoneAngleAndMatrix(boneName).getDegreeAngle().getX());
					
				}
				updateBoneRotationRanges();
				hideContextMenu();
			}});
		boneBar.addItem("Change bones'limit to Y", new Command(){
			@Override
			public void execute() {
				if(!isSelectedIk()){
					hideContextMenu();
					return;
				}
				IKData ik=getCurrentIkData();
				for(String boneName:ik.getBones()){
					boneLock.clearX(boneName);
					boneLock.clearZ(boneName);
					boneLock.setY(boneName,ab.getBoneAngleAndMatrix(boneName).getDegreeAngle().getY());
					
				}
				updateBoneRotationRanges();
				hideContextMenu();
			}});
		boneBar.addItem("Change bones'limit to Z", new Command(){
			@Override
			public void execute() {
				if(!isSelectedIk()){
					hideContextMenu();
					return;
				}
				IKData ik=getCurrentIkData();
				for(String boneName:ik.getBones()){
					boneLock.clearX(boneName);
					boneLock.clearY(boneName);
					boneLock.setZ(boneName,ab.getBoneAngleAndMatrix(boneName).getDegreeAngle().getZ());
					
				}
				updateBoneRotationRanges();
				hideContextMenu();
			}});
		
		boneBar.addItem("Change bones'limit to Y,Z", new Command(){
			@Override
			public void execute() {
				if(!isSelectedIk()){
					hideContextMenu();
					return;
				}
				IKData ik=getCurrentIkData();
				for(String boneName:ik.getBones()){
					boneLock.clearX(boneName);
					boneLock.setY(boneName,ab.getBoneAngleAndMatrix(boneName).getDegreeAngle().getY());
					boneLock.setZ(boneName,ab.getBoneAngleAndMatrix(boneName).getDegreeAngle().getZ());
					
				}
				updateBoneRotationRanges();
				hideContextMenu();
			}});
		
		boneBar.addItem("Change bones'limit to X,Z", new Command(){
			@Override
			public void execute() {
				if(!isSelectedIk()){
					hideContextMenu();
					return;
				}
				IKData ik=getCurrentIkData();
				for(String boneName:ik.getBones()){
					boneLock.clearY(boneName);
					boneLock.setX(boneName,ab.getBoneAngleAndMatrix(boneName).getDegreeAngle().getX());
					boneLock.setZ(boneName,ab.getBoneAngleAndMatrix(boneName).getDegreeAngle().getZ());
					
				}
				updateBoneRotationRanges();
				hideContextMenu();
			}});
		boneBar.addItem("Change bones'limit to Y,X", new Command(){
			@Override
			public void execute() {
				if(!isSelectedIk()){
					hideContextMenu();
					return;
				}
				IKData ik=getCurrentIkData();
				for(String boneName:ik.getBones()){
					boneLock.clearZ(boneName);
					boneLock.setY(boneName,ab.getBoneAngleAndMatrix(boneName).getDegreeAngle().getY());
					boneLock.setX(boneName,ab.getBoneAngleAndMatrix(boneName).getDegreeAngle().getX());
					
				}
				updateBoneRotationRanges();
				hideContextMenu();
			}});
		
		
		//createContextMenuFrames(rootBar);
		
	
		
		MenuBar cameraBar=new MenuBar(true);
		rootBar.addItem("Camera",cameraBar);
		
		cameraBar.addItem("Front", new Command(){
			@Override
			public void execute() {
				rotationXRange.setValue(0);
				rotationYRange.setValue(0);
				rotationZRange.setValue(0);
				positionXRange.setValue(0);
				positionYRange.setValue(defaultOffSetY);
				hideContextMenu();
			}});
		cameraBar.addItem("Back", new Command(){
			@Override
			public void execute() {
				rotationXRange.setValue(0);
				rotationYRange.setValue(180);
				rotationZRange.setValue(0);
				positionXRange.setValue(0);
				positionYRange.setValue(defaultOffSetY);
				hideContextMenu();
			}});
		cameraBar.addItem("Quoter", new Command(){
			@Override
			public void execute() {
				rotationXRange.setValue(45);
				rotationYRange.setValue(45);
				rotationZRange.setValue(0);
				positionXRange.setValue(0);
				positionYRange.setValue(defaultOffSetY);
				hideContextMenu();
			}});
		cameraBar.addItem("Top", new Command(){
			@Override
			public void execute() {
				rotationXRange.setValue(90);
				rotationYRange.setValue(0);
				rotationZRange.setValue(0);
				positionXRange.setValue(0);
				positionYRange.setValue(0);
				hideContextMenu();
			}});
		cameraBar.addItem("Bottom", new Command(){
			@Override
			public void execute() {
				rotationXRange.setValue(-90);
				rotationYRange.setValue(0);
				rotationZRange.setValue(0);
				positionXRange.setValue(0);
				positionYRange.setValue(0);
				hideContextMenu();
			}});
		cameraBar.addItem("Right", new Command(){
			@Override
			public void execute() {
				rotationXRange.setValue(0);
				rotationYRange.setValue(90);
				rotationZRange.setValue(0);
				positionXRange.setValue(0);
				positionYRange.setValue(defaultOffSetY);
				hideContextMenu();
			}});
		cameraBar.addItem("Left", new Command(){
			@Override
			public void execute() {
				rotationXRange.setValue(0);
				rotationYRange.setValue(-90);
				rotationZRange.setValue(0);
				positionXRange.setValue(0);
				positionYRange.setValue(defaultOffSetY);
				hideContextMenu();
			}});
		
	}
	
	//line has problem,TODO find a way to fix
	protected void syncIkPosition() {
		for(IKData ik:getAvaiableIkdatas()){
			Mesh ikMesh=targetMeshs.get(ik.getLastBoneName());//TODO define ik name,;
			if(ikMesh!=null){
				ikMesh.setPosition(ik.getTargetPos());
				}else{
					LogUtils.log("ikMesh not found:"+ik.getLastBoneName());
				}
		}
		
		if(isSelectedIk()){
			selectionMesh.setPosition(getCurrentIkData().getTargetPos());
			}
	}

	/**
	 * 
	 * @param boneName
	 * @param frameIndex
	 * @return origin ,take care of modify
	 */
	private Vector3 getBonePositionAtFrame(String boneName,int frameIndex){
		if(frameIndex<0 || frameIndex>getSelectedPoseEditorData().getPoseFrameDatas().size()-1){
			LogUtils.log("getBonePositionAtFrame:out of range frame size= "+getSelectedPoseEditorData().getPoseFrameDatas().size()+" index="+frameIndex);
			return null;//out of frame range;
		}
		
		
		
		if(!existBone(boneName)){
			LogUtils.log("getBonePositionAtFrame:bone not found "+boneName);
			return null;
		}
		
		int boneIndex=ab.getBoneIndex(boneName);
		
		PoseFrameData frameData=getSelectedPoseEditorData().getPoseFrameDatas().get(frameIndex);
		AnimationBonesData workingAnimationBoneData=new AnimationBonesData(animationBones, frameData.getAngleAndMatrixs());
		
		return workingAnimationBoneData.getBonePosition(boneIndex);
	}
	
	private boolean isCurrentHasPrevFrame() {
		return poseFrameDataIndex>0;
	}
	
	private boolean isCurrentHasNextFrame() {
		return poseFrameDataIndex<getSelectedPoseEditorData().getPoseFrameDatas().size();
	}

	protected void followTarget() {
		AnimationBonesData merged=getMergedAnimationBonesData(ab);
		for(IKData ik:getAvaiableIkdatas()){
			String name=ik.getLastBoneName();
			
			if(existBone(name)){
				ik.getTargetPos().copy(getDefaultIkPos(merged.getBoneIndex(name)));
				//doPoseByMatrix(ab);
			}
			
			
			
		}
	}

	protected void execIk() {
		execIk(45,10);
	}
	protected void execIk(int perLimit,int loop) {
		
		for(IKData ik:getAvaiableIkdatas()){
			doPoseIkk(0,false,perLimit,ik,loop);
		}
	}

	private void createContextMenuRoot(MenuBar rootBar){
		MenuBar rootBoneBar=new MenuBar(true);
		rootBar.addItem("Root",rootBoneBar);
		
		rootBoneBar.addItem("touch ground(Y-0)", new Command(){
			@Override
			public void execute() {
				
				touchGroundZero();
		
			}});
		
		rootBoneBar.addItem("initial Position", new Command(){
			@Override
			public void execute() {
				
				JsArrayNumber rootPos=animationBones.get(0).getPos();
				double rootX=0;
				double rootY=0;
				double rootZ=0;
				if(rootPos!=null && rootPos.length()==3){
					rootX=rootPos.get(0);
					rootY=rootPos.get(1);
					rootZ=rootPos.get(2);
				}
				
				
				ab.getBoneAngleAndMatrix(0).getPosition().set(rootX, rootY, rootZ);//bone pos
				ab.getBoneAngleAndMatrix(0).updateMatrix();
				fitIkOnBone();
				doPoseByMatrix(ab);
				
				hideContextMenu();
		
			}});

		rootBoneBar.addItem("initial Pose", new Command(){
			@Override
			public void execute() {
				//store position
				Vector3 lastPosition=ab.getBoneAngleAndMatrix(0).getPosition().clone();
				
				List<AngleAndPosition> angleAndPositions=AnimationBonesData.cloneAngleAndMatrix(initialPoseFrameData.getAngleAndMatrixs());
				angleAndPositions.get(0).getPosition().copy(lastPosition);
				angleAndPositions.get(0).updateMatrix();
				
				selectAnimationDataData(angleAndPositions);
				hideContextMenu();
		
			}});
		
		rootBoneBar.addItem("Move to Prev Frame Position", new Command(){
			@Override
			public void execute() {
				
				if(poseFrameDataIndex<=0){
					LogUtils.log("<=0:"+poseFrameDataIndex);
					hideContextMenu();
					//no frame
					return;
				}
				
				if(poseFrameDataIndex>=getSelectedPoseEditorData().getPoseFrameDatas().size()){
					//something invalid
					LogUtils.log("invalid range");
					hideContextMenu();
					return ;
				}
				
				PoseFrameData prevFrameData=getSelectedPoseEditorData().getPoseFrameDatas().get(poseFrameDataIndex-1);
				Vector3 prevFramePosition=prevFrameData.getAngleAndMatrixs().get(0).getPosition();
				
				
				
				
				ab.getBoneAngleAndMatrix(0).getPosition().copy(prevFramePosition);
				ab.getBoneAngleAndMatrix(0).updateMatrix();
				fitIkOnBone();
				doPoseByMatrix(ab);
				
				hideContextMenu();
		
			}});
		
		
		rootBoneBar.addItem("Move to Current Frame Position", new Command(){
			@Override
			public void execute() {
				
				//get not modified position
				PoseFrameData prevFrameData=getSelectedPoseEditorData().getPoseFrameDatas().get(poseFrameDataIndex);
				Vector3 prevFramePosition=prevFrameData.getAngleAndMatrixs().get(0).getPosition();
				
				ab.getBoneAngleAndMatrix(0).getPosition().copy(prevFramePosition);
				ab.getBoneAngleAndMatrix(0).updateMatrix();
				fitIkOnBone();
				doPoseByMatrix(ab);
				
				hideContextMenu();
		
			}});
		
		rootBoneBar.addItem("Move to Next Frame Position", new Command(){
			@Override
			public void execute() {
				
				if(poseFrameDataIndex<0){
					hideContextMenu();
					//no frame
					return;
				}
				
				if(poseFrameDataIndex>=getSelectedPoseEditorData().getPoseFrameDatas().size()){
					//something invalid
					LogUtils.log("invalid range");
					hideContextMenu();
					return ;
				}
				
				if(poseFrameDataIndex==getSelectedPoseEditorData().getPoseFrameDatas().size()-1){
					//at last frame
					hideContextMenu();
					return;
				}
				
				PoseFrameData prevFrameData=getSelectedPoseEditorData().getPoseFrameDatas().get(poseFrameDataIndex+1);
				Vector3 prevFramePosition=prevFrameData.getAngleAndMatrixs().get(0).getPosition();
				
				ab.getBoneAngleAndMatrix(0).getPosition().copy(prevFramePosition);
				ab.getBoneAngleAndMatrix(0).updateMatrix();
				fitIkOnBone();
				doPoseByMatrix(ab);
				
				hideContextMenu();
		
			}});
		
		rootBoneBar.addItem("Move to between Prev & Next Frame Position", new Command(){
			@Override
			public void execute() {
				
				if(poseFrameDataIndex<=0){//need prev
					hideContextMenu();
					//no frame
					return;
				}
				
				if(poseFrameDataIndex>=getSelectedPoseEditorData().getPoseFrameDatas().size()){
					//something invalid
					LogUtils.log("invalid range");
					hideContextMenu();
					return ;
				}
				
				if(poseFrameDataIndex==getSelectedPoseEditorData().getPoseFrameDatas().size()-1){
					//need next frame
					hideContextMenu();
					return;
				}
				
				PoseFrameData prevFrameData=getSelectedPoseEditorData().getPoseFrameDatas().get(poseFrameDataIndex-1);
				Vector3 prevFramePosition=prevFrameData.getAngleAndMatrixs().get(0).getPosition();
				
				PoseFrameData nextFrameData=getSelectedPoseEditorData().getPoseFrameDatas().get(poseFrameDataIndex+1);
				Vector3 nextFramePosition=nextFrameData.getAngleAndMatrixs().get(0).getPosition();
				
				
				Vector3 newPos=prevFramePosition.clone().add(nextFramePosition).divideScalar(2);
				
				ab.getBoneAngleAndMatrix(0).getPosition().copy(newPos);
				ab.getBoneAngleAndMatrix(0).updateMatrix();
				fitIkOnBone();
				doPoseByMatrix(ab);
				
				hideContextMenu();
		
			}});
		
		rootBoneBar.addItem("Swap all", new Command(){
			@Override
			public void execute() {
				if(bone3D!=null && bone3D.getChildren().length()>0){
					//selectBone(bone3D.getChildren().get(0),0,0,false);
					//h-flip
					//rotationBoneZRange.setValue(-rotationBoneZRange.getValue());
					//rotToBone();
					
					List<String> converted=new ArrayList<String>();
					
					//swap all
					for(IKData ik:getAvaiableIkdatas()){
						List<String> targets=Lists.newArrayList(ik.getBones());
						targets.add(ik.getLastBoneName());
						
						for(String name:targets){
							if(converted.contains(name)){
								continue;
							}
							String targetName=getMirroredName(name);
							if(targetName==null){
								continue;
							}
							int index=ab.getBoneIndex(targetName);
							int srcIndex=ab.getBoneIndex(name);
							if(index!=-1 && srcIndex!=-1){
								Vector3 angle1=ab.getBoneAngleAndMatrix(srcIndex).getDegreeAngle();
								
								Vector3 angle=ab.getBoneAngleAndMatrix(index).getDegreeAngle();
								rotToBone(name, angle.getX(), -angle.getY(), -angle.getZ(),false);
								
								rotToBone(targetName, angle1.getX(), -angle1.getY(), -angle1.getZ(),true);
							}
							
							converted.add(name);
							converted.add(targetName);
						}
					}
					
					//swap remains
					for(String bname:boneList){
						if(converted.contains(bname)){
							continue;
						}
						int index=ab.getBoneIndex(bname);
						if(index==-1){
							LogUtils.log("invalid bone:"+bname);
							continue;
						}
						Vector3 angle=ab.getBoneAngleAndMatrix(index).getDegreeAngle();
						rotToBone(bname, angle.getX(), -angle.getY(), -angle.getZ(),false);
					}
					
					
					doPoseByMatrix(ab);//update all
					
					//TODO check what exactlly doing
					updateBoneRanges();
					updateIkLabels();
					updateIkPositionLabel();
					
				}
				
			}
		});
		
		
		
		
		/*
		 * swap angle maybe need for old range
		rootBoneBar.addItem("180 to -180", new Command(){
			@Override
			public void execute() {
				Vector3 angle=ab.getBoneAngleAndMatrix(0).getAngle();
				LogUtils.log(ThreeLog.get(angle));
				if(angle.getX()==180){
					angle.setX(-180);
				}else if(angle.getX()==-180){
					angle.setX(180);
				}
				if(angle.getY()==180){
					angle.setY(-180);
				}else if(angle.getY()==-180){
					angle.setY(180);
				}
				if(angle.getZ()==180){
					angle.setZ(-180);
				}else if(angle.getZ()==-180){
					angle.setZ(180);
				}
				//ab.getBoneAngleAndMatrix(0).setPosition(getInitialPoseFrameData().getPositions().get(0).clone());
				ab.getBoneAngleAndMatrix(0).updateMatrix();
				doPoseByMatrix(ab);
				updateBoneRotationRanges();
				hideContextMenu();
		
			}});
			*/
	}
	protected void touchGroundZero() {
		
		//bodyMesh.getGeometry().computeBoundingBox();
		//BoundingBox box=bodyMesh.getGeometry().getBoundingBox();
		BoundingBox box=SkinningVertexUtils.computeBoundingBox(bodyMesh);
		
		
		//LogUtils.log("bone-pos");
		//ThreeLog.log(ab.getBonePosition(0));
		//LogUtils.log("box-min-pos");
		//ThreeLog.log(box.getMin());
		
		Vector3 currentRoot=ab.getBonePosition(0);
		currentRoot.setY(currentRoot.getY()-box.getMin().getY());
		
		//logger.fine("min:"+ThreeLog.get(box.getMin()));
		//logger.fine("max:"+ThreeLog.get(box.getMax()));
		ab.getBoneAngleAndMatrix(0).getPosition().setY(currentRoot.getY());
		ab.getBoneAngleAndMatrix(0).updateMatrix();
		
		//LogUtils.log("moved-root-pos");
		ThreeLog.log(ab.getBoneAngleAndMatrix(0).getPosition());
		
		
		fitIkOnBone();
		doPoseByMatrix(ab);
		
		hideContextMenu();
	}

	//TODO future
	private boolean isShowPrevIk;
	
	
	private void createContextMenuFrames(MenuBar rootBar){
		MenuBar framesBar=new MenuBar(true);
		contextMenuShowPrevFrame = new MenuItem("Show Prev Iks",true,new Command(){
			@Override
			public void execute() {
				contextMenuShowPrevFrame.setHTML("<b>"+"Show Prev Iks"+"</b>");
				contextMenuHidePrefIks.setHTML("Hide Prev Iks");
				isShowPrevIk=true;
				hideContextMenu();
			}});
		framesBar.addItem(contextMenuShowPrevFrame);
		contextMenuHidePrefIks = new MenuItem("<b>"+"Hide Prev Iks"+"</b>",true,new Command(){
			@Override
			public void execute() {
				contextMenuShowPrevFrame.setHTML(""+"Show Prev Iks"+"");
				contextMenuHidePrefIks.setHTML("<b>"+"Hide Prev Iks"+"</b>");
				isShowPrevIk=false;
				hideContextMenu();
			}});
		framesBar.addItem(contextMenuHidePrefIks);
		rootBar.addItem("Frames",framesBar);
	}
	
	private PoseFrameData getInitialPoseFrameData(){
		return initialPoseFrameData;
	}

	
	private String  lastSelectionIkName;
	
	private long lastClicked;
	
	/*
	 * to avoid select ik
	 */
	private boolean selectBoneFirstOnMouseDown;
	@Override
	public void onMouseDown(MouseDownEvent event) {
		logger.fine("onMouse down");
		
		
		if(event.getNativeButton()==NativeEvent.BUTTON_RIGHT){
		//	showContextMenu(mouseDownX, mouseDownY);
			return;
		}else{
			hideContextMenu();
		}
		
		//middle cant select anything
		if(event.getNativeButton()==NativeEvent.BUTTON_MIDDLE){
			return;
		}
		long t=System.currentTimeMillis();
		boolean doubleclick=t<lastClicked+300;//onDoubleClick called after mouse up,it's hard to use
		doMouseDown(event.getX(),event.getY(),doubleclick||selectBoneFirstOnMouseDown);
		lastClicked=t;
	}
	
	private void doMouseDown(int x,int y,final boolean selectBoneFirst){
		//LogUtils.log("doMouseDown:"+String.valueOf(selectBoneFirst));
		mouseDown=true;
		mouseDownX=x;
		mouseDownY=y;
		
		//log(mouseDownX+","+mouseDownY+":"+screenWidth+"x"+screenHeight);
		

		/*
		log("wpos:"+ThreeLog.get(GWTThreeUtils.toPositionVec(camera.getMatrix())));
		log("mpos:"+ThreeLog.get(GWTThreeUtils.toPositionVec(camera.getMatrixWorld())));
		log("rpos:"+ThreeLog.get(GWTThreeUtils.toPositionVec(camera.getMatrixRotationWorld())));
		log("pos:"+ThreeLog.get(camera.getPosition()));
		*/
		//log("mouse-click:"+event.getX()+"x"+event.getY());
		
		//TODO only call once for speed up
		JsArray<Object3D> targets=(JsArray<Object3D>) JsArray.createArray();
		
		JsArray<Object3D> childs=rootObject.getChildren();
		for(int i=0;i<childs.length();i++){
			//LogUtils.log(childs.get(i).getName());
			targets.push(childs.get(i));
		}
		JsArray<Object3D> bones=bone3D.getChildren();
		for(int i=0;i<bones.length();i++){
			//LogUtils.log(bones.get(i).getName());
			targets.push(bones.get(i));
		}
		
		for(int i=0;i<ik3D.getChildren().length();i++){
			//LogUtils.log(bones.get(i).getName());
			targets.push(ik3D.getChildren().get(i));
		}
		
		
JsArray<Intersect> intersects=projector.gwtPickIntersects(x, y, screenWidth, screenHeight,camera,targets);
		//log("intersects-length:"+intersects.length());
long t=System.currentTimeMillis();
List<Object3D> selections=convertSelections(intersects);


if(selectBoneFirst){//trying every click change ik and bone if both intersected
	//check bone first
	Object3D lastIk=null;
	
	for(Object3D selection:selections){
		if(selection.getName().startsWith("ik:")){
			if(selection.getName().equals(lastSelectionIkName)){
				lastIk=selection;
			}else{
			selectIk(selection,x,y,true);
			
			lastSelectionIkName=selection.getName();
			updateBoneRanges();
			return;
			}
		}
	}
	
	for(Object3D selection:selections){
		if(!selection.getName().isEmpty() && !selection.getName().startsWith("ik:")){
			selectBone(selection,x,y,true);
			
			return;
		}
	}
	
	
	if(lastIk!=null){//when ik selected,select another ik or bone first
		selectIk(lastIk,x,y,true);
		
		lastSelectionIkName=lastIk.getName();
		updateBoneRanges();
		return;
	}
	
}else{
	
	for(Object3D selection:selections){
		if(selection.getName().startsWith("ik:")){
			selectIk(selection,x,y,true);
			
			lastSelectionIkName=selection.getName();
			updateBoneRanges();
			return;
		}
		
		if(!selection.getName().isEmpty() && !selection.getName().startsWith("ik:")){
			selectBone(selection,x,y,true);
			
			return;
		}
	}
	
	
	/*
	//ik first
	for(Object3D selection:selections){
		if(selection.getName().startsWith("ik:")){
			selectIk(selection,x,y);
			
			lastSelectionIkName=selection.getName();
			updateBoneRanges();
			return;
		}
	}
	
	for(Object3D selection:selections){
		if(!selection.getName().isEmpty() && !selection.getName().startsWith("ik:")){
			selectBone(selection,x,y);
			
			return;
		}
	}
	*/
}

		
		selectBoneFirstOnMouseDown=false;
		//LogUtils.log("selectBoneFirstOnMouseDown:"+selectBoneFirstOnMouseDown);
		//log("no-selection");
		//not select ik or bone
		selectedBone=null;
		selectionMesh.setVisible(false);
		switchSelectionIk(null);
		
		updateIKVisible(ikVisibleCheck.getValue());
		logger.fine("onMouse down-end1");
	}
	private String selectedBone;
	
	private List<Object3D> convertSelections(JsArray<Intersect> intersects){
		List<Object3D> selections=new ArrayList<Object3D>();
		for(int i=0;i<intersects.length();i++){
		selections.add(intersects.get(i).getObject());
		}
		return selections;
	}
	
	private void selectBone(Object3D target,int x,int y,boolean needDrag){
		//maybe bone or root-bone
		selectedBone=target.getName();
		selectionMesh.setVisible(true);
		selectionMesh.setPosition(target.getPosition());
		selectionMesh.getMaterial().gwtGetColor().setHex(0xff0000);
		
		boolean ikVisible=true;
		for(IKData ik:getAvaiableIkdatas()){
			if(ik.getLastBoneName().equals(selectedBone)){
				ikVisible=false;
				break;
			}
		}	
		
		if(ikVisible){
			updateIKVisible(ikVisibleCheck.getValue());
			selectBoneFirstOnMouseDown=false;
		}else{
			selectBoneFirstOnMouseDown=true;
			updateIKVisible(false);//show guide?
		}
		
		switchSelectionIk(null);
		if(needDrag){
		
		dragObjectControler.selectObject(target, x,y, screenWidth, screenHeight, camera);
		logger.fine("onMouse down-end2");
		
		}
		
		//i guess set pos
		//this is same effect as mouse move
		positionXBoneRange.setValue((int) (selectionMesh.getPosition().getX()*100));
		positionYBoneRange.setValue((int)(selectionMesh.getPosition().getY()*100));
		positionZBoneRange.setValue((int)(selectionMesh.getPosition().getZ()*100));
		
		
		defereredFocusCanvas();
		
		return;
	}
	
	private void defereredFocusCanvas(){
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				tabPanel.selectTab(0);//this work reget focus canvas.
				canvas.setFocus(true);
			}
		});
		
	}
	
	
	private void selectIk(Object3D target,int x,int y,boolean drag){
		selectBoneFirstOnMouseDown=false;
		String ikBoneName=target.getName().substring(3);//3 is "ik:".length()
		
		
		for(int j=0;j<ikdatas.size();j++){
			if(ikdatas.get(j).getLastBoneName().equals(ikBoneName)){
				ikdataIndex=j;//set ikindex here
				selectionMesh.setVisible(true);
				selectionMesh.setPosition(target.getPosition());
				selectionMesh.getMaterial().gwtGetColor().setHex(0x00ff00);
				
				
				if(!ikBoneName.equals(currentSelectionIkName)){
					switchSelectionIk(ikBoneName);
				}
				selectedBone=null;
				
				
				if(drag){
				dragObjectControler.selectObject(target, x,y, screenWidth, screenHeight, camera);
				}
				
				logger.info("onMouse down-end3");
				defereredFocusCanvas();
				return;//ik selected
			}
		}
		
		
	}
	

	@Override
	public void onMouseUp(MouseUpEvent event) {
		logger.fine("onMouse up");
		
		mouseDown=false;
		dragObjectControler.unselectObject();
		logger.fine("6 drag");
	}
	
	@Override
	public void onMouseOut(MouseOutEvent event) {
		mouseDown=false;
		dragObjectControler.unselectObject();
	}
	
	
	private boolean mouseMoving;
	private double lastTime;
	private Mesh debugMesh;
	
	//private boolean doSync; never catched
	@Override
	public  void onMouseMove(MouseMoveEvent event) {
		if(mouseMoving){
			logger.info("conflict-move");
			if(lastTime+1000<System.currentTimeMillis()){
				logger.info("conflict-move-timeout");//never happen?
			}else{
			return;
			}
		}
		mouseMoving=true;
		lastTime=System.currentTimeMillis();
		double diffX=event.getX()-mouseDownX;
		double diffY=event.getY()-mouseDownY;
		
		int prevMouseDownX=mouseDownX;
		int prevMouseDownY=mouseDownY;
		
		mouseDownX=event.getX();
		mouseDownY=event.getY();
		
		
		
		double mouseMoved=(Math.abs(diffX)+Math.abs(diffY));
		if(mouseMoved==0){
			mouseMoving=false;
			return;
		}
		if(event.getNativeEvent().getButton()==NativeEvent.BUTTON_MIDDLE){
		changeCamera((int)diffX,(int)diffY,event.isShiftKeyDown(),event.isAltKeyDown(),event.isControlKeyDown());
		mouseMoving=false;
		return;
		}
		
		
		
		if(mouseDown){
			//log(diffX+","+diffY);
			logger.fine("mouse move");
			/*
			 * useless
			int limit=10;
			int limitX=(int) diffX;
			int limitY=(int) diffY;
			if(diffX>limit){
				limitX=limit;
			}
			if(diffY>limit){
				limitY=limit;
			}
			if(diffX<-limit){
				limitX=-limit;
			}
			if(diffY<-limit){
				limitY=-limit;
			}
			*/
			
			if(isSelectedIk()){
				logger.fine("selected ik");
				
				diffX*=0.1;
				diffY*=-0.1;
				Vector3 old=getCurrentIkData().getTargetPos().clone();
				if(dragObjectControler.isSelected()){
					//int eventX=prevMouseDownX+limitX;
					//int eventY=prevMouseDownY+limitY;
					int eventX=event.getX();
					int eventY=event.getY();
					
					logger.fine("selected dragObjectControler");
					
					scene.updateMatrixWorld(true);//very important.sometime selectedDraggablekObject.getParent().getMatrixWorld() return 0.
					Vector3 newPos=dragObjectControler.moveSelectionPosition(eventX,eventY, screenWidth, screenHeight, camera);
					
					if(newPos==null){
						logger.info("newPos-null:"+ThreeLog.get(dragObjectControler.getDraggableOffset()));
						mouseMoving=false;
						return;
					}
					double length=newPos.clone().sub(getCurrentIkData().getTargetPos()).length();
					/*
					if(newPos.getY()<8){
						
					log("error-newPos:"+ThreeLog.get(newPos));
					log("error-mouse:"+eventX+","+eventY);
					log("error-diff:"+diffX+","+diffY);
					log("error-diff-move:"+length+",mouse:"+mouseMoved+",offset:"+ThreeLog.get(dragObjectControler.getDraggableOffset()));
					log("error-log:"+dragObjectControler.getLog());
					}else{
						log("newPos:"+ThreeLog.get(newPos));
						log("mouse:"+eventX+","+eventY);
						log("diff:"+diffX+","+diffY);
						log("diff-move:"+length+",mouse:"+mouseMoved+",offset:"+ThreeLog.get(dragObjectControler.getDraggableOffset()));
						log("log:"+dragObjectControler.getLog());
					}
					*/
					
					if(length<mouseMoved*2 && mouseMoved!=0){
						if(length>mouseMoved || length>5){
							logger.fine("diff-move:"+length+",mouse:"+mouseMoved+",offset:"+ThreeLog.get(dragObjectControler.getDraggableOffset()));
							logger.fine("mouseDown:"+mouseDownX+","+mouseDownY);
						}
						
						//getCurrentIkData().getTargetPos().copy(newPos); // iguess 
						getCurrentIkData().setTargetPos(newPos);
						
						}else{
						
						logger.fine("diff-error:"+length+",mouse:"+mouseMoved+",offset:"+ThreeLog.get(dragObjectControler.getDraggableOffset()));
						if(length>mouseMoved*10 && mouseMoved!=0){//invalid
							//dragObjectControler.unselectObject();
							//dragObjectControler.selectObject(dragObjectControler.getSelectedDraggablekObject(), event.getX(), event.getY(), screenWidth, screenHeight, camera);
							
						}
					}
					
				}
				
				
				//log("diff-moved:"+ThreeLog.get(old.subSelf(getCurrentIkData().getTargetPos())));
				
				/*
				getCurrentIkData().getTargetPos().incrementX(diffX);
				getCurrentIkData().getTargetPos().incrementY(diffY);
				*/
				
				if(event.isAltKeyDown()){//slow
					if(event.isShiftKeyDown()){
					
					doPoseIkk(0,false,1,getCurrentIkData(),1);
					
						for(IKData ik:getAvaiableIkdatas()){
							//log("ik:"+ik.getName());
							if(ik!=getCurrentIkData()){
							doPoseIkk(0,false,5,ik,1);
							}
						}	
					}else{
					//not work correctly
					doPoseIkk(0,false,1,getCurrentIkData(),10);
					}
				}else if(event.isShiftKeyDown()){//move only
					//doPoseIkk(0,true,1,getCurrentIkData(),1);
					doPoseByMatrix(ab);
					//doPoseIkk(0,false,1,getCurrentIkData(),0);
				}else{
					doPoseIkk(0,true,1,getCurrentIkData(),5);
				}
				
				updateIkPositionLabel();
			}else if(isSelectedBone()){
				logger.info("selected bone:"+event.isShiftKeyDown());
				if(event.isShiftKeyDown()){//move position
					
					int boneIndex=ab.getBoneIndex(selectedBone);
					Vector3 pos=null;
					if(boneIndex==0){//this version support moving root only
						pos=ab.getBonePosition(boneIndex);
					}else{
						mouseMoving=false;
						return;
					}
					
					
					if(dragObjectControler.isSelected()){
						Vector3 newPos=dragObjectControler.moveSelectionPosition(event.getX(), event.getY(), screenWidth, screenHeight, camera);
						double length=newPos.clone().length();
						
						if(length<mouseMoved*50){
							if(length>mouseMoved*10){
								logger.fine("diff-move:"+length+",mouse="+mouseMoved);
							}
							//getCurrentIkData().getTargetPos().copy(newPos);	
							
							positionXBoneRange.setValue((int)(newPos.getX()*100)); //what is 100?
							positionYBoneRange.setValue((int)(newPos.getY()*100));
							positionZBoneRange.setValue((int)(newPos.getZ()*100));
							logger.fine("moved:"+length+",mouse="+mouseMoved);
						}else{
							logger.info("diff-error:"+length+",mouse="+mouseMoved);
						}
						
					}
					
					
					//positionXBoneRange.setValue(positionXBoneRange.getValue()+(int)diffX);
					//positionYBoneRange.setValue(positionYBoneRange.getValue()-(int)diffY);
					
					
					
					positionToBone();
					
					if(event.isAltKeyDown()){//not follow ik
					//	switchSelectionIk(null);
					//effect-ik
					for(IKData ik:getAvaiableIkdatas()){
						
						doPoseIkk(0,false,5,ik,1);
						}
					}else{ //follow ik
						
						
						if(boneIndex==0){
							/*
							Vector3 movedPos=ab.getBonePosition(boneIndex);
						movedPos.sub(pos);
						
						
						for(IKData ik:getAvaiableIkdatas()){
							ik.getTargetPos().add(movedPos);
							}
							*/
						fitIkOnBone();
						
						doPoseByMatrix(ab);//redraw
						}
					}
				}else{//change angle
					
				
				Vector3 angle=ab.getBoneAngleAndMatrix(selectedBone).getDegreeAngle();
				
				rotationBoneXRange.setValue(getRotationRangeValue(rotationBoneXRange.getValue(),(int)diffY));
				rotationBoneYRange.setValue(getRotationRangeValue(rotationBoneYRange.getValue(),(int)diffX));
				
				//rotationBoneXRange.setValue(rotationBoneXRange.getValue()+diffY);
				//rotationBoneYRange.setValue(rotationBoneYRange.getValue()+diffX);
				
				rotToBone();
				if(event.isAltKeyDown()){
				//	switchSelectionIk(null);
				//effect-ik
				for(IKData ik:getAvaiableIkdatas()){
					
					doPoseIkk(0,false,5,ik,1);
					}
				}else{
					//Vector3 rootPos=ab.getBonePosition(0);
					Vector3 movedAngle=ab.getBoneAngleAndMatrix(selectedBone).getDegreeAngle().clone();
					movedAngle.sub(angle);
					//log("before:"+ThreeLog.get(angle)+" moved:"+ThreeLog.get(movedAngle));
					//Matrix4 mx=GWTThreeUtils.degitRotationToMatrix4(movedAngle);
					
					
					/*
					for(IKData ik:getAvaiableIkdatas()){
						
						String name=ik.getLastBoneName();
						//Vector3 pos=ab.getBonePosition(name);
						
						if(existBone(name)){
							ik.getTargetPos().copy(getDefaultIkPos(ab.getBoneIndex(name)));
						}
						//ik.getTargetPos().set(pos.getX(), pos.getY(), pos.getZ());
						
						}
						*/
					fitIkOnBone();
					doPoseByMatrix(ab);//redraw
				}
				
				
				}
			}
			else{//global
				logger.fine("global");
			
			changeCamera((int)diffX,(int)diffY,event.isShiftKeyDown(),event.isAltKeyDown(),event.isControlKeyDown());
			
			}
		}else{
			/*useless
			if(!dragObjectControler.isSelected()){
				JsArray<Intersect> intersects=projector.gwtPickIntersects(event.getX(), event.getY(), screenWidth, screenHeight,camera, GWTThreeUtils.toPositionVec(camera.getMatrixWorld()),scene);
				//log("intersects-length:"+intersects.length());
				for(int i=0;i<intersects.length();i++){
					Intersect sect=intersects.get(i);
					Object3D target=sect.getObject();
					if(!target.getName().isEmpty()){
						if(target.getName().startsWith("ik:")){
							
							if(target!=dragObjectControler.getIntersectedDraggablekObject()){
								dragObjectControler.setIntersectedDraggablekObject(target);
							}
							//dragObjectControler.selectObject(target, event.getX(), event.getY(), screenWidth, screenHeight, camera);
							
						}
					}
				}
			}*/
		}
		mouseMoving=false;
	}
	
	private void changeCamera(int diffX,int diffY,boolean shiftKey,boolean AltKey,boolean ctrlKey){
		if(shiftKey){
			positionXRange.setValue(positionXRange.getValue()+diffX);
			positionYRange.setValue(positionYRange.getValue()-diffY);
		}else{
			rotationXRange.setValue(getRotationRangeValue(rotationXRange.getValue(),diffY));
			rotationYRange.setValue(getRotationRangeValue(rotationYRange.getValue(),diffX));
		}
	}
	
	
	private int getRotationRangeValue(int oldValue,int diffValue){
		int angle=oldValue+diffValue;
		if(angle>180){
			int over=angle-180;
			angle=-180+over;
		}
		if(angle<-180){
			int under=angle+180;
			angle=180+under;
		}
		return angle;
	}
	
	private boolean isSelectedBone(){
		return !isSelectedIk() && selectedBone!=null;
	}
	private IKData getCurrentIkData(){
		return ikdatas.get(ikdataIndex);
	}
	
	@Override
	public void onMouseWheel(MouseWheelEvent event) {
		if(isSelectedIk()){//IK 
			double dy=event.getDeltaY()*2.0/posDivided;
			getCurrentIkData().getTargetPos().gwtIncrementZ(dy);
			
			if(event.isAltKeyDown()){
				if(event.isShiftKeyDown()){//IK selected with ALT+Shift on mouseWheel,? bugs
					doPoseIkk(0,false,1,getCurrentIkData(),1);
						for(IKData ik:getAvaiableIkdatas()){
							if(ik!=getCurrentIkData()){
							doPoseIkk(0,false,5,ik,1);
							}
						}	
					}else{//IK selected with ALT on mouseWheel,Do move bones mildly.
				doPoseIkk(0,false,1,getCurrentIkData(),10);
					}
			}else if(event.isShiftKeyDown()){//IK selected with Shift on mouseWheel,Do move IK only
				
				//doPoseIkk(0,true,1,getCurrentIkData(),1);
				//LogUtils.log("shift-key");
				doPoseByMatrix(ab);
			}else{
				doPoseIkk(0,true,1,getCurrentIkData(),5);
			}
			
			updateIkPositionLabel();
		}else if(isSelectedBone()){
			if(event.isShiftKeyDown()){//move
			int diff=event.getDeltaY();
			
			int boneIndex=ab.getBoneIndex(selectedBone);
			Vector3 pos=null;
			if(boneIndex==0){//this version support moving root only
				pos=ab.getBonePosition(boneIndex);
			}
			
			
			positionZBoneRange.setValue(positionZBoneRange.getValue()+diff);
			positionToBone();
			if(event.isAltKeyDown()){
				//switchSelectionIk(null);
				//effect-ik
				execIk(5, 1);
				/*
				for(IKData ik:getAvaiableIkdatas()){
					
					
						if(ik!=getCurrentIkData()){//no need re-ik root?
						doPoseIkk(0,false,5,ik,1);
						}
						
					
					}*/
				}else{//ik-follow
					if(boneIndex==0){
					Vector3 moved=ab.getBonePosition(boneIndex);
					moved.sub(pos);
					for(IKData ik:getAvaiableIkdatas()){
						ik.getTargetPos().add(moved);
						}
					}
					
				}
			
			}else{
			int diff=event.getDeltaY();
			
			Vector3 angle=ab.getBoneAngleAndMatrix(selectedBone).getDegreeAngle();
			rotationBoneZRange.setValue(getRotationRangeValue(rotationBoneZRange.getValue(),diff));
			//rotationBoneZRange.setValue(rotationBoneZRange.getValue()+diff);
			rotToBone();
				if(event.isAltKeyDown()){
				//	switchSelectionIk(null);
				//effect-ik
					/*
				for(IKData ik:getAvaiableIkdatas()){
					
					doPoseIkk(0,false,5,ik,1);
					}
					*/
					execIk(5, 1);
				}else{
					//Vector3 rootPos=ab.getBonePosition(0);
					
					Vector3 movedAngle=ab.getBoneAngleAndMatrix(selectedBone).getDegreeAngle().clone();
					movedAngle.sub(angle);
					//logger.fine("before:"+ThreeLog.get(angle)+" moved:"+ThreeLog.get(movedAngle));
					//Matrix4 mx=GWTThreeUtils.degitRotationToMatrix4(movedAngle);
					
					
					
					//move green-ik-indicator
					//--this is keep green ik-bone position
					/*
					for(IKData ik:getAvaiableIkdatas()){
						
						
						
						
						String name=ik.getLastBoneName();
						//Vector3 pos=ab.getBonePosition(name);
						//ik.getTargetPos().set(pos.getX(), pos.getY(), pos.getZ());
						
						//LogUtils.log("ik:"+name);
						
						//some difference bone call this
						if(existBone(name)){//duplicate?
							ik.getTargetPos().copy(getDefaultIkPos(ab.getBoneIndex(name)));
							}
						}
						*/
					fitIkOnBone();
						
					
					doPoseByMatrix(ab);//redraw
				}
			}
		}
		else{//use small version
			double tzoom=5.0/posDivided;
			//TODO make class
			long t=System.currentTimeMillis();
			if(mouseLast+100>t){
				czoom*=2;
			}else{
				czoom=tzoom;
			}
			//GWT.log("wheel:"+event.getDeltaY());
			double tmp=cameraZ+event.getDeltaY()*czoom;
			tmp=Math.max(20.0/posDivided, tmp);
			tmp=Math.min(4000, tmp);
			cameraZ=(double)tmp;
			mouseLast=t;
		}
		
	}
	private double czoom;
	
	

	private InputRangeWidget positionXRange;
	private InputRangeWidget positionYRange;
	private InputRangeWidget positionZRange;
	//private InputRangeWidget frameRange;
	
	private InputRangeWidget rotationXRange;
	private InputRangeWidget rotationYRange;
	private InputRangeWidget rotationZRange;
	private InputRangeWidget rotationBoneXRange;
	private InputRangeWidget rotationBoneYRange;
	private InputRangeWidget rotationBoneZRange;
	private PopupPanel bottomPanel;
	private InputRangeWidget currentFrameRange;
	private Label currentFrameLabel;
	private InputRangeWidget positionXBoneRange;
	private InputRangeWidget positionYBoneRange;
	private InputRangeWidget positionZBoneRange;
	private CheckBox ylockCheck;
	private CheckBox xlockCheck;
	private List<String> ikLocks=new ArrayList<String>();
	private CheckBox showBonesCheck,ikVisibleCheck,smallCheck,showFingersCheck;
	
	private int posDivided=10;	//how small 10 or 100
	private CheckBox showBackgroundCheck;
	private VerticalPanel ikPositionsPanel;
	private Label ikPositionLabelY;
	private Label ikPositionLabelZ;

	
	
	private Label boneRotateLimitXLabel;
	private Label boneRotateLimitYLabel;
	private Label boneRotateLimitZLabel;
	private InputRangeWidget meshRotationYRange;
	@Override
	public void createControl(DropVerticalPanelBase parent) {
		Window.addCloseHandler(new CloseHandler<Window>() {
			@Override
			public void onClose(CloseEvent<Window> event) {
				//this call fix slow closing problem
				LogUtils.log("close");
				animationHandler.cancel();
			}
		});
		
		nearCamera=0.0001;//TODO scale up character
		HorizontalPanel h1=new HorizontalPanel();


		rotationXRange = InputRangeWidget.createInputRange(-180,180,0);
		
		Label rotateXLabel=HTML5Builder.createRangeLabel("X-Rotate:", rotationXRange);
		rotateXLabel.setWidth("120px");
		parent.add(rotateXLabel);
		
		
		
		
		parent.add(h1);
		h1.add(rotationXRange);
		Button reset=new Button("Reset");
		reset.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				rotationXRange.setValue(0);
			}
		});
		h1.add(reset);
		
		HorizontalPanel h2=new HorizontalPanel();
		
		rotationYRange = InputRangeWidget.createInputRange(-180,180,0);
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
		rotationZRange = InputRangeWidget.createInputRange(-180,180,0);
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
		positionXRange = InputRangeWidget.createInputRange(-300,300,0);
		parent.add(HTML5Builder.createRangeLabel("X-Position:", positionXRange,posDivided));
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
		positionYRange = InputRangeWidget.createInputRange(-300,300,0);
		parent.add(HTML5Builder.createRangeLabel("Y-Position:", positionYRange,posDivided));
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
		positionZRange = InputRangeWidget.createInputRange(-600,600,0);
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
		
		HorizontalPanel vPanel=new HorizontalPanel();
		parent.add(vPanel);
		
		transparentCheck = new CheckBox();
		vPanel.add(transparentCheck);
		transparentCheck.setText("transparent");
		transparentCheck.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				updateMaterial();
			}
		});
		
		transparentCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				try {
					storageControler.setValue(KEY_TRANSPARENT, ""+event.getValue());
				} catch (StorageException e) {
					//not important
					LogUtils.log("storage error:"+e.getMessage());
				}
			}
			
		});
		
		try {
			transparentCheck.setValue(ValuesUtils.toBoolean(storageControler.getValue(KEY_TRANSPARENT, "false"),false));
		} catch (StorageException e) {
			LogUtils.log("storage error:"+e.getMessage());
		}
		
		basicMaterialCheck = new CheckBox();
		vPanel.add(basicMaterialCheck);
		basicMaterialCheck.setText("BasicMaterial");
		basicMaterialCheck.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				updateMaterial();
			}
		});
		
			try {
				basicMaterialCheck.setValue(ValuesUtils.toBoolean(storageControler.getValue(KEY_BASIC_MATERIAL, "false"),false));
			} catch (StorageException e) {
				LogUtils.log("storage error:"+e.getMessage());
			}
		
		basicMaterialCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				try {
					storageControler.setValue(KEY_BASIC_MATERIAL, ""+event.getValue());
				} catch (StorageException e) {
					//not important
					LogUtils.log("storage error:"+e.getMessage());
				}
			}
			
		});
		
		/*
		 * i tried but behavior totally wrong.i should learn cdd ik again.
		HorizontalPanel optionPanel=new HorizontalPanel();
		parent.add(optionPanel);
		CheckBox useEndsite=new CheckBox("use end-site");
		optionPanel.add(useEndsite);
		useEndsite.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				isIkTargetEndSite=event.getValue();
				fitIkOnBone();
			}
		});
		*/
		
		
		HorizontalPanel shows=new HorizontalPanel();
		parent.add(shows);
		showBonesCheck = new CheckBox();
		shows.add(showBonesCheck);
		showBonesCheck.setText("Show Bones");
		showBonesCheck.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				updateBonesVisible(showBonesCheck.getValue());
			}
		});
		showBonesCheck.setValue(true);
		
		showFingersCheck = new CheckBox();
		parent.add(showFingersCheck);
		showFingersCheck.setText("Show Fingers");//basically no need
		showFingersCheck.setValue(false);
		showFingersCheck.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				updateFingerBonesVisible(showFingersCheck.getValue());
			}
		});
		
		
		smallCheck = new CheckBox();
		shows.add(smallCheck);
		smallCheck.setText("small");
		smallCheck.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				updateBonesSize();
			}
		});
		smallCheck.setValue(false);
		
		ikVisibleCheck = new CheckBox();
		shows.add(ikVisibleCheck);
		ikVisibleCheck.setText("Iks");
		ikVisibleCheck.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				updateIKVisible(ikVisibleCheck.getValue());
			}
		});
		ikVisibleCheck.setValue(true);
		
		
		final CheckBox showWireframeCheck = new CheckBox("wireframe");
		
		showBackgroundCheck = new CheckBox("Tile");
		shows.add(showBackgroundCheck);
		showBackgroundCheck.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean allVisible=showBackgroundCheck.getValue();
				boolean wire=showWireframeCheck.getValue();
				if(allVisible){
					updateBackgroundVisible(wire);
					planeMesh.setVisible(!wire);
				}else{
					updateBackgroundVisible(false);
					planeMesh.setVisible(false);
				}
				
			}
		});
		showBackgroundCheck.setValue(true);
		
		
		shows.add(showWireframeCheck);
		showWireframeCheck.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean allVisible=showBackgroundCheck.getValue();
				boolean wire=showWireframeCheck.getValue();
				if(allVisible){
					updateBackgroundVisible(wire);
					planeMesh.setVisible(!wire);
				}else{
					updateBackgroundVisible(false);
					planeMesh.setVisible(false);
				}
				
			}
		});
		showWireframeCheck.setValue(true);
		
		//dont need now
		/*
		HorizontalPanel frames=new HorizontalPanel();
		frameRange = InputRangeWidget.createInputRange(0,1,0);
		parent.add(HTML5Builder.createRangeLabel("Frame:", frameRange));
		//parent.add(frames);
		frames.add(frameRange);
		*/
		/*
		frameRange.addListener(InputRangeWidget.createInputRangeListener() {
			
			@Override
			public void changed(int newValue) {
				doPose(frameRange.getValue());
			}
		});
		*/
		
	
		//
		HorizontalPanel boneInfo=new HorizontalPanel();
		boneInfo.setWidth("250px");
		
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
		ikLockCheck.setTitle("add bone to iklocks");
		
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
		
		//mirror
		HorizontalPanel mButtons=new HorizontalPanel();
		parent.add(mButtons);
		
		//because it' natural from front-view
		Button rightToLeft=new Button("R > L");
		rightToLeft.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doMirror(true);
				
			}
		});
		mButtons.add(rightToLeft);
		
		
		Button mirror=new Button("L > R");
		mirror.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doMirror(false);
				
			}
		});
		mButtons.add(mirror);
		
		
		
		Button swap=new Button("do Swap");
		swap.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doSwap();
				
			}
		});
		mButtons.add(swap);
		
		
		bonePostionAndRotationContainer = new VerticalPanel();
		bonePostionAndRotationContainer.setSize("210px", "150px");
		bonePostionAndRotationContainer.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		parent.add(bonePostionAndRotationContainer);
		
		
		//ikspos
		ikPositionsPanel = new VerticalPanel();
		bonePostionAndRotationContainer.add(ikPositionsPanel);
		ikPositionsPanel.setVisible(false);
		
		ikPositionLabelX = new Label();
		ikPositionsPanel.add(ikPositionLabelX);
		ikPositionLabelY = new Label();
		ikPositionsPanel.add(ikPositionLabelY);
		ikPositionLabelZ = new Label();
		ikPositionsPanel.add(ikPositionLabelZ);
		
		
		
		//positions
		bonePositionsPanel = new VerticalPanel();
		bonePostionAndRotationContainer.add(bonePositionsPanel);
		bonePositionsPanel.setVisible(true);
		
		HorizontalPanel h1bpos=new HorizontalPanel();
		positionXBoneRange = InputRangeWidget.createInputRange(-60000/posDivided,60000/posDivided,0);
		bonePositionsPanel.add(HTML5Builder.createRangeLabel("X-Pos:", positionXBoneRange,posDivided));
		bonePositionsPanel.add(h1bpos);
		h1bpos.add(positionXBoneRange);
		createPosRangeControlers(positionXBoneRange,h1bpos);
		positionXBoneRange.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				positionToBone();
			}
		});
		
		HorizontalPanel h2bpos=new HorizontalPanel();
		
		positionYBoneRange = InputRangeWidget.createInputRange(-60000/posDivided,60000/posDivided,0);
		bonePositionsPanel.add(HTML5Builder.createRangeLabel("Y-Pos:", positionYBoneRange,posDivided));
		bonePositionsPanel.add(h2bpos);
		h2bpos.add(positionYBoneRange);
		createPosRangeControlers(positionYBoneRange,h2bpos);
		positionYBoneRange.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				positionToBone();
			}
		});
		
		
		HorizontalPanel h3bpos=new HorizontalPanel();
		positionZBoneRange = InputRangeWidget.createInputRange(-60000/posDivided,60000/posDivided,0);
		bonePositionsPanel.add(HTML5Builder.createRangeLabel("Z-Pos:", positionZBoneRange,posDivided));
		bonePositionsPanel.add(h3bpos);
		h3bpos.add(positionZBoneRange);
		
		createPosRangeControlers(positionZBoneRange,h3bpos);
		
		/*
		Button reset3bpos=new Button("Reset");
		reset3bpos.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				positionZBoneRange.setValue(0);
				positionToBone();
			}
		});
		h3bpos.add(reset3bpos);
		*/
		
		
		positionZBoneRange.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				positionToBone();
			}
		});
		
		
		
		
		
		boneRotationsPanel = new VerticalPanel();
		bonePostionAndRotationContainer.add(boneRotationsPanel);
		
		HorizontalPanel rotButtons=new HorizontalPanel();
		rotButtons.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		boneRotationsPanel.add(rotButtons);
		Button resetAll=new Button("Reset All Rotation",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				rotationBoneXRange.setValue(0);
				rotationBoneYRange.setValue(0);
				rotationBoneZRange.setValue(0);
				rotToBone();
				
				if(event.isAltKeyDown()){
					execIk(5,1);
				}
			}
		});
		rotButtons.add(resetAll);
		
		//flip angle controler
		rotButtons.add(new Label("Flip"));
		final ListBox flipBox=new ListBox();
		flipBox.addItem("");
		flipBox.addItem("X");
		flipBox.addItem("Y");
		flipBox.addItem("Z");
		rotButtons.add(flipBox);
		flipBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				
				int index=flipBox.getSelectedIndex();
				if(index==0){
					return;
				}
				if(index==1){
					rotationBoneXRange.setValue(-rotationBoneXRange.getValue());
					flipBox.setSelectedIndex(0);
				}
				else if(index==2){
					rotationBoneYRange.setValue(-rotationBoneYRange.getValue());
					flipBox.setSelectedIndex(0);
				}
				else if(index==3){
					rotationBoneZRange.setValue(-rotationBoneZRange.getValue());
					flipBox.setSelectedIndex(0);
				}
				
				rotToBone();
				
				
			}
		});
		
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
		xlockCheck.setTitle("lock this axis");
		
		
		
		HorizontalPanel labelPanelX=new HorizontalPanel();
		boneRotationsPanel.add(labelPanelX);
		
		rotationBoneXRange = InputRangeWidget.createInputRange(-180,180,0);
		Label boneRotateXLabel=HTML5Builder.createRangeLabel("X-Rotate:", rotationBoneXRange);
		labelPanelX.add(boneRotateXLabel);
		boneRotateXLabel.setWidth("120px");
		
		boneRotateLimitXLabel=new Label();
		labelPanelX.add(boneRotateLimitXLabel);
		
		
		
		boneRotationsPanel.add(h1b);
		h1b.add(rotationBoneXRange);
		createRotRangeControlers(rotationBoneXRange,h1b);
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
		ylockCheck.setTitle("lock this axis");
		
		HorizontalPanel labelPanelY=new HorizontalPanel();
		boneRotationsPanel.add(labelPanelY);
		
		rotationBoneYRange = InputRangeWidget.createInputRange(-180,180,0);
		
		Label boneRotateYLabel=HTML5Builder.createRangeLabel("Y-Rotate:", rotationBoneYRange);
		boneRotateYLabel.setWidth("120px");
		labelPanelY.add(boneRotateYLabel);
		
		boneRotateLimitYLabel=new Label();
		labelPanelY.add(boneRotateLimitYLabel);
		
		
		boneRotationsPanel.add(h2b);
		h2b.add(rotationBoneYRange);
		
		createRotRangeControlers(rotationBoneYRange,h2b);
		
		
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
		zlockCheck.setTitle("lock this axis");
		
		HorizontalPanel labelPanelZ=new HorizontalPanel();
		boneRotationsPanel.add(labelPanelZ);
		
		rotationBoneZRange = InputRangeWidget.createInputRange(-180,180,0);
		
		Label boneRotateZLabel=HTML5Builder.createRangeLabel("Z-Rotate:", rotationBoneZRange);
		boneRotateZLabel.setWidth("120px");
		labelPanelZ.add(boneRotateZLabel);
		
		boneRotateLimitZLabel=new Label();
		labelPanelZ.add(boneRotateLimitZLabel);
		
		boneRotationsPanel.add(h3b);
		h3b.add(rotationBoneZRange);
		
		createRotRangeControlers(rotationBoneZRange,h3b);
		
		rotationBoneZRange.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				rotToBone();
				if(event.isAltKeyDown()){
					execIk();
				}
			}
		});
		
		/*
		Button test=new Button("image",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String url=canvas.getRenderer().gwtPngDataUrl();//can'i snap shot with it.
				Window.open(url, "test", null);
			}
		});
		parent.add(test);
		*/
		
		/*
		 * crash so oftern if you use don't forget add
		 * THREE.WebGLRenderer(GWTRenderObject.create().preserveDrawingBuffer()); 
		 * 
		Button test=new Button("screen-shot");
		parent.add(test);
		test.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				//better screen shot
				
				//Keep before setting
				//change setting if
				//re render
				//back to setting
				
				
				String url=renderer.gwtPngDataUrl();
				
				//log(url);
				//String text="<img style='position:absolute;top:0;left:0' src='"+url+"'>";
				//ExportUtils.openTabHtml(text, "screenshot"+screenShotIndex);
				ExportUtils.openTabImage(url, "screenshot"+screenShotIndex);
				screenShotIndex++;
				
				
				//Window.open(url, "newwin"+screenShotIndex, null); sometime crash and kill owner
				screenShotIndex++;
			}
		});
		*/
		/*
		parent.add(new Label("Texture Image"));
		
		final FileUploadForm textureUpload=new FileUploadForm();
		parent.add(textureUpload);

		textureUpload.getFileUpload().addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				JsArray<File> files = FileUtils.toFile(event.getNativeEvent());
				
				final FileReader reader=FileReader.createFileReader();
				reader.setOnLoad(new FileHandler() {
					@Override
					public void onLoad() {
						//log("load:"+Benchmark.end("load"));
						//GWT.log(reader.getResultAsString());
						textureUrl=reader.getResultAsString();
						updateMaterial();
						
					}
				});
				reader.readAsDataURL(files.get(0));
				textureUpload.reset();
			}
		});
		*/
		
		/*
		CheckBox do1small=new CheckBox("x 0.1");
		do1small.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				onTotalSizeChanged(event.getValue());
			}
		});
		parent.add(do1small);
		*/
		
		
		
		positionYRange.setValue(defaultOffSetY);//for test
		
		
		
		//mesh-control
		
		parent.add(createMeshMatrixControlPanel());
		
		updateIkLabels();
		createBottomPanel();
		showControl();
		
	}
	private Panel createMeshMatrixControlPanel(){
		VerticalPanel meshControler=new VerticalPanel();
		
		meshControler.add(new Label("Mesh-Matrix"));
		
		
		meshRotationYRange = InputRangeWidget.createInputRange(-180,180,0);
		meshRotationYRange.addInputRangeListener(new InputRangeListener() {
			
			@Override
			public void changed(int newValue) {
				doPoseByMatrix(ab);
			}
		});
		
		meshRotationYRange.setWidth(240);
		
		Label meshRotateYLabel=HTML5Builder.createRangeLabel("Y-Rotate:", meshRotationYRange);
		meshRotateYLabel.setWidth("120px");
		meshControler.add(meshRotateYLabel);
		
		meshControler.add(meshRotationYRange);
		
		//positions
		HorizontalPanel xposPanel=new HorizontalPanel();
		meshControler.add(xposPanel);
		Label xposLabel=new Label("X-Pos:");
		xposPanel.add(xposLabel);
		meshPositionXBox = new DoubleBox();
		meshPositionXBox.setValue(0.0);
		xposPanel.add(meshPositionXBox);
		meshPositionXBox.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				doPoseByMatrix(ab);
			}
		});
		
		return meshControler;
	}
	/*
	int upscale=1;
	protected void onTotalSizeChanged(Boolean value) {
		if(value){
			upscale=10;
		}else{
			upscale=1;
		}
		
		List<Mesh> meshs=Lists.newArrayList();
		//meshs.add(selectionMesh);
		
		meshs.addAll(vertexMeshs);
		meshs.addAll(boneJointMeshs);
		meshs.addAll(boneCoreMeshs);
		meshs.addAll(endPointMeshs);
		
		
		if(selectionPointIndicateMesh!=null){
		meshs.add(selectionPointIndicateMesh);
		}
		if(boneSelectionMesh!=null){
			meshs.add(boneSelectionMesh);
			}
			
		
		//redo-bone and vertex
		for(Mesh mesh:meshs){
			double scale=(1.0/upscale);
			mesh.getScale().set(scale,scale,scale);
		}
	}
	*/
	
	public int getPosDivided() {
		return posDivided;
	}

	boolean isIkTargetEndSite;
	
	private void createPosRangeControlers(final InputRangeWidget range,HorizontalPanel parent){
		Button minus3b=new Button("-");
		minus3b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				range.setValue(range.getValue()-10);
				positionToBone();
				if(event.isAltKeyDown()){
					execIk(5,1);
				}
			}
		});
		parent.add(minus3b);
		
		Button minus=new Button("-.");
		minus.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				range.setValue(range.getValue()-1);
				positionToBone();
				if(event.isAltKeyDown()){
					execIk(5,1);
				}
			}
		});
		parent.add(minus);
		
		Button zero=new Button("0");
		zero.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				range.setValue(0);
				positionToBone();
				if(event.isAltKeyDown()){
					execIk(5,1);
				}
			}
		});
		parent.add(zero);
		
		Button plus3b=new Button("+.");
		plus3b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				range.setValue(range.getValue()+1);
				positionToBone();
				if(event.isAltKeyDown()){
					execIk(5,1);
				}
			}
		});
		parent.add(plus3b);
		
		
		Button plus=new Button("+");
		plus.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				range.setValue(range.getValue()+10);
				positionToBone();
				if(event.isAltKeyDown()){
					execIk(5,1);
				}
			}
		});
		parent.add(plus);
	}
	
	private void createRotRangeControlers(final InputRangeWidget range,HorizontalPanel parent){
		Button minus3b=new Button("-");
		minus3b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				range.setValue(range.getValue()-1);
				rotToBone();
				if(event.isAltKeyDown()){
					execIk(5,1);
				}
			}
		});
		parent.add(minus3b);
		
		
		List<Integer> angles=Lists.newArrayList(-180,-135,-90,-60,-45,0,45,60,90,135,180);
		final ValueListBox<Integer> vlist=new ValueListBox<Integer>(new Renderer<Integer>() {

			@Override
			public String render(Integer object) {
				if(object==null){
					return "";
				}
				return String.valueOf(object);
			}

			@Override
			public void render(Integer object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		vlist.setValue(null);
		vlist.setAcceptableValues(angles);
		vlist.addValueChangeHandler(new ValueChangeHandler<Integer>() {

			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				if(event.getValue()==null){
					return;
				}
				range.setValue(event.getValue());
				rotToBone();
				vlist.setValue(null);//reset to
				/*
				if(event.isAltKeyDown()){
					execIk();
				}
				*/
			}
		});
		
		parent.add(vlist);
		Button plus3b=new Button("+");
		plus3b.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				range.setValue(range.getValue()+1);
				rotToBone();
				if(event.isAltKeyDown()){
					execIk(5,1);
				}
			}
		});
		parent.add(plus3b);
	}
	
	
	
	
	
	protected void doSwap() {
		List<AngleAndPosition> lastAAP=cloneAngleAndPositions(ab.getBonesAngleAndMatrixs());
		if(isSelectedIk() && getSelectedBoneName().isEmpty()){
			IKData ik=getCurrentIkData();
			for(String name:ik.getBones()){
				String targetName=getMirroredName(name);
				if(targetName==null){
					continue;
				}
				int index=ab.getBoneIndex(targetName);
				int srcIndex=ab.getBoneIndex(name);
				if(index!=-1 && srcIndex!=-1){
					Vector3 angle1=ab.getBoneAngleAndMatrix(srcIndex).getDegreeAngle();
					
					Vector3 angle=ab.getBoneAngleAndMatrix(index).getDegreeAngle();
					rotToBone(name, angle.getX(), -angle.getY(), -angle.getZ(),false);
					
					rotToBone(targetName, angle1.getX(), -angle1.getY(), -angle1.getZ(),true);
				}
			}
			//move ik pos
			IKData targetIk=getIk(getMirroredName(ik.getName()));
			if(targetIk!=null){
					Vector3 root=ab.getBonePosition(0);
					
					Vector3 targetPos=targetIk.getTargetPos().clone().sub(root);
					targetPos.setX(targetPos.getX()*-1);
					targetPos.add(root);
					
					Vector3 srcPos=ik.getTargetPos().clone().sub(root);
					srcPos.setX(srcPos.getX()*-1);
					srcPos.add(root);
					
					ik.getTargetPos().set(targetPos.getX(),targetPos.getY(),targetPos.getZ());
					targetIk.getTargetPos().set(srcPos.getX(),srcPos.getY(),srcPos.getZ());
					doPoseByMatrix(ab);
			}
			
			
		}else{
		String name=getSelectedBoneName();
		if(name==null){
			return;
		}
			//h mirror
			String targetName=getMirroredName(name);
			
			if(targetName==null){
				return;
			}
			
			int index=ab.getBoneIndex(targetName);
			if(index!=-1){
				
				Vector3 targetAngle=ab.getBoneAngleAndMatrix(index).getDegreeAngle();
				double x=rotationBoneXRange.getValue();
				double y=rotationBoneYRange.getValue()*-1;
				double z=rotationBoneZRange.getValue()*-1;
				
				
				rotationBoneXRange.setValue((int) targetAngle.getX());
				rotationBoneYRange.setValue((int) targetAngle.getY()*-1);
				rotationBoneZRange.setValue((int) targetAngle.getZ()*-1);
				rotToBone(targetName,x,y,z,false);
				rotToBone();
			}
		}
		List<AngleAndPosition> newAAP=cloneAngleAndPositions(ab.getBonesAngleAndMatrixs());
		AngleAndPositionsCommand command=new AngleAndPositionsCommand(newAAP, lastAAP);
		undoControler.addCommand(command);
	}

	private IKData getIk(String name){
		for(IKData ik:ikdatas){
			if(ik.getName().equals(name)){
				return ik;
			}
		}
		return null;
	}
	
	
	
	protected void doMirror(boolean rightToLeft) {
		List<AngleAndPosition> lastAAP=cloneAngleAndPositions(ab.getBonesAngleAndMatrixs());
		if(isSelectedIk() && getSelectedBoneName().isEmpty()){
			IKData ik=getCurrentIkData();
			for(String name:ik.getBones()){
				String targetName=getMirroredName(name);
				if(targetName==null){
					continue;
				}
				
				String srcBoneName;
				String destBoneName;
				
				if(isRightBone(name)){
					if(rightToLeft){
						srcBoneName=name;
						destBoneName=targetName;
					}else{
						srcBoneName=targetName;
						destBoneName=name;
					}
				}else{
					if(rightToLeft){
						srcBoneName=targetName;
						destBoneName=name;
					}else{
						srcBoneName=name;
						destBoneName=targetName;
					}
				}
				
				
				
				
				int index=ab.getBoneIndex(srcBoneName);
				if(index!=-1){
					Vector3 angle=ab.getBoneAngleAndMatrix(index).getDegreeAngle();
					rotToBone(destBoneName, angle.getX(), -angle.getY(), -angle.getZ(),false);
				}
			}
			//
			//Vector3 lastPosition=ik.getTargetPos().clone();
			
			//invoke not call
			fitIkOnBone();
			
			
			doPoseByMatrix(ab);//do it only once
			
			
			switchSelectionIk(ik.getLastBoneName());//recreate ik pose otherwise use old pose
			
			updateBoneRanges();//possible changed
			
			//ik.getTargetPos().copy(lastPosition);//restore position,usually user continue editing.but i change my mind ,when set opposite selection current ik pos make bad effect
			
		}else{//single selected bone
		String name=getSelectedBoneName();
		if(name==null){
			//somehow not selected
			return;
		}
			
			String targetName=getMirroredName(name);
			LogUtils.log("mirror:"+targetName);
			if(targetName==null){
				return;
			}
			
			int targetBoneIndex=ab.getBoneIndex(targetName);
			if(targetBoneIndex!=-1){
				if(isRightBone(name)){
					if(rightToLeft){
						rotToBone(targetName, rotationBoneXRange.getValue(),-rotationBoneYRange.getValue(),-rotationBoneZRange.getValue(),true);
					}else{
						//copy from left
						Vector3 angle=ab.getBoneAngleAndMatrix(targetBoneIndex).getDegreeAngle();
						rotationBoneXRange.setValue((int) angle.getX());
						rotationBoneYRange.setValue((int) angle.getY()*-1);
						rotationBoneZRange.setValue((int) angle.getZ()*-1);
						rotToBone();
					}
				}else{//left bone
					if(rightToLeft){
						Vector3 angle=ab.getBoneAngleAndMatrix(targetBoneIndex).getDegreeAngle();
						rotationBoneXRange.setValue((int) angle.getX());
						rotationBoneYRange.setValue((int) angle.getY()*-1);
						rotationBoneZRange.setValue((int) angle.getZ()*-1);
						rotToBone();
					}else{
						rotToBone(targetName, rotationBoneXRange.getValue(),-rotationBoneYRange.getValue(),-rotationBoneZRange.getValue(),true);
					}
				}
				
			}
		}
		
		List<AngleAndPosition> newAAP=cloneAngleAndPositions(ab.getBonesAngleAndMatrixs());
		AngleAndPositionsCommand command=new AngleAndPositionsCommand(newAAP, lastAAP);
		undoControler.addCommand(command);
		//no need invoke;
	}
	
	

	protected void updateBonesVisible(boolean value) {
		if(bone3D!=null){
			Object3DUtils.setVisibleAll(bone3D, value);
			if(value){
				updateFingerBonesVisible(showFingersCheck.getValue());
			}
		}
		
		if(skeltonHelper!=null){
			skeltonHelper.setVisible(value);
		}
		
	}
	
	protected void updateFingerBonesVisible(boolean value) {
		List<String> fingerNames=Lists.newArrayList("thumb","index","middle","pinky","ring");
		if(bone3D!=null){
			for(int i=0;i<bone3D.getChildren().length();i++){
				Object3D obj=bone3D.getChildren().get(i);
				String name=obj.getName();
				for(String fingerName:fingerNames){
					if(name.startsWith(fingerName)){
						obj.setVisible(value);
					}
				}
			}
			
		}
	}
	
	protected void updateBonesSize() {
		if(bone3D!=null){
			doPoseByMatrix(ab);//re-create
		}
	}
	protected void updateIKVisible(boolean value) {
		if(ik3D!=null){
			Object3DUtils.setVisibleAll(ik3D, value);
		}
	}
	
	protected void updateBackgroundVisible(boolean value) {
		if(backgroundGrid!=null){
			Object3DUtils.setVisibleAll(backgroundGrid,value);
		}
	}
	

	protected String getMirroredName(String name) {
		if(name.endsWith("_R")){
			return name.replace("_R", "_L");
		}
		if(name.endsWith("_L")){
			return name.replace("_L", "_R");
		}
		
		if(name.indexOf("Right")!=-1){
			return name.replace("Right", "Left");
		}
		if(name.indexOf("right")!=-1){
			return name.replace("right", "left");
		}
		if(name.indexOf("Left")!=-1){
			return name.replace("Left", "Right");
		}
		if(name.indexOf("left")!=-1){
			return name.replace("left", "right");
		}
		//makehuman 19 bones
		if(name.startsWith("r")){
			return "l"+name.substring(1);
		}
		else if(name.startsWith("l")){
			return "r"+name.substring(1);
		}
		
		return null;
	}
	
	
	private boolean isRightBone(String name){
		if(name.endsWith("_R")){
			return true;
		}
		
		if(name.indexOf("Right")!=-1){
			return true;
		}
		if(name.indexOf("right")!=-1){
			return true;
		}
		
		//makehuman 19 bones
		if(name.startsWith("r")){//rethink later
		//	return true;
		}
		
		return false;
	}
	

	private JsArray<Material> loadedMaterials;
	JSONModelFile lastLoadedModel;
	
	
	private void fixGeometryWeight(Geometry geometry){
		for(int i=0;i<geometry.getSkinWeight().length();i++){
			Vector4 vec4=geometry.getSkinWeight().get(i);
			double x=vec4.getX();
			double y=vec4.getY();
			//seems somehow 1.0 weight make problem?
			if(x==1){
				geometry.getSkinIndices().get(i).setY(geometry.getSkinIndices().get(i).getX());
			}else if(y==1){
				geometry.getSkinIndices().get(i).setX(geometry.getSkinIndices().get(i).getY());
			}else{//total value is under 1.0 bone usually make problem
			double total=x+y;
			if(total>1){
			//	LogUtils.log("invalid:"+total);
			}
			double remain=(1.0-total);
			
			double nx=(x/total)*remain+x;
			double ny=1.0-nx;
			vec4.setX(nx);
			vec4.setY(ny);
			
			//must be 1.0 ?
			}
		}
	}
	
	private void LoadJsonModel(String jsonText){
		try {
			JSONModelFile model=GWTThreeUtils.parseJsonObject(jsonText);
			lastLoadedModel=model;
			GWTThreeUtils.loadJsonModel(lastLoadedModel,new  JSONLoadHandler() {
				@Override
				public void loaded(Geometry geometry,JsArray<Material> materials) {
					if(bodyMesh!=null){
						rootObject.remove(bodyMesh);//for initialzie
						bodyMesh=null;
					}
					//LogUtils.log("material?");
					loadedMaterials=materials;
					
					ab=null;//for remake matrix.
					LogUtils.log("loadJsonModel:");
					LogUtils.log(geometry);
					
					
					//force scalling up
					//test scale up
					//TODO support scale on app
					double scale=10;
					for(int i=0;i<geometry.getVertices().length();i++){
						geometry.getVertices().get(i).multiplyScalar(scale);//or another way.
					}
					
					if(geometry.getBones()!=null){
						for(int i=0;i<geometry.getBones().length();i++){
							JsArrayNumber number=geometry.getBones().get(i).getPos();
							for(int j=0;j<number.length();j++){
								number.set(j, number.get(j)*scale);
							}
						}
					}
					
					//LogUtils.log(geometry);
					
					//fix geometry weight,otherwise broken model
					fixGeometryWeight(geometry);//TODO need my calcurate
					
					
					baseGeometry=geometry;//change body mesh
					
					LogUtils.log(baseGeometry.getBones());
					if(baseGeometry.getBones()!=null && baseGeometry.getBones().length()>0){
						LogUtils.log("create-bone from geometry:size="+baseGeometry.getBones().length());
						setBone(baseGeometry.getBones()); //possible broken bone.TODO test geometry bone
						
						/*
						logger.fine("testly use bone from bvh");//TODO move back
						AnimationBoneConverter converter=new AnimationBoneConverter();
						setBone(converter.convertJsonBone(bvh));
						*/
						
					}else{
						Window.alert("your loaded model has not contain bone,\nand use default bone.but this make many problem.\nplease export  your model with bone");
						logger.fine("bvh:"+bvh);
						LogUtils.log("use bvh bone:maybe this is problem");
						//initialize default bone
						AnimationBoneConverter converter=new AnimationBoneConverter();
						setBone(converter.convertJsonBone(bvh));
					}
					//log(""+(baseGeometry.getBones()!=null));
					//log(baseGeometry.getBones());
					
					doRePose(0);
					
					
					//log("snapped");
					if(poseEditorDatas.size()==0){//initial new list
						initialPoseFrameData=snapCurrentFrameData();//get invalid pose
						updateMaterial();
						doNewFile();
					}
					
					//update texture
					if(texture!=null){
					if(lastLoadedModel.getMetaData().getFormatVersion()==3){
						LogUtils.log("model-format is 3.0 and set flipY=false");
						texture.setFlipY(false);
					}else{
						texture.setFlipY(true);
					}
					}
				}
			});//texture tested.
		} catch (InvalidModelFormatException e) {
			LogUtils.log("LoadJsonModel:"+e.getMessage());
		}
		
		
		
	}
	private String getSelectedBoneName(){
		if(boneNamesBox.getSelectedIndex()==-1){
			return "";
		}
		return boneNamesBox.getValue(boneNamesBox.getSelectedIndex());
	}
	
	private void positionToBone(String name,double x,double y,double z){
		
		int index=ab.getBoneIndex(name);
		if(index!=0){
			//limit root only 
			//TODO limit by bvh channel
			return;
		}
		
		
		
		//LogUtils.log("increment-x:x="+x+",increment="+incrementMeshX);
		
		

		
		Vector3 pos=THREE.Vector3(x,y,z
				).multiplyScalar(0.01);//what is 100?
		
		/*
		Vector3 angles=GWTThreeUtils.rotationToVector3(ab.getBoneAngleAndMatrix(index).getMatrix());
				
		
		
		
		Matrix4 posMx=GWTThreeUtils.translateToMatrix4(pos);
		Matrix4 rotMx=GWTThreeUtils.rotationToMatrix4(angles);
		rotMx.multiply(posMx,rotMx);
		ab.getBoneAngleAndMatrix(index).setMatrix(rotMx);
		*/
		
		ab.getBoneAngleAndMatrix(index).setPosition(pos);
		ab.getBoneAngleAndMatrix(index).updateMatrix();
		
		doPoseByMatrix(ab);
		
		if( isSelectedBone()){
			selectionMesh.setPosition(pos);
		}
	}
	protected void positionToBone() {
		positionToBone(boneNamesBox.getItemText(boneNamesBox.getSelectedIndex()),
				positionXBoneRange.getValue(),
				positionYBoneRange.getValue(),positionZBoneRange.getValue());
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
	private Button redoButton;
	private void createBottomPanel(){
		bottomPanel = new PopupPanel();
		bottomPanel.setVisible(true);
		bottomPanel.setSize("650px", "106px");
		
		TabPanel bottomTab=new TabPanel();
		bottomTab.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				onBottomTabSelectionChanged(event.getSelectedItem());
			}
			
		});
		bottomPanel.add(bottomTab);
		
		VerticalPanel main=new VerticalPanel();
		bottomTab.add(main,"Edit");
		bottomTab.add(createPlayerPanel(),"Play");
		bottomTab.selectTab(0);
		
		HorizontalPanel trueTop=new HorizontalPanel();
		trueTop.setWidth("100%");
		main.add(trueTop);
		//upper
		HorizontalPanel topPanel=new HorizontalPanel();
		
		trueTop.add(topPanel);
		
		pedSelectionListBox=new ValueListBox<PoseEditorData>(new Renderer<PoseEditorData>() {

			@Override
			public String render(PoseEditorData object) {
				if(object==null){
					return "";
				}
				return object.getName();
			}

			@Override
			public void render(PoseEditorData object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		pedSelectionListBox.addValueChangeHandler(new ValueChangeHandler<PoseEditorData>() {
			@Override
			public void onValueChange(ValueChangeEvent<PoseEditorData> event) {
				event.getValue().updateMatrix(ab);//need bone data
				updatePoseEditorDatas();
			}
		});
		
		topPanel.add(pedSelectionListBox);
		
		
		
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
		
		
		undoButton = new Button("Undo");
		topPanel.add(undoButton);
		undoButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				undoControler.undo();
			}
		});
		undoButton.setEnabled(false);
		
		redoButton = new Button("Redo");
		topPanel.add(redoButton);
		redoButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				undoControler.redo();
			}
		});
		redoButton.setEnabled(false);
		
		
		HorizontalPanel rightSide=new HorizontalPanel();
		rightSide.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		rightSide.setWidth("100%");
		trueTop.add(rightSide);
		
		Button imageBt=new Button("Screenshot",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				//doScreenShot();
				reservedScreenshot=true;
			}
		});
		rightSide.add(imageBt);
		gifAnimeBt = new Button("GifAnime",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reservedCreateGifAnime=true;
			}
		});
		//rightSide.add(gifAnimeBt);//stop support gif-anime
		
		imageLinkContainer = new VerticalPanel();
		imageLinkContainer.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		imageLinkContainer.setWidth("100px");
		rightSide.add(imageLinkContainer);
		
		
		
		
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
		final Button pasteBefore=new Button("Paste Before");
		final Button pasteAfter=new Button("Paste After");
		pasteBefore.setEnabled(false);
		pasteAfter.setEnabled(false);
		Button copy=new Button("Copy");
		upperPanel.add(copy);
		copy.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				doCopy();
				pasteBefore.setEnabled(true);
				pasteAfter.setEnabled(true);
			}
		});
		
		
		upperPanel.add(pasteBefore);
		pasteBefore.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				doPasteBefore();
				getSelectedPoseEditorData().setModified(true);
				updateSaveButtons();
			}
		});
		
		
		upperPanel.add(pasteAfter);
		pasteAfter.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				doPasteAfter();
				getSelectedPoseEditorData().setModified(true);
				updateSaveButtons();
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
		
		currentFrameRange = InputRangeWidget.createInputRange(0,0,0);
		currentFrameRange.setWidth(420);
		pPanel.add(currentFrameRange);
		
		currentFrameRange.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
			
				FrameMoveCommand command=new FrameMoveCommand(currentFrameRange.getValue(),cloneAngleAndPositions(ab.getBonesAngleAndMatrixs()));
				command.invoke();
				undoControler.addCommand(command);
			}
		});
		
		Button prev=new Button("Prev");
		pPanel.add(prev);
		prev.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doPrevFrame();
			}
		});
		Button next=new Button("Next");
		pPanel.add(next);
		next.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doNextFrame();
			}
		});
		
		currentFrameLabel = new Label("1/1");//usually this style
		currentFrameLabel.setWidth("40px");
		pPanel.add(currentFrameLabel);
		
		Button first=new Button("First");
		pPanel.add(first);
		first.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doFirstFrame();
			}
		});
		
		bottomPanel.show();
		super.leftBottom(bottomPanel);
	}
	
	protected void onBottomTabSelectionChanged(Integer selectedItem) {
		if(selectedItem==0){//edit tab
			executeStop();
			ikVisibleCheck.setValue(true, true);
			showBonesCheck.setValue(true, true);
		}else if(selectedItem==1){//play tab
			executePlayAnimation();
			ikVisibleCheck.setValue(false, true);
			showBonesCheck.setValue(false, true);
		}
	
		//I'm not sure still some objects make each time ,TODO fix this
		updateBonesVisible(showBonesCheck.getValue());
		if(ik3D!=null){
			Object3DUtils.setVisibleAll(ik3D, ikVisibleCheck.getValue());
		}
	}

	private Widget createPlayerPanel() {
		VerticalPanel panel=new VerticalPanel();
		
		
		HorizontalPanel buttons=new HorizontalPanel();
		buttons.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		panel.add(buttons);
		
		HorizontalPanel buttons2=new HorizontalPanel();
		buttons2.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		panel.add(buttons2);
		
		
		 CheckBox connectFirstCheck=new CheckBox("Connect to first frame");
		 connectFirstCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				connectFirst=event.getValue();
				executeStop();
				executePlayAnimation();
			}
			 
		});
		 buttons2.add(connectFirstCheck);
		
		 CheckBox moveOnlyRootCheck=new CheckBox("Move only root");
		 moveOnlyRootCheck.setValue(moveOnlyRoot);
		 moveOnlyRootCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				moveOnlyRoot=event.getValue();
				executeStop();
				executePlayAnimation();
			}
			 
		});
		 buttons2.add(moveOnlyRootCheck);
		
		
		Button play=new Button("Play Animation",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				executePlayAnimation();
				//TODO disable all editor
				}
		});
		buttons.add(play);
		
		
		Button stop=new Button("Stop",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				executeStop();
			}
		});
		buttons.add(stop);
		
		final HorizontalPanel downloadPanel=new HorizontalPanel();
		downloadPanel.setSpacing(4);
		
		
		Button export=new Button("Export",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AnimationClip clip=makeFrameAnimation(playAnimationDuration,posDivided,connectFirst,moveOnlyRoot);
				JavaScriptObject json=AnimationClip.toJSON(clip);
				JSONObject obj=new JSONObject(json);
				downloadPanel.clear();
				Anchor a=HTML5Download.get().generateTextDownloadLink(obj.toString(), "animation.json", "download",true);
				downloadPanel.add(a);
			}
		});
		buttons.add(export);
		buttons.add(downloadPanel);
		
		//
		return panel;
	}

	protected void executeStop() {
		stopAnimation();
		doPoseByMatrix(ab);//recover base pose
	
	}
	
	private double playAnimationDuration=1;
	private boolean connectFirst;
	
	/*
	 * if translate other bone,it's animation only work same bone.
	 */
	private boolean moveOnlyRoot=true;
	protected void executePlayAnimation() {
		stopAnimation();
		playFrameAnimation(playAnimationDuration,connectFirst,moveOnlyRoot);
	
	}

	private List<AngleAndPosition> poseFrameDataToAngleAndPosition(PoseFrameData poseFrameData){
		List<AngleAndPosition> list=Lists.newArrayList();
		for(int i=0;i<poseFrameData.getAngles().size();i++){
			//need add bone-position-data
			list.add(new AngleAndPosition(poseFrameData.getAngles().get(i).clone(), poseFrameData.getPositions().get(i).clone().add(ab.getBaseBoneRelativePosition(i)), null));
		}
		return list;
	}
	
	private AnimationClip makeFrameAnimation(double duration,double divided,boolean connectFirst,boolean moveOnlyRoot){
		PoseEditorData editorData=getSelectedPoseEditorData();
		List<JsArray<KeyframeTrack>> tracksList=Lists.newArrayList();
		
		List<PoseFrameData> frames=Lists.newArrayList(editorData.getPoseFrameDatas());
		
		if(connectFirst){
			frames.add(editorData.getPoseFrameDatas().get(0));
		}
		
		for(int i=0;i<frames.size();i++){
			List<AngleAndPosition> ap=poseFrameDataToAngleAndPosition(frames.get(i));
			JsArray<KeyframeTrack> tracks=createAnimationTracks(ap,divided,moveOnlyRoot);
			tracksList.add(tracks);
		}
		
		
		
		JsArray<KeyframeTrack> tracks=mergeAnimationTracks(tracksList, duration);
		
		/*
		LogUtils.log("tracks:"+tracks.length());
		for(int i=0;i<tracks.length();i++){
			LogUtils.log(tracks.get(i));
		}
		*/
		
		
		AnimationClip clip=THREE.AnimationClip("play", -1,tracks);
		return clip;
	}
	protected void playFrameAnimation(double duration,boolean connectFirst,boolean moveOnlyRoot) {
		AnimationClip clip=makeFrameAnimation(duration,1,connectFirst,moveOnlyRoot);
		
		mixer.stopAllAction();
		mixer.uncacheClip(clip);//same name cache that.
		mixer.clipAction(clip).play();
		
		
	}

	private boolean isUsingRenderer;//gifanime
	
	private boolean reservedCreateGifAnime;//call start gif-anime
	
	protected void doGifAnime() {
		//TODO timer for update ui.
		
		gifAnimeBt.setEnabled(false);
		Timer timer=new Timer(){

			@Override
			public void run() {
				
		try{
		isUsingRenderer=true;
		
		
		int quality=settingPanel.getGifQuality();
		int speed=settingPanel.getGifSpeed();
		
		
		
		
		
		
		
		boolean lastBackground=showBackgroundCheck.getValue();
		boolean lastIk=ikVisibleCheck.getValue();
		boolean lastBone=showBonesCheck.getValue();
		
		
		
		
		showBackgroundCheck.setValue(settingPanel.isGifShowBackground());
		ikVisibleCheck.setValue(settingPanel.isGifShowIk());
		showBonesCheck.setValue(settingPanel.isGifShowBone());
		
		
		
		LogUtils.log(settingPanel.isGifShowIk()+","+settingPanel.isGifShowBone());
		
		Canvas baseCanvas=settingPanel.createBgCanvas();
		
		Canvas gifCanvas=CanvasUtils.createCanvas(baseCanvas.getCoordinateSpaceWidth(),baseCanvas.getCoordinateSpaceHeight());
		
		
		int lastFrameIndex=currentFrameRange.getValue();
		List<ImageElement> elements=Lists.newArrayList();
		for(int i=0;i<getCurrentDataSize();i++){
			
			updatePoseIndex(i);
			renderer.render(scene, camera);
			
			String url=canvas.getRenderer().gwtPngDataUrl();
			ImageElement element=ImageElementUtils.create(url);
			
			CanvasUtils.clear(gifCanvas);
			gifCanvas.getContext2d().drawImage(baseCanvas.getCanvasElement(), 0, 0);
			
			CanvasUtils.drawCenter(gifCanvas, element);
			elements.add(ImageElementUtils.create(gifCanvas.toDataUrl()));
		}
		
		
		final String gifUrl=GifAnimeBuilder.from(elements).setQuality(quality).loop().delay(speed).toDataUrl();
		
		//becareful somehow rightnow no transparent  gif supported.
		//even choose choose transparent draw black. 
		Anchor anchor=HTML5Download.get().generateBase64DownloadLink(gifUrl, "image/gif", getSelectedPoseEditorData().getName()+".gif", "Download", true);
		
		imageLinkContainer.clear();
		imageLinkContainer.add(anchor);
		
		
		
		/*
		Anchor debug=HTML5Download.get().generateBase64DownloadLink(baseCanvas.toDataUrl(), "image/png", "poseeditor.png", "debug", true);
		imageLinkContainer.add(debug);
		*/
		
		//reset last index
		currentFrameRange.setValue(lastFrameIndex);
		updatePoseIndex(lastFrameIndex);
		
		
		showBackgroundCheck.setValue(lastBackground);
		ikVisibleCheck.setValue(lastIk);
		showBonesCheck.setValue(lastBone);
		
		updateBackgroundVisible(showBackgroundCheck.getValue());
		updateIKVisible(ikVisibleCheck.getValue());
		updateBonesVisible(showBonesCheck.getValue());
		
		isUsingRenderer=false;
		}catch (Exception e) {
			Window.alert(e.getMessage());
		}finally{
			gifAnimeBt.setEnabled(true);
		}
			}
		// TODO Auto-generated method stub
		
					};
		timer.schedule(50);//just disable bt;
					
				
	}
	
	private SimpleUndoControler undoControler=new SimpleUndoControler();
	
	private class SimpleUndoControler{
		private ICommand currentCommand;
		public void undo(){
			if(currentCommand!=null){
				currentCommand.undo();
			}
			undoButton.setEnabled(false);
			redoButton.setEnabled(true);
		}
		
		public void redo(){
			if(currentCommand!=null){
				currentCommand.redo();
			}
			undoButton.setEnabled(true);
			redoButton.setEnabled(false);
		}
		
		public void addCommand(ICommand command){
			currentCommand=command;
			undoButton.setEnabled(true);
			redoButton.setEnabled(false);
		}
	}
	
	private List<AngleAndPosition> cloneAngleAndPositions(List<AngleAndPosition> datas){
		return AnimationBonesData.cloneAngleAndMatrix(datas);
	}
	
	private class AngleAndPositionsCommand implements ICommand{
		public AngleAndPositionsCommand(List<AngleAndPosition> newBoneData, List<AngleAndPosition> lastBoneData) {
			super();
			this.newBoneData = newBoneData;
			this.lastBoneData = lastBoneData;
		}

		private List<AngleAndPosition> newBoneData;
		private List<AngleAndPosition> lastBoneData;
		@Override
		public void invoke() {
			selectAnimationDataData(cloneAngleAndPositions(newBoneData));
		}

		@Override
		public void undo() {
			selectAnimationDataData(cloneAngleAndPositions(lastBoneData));
		}

		@Override
		public void redo() {
			selectAnimationDataData(cloneAngleAndPositions(newBoneData));
		}
		
	}
	private class FrameMoveCommand implements ICommand{
		public FrameMoveCommand(int index,List<AngleAndPosition> boneData){
			this.frameIndex=index;
			this.boneData=boneData;
			this.lastIndex=poseFrameDataIndex;
		}
		private int lastIndex;
		private int frameIndex;
		private List<AngleAndPosition> boneData;
		
		@Override
		public void invoke() {
			currentFrameRange.setValue(frameIndex);
			updatePoseIndex(frameIndex);
		}

		@Override
		public void undo() {
			currentFrameRange.setValue(lastIndex);
			updatePoseIndex(lastIndex);
			
			selectAnimationDataData(AnimationBonesData.cloneAngleAndMatrix(boneData));
		}

		@Override
		public void redo() {
			currentFrameRange.setValue(frameIndex);
			updatePoseIndex(frameIndex);
		}
		
	}
	

	
	private boolean reservedScreenshot;
	
	private boolean reservedSettingPreview;
	protected void doScreenShot(WebGLRenderer renderer) {
		
		if(settingPanel.getScreenshotBackgroundType()==1){
			String bgcolor=settingPanel.getScreenshotBackgroundValue();
		
			int clearColor=ColorUtils.toColor(bgcolor);
			renderer.setClearColor(clearColor, 1);
			//need re-render
			renderer.render(scene, camera);
		}
		
		
		String url=canvas.getRenderer().gwtPngDataUrl();
		
		Anchor anchor=HTML5Download.get().generateBase64DownloadLink(url, "image/png", "poseeditor.png", "Download", true);
		
		imageLinkContainer.clear();
		imageLinkContainer.add(anchor);
		
		//set back clear
		renderer.setClearColor(0, 0);//somehow this is default
	}

	private void doFirstFrame() {
		FrameMoveCommand command=new FrameMoveCommand(0,AnimationBonesData.cloneAngleAndMatrix(ab.getBonesAngleAndMatrixs()));
		command.invoke();
		undoControler.addCommand(command);
	}

	private void doPrevFrame(){
		int value=currentFrameRange.getValue();
		if(value>0){
			value--;
			
			FrameMoveCommand command=new FrameMoveCommand(value,AnimationBonesData.cloneAngleAndMatrix(ab.getBonesAngleAndMatrixs()));
			command.invoke();
			undoControler.addCommand(command);
		}
	}
	
	private int getCurrentDataSize(){
		return getSelectedPoseEditorData().getPoseFrameDatas().size();
	}
	private void doNextFrame(){
		int value=currentFrameRange.getValue();
		if(value<getSelectedPoseEditorData().getPoseFrameDatas().size()-1){
			value++;
			
			FrameMoveCommand command=new FrameMoveCommand(value,AnimationBonesData.cloneAngleAndMatrix(ab.getBonesAngleAndMatrixs()));
			command.invoke();
			undoControler.addCommand(command);
		}
	}
	
	private int getNewDataIndex() throws StorageException{
		int dataIndex=0;
		
			dataIndex = storageControler.getValue(KEY_INDEX, 0);
		
		return dataIndex;
	}
	
	protected void doSaveAsFile(PoseEditorData pdata) {
		String result=Window.prompt("Save File", pdata.getName());
		if(result!=null){
			pdata.setName(result);
			JSONObject data=PoseEditorData.convertToJson(pdata);
			
			updateListBox();
			//fileNames.setItemText(poseEditorDataSelection, result);
			
			//TODO
			if(!storageControler.isAvailable()){
				//TODO just export
				Window.alert("not saved because your browser not supoort HTML5 storage");
				return;
			}
			
		//	Window.alert("hello");
			//save database
			
			int dataIndex=0;
			try {
				dataIndex = getNewDataIndex();
			} catch (StorageException e) {
				alert("save faild:"+e.getMessage());
				return;
			}
			
			//TODO method?
			//Canvas canvas=Canvas.createIfSupported();
			/*
			int thumbW=32;
			int thumbH=32;
			canvas.setSize(thumbW+"px", thumbH+"px");
			canvas.setCoordinateSpaceWidth(thumbW);
			canvas.setCoordinateSpaceHeight(thumbH);
			//log(renderer.gwtCanvas());
			//now stop write image.
			//canvas.getContext2d().drawImage(renderer.gwtCanvas(),0,0,screenWidth,screenHeight,0,0,thumbW,thumbH);
			
			String thumbnail=canvas.toDataUrl();
			LogUtils.log(thumbnail);
			*/
			
		//	Window.alert("hello1");
			//Window.alert("hello1");
			//Window.open(thumbnail, "tmp", null);
			try{
			storageControler.setValue(KEY_DATA+dataIndex, data.toString());
		//	Window.alert("hello2");
			//storageControler.setValue(KEY_IMAGE+dataIndex, thumbnail);
			storageControler.setValue(KEY_HEAD+dataIndex, pdata.getName()+"\t"+pdata.getCdate());
			
		//	Window.alert("hello3:"+dataIndex);
			pdata.setFileId(dataIndex);
			
			//increment
			dataIndex++;
			storageControler.setValue(KEY_INDEX, dataIndex);
			
			
			pdata.setModified(false);
			updateSaveButtons();
			updateDatasPanel();
			

			
			tabPanel.selectTab(1);//datas
			}catch(Exception e){
				try {
					//remove no need values
					storageControler.removeValue(KEY_DATA+dataIndex);
					storageControler.removeValue(KEY_HEAD+dataIndex);
				} catch (StorageException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				alert(e.getMessage());
			}
		}
		
		
	}
	
	//TODO more info
	public static void alert(String message){
		logger.fine(message);
		if(message.indexOf("(QUOTA_EXCEEDED_ERR)")!=-1 || message.indexOf("(QuotaExceededError)")!=-1){
		String title="QUOTA EXCEEDED_ERR\n";
		title+="maybe load texture is succeed.but store data is faild.so this data can't load again.\nbecause over internal HTML5 storage capacity.\n";
		title+="please remove unused textures or models from Preference Tab\n";
		Window.alert(title);
		}else{
		Window.alert("Error:"+message);
		}
	}
	
	
	protected void doSaveFile() {
		
		
		PoseEditorData pdata=getSelectedPoseEditorData();
		
		int fileId=pdata.getFileId();
		if(fileId!=-1){
			JSONObject data=PoseEditorData.convertToJson(pdata);
			try{
			storageControler.setValue(KEY_DATA+fileId, data.toString());
			pdata.setModified(false);
			
			updateSaveButtons();
			updateDatasPanel();//
			}catch(Exception e){
				alert(e.getMessage());
			}
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


	//private int poseEditorDataSelection;
	List<PoseEditorData> poseEditorDatas=new ArrayList<PoseEditorData>();
	
	public void updatePoseEditorDatas(){
		//poseEditorDataSelection=index;
		updatePoseIndex(0);
		updateSaveButtons();
	}
	private PoseEditorData getSelectedPoseEditorData(){
		return pedSelectionListBox.getValue();
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
		
		pedSelectionListBox.setValue(ped);
		
		//when new file 0 is natural
		updatePoseIndex(0);
		//updatePoseIndex(Math.max(0,poseFrameDataIndex-1));//new label
		updateSaveButtons();
	}
	
	
	private void updateListBox(){//refresh list
		pedSelectionListBox.setAcceptableValues(poseEditorDatas);
	}
	
	/**
	 * load frame data
	 * @param ped
	 */
	public void doLoad(PoseEditorData ped){
		//add to list
		poseEditorDatas.add(ped);
		updateListBox();
		pedSelectionListBox.setValue(ped);//select
		
		
		
		ped.updateMatrix(ab);//need bone data
		tabPanel.selectTab(0);//datas
		
		updatePoseEditorDatas();
	}
	

	protected void doPasteBefore() {
		if(clipboard!=null){
			int index=Math.max(0, poseFrameDataIndex);//same means insert before
			getSelectedPoseEditorData().getPoseFrameDatas().add(index,clipboard.clone());
			updatePoseIndex(index);
		}
	}
	
	protected void doPasteAfter() {
		if(clipboard!=null){
			getSelectedPoseEditorData().getPoseFrameDatas().add(currentFrameRange.getValue()+1,clipboard.clone());
			updatePoseIndex(currentFrameRange.getValue()+1);
		}
	}

	protected void doCut() {
		// TODO Auto-generated method stub
		
	}

	
	PoseFrameData clipboard;
	private boolean availIk(IKData ik){
		if(ab.getBoneIndex(ik.getLastBoneName())==-1){
			return false;
		}
		for(String name:ik.getBones()){
			if(ab.getBoneIndex(name)==-1){
				return false;
			}
		}
		return true;
	}
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
			Vector3 angle=matrixs.get(i).getDegreeAngle().clone();
			angles.add(angle);
			
			Vector3 position=ab.getMatrixPosition(i);//TODO getPosition()?
			position.sub(ab.getBaseBoneRelativePosition(i));
			
			positions.add(position);
		//	log(ab.getBoneName(i)+" pos="+ThreeLog.get(position)+",base="+ThreeLog.get(ab.getBaseBoneRelativePosition(i)));
		}
		
		List<Vector3> targets=new ArrayList<Vector3>();
		List<String> names=new ArrayList<String>();
		
		
		
		 Map<String,Vector3> ikDataMap=new LinkedHashMap<String,Vector3>();
			
			
		for(IKData ikdata:ikdatas){
			if(!availIk(ikdata)){//check ik 
				continue;
			}
			
			
			
			
			//some how not work correctly
			Vector3 pos=getDefaultIkPos(ab.getBoneIndex(ikdata.getLastBoneName()));
			pos.sub(ab.getBonePosition(ikdata.getLastBoneName()));//relative path
			
			//Vector3 pos=
			
			ikDataMap.put(ikdata.getName(), pos);
		}
		
		
		
		PoseFrameData ps=new PoseFrameData(matrixs, ikDataMap);
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
			updatePoseIndex(index,false);
		}else{
			getSelectedPoseEditorData().getPoseFrameDatas().add(index,ps);
			updatePoseIndex(getSelectedPoseEditorData().getPoseFrameDatas().size()-1,false);
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

	protected String convertBVHText(PoseEditorData ped) {
		ped.updateMatrix(ab);//current-bone
		
		BVH exportBVH=new BVH();
		
		BVHConverter converter=new BVHConverter();
		BVHNode node=converter.convertBVHNode(animationBones);
		
		exportBVH.setHiearchy(node);
		
		converter.setChannels(node,0,"XYZ");	//TODO support other order
		
		
		BVHMotion motion=new BVHMotion();
		motion.setFrameTime(.25);
		
		
		//TODO post issue
		//this is temporaly fix,first frame contain root pos and must sub position
		JsArrayNumber rootPos=animationBones.get(0).getPos();
		double rootX=0;
		double rootY=0;
		double rootZ=0;
		if(rootPos!=null && rootPos.length()==3){
			rootX=rootPos.get(0);
			rootY=rootPos.get(1);
			rootZ=rootPos.get(2);
		}
		
		
		//somehow this pos value don't care bone position.
		for(PoseFrameData pose:ped.getPoseFrameDatas()){
			double[] values=converter.angleAndMatrixsToMotion(pose.getAngleAndMatrixs(),BVHConverter.ROOT_POSITION_ROTATE_ONLY,"XYZ");
			
			//TODO update pos based on channel,but now has no channel data
			values[0]=values[0]-rootX;
			values[1]=values[1]-rootY;
			values[2]=values[2]-rootZ;
			
			motion.add(values);
		}
		motion.setFrames(motion.getMotions().size());//
		
		exportBVH.setMotion(motion);
		
		
		
		
		
		
		//log("frames:"+exportBVH.getFrames());
		BVHWriter writer=new BVHWriter();
		
		String bvhText=writer.writeToString(exportBVH);
		
		return bvhText;
		
		/*
		//log(bvhText);
		ExportUtils.exportTextAsDownloadDataUrl(bvhText, "UTF-8", "poseeditor"+exportIndex);
		//ExportUtils.openTabTextChrome(bvhText,"poseeditor"+exportIndex);//
		exportIndex++;
		*/
		
		
	}
	

	
	private int exportIndex=0;

	
	
	private int poseFrameDataIndex=0;
	//private List<PoseFrameData> poseFrameDatas=new ArrayList<PoseFrameData>();
	
	
	private void updatePoseIndex(int index){
		updatePoseIndex(index,true);
	}
	private void updatePoseIndex(int index,boolean needSelect){
		
		if(index==-1){
		currentFrameRange.setMax(0);
		currentFrameRange.setValue(0);
		currentFrameLabel.setText("");	
		}else{
		//poseIndex=index;
		currentFrameRange.setMax(Math.max(0,getSelectedPoseEditorData().getPoseFrameDatas().size()-1));
		
		if(currentFrameRange.getValue()==index){
			currentFrameRange.setValue(currentFrameRange.getMin());//on paste before not value change,index
			//need change value to update range widget;
		}
		currentFrameRange.setValue(index);
		currentFrameLabel.setText((index+1)+"/"+getSelectedPoseEditorData().getPoseFrameDatas().size());
		
		//currentFrameRange.setFocus(true);
		
		//LogUtils.log(currentFrameRange.getMin()+"/"+currentFrameRange.getMax()+" v="+currentFrameRange.getValue());
		
		if(!needSelect){
			poseFrameDataIndex=index;	//need set poseFrameDataIndex,
			return;//no need select maybe still add or replacing
		}
		//check in range
		if(index<=currentFrameRange.getMax() && getSelectedPoseEditorData().getPoseFrameDatas().size()!=0){//0 is empty no need to set
			selectFrameData(index);
		}else{
			LogUtils.log("call small index:"+index+" of "+currentFrameRange.getMax());
		}
		
		}
	}
	
	private void selectFrameData(int index) {
		
		poseFrameDataIndex=index;
		PoseFrameData pfd=getSelectedPoseEditorData().getPoseFrameDatas().get(index);
		
		if(pfd.getAngleAndMatrixs().size()!=animationBones.length()){
			Window.alert("difference- bone.not compatiple this frame.\nso push new button.");
			return;
		}
		
		selectAnimationDataData(AnimationBonesData.cloneAngleAndMatrix(pfd.getAngleAndMatrixs()));
		
	}
	
	private void selectAnimationDataData(List<AngleAndPosition> angleAndPositions) {	
		currentMatrixs=angleAndPositions;
		ab.setBonesAngleAndMatrixs(currentMatrixs);
		//update
		
		fitIkOnBone();
		//followTarget();//use initial pos,TODO follow or fit
		
		/*
		for(int i=0;i<ikdatas.size();i++){
			if(!availIk(ikdatas.get(i))){
				continue;
			}
			
			int boneIndex=ab.getBoneIndex(ikdatas.get(i).getLastBoneName());
			ab.getBonePosition(boneIndex);
			
			
			//this is load form ikdatas
			String ikName=ikdatas.get(i).getName();
			Vector3 vec=pfd.getIkTargetPosition(ikName);
			if(vec!=null){
				vec=vec.clone();
				vec.add(ab.getBonePosition(ikdatas.get(i).getLastBoneName()));//relative path
				LogUtils.log("ignore ikpos");
				ikdatas.get(i).getTargetPos().set(vec.getX(), vec.getY(), vec.getZ());	
				}
		}
		*/
		
		
		if(isSelectedIk()){
		switchSelectionIk(getCurrentIkData().getLastBoneName());
		}
		
		
		doPoseByMatrix(ab);
		updateBoneRanges();
	}

	/**
	 * ik position is same on last ik bone.
	 * this style usually not so moved when root-bone rotateds
	 * 
	 * call doPoseByMatrix or syncIkPosition to sync 3d model position
	 */
	private void fitIkOnBone() {
		AnimationBonesData merged=getMergedAnimationBonesData(ab);
		for(IKData ik:getAvaiableIkdatas()){
			String name=ik.getLastBoneName();
			Vector3 pos=merged.getBonePosition(name,isIkTargetEndSite);//try get endsite
			ik.getTargetPos().copy(pos);
		}
	}
	
	private AnimationBonesData getMergedAnimationBonesData(AnimationBonesData origin){
		return new AnimationBonesData(animationBones,mergeMeshMatrix(origin));
	}

	private void rotToBone(String name,double x,double y,double z,boolean doPoseByMatrix){
		int index=ab.getBoneIndex(name);
		if(index==-1){
			LogUtils.log("rotToBone:invalid bone called name="+name);
			return ;
		}
		
		//for mesh-rotation
		if(index==0){
			y+=meshRotationYRange.getValue();
		}
		
		
		//Matrix4 mx=ab.getBoneMatrix(name);
		Vector3 degAngles=THREE.Vector3(x,y,z);
		Vector3 angles=GWTThreeUtils.degreeToRagiant(degAngles);
		//log("set-angle:"+ThreeLog.get(GWTThreeUtils.radiantToDegree(angles)));
		//mx.setRotationFromEuler(angles, "XYZ");
		
		
		Vector3 pos=GWTThreeUtils.toPositionVec(ab.getBoneAngleAndMatrix(index).getMatrix());
		//log("pos:"+ThreeLog.get(pos));
		Matrix4 posMx=GWTThreeUtils.translateToMatrix4(pos);
		
		Matrix4 rotMx=GWTThreeUtils.rotationToMatrix4(angles);
		rotMx.multiplyMatrices(posMx,rotMx);
		
		//log("bone-pos:"+ThreeLog.get(bones.get(index).getPos()));
		
		ab.getBoneAngleAndMatrix(index).setMatrix(rotMx);
		ab.getBoneAngleAndMatrix(index).setDegreeAngle(degAngles);
	
		if(doPoseByMatrix){
			doPoseByMatrix(ab);
		}
	}
	
	
	//update current selection angles
	private void rotToBone(){
		String name=boneNamesBox.getItemText(boneNamesBox.getSelectedIndex());
		rotToBone(name,rotationBoneXRange.getValue(),rotationBoneYRange.getValue(),rotationBoneZRange.getValue(),true);
	}
	
	/*
		private void rotToBone(String boneName,Vector3 degAngles,boolean doPoseByMatrix){
			
		int index=ab.getBoneIndex(boneName);
		if(index==-1){
			LogUtils.log("rotToBone:invalid bone called name="+boneName);
			return ;
		}
		Vector3 angles=GWTThreeUtils.degreeToRagiant(degAngles);
		//log("set-angle:"+ThreeLog.get(GWTThreeUtils.radiantToDegree(angles)));
		//mx.setRotationFromEuler(angles, "XYZ");
		
		
		Vector3 pos=GWTThreeUtils.toPositionVec(ab.getBoneAngleAndMatrix(index).getMatrix());
		//log("pos:"+ThreeLog.get(pos));
		Matrix4 posMx=GWTThreeUtils.translateToMatrix4(pos);
		
		Matrix4 rotMx=GWTThreeUtils.rotationToMatrix4(angles);
		rotMx.multiplyMatrices(posMx,rotMx);
		
		//log("bone-pos:"+ThreeLog.get(bones.get(index).getPos()));
		
		ab.getBoneAngleAndMatrix(index).setMatrix(rotMx);
		ab.getBoneAngleAndMatrix(index).setAngle(degAngles);
		//log("set angle:"+ThreeLog.get(degAngles));
		if(doPoseByMatrix){
			doPoseByMatrix(ab);
		}
	}
	*/
	
	
	
	private void updateBoneRanges(){
	updateBoneRotationRanges();
	updateBonePositionRanges();
	
	}
	private void updateBoneRotationRanges(){
		if(isSelectEmptyBoneListBox()){
			setEnableBoneRanges(false,false,true);
			
			boneRotateLimitXLabel.setText("");
			boneRotateLimitYLabel.setText("");
			boneRotateLimitZLabel.setText("");
			
			return;
		}else{
			setEnableBoneRanges(true,true,false);
		}
		
		String name=boneNamesBox.getItemText(boneNamesBox.getSelectedIndex());
		
		BoneLimit boneLimit=boneLimits.get(name);
		
		if(boneLimit!=null){
			boneRotateLimitXLabel.setText("min:"+boneLimit.getMinXDegit()+" - max:"+boneLimit.getMaxXDegit());
			boneRotateLimitYLabel.setText("min:"+boneLimit.getMinYDegit()+" - max:"+boneLimit.getMaxYDegit());
			boneRotateLimitZLabel.setText("min:"+boneLimit.getMinZDegit()+" - max:"+boneLimit.getMaxZDegit());
			
		}else{
			boneRotateLimitXLabel.setText("");
			boneRotateLimitYLabel.setText("");
			boneRotateLimitZLabel.setText("");
		}
		
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
		
		Vector3 angles=ab.getBoneAngleAndMatrix(name).getDegreeAngle();
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
	
	private boolean isSelectEmptyBoneListBox(){
	
	return boneNamesBox.getSelectedIndex()==-1 || boneNamesBox.getItemText(boneNamesBox.getSelectedIndex()).isEmpty();	
	}
	private void updateBonePositionRanges(){
		if(isSelectEmptyBoneListBox()){
			return;
		}
		String name=boneNamesBox.getItemText(boneNamesBox.getSelectedIndex());
		
		Vector3 values=GWTThreeUtils.toPositionVec(ab.getBoneAngleAndMatrix(name).getMatrix());
		values.multiplyScalar(100);

		int x=(int) values.getX();
		positionXBoneRange.setValue(x);
		
		int y=(int) values.getY();
		
		positionYBoneRange.setValue(y);
	
		int z=(int) values.getZ();
		positionZBoneRange.setValue(z);
	}
	
	private Material bodyMaterial;
	private String textureUrl="female001_texture1.jpg";//default
	private Texture texture;
	protected void updateMaterial() {
		if(lastLoadedModel!=null){
			if(lastLoadedModel.getMetaData().getFormatVersion()==3){
				texture.setFlipY(false);
			}else{
				texture.setFlipY(true);
			}
		}
		
		Material material=null;
		boolean transparent=transparentCheck.getValue();
		double opacity=1;
		if(transparent){
			opacity=0.75;
		}
		JSParameter parameter=MeshBasicMaterialParameter.create().map(texture).transparent(true).opacity(opacity).skinning(true);
		
		if(texture==null){//some case happend
			material=THREE.MeshBasicMaterial(MeshBasicMaterialParameter.create().transparent(true).opacity(opacity).skinning(true));
			//only initial happend,if you set invalid texture
		}else{
			if(basicMaterialCheck.getValue()){
				material=THREE.MeshBasicMaterial(parameter);
				
			}else{
				
				material=THREE.MeshLambertMaterial(parameter);
				
			}
		}
		
		bodyMaterial=material;
		if(bodyMesh!=null){
			bodyMaterial.setNeedsUpdate(true);
			bodyMesh.setMaterial(material);
		}
		
		/*
		if(basicMaterialCheck.getValue()){
			if(bodyMesh!=null){
				bodyMesh.setMaterial(material);//somehow now works.
			}else{
				LogUtils.log("materical update called,but body mesh is null");
			}
		}else{
			//not basic material need recreate-model
			doPoseByMatrix(ab);
		}
		*/
		
		
		
		//test loaded material
		
		/* i have no idea how to set it.
		if(loadedMaterials!=null){
		MeshFaceMaterial faceMaterial=THREE.MeshFaceMaterial(loadedMaterials);
		LogUtils.log(faceMaterial);
		bodyMesh.setMaterial(faceMaterial);
		}
		*/
	}

	//TODO use for load bvh
	private void loadBVH(String path){
		
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(path));

			try {
				builder.sendRequest(null, new RequestCallback() {
					
					@Override
					public void onResponseReceived(Request request, Response response) {
						String bvhText=response.getText();
						parseInitialBVHAndLoadModels(bvhText);

					}
					
					
					

@Override
public void onError(Request request, Throwable exception) {
				Window.alert("load faild:");
}
				});
			} catch (RequestException e) {
				LogUtils.log(e.getMessage());
				e.printStackTrace();
			}
	}


	private Geometry baseGeometry;
	
private List<String> boneList=new ArrayList<String>();
	protected void parseInitialBVHAndLoadModels(String bvhText) {
		final BVHParser parser=new BVHParser();
		
		parser.parseAsync(bvhText, new ParserListener() {

			@Override
			public void onFaild(String message) {
				LogUtils.log(message);
			}
			@Override
			public void onSuccess(BVH bv) {
				bvh=bv;
				
				//createBonesFromBVH
				AnimationBoneConverter converter=new AnimationBoneConverter();
				setBone(converter.convertJsonBone(bvh));
				
				if(preferencePanel!=null){
					LogUtils.log("load from preference");
					preferencePanel.loadSelectionModel();
					preferencePanel.loadSelectionTexture();
				}
				
				//frameRange.setMax(animationData.getHierarchy().get(0).getKeys().length());
				/*
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
				*/
			}
		});	
	}
	
	private void setBone(JsArray<AnimationBone> bo){
		animationBones=bo;
		AnimationDataConverter dataConverter=new AnimationDataConverter();
		dataConverter.setSkipFirst(false);
		
		animationData = dataConverter.convertJsonAnimation(animationBones,bvh);//use for first pose
		
		boneList.clear();
		for(int i=0;i<animationBones.length();i++){
			boneList.add(animationBones.get(i).getName()); //TODO should i trim?
			//log(bones.get(i).getName()+","+ThreeLog.get(GWTThreeUtils.jsArrayToVector3(bones.get(i).getPos())));
		}
	}
	
	/**
	 * simple check bone-name;
	 * @param name
	 * @return
	 */
	private boolean existBone(String name){
		
		return boneList.contains(name);
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
			
			Matrix4 mx=THREE.Matrix4().makeRotationFromQuaternion(GWTThreeUtils.jsArrayToQuaternion(motion.getRot()));
			Vector3 motionPos=GWTThreeUtils.jsArrayToVector3(motion.getPos());
			//seems same as bone
		//	LogUtils.log(motionPos);
			mx.setPosition(motionPos);
			//mx.setRotationFromQuaternion();
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
				apos.add(parentMv.getAbsolutePosition());
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
	
	
	private boolean hasChild(List<List<Integer>> paths,int target){
		boolean ret=false;
		for(List<Integer> path:paths){
			for(int i=0;i<path.size()-1;i++){//exclude last
				if(path.get(i)==target){
					return true;
				}
			}
		}
		return ret;
	}
	
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
	SkinnedMesh bodyMesh;
	Object3D rootObject;
	Object3D bone3D;
	Object3D ik3D;
	private CheckBox transparentCheck;
	private CheckBox basicMaterialCheck;

	
	
	/**
	 * called after load
	 * @param index
	 */
	/*
	private void doPose(int index){
	
		//initial bone names
		log(bones);
		for(int i=0;i<bones.length();i++){
			//log(bones.get(i).getName());

		}
		
	
	//initializeBodyMesh();
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
	
	//}
	
	//for after loading
private void doRePose(int index){
	//initializeBodyMesh();
	initializeAnimationData(index,true);
	//stepCDDIk();	
	doPoseByMatrix(ab);
	
	updateBoneRanges();
	LogUtils.log("update-bone-range");
	}
	
	
AnimationBonesData ab;
List<AngleAndPosition> baseMatrixs;		




private void applyMatrix(List<AngleAndPosition> matrix,List<NameAndVector3> samples){
for(NameAndVector3 nv:samples){
	int boneIndex=ab.getBoneIndex(nv.getName());
	Matrix4 translates=GWTThreeUtils.translateToMatrix4(GWTThreeUtils.toPositionVec(ab.getBoneAngleAndMatrix(boneIndex).getMatrix()));
	Matrix4 newMatrix=GWTThreeUtils.rotationToMatrix4(nv.getVector3());
	newMatrix.multiplyMatrices(translates,newMatrix);
	//log("apply-matrix");
	matrix.get(boneIndex).setDegreeAngle(GWTThreeUtils.radiantToDegree(nv.getVector3()));
	matrix.get(boneIndex).setMatrix(newMatrix);
	}
}


List<List<AngleAndPosition>> candiateAngleAndMatrixs;

/*
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
*/

//initialize & loaded
List<AngleAndPosition> currentMatrixs;
private void initializeAnimationData(int index,boolean resetMatrix){

	//initialize AnimationBone
	if(ab==null){
	baseMatrixs=AnimationBonesData.boneToAngleAndMatrix(animationBones, animationData, index);
	ab=new AnimationBonesData(animationBones,AnimationBonesData.cloneAngleAndMatrix(baseMatrixs) );
	currentMatrixs=null;
	for(int i=0;i<animationBones.length();i++){
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
private Vector3 findNextStep(AnimationBonesData merged,int boneIndex,int lastBoneIndex,Vector3 targetPos){
	Vector3 lastTrans=merged.getMatrixPosition(lastBoneIndex);
	List<Integer> path=merged.getBonePath(lastBoneIndex);
	Matrix4 matrix=THREE.Matrix4();
	for(int i=0;i<path.size()-1;i++){
		int bindex=path.get(i);
		AngleAndPosition am=merged.getBoneAngleAndMatrix(bindex);
		matrix.multiply(am.getMatrix());
	}
	Vector3 base=THREE.Vector3(0,0,0);
	Vector3 pos=matrix.multiplyVector3(lastTrans.clone());
	double length=pos.sub(targetPos).length();
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
					AngleAndPosition am=merged.getBoneAngleAndMatrix(bindex);
					Matrix4 m=am.getMatrix();
					if(bindex==boneIndex){
						Vector3 newAngle=am.getDegreeAngle().clone().add(tmpVec);
						Vector3 pv=GWTThreeUtils.toPositionVec(m);
						
						m=THREE.Matrix4();
						m.setPosition(pv);
						m.setRotationFromEuler(newAngle, "XYZ");
						
					}
					matrix.multiply(m);
				}
				
				pos=matrix.multiplyVector3(lastTrans.clone());
				double tmpl=pos.sub(targetPos).length();
				//log("length:"+tmpl+","+x+"x"+y+"x"+z);
				if(tmpl<length){
					base.set(x*5,y*5,z*5);
					length=tmpl;
				}
			}
		}
	}
	//log("mutch:"+ThreeLog.get(base));
	return base.add(merged.getBoneAngleAndMatrix(boneIndex).getDegreeAngle());
}

private boolean doLimit=true;
private boolean ignorePerLimit=false;



private List<AngleAndPosition> mergeMeshMatrix(AnimationBonesData animationBonesData){
	List<AngleAndPosition> mergedMatrix=AnimationBonesData.cloneAngleAndMatrix(animationBonesData.getBonesAngleAndMatrixs());
	mergedMatrix.set(0, mergedMatrix.get(0).clone());//temporaly merge
	double incrementMeshX=meshPositionXBox.getValue()!=null?meshPositionXBox.getValue():0;
	mergedMatrix.get(0).getPosition().gwtIncrementX(incrementMeshX);
	mergedMatrix.get(0).getDegreeAngle().gwtIncrementY(meshRotationYRange.getValue());
	mergedMatrix.get(0).updateMatrix();
	
	return mergedMatrix;
}
/**
 * modify mergedMatrix
 * @param mergedMatrix
 * @return
 */
private List<AngleAndPosition> unmergeMeshMatrix(List<AngleAndPosition> mergedMatrix){
	
	double incrementMeshX=meshPositionXBox.getValue()!=null?meshPositionXBox.getValue():0;
	mergedMatrix.get(0).getPosition().gwtDecrementX(incrementMeshX);
	mergedMatrix.get(0).getDegreeAngle().gwtDecrementY(meshRotationYRange.getValue());
	mergedMatrix.get(0).updateMatrix();
	
	return mergedMatrix;
}

private void stepCDDIk(int perLimit,IKData ikData,int cddLoop){

	//do CDDIK
	//doCDDIk();
	currentIkJointIndex=0;
	AnimationBonesData merged=getMergedAnimationBonesData(ab);
	
	List<AngleAndPosition> minMatrix=mergeMeshMatrix(ab);
	double minLength=merged.getBonePosition(ikData.getLastBoneName(),isIkTargetEndSite).clone().sub(ikData.getTargetPos()).length();
	for(int i=0;i<ikData.getIteration()*cddLoop;i++){
	String targetBoneName=ikData.getBones().get(currentIkJointIndex);
	
	
	//ik locks not so good than expected
	if(ikLocks.contains(targetBoneName)){
		currentIkJointIndex++;
		if(currentIkJointIndex>=ikData.getBones().size()){
			currentIkJointIndex=0;
		}
		//LogUtils.log("skipped-ik:"+targetBoneName);
		continue;
	}
	//LogUtils.log("do-ik:"+targetBoneName);
	
	int boneIndex=ab.getBoneIndex(targetBoneName);
	
	
	Vector3 ikkedAngle=null;
	Matrix4 jointRot=merged.getBoneAngleAndMatrix(targetBoneName).getMatrix();
	Matrix4 translates=GWTThreeUtils.translateToMatrix4(GWTThreeUtils.toPositionVec(jointRot));
	Vector3 currentAngle=merged.getBoneAngleAndMatrix(targetBoneName).getDegreeAngle().clone();
	//log("current:"+ThreeLog.get(currentAngle));
	String beforeAngleLog="";
	if(perLimit>0){
	Vector3 lastJointPos=merged.getBonePosition(ikData.getLastBoneName(),isIkTargetEndSite);
	
	
	
	//Vector3 jointPos=ab.getParentPosition(targetName);
	Vector3 jointPos=merged.getBonePosition(targetBoneName);
	
	
	
	//Vector3 beforeAngles=GWTThreeUtils.radiantToDegree(GWTThreeUtils.rotationToVector3(jointRot));
	//Vector3 beforeAngle=ab.getBoneAngleAndMatrix(targetBoneName).getAngle().clone();
	
	//Matrix4 newMatrix=cddIk.doStep(lastJointPos, jointPos, jointRot, ikData.getTargetPos());
	
	//TODO add parent bone angles
	//AngleAndPosition root=ab.getBoneAngleAndMatrix(0);
	Vector3 parentAngle=merged.getParentAngles(boneIndex);
	Matrix4 newMatrix=cddIk.getStepAngleMatrix(parentAngle,lastJointPos, jointPos, jointRot, ikData.getTargetPos());
	beforeAngleLog=targetBoneName+","+"parent:"+ThreeLog.get(parentAngle)+",joint:"+ThreeLog.get(currentAngle);
	if(newMatrix==null){//invalid value
		if(debug){
			LogUtils.log("null matrix");
		}
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
	//diffAngles.normalize();
	}
	//diffAngles.normalize().addScalar(perLimit);//miss choice
	
	//log("diff:"+ThreeLog.get(diffAngles));
	if(!ignorePerLimit){
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
	}
	
	currentAngle.add(diffAngles);
	//log("added:"+ThreeLog.get(currentAngle));
	
	//currentAngle.setX(0);//keep x
	
	
	
	ikkedAngle=GWTThreeUtils.degreeToRagiant(currentAngle);
	}else{
		//faild TODO fix it
		Vector3 angle=findNextStep(merged,boneIndex, ab.getBoneIndex(ikData.getLastBoneName()), ikData.getTargetPos());
		//log(targetBoneName+" before:"+ThreeLog.get(ab.getBoneAngleAndMatrix(boneIndex).getAngle())+" after:"+ThreeLog.get(angle));
		ikkedAngle=GWTThreeUtils.degreeToRagiant(angle);
		
	}
	//log("before:"+ThreeLog.get(beforeAngle)+" after:"+ThreeLog.get(currentAngle));
	
	
	//limit max
	BoneLimit blimit=boneLimits.get(targetBoneName);
	//log(targetBoneName);
	//log("before-limit:"+ThreeLog.get(GWTThreeUtils.radiantToDegree(angles)));
	if(blimit!=null && doLimit){
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
	
	
	//String afterAngleLog=("after-limit:"+ThreeLog.get(GWTThreeUtils.radiantToDegree(ikkedAngle)));
	Matrix4 newMatrix=GWTThreeUtils.rotationToMatrix4(ikkedAngle);
	
	newMatrix.multiplyMatrices(translates,newMatrix);
	
	merged.getBoneAngleAndMatrix(boneIndex).setMatrix(newMatrix);
	merged.getBoneAngleAndMatrix(boneIndex).setDegreeAngle(GWTThreeUtils.radiantToDegree(ikkedAngle));
	
	
	//log(targetName+":"+ThreeLog.getAngle(jointRot)+",new"+ThreeLog.getAngle(newMatrix));
	//log("parentPos,"+ThreeLog.get(jointPos)+",lastPos,"+ThreeLog.get(lastJointPos));
	
	Vector3 diffPos=merged.getBonePosition(ikData.getLastBoneName()).clone().subSelf(ikData.getTargetPos());
	
	/*
	if(diffPos.length()>2){
		//usually ivalid
		
		log(i+","+"length="+diffPos.length()+" diff:"+ThreeLog.get(diffPos));
		log(beforeAngleLog);
		log(afterAngleLog);
	}*/
	
	
	if(diffPos.length()<minLength){
		minMatrix=mergeMeshMatrix(ab);//initialize
	}
	
	
	
	currentIkJointIndex++;
	if(currentIkJointIndex>=ikData.getBones().size()){
		currentIkJointIndex=0;
	}
	
	
	
	if(diffPos.length()<0.02){
		break;
	}
	//tmp1=lastJointPos;
	//tmp2=jointPos;
	}
	ab.setBonesAngleAndMatrixs(unmergeMeshMatrix(minMatrix));//use min
}

private void doPoseIkk(int index,boolean resetMatrix,int perLimit,IKData ikdata,int cddLoop){
		
	if(!existBone(ikdata.getLastBoneName())){
		return;//some time non exist bone selected.
	}
	//initializeBodyMesh();
	initializeAnimationData(index,resetMatrix);
	stepCDDIk(perLimit,ikdata,cddLoop);	//not working?
	doPoseByMatrix(ab);
	
	
	updateBoneRanges();
	
	
	}
private List<AngleAndPosition> findStartMatrix(String boneName,Vector3 targetPos) {
	List<AngleAndPosition> retMatrix=candiateAngleAndMatrixs.get(0);
	AnimationBonesData tmpData=new AnimationBonesData(animationBones, retMatrix);
	//ab.setBonesAngleAndMatrixs(retMatrix);//TODO without set
	Vector3 tpos=tmpData.getBonePosition(boneName);
	double minlength=targetPos.clone().sub(tpos).length();
	for(int i=1;i<candiateAngleAndMatrixs.size();i++){
		List<AngleAndPosition> mxs=candiateAngleAndMatrixs.get(i);
		tmpData.setBonesAngleAndMatrixs(mxs);//TODO change
		Vector3 tmpPos=tmpData.getBonePosition(boneName);
		double tmpLength=targetPos.clone().sub(tmpPos).length();
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
	
	
	
	
	return unmergeMeshMatrix(AnimationBonesData.cloneAngleAndMatrix(retMatrix));
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
	
	
private double baseBoneCoreSize=7;
private double baseIkLength=13;
private void doPoseByMatrix(AnimationBonesData animationBonesData){
	
	/*
	 * 
	 * creating bone and ik everyframe totally waste of fps
	 * 
	 */
	
	if(animationBonesData==null){
		LogUtils.log("doPoseByMatrix:animationBonesData=null");
		return;
	}
	
	if(isSelectedBone()){
		selectionMesh.setPosition(ab.getBonePosition(selectedBone));
	}
		
		List<AngleAndPosition> originalBoneMatrix=animationBonesData.getBonesAngleAndMatrixs();
		
		//temporaly merged root & mesh-matrix
		List<AngleAndPosition> boneMatrix=mergeMeshMatrix(animationBonesData);
		
		/*
		 *  merge mesh-matrix here,bone seems follow,beside pos value
		 * 
		 */
		
		
		
		
		bonePath=boneToPath(animationBones);
		if(bone3D!=null){
			group2.remove(bone3D);
		}
		bone3D=THREE.Object3D();
		group2.add(bone3D);
		
		if(ik3D!=null){
			group2.remove(ik3D);
		}
		ik3D=THREE.Object3D();
		group2.add(ik3D);
		
		
		//selection
		
		//test ikk
		/*
		Mesh cddIk0=THREE.Mesh(THREE.CubeGeometry(1.5, 1.5, 1.5),THREE.MeshLambertMaterial().color(0x00ff00).build());
		cddIk0.setPosition(getCurrentIkData().getTargetPos());
		bone3D.add(cddIk0);
		*/
		
		double bsize=baseBoneCoreSize;
		bsize/=posDivided;
		if(smallCheck.getValue()){
			bsize/=4;
		}
		
		BoxGeometry box=THREE.BoxGeometry(bsize,bsize, bsize);
		
		//can't merge material,because each bone has color 
		
		List<Matrix4> moveMatrix=new ArrayList<Matrix4>(); 
		List<Vector3> bonePositions=new ArrayList<Vector3>();
		
		for(int i=0;i<animationBones.length();i++){
			Matrix4 mv=boneMatrix.get(i).getMatrix();
			
			Mesh mesh=null;
			if(i==0){//root is better 
				
				mesh=THREE.Mesh(THREE.SphereGeometry(bsize, 4, 4),THREE.MeshLambertMaterial(GWTParamUtils.MeshBasicMaterial().color(0xFF0000)));
				
			}else{
				mesh=THREE.Mesh(box,THREE.MeshLambertMaterial(GWTParamUtils.MeshBasicMaterial().color(0xFF0000)));
				
			}
			
			
			bone3D.add(mesh);
			
			
			Vector3 pos=THREE.Vector3();
			pos.setFromMatrixPosition(mv);
			
			//Vector3 rot=GWTThreeUtils.rotationToVector3(GWTThreeUtils.jsArrayToQuaternion(bones.get(i).getRotq()));
			Vector3 rot=GWTThreeUtils.degreeToRagiant(ab.getBoneAngleAndMatrix(i).getDegreeAngle());
			List<Integer> path=bonePath.get(i);
			String boneName=animationBones.get(i).getName();
			//log(boneName);
			mesh.setName(boneName);
			
			
			Matrix4 matrix=THREE.Matrix4();
			for(int j=0;j<path.size()-1;j++){//last is boneself,no need for bone-pos
				Matrix4 mx=boneMatrix.get(path.get(j)).getMatrix();
				matrix.multiplyMatrices(matrix, mx);
			}
			pos.applyProjection(matrix);//set pos before last bone matrixed.
			//matrix.multiplyVector3(pos);
			
			//but need for transform
			matrix.multiplyMatrices(matrix, boneMatrix.get(path.get(path.size()-1)).getMatrix());//last one
			moveMatrix.add(matrix);
			//maybe this position use for end-sites.
			
			
			if(animationBones.get(i).getParent()!=-1){
				
			Vector3 ppos=bonePositions.get(animationBones.get(i).getParent());	
			//pos.addSelf(ppos);
			
			//log(boneName+":"+ThreeLog.get(pos)+","+ThreeLog.get(ppos));	
			
			
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
			
			for(IKData ik:getAvaiableIkdatas()){
				if(ik.getLastBoneName().equals(boneName)){//valid ik
					Mesh ikMesh=targetMeshs.get(boneName);
					
					if(ikMesh==null){//at first call this from non-ik stepped.
						//log("xxx");
						//initial
						
						double ikLength=baseIkLength/posDivided;
						if(!hasChild(bonePath,i)){
							ikLength=baseIkLength*2/posDivided;
						}
						
						
						double ikCoreSize=baseBoneCoreSize*2/posDivided;
						
						//ThreeLog.log(pos.clone().sub(ppos));
						//LogUtils.log(ikLength);
						
						Vector3 ikpos=pos.clone().sub(ppos).multiplyScalar(ikLength).add(pos);
						//ThreeLog.log(ikpos);
						//ikpos=pos.clone();
						//trying transparent
						ikMesh=THREE.Mesh(THREE.BoxGeometry(ikCoreSize, ikCoreSize, ikCoreSize),THREE.MeshLambertMaterial(MeshLambertMaterialParameter.create().color(0x00ff00).transparent(true).opacity(0.5)));
						ikMesh.setPosition(ikpos);
						ikMesh.setName("ik:"+boneName);
					//	log(boneName+":"+ThreeLog.get(ikpos));
						//log(ThreeLog.get(pos));
						ik.getTargetPos().set(ikpos.getX(), ikpos.getY(), ikpos.getZ());
						targetMeshs.put(boneName, ikMesh);
						
					}else{
						ikMesh.getParent().remove(ikMesh);
					}
					ik3D.add(ikMesh);
					ikMesh.setPosition(ik.getTargetPos());
					Line ikline=GWTGeometryUtils.createLineMesh(pos, ik.getTargetPos(), 0xffffff);
					ik3D.add(ikline);
					
					
				}
			}
			
			}
			mesh.setRotation(rot);
			mesh.setPosition(pos);
			
			//mesh color
			if(pos.getY()<0){
				mesh.getMaterial().gwtGetColor().set(0xffee00);//over color
			}else if(pos.getY()<10.0/posDivided){
				mesh.getMaterial().gwtGetColor().set(0xff8800);//near
			}
			
			bonePositions.add(pos);
		}
		
		//dont work
		updateBonesVisible(showBonesCheck.getValue());
		
		
		if(selectBoneFirstOnMouseDown){
			Object3DUtils.setVisibleAll(ik3D, false);
		}else{
			Object3DUtils.setVisibleAll(ik3D, ikVisibleCheck.getValue());
		}
		
		//bone3D.setVisible(showBonesCheck.getValue());
		
		//Geometry geo=GeometryUtils.clone(baseGeometry);
		

		
		//initialize AutoWeight
		if(bodyMesh==null){//initial
			bodyIndices = (JsArray<Vector4>) JsArray.createArray();
			bodyWeight = (JsArray<Vector4>) JsArray.createArray();
			
			
			
			//geometry initialized 0 indices & weights
			if(baseGeometry.getSkinIndices().length()!=0 && baseGeometry.getSkinWeight().length()!=0){
				//LogUtils.log(bones);
				LogUtils.log("use geometry bone");
				//test
				/*
				LogUtils.log("testlly use root all geometry");
				WeightBuilder.autoWeight(baseGeometry, bones, WeightBuilder.MODE_ROOT_ALL, bodyIndices, bodyWeight);
				*/
				
				WeightBuilder.autoWeight(baseGeometry, animationBones, WeightBuilder.MODE_FROM_GEOMETRY, bodyIndices, bodyWeight);
				
				/*
				List<String> lines=new ArrayList<String>();
				for(int i=0;i<bodyWeight.length();i++){
					lines.add(i+get(bodyWeight.get(i)));
				}
				LogUtils.log("after");
				LogUtils.log(Joiner.on("\n").join(lines));
				
				*/
				
			}else{
				LogUtils.log("auto-weight :sometime this broke models.you can use ModelWeight Apps");
				WeightBuilder.autoWeight(baseGeometry, animationBones, WeightBuilder.MODE_NearParentAndChildren, bodyIndices, bodyWeight);
				}
			}else{
				//root.remove(bodyMesh);
			}
		
		//Geometry geo=bodyMesh.getGeometry();
		//Geometry geo=GeometryUtils.clone(baseGeometry);
		
		//log("bi-length:"+bodyIndices.length());
		/*
		for(int i=0;i<baseGeometry.vertices().length();i++){
			Vector3 baseVertex=baseGeometry.vertices().get(i);
			Vector3 vertexPosition=baseVertex.clone();
			
			
			Vector3 targetVertex=geo.vertices().get(i);
			
			int boneIndex1=(int) bodyIndices.get(i).getX();
			int boneIndex2=(int) bodyIndices.get(i).getY();
			String name=animationBonesData.getBoneName(boneIndex1);
			//log(boneIndex1+"x"+boneIndex2);
			*/
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
			/*
			Vector3 bonePos=animationBonesData.getBaseBonePosition(boneIndex1);
			Vector3 relatePos=bonePos.clone();
			relatePos.subVectors(vertexPosition,bonePos);
			//double length=relatePos.length();
			 * 
			 moveMatrix.get(boneIndex1).multiplyVector3(relatePos);
			*/
			
			
			
			/*
			
			if(name.equals("RightLeg")){
				Vector3 parentPos=animationBonesData.getParentPosition(boneIndex1);
				relatePos.subSelf(parentPos);
				Matrix4 tmpMatrix2=GWTThreeUtils.rotationToMatrix4(GWTThreeUtils.degreeToRagiant(THREE.Vector3(0, 0, -20)));
				tmpMatrix2.multiplyVector3(relatePos);
				relatePos.addSelf(parentPos);
			}*/
			
			//relatePos.addSelf(bonePos);
		/*
			if(boneIndex2!=boneIndex1){
				//what is this?
				Vector3 bonePos2=animationBonesData.getBaseBonePosition(boneIndex2);
				Vector3 relatePos2=bonePos2.clone();
				relatePos2.subVectors(baseVertex,bonePos2);
				double length2=relatePos2.length();
				moveMatrix.get(boneIndex2).multiplyVector3(relatePos2);
				
				
				
				
				//scalar weight
				
				relatePos.multiplyScalar(bodyWeight.get(i).getX());
			
				relatePos2.multiplyScalar(bodyWeight.get(i).getY());
				relatePos.add(relatePos2);
				
				*/
		
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
				
				/*//why need this?,anyway this crash models.
				if(length2<1){
				Vector3 abpos=THREE.Vector3();
				abpos.subVectors(relatePos, bonePositions.get(boneIndex2));
				double scar=abpos.length()/length2;
				abpos.multiplyScalar(scar);
				abpos.add(bonePositions.get(boneIndex2));
				relatePos.set(abpos.getX(), abpos.getY(), abpos.getZ());
				
				//LogUtils.log("length2<1:"+i);
				}
				*/
				
				/*
				Vector3 diff=THREE.Vector3();
				diff.sub(relatePos2, relatePos);
				diff.multiplyScalar(bodyWeight.get(i).getY());
				relatePos.addSelf(diff);
				*/
		
		/*
			}else{
				if(name.equals("RightLeg")){
				//	Matrix4 tmpMatrix2=GWTThreeUtils.rotationToMatrix4(GWTThreeUtils.degreeToRagiant(THREE.Vector3(0, 0, -20)));
				//	tmpMatrix2.multiplyVector3(relatePos);
				}
			}
			
			
			targetVertex.set(relatePos.getX(), relatePos.getY(), relatePos.getZ());
		}
		*/
		
		/*
		geo.computeFaceNormals();
		geo.computeVertexNormals();
		
		//Material material=THREE.MeshLambertMaterial().map(ImageUtils.loadTexture("men3smart_texture.png")).build();
		
		bodyMesh=THREE.Mesh(geo, bodyMaterial);
		root.add(bodyMesh);
		*/
		
		if(bodyMesh==null){
			createSkinnedMesh();
		}
		
		
		/*//update-mesh-matrix
		 * 
		 */
		
		double meshRotationY=meshRotationYRange.getValue().doubleValue();
		bodyMesh.getRotation().setY(Math.toRadians(meshRotationY));
		bodyMesh.getPosition().setX(meshPositionXBox.getValue());
		
		
		//make skinning animation
		AnimationClip clip=createAnimationClip(animationBonesData.getBonesAngleAndMatrixs());
		mixer.stopAllAction();
		mixer.uncacheClip(clip);//same name cache that.
		mixer.clipAction(clip).play();
		
		
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

private AnimationMixer mixer;


public void createSkinnedMesh(){
	stopAnimation();
	//must be remove by hand
	baseGeometry.computeBoundingSphere();
	bodyMesh=THREE.SkinnedMesh(baseGeometry, bodyMaterial);
	
	mixer=THREE.AnimationMixer(bodyMesh);//replace mixer
	group1.add(bodyMesh);
	
	if(skeltonHelper!=null){
		scene.remove(skeltonHelper);//re-create
	}
	
	skeltonHelper = THREE.SkeletonHelper(bodyMesh);
	scene.add(skeltonHelper);
	updateBonesVisible(showBonesCheck.getValue());
	
	touchGroundZero();//
	
}

public void stopAnimation() {
	if(mixer==null){
		return;
	}
	mixer.stopAllAction();
	
	//characterMesh.getGeometry().getBones().get(60).setRotq(q)
}

/*
 * radiants
 * same as THREE.Quaternion().setFromEuler(euler, false)
 */
public Quaternion createAngleQuaternion(double x,double y,double z){
	Quaternion q=THREE.Quaternion();
	
	Quaternion xq=THREE.Quaternion().setFromAxisAngle(THREE.Vector3(1, 0, 0), x);
	q.multiply(xq);
	
	Quaternion yq=THREE.Quaternion().setFromAxisAngle(THREE.Vector3(0, 1, 0), y);
	q.multiply(yq);
	
	Quaternion zq=THREE.Quaternion().setFromAxisAngle(THREE.Vector3(0, 0, 1), z);
	q.multiply(zq);
	
	return q;
}

public Quaternion createAngleQuaternionByAngle(double x,double y,double z){
	return createAngleQuaternion(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
}

private void concat(JsArrayNumber target,JsArrayNumber values){
	for(int i=0;i<values.length();i++){
		target.push(values.get(i));
	}
}

public AnimationClip createAnimationClip(List<AngleAndPosition> angleAndPositions){
	AnimationClip clip=THREE.AnimationClip("pose", -1, createAnimationTracks(angleAndPositions,1,moveOnlyRoot));
	return clip;
}
	public JsArray<KeyframeTrack> createAnimationTracks(List<AngleAndPosition> angleAndPositions,double posdivided,boolean moveOnlyRoot){
	JsArray<KeyframeTrack> tracks=JavaScriptObject.createArray().cast();
	for(int i=0;i<angleAndPositions.size();i++){
		int index=i;
		
		
		AngleAndPosition ap=angleAndPositions.get(index);
		int boneIndex=index;
		
		JsArrayNumber times=JavaScriptObject.createArray().cast();
		times.push(0);
		
		Quaternion q=createAngleQuaternionByAngle(ap.getDegreeAngle().getX(),ap.getDegreeAngle().getY(),ap.getDegreeAngle().getZ());
		JsArrayNumber values=JsArray.createArray().cast();
		concat(values,q.toArray());
		
		QuaternionKeyframeTrack track=THREE.QuaternionKeyframeTrack(".bones["+boneIndex+"].quaternion", times, values);
		tracks.push(track);
			
		
	}
	
	//position animation
	for(int i=0;i<angleAndPositions.size();i++){
		int index=i;
		
		if(index!=0 && moveOnlyRoot){
			continue;
		}
		
		
		AngleAndPosition ap=angleAndPositions.get(index);
		int boneIndex=index;
		
		JsArrayNumber times=JavaScriptObject.createArray().cast();
		times.push(0);
		
		
		JsArrayNumber values=JsArray.createArray().cast();
		concat(values,ap.getPosition().clone().divideScalar(posdivided).toArray());
		
		VectorKeyframeTrack track=THREE.VectorKeyframeTrack(".bones["+boneIndex+"].position", times, values);
		tracks.push(track);
		
		//position animation
		
	}
	
	return tracks;
}
	
public JsArray<KeyframeTrack> mergeAnimationTracks(List<JsArray<KeyframeTrack>> trackList,double betweenTime){
	final JsArray<KeyframeTrack> tracks=JavaScriptObject.createArray().cast();
	
	for(int i=0;i<trackList.get(0).length();i++){
	KeyframeTrack track=trackList.get(0).get(i);
	
	JsArrayNumber times=JavaScriptObject.createArray().cast();
	JsArrayNumber values=JavaScriptObject.createArray().cast();
	double lastTime=0;
	for(int k=0;k<trackList.size();k++){
		JsArray<KeyframeTrack> tracksA=trackList.get(k);
		
		KeyframeTrack trackA=tracksA.get(i);
	
		JsArrayNumber timesA=trackA.getTimes().cast();
		JsArrayNumber valuesA=trackA.getValues().cast();
		concat(values,valuesA);
		
		double time=0;
		for(int j=0;j<timesA.length();j++){
			time=timesA.get(j)+lastTime;
			times.push(time);
		}
		lastTime=time+betweenTime;
	}
	//don't check with instanceof
	if(track.getValueTypeName().equals("quaternion")){
		tracks.push(THREE.QuaternionKeyframeTrack(track.getName(),times,values));
	}else if(track.getValueTypeName().equals("vector")){
		tracks.push(THREE.VectorKeyframeTrack(track.getName(),times,values));
	}else{
		LogUtils.log("invalid instance type:mergeAnimationTracks:"+track.getValueTypeName());
	}
	
	}
	
	
	return  tracks;
}

/**
 * 
 * my old pose change way
 * @deprecated
 * @param moveMatrix
 * @param animationBonesData
 * @return
 */
public  Mesh createPosedMesh(List<Matrix4> moveMatrix,AnimationBonesData animationBonesData){
	Geometry geo=GeometryUtils.clone(baseGeometry);
	
	//log("bi-length:"+bodyIndices.length());
	
	for(int i=0;i<baseGeometry.vertices().length();i++){
		Vector3 baseVertex=baseGeometry.vertices().get(i);
		Vector3 vertexPosition=baseVertex.clone();
		
		
		Vector3 targetVertex=geo.vertices().get(i);
		
		int boneIndex1=(int) bodyIndices.get(i).getX();
		int boneIndex2=(int) bodyIndices.get(i).getY();
		String name=animationBonesData.getBoneName(boneIndex1);
		
		Vector3 bonePos=animationBonesData.getBaseBonePosition(boneIndex1);
		Vector3 relatePos=bonePos.clone();
		relatePos.subVectors(vertexPosition,bonePos);
		//double length=relatePos.length();
		
		
		
		moveMatrix.get(boneIndex1).multiplyVector3(relatePos);
		
		if(boneIndex2!=boneIndex1){
			//what is this?
			Vector3 bonePos2=animationBonesData.getBaseBonePosition(boneIndex2);
			Vector3 relatePos2=bonePos2.clone();
			relatePos2.subVectors(baseVertex,bonePos2);
			double length2=relatePos2.length();
			moveMatrix.get(boneIndex2).multiplyVector3(relatePos2);
			
			
			
			
			//scalar weight
			
			relatePos.multiplyScalar(bodyWeight.get(i).getX());
		
			relatePos2.multiplyScalar(bodyWeight.get(i).getY());
			relatePos.add(relatePos2);
			
			
			//keep distance1 faild
			
			
		}else{
			if(name.equals("RightLeg")){
			//	Matrix4 tmpMatrix2=GWTThreeUtils.rotationToMatrix4(GWTThreeUtils.degreeToRagiant(THREE.Vector3(0, 0, -20)));
			//	tmpMatrix2.multiplyVector3(relatePos);
			}
		}
		
		
		targetVertex.set(relatePos.getX(), relatePos.getY(), relatePos.getZ());
	}
	
	geo.computeFaceNormals();
	geo.computeVertexNormals();
	
	//Material material=THREE.MeshLambertMaterial().map(ImageUtils.loadTexture("men3smart_texture.png")).build();
	
	return THREE.Mesh(geo, bodyMaterial);
	
}

//TODO move to three.js
public static String get(Vector4 vec){
	if(vec==null){
		return "Null";
	}
	String ret="x:"+vec.getX();
	ret+=",y:"+vec.getY();
	ret+=",z:"+vec.getZ();
	ret+=",w:"+vec.getW();
	return ret;
}

private Vector3 getDefaultIkPos(int index){
	AnimationBonesData merged=getMergedAnimationBonesData(ab);
	Vector3 pos=merged.getBonePosition(index);
	Vector3 ppos=merged.getParentPosition(index);
	double ikLength=1.5;
	if(!hasChild(bonePath,index)){
		ikLength=2.5;
	}
	
	return pos.clone().sub(ppos).multiplyScalar(ikLength).add(ppos);
}


private Map<String,Mesh> targetMeshs=new HashMap<String,Mesh>();
private ListBox rotateAndPosList;
private VerticalPanel bonePositionsPanel;
private VerticalPanel boneRotationsPanel;
private CheckBox zlockCheck;
private CheckBox ikLockCheck;
//private ListBox fileNames;
private ValueListBox<PoseEditorData> pedSelectionListBox;
private IStorageControler storageControler;
private VerticalPanel datasPanel;
private Button saveButton;
private VerticalPanel bonePostionAndRotationContainer;
private MenuItem contextMenuShowPrevFrame;
private MenuItem contextMenuHidePrefIks;

private GridHelper backgroundGrid;
private Label ikPositionLabelX;
private MenuBar rootBar;
private VerticalPanel imageLinkContainer;
private Button undoButton;
private Button gifAnimeBt;
private Mesh planeMesh;
private SkeletonHelper skeltonHelper;
private Group group1;
private Group group2;
private DoubleBox meshPositionXBox;
;

/**
 * @deprecated
 */
	private void doPose(List<MatrixAndVector3> boneMatrix){
		
		
		
		bonePath=boneToPath(animationBones);
		if(bone3D!=null){
			rootObject.remove(bone3D);
		}
		bone3D=THREE.Object3D();
		rootObject.add(bone3D);
		
		//test ikk
		Mesh cddIk0=THREE.Mesh(THREE.BoxGeometry(.5, .5, .5),THREE.MeshLambertMaterial(GWTParamUtils.MeshBasicMaterial().color(0x00ff00)));
		cddIk0.setPosition(getCurrentIkData().getTargetPos());
		bone3D.add(cddIk0);
		
		
		List<Matrix4> moveMatrix=new ArrayList<Matrix4>(); 
		List<Vector3> bonePositions=new ArrayList<Vector3>();
		for(int i=0;i<animationBones.length();i++){
			MatrixAndVector3 mv=boneMatrix.get(i);
			Mesh mesh=THREE.Mesh(THREE.BoxGeometry(.2, .2, .2),THREE.MeshLambertMaterial(GWTParamUtils.MeshBasicMaterial().color(0xff0000)));
			bone3D.add(mesh);
			
			Vector3 pos=mv.getPosition().clone();
			List<Integer> path=bonePath.get(i);
			String boneName=animationBones.get(i).getName();
			//log(boneName);
			
			Matrix4 tmpmx=boneMatrix.get(path.get(path.size()-1)).getMatrix();
			Vector3 tmpp=THREE.Vector3();
			tmpp.setFromMatrixPosition(tmpmx);
			//log(pos.getX()+","+pos.getY()+","+pos.getZ()+":"+tmpp.getX()+","+tmpp.getY()+","+tmpp.getZ());
			
			Matrix4 matrix=THREE.Matrix4();
			for(int j=0;j<path.size()-1;j++){//last is boneself
			//	log(""+path.get(j));
				Matrix4 mx=boneMatrix.get(path.get(j)).getMatrix();
				matrix.multiplyMatrices(matrix, mx);
			}
			matrix.multiplyVector3(pos);
			matrix.multiplyMatrices(matrix, boneMatrix.get(path.get(path.size()-1)).getMatrix());//last one
			moveMatrix.add(matrix);
			
			
			
			if(animationBones.get(i).getParent()!=-1){
			Vector3 ppos=bonePositions.get(animationBones.get(i).getParent());	
			//pos.addSelf(ppos);
			
			Line line=GWTGeometryUtils.createLineMesh(pos, ppos, 0xffffff);
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
			Vector3 baseVertex=baseGeometry.vertices().get(i);
			Vector3 targetVertex=geo.vertices().get(i);
			
			int boneIndex1=(int) bodyIndices.get(i).getX();
			int boneIndex2=(int) bodyIndices.get(i).getY();
			
			Vector3 bonePos=boneMatrix.get(boneIndex1).getAbsolutePosition();
			Vector3 relatePos=bonePos.clone();
			relatePos.sub(baseVertex,bonePos);
			double length=relatePos.length();
			
			moveMatrix.get(boneIndex1).multiplyVector3(relatePos);
			//relatePos.addSelf(bonePos);
			if(boneIndex2!=boneIndex1){
				Vector3 bonePos2=boneMatrix.get(boneIndex2).getAbsolutePosition();
				Vector3 relatePos2=bonePos2.clone();
				relatePos2.sub(baseVertex,bonePos2);
				double length2=relatePos2.length();
				moveMatrix.get(boneIndex2).multiplyVector3(relatePos2);
				
				
				//scalar weight
				
				relatePos.multiplyScalar(bodyWeight.get(i).getX());
			
				relatePos2.multiplyScalar(bodyWeight.get(i).getY());
				relatePos.add(relatePos2);
				
				
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
				abpos.add(bonePositions.get(boneIndex2));
				relatePos.set(abpos.getX(), abpos.getY(), abpos.getZ());
				
				}
				/*
				Vector3 diff=THREE.Vector3();
				diff.sub(relatePos2, relatePos);
				diff.multiplyScalar(bodyWeight.get(i).getY());
				relatePos.addSelf(diff);
				*/
			}
			
			
			targetVertex.set(relatePos.getX(), relatePos.getY(), relatePos.getZ());
		}
		
		geo.computeFaceNormals();
		geo.computeVertexNormals();
		
		//Material material=THREE.MeshLambertMaterial().map(ImageUtils.loadTexture("men3smart_texture.png")).build();
		
		bodyMesh=THREE.SkinnedMesh(geo, bodyMaterial);
		rootObject.add(bodyMesh);
		
		
		
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
	String html="Pose Editor ver."+version+" "+super.getHtml()+" 3D-Models created by <a href='http://www.manuelbastioni.com/manuellab.php'>Manuel Bastioni Lab 3D</a> and <a href='http://makehuman.org'>Makehuman</a>";

	return html;	
	}

	@Override
	public String getTabTitle() {
		return "Editor";
	}

	@Override
	public void modelChanged(SimpleTextData model) {
		//log("model-load:"+model.getData());
		LoadJsonModel(model.getData());
		
		//refresh matrix for new bone-model
		pedSelectionListBox.getValue().updateMatrix(ab);
		
		
		
		//new model need new initial pose
		initialPoseFrameData=snapCurrentFrameData();
		
		try{
		//now catch error	
		selectFrameData(currentFrameRange.getValue());//re pose
		}catch (Exception e) {
			Window.alert("maybe difference bone model loaded.\nreload app");
		}
	}

	@Override
	public void textureChanged(SimpleTextData textureValue) {
		textureUrl=textureValue.getData();
		
		generateTexture();
		
	}
	
	
	
	
	private void generateTexture(){
		final Image img=new Image(textureUrl);
		img.setVisible(false);
		RootPanel.get().add(img);
		
		img.addLoadHandler(new com.google.gwt.event.dom.client.LoadHandler() {
			
			@Override
			public void onLoad(LoadEvent event) {
				Canvas canvas=Canvas.createIfSupported();
				canvas.setCoordinateSpaceWidth(img.getWidth());
				canvas.setCoordinateSpaceHeight(img.getHeight());
				canvas.getContext2d().drawImage(ImageElement.as(img.getElement()),0,0);
				texture=THREE.CanvasTexture(canvas.getCanvasElement());
				texture.setNeedsUpdate(true);
				
				img.removeFromParent();
				//LogUtils.log("generate-texture");
				updateMaterial();
			}
		});
		
		
		
	}

	//this called after mouse up and it's hard to use
	@Override
	public void onDoubleClick(DoubleClickEvent event) {
		
		//usually called after onMouseDown?
		//doMouseDown(event.getX(),event.getY(),true);
	}
}
