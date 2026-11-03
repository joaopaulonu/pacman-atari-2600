/*package com.pacmanjava.map;

import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.KeepOnScreenComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class PacManEntityFactory implements EntityFactory {

    @Spawns("1")
    public Entity newRectangle(SpawnData data) {
        return entityBuilder(data)
                .view(new Rectangle(8, 8))
                .bbox(new HitBox(BoundingShape.box(8, 8)))
                .with(new PhysicsComponent())
                .build();
    }

    @Spawns("0")
    public Entity newEmpty(SpawnData data) {
        return entityBuilder(data)
                .view(new Rectangle(8, 8, Color.RED))
                .build();
    }

    @Spawns("3")
    public Entity newDoor(SpawnData data) {
        return entityBuilder(data)
                .view(new Rectangle(8, 8, Color.WHITE))
                .build();
    }
}*/