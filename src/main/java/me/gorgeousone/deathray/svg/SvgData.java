package me.gorgeousone.deathray.svg;

import java.util.List;

public class SvgData {
	private final List<Circle> circles;
	private final List<Path> paths;
	
	public SvgData(List<Circle> circles, List<Path> paths) {
		this.circles = circles;
		this.paths = paths;
	}
	
	public PointCloud samplePoints(double samplesPerBlock) {
		PointCloud pointCloud = new PointCloud();
		
		for (Path path : paths) {
			pointCloud.add(path.samplePoints(samplesPerBlock));
		}
		for (Circle circle : circles) {
			pointCloud.add(circle.samplePoints(samplesPerBlock));
		}
		return pointCloud;
		
	}
}
