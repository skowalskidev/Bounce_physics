import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;

public class Renderer extends JFrame {
    private int DELAY = 30;
    private double G = 0.2;

    private Collisions collisions;

    private ArrayList<Line> lines = new ArrayList<>();
    private Ball ball = new Ball(402, 635, 10, 4.4112000000000045, 5.904);

    public ArrayList<Integer>repositionedBalls = new ArrayList<>();
    public ArrayList<Integer>linePoints = new ArrayList<>();

    public Renderer() {
        collisions = new Collisions(ball, this);
        drawLine(50, 50, 650, 50);
        drawLine(650, 50, 650, 650);
        drawLine(650, 650, 50, 650);
        drawLine(50, 650, 50, 50);
        drawLine(100, 650, 150, 600);
        drawLine(150, 600, 200, 600);
        drawLine(200, 600, 250, 650);

        drawLine(250, 650, 300, 600);
        drawLine(300, 600, 350, 600);
        drawLine(350, 600, 400, 650);

        drawLine(400, 650, 450, 600);
        drawLine(450, 600, 500, 600);
        drawLine(500, 600, 550, 650);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        //Always save line x coordinates starting from the left
        if (x1 < x2) {
            lines.add(new Line(x1, y1, x2, y2));
        } else {
            lines.add(new Line(x2, y1, x1, y2));
        }
    }

    public void paint(Graphics graphics) {
        super.paint(graphics);
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setStroke(new BasicStroke(2));

        //ball.setX((int) MouseInfo.getPointerInfo().getLocation().getX());
        //ball.setY((int) MouseInfo.getPointerInfo().getLocation().getY());

        g2.setColor(Color.red);
        for(int s = 0; s< repositionedBalls.size(); s+=2){
           g2.draw(new Ellipse2D.Double(repositionedBalls.get(s) - ball.getRadius(), repositionedBalls.get(s+1) - ball.getRadius(), ball.getDiameter(), ball.getDiameter()));
        }
        g2.setColor(Color.pink);
        for(int s = 0; s< linePoints.size(); s+=2){
            g2.draw(new Ellipse2D.Double(linePoints.get(s) - ball.getRadius()*0.5, linePoints.get(s+1) - ball.getRadius()*0.5, ball.getRadius(), ball.getRadius()));
        }
        g2.setColor(Color.black);
        g2.draw(new Ellipse2D.Double(ball.getX() - ball.getRadius(), ball.getY() - ball.getRadius(), ball.getDiameter(), ball.getDiameter()));
        for (int i = 0; i < lines.size(); i++) {
            Line line = lines.get(i);
            g2.draw(new Line2D.Double(line.getX1(), line.getY1(), line.getX2(), line.getY2()));

            Vector2D lineVector = new Vector2D(line.getX2() - line.getX1(), line.getY2() - line.getY1());
            double lineAngle = lineVector.angle();

            Vector2D ballVector = new Vector2D(ball.getX() - line.getX1(), ball.getY() - line.getY1());
            Vector2D ballVector2 = new Vector2D(ball.getX() - line.getX2(), ball.getY() - line.getY2());

            if (line.getX1() > line.getX2()) {
                Vector2D tempVector = ballVector;
                ballVector = ballVector2;
                ballVector2 = tempVector;
            }

            double ballAngle = ballVector.angle();
            double ballAngleToLinePerpendicular = (lineAngle - Math.PI / 2) - ballAngle;

            double ballAngle2 = ballVector2.angle();
            double ballAngleToLinePerpendicular2 = (lineAngle - Math.PI / 2) - ballAngle2;

            g2.draw(new Line2D.Double(ball.getX(), ball.getY(), ball.getX() + ball.getVelocity().getdX(), ball.getY() + ball.getVelocity().getdY()));

            if (line.getY1() < line.getY2()) {
                //Colliding at the first corner
                if (ballAngleToLinePerpendicular < -Math.PI || ballAngleToLinePerpendicular > 0) {
                    if (getPointCollision(ball, line.getX1(), line.getY1())) {
                        collisions.add(line, false);
                        //break;
                    }
                }
                //Colliding at the second corner
                else if (ballAngleToLinePerpendicular2 < 0 && ballAngleToLinePerpendicular2 > -Math.PI) {
                    if (getPointCollision(ball, line.getX2(), line.getY2())) {
                        collisions.add(line, false);
                        //break;
                    }
                }
                //If ball is between two corners
                else if (Math.abs(perpDistToLine(ball, line)) <= ball.getRadius()) {
                    collisions.add(line, true);
                    //break;
                }
            } else {
                //Colliding at the first corner
                if (ballAngleToLinePerpendicular < -Math.PI && ballAngleToLinePerpendicular > -Math.PI * 2) {
                    if (getPointCollision(ball, line.getX1(), line.getY1())) {
                        collisions.add(line, false);
                        //break;
                    }
                }
                //Colliding at the second corner
                else if (ballAngleToLinePerpendicular2 > -Math.PI || ballAngleToLinePerpendicular2 < -Math.PI * 2) {
                    if (getPointCollision(ball, line.getX2(), line.getY2())) {
                        collisions.add(line, false);
                        //break;
                    }
                }
                //If ball is between two corners
                else if (Math.abs(perpDistToLine(ball, line)) <= ball.getRadius()) {
                    collisions.add(line, true);
                    //break;
                }
            }
        }


    }

    public void go() {
        TimerTask timerTask = new TimerTask() {
            public void run() {
                repaint();
                collisions.update();
                ball.update();
                ball.applyGravity(G);
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

    private boolean getPointCollision(Ball ball, int x, int y) {
        return Math.sqrt((ball.getX() - x) * (ball.getX() - x) + (ball.getY() - y) * (ball.getY() - y)) <= ball.getRadius() ? true : false;
    }

    public static void main(String args[]) {
        Renderer animation = new Renderer();
        animation.setSize(700, 700);
        animation.show();
        animation.go();
    }
}
