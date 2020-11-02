# Project 5

Program written in Java/Eclipse.
It can be ran through Eclipse with Lomo.java holding the main class for configuration. If it doesn't already, eclipse needs opencv added as a User Library.
Program requires a file path argument for the input image. 
Running through command line.
cd to src file where Lomo.java is located
To compile:
javac -cp /C:/... file path to open cv jar:. Lomo.java
example: (personal using Ubuntu)
javac -cp /home/corwin/opencv_build/opencv/build/bin/opencv-440.jar:. Lomo.java
To run:
java -cp /C:/ ... file path to open cv jar:. -Djava.library.path=/C:/ ... path to open cv library  Lomo 'path to image'
example: (personal using Ubuntu)
//TODO
Running the program displays the Image and trackbars. One trackbar controls the parameter s that affects the color manipulation of the red channel. The other trackbar controls the parameter r which determines the radius of the halo for the vignette effect. 
To change the radius and the color manipulation parameter, toggle each trackbar.
The image is set in a display browser to be viewed as it is manipulated. //TODO save function information
Observations.txt: contains noted observations from applying algorithms to the image during Lomographic filter project
Testing.txt: contains testing conditions and expected responses
