package com.gadarts.te.systems.map;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;
import com.gadarts.te.EntityBuilder;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.common.assets.texture.SurfaceTextures;
import com.gadarts.te.common.map.*;
import com.gadarts.te.common.model.GameModelInstance;
import com.gadarts.te.systems.map.graph.MapGraph;
import com.gadarts.te.systems.map.graph.MapGraphNode;
import com.google.gson.*;

import java.util.Base64;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.gadarts.te.EntityBuilder.beginBuildingEntity;
import static com.gadarts.te.common.assets.texture.SurfaceTextures.MISSING;
import static com.gadarts.te.common.map.MapJsonKeys.*;
import static com.gadarts.te.common.map.MapNodesTypes.OBSTACLE_KEY_DIAGONAL_FORBIDDEN;
import static com.gadarts.te.components.ComponentsMapper.modelInstance;
import static java.lang.String.format;

public class MapInflater implements Disposable {
    public static final String MAP_PATH_TEMP = "maps/%s.json";
    private static final Matrix4 auxMatrix = new Matrix4();
    private final static Vector3 auxVector3_1 = new Vector3();
    private static final Vector3 auxVector3_2 = new Vector3();
    private static final Vector3 auxVector3_3 = new Vector3();
    private static final Vector3 auxVector3_4 = new Vector3();
    private static final Vector3 auxVector3_5 = new Vector3();
    private final Gson gson = new GsonBuilder().create();
    private final GameAssetsManager assetsManager;
    private final Engine engine;
    private final WallCreator wallCreator;
    private final Model floorModel;

    public MapInflater(GameAssetsManager assetsManager, Engine engine) {
        this.assetsManager = assetsManager;
        this.engine = engine;
        wallCreator = new WallCreator(assetsManager);
        floorModel = createFloorModel();
    }

    private Model createFloorModel( ) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder meshPartBuilder = modelBuilder.part("floor",
            GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,
            createFloorMaterial());
        createRect(meshPartBuilder);
        return modelBuilder.end();
    }

    private Material createFloorMaterial( ) {
        Material material = new Material();
        material.id = "floor_test";
        BlendingAttribute blendingAttribute = new BlendingAttribute();
        blendingAttribute.opacity = 1F;
        material.set(blendingAttribute);
        return material;
    }

    private void createRect(final MeshPartBuilder meshPartBuilder) {
        meshPartBuilder.setUVRange(0, 0, 1, 1);
        final float OFFSET = -0.5f;
        meshPartBuilder.rect(
            auxVector3_4.set(OFFSET, 0, 1 + OFFSET),
            auxVector3_1.set(1 + OFFSET, 0, 1 + OFFSET),
            auxVector3_2.set(1 + OFFSET, 0, OFFSET),
            auxVector3_3.set(OFFSET, 0, OFFSET),
            auxVector3_5.set(0, 1, 0));
    }

    public MapGraph inflate(String mapName) {
        String path = format(MAP_PATH_TEMP, mapName);
        JsonObject mapJsonObj = gson.fromJson(Gdx.files.internal(path).reader(), JsonObject.class);
        MapGraph mapGraph = createMapGraph(mapJsonObj);
        inflateNodes(mapJsonObj.get(NODES).getAsJsonObject(), mapGraph);
        inflateHeightsAndWalls(mapJsonObj, mapGraph);
        return mapGraph;
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
            GameModelInstance mi = new GameModelInstance(floorModel);
            defineNodeModelInstance(row, col, definition, mi);
            entityBuilder.addModelInstanceComponent(mi);
        }
        node.setType(definition == SurfaceTextures.BLANK ? OBSTACLE_KEY_DIAGONAL_FORBIDDEN : node.getType());
        node.setEntity(entityBuilder.addFloorComponent(node, definition).finishAndAddToEngine());
    }

    private void defineNodeModelInstance(int row,
                                         int col,
                                         SurfaceTextures definition,
                                         GameModelInstance mi) {
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
                              final float height,
                              final MapGraph mapGraph) {
        inflateEastWall(nodeWallsJsonObject, node, height, mapGraph);
        inflateSouthWall(nodeWallsJsonObject, node, height, mapGraph);
        inflateWestWall(nodeWallsJsonObject, node, height, mapGraph);
        inflateNorthWall(nodeWallsJsonObject, node, height, mapGraph);
    }

    private void inflateEastWall(final JsonObject nodeWallsJsonObject,
                                 final MapGraphNode node,
                                 final float height,
                                 final MapGraph mapGraph) {
        int col = node.getX();
        int eastCol = col + 1;
        JsonElement east = nodeWallsJsonObject.get(EAST);
        if (eastCol < mapGraph.getWidth()) {
            if (height != mapGraph.getNode(eastCol, node.getZ()).getHeight() && east != null) {
                JsonObject asJsonObject = east.getAsJsonObject();
                WallParameters wallParameters = inflateWallParameters(asJsonObject);
                applyEastWall(node, height, mapGraph, wallParameters, eastCol);
            }
        }
    }

    private void applyEastWall(MapGraphNode node,
                               float height,
                               MapGraph mapGraph,
                               WallParameters wallParameters,
                               int eastNodeCol) {
        com.gadarts.te.common.assets.texture.SurfaceTextures definition = wallParameters.getDefinition();
        if (definition != MISSING) {
            MapNodeData nodeData = new MapNodeData(node.getZ(), node.getX(), OBSTACLE_KEY_DIAGONAL_FORBIDDEN);
            NodeWalls walls = nodeData.getWalls();
            walls.setEastWall(WallCreator.createWall(
                nodeData,
                wallCreator.getEastWallModel(),
                assetsManager,
                definition));
            Coords coords = nodeData.getCoords();
            MapNodeData eastNodeData = new MapNodeData(
                coords.getZ(),
                eastNodeCol,
                OBSTACLE_KEY_DIAGONAL_FORBIDDEN);
            nodeData.lift(height);
            if (eastNodeCol >= 0 && eastNodeCol < mapGraph.getWidth()) {
                eastNodeData.setHeight(mapGraph.getNode(eastNodeData.getCoords()).getHeight());
            }
            wallCreator.adjustEastWall(nodeData, eastNodeData);
            inflateWall(walls.getEastWall(), nodeData, mapGraph);
        }
    }

    private void inflateWall(Wall wall, MapNodeData parentNodeData, MapGraph mapGraph) {
        BoundingBox bBox = wall.getModelInstance().calculateBoundingBox(new BoundingBox());
        avoidZeroDimensions(bBox);
        bBox.mul(auxMatrix.set(wall.getModelInstance().transform).setTranslation(Vector3.Zero));
        GameModelInstance modelInstance = new GameModelInstance(wall.getModelInstance());
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
                                  final float height,
                                  final MapGraph mapGraph) {
        int row = node.getZ();
        int southRow = row + 1;
        JsonElement south = nodeWallsJsonObject.get(MapJsonKeys.SOUTH);
        if (southRow < mapGraph.getDepth()) {
            if (height != mapGraph.getNode(node.getX(), southRow).getHeight() && south != null) {
                JsonObject asJsonObject = south.getAsJsonObject();
                WallParameters wallParameters = inflateWallParameters(asJsonObject);
                applySouthWall(node, height, mapGraph, wallParameters, southRow);
            }
        }
    }

    private void applySouthWall(MapGraphNode node,
                                float height,
                                MapGraph mapGraph,
                                WallParameters wallParameters,
                                int southNodeRow) {
        SurfaceTextures definition = wallParameters.getDefinition();
        if (definition != MISSING) {
            MapNodeData nodeData = new MapNodeData(node.getZ(), node.getX(), OBSTACLE_KEY_DIAGONAL_FORBIDDEN);
            NodeWalls walls = nodeData.getWalls();
            walls.setSouthWall(WallCreator.createWall(
                nodeData,
                wallCreator.getSouthWallModel(),
                assetsManager,
                definition));
            Coords coords = nodeData.getCoords();
            MapNodeData southNodeData = new MapNodeData(
                southNodeRow,
                coords.getX(),
                OBSTACLE_KEY_DIAGONAL_FORBIDDEN);
            nodeData.lift(height);
            if (southNodeRow >= 0 && southNodeRow < mapGraph.getDepth()) {
                southNodeData.setHeight(mapGraph.getNode(southNodeData.getCoords()).getHeight());
            }
            wallCreator.adjustSouthWall(southNodeData, nodeData);
            inflateWall(walls.getSouthWall(), nodeData, mapGraph);
        }
    }

    private void applyNorthWall(MapGraphNode node,
                                float height,
                                MapGraph mapGraph,
                                WallParameters wallParameters,
                                int northNodeRow) {
        SurfaceTextures definition = wallParameters.getDefinition();
        if (definition != MISSING) {
            MapNodeData nodeData = new MapNodeData(node.getZ(), node.getX(), OBSTACLE_KEY_DIAGONAL_FORBIDDEN);
            NodeWalls walls = nodeData.getWalls();
            walls.setNorthWall(WallCreator.createWall(
                nodeData,
                wallCreator.getNorthWallModel(),
                assetsManager,
                definition));
            Coords coords = nodeData.getCoords();
            MapNodeData northNodeData = new MapNodeData(
                northNodeRow,
                coords.getZ(),
                OBSTACLE_KEY_DIAGONAL_FORBIDDEN);
            nodeData.lift(height);
            if (northNodeRow >= 0 && northNodeRow < mapGraph.getDepth()) {
                northNodeData.setHeight(mapGraph.getNode(northNodeData.getCoords()).getHeight());
            }
            wallCreator.adjustNorthWall(nodeData, northNodeData);
            inflateWall(walls.getNorthWall(), nodeData, mapGraph);
        }
    }

    private void inflateNorthWall(final JsonObject nodeWallsJsonObject,
                                  final MapGraphNode node,
                                  final float height,
                                  final MapGraph mapGraph) {
        int row = node.getZ();
        int northRow = row - 1;
        JsonElement north = nodeWallsJsonObject.get(MapJsonKeys.NORTH);
        if (northRow >= 0) {
            if (height != mapGraph.getNode(node.getX(), northRow).getHeight() && north != null) {
                JsonObject asJsonObject = north.getAsJsonObject();
                WallParameters wallParameters = inflateWallParameters(asJsonObject);
                applyNorthWall(node, height, mapGraph, wallParameters, northRow);
            }
        }
    }

    private void inflateWestWall(final JsonObject nodeWallsJsonObject,
                                 final MapGraphNode node,
                                 final float height,
                                 final MapGraph mapGraph) {
        int col = node.getX();
        int westCol = col - 1;
        JsonElement west = nodeWallsJsonObject.get(WEST);
        if (westCol >= 0) {
            if (height != mapGraph.getNode(westCol, node.getZ()).getHeight() && west != null) {
                JsonObject asJsonObject = west.getAsJsonObject();
                WallParameters wallParameters = inflateWallParameters(asJsonObject);
                applyWestWall(node, height, mapGraph, wallParameters, westCol);
            }
        }
    }

    private void applyWestWall(MapGraphNode node,
                               float height,
                               MapGraph mapGraph,
                               WallParameters wallParameters,
                               int westNodeCol) {
        SurfaceTextures definition = wallParameters.getDefinition();
        if (definition != MISSING) {
            MapNodeData nodeData = new MapNodeData(node.getZ(), node.getX(), OBSTACLE_KEY_DIAGONAL_FORBIDDEN);
            NodeWalls walls = nodeData.getWalls();
            walls.setWestWall(WallCreator.createWall(
                nodeData,
                wallCreator.getWestWallModel(),
                assetsManager,
                definition));
            Coords coords = nodeData.getCoords();
            MapNodeData westNodeData = new MapNodeData(
                coords.getZ(),
                westNodeCol,
                OBSTACLE_KEY_DIAGONAL_FORBIDDEN);
            nodeData.lift(height);
            if (westNodeCol >= 0 && westNodeCol < mapGraph.getWidth()) {
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
        JsonObject nodesJsonObject = mapJsonObject.get(NODES).getAsJsonObject();
        JsonArray nodesData = nodesJsonObject.getAsJsonArray(NODES_DATA);
        nodesData.forEach(nodeDataJson -> {
            JsonObject nodeDataJsonObject = nodeDataJson.getAsJsonObject();
            MapGraphNode node = getNodeByJson(mapGraph, nodeDataJsonObject);
            inflateNodeHeight(nodeDataJsonObject, node);
        });
        nodesData.forEach(nodeDataJson -> {
            JsonObject nodeDataJsonObject = nodeDataJson.getAsJsonObject();
            MapGraphNode node = getNodeByJson(mapGraph, nodeDataJsonObject);
            if (nodeDataJsonObject.has(WALLS)) {
                inflateWalls(nodeDataJsonObject.getAsJsonObject(WALLS), node, node.getHeight(), mapGraph);
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
                float height = tileJsonObject.get(HEIGHT).getAsFloat();
                inflateWalls(tileJsonObject, node, height, mapGraph);
            });
        });
    }

    private MapGraph createMapGraph(final JsonObject mapJsonObj) {
        JsonObject tilesJsonObject = mapJsonObj.get(NODES).getAsJsonObject();
        return new MapGraph(tilesJsonObject.get(WIDTH).getAsInt(), tilesJsonObject.get(DEPTH).getAsInt());
    }

    @Override
    public void dispose( ) {
        floorModel.dispose();
        wallCreator.dispose();
    }
}
