import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;

public class Renderer extends JFrame {
    private int DELAY = 30;
    private ArrayList<Line> lines = new ArrayList<>();
    private Ball ball = new Ball(150, 100, 10, 0, 5);

    public Renderer() {
        Line line1 = new Line(100, 100, 200, 800);
        lines.add(line1);
    }

    public void paint(Graphics graphics) {
        super.paint(graphics);
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setStroke(new BasicStroke(2));

        //ball.setX((int) MouseInfo.getPointerInfo().getLocation().getX());
        //ball.setY((int) MouseInfo.getPointerInfo().getLocation().getY());
        g2.draw(new Ellipse2D.Double(ball.getX() - ball.getRadius(), ball.getY() - ball.getRadius(), ball.getDiameter(), ball.getDiameter()));
        for (int i = 0; i < lines.size(); i++) {
            Line line = lines.get(i);
            Vector2D lineVector = new Vector2D(line.getX2() - line.getX1(), line.getY2() - line.getY1());
            Vector2D velocityVector = new Vector2D(ball.getVelocityX(), ball.getVelocityY());

            //Angle of collision and deflection
            double incidentAngle = velocityVector.angle();
            double lineAngle = lineVector.angle();
            double anglediff = lineAngle - incidentAngle;//Will never be negative otherwise the ball would not collide

            Vector2D finalVelocity = new Vector2D();
            finalVelocity.Vector2DFromAngle(lineAngle + anglediff, velocityVector.magnitude());

            if (Math.abs(perpDistToLine(ball, lines.get(i))) <= ball.getRadius() && !lines.get(i).equals(ball.getLastCollidedWith())) {
                g2.setColor(Color.red);
                ball.setLastCollidedWith(lines.get(i));
                ball.setVelocity(finalVelocity.dX, finalVelocity.dY);
            } else {
                g2.setColor(Color.black);
            }
            g2.draw(new Line2D.Double(lines.get(i).getX1(), lines.get(i).getY1(), lines.get(i).getX2(), lines.get(i).getY2()));

            //Debug lines
            g2.setStroke(new BasicStroke(1));


            int perpDist = perpDistToLine(ball, lines.get(i));

            g2.setColor(Color.green);
            g2.draw(new Line2D.Double(lines.get(i).getX1(), lines.get(i).getY1(), lines.get(i).getX1() + perpDist, lines.get(i).getY1() + perpDist));

            Vector2D lineVectorPerp = lineVector.perpendicular();

            Vector2D ballVector = new Vector2D(ball.getX() - line.getX1(), ball.getY() - line.getY1());

            g2.draw(new Line2D.Double(line.getX1(), line.getY1(), line.getX1() + lineVectorPerp.dX, line.getY1() + lineVectorPerp.dY));

            g2.draw(new Line2D.Double(line.getX1(), line.getY1(), line.getX1() + ballVector.dX, line.getY1() + ballVector.dY));

            //Collisions
            g2.setColor(Color.orange);

            //Original Velocity
            g2.draw(new Line2D.Double(ball.getX(), ball.getY(), ball.getX() + velocityVector.dX, ball.getY() + velocityVector.dY));

            g2.draw(new Line2D.Double(ball.getX(), ball.getY(), ball.getX() + lineVectorPerp.dX, ball.getY() + lineVectorPerp.dY));

            g2.setColor(Color.red);
            g2.draw(new Line2D.Double(ball.getX(), ball.getY(), ball.getX() + finalVelocity.dX * 10, ball.getY() + finalVelocity.dY * 10));
        }
    }

    public void go() {
        TimerTask timerTask = new TimerTask() {
            public void run() {
                ball.update();
                repaint();
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 0, DELAY);
    }

    private int perpDistToLine(Ball ball, Line line) {
        Vector2D ballVector = new Vector2D(ball.getX() - line.getX1(), ball.getY() - line.getY1());
        Vector2D lineVector = new Vector2D(line.getX2() - line.getX1(), line.getY2() - line.getY1());
        double ballAngle = ballVector.angle();
        double lineAngle = lineVector.angle();
        double angleDiff = lineAngle - ballAngle;
        return (int) (ballVector.magnitude() * Math.sin(angleDiff));
    }

    //Debug
    private double radiansToDegrees(double radians){
        return radians * 180/Math.PI;
    }


    private int collisionNewVelocity() {
        return 0;
    }

    public static void main(String args[]) {
        Renderer animation = new Renderer();
        animation.setSize(700, 700);
        animation.show();
        animation.go();
    }
}
