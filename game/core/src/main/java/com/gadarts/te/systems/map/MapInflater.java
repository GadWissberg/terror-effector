package com.gadarts.te.systems.map;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pools;
import com.gadarts.te.DebugSettings;
import com.gadarts.te.EntityBuilder;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.common.assets.atlas.Atlases;
import com.gadarts.te.common.assets.definitions.Definitions;
import com.gadarts.te.common.assets.definitions.DefinitionsUtils;
import com.gadarts.te.common.assets.definitions.character.CharacterDefinition;
import com.gadarts.te.common.assets.definitions.character.enemy.EnemiesDefinitions;
import com.gadarts.te.common.assets.definitions.character.enemy.EnemyDefinition;
import com.gadarts.te.common.assets.definitions.character.player.PlayerDefinition;
import com.gadarts.te.common.assets.definitions.character.player.PlayerWeaponDefinition;
import com.gadarts.te.common.assets.definitions.character.player.PlayerWeaponsDefinitions;
import com.gadarts.te.common.assets.definitions.env.EnvObjectDefinition;
import com.gadarts.te.common.assets.definitions.env.EnvObjectsDefinitions;
import com.gadarts.te.common.assets.texture.SurfaceTextures;
import com.gadarts.te.common.definitions.character.CharacterType;
import com.gadarts.te.common.map.*;
import com.gadarts.te.common.map.element.Direction;
import com.gadarts.te.common.utils.EnvObjectUtils;
import com.gadarts.te.common.utils.GameException;
import com.gadarts.te.common.utils.GeneralUtils;
import com.gadarts.te.components.ComponentsMapper;
import com.gadarts.te.components.ModelInstanceComponent;
import com.gadarts.te.components.cd.CharacterAnimations;
import com.gadarts.te.components.character.CharacterSpriteData;
import com.gadarts.te.systems.map.graph.MapGraph;
import com.gadarts.te.systems.map.graph.MapGraphNode;
import com.google.gson.*;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static com.gadarts.te.EntityBuilder.beginBuildingEntity;
import static com.gadarts.te.common.assets.atlas.Atlases.PLAYER_GLOCK;
import static com.gadarts.te.common.assets.texture.SurfaceTextures.MISSING;
import static com.gadarts.te.common.definitions.character.SpriteType.IDLE;
import static com.gadarts.te.common.map.MapJsonKeys.*;
import static com.gadarts.te.common.map.MapNodesTypes.OBSTACLE_KEY_DIAGONAL_FORBIDDEN;
import static com.gadarts.te.common.map.MapNodesTypes.PASSABLE_NODE;
import static com.gadarts.te.components.ComponentsMapper.modelInstance;
import static java.lang.String.format;

public class MapInflater implements Disposable {
    public static final String MAP_PATH_TEMP = "maps/%s.json";
    private static final Matrix4 auxMatrix = new Matrix4();
    private final static Vector3 auxVector3_1 = new Vector3();
    private final Gson gson = new GsonBuilder().create();
    private final GameAssetsManager assetsManager;
    private final Engine engine;
    private final WallCreator wallCreator;
    private final Model floorModel = MapUtils.createFloorModel();


    public MapInflater(GameAssetsManager assetsManager, Engine engine) {
        this.assetsManager = assetsManager;
        this.engine = engine;
        wallCreator = new WallCreator(assetsManager, false);
    }


    public MapGraph inflate(String mapName) {
        String path = format(MAP_PATH_TEMP, mapName);
        JsonObject mapJsonObj = gson.fromJson(Gdx.files.internal(path).reader(), JsonObject.class);
        MapGraph mapGraph = createMapGraph(mapJsonObj);
        inflateNodes(mapJsonObj.get(NODES).getAsJsonObject(), mapGraph);
        inflateHeightsAndWalls(mapJsonObj, mapGraph);
        inflateElements(mapJsonObj, mapGraph);
        mapGraph.init();
        return mapGraph;
    }


    private CharacterSpriteData createCharacterSpriteData(CharacterDefinition definition) {
        CharacterSpriteData characterSpriteData = Pools.obtain(CharacterSpriteData.class);
        characterSpriteData.init(
            IDLE,
            definition.getPrimaryAttackHitFrameIndex(),
            definition.isSingleDeathAnimation());
        return characterSpriteData;
    }

    private void inflateElements(JsonObject mapJsonObj, MapGraph mapGraph) {
        inflateEnvObjects(mapJsonObj, mapGraph);
        inflateCharacters(mapJsonObj, mapGraph);
    }

    private void inflateEnvObjects(JsonObject mapJsonObj, MapGraph mapGraph) {
        mapJsonObj.get(ELEMENTS).getAsJsonObject().get(ENV_OBJECTS).getAsJsonArray().forEach(jsonElement -> {
            Entity entity = engine.createEntity();
            ModelInstanceComponent component = engine.createComponent(ModelInstanceComponent.class);
            JsonObject elementJsonObject = jsonElement.getAsJsonObject();
            Coords coords = new Coords(elementJsonObject.get(COORD_X).getAsInt(), elementJsonObject.get(COORD_Z).getAsInt());
            EnvObjectsDefinitions envObjectDefinitions = (EnvObjectsDefinitions) assetsManager.getDefinition(Definitions.ENV_OBJECTS);
            String definition = elementJsonObject.get(DEFINITION).getAsString();
            EnvObjectDefinition envObjectDefinition;
            try {
                List<EnvObjectDefinition> definitions = envObjectDefinitions.definitions();
                envObjectDefinition = definitions.stream()
                    .filter(def -> def.id()
                        .equalsIgnoreCase(definition))
                    .findFirst()
                    .orElseThrow((Supplier<Throwable>) ( ) -> new GameException("No env-object definition with %s".formatted(definition)));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            component.init(EnvObjectUtils.createModelInstanceForEnvObject(
                    assetsManager,
                    coords,
                    mapGraph.getNode(coords).getHeight(),
                    envObjectDefinition,
                    Direction.valueOf(elementJsonObject.get(DIRECTION).getAsString())),
                false);
            entity.add(component);
            engine.addEntity(entity);
        });
    }

    private void inflateCharacters(JsonObject mapJsonObj, MapGraph mapGraph) {
        mapJsonObj.get(ELEMENTS).getAsJsonObject().get(CHARACTERS).getAsJsonArray().forEach(jsonElement -> {
            JsonObject asJsonObject = jsonElement.getAsJsonObject();
            String characterDefinition = asJsonObject.get(DEFINITION).getAsString();
            EnemiesDefinitions enemiesDefinitions = (EnemiesDefinitions) assetsManager.getDefinition(Definitions.ENEMIES);
            CharacterDefinition selectedDefinition = DefinitionsUtils.parse(characterDefinition, enemiesDefinitions.definitions());
            if (selectedDefinition == null) {
                selectedDefinition = PlayerDefinition.getInstance();
            }
            EntityBuilder builder;
            Atlases atlas;
            CharacterAnimations animations;
            if (selectedDefinition.getCharacterType() == CharacterType.PLAYER) {
                builder = beginBuildingEntity(engine).addPlayerComponent(assetsManager.get(PLAYER_GLOCK.name()));
                PlayerWeaponsDefinitions playerWeaponsDefinitions = (PlayerWeaponsDefinitions) assetsManager.getDefinition(Definitions.PLAYER_WEAPONS);
                List<PlayerWeaponDefinition> definitions = playerWeaponsDefinitions.definitions();
                PlayerWeaponDefinition startingWeaponDefinition = DefinitionsUtils.parse(DebugSettings.STARTING_WEAPON, definitions);
                animations = assetsManager.get(startingWeaponDefinition.relatedAtlas().name());
                atlas = startingWeaponDefinition.relatedAtlas();
            } else {
                assert selectedDefinition instanceof EnemyDefinition;
                builder = beginBuildingEntity(engine).addEnemyComponent((EnemyDefinition) selectedDefinition);
                animations = assetsManager.get(selectedDefinition.getAtlasDefinition().name());
                atlas = selectedDefinition.getAtlasDefinition();
            }
            CharacterSpriteData characterSpriteData = createCharacterSpriteData(selectedDefinition);
            Direction direction = Direction.valueOf(asJsonObject.get(DIRECTION).getAsString());
            float x = asJsonObject.get(COORD_X).getAsInt() + 0.5F;
            float z = asJsonObject.get(COORD_Z).getAsInt() + 0.5F;
            Vector3 position = new Vector3(
                x,
                mapGraph.getNode((int) x, (int) z).getHeight(),
                z);
            builder.addCharacterComponent(characterSpriteData, direction)
                .addCharacterDecalComponent(assetsManager.get(atlas.name()), IDLE, direction, position)
                .addAnimationComponent(animations.get(IDLE, direction));
            Entity character = builder.finishAndAddToEngine();
            ComponentsMapper.floor.get(mapGraph.getNode((int) x, (int) z).getEntity()).setContainedCharacter(character);
        });
    }

    private void inflateNodes(JsonObject nodesJsonObject, MapGraph mapGraph) {
        byte[] matrixByte = Base64.getDecoder().decode(nodesJsonObject.get(MATRIX).getAsString().getBytes());
        IntStream.range(0, mapGraph.getDepth()).forEach(row ->
            IntStream.range(0, mapGraph.getWidth()).forEach(col -> {
                byte currentValue = matrixByte[row * mapGraph.getWidth() + col % mapGraph.getDepth()];
                if (currentValue != 0) {
                    MapGraphNode node = mapGraph.getNode(col, row);
                    inflateNode(row, col, currentValue, node);
                }
            }));
    }

    private void inflateNode(final int row, final int col, final byte chr, MapGraphNode node) {
        SurfaceTextures definition = SurfaceTextures.values()[chr - 1];
        EntityBuilder entityBuilder = beginBuildingEntity(engine);
        if (definition != MISSING) {
            ModelInstance mi = new ModelInstance(floorModel);
            defineNodeModelInstance(row, col, definition, mi);
            entityBuilder.addModelInstanceComponent(mi);
        }
        node.setType(definition == SurfaceTextures.BLANK ? OBSTACLE_KEY_DIAGONAL_FORBIDDEN : node.getType());
        node.setEntity(entityBuilder.addFloorComponent(node, definition).finishAndAddToEngine());
    }

    private void defineNodeModelInstance(int row,
                                         int col,
                                         SurfaceTextures definition,
                                         ModelInstance mi) {
        mi.materials.get(0).set(TextureAttribute.createDiffuse(assetsManager.getTexture(definition)));
        mi.transform.setTranslation(auxVector3_1.set(col + 0.5f, 0, row + 0.5f));
    }

    private MapGraphNode getNodeByJson(final MapGraph mapGraph, final JsonObject tileJsonObject) {
        int row = tileJsonObject.get(COORD_Z).getAsInt();
        int col = tileJsonObject.get(COORD_X).getAsInt();
        return mapGraph.getNode(col, row);
    }

    private void inflateNodeHeight(JsonObject nodeDataJsonObject, MapGraphNode node) {
        if (!nodeDataJsonObject.has(HEIGHT)) return;
        float height = nodeDataJsonObject.get(HEIGHT).getAsFloat();
        node.setHeight(height);
        Entity entity = node.getEntity();
        if (entity != null && modelInstance.has(entity)) {
            modelInstance.get(entity).getModelInstance().transform.translate(0, height, 0);
        }
    }

    private void inflateWalls(final JsonObject nodeWallsJsonObject,
                              final MapGraphNode node,
                              final MapGraph mapGraph) {
        inflateEastWall(nodeWallsJsonObject, node, mapGraph);
        inflateSouthWall(nodeWallsJsonObject, node, mapGraph);
        inflateWestWall(nodeWallsJsonObject, node, mapGraph);
        inflateNorthWall(nodeWallsJsonObject, node, mapGraph);
    }

    private void inflateEastWall(JsonObject nodeWallsJsonObject,
                                 MapGraphNode node,
                                 MapGraph mapGraph) {
        int eastX = node.getX() + 1;
        JsonElement east = nodeWallsJsonObject.get(EAST);
        if (eastX < mapGraph.getWidth()) {
            if (node.getHeight() != mapGraph.getNode(eastX, node.getZ()).getHeight() && east != null) {
                JsonObject asJsonObject = east.getAsJsonObject();
                WallParameters wallParameters = inflateWallParameters(asJsonObject);
                applyEastWall(node, mapGraph, wallParameters, eastX);
            }
        }
    }

    private void applyEastWall(MapGraphNode node,
                               MapGraph mapGraph,
                               WallParameters wallParameters,
                               int eastNodeX) {
        SurfaceTextures definition = wallParameters.getDefinition();
        if (definition != MISSING) {
            MapNodeData nodeData = new MapNodeData(node.getX(), node.getZ(), PASSABLE_NODE, new ModelInstance((floorModel)), definition);
            NodeWalls walls = nodeData.getWalls();
            walls.setEastWall(wallCreator.createWall(nodeData, wallCreator.getEastWallModel(), assetsManager, definition));
            MapNodeData eastNodeData = new MapNodeData(nodeData.getCoords().getX() + 1,
                nodeData.getCoords().getZ(),
                PASSABLE_NODE,
                new ModelInstance((floorModel)),
                definition);
            nodeData.applyHeight(node.getHeight());
            if (eastNodeX >= 0 && eastNodeX < mapGraph.getWidth()) {
                eastNodeData.setHeight(mapGraph.getNode(eastNodeData.getCoords()).getHeight());
            }
            wallCreator.adjustEastWall(nodeData, eastNodeData);
            inflateWall(walls.getEastWall(), nodeData, mapGraph);
        }
    }

    private void inflateWall(Wall wall, MapNodeData parentNodeData, MapGraph mapGraph) {
        if (wall == null) return;

        BoundingBox bBox = wall.getModelInstance().calculateBoundingBox(new BoundingBox());
        avoidZeroDimensions(bBox);
        bBox.mul(auxMatrix.set(wall.getModelInstance().transform).setTranslation(Vector3.Zero));
        ModelInstance modelInstance = new ModelInstance(wall.getModelInstance());
        beginBuildingEntity(engine).addModelInstanceComponent(modelInstance)
            .addWallComponent(mapGraph.getNode(parentNodeData.getCoords()))
            .finishAndAddToEngine();
    }

    private void avoidZeroDimensions(final BoundingBox bBox) {
        Vector3 center = bBox.getCenter(auxVector3_1);
        if (bBox.getWidth() == 0) {
            center.x += 0.01F;
        }
        if (bBox.getHeight() == 0) {
            center.y += 0.01F;
        }
        if (bBox.getDepth() == 0) {
            center.z += 0.01F;
        }
        bBox.ext(center);
    }

    private WallParameters inflateWallParameters(JsonObject asJsonObject) {
        SurfaceTextures definition = SurfaceTextures.valueOf(asJsonObject.get(TEXTURE).getAsString());
        return new WallParameters(
            asJsonObject.has(V_SCALE) ? asJsonObject.get(V_SCALE).getAsFloat() : 0,
            asJsonObject.has(H_OFFSET) ? asJsonObject.get(H_OFFSET).getAsFloat() : 0,
            asJsonObject.has(V_OFFSET) ? asJsonObject.get(V_OFFSET).getAsFloat() : 0,
            definition);
    }

    private void inflateSouthWall(final JsonObject nodeWallsJsonObject,
                                  final MapGraphNode node,
                                  final MapGraph mapGraph) {
        int southZ = node.getZ() + 1;
        JsonElement south = nodeWallsJsonObject.get(MapJsonKeys.SOUTH);
        if (southZ < mapGraph.getDepth()) {
            if (node.getHeight() != mapGraph.getNode(node.getX(), southZ).getHeight() && south != null) {
                JsonObject asJsonObject = south.getAsJsonObject();
                WallParameters wallParameters = inflateWallParameters(asJsonObject);
                applySouthWall(node, mapGraph, wallParameters, southZ);
            }
        }
    }

    private void applySouthWall(MapGraphNode node,
                                MapGraph mapGraph,
                                WallParameters wallParameters,
                                int southNodeZ) {
        SurfaceTextures definition = wallParameters.getDefinition();
        if (definition != MISSING) {
            MapNodeData nodeData = new MapNodeData(
                node.getZ(),
                node.getX(),
                PASSABLE_NODE,
                new ModelInstance((floorModel)),
                definition);
            NodeWalls walls = nodeData.getWalls();
            walls.setSouthWall(wallCreator.createWall(
                nodeData,
                wallCreator.getSouthWallModel(),
                assetsManager,
                definition));
            Coords coords = nodeData.getCoords();
            MapNodeData southNodeData = new MapNodeData(
                coords.getX(),
                southNodeZ,
                PASSABLE_NODE, new ModelInstance((floorModel)),
                definition);
            nodeData.setHeight(node.getHeight());
            if (southNodeZ >= 0 && southNodeZ < mapGraph.getDepth()) {
                southNodeData.setHeight(mapGraph.getNode(southNodeData.getCoords()).getHeight());
            }
            wallCreator.adjustSouthWall(southNodeData, nodeData);
            inflateWall(walls.getSouthWall(), nodeData, mapGraph);
        }
    }

    private void applyNorthWall(MapGraphNode node,
                                MapGraph mapGraph,
                                WallParameters wallParameters,
                                int northNodeRow) {
        SurfaceTextures definition = wallParameters.getDefinition();
        if (definition != MISSING) {
            MapNodeData nodeData = new MapNodeData(
                node.getX(),
                node.getZ(),
                PASSABLE_NODE,
                new ModelInstance((floorModel)),
                definition);
            NodeWalls walls = nodeData.getWalls();
            walls.setNorthWall(wallCreator.createWall(
                nodeData,
                wallCreator.getNorthWallModel(),
                assetsManager,
                definition));
            Coords coords = nodeData.getCoords();
            MapNodeData northNodeData = new MapNodeData(
                coords.getX(),
                coords.getZ() - 1,
                PASSABLE_NODE,
                new ModelInstance((floorModel)),
                definition);
            nodeData.setHeight(node.getHeight());
            if (northNodeRow >= 0 && northNodeRow < mapGraph.getDepth()) {
                northNodeData.setHeight(mapGraph.getNode(northNodeData.getCoords()).getHeight());
            }
            wallCreator.adjustNorthWall(nodeData, northNodeData);
            inflateWall(walls.getNorthWall(), nodeData, mapGraph);
        }
    }

    private void inflateNorthWall(final JsonObject nodeWallsJsonObject,
                                  final MapGraphNode node,
                                  final MapGraph mapGraph) {
        int northZ = node.getZ() - 1;
        JsonElement north = nodeWallsJsonObject.get(MapJsonKeys.NORTH);
        if (northZ >= 0) {
            if (node.getHeight() != mapGraph.getNode(node.getX(), northZ).getHeight() && north != null) {
                JsonObject asJsonObject = north.getAsJsonObject();
                WallParameters wallParameters = inflateWallParameters(asJsonObject);
                applyNorthWall(node, mapGraph, wallParameters, northZ);
            }
        }
    }

    private void inflateWestWall(final JsonObject nodeWallsJsonObject,
                                 final MapGraphNode node,
                                 final MapGraph mapGraph) {
        int col = node.getX();
        int westCol = col - 1;
        JsonElement west = nodeWallsJsonObject.get(WEST);
        if (westCol >= 0) {
            if (node.getHeight() != mapGraph.getNode(westCol, node.getZ()).getHeight() && west != null) {
                JsonObject asJsonObject = west.getAsJsonObject();
                WallParameters wallParameters = inflateWallParameters(asJsonObject);
                applyWestWall(node, mapGraph, wallParameters, westCol);
            }
        }
    }

    private void applyWestWall(MapGraphNode node,
                               MapGraph mapGraph,
                               WallParameters wallParameters,
                               int westNodeX) {
        SurfaceTextures definition = wallParameters.getDefinition();
        if (definition != MISSING) {
            MapNodeData nodeData = new MapNodeData(
                node.getX(),
                node.getZ(),
                PASSABLE_NODE,
                new ModelInstance((floorModel)),
                definition);
            NodeWalls walls = nodeData.getWalls();
            walls.setWestWall(wallCreator.createWall(
                nodeData,
                wallCreator.getWestWallModel(),
                assetsManager,
                definition));
            Coords coords = nodeData.getCoords();
            MapNodeData westNodeData = new MapNodeData(node.getX() - 1,
                coords.getZ(),
                PASSABLE_NODE,
                new ModelInstance((floorModel)),
                definition);
            nodeData.setHeight(node.getHeight());
            if (westNodeX >= 0 && westNodeX < mapGraph.getWidth()) {
                westNodeData.setHeight(mapGraph.getNode(westNodeData.getCoords()).getHeight());
            }
            wallCreator.adjustWestWall(
                westNodeData,
                nodeData);
            inflateWall(walls.getWestWall(), nodeData, mapGraph);
        }
    }


    private void inflateHeightsAndWalls(JsonObject mapJsonObject,
                                        MapGraph mapGraph) {
        JsonArray nodesData = mapJsonObject.get(NODES).getAsJsonObject().getAsJsonArray(NODES_DATA);
        if (nodesData == null) return;

        nodesData.forEach(nodeDataJson -> {
            JsonObject nodeDataJsonObject = nodeDataJson.getAsJsonObject();
            MapGraphNode node = getNodeByJson(mapGraph, nodeDataJsonObject);
            inflateNodeHeight(nodeDataJsonObject, node);
        });
        nodesData.forEach(nodeDataJson -> {
            JsonObject nodeDataJsonObject = nodeDataJson.getAsJsonObject();
            MapGraphNode node = getNodeByJson(mapGraph, nodeDataJsonObject);
            if (nodeDataJsonObject.has(WALLS)) {
                inflateWalls(nodeDataJsonObject.getAsJsonObject(WALLS), node, mapGraph);
            }
        });
        JsonElement heightsElement = mapJsonObject.get(NODES).getAsJsonObject().get(HEIGHTS);
        Optional.ofNullable(heightsElement).ifPresent(element -> {
            JsonArray heights = element.getAsJsonArray();
            heights.forEach(jsonElement -> {
                JsonObject tileJsonObject = jsonElement.getAsJsonObject();
                MapGraphNode node = getNodeByJson(mapGraph, tileJsonObject);
                float height = tileJsonObject.get(HEIGHT).getAsFloat();
                node.setHeight(height);
                Entity entity = node.getEntity();
                if (entity != null && modelInstance.has(entity)) {
                    modelInstance.get(entity).getModelInstance().transform.translate(0, height, 0);
                }
            });
            heights.forEach(jsonElement -> {
                JsonObject tileJsonObject = jsonElement.getAsJsonObject();
                MapGraphNode node = getNodeByJson(mapGraph, tileJsonObject);
                inflateWalls(tileJsonObject, node, mapGraph);
            });
        });
    }

    private MapGraph createMapGraph(final JsonObject mapJsonObj) {
        JsonObject nodesJsonObject = mapJsonObj.get(NODES).getAsJsonObject();
        return new MapGraph(
            nodesJsonObject.get(WIDTH).getAsInt(),
            nodesJsonObject.get(DEPTH).getAsInt());
    }

    @Override
    public void dispose( ) {
        GeneralUtils.disposeObject(this, MapInflater.class);
    }
}
