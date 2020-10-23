package src;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;

/**
 *
 */

public class Lomo {

	
/**
 * Makes the lookup table in the form of a matrix
 * @param s, value the red channel is manipulated
 * @return lookup table matrix
 */
public static Mat makeLUT(double s) {
	//checks that all pixel values are between 0 and 256
    if (s < 0 || s > 256) {
        System.out.println("Invalid Number of Colors. It must be between 0 and 256 inclusive.");
        return null;
    }
    //creates the lookuptable
    Mat lookupTable = Mat.zeros(new Size(1, 256), CV_8UC1);
    int i = 0;
    int startIdx = i;
    //i is the column, y is the row 
  for (i = 0; i < 256; i++) {
	  for (int y = startIdx; y < i; y++) {
    	double a = -(i/256 - 0.5)/s;
    	double e = Math.exp(a);
    	double result = 1/(1+e);
    	//sets the data for that particular pixel as the calculated data
    	lookupTable.put(i, y, result);
    }
	  startIdx = i;
 
    }
    return lookupTable;
    
}

/**
 * Performs the filter on the image by calling the lookuptable 
 * @param img, image to be filtered
 * @param s value inputted by the user that is passed into the calculation
 * @return 
 */
public static Mat redFilter(Mat img, double s) {
	//makes the lookuptable for the red channel
	Mat redLUT = makeLUT(s);
	//splits the image Mat into the 3 color channels
	List<Mat> BGR = new ArrayList<>(3);
	Core.split(img, BGR);
	//performs the lookup table manipulation to the image using the lookuptable created in makeLUT
	Core.LUT(BGR.get(2), redLUT, BGR.get(2));
	//merges the original image to the manipulated image
	Core.merge(BGR, img);
	return img;
}
}