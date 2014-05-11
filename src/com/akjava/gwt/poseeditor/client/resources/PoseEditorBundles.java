package com.akjava.gwt.poseeditor.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface PoseEditorBundles extends ClientBundle {
public static PoseEditorBundles INSTANCE=GWT.create(PoseEditorBundles.class);
	TextResource modelNames();
	TextResource textureNames();
	@Source("tpose.bvh")
	TextResource pose();

}
