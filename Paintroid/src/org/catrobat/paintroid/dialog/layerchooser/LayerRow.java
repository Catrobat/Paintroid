package org.catrobat.paintroid.dialog.layerchooser;

public class LayerRow {

	public int icon;
	public String name;
	public boolean visible;

	public LayerRow(int icon, String name, boolean visible) {
		super();
		this.icon = icon;
		this.name = name;
		this.visible = visible;
	}

	public LayerRow() {
		super();
	}
}
