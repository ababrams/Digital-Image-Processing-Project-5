package src;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.HighGui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 */

public class Lomo {
	public static Mat img;
	private static JFrame frame;
	private static JLabel imgLabel;
    private static final int ALPHA_SLIDER_MAX = 100;
    private static int alphaVal = 0;
    
	public static void main(String args[]) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		if (args.length == 0) {
			System.out.println("-h for information on how to use solution.");
		} else {
			commandLineParser(args[0]);
		}
		img = Imgcodecs.imread("images/costume.png", 1);
		double s = 0.08;
		double r = 3;
		Mat red = redFilter(img, s);
		Imgproc.resize(img, img, new Size(img.rows() / 2, img.rows() / 2), 0, 0, Imgproc.INTER_AREA);
		Mat filteredImage = haloFilter(red, r);
		//lomoGUI(filteredImage);
		HighGui.imshow("Image", filteredImage);
		HighGui.waitKey();
		System.exit(0);
	}

	/**
	 * Performs the filter on the image by calling the lookuptable
	 * 
	 * @param img, image to be filtered
	 * @param s    value input by the user that is passed into the calculation
	 * @return
	 */
	public static Mat redFilter(Mat img, double s) {
		// makes the lookup table for the red channel
		Mat redLUT = makeLUT(s);
		// splits the image Mat into the 3 color channels
		List<Mat> channels = new ArrayList<>(3);
		Core.split(img, channels);
		// performs the lookup table manipulation to the image using the lookup table
		// created in makeLUT
		Core.LUT(channels.get(2), redLUT, channels.get(2));
		// merges the original image to the manipulated image
		Core.merge(channels, img);
		return img;
	}

	/**
	 * Makes the lookup table in the form of a matrix. Look up tables apply the new
	 * calculated pixel intensity to to pixel intensity values that match i;.
	 * 
	 * @param s, value the red channel is manipulated
	 * @return lookup table matrix
	 */
	public static Mat makeLUT(double s) {
		// creates the lookup table
		Mat lookupTable = new Mat(1, 256, CvType.CV_8UC1);
		
		for(double i = 0; i < 256; i++) {
			double a = -(((i / 256.0) - 0.5) / s);
			double e = Math.exp(a);
			double result = 256.0 / (1.0 + e);
			// sets the data for that index as the calculated data
			lookupTable.put(0,(int) i, result);
		}
		return lookupTable;

	}

	/**
	 * Processes argument passed from command line. If the argument is the file path
	 * of image the GUI is executed.
	 * 
	 * @param s argument at index 0
	 */
	public static void commandLineParser(String s) {
		switch (s) {
		case "-h":
			System.out.println("How to use this program.\n\n"
					+ "After program compilation, when running the program, include directory path of desired image to be manipulated after program call.\n"
					+ "\n\tLomo <image-file-name>" + "\n\nThe GUI will now execute with provided image."
					+ "\nUse trackbars to manipulate image according to assignment specifications and requirements."
					+ "\nPress 's' to save copy of the current image in display and enter filename."
					+ "\nPress 'q' to terminate the application.");
			break;
		default:
			File f = new File(s);
			if (f.isFile()) {
				try {
					img = Imgcodecs.imread(s, 1);
					if (img == null) {
						System.out.println("File at " + f.toString() + " is not an image.");
						System.exit(0);
					} else {
						lomoGUI(img);
						System.out.println("Image: " + f.toString());
					}
				} catch (IOException e) {
					System.out.println("File could not be opened.");
				}
			} else if (f.isDirectory()) {
				System.out.println("Filepath is a directory, include file in passed arguments.");
			} else {
				System.out.println("Filepath to image error.");
			}
		}
	}

	/**
	 * Saves copy of current displayed image user must input filepath and filename.
	 * 
	 * @param img current displayed image
	 */
	public static void save(Mat img) {
		Scanner in = new Scanner(System.in);

		System.out.println("Enter filepath and filename to save current copy of image: ");
		String fName = in.next();

		Imgcodecs.imwrite(fName, img);
		System.out.println("Image saved.");

		in.close();
	}

	/**
	 * Applies vignette effect to image
	 * 
	 * @param img image previously manipulated by the red filter
	 * @param r   radius tracked by slider, must range 1 <= r <= 100
	 * @return image with halo filter
	 */
	public static Mat haloFilter(Mat img, double r) {
		r = r* 100;
		//rMax = maximum radius.
		int rMax = 0;
		if (img.rows()<img.cols()) {
			 rMax = img.rows();
		} else {
			rMax = img.cols();
		}
		if (r > rMax) {
		   System.out.println("raduis can not be greater than" + rMax);
		   //TODO prompt for new r value. probably needs to be in the gui code. 
		}
		// create new image of the same size
		Mat mask = new Mat(img.rows(), img.cols(), CvType.CV_32FC3);
		// assign the value of 0.75 to each element in matrix
		for (int i = 0; i < img.rows(); i++) {
			for (int j = 0; j < img.cols(); j++) {
				double [] maskChannel = mask.get(i,j);
				//red channel
				maskChannel[0] = 0.75;
				//green channel
				maskChannel[1] = 0.75;
				//blue channel
				maskChannel[2] = 0.75;
			}
			//finds the center point of the image 
			int centerCol= img.cols()/2;
			int centerRow = img.rows()/2;
			//creates a circle on the mask with white color represented in the scalar object. 
			Imgproc.circle(mask, new Point(centerCol, centerRow),(int)r, new Scalar(255, 255, 255));
		}
		
		//
		Size radius = new Size(r, r);
		//blur the mask 
		Imgproc.blur(mask, mask, radius);
		img.convertTo(img, CvType.CV_32FC3);
		//multiply the mask image with the image manipulated by the red filter
		Mat newMat = new Mat();
		newMat = img.mul(mask);
		newMat.convertTo(newMat, CvType.CV_8UC3);
		return newMat;
	}

	public static void lomoGUI(Mat matImg) {
		 // Create and set up the window.
        frame = new JFrame("Linear Blend");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set up the content pane.
        Image img = HighGui.toBufferedImage(matImg);
        addComponentsToPane(frame.getContentPane(), img);
        // Use the content pane's default BorderLayout. No need for
        // setLayout(new BorderLayout());
        // Display the window
        frame.pack();
        frame.setVisible(true);
	}
	
	private static void addComponentsToPane(Container pane, Image img) {
	        if (!(pane.getLayout() instanceof BorderLayout)) {
	            pane.add(new JLabel("Container doesn't use BorderLayout!"));
	            return;
	        }
	        JPanel sliderPanel = new JPanel();
	        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
	        sliderPanel.add(new JLabel(String.format("Alpha x %d", ALPHA_SLIDER_MAX)));
	        JSlider slider = new JSlider(0, ALPHA_SLIDER_MAX, 0);
	        slider.setMajorTickSpacing(20);
	        slider.setMinorTickSpacing(5);
	        slider.setPaintTicks(true);
	        slider.setPaintLabels(true);
	        slider.addChangeListener(new ChangeListener() {
	            @Override
	            public void stateChanged(ChangeEvent e) {
	                JSlider source = (JSlider) e.getSource();
	                alphaVal = source.getValue();
	               // update();
	            }
	        });
	        sliderPanel.add(slider);
	        pane.add(sliderPanel, BorderLayout.PAGE_START);
	        imgLabel = new JLabel(new ImageIcon(img));
	        pane.add(imgLabel, BorderLayout.CENTER);
	    }

}