package me.gorgeousone.deathray.command;

import me.gorgeousone.deathray.cmdframework.argument.ArgType;
import me.gorgeousone.deathray.cmdframework.argument.ArgValue;
import me.gorgeousone.deathray.cmdframework.argument.Argument;
import me.gorgeousone.deathray.cmdframework.command.ArgCommand;
import me.gorgeousone.deathray.svg.PointCloud;
import me.gorgeousone.deathray.svg.SvgData;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class SummonCommand extends ArgCommand {
	
	private final JavaPlugin plugin;
	private final SvgData rune;
	private final ThreadLocalRandom rnd = ThreadLocalRandom.current();
	
	public SummonCommand(JavaPlugin plugin, SvgData rune) {
		super("summon");
		addAlias("s");
		addArg(new Argument("particles", ArgType.INTEGER));
		addArg(new Argument("duration", ArgType.DECIMAL).setDefault("3"));
		addArg(new Argument("density", ArgType.DECIMAL).setDefault("0.5"));
		addArg(new Argument("dx", ArgType.DECIMAL).setDefault("0"));
		addArg(new Argument("dy", ArgType.DECIMAL).setDefault("150"));
		addArg(new Argument("dz", ArgType.DECIMAL).setDefault("0"));
		
		this.plugin = plugin;
		this.rune = rune;
	}
	
	@Override
	protected void executeArgs(CommandSender sender, List<ArgValue> argValues, Set<String> usedFlags) {
		int particlesPerBlock = argValues.get(0).getInt();
		double duration = argValues.get(1).getDouble();
		double samplesPerBlock = argValues.get(2).getDouble();
		
        Player player = (Player) sender;
		Location location = player.getLocation();
		location.add(
				argValues.get(3).getDouble(),
				argValues.get(4).getDouble(),
				argValues.get(5).getDouble());
		
		renderRune(location, particlesPerBlock, duration, samplesPerBlock);
		renderAccumulate(location);
		renderEnergyBall(location);
		renderLaser(location);
	}
	
	void renderRune(Location location, int particlesPerBlock, double duration, double samplesPerBlock) {
		PointCloud pointCloud = rune.samplePoints(samplesPerBlock);
		//adapt radius to fill space between sample points without gaps or overlaps
		double radius = 0.5 / samplesPerBlock;
		int period = 1;
		int iter = 20 / period;
		
		new BukkitRunnable() {
			int countdown = (int) (duration * 20 / period);
			
			@Override
			public void run() {
				pointCloud.render(location, particlesPerBlock, radius, iter);
				
				if (--countdown <= 0) {
					cancel();
				}
			}
		}.runTaskTimer(plugin, 0, period);
	}
	
	public void renderAccumulate(Location location) {
		double buildUpDuration = 5;
		double stayDuration = 4;
		int ticks = (int) (buildUpDuration * 20);
		List<Vector> normals = sampleCircleNormals(2000);
		
		double minRadius = 15;
		double maxRadius = 80;
		double maxParticles = 1000;
		
		double radiusStep = (maxRadius - minRadius) / ticks;
		double particlesStep = maxParticles / ticks;
		World world = location.getWorld();
		
		new BukkitRunnable() {
			double currentRadius = 0;
			int countdown = ticks;
			double currentParticles = 1;
			
			@Override
			public void run() {
				for (int i = 0; i < currentParticles; i++) {
					int rndIndex = rnd.nextInt(normals.size());
					Vector normal = normals.get(rndIndex);
					Vector start = location.toVector().add(normal.clone().multiply(-(minRadius + rnd.nextDouble() * currentRadius)));
					world.spawnParticle(Particle.FLAME, start.getX(), start.getY(), start.getZ(), 0, normal.getX(), 0, normal.getZ(), 1);
				}
				if (countdown > 0) {
					currentRadius += radiusStep;
					currentParticles += particlesStep;
				}
				if (--countdown <= -stayDuration*20) {
					cancel();
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}
	
	private void renderEnergyBall(Location location) {
		double buildUpDuration = 5;
		double stayDuration = 9;
		int ticks = (int) (buildUpDuration * 20);
		List<Vector> normals = sampleSphereNormals(10000);
		
		double minRadius = 1;
		double maxRadius = 10;
		double maxParticles = 300;
		
		double radiusStep = (maxRadius - minRadius) / ticks;
		double particlesStep = maxParticles / ticks;
		World world = location.getWorld();
		double particleSpeed = 0.1;
		
		new BukkitRunnable() {
			double currentRadius = 0;
			int countdown = ticks;
			double currentParticles = 1;
			
			@Override
			public void run() {
				for (int i = 0; i < currentParticles; i++) {
					int rndIndex = rnd.nextInt(normals.size());
					Vector normal = normals.get(rndIndex);
					Vector start = location.toVector().add(normal.clone().multiply(-(minRadius + currentRadius + rnd.nextDouble())));
					world.spawnParticle(Particle.FIREWORKS_SPARK, start.getX(), start.getY(), start.getZ(), 0, normal.getX(), 0, normal.getZ(), particleSpeed);
				}
				if (countdown > 0) {
					currentRadius += radiusStep;
					currentParticles += particlesStep;
				}
				if (--countdown <= -stayDuration*20) {
					cancel();
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}
	
	private void renderLaser(Location location) {
		double delay = 10;
		double buildUpDuration = 1;
		double stayDuration = 4;
		double maxRadius = 1;
		double maxParticles = 500;
		
		int ticks = (int) (buildUpDuration * 20);
		double radiusStep = maxRadius / ticks;
		double particlesStep = maxParticles / ticks;
		
		World world = location.getWorld();
		List<Vector> points = sampleCircle(10000);
		double speed = 10;
		
		new BukkitRunnable() {
			double currentRadius = 0;
			int countdown = ticks;
			double currentParticles = 1;
			
			@Override
			public void run() {
				for (int i = 0; i < currentParticles; i++) {
					int rndIndex = rnd.nextInt(points.size());
					Vector point = points.get(rndIndex);
					
					Vector start = location.toVector().add(point.clone().multiply(currentRadius));
					world.spawnParticle(Particle.FIREWORKS_SPARK, start.getX(), start.getY() - rnd.nextDouble() * 25, start.getZ(), 0, 0, -1, 0, speed);
				}
				if (countdown > 0) {
					currentRadius += radiusStep;
					currentParticles += particlesStep;
				}
				if (--countdown <= -stayDuration*20) {
					cancel();
				}
			}
		}.runTaskTimer(plugin, (long) (delay * 20), 1);
	}
	
	private static List<Vector> sampleCircleNormals(int count) {
		List<Vector> normals = new ArrayList<>();
		
		for (int i = 0; i < count; i++) {
			double yaw = Math.random() * 2 * Math.PI;
			normals.add(new Vector(
					Math.cos(yaw),
					0,
					Math.sin(yaw)));
		}
		return normals;
	}
	
	private static List<Vector> sampleCircle(int count) {
		List<Vector> points = new ArrayList<>();
		
		for (int i = 0; i < count; i++) {
			double yaw = Math.random() * 2 * Math.PI;
			double radius = Math.sqrt(Math.random());
			points.add(new Vector(
					Math.cos(yaw) * radius,
					0,
					Math.sin(yaw) * radius));
		}
		return points;
	}
	
	private static List<Vector> sampleSphereNormals(int count) {
		List<Vector> normals = new ArrayList<>();
		
		for (int i = 0; i < count; i++) {
			double yaw = Math.random() * 2 * Math.PI;
			double pitch = Math.asin(Math.random() * 2 - 1);
			double cosPitch = Math.cos(pitch);
			
			normals.add(new Vector(
					Math.cos(yaw) * cosPitch,
					Math.sin(pitch),
					Math.sin(yaw) * cosPitch));
		}
		return normals;
	}
}
