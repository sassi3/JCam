# OpenCV

OpenCV is a very popular open source library for image processing. It works thanks to JavaCV which is a Java wrapper
for OpenCV's C++ algorithms.

* OpenCV full [documentation](https://docs.opencv.org/4.x/index.html);
* JavaCV [GitHub](https://github.com/bytedeco/javacv/).

*Note*: if you are working on a Swift project, consider to use Webcam Capture API (see WebcamCapture.md) instead of OpenCV. That library has more features than OpenCV, 
for example the possibility to have a camera preview, and at the same time it supports JavaCV's framework. 

# Quick information

## Camera and image's handle

### `FrameGrabber` && `OpenCVFrameGrabber`

It is a data structure which extends `OpenCVFrameGrabber`. A `FrameGrabber` is a container
for a `OpenCVFrameGrabber` object.
\
`OpenCVFrameGrabber` is an object that requires the device's number which you are going to use to take photos.
The default device is `0`.

Before taking a photo you need to start the grabber with the `FrameGrabber`'s method `start()` 
(clearly at the end of your task you need to `stop()` the grabber). 
Then it is possible to take photo using the `grab()` method. It returns a `Frame` object.

### `Frame` && `JavaFXFrameConverter`

It is a data structure which is useful as "bridge" between JavaCV and JavaFX. In fact, 
`grabber.grab()` returns a `Frame`, but JavaFX works with `Image` objects. The conversion
operation is performed by the JavaFX's object `JavaFXFrameConverter`: its method `convert(frame)` allows
to convert a JavaCV `Frame` into `Image`.

#### `IplImage` (obsolete)

`IplImage` is a data structure to represent common image using JavaCV API. It contains a lot of useful informations
about the image, like width, height, bit's depth and some pointers that point to image's data itself.

**Warning**: `Iplimage` is obsolete; in latest data structures it has been replaced with `Mat`. It is possible that 
using this one you can gain some advantages and functionalities working on photos.

#### `Mat` (current)

`Mat` is a powerful data structure to represent n-dimensional dense numerical
single-channel or multi-channel array. It is clear the reason why it 
is the favourite way to store images of every type.

`Mat`'s [JavaDoc](https://bytedeco.org/javacpp-presets/opencv/apidocs/org/bytedeco/opencv/opencv_core/Mat.html).
