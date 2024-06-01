package com.gadarts.te.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Queue;
import com.gadarts.te.SoundPlayer;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.components.ComponentsMapper;
import com.gadarts.te.components.EnemyComponent;
import com.gadarts.te.components.character.CharacterComponent;
import com.gadarts.te.systems.character.CharacterCommand;
import com.gadarts.te.systems.character.CharacterCommandDefinition;
import com.gadarts.te.systems.data.SharedDataBuilder;
import com.gadarts.te.systems.enemy.EnemyUtils;
import com.gadarts.te.systems.map.graph.MapGraph;
import com.gadarts.te.systems.map.graph.MapGraphNode;

import java.util.LinkedHashSet;

import static com.gadarts.te.systems.SystemEvent.CHARACTER_ANIMATION_RUN_NEW_FRAME;
import static com.gadarts.te.systems.SystemEvent.ENEMY_NEW_TURN;

public class EnemySystem extends GameSystem {
    private final static LinkedHashSet<GridPoint2> bresenhamOutput = new LinkedHashSet<>();
    private final static Vector2 auxVector2_1 = new Vector2();
    private final static Vector2 auxVector2_2 = new Vector2();
    private static final float ENEMY_HALF_FOV_ANGLE = 95F;
    private ImmutableArray<Entity> enemiesEntities;

    @Override
    public void initialize(SharedDataBuilder sharedDataBuilder,
                           GameAssetsManager assetsManager,
                           MessageDispatcher eventDispatcher,
                           SoundPlayer soundPlayer) {
        super.initialize(sharedDataBuilder, assetsManager, eventDispatcher, soundPlayer);
        enemiesEntities = getEngine().getEntitiesFor(Family.all(EnemyComponent.class).get());
        subscribeToEvents(CHARACTER_ANIMATION_RUN_NEW_FRAME, SystemEvent.ENEMY_NEW_TURN);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

    }

    @Override
    public boolean handleMessage(Telegram msg) {
        if (msg.message == CHARACTER_ANIMATION_RUN_NEW_FRAME.ordinal()) {
            for (int i = 0; i < enemiesEntities.size(); i++) {
                awakeEnemyIfTargetSpotted(enemiesEntities.get(i));
            }
        } else if (msg.message == ENEMY_NEW_TURN.ordinal()) {
            Vector2 nodePosition = ComponentsMapper.characterDecal.get(sessionData.player()).getNodePosition(auxVector2_1);
            MapGraphNode destination = sessionData.mapGraph().getNode((int) nodePosition.x, (int) nodePosition.y);
            Queue<CharacterCommand> characterCommands = sessionData.commandsToExecute();
            characterCommands.addLast((new CharacterCommand()).init(CharacterCommandDefinition.GO_TO, destination, (Entity) msg.extraInfo));
            characterCommands.addLast((new CharacterCommand()).init(CharacterCommandDefinition.ATTACK_MELEE, destination, (Entity) msg.extraInfo));
        }
        return false;
    }

    private boolean isTargetInFov(final Entity enemy) {
        CharacterComponent charComponent = ComponentsMapper.character.get(enemy);
        Vector3 enemyPos = ComponentsMapper.characterDecal.get(enemy).getDecal().getPosition();
        Vector3 targetPos = ComponentsMapper.characterDecal.get(sessionData.player()).getDecal().getPosition();
        Vector2 enemyDirection = charComponent.getFacingDirection().getDirection(auxVector2_1);
        float dirToTarget = auxVector2_2.set(targetPos.x, targetPos.z).sub(enemyPos.x, enemyPos.z).nor().angleDeg();
        float angleDiff = (enemyDirection.angleDeg() - dirToTarget + 180 + 360) % 360 - 180;
        return angleDiff <= ENEMY_HALF_FOV_ANGLE && angleDiff >= -ENEMY_HALF_FOV_ANGLE;
    }

    private void awakeEnemyIfTargetSpotted(final Entity enemy) {
        if (!isTargetInFov(enemy)) return;

        LinkedHashSet<GridPoint2> nodes = EnemyUtils.findAllNodesToTarget(enemy, bresenhamOutput, true, sessionData.player());
        if (!checkIfFloorNodesBlockSightToTarget(enemy, nodes)) {
            boolean targetIsClose = isTargetCloseEnough(enemy);
            if (targetIsClose) {
                eventDispatcher.dispatchMessage(SystemEvent.ENEMY_SPOTTED_PLAYER.ordinal(), enemy);
            }
        }
    }

    private boolean isTargetCloseEnough(Entity enemy) {
        Vector2 enemyPos = ComponentsMapper.characterDecal.get(enemy).getNodePosition(auxVector2_1);
        Entity target = sessionData.player();
        Vector2 targetPos = ComponentsMapper.characterDecal.get(target).getNodePosition(auxVector2_2);
        ComponentsMapper.enemy.get(enemy).setTargetLastVisibleNode(sessionData.mapGraph().getNode((int) targetPos.x, (int) targetPos.y));
        int maxDistance = 4;
        return enemyPos.dst2(targetPos) <= Math.pow(maxDistance, 2);
    }

    private boolean checkIfFloorNodesBlockSightToTarget(Entity enemy, LinkedHashSet<GridPoint2> nodes) {
        Vector2 enemyPosition = ComponentsMapper.characterDecal.get(enemy).getNodePosition(auxVector2_1);
        for (GridPoint2 n : nodes) {
            MapGraph map = sessionData.mapGraph();
            MapGraphNode node = map.getNode(n.x, n.y);
            if (checkIfFloorNodeBlockSightToTarget(enemyPosition, map, node)) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkIfFloorNodeBlockSightToTarget(Vector2 enemyPosition,
                                                              MapGraph map,
                                                              MapGraphNode node) {
        return node.getHeight() > map.getNode((int) enemyPosition.x, (int) enemyPosition.y).getHeight() + 1;
    }


    @Override
    public void dispose( ) {

    }
}
