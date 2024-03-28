package com.gadarts.te.systems.render;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.gadarts.te.common.assets.GameAssetsManager;
import com.gadarts.te.common.definitions.character.SpriteType;
import com.gadarts.te.common.map.element.Direction;
import com.gadarts.te.common.utils.CharacterUtils;
import com.gadarts.te.common.utils.LightUtils;
import com.gadarts.te.components.AnimationComponent;
import com.gadarts.te.components.ComponentsMapper;
import com.gadarts.te.components.ModelInstanceComponent;
import com.gadarts.te.components.cd.CharacterAnimation;
import com.gadarts.te.components.cd.CharacterAnimations;
import com.gadarts.te.components.cd.CharacterDecalComponent;
import com.gadarts.te.components.character.CharacterComponent;
import com.gadarts.te.components.character.CharacterSpriteData;
import com.gadarts.te.systems.GameSystem;
import com.gadarts.te.systems.SystemEvent;
import com.gadarts.te.systems.data.SharedData;
import com.gadarts.te.systems.data.SharedDataBuilder;

import static com.gadarts.te.DebugSettings.HIDE_CHARACTERS;
import static com.gadarts.te.common.definitions.character.SpriteType.IDLE;
import static com.gadarts.te.common.definitions.character.SpriteType.RUN;

public class RenderSystem extends GameSystem {
    private static final int DECALS_POOL_SIZE = 200;
    private static final Vector3 auxVector = new Vector3();
    private final DecalsGroupStrategies strategies = new DecalsGroupStrategies();
    private ImmutableArray<Entity> characterDecalsEntities;
    private AxisModelHandler axisModelHandler;
    private ImmutableArray<Entity> modelEntities;
    private ModelBatch modelBatch;
    private Environment environment;
    private DecalBatch decalBatch;

    private static CharacterAnimation fetchCharacterAnimationByDirectionAndType(Entity entity,
                                                                                Direction direction,
                                                                                SpriteType sprType) {
        int randomIndex = MathUtils.random(sprType.getVariations() - 1);
        CharacterAnimation animation = null;
        CharacterAnimations animations = ComponentsMapper.characterDecal.get(entity).getAnimations();
        if (animations.contains(sprType)) {
            animation = animations.get(sprType, randomIndex, direction);
        } else if (ComponentsMapper.player.has(entity)) {
            animation = ComponentsMapper.player.get(entity).getGeneralAnimations().get(sprType, randomIndex, direction);
        }
        return animation;
    }

    private static Direction forceDirectionForAnimationInitialization(Direction direction,
                                                                      SpriteType spriteType,
                                                                      AnimationComponent animationComponent) {
        if (spriteType.isSingleDirection()) {
            Animation<TextureAtlas.AtlasRegion> animation = animationComponent.getAnimation();
            if (animation == null || !animation.isAnimationFinished(animationComponent.getStateTime())) {
                direction = Direction.SOUTH;
            }
        }
        return direction;
    }

    @Override
    public void initialize(SharedDataBuilder sharedDataBuilder, GameAssetsManager assetsManager, MessageDispatcher eventDispatcher) {
        super.initialize(sharedDataBuilder, assetsManager, eventDispatcher);
        characterDecalsEntities = getEngine().getEntitiesFor(Family.all(CharacterDecalComponent.class).get());
        axisModelHandler = new AxisModelHandler();
        Engine engine = getEngine();
        axisModelHandler.addAxis(engine);
        modelEntities = engine.getEntitiesFor(Family.all(ModelInstanceComponent.class).get());
        modelBatch = new ModelBatch();
        environment = LightUtils.createEnvironment();
    }

    @Override
    public void onSystemReady(SharedData sharedData) {
        super.onSystemReady(sharedData);
        strategies.createDecalGroupStrategies(sharedData.getCamera(), assetsManager);
        this.decalBatch = new DecalBatch(DECALS_POOL_SIZE, strategies.getRegularDecalGroupStrategy());
    }

    @Override
    public void update(float deltaTime) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ScreenUtils.clear(Color.BLACK, true);
        modelBatch.begin(sharedData.getCamera());
        for (Entity entity : modelEntities) {
            ModelInstanceComponent modelInstanceComponent = ComponentsMapper.modelInstance.get(entity);
            ModelInstance modelInstance = modelInstanceComponent.getModelInstance();
            if (modelInstanceComponent.isApplyEnvironment()) {
                modelBatch.render(modelInstance, environment);
            } else {
                modelBatch.render(modelInstance);
            }
        }
        modelBatch.end();
        renderDecals();
    }

    private void renderDecals( ) {
        Gdx.gl.glDepthMask(false);
        decalBatch.setGroupStrategy(strategies.getRegularDecalGroupStrategy());
        renderCharacters();
        decalBatch.flush();
        Gdx.gl.glDepthMask(true);
    }

    private void renderCharacters( ) {
        for (Entity entity : characterDecalsEntities) {
            updateCharacterDecal(entity);
            renderCharacterDecal(entity);
        }
    }

    private void renderCharacterDecal(Entity entity) {
        if (HIDE_CHARACTERS) return;

        Decal decal = ComponentsMapper.characterDecal.get(entity).getDecal();
        Vector3 decalPosition = decal.getPosition();
        OrthographicCamera camera = sharedData.getCamera();
        decal.lookAt(auxVector.set(decalPosition).sub(camera.direction), camera.up);
        decalBatch.add(decal);
    }

    private void updateCharacterDecal(Entity entity) {
        CharacterComponent characterComp = ComponentsMapper.character.get(entity);
        CharacterSpriteData charSpriteData = characterComp.getCharacterSpriteData();
        Direction direction = CharacterUtils.calculateDirectionSeenFromCamera(sharedData.getCamera(), characterComp.getFacingDirection());
        SpriteType spriteType = charSpriteData.getSpriteType();
        boolean sameSpriteType = spriteType.equals(ComponentsMapper.characterDecal.get(entity).getSpriteType());
        Direction characterFacingDirection = ComponentsMapper.characterDecal.get(entity).getDirection();
        if ((!sameSpriteType || (!spriteType.isSingleDirection() && !characterFacingDirection.equals(direction)))) {
            updateCharacterDecalSprite(entity, direction, spriteType, sameSpriteType);
        } else if (spriteType != RUN) {
            updateCharacterDecalFrame(entity, characterComp, spriteType);
        }
    }

    private void updateCharacterDecalFrame(Entity entity,
                                           CharacterComponent characterComponent,
                                           SpriteType spriteType) {
        CharacterDecalComponent characterDecalComponent = ComponentsMapper.characterDecal.get(entity);
        AnimationComponent animationComponent = ComponentsMapper.animation.get(entity);
        Animation<TextureAtlas.AtlasRegion> anim = animationComponent.getAnimation();
        if (ComponentsMapper.animation.has(entity) && anim != null) {
            if (spriteType == IDLE && anim.isAnimationFinished(animationComponent.getStateTime())) {
                if (anim.getPlayMode() == Animation.PlayMode.NORMAL) {
                    anim.setPlayMode(Animation.PlayMode.REVERSED);
                } else {
                    anim.setPlayMode(Animation.PlayMode.NORMAL);
                    Direction direction = CharacterUtils.calculateDirectionSeenFromCamera(
                        sharedData.getCamera(),
                        characterComponent.getFacingDirection());
                    CharacterAnimation animation = fetchCharacterAnimationByDirectionAndType(
                        entity,
                        direction,
                        spriteType);
                    animationComponent.init(0, animation);
                }
                animationComponent.resetStateTime();
            }
            TextureAtlas.AtlasRegion currentFrame = (TextureAtlas.AtlasRegion) characterDecalComponent.getDecal().getTextureRegion();
            TextureAtlas.AtlasRegion newFrame = animationComponent.calculateFrame();
            if (characterDecalComponent.getSpriteType() == spriteType && currentFrame != newFrame) {
                Decal decal = characterDecalComponent.getDecal();
                decal.setTextureRegion(newFrame);
            }
        }
    }


    private void updateCharacterDecalSprite(Entity entity,
                                            Direction direction,
                                            SpriteType spriteType,
                                            boolean sameSpriteType) {
        CharacterDecalComponent characterDecalComponent = ComponentsMapper.characterDecal.get(entity);
        SpriteType prevSprite = characterDecalComponent.getSpriteType();
        characterDecalComponent.initializeSprite(spriteType, direction);
        if (ComponentsMapper.animation.has(entity)) {
            initializeCharacterAnimationBySpriteType(entity, direction, spriteType, sameSpriteType);
        }
        if (prevSprite != characterDecalComponent.getSpriteType()) {
            eventDispatcher.dispatchMessage(SystemEvent.SPRITE_TYPE_CHANGED.ordinal(), entity);
        }
    }

    private void initializeCharacterAnimationBySpriteType(Entity entity,
                                                          Direction direction,
                                                          SpriteType spriteType,
                                                          boolean sameSpriteType) {
        AnimationComponent animationComponent = ComponentsMapper.animation.get(entity);
        direction = forceDirectionForAnimationInitialization(direction, spriteType, animationComponent);
        Animation<TextureAtlas.AtlasRegion> oldAnimation = ComponentsMapper.animation.get(entity).getAnimation();
        CharacterAnimation newAnimation = fetchCharacterAnimationByDirectionAndType(entity, direction, spriteType);
        if (newAnimation != null) {
            boolean isIdle = spriteType == IDLE;
            animationComponent.init(isIdle ? 0 : spriteType.getFrameDuration(), newAnimation);
            boolean differentAnimation = oldAnimation != newAnimation;
            if (!sameSpriteType || isIdle) {
                if (spriteType.getPlayMode() != Animation.PlayMode.LOOP) {
                    newAnimation.setPlayMode(Animation.PlayMode.NORMAL);
                }
                animationComponent.resetStateTime();
            } else if (differentAnimation) {
                newAnimation.setPlayMode(oldAnimation.getPlayMode());
            }
            if (differentAnimation) {
                eventDispatcher.dispatchMessage(SystemEvent.ANIMATION_CHANGED.ordinal(), entity);
            }
        }
    }

    @Override
    public void dispose( ) {
        axisModelHandler.dispose();
        modelBatch.dispose();
    }
}
