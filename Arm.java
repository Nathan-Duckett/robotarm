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
    
    public ImageRenderer img = new ImageRenderer();
    
    public Arm(){
        UI.initialise();
        UI.addButton("Do it", this::Calculate);
        UI.addButton("Quit", UI::quit);
    }
    
    public void initLists(){
        xCo = img.xCoord;
        yCo = img.yCoord;
        Pen = img.pen;

        UI.println(img.xCoord.size());
        UI.println(yCo.size());
        UI.println(Pen.size());
    }

    public void Calculate(){
        xm1=240;
        ym1=449;
        xm2=389;
        ym2=449;
        R=290;
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
            if(x3<x4){leftMotorAngle = Math.PI/2 - Math.atan2(ym1-y3,x3-xm1);}
            else{leftMotorAngle = Math.PI/2 - Math.atan2(ym1-y4,x4-xm1);}
            
            
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
            if(x3>x4){rightMotorAngle = Math.PI/2 - Math.atan2(ym2-y3,xm2-x3);}
            else{rightMotorAngle = Math.PI/2 - Math.atan2(ym2-y4,xm2-x4);}
            
            writeMotorSignals(leftMotorAngle, rightMotorAngle, penAngle);
        }
        
    }

    public void initWriter(){
        try{
            write = new PrintStream (new File("image.txt"));
            UI.println("File Created\n\n");
            //UI.println(xCo.size()+yCo.size()+Pen.size());
        }
        catch(IOException e){
            UI.println("Error opening the file: " + e);
        } 
    }

    public void writeMotorSignals(double left, double right, double pen){
        //write.println(left+","+right+","+pen);
        //left = Math.abs(Math.toDegrees(left));
        //right = Math.abs(Math.toDegrees(right));
        left = Math.abs(left * 180.0/Math.PI);
        right = Math.abs(right * 180.0/Math.PI);
        UI.println(left + "  " + right);
        left = left * (500.0/ 90.0) + 1000;
        right = right * (500.0/ 90.0) + 1000;
        write.printf("%.0f,%.0f,%.0f\n", left, right, pen);
    }

    public void closeWriter() {
        write.close();
    }
    
    public static void main(String[] args){
        Arm obj = new Arm();
        obj.img.renderImage();
        obj.initLists();
        obj.Calculate();
        obj.closeWriter();
    }

}
