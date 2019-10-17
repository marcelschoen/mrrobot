package ch.marcelschoen.mrrobot;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class MrRobot extends Sprite {
    public World world;
    public Body b2body;

    public MrRobot(World world) {
        this.world = world;
        defineMrRobot();
    }

    public void defineMrRobot() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32,32);
        bdef.type = BodyDef.BodyType.DynamicBody;

        b2body = world.createBody(bdef);
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5);

        fdef.shape = shape;
        b2body.createFixture(fdef);
    }
}
