package com.gadarts.te.systems.character;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.gadarts.te.systems.map.MapGraphPath;
import com.gadarts.te.systems.map.graph.MapGraphNode;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CharacterCommand implements Pool.Poolable {
    private MapGraphNode destination;
    private CharacterCommandDefinition characterCommandDefinition;
    @Setter
    private CharacterCommandState state;
    private Entity initiator;
    @Setter
    private MapGraphPath path;

    @Override
    public void reset( ) {

    }

    public void init(CharacterCommandDefinition characterCommandDefinition, MapGraphNode destination, Entity initiator) {
        this.characterCommandDefinition = characterCommandDefinition;
        this.destination = destination;
        this.state = CharacterCommandState.READY;
        this.initiator = initiator;
    }
}
