package gst.opengl;

import com.sun.jna.NativeLong;
import org.freedesktop.gstreamer.*;
import org.freedesktop.gstreamer.interfaces.VideoOverlay;

import static org.freedesktop.gstreamer.lowlevel.GstVideoOverlayAPI.GSTVIDEOOVERLAY_API;

public class FilesrcOpenglPipeline {
    private final NativeLong hwnd;
    private Pipeline pipeline;
    private Bus bus;

    public FilesrcOpenglPipeline(String[] args, long hwnd) {
        this.hwnd = new NativeLong(hwnd);
        Gst.init("RtspTest", args);
    }

    public void startPipeline() {

        final Element fileserc = ElementFactory.make("filesrc", "filesrc");
        fileserc.set("location", "/mnt/OKTAUTODRV/video-samples/heavy.mkv");

        final Element decodebin = ElementFactory.make("decodebin", "decodebin");
        final Element glimagesink = ElementFactory.make("glimagesink", "glimagesink");

        pipeline = new Pipeline();
        pipeline.addMany(fileserc, decodebin, glimagesink);
        if (!fileserc.link(decodebin)) {
            System.out.println("Couldn't link file to decode");
        }

        decodebin.connect((Element.PAD_ADDED) (element, pad) -> pad.link(glimagesink.getStaticPad("sink")));

        bus = pipeline.getBus();
        bus.connect((Bus.MESSAGE) (arg0, arg1) ->
            System.out.println(arg1.getStructure())
        );
        bus.setSyncHandler(message -> {
            if (!VideoOverlay.isVideoOverlayPrepareWindowHandleMessage(message)) {
                return BusSyncReply.PASS;
            }
            // TODO: get vo from message
            final VideoOverlay vo = VideoOverlay.wrap(glimagesink);
            GSTVIDEOOVERLAY_API.gst_video_overlay_set_window_handle(vo, hwnd);
            return BusSyncReply.DROP;
        });

        pipeline.play();
    }
}
