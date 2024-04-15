package com.gadarts.te.systems.character;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.TimeUtils;
import com.gadarts.te.SoundPlayer;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.common.assets.sounds.Sounds;
import com.gadarts.te.common.definitions.character.SpriteType;
import com.gadarts.te.common.map.element.Direction;
import com.gadarts.te.common.utils.GeneralUtils;
import com.gadarts.te.components.AnimationComponent;
import com.gadarts.te.components.ComponentsMapper;
import com.gadarts.te.components.cd.CharacterDecalComponent;
import com.gadarts.te.components.character.CharacterComponent;
import com.gadarts.te.components.character.CharacterRotationData;
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
import static com.gadarts.te.common.map.element.Direction.SOUTH;
import static com.gadarts.te.common.map.element.Direction.findDirection;
import static com.gadarts.te.systems.SystemEvent.CHARACTER_ANIMATION_RUN_NEW_FRAME;

public class CharacterSystem extends GameSystem {
    public static final long MAX_IDLE_ANIMATION_INTERVAL = 10000L;
    public static final int CHARACTER_ROTATION_INTERVAL = 125;
    private static final long MIN_IDLE_ANIMATION_INTERVAL = 2000L;
    private final static Vector2 auxVector2_1 = new Vector2();
    private final static Vector2 auxVector2_2 = new Vector2();
    private final static Vector2 auxVector2_3 = new Vector2();
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
    public void initialize(SharedDataBuilder sharedDataBuilder, GameAssetsManager assetsManager, MessageDispatcher eventDispatcher, SoundPlayer soundPlayer) {
        super.initialize(sharedDataBuilder, assetsManager, eventDispatcher, soundPlayer);
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
            updateCurrentCommand();
        } else if (commandsToExecute.notEmpty()) {
            commandInProgress = commandsToExecute.removeFirst();
        }
    }

    private void updateCurrentCommand( ) {
        if (commandInProgress.getState() == CharacterCommandState.READY) {
            updateReadyGoTo();
        } else {
            Direction directionToDest = calculateDirectionToDestination();
            CharacterComponent characterComponent = ComponentsMapper.character.get(commandInProgress.getInitiator());
            CharacterRotationData rotData = characterComponent.getRotationData();
            Direction facingDirection = characterComponent.getFacingDirection();
            long lastRotation = rotData.getLastRotation();
            CharacterSpriteData characterSpriteData = characterComponent.getCharacterSpriteData();
            SpriteType spriteType = characterSpriteData.getSpriteType();
            if (spriteType == IDLE) {
                if (facingDirection != directionToDest) {
                    rotate(lastRotation, rotData, facingDirection, directionToDest, characterComponent);
                } else {
                    characterSpriteData.setSpriteType(SpriteType.RUN);
                }
            }
        }
    }

    private static void rotate(long lastRotation, CharacterRotationData rotData, Direction facingDirection, Direction directionToDest, CharacterComponent characterComponent) {
        if (TimeUtils.timeSinceMillis(lastRotation) > CHARACTER_ROTATION_INTERVAL) {
            rotData.setLastRotation(TimeUtils.millis());
            Vector2 currentDirVec = facingDirection.getDirection(auxVector2_1);
            float diff = directionToDest.getDirection(auxVector2_2).angleDeg() - currentDirVec.angleDeg();
            int side = auxVector2_3.set(1, 0).setAngleDeg(diff).angleDeg() > 180 ? -1 : 1;
            Direction newDir = findDirection(currentDirVec.rotateDeg(45f * side));
            characterComponent.setFacingDirection(newDir);
        }
    }

    private void updateReadyGoTo( ) {
        if (commandInProgress.getCharacterCommandDefinition() == CharacterCommandDefinition.GO_TO) {
            Entity initiator = commandInProgress.getInitiator();
            Decal decal = ComponentsMapper.characterDecal.get(initiator).getDecal();
            Vector3 position = decal.getPosition();
            MapGraphNode startNode = sharedData.mapGraph().getNode((int) position.x, (int) position.z);
            auxPath.clear();
            boolean found = pathFinder.searchNodePath(startNode, commandInProgress.getDestination(), heuristic, auxPath);
            if (found) {
                commandInProgress.setState(CharacterCommandState.IN_PROGRESS);
                commandInProgress.setPath(auxPath);
                commandInProgress.setNextNodeIndex(1);
            } else {
                commandInProgress = null;
            }
        }
    }

    private Direction calculateDirectionToDestination( ) {
        int nextNodeIndex = commandInProgress.getNextNodeIndex();
        MapGraphPath path = commandInProgress.getPath();
        if (nextNodeIndex >= path.nodes.size) return SOUTH;

        Entity character = commandInProgress.getInitiator();
        Vector3 characterPos = auxVector3_1.set(ComponentsMapper.characterDecal.get(character).getDecal().getPosition());
        Vector2 destPos = path.get(nextNodeIndex).getCenterPosition(auxVector2_2);
        Vector2 directionToDest = destPos.sub(characterPos.x, characterPos.z).nor();
        return findDirection(directionToDest);
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
        Entity initiator = commandInProgress.getInitiator();
        playStepSound(initiator);
        Decal decal = ComponentsMapper.characterDecal.get(initiator).getDecal();
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
            ComponentsMapper.character.get(initiator).getCharacterSpriteData().setSpriteType(IDLE);
            commandInProgress = null;
        }
    }

    private void playStepSound(Entity initiator) {
        AnimationComponent animationComponent = ComponentsMapper.animation.get(initiator);
        Animation<TextureAtlas.AtlasRegion> animation = animationComponent.getAnimation();
        int keyFrameIndex = animation.getKeyFrameIndex(animationComponent.getStateTime());
        if (keyFrameIndex == 0 || keyFrameIndex == 4) {
            soundPlayer.playSound(Sounds.STEP);
        }
    }


    private void takeStep( ) {
        CharacterDecalComponent characterDecalComponent = ComponentsMapper.characterDecal.get(commandInProgress.getInitiator());
        Decal decal = characterDecalComponent.getDecal();
        Vector3 decalPos = decal.getPosition();
        MapGraphNode nextNode = commandInProgress.getPath().get(commandInProgress.getNextNodeIndex());
        float distanceToNextNode = nextNode.getCenterPosition(auxVector2_1).dst2(decalPos.x, decalPos.z);
        Vector2 nodePosition = characterDecalComponent.getNodePosition(auxVector2_1);
        MapGraph map = sharedData.mapGraph();
        MapGraphNode currentNode = map.getNode((int) nodePosition.x, (int) nodePosition.y);
        if (distanceToNextNode < MOVEMENT_EPSILON) {
            placeCharacterInTheNextNode(decal);
        } else {
            translateCharacter(characterDecalComponent);
        }
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
        Decal decal = characterDecalComponent.getDecal();
        MapGraphNode nextNode = commandInProgress.getPath().get(commandInProgress.getNextNodeIndex());
        Vector2 nextNodePosition = auxVector2_1.set(nextNode.getX(), nextNode.getZ()).add(0.5F, 0.5F);
        Vector2 velocity = nextNodePosition.sub(auxVector2_2.set(decal.getX(), decal.getZ())).nor().scl(CHAR_STEP_SIZE);
        decal.translate(auxVector3_1.set(velocity.x, 0, velocity.y));
    }

    private boolean reachedNodeOfPath( ) {
        eventDispatcher.dispatchMessage(SystemEvent.CHARACTER_REACHED_NODE.ordinal(), commandInProgress.getInitiator());
        MapGraphPath path = commandInProgress.getPath();
        commandInProgress.setPrevNode(path.get(commandInProgress.getNextNodeIndex()));
        commandInProgress.setNextNodeIndex(commandInProgress.getNextNodeIndex() + 1);
        int nextNodeIndex = commandInProgress.getNextNodeIndex();
        MapGraphNode nextNode = nextNodeIndex < path.nodes.size ? path.get(nextNodeIndex) : null;
        MapGraph mapGraph = sharedData.mapGraph();
        boolean done = nextNodeIndex == -1 || mapGraph.findConnection(commandInProgress.getPrevNode(), nextNode) == null;
        if (!done && nextNode != null) {
            Vector2 nextNodeCenterPosition = nextNode.getCenterPosition(auxVector2_1);
            MapGraphNode prevNode = commandInProgress.getPrevNode();
            Direction newDirection = Direction.findDirection(nextNodeCenterPosition.sub(prevNode.getCenterPosition(auxVector2_2)));
            ComponentsMapper.character.get(commandInProgress.getInitiator()).setFacingDirection(newDirection);
        }
        return done;
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
