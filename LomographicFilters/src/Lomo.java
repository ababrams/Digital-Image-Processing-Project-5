

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *Class lomo stands for lomographic filter.
 *This class implements 2 lomographic filters, a red channel filter and a vignette effect.
 *These filters are modified through the use of sliders and the resulting image can be saved into the file directy by a button
 */

public class Lomo implements ActionListener, ChangeListener {
	public Mat img;
	public Mat copyImg;
	private final int ALPHA_SLIDER_MAX = 100;
	private final int BETA_SLIDER_MAX = 20;
	private JFrame frame;
	private JLabel imgLabel;
	private JButton saveButton;
	private JSlider sliderS;
	private JSlider sliderR;
	private JPanel sliderPanel;
	private double r;
	private double s;

	public static void main(String args[]) {
		Lomo lomo = new Lomo();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		if (args.length == 0) {
			System.out.println("-h for information on how to use solution.");
		} else {
			lomo.commandLineParser(args[0]);
		}
	}

	/**
	 * Performs the filter on the image by calling the lookuptable
	 * 
	 * @param img, image to be filtered
	 * @param s    value input by the user that is passed into the calculation
	 * @return
	 */
	public Mat redFilter(Mat img, double s) {
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
	public Mat makeLUT(double s) {
		// creates the lookup table
		Mat lookupTable = new Mat(1, 256, CvType.CV_8UC1);

		for (double i = 0; i < 256; i++) {
			double a = -(((i / 256.0) - 0.5) / s);
			double e = Math.exp(a);
			double result = 256.0 / (1.0 + e);
			// sets the data for that index as the calculated data
			lookupTable.put(0, (int) i, result);
		}
		return lookupTable;

	}
	
	/**
	 * Applies vignette effect to image
	 * 
	 * @param img image previously manipulated by the red filter
	 * @param r   radius tracked by slider, must range 1 <= r <= 100
	 * @return image with halo filter
	 */
	public Mat haloFilter(Mat img, double r) {
		// rMax = maximum radius.
		int rMax = 0;
		if (img.rows() < img.cols()) {
			rMax = img.rows();
		} else {
			rMax = img.cols();
		}

		double radius = (r * .01) * rMax;

		// create new image of the same size
		Mat mask = new Mat(img.rows(), img.cols(), CvType.CV_32FC3);
		// assign the value of 0.75 to each element in matrix
		for (int i = 0; i < img.rows(); i++) {
			for (int j = 0; j < img.cols(); j++) {
				double[] maskChannel = mask.get(i, j);
				// red channel
				maskChannel[0] = 0.75;
				// green channel
				maskChannel[1] = 0.75;
				// blue channel
				maskChannel[2] = 0.75;
			}
		}
		// finds the center point of the image
		int centerCol = img.cols() / 2;
		int centerRow = img.rows() / 2;
		// creates a circle on the mask with white color represented in the scalar
		// object.
		Imgproc.circle(mask, new Point(centerRow, centerCol), (int) radius, new Scalar(1, 1, 1), -1);

		Size circle = new Size(radius, radius);
		// blur the mask
		Imgproc.blur(mask, mask, circle);
		img.convertTo(img, CvType.CV_32FC3);
		// multiply the mask image with the image manipulated by the red filter
		Mat newMat = new Mat();
		newMat = img.mul(mask);
		newMat.convertTo(img, CvType.CV_8UC3);
		return newMat;
	}
	
	/**
	 * Processes argument passed from command line. If the argument is the file path
	 * of image the GUI is executed.
	 * 
	 * @param s argument at index 0
	 */
	public void commandLineParser(String f) {
		switch (f) {
		case "-h":
			System.out.println("How to use this program.\n\n"
					+ "After program compilation, when running the program, include directory path of desired image to be manipulated after program call.\n"
					+ "\n\tLomo <image-file-name>" + "\n\nThe GUI will now execute with provided image."
					+ "\nUse trackbars to manipulate image according to assignment specifications and requirements."
					+ "\nPress 's' to save copy of the current image in display and enter filename."
					+ "\nPress 'q' to terminate the application.");
			break;
		default:
			File file = new File(f);
			if (file.isFile()) {
				try {
					img = Imgcodecs.imread(f, 1);
					Imgproc.resize(img, img, new Size(img.rows() / 2, img.rows() / 2), 0, 0,
							 Imgproc.INTER_AREA);
					if (img == null) {
						System.out.println("File at " + file.toString() + " is not an image.");
						System.exit(0);
					} else {
						GUI(img);
						System.out.println("Image: " + file.toString());
					}
				} catch (Exception e) {
					System.out.println("File could not be opened.");
				}
			} else if (file.isDirectory()) {
				System.out.println("Filepath is a directory, include file in passed arguments.");
			} else {
				System.out.println("Filepath to image error.");
			}
		}
	}

	/**
	 * Saves copy of current displayed image, user must input filepath and filename.
	 * 
	 * @param img current displayed image
	 */
	public void save(Mat img) {
		Scanner in = new Scanner(System.in);

		System.out.println("Enter filepath and filename to save current copy of image: ");
		String fName = in.next();

		Imgcodecs.imwrite(fName, img);
		System.out.println("Image saved.");

		in.close();
	}


	//Start the code for the GUI here
	public void GUI(Mat matImg) {
		// Create and set up the window.
		frame = new JFrame("Lomographic Filter");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Change original image to buffered image to display
		Image image = HighGui.toBufferedImage(matImg);
		// Set up the content pane.'
		addComponentsToPane(frame.getContentPane(), image);
		
		// Display the window
		frame.pack();
		frame.setVisible(true);
		System.out.println();
	}

	/** 
	 * Instantiating the components, takes the image as input
	 */
	private void addComponentsToPane(Container pane, Image image) {
		if (!(pane.getLayout() instanceof BorderLayout)) {
			pane.add(new JLabel("Container doesn't use BorderLayout!"));
			return;
		}

		//Sliders are implemented here
		//SliderS is for the red filter
		//SliderR is for the vignette filter
		//saveButton is for the save button
		sliderPanel = new JPanel();
		saveButton = new JButton("Save");
		imgLabel = new JLabel(new ImageIcon());
		sliderS = new JSlider(8, BETA_SLIDER_MAX, 20);
		sliderR = new JSlider(1, ALPHA_SLIDER_MAX, 100);
	    imgLabel = new JLabel(new ImageIcon(image));
		
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));

		sliderR.setMajorTickSpacing(20);
		sliderR.setMinorTickSpacing(5);
		sliderR.setPaintTicks(true);
		sliderR.setPaintLabels(true);

		sliderS.setMajorTickSpacing(1);
		sliderS.setMinorTickSpacing(1);
		sliderS.setPaintTicks(true);
		sliderS.setPaintLabels(true);

		saveButton.addActionListener(this);

		sliderS.addChangeListener(this);
		sliderR.addChangeListener(this);

		//Adds the save button
		pane.add(saveButton, BorderLayout.PAGE_END);
		pane.add(imgLabel);
		
		//Labels both of the sliders and adds them to the GUI
		sliderPanel.add(new JLabel(String.format("S")));
		sliderPanel.add(sliderS);
		sliderPanel.add(new JLabel(String.format("R")));
		sliderPanel.add(sliderR);
		pane.add(sliderPanel, BorderLayout.PAGE_START);
		pane.add(imgLabel, BorderLayout.CENTER);
	}

	/**
	 * Saves the image
	 * 
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		save(copyImg);
	}

	/**
	 * Changes the image as the slider changes
	 * 
	 * @param e
	 */
	public void stateChanged(ChangeEvent e) {
			r = sliderR.getValue();
			s = sliderS.getValue();
			copyImg = new Mat();
			img.copyTo(copyImg);
			redFilter(copyImg, s*.01);
			haloFilter(copyImg, r);
			update(copyImg);
	}

	/**
	 * Updates the image when the slider changes value
	 * 
	 * @param filteredImg
	 */
	private void update(Mat filteredImg) {
		imgLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(filteredImg)));
		frame.repaint();
	}

}
