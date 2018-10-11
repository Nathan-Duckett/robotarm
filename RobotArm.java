import java.io.*;

/**
 * Code for the SCARA robot arm for ENGR110
 */
public class RobotArm {
    private PrintStream output;


    public RobotArm () {
        try {
            output = new PrintStream(new File("output.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("Error loading file");
        }
    }

    private void generate() {

    }

    private void convertToSignals() {
        output.println("1500,1500, 1500");
    }

    private void readPPM() {

    }

    public static void main(String args[]) {
        RobotArm r = new RobotArm();
        r.convertToSignals();
    }
}
