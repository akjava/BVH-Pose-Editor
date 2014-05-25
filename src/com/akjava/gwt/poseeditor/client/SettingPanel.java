package com.akjava.gwt.poseeditor.client;

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
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SettingPanel extends DockLayoutPanel{
	public static final String KEY_GIF_BG_VALUE="poseeditor_bg_value";
	public static final String KEY_GIF_BG_TYPE="poseeditor_bg_type";
	public static final String KEY_GIF_WITH_IK="poseeditor_gif_ik";
public static final String KEY_GIF_WITH_BONE="poseeditor_gif_bone";
public static final String KEY_GIF_WITH_TILE="poseeditor_gif_tile";
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
		tab.add(scroll,"ExportImage");
		scroll.add(createGifPanel());
		
		synchUI();
	}
	
	private StorageControler storageControler=new StorageControler();
	private CheckBox tileCheck;
	private CheckBox boneCheck;
	private CheckBox ikCheck;
	
	private void doCancel(){
		bgImageLabel.setText(lastBgLabel);
		bgImage=lastBgImage;//restore
		closePanel();
	}
	
	private Widget createGifPanel() {
		VerticalPanel panel=new VerticalPanel();
		panel.setSize("100%","100%");
		Label label=new Label("Visible options");
		panel.add(label);
		HorizontalPanel checks=new HorizontalPanel();
		panel.add(checks);
		
		tileCheck = new CheckBox("Tile");
		checks.add(tileCheck);
		
		boneCheck = new CheckBox("Bone");
		checks.add(boneCheck);
		
		ikCheck = new CheckBox("IK");
		checks.add(ikCheck);
		
		//transparent and basic material can't support because need recreate model.
		
		//background
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
		bgTypes.add(transparentBt);
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
		colorBox.setValue("#000000");
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
	private String lastBgLabel="";
	private ImageElement lastBgImage;
	private ImageElement bgImage;

	int gifWidth=497;
	int gifHeight=373;
	
	public boolean isGifShowIk(){
		return ikCheck.getValue();
	}
	public boolean isGifShowTile(){
		return tileCheck.getValue();
	}
	public boolean isGifShowBone(){
		return boneCheck.getValue();
	}
	
	public void synchUI(){
		//gif-panel
		tileCheck.setValue(storageControler.getValue(KEY_GIF_WITH_TILE, true));
		boneCheck.setValue(storageControler.getValue(KEY_GIF_WITH_BONE, false));
		ikCheck.setValue(storageControler.getValue(KEY_GIF_WITH_IK, false));
		
		
		lastBgImage=bgImage;//store for cancel
		lastBgLabel=bgImageLabel.getText();
		
		int bgType=storageControler.getValue(KEY_GIF_BG_TYPE, 0);
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
		
		updatePreviewCanvas();
	}

	protected void closePanel() {
		PoseEditor.poseEditor.selectMainTab();
		
	}

	protected void updateSettings() {
		//gif-panel
				try {//TODO only update modified value
					storageControler.setValue(KEY_GIF_WITH_TILE, tileCheck.getValue());
					storageControler.setValue(KEY_GIF_WITH_BONE, boneCheck.getValue());
					storageControler.setValue(KEY_GIF_WITH_IK, ikCheck.getValue());
					
					
					//TODO update bgtypes
					if(transparentBt.getValue()){
						storageControler.setValue(KEY_GIF_BG_TYPE, 0);
					}else if(colorBt.getValue()){
						storageControler.setValue(KEY_GIF_BG_TYPE, 1);
						storageControler.setValue(KEY_GIF_BG_VALUE, colorBox.getValue());
					}else if(imageBt.getValue()){
						storageControler.setValue(KEY_GIF_BG_TYPE, 2);
					}
					
					
					
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

	public void updatePreviewCanvas() {
		createBgCanvas();
		if(previewImage!=null){
		CanvasUtils.drawCenter(bgCanvas, previewImage);
		}
	}

}
