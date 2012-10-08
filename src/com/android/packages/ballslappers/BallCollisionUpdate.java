package com.android.packages.ballslappers;

import android.util.Log;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class BallCollisionUpdate implements ContactListener {

	public void beginContact(Contact contact) {
		Body bodyA = contact.getFixtureA().getBody();
		Body bodyB = contact.getFixtureB().getBody();
		Object userAData = bodyA.getUserData();
		Object userBData = bodyB.getUserData();
		if(userAData.equals("ballBody") && userBData.equals("randomLineBody")
				|| userAData.equals("randomLineBody") && userBData.equals("ballBody")) {
			Log.v("Contact Made", "Ball contacted the line");
		}
	}

	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
