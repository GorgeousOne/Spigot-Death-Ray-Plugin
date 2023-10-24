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
	
	public double getWidth() {
		return width;
	}
	
	public List<Vector> samplePoints(double density) {
		List<Vector> points = new ArrayList<>();
		
		for (int i = 0; i < nodes.size() - 1; i++) {
			
			Vector start = nodes.get(i);
			Vector end = nodes.get(i + 1);
			
			double distance = start.distance(end);
			int pointCount = (int) (distance * density);
			double step = 1d / pointCount;
			
			for (int j = 0; j < pointCount; j++) {
				points.add(start.clone().multiply(1 - j * step).add(end.clone().multiply(j * step)));
			}
		}
		return points;
	}
}
