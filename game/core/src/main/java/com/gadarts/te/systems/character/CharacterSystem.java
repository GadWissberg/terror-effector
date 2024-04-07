package com.gadarts.te.systems.character;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.TimeUtils;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.common.definitions.character.SpriteType;
import com.gadarts.te.common.utils.GeneralUtils;
import com.gadarts.te.components.ComponentsMapper;
import com.gadarts.te.components.cd.CharacterDecalComponent;
import com.gadarts.te.components.character.CharacterComponent;
import com.gadarts.te.components.character.CharacterSpriteData;
import com.gadarts.te.systems.GameSystem;
import com.gadarts.te.systems.SystemEvent;
import com.gadarts.te.systems.data.SharedData;
import com.gadarts.te.systems.data.SharedDataBuilder;
import com.gadarts.te.systems.map.GameHeuristic;
import com.gadarts.te.systems.map.MapGraphPath;
import com.gadarts.te.systems.map.graph.MapGraph;
import com.gadarts.te.systems.map.graph.MapGraphNode;

import static com.gadarts.te.common.definitions.character.CharacterType.BILLBOARD_Y;
import static com.gadarts.te.common.definitions.character.SpriteType.IDLE;
import static com.gadarts.te.systems.SystemEvent.CHARACTER_ANIMATION_RUN_NEW_FRAME;

public class CharacterSystem extends GameSystem {
    public static final long MAX_IDLE_ANIMATION_INTERVAL = 10000L;
    private static final long MIN_IDLE_ANIMATION_INTERVAL = 2000L;
    private final static Vector2 auxVector2_1 = new Vector2();
    private final static Vector2 auxVector2_2 = new Vector2();
    private static final float CHAR_STEP_SIZE = 0.22f;
    private static final Vector3 auxVector3_1 = new Vector3();
    private static final Vector3 auxVector3_2 = new Vector3();
    private static final float MOVEMENT_EPSILON = 0.02F;
    private final GameHeuristic heuristic = new GameHeuristic();
    private final Queue<CharacterCommand> commandsToExecute = new Queue<>();
    private final MapGraphPath auxPath = new MapGraphPath();
    private ImmutableArray<Entity> characters;
    private CharacterCommand commandInProgress;
    private IndexedAStarPathFinder<MapGraphNode> pathFinder;

    @Override
    public void initialize(SharedDataBuilder sharedDataBuilder, GameAssetsManager assetsManager, MessageDispatcher eventDispatcher) {
        super.initialize(sharedDataBuilder, assetsManager, eventDispatcher);
        characters = getEngine().getEntitiesFor(Family.all(CharacterComponent.class).get());
        subscribeToEvents(SystemEvent.PLAYER_REQUESTS_MOVE, CHARACTER_ANIMATION_RUN_NEW_FRAME);
    }

    @Override
    public void onSystemReady(SharedData sharedData) {
        super.onSystemReady(sharedData);
        pathFinder = new IndexedAStarPathFinder<>(sharedData.mapGraph());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        long now = TimeUtils.millis();
        for (int i = 0; i < characters.size(); i++) {
            Entity character = characters.get(i);
            handleIdle(character, now);
        }
        if (commandInProgress != null) {
            beginCommand();
        } else if (commandsToExecute.notEmpty()) {
            commandInProgress = commandsToExecute.removeFirst();
        }
    }

    private void beginCommand( ) {
        if (commandInProgress.getState() == CharacterCommandState.READY) {
            if (commandInProgress.getCharacterCommandDefinition() == CharacterCommandDefinition.GO_TO) {
                Entity initiator = commandInProgress.getInitiator();
                ComponentsMapper.character.get(initiator).getCharacterSpriteData().setSpriteType(SpriteType.RUN);
                Decal decal = ComponentsMapper.characterDecal.get(initiator).getDecal();
                Vector3 position = decal.getPosition();
                MapGraphNode startNode = sharedData.mapGraph().getNode((int) position.x, (int) position.z);
                auxPath.clear();
                boolean found = pathFinder.searchNodePath(startNode, commandInProgress.getDestination(), heuristic, auxPath);
                if (found) {
                    commandInProgress.setState(CharacterCommandState.IN_PROGRESS);
                    commandInProgress.setPath(auxPath);
                    commandInProgress.setNextNodeIndex(0);
                } else {
                    commandInProgress = null;
                }
            }
        }
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        boolean handled = false;
        if (msg.message == SystemEvent.PLAYER_REQUESTS_MOVE.ordinal()) {
            CharacterCommand characterCommand = Pools.get(CharacterCommand.class).obtain();
            Vector2 extraInfo = (Vector2) msg.extraInfo;
            MapGraphNode destination = sharedData.mapGraph().getNode((int) extraInfo.x, (int) extraInfo.y);
            characterCommand.init(CharacterCommandDefinition.GO_TO, destination, sharedData.player());
            commandsToExecute.addLast(characterCommand);
            handled = true;
        } else if (msg.message == CHARACTER_ANIMATION_RUN_NEW_FRAME.ordinal()) {
            handleRunning();
            handled = true;
        }
        return handled;
    }

    private void handleRunning( ) {
        Decal decal = ComponentsMapper.characterDecal.get(commandInProgress.getInitiator()).getDecal();
        placeCharacterInNextNodeIfCloseEnough(decal);
        Vector2 characterPosition = auxVector2_1.set(decal.getX(), decal.getZ());
        int nextNodeIndex = commandInProgress.getNextNodeIndex();
        MapGraphNode nextNode = commandInProgress.getPath().get(nextNodeIndex);
        boolean done = false;
        if (nextNodeIndex == -1 || characterPosition.dst2(nextNode.getCenterPosition(auxVector2_2)) < MOVEMENT_EPSILON) {
            done = reachedNodeOfPath();
        }
        if (!done) {
            takeStep();
        } else {
            ComponentsMapper.character.get(commandInProgress.getInitiator()).getCharacterSpriteData().setSpriteType(IDLE);
            commandInProgress = null;
        }
    }


    private void takeStep( ) {
        CharacterDecalComponent characterDecalComponent = ComponentsMapper.characterDecal.get(commandInProgress.getInitiator());
        MapGraph map = sharedData.mapGraph();
        Vector2 nodePosition = characterDecalComponent.getNodePosition(auxVector2_1);
        MapGraphNode currentNode = map.getNode((int) nodePosition.x, (int) nodePosition.y);
        translateCharacter(characterDecalComponent);
        nodePosition = characterDecalComponent.getNodePosition(auxVector2_1);
        MapGraphNode newNode = map.getNode((int) nodePosition.x, (int) nodePosition.y);
        if (currentNode != newNode) {
            fixHeightPositionOfDecals(newNode);
        }
    }

    private void fixHeightPositionOfDecals(MapGraphNode newNode) {
        CharacterDecalComponent characterDecalComponent = ComponentsMapper.characterDecal.get(commandInProgress.getInitiator());
        Decal decal = characterDecalComponent.getDecal();
        Vector3 position = decal.getPosition();
        float newNodeHeight = newNode.getHeight();
        decal.setPosition(position.x, newNodeHeight + BILLBOARD_Y, position.z);
    }

    private void translateCharacter(CharacterDecalComponent characterDecalComponent) {
        Vector3 decalPos = characterDecalComponent.getDecal().getPosition();
        Entity floorEntity = sharedData.mapGraph().getNode((int) decalPos.x, (int) decalPos.z).getEntity();
        Decal decal = characterDecalComponent.getDecal();
        if (floorEntity != null) {
            MapGraphNode nextNode = commandInProgress.getPath().get(commandInProgress.getNextNodeIndex());
            Vector2 nextNodePosition = auxVector2_1.set(nextNode.getX(), nextNode.getZ()).add(0.5F, 0.5F);
            Vector2 velocity = nextNodePosition.sub(auxVector2_2.set(decal.getX(), decal.getZ())).nor().scl(CHAR_STEP_SIZE);
            decal.translate(auxVector3_1.set(velocity.x, 0, velocity.y));
        } else {
            placeCharacterInTheNextNode(decal);
        }
    }

    private boolean reachedNodeOfPath( ) {
        eventDispatcher.dispatchMessage(SystemEvent.CHARACTER_REACHED_NODE.ordinal(), commandInProgress.getInitiator());
        MapGraphPath path = commandInProgress.getPath();
        commandInProgress.setPrevNode(path.get(commandInProgress.getNextNodeIndex()));
        commandInProgress.setNextNodeIndex(commandInProgress.getNextNodeIndex() + 1);
        int nextNodeIndex = commandInProgress.getNextNodeIndex();
        MapGraphNode nextNode = nextNodeIndex < path.nodes.size ? path.get(nextNodeIndex) : null;
        MapGraph mapGraph = sharedData.mapGraph();
        return nextNodeIndex == -1 || mapGraph.findConnection(commandInProgress.getPrevNode(), nextNode) == null;
    }


    private void placeCharacterInNextNodeIfCloseEnough(Decal decal) {
        Vector3 decalPos = decal.getPosition();
        MapGraphNode nextNode = commandInProgress.getPath().get(commandInProgress.getNextNodeIndex());
        float distanceToNextNode = nextNode.getCenterPosition(auxVector2_1).dst2(decalPos.x, decalPos.z);
        if (distanceToNextNode < CHAR_STEP_SIZE) {
            placeCharacterInTheNextNode(decal);
        }
    }

    private void placeCharacterInTheNextNode(Decal decal) {
        Vector3 centerPos = commandInProgress.getPath().get(commandInProgress.getNextNodeIndex()).getCenterPosition(auxVector3_1);
        decal.setPosition(auxVector3_2.set(centerPos.x, centerPos.y + BILLBOARD_Y, centerPos.z));
    }

    private void handleIdle(Entity character, long now) {
        CharacterSpriteData spriteData = ComponentsMapper.character.get(character).getCharacterSpriteData();
        SpriteType spriteType = spriteData.getSpriteType();
        if (spriteType == IDLE && spriteData.getNextIdleAnimationPlay() < now) {
            long random = MathUtils.random(MAX_IDLE_ANIMATION_INTERVAL - MIN_IDLE_ANIMATION_INTERVAL);
            spriteData.setNextIdleAnimationPlay(now + MIN_IDLE_ANIMATION_INTERVAL + random);
            ComponentsMapper.animation.get(character).getAnimation().setFrameDuration(spriteType.getFrameDuration());
        }
    }

    @Override
    public void dispose( ) {
        GeneralUtils.disposeObject(this, CharacterSystem.class);
    }
}
