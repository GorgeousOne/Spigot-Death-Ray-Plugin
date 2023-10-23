package me.gorgeousone.deathray.svg;

public class Circle {
	private final double cx;
	private final double cy;
	private final double r;
	private double width;
	
	public Circle(double cx, double cy, double r) {
		this.cx = cx;
		this.cy = cy;
		this.r = r;
	}
	
	public void setWidth(double width) {
		this.width = width;
	}
	
	@Override
	public String toString() {
		return "Circle{" +
		       "cx=" + cx +
		       ", cy=" + cy +
		       ", r=" + r +
		       '}';
	}
}

