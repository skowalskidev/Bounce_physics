import java.util.ArrayList;

/**
 * Created by Szymon on 12/02/2017.
 */
public class Collisions {
    private ArrayList<Line> collidedLines = new ArrayList<>(0);
    private ArrayList<Boolean> collisionType = new ArrayList<>(0);//Corner or surface
    private ArrayList<Integer> collisionLinesDuplicateCount = new ArrayList<>(0);//2 states: true the same collision registered, false brand new collision with the corresponding line
    private ArrayList<Boolean> collisionUpdated = new ArrayList<>(0);//2 states: true updated, false not updated i.e. collision expired
    private Ball ball;
    private Renderer parent;
    private double D = 0.9;


    public Collisions(Ball ball, Renderer parent) {
        this.ball = ball;
        this.parent = parent;
    }

    /**
     * @param line
     * @param type True:Surface Collision, False:Corner
     */
    public void add(Line line, Boolean type) {
        int duplicateIndex = indexOf(line);
        if (!type) return;//DEBUG:Turned off corner collisions
        if (duplicateIndex > -1) {//This collision has already been registered
            collisionLinesDuplicateCount.set(duplicateIndex, collisionLinesDuplicateCount.get(duplicateIndex) + 1);
            collisionUpdated.set(duplicateIndex, true);
            return;
        }
        collisionLinesDuplicateCount.add(0);
        collisionUpdated.add(true);
        collidedLines.add(line);
        collisionType.add(type);
    }

    public void remove(int i) {
        collisionType.remove(i);
        collidedLines.remove(i);
        collisionLinesDuplicateCount.remove(i);
        collisionUpdated.remove(i);
    }

    public int indexOf(Line line) {
        return collidedLines.indexOf(line);
    }

    public void update() {
        double angleSum = 0;
        int averageCollsionCount = 0;
        double finalMagnitude;
        Vector2D currentVelocity = null;
        Line currentLine = null;
        for (int i = 0; i < collidedLines.size(); i++) {
            if (!collisionUpdated.get(i)) {//Expired collision remove it
                /*remove(i);
                i--;
                continue;*/
            }
            collisionUpdated.set(i, false);//Reset all updated collisions
            if (collisionLinesDuplicateCount.get(i) > 0) {
                //continue;
            }

            currentLine = collidedLines.get(i);
            if (collisionType.get(i)) {
                currentVelocity = surfaceCollision(ball, currentLine);
            } else {
                currentVelocity = cornerCollision(ball, currentLine);
            }

            angleSum += currentVelocity.angle();
            averageCollsionCount++;
            break;//Don't need multi collisions
        }
        for (int i = 0; i < collidedLines.size(); i++) {
            collisionUpdated.set(i, false);//Reset all updated collisions
            remove(i);//Dont need to store collisions anymore
        }

        if(averageCollsionCount < 1)return;

        finalMagnitude = currentVelocity.magnitude();//Last line magnitude (magnitudes of all lines should be the same)
        Vector2D finalVelocity = new Vector2D();
        finalVelocity.Vector2DFromAngle(angleSum / averageCollsionCount, finalMagnitude);

        ball.setVelocity(finalVelocity.getdX() * D, finalVelocity.getdY() * D);
    }

    private Vector2D cornerCollision(Ball ball, Line line) {
        repositionBallCorner(line);
        return new Vector2D(-ball.getVelocity().getdX(), -ball.getVelocity().getdY());
    }

    private Vector2D surfaceCollision(Ball ball, Line line) {
        Vector2D lineVector = new Vector2D(line.getX2() - line.getX1(), line.getY2() - line.getY1());
        Vector2D velocityVector = new Vector2D(ball.getVelocity().getdX(), ball.getVelocity().getdY());

        //Angle of collision and deflection
        double incidentAngle = velocityVector.angle();
        double lineAngle = lineVector.angle();
        double anglediff = lineAngle - incidentAngle;

        Vector2D finalVelocity = new Vector2D();
        finalVelocity.Vector2DFromAngle(lineAngle + anglediff, velocityVector.magnitude());

        repositionBall(line);

        return finalVelocity;
    }

    public void repositionBall(Line line) {
        Vector2D lineVector = new Vector2D(line.getX2() - line.getX1(), line.getY2() - line.getY1());
        double lineAngle = lineVector.angle();

        Vector2D lineBallVector = new Vector2D(ball.getX() - line.getX1(), ball.getY() - line.getY1());
        double anglediff = lineBallVector.angle() - lineAngle;

        double lineParallelPointMagnitude = Math.cos(anglediff) * lineBallVector.magnitude();
        Vector2D lineParallelPointVector = new Vector2D();
        lineParallelPointVector.Vector2DFromAngle(lineAngle, -lineParallelPointMagnitude);

        parent.linePoints.add(line.getX1() + (int) lineParallelPointVector.getdX());
        parent.linePoints.add(line.getY1() + (int) lineParallelPointVector.getdY());

        double linePerpAngle = Math.abs(lineVector.angle() - Math.PI / 2);
        Vector2D bumpVector = new Vector2D();
        bumpVector.Vector2DFromAngle(linePerpAngle, ball.getRadius() + 2);
        bumpVector.setdX(-bumpVector.getdX());

        Vector2D finalPosVector = lineParallelPointVector.add(bumpVector);//Relative to line x1 & y1


        //Pure angle checking to make sure the bumping occurs in the opposite direction of the initial velocity i.e. back up the ball onto the line surface
        anglediff = finalPosVector.angle() - lineAngle;
        Vector2D originalVelocityLineVector = new Vector2D(lineVector.getdX() + ball.getVelocity().getdX(), lineVector.getdY() + ball.getVelocity().getdY());
        double angleDiff2 = originalVelocityLineVector.angle() - lineAngle;

        if (anglediff < 0 && angleDiff2 < 0 || anglediff > 0 && angleDiff2 > 0) {
            bumpVector.flip();
            finalPosVector = lineParallelPointVector.add(bumpVector);//Relative to line x1 & y1
        }

        ball.setX(line.getX1() + (int) finalPosVector.getdX());
        ball.setY(line.getY1() + (int) finalPosVector.getdY());

        parent.repositionedBalls.add(line.getX1() + (int) finalPosVector.getdX());
        parent.repositionedBalls.add(line.getY1() + (int) finalPosVector.getdY());
    }

    public void repositionBallCorner(Line line) {
        Vector2D bumpVector = new Vector2D();
        bumpVector.Vector2DFromAngle(ball.getVelocity().angle(), ball.getRadius());

        if (Math.abs(ball.getX() - line.getX1()) + Math.abs(ball.getY() - line.getY1()) < Math.abs(ball.getX() - line.getX2()) + Math.abs(ball.getY() - line.getY2())) {
            ball.setX(line.getX1() + (int) bumpVector.getdX());
            ball.setY(line.getY1() + (int) -bumpVector.getdY());
            System.out.println("Corner " + line.getX1() + " " + line.getY1());
            parent.linePoints.add(line.getX1());
            parent.linePoints.add(line.getY1());

            parent.repositionedBalls.add(line.getX1() + (int) bumpVector.getdX());
            parent.repositionedBalls.add(line.getY1() + (int) bumpVector.getdY());
        } else {
            ball.setX(line.getX2() + (int) bumpVector.getdX());
            ball.setY(line.getY2() + (int) -bumpVector.getdY());
            System.out.println("Corner " + line.getX2() + " " + line.getY2());
            parent.linePoints.add(line.getX2());
            parent.linePoints.add(line.getY2());

            parent.repositionedBalls.add(line.getX2() + (int) bumpVector.getdX());
            parent.repositionedBalls.add(line.getY2() + (int) bumpVector.getdY());
        }

    }
}
