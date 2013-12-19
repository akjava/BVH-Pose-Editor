package com.akjava.gwt.poseeditor.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileHandler;
import com.akjava.gwt.html5.client.file.FileReader;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.lib.client.ExportUtils;
import com.akjava.gwt.lib.client.HeaderAndValue;
import com.akjava.gwt.lib.client.IStorageControler;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageDataList;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.lib.client.ValueUtils;
import com.akjava.gwt.poseeditor.client.resources.PoseEditorBundles;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PreferenceTabPanel extends VerticalPanel {
	private IStorageControler controler;
	private PreferenceListener listener;
	private Map<Integer, String> presetModelMap = new HashMap<Integer, String>();
	private ListBox modelListBox;
	private Label modelSelection;

	private StorageDataList modelControler;
	private StorageDataList textureControler;
	public static final String KEY_MODEL_SELECTION="MODEL_SELECTION";
	public static final String KEY_TEXTURE_SELECTION="TEXTURE_SELECTION";
	private Button removeBt;
	public PreferenceListener getListener() {
		return listener;
	}

	public void setListener(PreferenceListener listener) {
		this.listener = listener;
	}

	private void createModelControl(){
		LogUtils.log("1");
		HorizontalPanel mPanel=new HorizontalPanel();
		
		add(mPanel);
		Label modelLabel=new Label("Model");
		modelLabel.setWidth("150px");
		mPanel.add(modelLabel);
		Button modelUpdateBt=new Button("Update");
		
		mPanel.add(modelUpdateBt);
		modelUpdateBt.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				updateModelList();
			}
		});
		
		final FileUploadForm modelUpload=new FileUploadForm();
		
		add(modelUpload);
		
		modelUpload.getFileUpload().addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				JsArray<File> files = FileUtils.toFile(event.getNativeEvent());
				
				final FileReader reader=FileReader.createFileReader();
				final File file=files.get(0);
				reader.setOnLoad(new FileHandler() {
					@Override
					public void onLoad() {
						modelUpload.reset();
						try{
						String text=reader.getResultAsString();
						JSONValue lastJsonValue = JSONParser.parseStrict(text);
						//TODO more validate
						JSONObject object=lastJsonValue.isObject();
						if(object==null){
							Window.alert("invalid model");
							return;
						}
						LogUtils.log("preferenceDebug:1");
						modelControler.setDataValue(file.getFileName(), text);
						LogUtils.log("preferenceDebug:2");
						int id=modelControler.incrementId();
						LogUtils.log("preferenceDebug:3"+id);
						
						loadModel(new HeaderAndValue(id, file.getFileName(), text));
						LogUtils.log("preferenceDebug:4");
						
						modelListBox.addItem(file.getFileName(),""+id);
						LogUtils.log("preferenceDebug:5"+id);
						LogUtils.log("preferenceDebug:6"+(modelListBox.getItemCount()-1));
						modelListBox.setSelectedIndex(modelListBox.getItemCount()-1);
						LogUtils.log("preferenceDebug:7"+id);
						updateModelButtons();
						
						
						controler.setValue(KEY_MODEL_SELECTION, modelListBox.getItemCount()-1);
						LogUtils.log("preferenceDebug:8"+id);
						}catch(Exception e){
							PoseEditor.alert(e.getMessage());
						}
					}
				});
				reader.readAsText(file,"utf-8");
			}
		});
		
		modelControler=new StorageDataList(controler, "MODEL");
		modelSelection = new Label();
		add(modelSelection);

		modelListBox = new ListBox();
		modelListBox.setWidth("300px");
		add(modelListBox);
		modelListBox.setVisibleItemCount(5);
		
		
		modelListBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				updateModelButtons();
			}
		});
		
		
		
		updateModelList();
		
		HorizontalPanel buttons = new HorizontalPanel();
		add(buttons);
		Button loadBt = new Button("Load");
		buttons.add(loadBt);
		loadBt.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				loadModelByListIndex(modelListBox
						.getSelectedIndex());
			}
		});
		removeBt = new Button("Remove");
		buttons.add(removeBt);
		removeBt.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				removeModelByListIndex(modelListBox
						.getSelectedIndex());
			}
		});
	
		
		
	}
	
	String[] imageExtensions={".jpg",".png",".jpeg"};
	private boolean isImageFile(File file){
		String name=file.getFileName().toLowerCase();
		for(int i=0;i<imageExtensions.length;i++){
			if(name.endsWith(imageExtensions[i])){
				return true;
			}
		}
		
		return false;
	}
	private ListBox textureListBox;
	private Label textureSelection;
	private FocusWidget removeTextureBt;
	private Map<Integer, String> presetTextureMap = new HashMap<Integer, String>();
	private void createTextureControl(){

		add(new Label("Texture"));
		final FileUploadForm textureUpload=new FileUploadForm();
		add(textureUpload);
		textureUpload.getFileUpload().addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				JsArray<File> files = FileUtils.toFile(event.getNativeEvent());
				
				final FileReader reader=FileReader.createFileReader();
				final File file=files.get(0);
				reader.setOnLoad(new FileHandler() {
				

					@Override
					public void onLoad() {
						textureUpload.reset();
						String text=reader.getResultAsString();
						if(!isImageFile(file)){
							Window.alert("texture support .jpg or .png");
							return;
						}
						try{
						textureControler.setDataValue(file.getFileName(), text);
						int id=textureControler.incrementId();
						
						loadTexture(new HeaderAndValue(id, file.getFileName(), text));
						textureListBox.addItem(file.getFileName(),""+id);
						textureListBox.setSelectedIndex(textureListBox.getItemCount()-1);
						updateTextureButtons();
						controler.setValue(KEY_TEXTURE_SELECTION, textureListBox.getItemCount()-1);
						}catch(Exception e){
							
							PoseEditor.alert(e.getMessage());
						}
					}
				});
				reader.readAsDataURL(file);
			}
		});
		
		
		textureControler=new StorageDataList(controler, "TEXTURE");
		textureSelection = new Label();
		add(textureSelection);
		
		textureListBox = new ListBox();
		textureListBox.setWidth("300px");
		add(textureListBox);
		textureListBox.setVisibleItemCount(5);
		
		
		
		
		// read from
		String textureText = PoseEditorBundles.INSTANCE.textureNames().getText();
		String[][] mapV = ValueUtils.csvToArray(textureText);
		for (int i = 0; i < mapV.length; i++) {
			if (mapV[i].length == 2) {
				String filePath = mapV[i][0];
				String id = mapV[i][1];
				String fileName = getFileNameWithoutExtension(filePath);
				presetTextureMap.put(Integer.parseInt(id), filePath);
				textureListBox.addItem(fileName, id);
			}
		}
		
		
		List<HeaderAndValue> textures=textureControler.getDataList();
		for(int i=0;i<textures.size();i++){
			textureListBox.addItem(textures.get(i).getHeader(),""+textures.get(i).getId());
		}

		textureListBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				updateTextureButtons();
			}
		});
		
		

		
		
		int listBoxSelection;
		try {
			listBoxSelection = controler.getValue(KEY_TEXTURE_SELECTION, 0);
			if(listBoxSelection>=textureListBox.getItemCount()){
				LogUtils.log("warn texture index is invalid:"+listBoxSelection);
				listBoxSelection=0;
			}
			
			textureListBox.setSelectedIndex(listBoxSelection);
		} catch (StorageException e) {
			LogUtils.log("get key_texture_selection faild");
		}//default bone
	
		
		
		
		HorizontalPanel buttons = new HorizontalPanel();
		add(buttons);
		Button loadBt = new Button("Load");
		buttons.add(loadBt);
		loadBt.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				loadTextureByListIndex(textureListBox
						.getSelectedIndex());
			}
		});
		removeTextureBt = new Button("Remove");
		buttons.add(removeTextureBt);
		removeTextureBt.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				removeTextureByListIndex(textureListBox
						.getSelectedIndex());
			}
		});

		Button exportBt = new Button("Export");
		buttons.add(exportBt);
		exportBt.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				exportTextureByListIndex(textureListBox
						.getSelectedIndex());
			}
		});
	}
	
	private void updateModelList(){
		modelListBox.clear();
		// read from resource
		
			
		
		
				String modelText = PoseEditorBundles.INSTANCE.modelNames().getText();
				String[][] mapV = ValueUtils.csvToArray(modelText);
				for (int i = 0; i < mapV.length; i++) {
					if (mapV[i].length == 2) {
						String filePath = mapV[i][0];
						String id = mapV[i][1];
						String fileName = getFileNameWithoutExtension(filePath);
						presetModelMap.put(Integer.parseInt(id), filePath);
						modelListBox.addItem(fileName, id);
					}
				}
				
				//update from storage
				List<HeaderAndValue> models=modelControler.getDataList();
				for(int i=0;i<models.size();i++){
					modelListBox.addItem(models.get(i).getHeader(),""+models.get(i).getId());
				}
				
				
				
				int listBoxSelection;
				try {
					listBoxSelection = controler.getValue(KEY_MODEL_SELECTION, 0);
					if(listBoxSelection>=modelListBox.getItemCount()){
						LogUtils.log("warn model index is invalid:"+listBoxSelection);
						listBoxSelection=0;
					}
					modelListBox.setSelectedIndex(listBoxSelection);
				} catch (StorageException e) {
					LogUtils.log("load model selection faild");
					e.printStackTrace();
				}//default bone
				
				
	}
	
	public PreferenceTabPanel(IStorageControler cs,
			PreferenceListener listener) {
		this.controler = cs;
		this.listener = listener;
		
		createModelControl();
		
		createTextureControl();
	}

	protected void updateModelButtons() {
		//preset cant remove
		String id = modelListBox.getValue(modelListBox.getSelectedIndex());
		final int idIndex = Integer.parseInt(id);
		if(idIndex<0){//preset cant delate
			removeBt.setEnabled(false);
		}else{
			removeBt.setEnabled(true);
		}
	}
	
	protected void updateTextureButtons() {
		//preset cant remove
		String id = textureListBox.getValue(textureListBox.getSelectedIndex());
		final int idIndex = Integer.parseInt(id);
		if(idIndex<0){
			removeTextureBt.setEnabled(false);
		}else{
			removeTextureBt.setEnabled(true);
		}
	}

	public void loadSelectionModel(){
		loadModelByListIndex(modelListBox.getSelectedIndex());
	}
	public void loadSelectionTexture(){
		loadTextureByListIndex(textureListBox.getSelectedIndex());
	}
	
	private void removeModelByListIndex(final int index) {
		String id = modelListBox.getValue(index);
		final String modelName=modelListBox.getItemText(index);
		
		LogUtils.log(id+","+modelName);
		final int idIndex = Integer.parseInt(id);
		if (idIndex < 0) {
			//never calld;
		} else {
			
			modelControler.clearData(idIndex);
			int boxIndex=modelListBox.getSelectedIndex();
			modelListBox.removeItem(boxIndex);
			modelListBox.setSelectedIndex(boxIndex-1);
			
			updateModelButtons();
			loadSelectionModel();
		}
	}
	
	private void removeTextureByListIndex(final int index) {
		String id = textureListBox.getValue(index);
		final String textureName=textureListBox.getItemText(index);
		
		LogUtils.log(id+","+textureName);
		final int idIndex = Integer.parseInt(id);
		if (idIndex < 0) {
			//never calld;
		} else {
			
			textureControler.clearData(idIndex);
			int boxIndex=textureListBox.getSelectedIndex();
			textureListBox.removeItem(boxIndex);
			textureListBox.setSelectedIndex(boxIndex-1);
			
			updateTextureButtons();
			loadSelectionTexture();
		}
	}

	private void loadModelByListIndex(final int index) {
		String id = modelListBox.getValue(index);
		final String modelName=modelListBox.getItemText(index);
		
		LogUtils.log(id+","+modelName);
		final int idIndex = Integer.parseInt(id);
		if (idIndex < 0) {
			String path = presetModelMap.get(idIndex);
			if (path != null) {

				RequestBuilder builder = new RequestBuilder(
						RequestBuilder.GET, URL.encode(path));

				try {
					builder.sendRequest(null, new RequestCallback() {

						@Override
						public void onResponseReceived(Request request,
								Response response) {
							
							loadModel(new HeaderAndValue(idIndex, modelName, response.getText()));
							try {
								controler.setValue(KEY_MODEL_SELECTION, index);
							} catch (StorageException e) {
								LogUtils.log("set KEY_MODEL_SELECTION faild");
								e.printStackTrace();
							}
						}

						@Override
						public void onError(Request request,
								Throwable exception) {
							Window.alert("load faild:");
						}
					});
				} catch (RequestException e) {
					LogUtils.log(e.getMessage());
					e.printStackTrace();
				}
			} else {
				LogUtils.log("preset model not found" + idIndex);
			}
		} else {
			// load from cache
			HeaderAndValue hv=modelControler.getDataValue(idIndex);
			
			
			loadModel(hv);
			try {
				controler.setValue(KEY_MODEL_SELECTION, index);
			} catch (StorageException e) {
				LogUtils.log("set KEY_MODEL_SELECTION faild");
				e.printStackTrace();
			}
		}
	}
	
	private void loadTextureByListIndex(final int index) {
		String id = textureListBox.getValue(index);
		final String textureName=textureListBox.getItemText(index);
		
		LogUtils.log(id+","+textureName);
		final int idIndex = Integer.parseInt(id);
		if (idIndex < 0) {
			String path = presetTextureMap.get(idIndex);
			if (path != null) {
				loadTexture(new HeaderAndValue(idIndex, textureName, path));
				try {
					controler.setValue(KEY_TEXTURE_SELECTION, index);
				} catch (StorageException e) {
					LogUtils.log("set KEY_TEXTURE_SELECTION faild");
					e.printStackTrace();
				}
				
			} else {
				LogUtils.log("preset model not found" + idIndex);
			}
		} else {
			// load from cache
			HeaderAndValue hv=textureControler.getDataValue(idIndex);
			
			
			loadTexture(hv);
			try {
				controler.setValue(KEY_TEXTURE_SELECTION, index);
			} catch (StorageException e) {
				LogUtils.log("set KEY_TEXTURE_SELECTION faild");
				e.printStackTrace();
			}
		}
	}
	private void exportTextureByListIndex(final int index) {
		String id = textureListBox.getValue(index);
		final String textureName=textureListBox.getItemText(index);
		
		LogUtils.log(id+","+textureName);
		final int idIndex = Integer.parseInt(id);
		if (idIndex < 0) {
			String path = presetTextureMap.get(idIndex);
			if (path != null) {
				//becareful app mode cant export iamge because of app has secret url
				//blank uimage must be absolute path,or data url
				//ExportUtils.openTabDataURLImage(GWT.getHostPageBaseURL()+path, textureName);
				Window.open(path, textureName,null);
			} else {
				LogUtils.log("preset model not found" + idIndex);
			}
		} else {
			// load from cache
			HeaderAndValue hv=textureControler.getDataValue(idIndex);
			
			//LogUtils.log("texture:"+hv.getData());
			//Window.open(hv.getData(), textureName, null);
			ExportUtils.openTabAbsoluteURLImage(hv.getData(), textureName);
			
		}
	}
	
	private void loadModel(HeaderAndValue value) {
		listener.modelChanged(value);
		modelSelection.setText("Selection:"+value.getHeader());
	}
	
	private void loadTexture(HeaderAndValue value) {
		listener.textureChanged(value);
		textureSelection.setText("Selection:"+value.getHeader());
	}

	public String getFileNameWithoutExtension(String name) {
		int start = name.lastIndexOf("/");
		if (start == -1) {
			start = 0;
		} else {
			start++;
		}
		int end = name.lastIndexOf(".");
		if (end == -1) {
			end = name.length();
		}
		return name.substring(start, end);
	}

	public interface PreferenceListener {
		public void modelChanged(HeaderAndValue model);

		public void textureChanged(HeaderAndValue texture);
	}
}
