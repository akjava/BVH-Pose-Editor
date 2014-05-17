package com.akjava.gwt.poseeditor.client;

public interface ICommand {
public void invoke();
public void undo();
public void redo();
}
