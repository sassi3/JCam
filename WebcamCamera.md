# Webcam Camera

Webcam Camera API is a powerful library that allow you to display a streaming
preview of what your webcam is capturing, and to take and save photos.

* You can find all information you need at API's [GitHub](https://github.com/sarxos/webcam-capture);
* YouTube [playlist](https://www.youtube.com/playlist?list=PLhs1urmduZ28_IFafEsXNq3fjdqXLfpuL) to learn quickly how to use this library; 
* Webcam Capture's [JavaDoc](https://javadoc.io/doc/com.github.sarxos/webcam-capture/latest/index.html).

**Important**: Webcam Capture API **supports** JavaCV's framework.

## Quick information

### `Jframe`

This data structure is an extension of `Frame`, but `Jframe` is sightly incompatible with that because it contains a 
`JRootPane`. The content pane provided by the root pane should, as a rule, contain all the non-menu components displayed by the `JFrame`.

Specific information about `Jframe` data structure and its methods are available on its [JavaDoc](https://docs.oracle.com/javase/8/docs/api/javax/swing/JFrame.html).

### Swing

It is true that Webcam Capture is a library written for Swing projects,
but it is possible to use JavaFX by importing `javafx.embed.swing.SwingFXUtils`.
