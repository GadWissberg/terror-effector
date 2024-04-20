package com.gadarts.te.systems.character;

import com.gadarts.te.systems.map.graph.MapGraphNode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CharacterCommandState {

    private MapGraphNode prevNode;

    private CharacterCommandStatus status;
    private int nextNodeIndex = -1;

}
