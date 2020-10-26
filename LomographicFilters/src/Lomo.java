
import java.util.ArrayList;
import java.util.List;


import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.CvType; 
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.HighGui;

/**
 *
 */

public class Lomo {
	public static Mat img;


public static void main(String args[]) {
	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	img =  Imgcodecs.imread("images/costume.png", 1);
	double s = 0.2;
	redFilter(img, s);
	Imgproc.resize(img, img, new Size(img.rows()/2, img.rows()/2), 0, 0, 
	         Imgproc.INTER_AREA);
	//HighGui.imshow("Image", img);
	HighGui.waitKey();
	System.exit(0);
}




/**
 * Makes the lookup table in the form of a matrix
 * @param s, value the red channel is manipulated
 * @return lookup table matrix
 */
public static Mat makeLUT(Mat img, double s) {
	//checks that all pixel values are between 0 and 256
    if (s < 0 || s > 256) {
        System.out.println("Invalid Number of Colors. It must be between 0 and 256 inclusive.");
        return null;
    }
    //creates the lookup table
    Mat lookupTable = new Mat(new Size(1, 256), CvType.CV_8UC1);
    int i = 0;
    int startIdx = i;
    //i is the column, y is the row 
  for (i = 0; i < 256; i++) {
	  for (int y = startIdx; y < i; y++) {
    	double a = -((i/256) - 0.5)/s;
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
 * @param s value input by the user that is passed into the calculation
 * @return 
 */
public static Mat redFilter(Mat img, double s) {
	//makes the lookup table for the red channel
	Mat redLUT = makeLUT(img, s);
	//splits the image Mat into the 3 color channels
	List<Mat> channels = new ArrayList<>(3);
	Core.split(img, channels);
	//performs the lookup table manipulation to the image using the lookuptable created in makeLUT
	Core.LUT(channels.get(2), redLUT, channels.get(2));

	//merges the original image to the manipulated image
	Core.merge(channels, img);
	HighGui.imshow("Image", img);
	return img;
}
}