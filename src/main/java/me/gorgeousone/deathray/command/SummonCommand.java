package me.gorgeousone.deathray.command;

import me.gorgeousone.deathray.cmdframework.argument.ArgType;
import me.gorgeousone.deathray.cmdframework.argument.ArgValue;
import me.gorgeousone.deathray.cmdframework.argument.Argument;
import me.gorgeousone.deathray.cmdframework.command.ArgCommand;
import me.gorgeousone.deathray.svg.PointCloud;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Set;

public class SummonCommand extends ArgCommand {
	
	private final JavaPlugin plugin;
	private final PointCloud pointCloud;
	
	public SummonCommand(JavaPlugin plugin, PointCloud pointCloud) {
		super("summon");
		addAlias("s");
		addArg(new Argument("duration", ArgType.DECIMAL));
		addArg(new Argument("particles", ArgType.INTEGER).setDefault("1"));
		addArg(new Argument("dx", ArgType.DECIMAL).setDefault("0"));
		addArg(new Argument("dy", ArgType.DECIMAL).setDefault("0"));
		addArg(new Argument("dz", ArgType.DECIMAL).setDefault("0"));
		
		this.plugin = plugin;
		this.pointCloud = pointCloud;
	}
	
	@Override
	protected void executeArgs(CommandSender sender, List<ArgValue> argValues, Set<String> usedFlags) {
		double duration = argValues.get(0).getDouble();
		int particleCount = argValues.get(1).getInt();

		Player player = (Player) sender;
		Location location = player.getLocation();
		location.add(
				argValues.get(2).getDouble(),
				argValues.get(3).getDouble(),
				argValues.get(4).getDouble());
		
		new BukkitRunnable() {
//			int countdown = (int) (duration * 20);
			int countdown = (int) duration;
			
			@Override
			public void run() {
				pointCloud.render(location, particleCount);
				
				if (--countdown <= 0) {
					cancel();
				}
			}
		}.runTaskTimer(plugin, 0, 20);
	}
}
