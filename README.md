# Project 5

Program written in Java/Eclipse.

It can be ran through Eclipse with Lomo.java holding the main class for configuration. If it doesn't already, eclipse needs opencv added as a User Library.

Program requires a file path argument for the input image. This program is calibrated for larger images. It is recommended to use the images provided in the image folder. If other images are used, they will be automatcially resized to half their original size. 
Running through command line.

cd to src file where Lomo.java is located

To compile:

javac -cp /C:/... file path to open cv jar:. Lomo.java

example:

javac -cp /home/corwin/opencv_build/opencv/build/bin/opencv-440.jar:. Lomo.java

To run:

java -cp /C:/ ... file path to open cv jar:. -Djava.library.path=/C:/ ... path to open cv library  Lomo 'path to image'

example: java -cp /home/corwin/opencv_build/opencv/build/bin/opencv-440.jar:.  -Djava.library.path=/home/corwin/opencv_build/opencv/build/lib/ Lomo /home/corwin/Desktop/images/


Running the program displays the Image and trackbars. One trackbar controls the parameter s that affects the color manipulation of the red channel. The other trackbar controls the parameter r which determines the radius of the halo for the vignette effect. 
To change the radius and the color manipulation parameter, toggle each trackbar.
The image is set in a display browser to be viewed as it is manipulated. To save the image currently displayed, click the save button on the bottom of the gui and enter the absolute path into the command line. 


Tests:

Program requires an image passeed in as a command line argument

Test1:
Execute program with a file that is not an image.

Result: Console output "File at ballpit.docx is not an image."

Test2:
Execute program with file that does not exist

Result: Console output "File could not be opened."

Test3:
Task bar was set as 100 and 20. Then the task bar was set at 1 and 8

Result: The image changed appropriately 

Test4:
The save button was selected 

Result: "Enter filepath and filename to save current copy of image:"

Test5:
The save button was selected and filepath and filename were entered. 

Result: Image was saved in the correct location with the correct filename

Test6:
Wrong keys pressed during JFrame operation

Result: Nothing occurs as the only keys with listeners are the ones detailed in the ReadMe

These are only types of tests conducted for program. The rest of the program is self contained and cannot deviate from normal operation if the requirements are meet.


Observations:

When the radius is set at 100 the halo is not visible. When it is set at 1, an extremely tiny circle is visible. The halo affect is visible when the radius is 45 or less. The most visually effective values for the radius is between 35-45. Any value lower than 35 produces an image that is largely overtaken by the halo. Any value larger than 45 the halo is not visible. This is due to the radius of the halo effect being a percentage of the smallest dimension of the image. In the case of our sample image the limiting dimension is it's width which is roughly 45% of its height.

As the value of s decreased, the color in the image became more manipulated. It increased the contrast and saturation. At the value of 0.20, the image was not visibly distinct in color from the original image. The value of 0.08 created the most dramatic color change. 

The final image compared to the original image is far more saturated in color. The contrast is greater as well and the image appears soft. The manipulated red channel caused the other colors in the image to be visibly different. The halo caused the center to pop and the edges to be darker.

![Example Image](https://gitlab.cs.ecu.edu/digital-image-processing/project-5/-/blob/master/example.JPG)
