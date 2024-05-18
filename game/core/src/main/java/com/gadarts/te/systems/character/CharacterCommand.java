package com.gadarts.te.systems.character;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.gadarts.te.systems.map.MapGraphPath;
import com.gadarts.te.systems.map.graph.MapGraphNode;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CharacterCommand implements Pool.Poolable {
    private final CharacterCommandState state = new CharacterCommandState();
    private CharacterCommandDefinition characterCommandDefinition;
    private MapGraphNode destination;
    @Setter
    private MapGraphPath path;
    private Entity initiator;

    @Setter
    private boolean stopWhenPossible;

    @Override
    public void reset( ) {

    }

    public void init(CharacterCommandDefinition characterCommandDefinition, MapGraphNode destination, Entity initiator) {
        this.characterCommandDefinition = characterCommandDefinition;
        this.destination = destination;
        this.state.setStatus(CharacterCommandStatus.READY);
        this.initiator = initiator;
    }
}
