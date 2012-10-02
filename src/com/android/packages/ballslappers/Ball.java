package com.android.packages.ballslappers;

public class Ball {
	public float x_pos, y_pos, x_vel, y_vel;
	public float trajectory_angle;
	public float speed;
		
	public Ball() {
		// fake this data until figure out how to retrieve it
		int BOARD_WIDTH = 50;
		int BOARD_HEIGHT = 80;
		x_pos = (float) BOARD_WIDTH / 2;
		y_pos = (float) BOARD_HEIGHT / 2;
		trajectory_angle = getRandomAngle();
	}
	
	public Ball(Ball b) {
		x_pos = b.x_pos;
		y_pos = b.y_pos;
		x_vel = b.x_vel;
		y_vel = b.y_vel;
		trajectory_angle = b.trajectory_angle;
		speed = b.speed;
	}
	
	private float getRandomAngle() {
		return 
	}
	
}