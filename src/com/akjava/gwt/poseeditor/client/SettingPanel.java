package com.akjava.gwt.poseeditor.client;

import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageException;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SettingPanel extends DockLayoutPanel{
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
				closePanel();
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
		
		synchUI();
	}
	
	private StorageControler storageControler=new StorageControler();
	private CheckBox tileCheck;
	private CheckBox boneCheck;
	private CheckBox ikCheck;
	
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
		
		
		
		return panel;
	}

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
	}

	protected void closePanel() {
		PoseEditor.poseEditor.selectMainTab();
		
	}

	protected void updateSettings() {
		//gif-panel
				try {
					storageControler.setValue(KEY_GIF_WITH_TILE, tileCheck.getValue());
					storageControler.setValue(KEY_GIF_WITH_BONE, boneCheck.getValue());
					storageControler.setValue(KEY_GIF_WITH_IK, ikCheck.getValue());
				} catch (StorageException e) {
					//possible quote error
					PoseEditor.alert(e.getMessage());
				}
	}

}
