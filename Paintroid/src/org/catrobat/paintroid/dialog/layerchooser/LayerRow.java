package org.catrobat.paintroid.dialog.layerchooser;

public class LayerRow {

	public int icon;
	public String name;
	public boolean visible;
	public boolean selected;

	public LayerRow(int icon, String name, boolean visible, boolean selected) {
		super();
		this.icon = icon;
		this.name = name;
		this.visible = visible;
		this.selected = selected;

	}

	public LayerRow() {
		super();
	}
}
