package com.akjava.gwt.poseeditor.client;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.input.ColorBox;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageException;
import com.google.common.base.Ascii;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SettingPanel extends DockLayoutPanel{
	public static final String KEY_SCREENSHOT_BG_TYPE="poseeditor_screenshot_bg_type";
	public static final String KEY_SCREENSHOT_BG_VALUE="poseeditor_screenshot_bg_value";
	
	public static final String KEY_GIF_QUALITY="poseeditor_gif_quality";
	public static final String KEY_GIF_SPEED="poseeditor_gif_speed";
	public static final String KEY_GIF_HEIGHT="poseeditor_gif_height";
	public static final String KEY_GIF_WIDTH="poseeditor_gif_width";
	public static final String KEY_GIF_BG_VALUE="poseeditor_bg_value";
	public static final String KEY_GIF_BG_TYPE="poseeditor_bg_type";
	public static final String KEY_GIF_WITH_IK="poseeditor_gif_ik";
public static final String KEY_GIF_WITH_BONE="poseeditor_gif_bone";
public static final String KEY_GIF_WITH_BACKGROUND="poseeditor_gif_background";
	public SettingPanel() {
		super(Unit.PX);
		this.setSize("100%", "100%");
		
		HorizontalPanel controler=new HorizontalPanel();
		
		this.addNorth(controler, 30);
		
		Button okBt=new Button("OK",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				updateSettings();
				closePanel();
			}
		});
		controler.add(okBt);
		
		Button cancelBt=new Button("Cancel",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				doCancel();
			}
		});
		controler.add(cancelBt);
		
		//main
		TabLayoutPanel tab=new TabLayoutPanel(25, Unit.PX);
		//tab.setSize("100%","100%");
		this.add(tab);
		
		
		
		ScrollPanel scroll=new ScrollPanel();
		//scroll.setSize("100%","100%");
		//creaate gif-anime panel
		tab.add(scroll,"GifAnime");
		scroll.add(createGifPanel());
		
		
		ScrollPanel scroll2=new ScrollPanel();
		//scroll.setSize("100%","100%");
		//creaate gif-anime panel
		tab.add(scroll2,"Screenshot");
		scroll2.add(createScreenshotPanel());
		
		synchUI();
	}
	
	private StorageControler storageControler=new StorageControler();
	private CheckBox backgroundCheck;
	private CheckBox boneCheck;
	private CheckBox ikCheck;
	private IntegerBox heightBox;
	private ValueListBox<Integer> qualityBox;
	private ValueListBox<Integer> speedBox;
	private RadioButton screenshotTransparentBt;
	private RadioButton screenshotColorBt;
	private ColorBox screenshotColorBox;
	
	private void doCancel(){
		bgImageLabel.setText(lastBgLabel);
		bgImage=lastBgImage;//restore
		closePanel();
	}
	
	private Widget createScreenshotPanel() {
		VerticalPanel panel=new VerticalPanel();
		panel.setSize("100%","100%");
		
		//background
				Label label2=new Label("Background");
				panel.add(label2);
				HorizontalPanel bgTypes=new HorizontalPanel();
				bgTypes.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
				panel.add(bgTypes);
				screenshotTransparentBt = new RadioButton("scbgtype","Transparent");
				screenshotTransparentBt.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						updatePreviewCanvas();
					}
				});
				
				bgTypes.add(screenshotTransparentBt);//stop because not support so far
				
				
				screenshotColorBt = new RadioButton("scbgtype","Color");
				bgTypes.add(screenshotColorBt);
				
				
				screenshotColorBox = new ColorBox();
				screenshotColorBox.setValue("#333333");
				bgTypes.add(colorBox);
				
		
		return panel;
	}
	
	/*
	public boolean isPreviewScreenShotTransparent(){
		return screenshotTransparentBt.getValue();
	}
	public String getPreviewScreenshotBackgroundValue(){
		return screenshotColorBox.getValue();
	}
	*/
	
	private Widget createGifPanel() {
		VerticalPanel panel=new VerticalPanel();
		panel.setSize("100%","100%");
		Label label=new Label("Visible options");
		panel.add(label);
		HorizontalPanel checks=new HorizontalPanel();
		panel.add(checks);
		
		backgroundCheck = new CheckBox("Tile");
		checks.add(backgroundCheck);
		backgroundCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				PoseEditor.poseEditor.doReserveSettingPreview();
			}
			
		});
		
		boneCheck = new CheckBox("Bone");
		checks.add(boneCheck);
		boneCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				PoseEditor.poseEditor.doReserveSettingPreview();
			}
			
		});
		
		ikCheck = new CheckBox("IK");
		checks.add(ikCheck);
		ikCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				PoseEditor.poseEditor.doReserveSettingPreview();
			}
			
		});
		
		
		//transparent and basic material can't support because need recreate model.
		
		Label label3=new Label("Size");
		panel.add(label3);
		HorizontalPanel sizePanel=new HorizontalPanel();
		sizePanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		panel.add(sizePanel);
		
		sizePanel.add(new Label("Width:"));
		
		widthBox = new IntegerBox();
		widthBox.setWidth("50px");
		sizePanel.add(widthBox);
		widthBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				updateCanvasSize();
			}
		});
		
		sizePanel.add(new Label("Height:"));
		
		heightBox = new IntegerBox();
		heightBox.setWidth("50px");
		sizePanel.add(heightBox);
		heightBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				updateCanvasSize();
			}
		});
		
		Button reset1=new Button("Reset size",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				widthBox.setValue(gifWidth);
				heightBox.setValue(gifHeight);
				updateCanvasSize();
			}
		});
		sizePanel.add(reset1);
		
		
		Label label4=new Label("GifAnime Options");
		panel.add(label4);
		HorizontalPanel h1=new HorizontalPanel();
		panel.add(h1);
		
		h1.add(new Label("Quality"));
		qualityBox = new ValueListBox<Integer>(new Renderer<Integer>() {

			@Override
			public String render(Integer value) {
				if(value==10){
					return "medium(10)";
				}
				
				if(value==1){
					return "High(1)";
				}
				
				if(value==20){
					return "low(20)";
				}
				
				return ""+value;
			}

			@Override
			public void render(Integer object, Appendable appendable) throws IOException {
				
			}
		});
		List<Integer> acceptableValues=Lists.newArrayList();
		for(int i=20;i>0;i--){
			acceptableValues.add(i);
		}
		qualityBox.setValue(20);//low 
		qualityBox.setAcceptableValues(acceptableValues);
		h1.add(qualityBox);
		
		h1.add(new Label("Speed"));
		speedBox = new ValueListBox<Integer>(new Renderer<Integer>() {

			@Override
			public String render(Integer value) {
				if(value==1000){
					return "slow(1000ms)";
				}
				
				if(value==50){
					return "first(50ms)";
				}
				
				if(value==500){
					return "medium(500ms)";
				}
				
				return ""+value;
			}

			@Override
			public void render(Integer object, Appendable appendable) throws IOException {
				
			}
		});
		List<Integer> acceptableValues2=Lists.newArrayList();
		for(int i=1;i<=20;i++){
			acceptableValues2.add(i*50);
		}
		speedBox.setValue(300);//low 
		speedBox.setAcceptableValues(acceptableValues2);
		h1.add(speedBox);
		
		
		
		//background
		Label label2=new Label("Background");
		panel.add(label2);
		HorizontalPanel bgTypes=new HorizontalPanel();
		bgTypes.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		panel.add(bgTypes);
		transparentBt = new RadioButton("bgtype","Transparent");
		transparentBt.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				updatePreviewCanvas();
			}
		});
		
		//bgTypes.add(transparentBt);//stop because not support so far
		
		
		colorBt = new RadioButton("bgtype","Color");
		bgTypes.add(colorBt);
		/*
		colorBt.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				updatePreviewCanvas();
			}
		});
		*/
		colorBt.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				updatePreviewCanvas();
			}
		});
		
		colorBox = new ColorBox();
		colorBox.setValue("#333333");
		bgTypes.add(colorBox);
		imageBt = new RadioButton("bgtype","Image");
		bgTypes.add(imageBt);
		imageBt.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				updatePreviewCanvas();
			}
		});
		
		bgImageLabel = new Label();
		bgImageLabel.setWidth("100px");
		
		bgImageUpload = FileUtils.createSingleFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String text) {
				bgImageLabel.setText(Ascii.truncate(file.getFileName(),15,"..."));
				bgImage=ImageElementUtils.create(text);
				imageBt.setValue(true);
				updatePreviewCanvas();
			}
		}, true);
		
		bgTypes.add(bgImageUpload);
		
		bgTypes.add(bgImageLabel);
		Button reset=new Button("Reset",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				bgImageLabel.setText("");
				bgImage=null;
				updatePreviewCanvas();
			}
		});
		bgTypes.add(reset);
		
		//size
		//TODO support size
		
		bgCanvas = CanvasUtils.createCanvas(gifWidth,gifHeight);
		panel.add(bgCanvas);
		
		return panel;
	}
	protected void updateCanvasSize() {
		CanvasUtils.createCanvas(bgCanvas,widthBox.getValue(),heightBox.getValue());
		updatePreviewCanvas();
	}

	private String lastBgLabel="";
	private ImageElement lastBgImage;
	private ImageElement bgImage;

	int gifWidth=497;
	int gifHeight=373;
	
	
	
	
	public void synchUI(){
		//gif-panel
		backgroundCheck.setValue(isGifShowBackground());
		boneCheck.setValue(isGifShowBone());
		ikCheck.setValue(isGifShowIk());
		
		int screenshottype=getScreenshotBackgroundType();
		if(screenshottype==0){
			screenshotTransparentBt.setValue(true);
		}else if(screenshottype==1){
			screenshotColorBt.setValue(true);
			String scvalue=getScreenshotBackgroundValue();
			screenshotColorBox.setValue(scvalue);
		}
		
		lastBgImage=bgImage;//store for cancel
		lastBgLabel=bgImageLabel.getText();
		
		int bgType=storageControler.getValue(KEY_GIF_BG_TYPE, 1);//sadlly transparent not support 
		String bgValue=storageControler.getValue(KEY_GIF_BG_VALUE, "#000000");
		
		LogUtils.log(bgType+","+bgValue);
		
		if(bgType==0){
			transparentBt.setValue(true);
		}else if(bgType==1){
			colorBt.setValue(true);
			colorBox.setValue(bgValue);
		}else if(bgType==2){
			imageBt.setValue(true);
		}
		
		widthBox.setValue(Math.max(1, getGifWidth()));
		heightBox.setValue(Math.max(1,getGifHeight()));
		
		qualityBox.setValue(getGifQuality());
		speedBox.setValue(getGifSpeed());
		
		updateCanvasSize();
		
		updatePreviewCanvas();
	}

	protected void closePanel() {
		PoseEditor.poseEditor.selectMainTab();
		
	}

	
	protected void updateSettings() {
		//gif-panel
				try {//TODO only update modified value
					
					//screenshot
					int sctype=screenshotTransparentBt.getValue()?0:1;
					storageControler.setValue(KEY_SCREENSHOT_BG_TYPE, sctype);
					storageControler.setValue(KEY_SCREENSHOT_BG_VALUE, screenshotColorBox.getValue());
					
					storageControler.setValue(KEY_GIF_WITH_BACKGROUND, backgroundCheck.getValue());
					storageControler.setValue(KEY_GIF_WITH_BONE, boneCheck.getValue());
					storageControler.setValue(KEY_GIF_WITH_IK, ikCheck.getValue());
					
					
					
					if(transparentBt.getValue()){
						storageControler.setValue(KEY_GIF_BG_TYPE, 0);
					}else if(colorBt.getValue()){
						storageControler.setValue(KEY_GIF_BG_TYPE, 1);
						storageControler.setValue(KEY_GIF_BG_VALUE, colorBox.getValue());
					}else if(imageBt.getValue()){
						storageControler.setValue(KEY_GIF_BG_TYPE, 2);
					}
					
					
					storageControler.setValue(KEY_GIF_WIDTH, widthBox.getValue());
					storageControler.setValue(KEY_GIF_HEIGHT, heightBox.getValue());
					
					storageControler.setValue(KEY_GIF_QUALITY, qualityBox.getValue());
					storageControler.setValue(KEY_GIF_SPEED, speedBox.getValue());
					
				} catch (StorageException e) {
					//possible quote error
					PoseEditor.alert(e.getMessage());
				}
	}

	private ImageElement previewImage;
	private RadioButton transparentBt;
	private RadioButton colorBt;
	private ColorBox colorBox;
	private RadioButton imageBt;
	private FileUploadForm bgImageUpload;
	private Canvas bgCanvas;
	private Label bgImageLabel;
	private IntegerBox widthBox;
	public void setPreviewImage(String url) {
		previewImage=ImageElementUtils.create(url);
		updatePreviewCanvas();
	}
	
	public Canvas createBgCanvas(){
		CanvasUtils.clear(bgCanvas);
		if(transparentBt.getValue()){
			//do nothing
		}else if(colorBt.getValue()){
			String bg=colorBox.getValue();
			CanvasUtils.fillRect(bgCanvas, bg);
		}else if(imageBt.getValue()){
			if(bgImage!=null){
				//TODO set pos,scale,rot from simplelogo
				CanvasUtils.drawCenter(bgCanvas, bgImage);
			}
		}
		
		return bgCanvas;
	}

	public int getGifQuality(){
		return storageControler.getValue(KEY_GIF_QUALITY, 10);
	}

	public int getGifSpeed(){
		return storageControler.getValue(KEY_GIF_SPEED, 200);
	}
	public int getGifWidth(){
		return storageControler.getValue(KEY_GIF_WIDTH, gifWidth);
	}
	public int getGifHeight(){
		return storageControler.getValue(KEY_GIF_HEIGHT, gifHeight);
	}
	
	public boolean isGifShowBone(){
		return storageControler.getValue(KEY_GIF_WITH_BONE, false);
	}
	public boolean isGifShowIk(){
		return storageControler.getValue(KEY_GIF_WITH_IK, false);
	}
	public boolean isGifShowBackground(){
		return storageControler.getValue(KEY_GIF_WITH_BACKGROUND, false);
	}

	
	public int getScreenshotBackgroundType(){
		return storageControler.getValue(KEY_SCREENSHOT_BG_TYPE, 0);
	}
	public String getScreenshotBackgroundValue(){
		return storageControler.getValue(KEY_SCREENSHOT_BG_VALUE, "#333333");
	}
	
	public boolean isPreviewGifShowBone(){
		return boneCheck.getValue();
	}
	public boolean isPreviewGifShowIk(){
		return ikCheck.getValue();
	}
	public boolean isPreviewGifShowBackground(){
		return backgroundCheck.getValue();
	}
	
	public void updatePreviewCanvas() {
		createBgCanvas();
		if(previewImage!=null){
		CanvasUtils.drawCenter(bgCanvas, previewImage);
		}
	}

}
