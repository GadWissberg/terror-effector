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
import com.gadarts.te.components.character.CharacterComponent;
import com.gadarts.te.components.character.CharacterSpriteData;
import com.gadarts.te.systems.GameSystem;
import com.gadarts.te.systems.SystemEvent;
import com.gadarts.te.systems.data.SharedData;
import com.gadarts.te.systems.data.SharedDataBuilder;
import com.gadarts.te.systems.map.GameHeuristic;
import com.gadarts.te.systems.map.MapGraphPath;
import com.gadarts.te.systems.map.graph.MapGraphNode;

import static com.gadarts.te.common.definitions.character.SpriteType.IDLE;

public class CharacterSystem extends GameSystem {
    public static final long MAX_IDLE_ANIMATION_INTERVAL = 10000L;
    private static final long MIN_IDLE_ANIMATION_INTERVAL = 2000L;
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
        subscribeToEvents(SystemEvent.PLAYER_REQUESTS_MOVE);
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
            if (commandInProgress.getState() == CharacterCommandState.IN_PROGRESS) {

            } else if (commandInProgress.getState() == CharacterCommandState.READY) {
                if (commandInProgress.getCharacterCommandDefinition() == CharacterCommandDefinition.GO_TO) {
                    Decal decal = ComponentsMapper.characterDecal.get(commandInProgress.getInitiator()).getDecal();
                    Vector3 position = decal.getPosition();
                    MapGraphNode startNode = sharedData.mapGraph().getNode((int) position.x, (int) position.z);
                    auxPath.clear();
                    boolean found = pathFinder.searchNodePath(startNode, commandInProgress.getDestination(), heuristic, auxPath);
                    if (found) {
                        commandInProgress.setState(CharacterCommandState.IN_PROGRESS);
                        commandInProgress.setPath(auxPath);
                    } else {
                        commandInProgress = null;
                    }
                }
            }
        } else if (commandsToExecute.notEmpty()) {
            commandInProgress = commandsToExecute.removeFirst();
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
        }
        return handled;
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
