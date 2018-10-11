import java.io.*;
import java.util.*;

/**
 * Code for the SCARA robot arm for ENGR110
 */
public class RobotArm {
    private PrintStream output;

    private ArrayList<Integer> currentImage;
    private int currentImageRows, currentImageCols;

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

    private void readPPM(String fileName) {
        currentImage = new ArrayList<Integer>();
        Scanner sc;
        try {
            sc = new Scanner(new File(fileName));

            if (!sc.next().equals("P2")) return;

            currentImageCols = 0;
            currentImageRows = 0;
            int colorDepth = 255;
            double colorScale = 1;
            
            //Get the columns
            try {
                while (!sc.hasNextInt()) {
                    sc.next();
                }
                currentImageCols = sc.nextInt();
            } catch(InputMismatchException e) { System.out.println("Error: " + e); sc.nextLine(); }
            //Get the rows
            try  {
                while (!sc.hasNextInt()) {
                    sc.next();
                }
                currentImageRows = sc.nextInt();
            } catch(InputMismatchException e) { System.out.println("Error: " + e); sc.nextLine(); };
            //Get color depth
            try {
                while (!sc.hasNextInt()) {
                    sc.next();
                }
                colorDepth = sc.nextInt();
                colorScale = 255 / colorDepth;
            } catch(InputMismatchException e) { System.out.println("Error: " + e); sc.nextLine(); };
            
            for (int i = 0; i < currentImageRows; i++) {
                for (int j = 0; j < currentImageCols; j++){
                    int colorValue = sc.nextInt();
                    colorValue *= colorDepth;
                    currentImage.add(colorValue);
                }
            }

        } catch (IOException e) { System.out.println("Error loading the ppm"); }
        
        
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
