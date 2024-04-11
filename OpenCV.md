# OpenCV

OpenCV is a very popular open source library for image processing. It uses a C++ wrapper to run faster.

* OpenCV full documentation: https://docs.opencv.org/4.x/index.html

Here you can find some useful notes about OpenCV functions and data structures usage.

Consider to use Webcam Capture API (see WebcamCapture.md) instead of OpenCV. That library has more features than OpenCV, 
for example the possibility to have a camera preview. 

## `IplImage`

`IplImage` is the base data structure to represent a common image using OpenCV API. It contains a lot of useful informations
about the image, like width, height, bit's depth and some pointers that point to image's data itself.

**Warning**: `Iplimage` is obsolete; in latest data structures it has been replaced with `cv::Mat`.