package me.gorgeousone.deathray.svg;

import org.bukkit.util.Vector;

import java.util.List;

public class Circle {
	private final double cx;
	private final double cz;
	private final double r;
	private double width;
	
	public Circle(double cx, double cz, double r) {
		this.cx = cx;
		this.cz = cz;
		this.r = r;
	}
	
	public void setWidth(double width) {
		this.width = width;
	}
	
	public double getWidth() {
		return width;
	}
	
	public List<Vector> samplePoints(double density) {
		double circumference = 2 * Math.PI * r;
		int pointCount = (int) (circumference * density);
		double angleStep = 2 * Math.PI / pointCount;
		
		double angle = 0;
		double x;
		double z;
		
		List<Vector> points = new java.util.ArrayList<>(pointCount);
		
		for (int i = 0; i < pointCount; i++) {
			x = cx + r * Math.cos(angle);
			z = cz + r * Math.sin(angle);
			
			points.add(new Vector(x, 0, z));
			angle += angleStep;
		}
		return points;
	}
	
	@Override
	public String toString() {
		return "Circle{" +
		       "cx=" + cx +
		       ", cy=" + cz +
		       ", r=" + r +
		       '}';
	}
}

