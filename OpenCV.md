# OpenCV

OpenCV is a very popular open source library for image processing. It works thanks to JavaCV which is a Java wrapper
for OpenCV's C++ algorithms.

* OpenCV full documentation: https://docs.opencv.org/4.x/index.html;
* JavaCV [GitHub](https://github.com/bytedeco/javacv/).

Here you can find some useful notes about OpenCV functions and data structures usage.

Consider to use Webcam Capture API (see WebcamCapture.md) instead of OpenCV. That library has more features than OpenCV, 
for example the possibility to have a camera preview, and at the same time it supports JavaCV's framework. 

## Quick information

### `IplImage` (obsolete)

`IplImage` is the base data structure to represent a common image using OpenCV API. It contains a lot of useful informations
about the image, like width, height, bit's depth and some pointers that point to image's data itself.

**Warning**: `Iplimage` is obsolete; in latest data structures it has been replaced with `cv::Mat`.

### `Mat`


