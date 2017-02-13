import java.util.ArrayList;

/**
 * Created by Szymon on 12/02/2017.
 */
public class Collisions {
    private ArrayList<Line> collidedLines = new ArrayList<>();
    private ArrayList<Boolean> collisionType = new ArrayList<>();//Corner or surface
    private ArrayList<Integer> collisionLinesDuplicateCount = new ArrayList<>();//2 states: true the same collision registered, false brand new collision with the corresponding line
    private ArrayList<Boolean> collisionUpdated = new ArrayList<>();//2 states: true updated, false not updated i.e. collision expired
    private Ball ball;
    private double D = 0.9;


    public Collisions(Ball ball){
        this.ball = ball;
    }
    /**
     *
     * @param line
     * @param type True:Surface Collision, False:Corner
     */
    public void add(Line line, Boolean type){
        int duplicateIndex = indexOf(line);
        if(duplicateIndex > -1){//This collision has already been registered
            collisionLinesDuplicateCount.set(duplicateIndex, collisionLinesDuplicateCount.get(duplicateIndex) + 1);
            collisionUpdated.set(duplicateIndex, true);
            return;
        }
        else {
            collisionLinesDuplicateCount.add(0);
            collisionUpdated.add(true);
        }
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

    public void update(){
        double angleSum = 0;
        int averageCollsionCount = 0;
        double finalMagnitude;
        Vector2D currentVelocity = null;
        Line currentLine = null;
        for (int i = 0; i < collidedLines.size(); i++){
            if(!collisionUpdated.get(i)) {//Expired collision remove it
                remove(i);
                i--;
                continue;
            }
            collisionUpdated.set(i, false);//Reset all updated collisions
            System.out.println(collisionLinesDuplicateCount.get(i) % 4);
            if(collisionLinesDuplicateCount.get(i) % 2 != 0) {
                continue;
            }

            currentLine = collidedLines.get(i);
            if(collisionType.get(i)){
                currentVelocity = surfaceCollision(ball, currentLine);
            }
            else {
                currentVelocity = cornerCollision(ball, currentLine);
            }

            angleSum += currentVelocity.angle();
            averageCollsionCount++;
        }

        if(currentLine == null){
            return;
        }

        finalMagnitude = currentVelocity.magnitude();//Last line magnitude (magnitudes of all lines should be the same)
        Vector2D finalVelocity = new Vector2D();
        finalVelocity.Vector2DFromAngle(angleSum / averageCollsionCount, finalMagnitude);

        ball.setVelocity(finalVelocity.getdX() * D, finalVelocity.getdY() * D);
    }

    private Vector2D cornerCollision(Ball ball, Line line) {
        return new Vector2D(-ball.getVelocity().getdX(), -ball.getVelocity().getdY());
    }

    private Vector2D surfaceCollision(Ball ball, Line line){
        Vector2D lineVector = new Vector2D(line.getX2() - line.getX1(), line.getY2() - line.getY1());
        Vector2D velocityVector = new Vector2D(ball.getVelocity().getdX(), ball.getVelocity().getdY());

        //Angle of collision and deflection
        double incidentAngle = velocityVector.angle();
        double lineAngle = lineVector.angle();
        double anglediff = lineAngle - incidentAngle;

        Vector2D finalVelocity = new Vector2D();
        finalVelocity.Vector2DFromAngle(lineAngle + anglediff, velocityVector.magnitude());

        return finalVelocity;
    }

}
