package gst.appsink;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.freedesktop.gstreamer.*;
import org.freedesktop.gstreamer.elements.AppSink;

import java.nio.ByteOrder;

/*
 * The main idea is to create a pipeline that has an appsink to display the images.
 * Connect the AppSink to the rest of the pipeline.
 * Connect the AppSinkListener to the AppSink.
 * The AppSink writes frames to the ImageContainer.
 * if you want to display the Videoframes simply add a changeListener to the container who will draw the current
 * Image to a Canvas or ImageView.
 */
public class RtspGstRenderer extends Application {
    private ImageView imageView;

    private AppSink videosink;
    private Pipeline pipeline;
    Bus bus;
    private StringBuilder caps;
    private ImageContainer imageContainer;

    public RtspGstRenderer() {

        videosink = new AppSink("GstVideoComponent");
        videosink.set("emit-signals", true);

        AppSinkListener appSinkListener = new AppSinkListener();
        videosink.connect(appSinkListener);

        caps = new StringBuilder("video/x-raw, ");
        // JNA creates ByteBuffer using native byte order, set masks according to that.
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            caps.append("format=BGRx");
        } else {
            caps.append("format=xRGB");
        }

        videosink.setCaps(new Caps(caps.toString()));
//        videosink.set("max-buffers", 5000);
        videosink.set("drop", true);

        // videofile source
        final Element source = ElementFactory.make("filesrc", "source");
        source.set("location" , "/home/user/vid/samples/video1.mkv");

        final Element decode = ElementFactory.make("decodebin", "decode");
        final Element convert = ElementFactory.make("videoconvert", "sink");

        pipeline = new Pipeline();
        pipeline.addMany(source, decode, convert, videosink);
        if (!source.link(decode)) {
            System.out.println("Couldn't link source to decode");
        }
        if (!Pipeline.linkMany(convert, videosink)) {
            System.out.println("Couldn't link convert to videosink");
        }

        decode.connect((Element.PAD_ADDED) (element, pad) -> {
            System.out.println("Pad added: " + pad);
            final Structure structure = pad.getCaps().getStructure(0);
            if (!structure.getName().contentEquals("video/x-raw")) {
                return;
            }
            pad.link(convert.getStaticPad("sink"));
        });

        imageView = new ImageView();
        imageContainer = appSinkListener.getImageContainer();
        imageContainer.addListener((observable, oldValue, newValue) ->
            Platform.runLater(() -> imageView.setImage(newValue))
        );

        bus = pipeline.getBus();
        bus.connect((Bus.MESSAGE) (arg0, arg1) ->
            System.out.println(arg1.getStructure())
        );

        pipeline.play();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("gst-java-test");
        BorderPane grid = new BorderPane();
        grid.setCenter(imageView);
        imageView.fitWidthProperty().bind(grid.widthProperty());
        imageView.fitHeightProperty().bind(grid.heightProperty());
        imageView.setPreserveRatio(true);
        primaryStage.setScene(new Scene(grid, 460, 460));
        primaryStage.show();
    }

    public static void main(String[] args) {
        Gst.init("RtspTest", args);
        launch(args);
    }
}
