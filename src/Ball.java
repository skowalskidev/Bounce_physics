/**
 * Created by Szymon on 04/02/2017.
 */
public class Ball {
    private int x;
    private int y;
    private int radius;
    private int diameter;
    private double velocityX;
    private double velocityY;
    private Line lastCollidedWith;


    public Ball(int x, int y, int radius, double velocityX, double velocityY){
        setX(x);
        setY(y);
        setRadius(radius);
        setVelocity(velocityX, velocityY);
    }

    public void update() {
        setX(x += velocityX);
        setY(y += velocityY);
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

    public double getVelocityX() {
        return velocityX;
    }

    public void setVelocity(double velocityX, double velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public double getVelocityY() {
        return velocityY;
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

    public Line getLastCollidedWith() {
        return lastCollidedWith;
    }

    public void setLastCollidedWith(Line lastCollidedWith) {
        this.lastCollidedWith = lastCollidedWith;
    }
}
