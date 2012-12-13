package com.cen3031.android.packages.ballslappers;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Obstacle extends Rectangle {
	private Body body;
	
	public Obstacle(float posX, float posY, int width, int height, FixtureDef fd, VertexBufferObjectManager vb) {
		super(posX, posY, width, height, vb);
		body = PhysicsFactory.createBoxBody(MainActivity.getmPhysicsWorld(), this, BodyType.StaticBody, fd);
		body.setUserData("obstacle");
	}
	
	public Body getBody() {
		return body;
	}
}
