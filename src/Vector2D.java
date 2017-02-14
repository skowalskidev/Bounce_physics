/**
 *  ========================================================
 *  Vector2D.java: Source code for two-dimensional vectors
 *
 *  Written by: Mark Austin                   November, 2005
 *  Extended by: Szymon Kowalski              February, 2017
 *  ========================================================
 */

import java.lang.Math;

public class Vector2D {
    private double dX;
    private double dY;

    public double getdX() {
        return dX;
    }

    public void setdX(double dX) {
        this.dX = dX;
    }

    public double getdY() {
        return dY;
    }

    public void setdY(double dY) {
        this.dY = dY;
    }

    public void set(double dX, double dY){
        setdX(dX);
        setdY(dY);
    }

    // Constructor methods ....

    public Vector2D() {
        dX = dY = 0.0;
    }

    public Vector2D(double dX, double dY) {
        this.dX = dX;
        this.dY = dY;
    }

    public void Vector2DFromAngle(double angle, double magnitude) {
        this.dX = -Math.cos(angle) * magnitude;
        this.dY = -Math.sin(angle)* magnitude;
    }

    // Convert vector to a string ...

    public String toString() {
        return "Vector2D(" + dX + ", " + dY + ")";
    }

    // Compute magnitude of vector ....

    public double magnitude() {
        return Math.sqrt(dX * dX + dY * dY);
    }

    // Compute unit vector

    public Vector2D unit() {
        return scale(1/ magnitude());
    }

    // Sum of two vectors ....

    public Vector2D add(Vector2D v1) {
        Vector2D v2 = new Vector2D(this.dX + v1.dX, this.dY + v1.dY);
        return v2;
    }

    // Subtract vector v1 from v .....

    public Vector2D sub(Vector2D v1) {
        Vector2D v2 = new Vector2D(this.dX - v1.dX, this.dY - v1.dY);
        return v2;
    }

    // Scale vector by a constant ...

    public Vector2D scale(double scaleFactor) {
        Vector2D v2 = new Vector2D(this.dX * scaleFactor, this.dY * scaleFactor);
        return v2;
    }

    // Normalize a vectors magnitude....

    public Vector2D normalize() {
        Vector2D v2 = new Vector2D();

        double length = Math.sqrt(this.dX * this.dX + this.dY * this.dY);
        if (length != 0) {
            v2.dX = this.dX / length;
            v2.dY = this.dY / length;
        }

        return v2;
    }

    // Dot product of two vectors .....

    public double dotProduct(Vector2D v1) {
        return this.dX * v1.dX + this.dY * v1.dY;
    }

    // Perpendicular vector

    public Vector2D perpendicular() {
        return new Vector2D(dY, -dX);
    }

    // Angle of this vector

    public double angle() {
        return Math.PI/2 - Math.atan2(dX,dY);
    }

    // Angle between vectors

    public double angle(Vector2D base) {
        return angle() - base.angle();
    }

    // Reverse/Flip

    public void flip() {
        dX = -dX;
        dY = -dY;
    }
}