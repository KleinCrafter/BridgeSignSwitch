package eu.letsmine.sponge.bridgesignswitch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.DataRegistration.Builder;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.manipulator.mutable.block.DirectionalData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.TargetBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.ArchetypeVolume;
import org.spongepowered.api.world.schematic.Schematic;

import com.flowpowered.math.vector.Vector3i;
import com.google.inject.Inject;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

@Plugin(id = BridgeSignSwitch.ID, name = BridgeSignSwitch.NAME, version = BridgeSignSwitch.VERSION)
public class BridgeSignSwitch {
	public static final String ID = "bridgesignswitch";
	public static final String NAME = "BridgeSignSwitch";
	public static final String VERSION = "0.0.3";
	
	private static final Map<Vector3i, Schematic> BRIDGES = new HashMap<>();
	
	@Inject
	private Game game;
	
	@Inject
	private PluginContainer pluginContainer;
	
	@Inject
	private Logger logger;
	
	@Inject
	@DefaultConfig(sharedRoot = true)
	private Path defaultConfig;

	@Inject
	@DefaultConfig(sharedRoot = true)
	private ConfigurationLoader<CommentedConfigurationNode> configManager;

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path privateConfigDir;
	private Path bridgeDir;

	private Text bridgeSignTemplate;
	private Text bridgeEndSignTemplate;
	
	@Listener
	public void onPreInit(GamePreInitializationEvent event) {
		DataRegistration.<BridgeData, BridgeData.Immutable>builder()
		      .setDataClass(BridgeData.class)
		      .setImmutableDataClass(BridgeData.Immutable.class)
		      .setBuilder(new BridgeData.Builder())
		      .setManipulatorId("BridgeData")
		      .buildAndRegister(this);
	}
	
	@Listener
	public void onServerStart(GameStartedServerEvent event) {
		bridgeDir = privateConfigDir.resolve("bridges");
		File bridgeFile = bridgeDir.toFile();
		if (!bridgeFile.exists()) {
			logger.info("Creating: " + bridgeFile.toString());
			bridgeFile.mkdirs();
		} else {
			logger.info("Exist: " + bridgeFile.toString());
		}
		File[] files = bridgeFile.listFiles();
		if (files.length > 0) {
			logger.info("Loading " + files.length + " Bridges");
			for (File file : files) {
				try {
					Path bridgeConfig = file.toPath();
					logger.info("Load: " + bridgeConfig.toString());
					ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(bridgeConfig).build();
					ConfigurationNode bridgeRootNote = loader.load();
					Vector3i v = bridgeRootNote.getNode("vector").getValue(DataTranslators.VECTOR_3_I.getToken());
					Schematic s = bridgeRootNote.getNode("schematic").getValue(DataTranslators.LEGACY_SCHEMATIC.getToken());
					
					BRIDGES.put(v, s);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		} else {
			logger.info("No Bridges found.");
		}
		
		bridgeSignTemplate = Text.builder("[Bridge]").build();
		bridgeEndSignTemplate = Text.builder("[Bridge End]").build();
	}
	
	@Listener
	public void onServerStop(GameStoppingServerEvent event) {
		BRIDGES.forEach((v, s) -> {
			try {
				Path bridgeConfig = bridgeDir.resolve("" + v.hashCode());
				ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(bridgeConfig).build();
				ConfigurationNode rootNode = loader.createEmptyNode(ConfigurationOptions.defaults());
				ConfigurationNode vectorNode = rootNode.getNode("vector");
				vectorNode.setValue(DataTranslators.VECTOR_3_I.getToken(), v);
				ConfigurationNode schematicNode = rootNode.getNode("schematic");
				schematicNode.setValue(DataTranslators.LEGACY_SCHEMATIC.getToken(), s);
			
				loader.save(rootNode);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			
		});
		BRIDGES.clear();
	}
	
	@Listener
	public void onChangeBlockEvent(TargetBlockEvent event) {
		if (event.getTargetBlock().getExtendedState().getType() == BlockTypes.STANDING_SIGN) {
			String[] causes = event.getCause().all().stream().map(o -> o.toString()).toArray(String[]::new);
			logger.info(event.getClass().getSimpleName() + " Debug: " + String.join(" ", causes));
		}
	}
	
	@Listener
	public void onSignInteract(InteractBlockEvent.Secondary.MainHand event, @First Player player) {
		BlockSnapshot bs = event.getTargetBlock();
		Optional<Location<World>> optionalLocation = bs.getLocation();
		if (optionalLocation.isPresent()) {
			Location<World> location = optionalLocation.get();
			Optional<TileEntity> optionalTe = location.getTileEntity();
			if (optionalTe.isPresent()) {
				TileEntity te = optionalTe.get();
				Optional<SignData> optionalSignData = te.get(SignData.class);
				if (optionalSignData.isPresent()) {
					SignData signData = optionalSignData.get();
					Optional<Text> optionalText = signData.get(1);
					if (optionalText.isPresent()) {
						if (optionalText.get().compareTo(bridgeSignTemplate) == 0) {
							BridgeData bd = te.getOrCreate(BridgeData.class).get();
							if (bd.getDistance() == 0) {
								//Not Created yet
								createBridge(player, location, te, bd);
							} else {
								//Toggle Bridge
								toggleBridge(player, location, te, bd);
							}
							
						} else {
							logger.info("Debug: SignTemplate not Match");
						}
					} else {
						logger.info("Debug: Line 1 not found");
					}
				} else {
					logger.info("Debug: Not a Sign");
				}
			} else {
				logger.info("Debug: No TileEntiry found");
			}
		} else {
			logger.info("Debug: Location not Found");
		}
	}
	
	private void createBridge(Player player, Location<World> location, TileEntity tileEntity, BridgeData bridgeData) {
		Optional<DirectionalData> optionalDirection = location.get(DirectionalData.class);
		if (optionalDirection.isPresent()) {
			DirectionalData directionalData = optionalDirection.get();
			Direction direction = directionalData.direction().get();
			if (direction.isCardinal()) {
				Direction oppositDirection = direction.getOpposite();
				Optional<Location<World>> optionalOtherSign = findOtherSign(location, oppositDirection, 21, 1, bridgeSignTemplate, bridgeEndSignTemplate);
				if (optionalOtherSign.isPresent()) {
					Location<World> otherSignLocation = optionalOtherSign.get();
					double distance = otherSignLocation.getPosition().distance(location.getPosition());
					bridgeData.setDistance((int)distance);
					bridgeData.setPrimary(true);
					logger.info("Debug: Sign Found: " + distance);
					DataTransactionResult result = tileEntity.offer(bridgeData);
					if (result.getType() != DataTransactionResult.Type.SUCCESS) {
						logger.info("Debug: Offer Failed");
						tileEntity.undo(result);
					} else {
						logger.info("Debug: Offer Success");
						//Other Sign
						Optional<TileEntity> optionalOtherTileEntity = otherSignLocation.getTileEntity();
						if (!optionalOtherTileEntity.isPresent()) {
							logger.error("Sign without TileEntity: %s", otherSignLocation);
							return;
						}
						TileEntity otherTileEntity = optionalOtherTileEntity.get();
						Optional<BridgeData> optionalOtherBridgeData = otherTileEntity.getOrCreate(BridgeData.class);
						if (!optionalOtherBridgeData.isPresent()) {
							logger.error("Can not getOrCreate BridgeData: %s", otherSignLocation);
							return;
						}
						BridgeData otherBridgeData = optionalOtherBridgeData.get();
						otherBridgeData.setDistance((int)distance);
						otherBridgeData.setPrimary(false);
						DataTransactionResult otherResult = otherTileEntity.offer(otherBridgeData);
						if (otherResult.getType() != DataTransactionResult.Type.SUCCESS) {
							logger.info("Debug: Other Offer Failed");
							otherTileEntity.undo(otherResult);
						} else {
							//Now we can toggle the Bridge
							toggleBridge(player, location, tileEntity, bridgeData);
						}
					}
				} else {
					logger.info("Debug: No other Sign Found");
				}
			} else {
				logger.info("Debug: No Cardinal Direction");
			}
		} else {
			logger.info("Debug: Direction not Found");
		}
	}
	
	private Optional<Location<World>> findOtherSign(Location<World> start, Direction direction, int r, int line, Text... text) {
		//public Sign searchSign(World world, int xStart, int yStart, int zStart, byte xSearch, byte ySearch, byte zSearch, int r, Material material, String... signPattern) {
		Location<World> otherBlock = start.getBlockRelative(direction); //Ersten Block Überspringen
		for (byte i = 1; i <= r; i++) {
			otherBlock = otherBlock.getBlockRelative(direction);
			if (isSign(otherBlock.getBlockType())) {
				Optional<TileEntity> optionalTileEntity = otherBlock.getTileEntity();
				if (optionalTileEntity.isPresent()) {
					Optional<SignData> optionalSignData = optionalTileEntity.get().get(SignData.class);
					SignData signData = optionalSignData.get();
					Optional<Text> optionalText = signData.get(line);
					if (optionalText.isPresent()) {
						for (Text t : text) {
							if (optionalText.get().compareTo(t) == 0) {
								return Optional.of(otherBlock);
							}
						}
					}
				}
			}
		}
		return Optional.empty();
	}
	
	private boolean isSign(BlockType type) {
		return type == BlockTypes.STANDING_SIGN || type == BlockTypes.WALL_SIGN;
	}
	
	private void toggleBridge(Player player, Location<World> location, TileEntity tileEntity, BridgeData bridgeData) {
		Optional<DirectionalData> optionalDirection = location.get(DirectionalData.class);
		if (optionalDirection.isPresent()) {
			DirectionalData directionalData = optionalDirection.get();
			Direction direction = directionalData.direction().get();
			if (direction.isCardinal()) {
				Direction oppositDirection = direction.getOpposite();
				Vector3i otherSignVektorOffset = oppositDirection.asBlockOffset().mul(bridgeData.getDistance());
				Location<World> otherSignLocation = location.add(otherSignVektorOffset);
				Optional<TileEntity> optionalOtherTileEntity = otherSignLocation.getTileEntity();
				if (!optionalOtherTileEntity.isPresent()) {
					logger.info("Debug: Other Sign not found.");
					return;
				}
				TileEntity otherTileEntity = optionalOtherTileEntity.get();

				Optional<DirectionalData> optionalOtherDirectionalData = otherSignLocation.get(DirectionalData.class); //Other Sign destroyed an replaced
				if (!optionalOtherDirectionalData.isPresent()) {
					logger.info(String.format("Debug: Other isn't a Sign?! %1$s", otherSignLocation));
					return;
				}
				
				if (optionalOtherDirectionalData.get().direction().get() != oppositDirection) {
					logger.info("Debug: Other Sign has a wrong Direction.");
					return;
				}
				
				Optional<BridgeData> optionalOtherBridgeData = otherTileEntity.getOrCreate(BridgeData.class); //Other Sign destroyed an replaced
				if (!optionalOtherBridgeData.isPresent()) {
					logger.error("Can not getOrCreate BridgeData: %s", otherSignLocation);
					return;
				}
				BridgeData otherBridgeData = optionalOtherBridgeData.get();
				if (otherBridgeData.getDistance() > 0) {
					if (otherBridgeData.getDistance() != bridgeData.getDistance()) {
						//TODO: Handle Replaced Signs...
					
						otherBridgeData.setDistance(bridgeData.getDistance());
						otherBridgeData.setPrimary(!bridgeData.isPrimary());
						otherBridgeData.setOpen(bridgeData.isOpen());
						DataTransactionResult otherResult = otherTileEntity.offer(otherBridgeData);
						if (otherResult.getType() != DataTransactionResult.Type.SUCCESS) {
							logger.info("Debug: Other Offer Failed");
							otherTileEntity.undo(otherResult);
							return;
						}
					}
					//TODO: Open and Primary match check -> strange BridgeSign forest...
				}
				
				Location<World> primaryLocation = bridgeData.isPrimary() ? location : otherBridgeData.isPrimary() ? otherSignLocation : null;
				if (primaryLocation == null) {
					logger.error("no Primary Sign found...");
					return;
				}
				
				Vector3i p0Vector = primaryLocation.getBlockPosition();
				
				//Site calculate
				byte x;
				byte z;
				if (direction == Direction.NORTH) {
					x = -1;
					z = 0;
				} else if (direction == Direction.EAST) {
					x = 0;
					z = 1;
				} else if (direction == Direction.SOUTH) {
					x = 1;
					z = 0;
				} else if (direction == Direction.WEST) {
					x = 0;
					z = -1;
				} else {
					logger.error("Easter egg?! -> It's a Bug not a Feature");
					return;
				}
				
				//one Down, one Forward
				Location<World> p1 = location.getBlockRelative(oppositDirection).getBlockRelative(Direction.DOWN);
				//one Down, one Backward
				Location<World> p2 = otherSignLocation.getBlockRelative(direction).getBlockRelative(Direction.DOWN);

				Vector3i p1Vector = p1.getBlockPosition();
				Vector3i p2Vector = p2.getBlockPosition();

				int xmin = Math.min(p1Vector.getX(), p2Vector.getX()) + x;
				int zmin = Math.min(p1Vector.getZ(), p2Vector.getZ()) + z;
				
				int y = p1Vector.getY();
				
				int xmax = Math.max(p1Vector.getX(), p2Vector.getX()) - x;
				int zmax = Math.max(p1Vector.getZ(), p2Vector.getZ()) - z;

				Vector3i minVector = new Vector3i(xmin, y, zmin);
				Vector3i maxVector = new Vector3i(xmax, y, zmax);
				
				//Aktuellen zwischenraum speichern
				World world = primaryLocation.getExtent();
				ArchetypeVolume archetypeVolume = world.createArchetypeVolume(minVector, maxVector, p0Vector);
				if (archetypeVolume == null) {
					logger.info("Debug: ArchetypeVolume can not create");
					return;
				}
				Schematic bridgeSchematic = Schematic.builder().volume(archetypeVolume)
					.metaValue(Schematic.METADATA_AUTHOR, BridgeSignSwitch.NAME)
					.metaValue(Schematic.METADATA_NAME, p0Vector.hashCode()).build();
				
				//Aktuellen Zwischenraum laden
				Schematic newBridge = BRIDGES.remove(p0Vector);
				if (newBridge == null) {
					//Zwischenraum löschen
					for (int xc = xmin; xc <= xmax; xc++) {
						for (int zc = zmin; zc <= zmax; zc++) {
							world.setBlock(xc, y, zc, BlockTypes.AIR.getDefaultState(), BlockChangeFlag.ALL, Cause.source(pluginContainer).build());
						}
					}
				} else {
					//Zwischenraum einfügen wenn vorhanden
					newBridge.apply(primaryLocation, BlockChangeFlag.ALL, Cause.source(pluginContainer).owner(player).build());
				}
				
				BRIDGES.put(p0Vector, bridgeSchematic);
				
				bridgeData.setOpen(!bridgeData.isOpen());
				otherBridgeData.setOpen(bridgeData.isOpen());
				
				DataTransactionResult result = tileEntity.offer(bridgeData);
				if (result.getType() != DataTransactionResult.Type.SUCCESS) {
					logger.info("Debug: Offer Failed");
					tileEntity.undo(result);
				} else {
					logger.info("Debug: Offer Success");
					DataTransactionResult otherResult = otherTileEntity.offer(otherBridgeData);
					if (otherResult.getType() != DataTransactionResult.Type.SUCCESS) {
						logger.info("Debug: Other Offer Failed");
						otherTileEntity.undo(otherResult);
					} else {
						logger.info("Debug: Other Offer Success");
					}
				}
			} else {
				logger.info("Debug: No Cardinal Direction");
			}
		} else {
			logger.info("Debug: Direction not Found");
		}
	}
	
}
