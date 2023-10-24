package me.gorgeousone.deathray;

import me.gorgeousone.deathray.cmdframework.command.ParentCommand;
import me.gorgeousone.deathray.cmdframework.handler.CommandHandler;
import me.gorgeousone.deathray.command.SummonCommand;
import me.gorgeousone.deathray.svg.SvgData;
import me.gorgeousone.deathray.svg.SvgParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class DeathRayPlugin extends JavaPlugin {
	
	@Override
	public void onEnable() {
		registerCommands();
	}
	
	private SvgData loadRune() {
		File file = getDataFolder().toPath().resolve("rune_circle.svg").toFile();
		try {
			return SvgParser.parseSvg(file, 1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void registerCommands() {
		
		ParentCommand deathCmd = new ParentCommand("death");
		deathCmd.addChild(new SummonCommand(this, loadRune()));
		
		CommandHandler cmdHandler = new CommandHandler(this);
		cmdHandler.registerCommand(deathCmd);
	}
}
