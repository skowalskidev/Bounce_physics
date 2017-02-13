/**
 * Created by Szymon on 04/02/2017.
 */
public class Ball {
    private int x;
    private int y;
    private int radius;
    private int diameter;
    private Vector2D velocity;


    public Ball(int x, int y, int radius, double velocityX, double velocityY) {
        setX(x);
        setY(y);
        setRadius(radius);
        velocity = new Vector2D(velocityX, velocityY);
    }

    public void update() {
        setX(x += velocity.getdX());
        setY(y += velocity.getdY());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocityX, double velocityY) {
        velocity.set(velocityX, velocityY);
    }

    public void applyGravity(double G) {
        double newVelocityY = getVelocity().getdY() + G;
        getVelocity().setdY(newVelocityY);
    }

    public void applyDampening(double D) {
        setVelocity(velocity.getdX() * D, velocity.getdY() * D);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        diameter = radius * 2;
    }

    public int getDiameter() {
        return diameter;
    }
}
