/* NOTES: 
 *  1. this class may likely share functionality with
 *     powerups, we can deal with that later
 *  2. need to determine the exact layouts of the maps
 *     in order to determine trajectories after collisions,
 *     don't know of any other way to do it
 */

import java.util.Random;

public class Ball {
    // fake these constants until later
    public static final int MAP_WIDTH = 50;
    public static final int MAP_HEIGHT = 80;
    public static final float MIN_SPEED = (float) 0.5;
    public static final float MAX_SPEED = 10;

    // random number generator used for generating angles
    private static Random randomGenerator = new Random();

    /* ------------------
     * BALL PROPERTIES 
     * ------------------*/
    public float x_pos, y_pos;
    public float speed;
    // current velocity vector <x,y> components
    public float vel_x, vel_y;
    // angle measured counter-clockwise from the positive x-axis
    public float trajectory_angle;
    public int radius;

    /* ------------------
     * CONSTRUCTORS
     * ------------------*/
    public Ball() {
        // initialize the ball in the middle of the map
        x_pos = (float) MAP_WIDTH / 2;
        y_pos = (float) MAP_HEIGHT / 2;
        trajectory_angle = getRandomAngle();
    }

    public Ball(Ball b) {
        x_pos = b.x_pos;
        y_pos = b.y_pos;
        vel_x = b.vel_x;
        vel_y = b.vel_y;
        trajectory_angle = b.trajectory_angle;
        speed = b.speed;
    }

    /* ------------------
     * METHODS
     * ------------------*/
    public void setPosition(float x, float y) {
        // TODO ... 
        /* need to determine how to check if the position is valid
         * depending on which map size we are dealing with... */
    }

    public void setSpeed(float speed) {
        this.speed = confine(speed, MIN_SPEED, MAX_SPEED);
        updateVelocity();
    }

    public void setTrajectoryAngle(float angle) {
        trajectory_angle = normalizeAngle(angle); 
        updateVelocity();
    }

    public void updateVelocity() {
        vel_x = speed * (float) Math.cos(trajectory_angle);
        vel_y = speed * (float) Math.sin(trajectory_angle);
    }

    public void move() {
        x_pos += vel_x;
        y_pos += vel_y;
    }

    public void hitWall() {
        // TODO ...
        /* need to determine the angles of the walls depending on the map size
         * we are dealing with in order to determine the correct trajectory 
         *
         * speed will remain constant 
         * ball will bounce with angle of incidence the same as the angle of reflection */
    }

    public void hitPaddle() {
        // TODO ... 
        /* need to determine the angles of the paddles depending on the map size
         * size we are dealing with in order to determine the correct trajectory 
         *
         * speed will be dependent upon point where ball hits paddle, faster 
         * towards the edges
         * ball will bounce with no regard to the angle of incidence, solely based
         * on where the ball hits paddle, but we still need to know the angle of the 
         * paddle to determine the angle of the velocity vector */
    }

    public float getRandomAngle() {
        /* generates a pseudorandom angle between ~ 0 and 2pi
         * might need to change this depending on the number of
         * players to give a more direct path to a paddle */
        return (float) (2 * Math.PI) * randomGenerator.nextFloat();
    }

    public float normalizeAngle(float angle) {
        /* normalizes the angle to be between 0 and 2*PI */
        float normalizedAngle = angle;
        while (normalizedAngle < 0) {
            normalizedAngle += 2 * (float) Math.PI;
        }
        while (normalizedAngle > 2 * (float) Math.PI) {
            normalizedAngle -= 2 * (float) Math.PI;
        }
        return normalizedAngle;
    }

    public float confine(float number, float left, float right) {
        /* confines the number between left and right, returns
         * left if number < left, right if number > right */
        return Math.min(Math.max(left, number), right); 
    }

    // debug output
    public String toString() {
        return "x_pos:\t" + x_pos + "\n" +
               "y_pos:\t" + y_pos + "\n" +
               "vel_x:\t" + vel_x + "\n" +
               "vel_y:\t" + vel_y + "\n" +
               "speed:\t" + speed + "\n" +
               "angle:\t" + trajectory_angle + "\n";
    }
}
