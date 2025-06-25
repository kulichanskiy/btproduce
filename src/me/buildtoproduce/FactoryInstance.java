package me.buildtoproduce;

import org.bukkit.Location;
import java.util.List;
import java.util.UUID;

public class FactoryInstance {
	private final String name;
	private final UUID owner;
	private final List<Location> blocks;
	
	public FactoryInstance(String name, UUID owner, List<Location> blocks) {
		this.name = name;
		this.owner = owner;
		this.blocks = blocks;
	}
	
	public String getName() {
		return name;
	}
	
	public UUID getOwner() {
		return owner;
	}
	public List<Location> getBlocks() {
		return blocks;
	}
}
