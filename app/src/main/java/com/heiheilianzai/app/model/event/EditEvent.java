package com.heiheilianzai.app.model.event;

public class EditEvent {
    private boolean isEditOpen;

    public EditEvent(boolean isEditOpen) {
        this.isEditOpen = isEditOpen;
    }

    public boolean isEditOpen() {
        return isEditOpen;
    }

    public void setEditOpen(boolean editOpen) {
        isEditOpen = editOpen;
    }
}
