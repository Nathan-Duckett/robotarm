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

    private void generateHorizontal() {
        try {
            output = new PrintStream(new File("horizontal.txt"));

            output.println("1500,1500,1600");
            output.println("1300,1300,1600");
            for (int i = 1300; i < 1700; i += 50) {
                output.printf("%d,%d,1400\n", i, i);
            }
            output.println("1700,1700,1600");

        } catch (FileNotFoundException e) {
            System.out.println("Error loading file");
        }
    }

    public static void main(String args[]) {
        RobotArm r = new RobotArm();
        r.convertToSignals();
        r.generateHorizontal();
    }
}
