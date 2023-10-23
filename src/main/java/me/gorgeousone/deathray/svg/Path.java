package me.gorgeousone.deathray.svg;

import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Path {
	
	private List<Vector> nodes;
	private double width;
	
	public Path() {
		this.nodes = new ArrayList<>();
	}
	
	public void add(Vector node) {
		nodes.add(node);
	}
	
	public Vector get(int index) {
		return nodes.get(index).clone();
	}
	
	public int size() {
		return nodes.size();
	}
	
	public void setWidth(double width) {
		this.width = width;
	}
}
