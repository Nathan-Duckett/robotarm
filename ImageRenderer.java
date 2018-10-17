// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP102 - 2018T1
 * Name: 
 * Username:
 * ID:
 */

import ecs100.*;
import java.util.*;
import java.io.*;
import java.awt.Color;

/** Renders plain ppm images onto the graphics panel
 *  ppm images are the simplest possible colour image format.
 */

public class ImageRenderer{
    public static final int TOP = 20;   // top edge of the image
    public static final int LEFT = 20;  // left edge of the image
    public static final int PIXEL_SIZE = 2;  

    private int [][] originalImage;     //A list to store the pixel values of the image
    private int rows = 0;
    private int cols = 0;

    private int [][] applyKernalX= new int [][] {{-1,0,1},  
            {-2,0,2},
            {-1,0,1}};

    private int [][] applyKernalY= new int [][] {{1,2,1},
            {0,0,0},
            {-1,-2,-1}};  

    private int [][] edgeImageX;    //A list to store horizontal edges
    private int [][] edgeImageY;    //A list to stroe vertical edges
    private int [][] bwImage;    //A list to stroe vertical edges

    public ArrayList <Integer> xCoord = new ArrayList <Integer> ();
    public ArrayList <Integer> yCoord = new ArrayList <Integer> (); 
    public ArrayList <Integer> pen = new ArrayList <Integer> (); 
    
    private boolean follow = false;
    private int color;
    private int count;

    public ImageRenderer(){
        //ImageRenderer ir = new ImageRenderer();
    }
    
    /** 
     * Renders a ppm image file.
     * Asks for the name of the file, then calls scanImage.
     */
    public void renderImage(){
        String fileName  = UIFileChooser.open();
        File myFile = new File(fileName);
        try {
            Scanner scan = new Scanner(myFile);
            this.scanImage(scan);
        }

        catch(IOException e){UI.printf("File Failure %s \n", e);}
    } 

    /** 
     * Renders a ppm image file.
     * The first four tokens are "P3", number of columns, number of rows, 255
     * The remaining tokens are the pixel values (red, green, blue for each pixel)
     */
    public void scanImage(Scanner sc){
        int row = 0;
        int col = 0;
        while (sc.hasNext()){
            String format = sc.next();
            int rows = sc.nextInt();    //Number of rows
            int cols = sc.nextInt();    //Number of cols
            int pixelValue = sc.nextInt();  //Bit depth
            originalImage = new int [rows][cols];//Init
            //xyCoordinates=new HashSet<double>[rows*cols][rows*cols];  //Init 
            if(format.equals("P3"))     {
                col = 0;
                while(col < cols){
                    row = 0;
                    while(row < rows){
                        int r = sc.nextInt();  
                        int g = sc.nextInt();
                        int b = sc.nextInt(); 
                        int avg = (r+g+b)/3;                   
                        originalImage[row][col] = avg;                        
                        row++;
                    }
                    col++;   
                }
            }
        }
        renderImage(row, col);

    }

    /** 
     * Perform 2D convolution using sobel kernels.
     */
    public void renderImage(int rows,int cols){

        edgeImageX = new int [rows-2][cols-2];
        edgeImageY = new int [rows-2][cols-2];
        bwImage= new int [rows][cols];
        for(int col = 0; col < cols-2; col++){            

            for(int row = 0; row < rows-2; row++){

                edgeImageX[row][col] =
                originalImage[row][col] * applyKernalX [0][0] + originalImage[row][col+1] * applyKernalX [0][1] + originalImage[row][col+2] * applyKernalX [0][2] +
                originalImage[row+1][col] * applyKernalX [1][0] + originalImage[row+1][col+1] * applyKernalX [1][1] + originalImage[row+1][col+2] * applyKernalX [1][2] +
                originalImage[row+2][col] * applyKernalX [2][0] + originalImage[row+2][col+1] * applyKernalX [2][1] + originalImage[row+2][col+2] * applyKernalX [2][2]                ;

                edgeImageY[row][col] =
                originalImage[row][col] * applyKernalY [0][0] + originalImage[row][col+1] * applyKernalY [0][1] + originalImage[row][col+2] * applyKernalY [0][2] +
                originalImage[row+1][col] * applyKernalY [1][0] + originalImage[row+1][col+1] * applyKernalY [1][1] + originalImage[row+1][col+2] * applyKernalY [1][2] +
                originalImage[row+2][col] * applyKernalY [2][0] + originalImage[row+2][col+1] * applyKernalY [2][1] + originalImage[row+2][col+2] * applyKernalY [2][2];

                double x = edgeImageX[row][col];//horizontal edges
                double y = edgeImageY[row][col];//vertical edges
                double a = Math.sqrt((x * x )+(y * y));//union vertical edges and horizontal edges               

                //Draw the edges in white and the rest in black. The XY coord. of the white edges should be converted to motor signals
                if (a >= 250){  //Reduce this value to increase the depth of the edges. But don't go below 200
                    UI.setColor(Color.white);
                    UI.fillRect(LEFT+PIXEL_SIZE*row,TOP+PIXEL_SIZE*col,PIXEL_SIZE,PIXEL_SIZE);
                    color=255;
                }
                else{
                    UI.setColor(Color.black);
                    UI.fillRect(LEFT+PIXEL_SIZE*row,TOP+PIXEL_SIZE*col,PIXEL_SIZE,PIXEL_SIZE);
                    color=0;
                }
                bwImage[row][col]=color;
            }
            UI.printf("\n");
        }
        Image3(rows,cols);
    }

    public void Image3(int row, int col){
        follow = false;
        boolean completed = true;
        for(int co = 0; co < col; co++){            
            for(int ro = 0; ro < row; ro++){
                if(bwImage[ro][co] == 255){
                    completed = false;
                    follow = true;
                    pen.add(0);
                    while(follow){
                        xCoord.add(ro);
                        UI.println("x: "+ro);
                        yCoord.add(co);
                        UI.println("y: "+co);
                        pen.add(1);
                        bwImage[ro][co] = 0;
                        checkNeigh(ro,co);
                        if(count == -2){
                            follow = false;
                            break;
                        }
                        if(count == 0){
                            ro -= 1;
                            co -= 1;
                        }
                        else if(count == 1){
                            ro -= 1;
                        }
                        else if(count == 2){
                            ro -= 1;
                            co += 1;
                        }
                        else if(count == 3){
                            co -= 1;
                        }
                        else if(count == 5){
                            co += 1;
                        }
                        else if(count == 6){
                            ro += 1;
                            co -= 1;
                        }
                        else if(count == 7){
                            ro += 1;
                        }
                        else if(count == 8){
                            ro += 1;
                            co += 1;
                        }
                    }
                }
            }
        }
        
        if(completed){
            for(int value : xCoord){
                UI.println("comp x: "+value);
            }
            for(int value : yCoord){
                UI.println("comp y: "+value);
            }
        }
    }

    public int checkNeigh(int row, int col){
        count = -1;
        int max = 255;
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                count++;
                UI.println("count: "+count);
                if(bwImage[row + i][col + j] == max){
                    UI.println("count: "+count);
                    return count;
                }
            }
        }
        count = -2;
        return count;
    }
    
    public ArrayList<Integer> getX(){
        return xCoord;
    }
    
    public ArrayList<Integer> getY(){
        return yCoord;
    }
    
    public ArrayList<Integer> getPen(){
        return pen;
    }
    
    
}

