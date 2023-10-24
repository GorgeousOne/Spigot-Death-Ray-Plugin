package me.gorgeousone.deathray.command;

import me.gorgeousone.deathray.cmdframework.argument.ArgType;
import me.gorgeousone.deathray.cmdframework.argument.ArgValue;
import me.gorgeousone.deathray.cmdframework.argument.Argument;
import me.gorgeousone.deathray.cmdframework.command.ArgCommand;
import me.gorgeousone.deathray.svg.PointCloud;
import me.gorgeousone.deathray.svg.SvgData;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Set;

public class SummonCommand extends ArgCommand {
	
	private final JavaPlugin plugin;
	private final SvgData rune;
	
//	/death summon 1 20 0.5 1 0 100 0
	public SummonCommand(JavaPlugin plugin, SvgData rune) {
		super("summon");
		addAlias("s");
		addArg(new Argument("particles", ArgType.INTEGER));
		addArg(new Argument("duration", ArgType.DECIMAL).setDefault("3"));
		addArg(new Argument("density", ArgType.DECIMAL).setDefault("5"));
		addArg(new Argument("radius", ArgType.DECIMAL).setDefault("1"));
		addArg(new Argument("dx", ArgType.DECIMAL).setDefault("0"));
		addArg(new Argument("dy", ArgType.DECIMAL).setDefault("0"));
		addArg(new Argument("dz", ArgType.DECIMAL).setDefault("0"));
		
		this.plugin = plugin;
		this.rune = rune;
	}
	
	@Override
	protected void executeArgs(CommandSender sender, List<ArgValue> argValues, Set<String> usedFlags) {
		int particlesPerBlock = argValues.get(0).getInt();
		double duration = argValues.get(1).getDouble();
		PointCloud pointCloud = rune.samplePoints(argValues.get(2).getDouble());
		double radius = argValues.get(3).getDouble();

		Player player = (Player) sender;
		Location location = player.getLocation();
		location.add(
				argValues.get(4).getDouble(),
				argValues.get(5).getDouble(),
				argValues.get(6).getDouble());
		
		int period = 1;
		int iter = 20 / period;
		
		new BukkitRunnable() {
			int countdown = (int) (duration * 20 / period);
//			int countdown = (int) duration;
			
			@Override
			public void run() {
				pointCloud.render(location, particlesPerBlock, radius, iter);
				
				if (--countdown <= 0) {
					cancel();
				}
			}
		}.runTaskTimer(plugin, 0, period);
	}
}
