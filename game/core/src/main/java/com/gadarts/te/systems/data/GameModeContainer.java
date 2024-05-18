package com.gadarts.te.systems.data;

import com.gadarts.te.systems.turns.GameMode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameModeContainer {
    private GameMode mode = GameMode.EXPLORE;
}
