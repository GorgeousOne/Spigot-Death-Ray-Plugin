package me.gorgeousone.deathray.svg;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class PointCloud {
	
	private final List<Vector> points;
	private int offset;
	
	public PointCloud() {
		this.points = new ArrayList<>();
	}
	
	public void add(List<Vector> points) {
		this.points.addAll(points);
	}
	
	public void render(Location location, int particlesPerBlock, double radius, int iter) {
		World world = location.getWorld();
		double offX = location.getX();
		double offY = location.getY();
		double offZ = location.getZ();
		
		for (int i = offset; i < points.size(); i += iter) {
			Vector point = points.get(i);
			
			double x = point.getX() + offX;
			double y = point.getY() + offY;
			double z = point.getZ() + offZ;
			world.spawnParticle(Particle.BARRIER, x, y, z, particlesPerBlock, radius, 0d, radius);
//				world.spawnParticle(Particle.VILLAGER_HAPPY, x, y, z, particlesPerBlock, 0d, 0d, 0d);
		}
		offset = (offset + 1) % iter;
	}
}
