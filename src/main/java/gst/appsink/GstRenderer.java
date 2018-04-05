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
public class GstRenderer extends Application {
    private ImageView imageView;

    private AppSink appsink;
    private Pipeline pipeline;
    private Bus bus;
    private StringBuilder caps;
    private ImageContainer imageContainer;

    public GstRenderer() {

        appsink = new AppSink("GstVideoComponent");
        appsink.set("emit-signals", true);

        AppSinkListener appSinkListener = new AppSinkListener();
        appsink.connect(appSinkListener);

        caps = new StringBuilder("video/x-raw, ");
        // JNA creates ByteBuffer using native byte order, set masks according to that.
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            caps.append("format=BGRx");
        } else {
            caps.append("format=xRGB");
        }

        appsink.setCaps(new Caps(caps.toString()));
//        appsink.set("max-buffers", 5000);
        appsink.set("drop", true);

//        bin = Bin.launch("autovideosrc ! videoconvert", true);
        // rtsp source
//        Bin rtspsrc = Bin.launch("rtspsrc location=\"rtsp://192.168.0.11:8554/video1\" ! rtph264depay ! avdec_h264 ! videoconvert", true);
//        Bin rtspsrc = Bin.launch("rtspsrc location=\"rtsp://192.168.0.11:8554/video1\"", true);
//        Bin rtph264depay = Bin.launch("rtph264depay", true);
//        Bin avdec_h264 = Bin.launch("avdec_h264", true);
//        Bin videoconvert = Bin.launch("videoconvert", true);
//        pipeline.addMany(rtspsrc, rtph264depay, avdec_h264, videoconvert, appsink);
//        Pipeline.linkMany(rtspsrc, rtph264depay, avdec_h264, videoconvert, appsink);

        // videofile source
        final Element filesrc = ElementFactory.make("filesrc", "filesrc");
        filesrc.set("location" , "/home/user/vid/samples/video1.mkv");

        final Element decodebin = ElementFactory.make("decodebin", "decodebin");
        final Element videoconvert = ElementFactory.make("videoconvert", "videoconvert");

        pipeline = new Pipeline();
        pipeline.addMany(filesrc, decodebin, videoconvert, appsink);
        if (!filesrc.link(decodebin)) {
            System.out.println("Couldn't link source to decode");
        }
        if (!Pipeline.linkMany(videoconvert, appsink)) {
            System.out.println("Couldn't link convert to appsink");
        }

        decodebin.connect((Element.PAD_ADDED) (element, pad) -> {
            System.out.println("Pad added: " + pad);
            final Structure structure = pad.getCaps().getStructure(0);
            if (!structure.getName().contentEquals("video/x-raw")) {
                return;
            }
            pad.link(videoconvert.getStaticPad("sink"));
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
        primaryStage.setTitle("Drawing Operations Test");
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
