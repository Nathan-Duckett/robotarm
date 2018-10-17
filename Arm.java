import ecs100.*;
import java.util.*;
import java.io.*;
import java.awt.Color;

/** <description of class Arm>
 */
public class Arm extends ImageRenderer{

    private ArrayList <Integer> xCo = new ArrayList <Integer> (xCoord);
    private ArrayList <Integer> yCo = new ArrayList <Integer> (yCoord); 
    private ArrayList <Integer> Pen = new ArrayList <Integer> (pen); 
    
    private PrintStream output;
    private double previousX = 300;
    private double previousY = 100;

    private double initialAngleLeft = 1500;
    private double initialAngleRight = 1500;

    private static final double radiusSize = 1500;
    private ArrayList<Double> commands = new ArrayList<Double>();


    private double Theta1;
    private double Theta2;
    private double xm1=300;
    private double ym1=480;
    private double xm2=340;
    private double ym2=480;

    public Arm(){
        UI.initialise();
        UI.addButton("Do it", this::calculate);
        UI.addButton("Quit", UI::quit);
    }

    public void calculate(){
        for(int i=0; i<xCo.size();i++){
            int x1=xCo.get(i);
            int y1=yCo.get(i);
            Theta1 = Math.atan2(y1-ym1,x1-xm1);
        }
        for(int i=0; i<xCo.size();i++){
            int x2=xCo.get(i);
            int y2=yCo.get(i);
            Theta2 = Math.atan2(y2-ym2,x2-xm2);
        }
    }

    /**
     * Move the pen from the current position to a new x, y position
     * Converts to commands which are stored in the list
     */
    private void moveToPoint(double x, double y) {
        System.out.println("Moving to point");
        double d = Math.sqrt((Math.pow(x - previousX, 2) + (Math.pow(y - previousY, 2))));

        if (d > 2 * radiusSize) {
            return;
        }

        double xA = (previousX + x) / 2;
        double yA = (previousY + y) / 2;

        double h = Math.sqrt(Math.pow(radiusSize, 2) - Math.pow((d / 2), 2));

        double cosAngle = Math.cos((x - previousX) / d);
        double sinAngle = Math.sin((y - previousY) / d);

        double x3 = xA + h * sinAngle;
        double y3 = yA - h * cosAngle;
        double x4 = xA - h * sinAngle;
        double y4 = yA + h * cosAngle;

        int origin = 1500;

        //Angles are measured in radians
        double angleLeft = Math.atan2(y3, x3 - origin);
        double angleRight = Math.atan2(y4, origin - x4);

        commands.add(angleLeft);
        commands.add(angleRight);
        commands.add(1600.0);

        //For debugging in the simulator
        System.out.println(Math.toDegrees(angleLeft));
        System.out.println(Math.toDegrees(angleRight));
        
        previousX = x;
        previousY = y;
    }

    private void convertToSignals() {
        for (int i = 0; i < commands.size() / 3; i += 3){
            double left = (commands.get(i) * Math.PI * 500) / 2;
            double right = (commands.get(i + 1) * Math.PI * 500) / 2;
            double pen = commands.get(i + 2);
            output.printf("%f,%f,%f\n", left, right, pen);
            System.out.printf("%f,%f,%f\n", left, right, pen);
        }
        output.close();
    }


    public static void main(String[] args){
        Arm obj = new Arm();
    }

}
