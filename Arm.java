/* 
 * Name:
 * ID:
 */


import ecs100.*;
import java.util.*;
import java.io.*;
import java.awt.Color;

/** <description of class Arm>
 */
public class Arm{

    private ArrayList <Integer> xCo;
    private ArrayList <Integer> yCo; 
    private ArrayList <Integer> Pen; 
    
    private double d,xT,yT,xm1,ym1,xm2,ym2,xA,yA,R,H,SinTheta,CosTheta,x3,y3,x4,y4,leftMotorAngle,rightMotorAngle,penAngle=1500;
    
    private PrintStream write;
    
    public ImageRenderer img;
    
    public Arm(){
        img = new ImageRenderer();
        xm1=240;
        ym1=449;
        xm2=389;
        ym2=449;
        R=290;
        
        UI.initialise();
        UI.addButton("Load Image (choose)", img::renderImage);
        UI.addButton("Load Vertical Image", () -> {img.renderDirectly("vertical-line.ppm"); });
        UI.addButton("Load Horizontal Image", () -> {img.renderDirectly("horizontal-line.ppm"); });
        UI.addButton("Clear Current Image", this::clear);
        UI.addTextField("Left motor x", (String v) -> { xm1 = new Double(v); });
        UI.addTextField("Left motor y", (String v) -> { ym1 = new Double(v); });
        UI.addTextField("Right motor x", (String v) -> { xm2 = new Double(v); });
        UI.addTextField("Right motor y", (String v) -> { ym2 = new Double(v); });
        UI.addButton("Check motor positions", this::showMotorPositions);
        UI.addButton("Print Signals to File", this::Calculate);
        
        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.4);
        UI.setWindowSize(1200, 800);
    }
    
    /**
     * Calculates the angles required for each motor to move the pen to all x and y positions
     * in the co-ordinate lists
     */
    public void Calculate(){
        initLists();
        if (xCo.isEmpty() && yCo.isEmpty() && Pen.isEmpty()) {
            UI.println("You have not loaded an image yet.");
            UI.println("Click Load Image first before trying to print ");
        }
        
        if (xCo.size() != yCo.size() && xCo.size() != Pen.size() && yCo.size() != Pen.size()) {
            UI.println("An error occurred where the co-ordinate inputs do not add up");
            UI.println("Please load the image, and try again");
        }
        
        initWriter();
        
        for(int i=0; i<xCo.size();i++){
            xT = xCo.get(i);
            yT = yCo.get(i);
            
            /** Motor 1**/
            //Distance betwee tool and motor:
            d= Math.sqrt( ((xT-xm1)*(xT-xm1)) + ((yT-ym1)*(yT-ym1)) );
            
            //Midpoint
            xA = (xT+xm1)/2;
            yA = (yT+ym1)/2;
            
            //Distance between midpoint and joints:
            H= Math.sqrt((R*R)-((d/2)*(d/2)));
            
            //Angles
            CosTheta = Math.cos((xT-xm1)/d);
            SinTheta = Math.sin((yT-ym1)/d);
            
            //Joint positions:
            x3 = xA + H*SinTheta;
            y3 = yA - H*CosTheta;
            x4 = xA - H*SinTheta;
            y4 = yA + H*CosTheta;
            
            //Choose the lower x value for left motor and calculate angle
            if(x3<x4){leftMotorAngle = Math.atan2(ym1-y3,x3-xm1);}
            else{leftMotorAngle = Math.atan2(ym1-y4,x4-xm1);}
            
            
            /** Motor 2**/
            //Distance betwee tool and motor:
            d= Math.sqrt( ((xT-xm2)*(xT-xm2)) + ((yT-ym2)*(yT-ym2)) );
            
            //Midpoint
            xA = (xT+xm2)/2;
            yA = (yT+ym2)/2;
            
            //Distance between midpoint and joints:
            H= Math.sqrt((R*R)-((d/2)*(d/2)));
            
            //Angles
            CosTheta = Math.cos((xT-xm2)/d);
            SinTheta = Math.sin((yT-ym2)/d);
            
            //Joint positions:
            x3 = xA + H*SinTheta;
            y3 = yA - H*CosTheta;
            x4 = xA - H*SinTheta;
            y4 = yA + H*CosTheta;
            
            //Choose the higher x value for right motor and calculate angle
            if(x3>x4){rightMotorAngle = Math.atan2(ym2-y3,xm2-x3);}
            else{rightMotorAngle = Math.atan2(ym2-y4,xm2-x4);}
            
            writeMotorSignals(leftMotorAngle, rightMotorAngle, penAngle);
        }
        write.close();
    }

    /**
     * Initializes a new Writer to output to the file
     */
    public void initWriter(){
        try{
            write = new PrintStream (new File("image.txt"));
            UI.println("Command File Created\n\n");
            //UI.println(xCo.size()+yCo.size()+Pen.size());
        }
        catch(IOException e){
            UI.println("Error opening the file: " + e);
        } 
    }
    
    /**
     * Initializes the x, y and pen lists with the co-ordinates retrieved after rendering an image
     */
    public void initLists(){
        xCo = img.getX();
        yCo = img.getY();
        Pen = img.getPen();
    }
    
    private void clear() {
        xCo = new ArrayList<Integer>();
        yCo = new ArrayList<Integer>();
        Pen = new ArrayList<Integer>();
        img = new ImageRenderer();
        UI.clearPanes();
    }

    /**
     * Take an angle (in radians) and calculate the motor signal which is required to move to
     * the new angle positions.
     */
    public void writeMotorSignals(double left, double right, double pen){       
        //Convert from radians to degrees
        left = Math.abs(left * 180.0/Math.PI);
        right = Math.abs(right * 180.0/Math.PI);
        Trace.println(left + "  " + right);     //Debugging purposes
        
        //Apply the straight line formula to calculate the motor signal
        left = left * (500.0/ 90.0) + 1000;
        right = right * (500.0/ 90.0) + 1000;
        
        //Print the signals with no decimal places
        write.printf("%.0f,%.0f,%.0f\n", left, right, pen);
    }
    
    /**
     * Print out the current co-ordinates of the two motors for the arm
     */
    private void showMotorPositions() {
        UI.println("---- Current Motor Positions ----");
        UI.printf("Left: (%.1f,%.1f)\n", xm1, ym1);
        UI.printf("Right: (%.1f,%.1f)\n", xm2, ym2);
        UI.println("---------------------------------");
    }
    
    public static void main(String[] args){
        Arm obj = new Arm();
    }

}
