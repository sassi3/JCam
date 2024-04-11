# Webcam Camera

### Out of this project: it uses Swing

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

### Webcam Camera in JavaFX

It is true that Webcam Capture is a library written for Swing projects,
so be careful using that into JavaFX projects! There is a way to use this library in
Java FX (see the correspondent section on the linked GitHub page). Anyway, it is not
recommended to use JavaFX framework because Webcam Camera is Swift native. Principal problem is 
`Jframe` data structure: it is incompatible with JavaFX because it uses `Frame`.
