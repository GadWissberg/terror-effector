package com.gadarts.te.components;

import com.gadarts.te.common.assets.definitions.character.enemy.EnemyDefinition;
import com.gadarts.te.systems.map.graph.MapGraphNode;
import lombok.Getter;
import lombok.Setter;

@Getter
public class EnemyComponent implements GameComponent {

    @Setter
    private MapGraphNode targetLastVisibleNode;
    private EnemyDefinition definition;

    @Override
    public void reset( ) {

    }

    public void init(EnemyDefinition enemyDefinition) {
        this.definition = enemyDefinition;
    }
}
