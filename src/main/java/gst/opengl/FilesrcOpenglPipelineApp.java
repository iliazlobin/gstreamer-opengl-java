package gst.opengl;

import cuchaz.jfxgl.CalledByEventsThread;
import cuchaz.jfxgl.JFXGL;
import cuchaz.jfxgl.JFXGLLauncher;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;

public class FilesrcOpenglPipelineApp {

//    @Override
//    public void start(Stage primaryStage) throws Exception {
//
////        primaryStage.setTitle("Drawing Operations Test");
////        BorderPane grid = new BorderPane();
////        grid.setCenter(imageView);
////        imageView.fitWidthProperty().bind(grid.widthProperty());
////        imageView.fitHeightProperty().bind(grid.heightProperty());
////        imageView.setPreserveRatio(true);
////        primaryStage.setScene(new Scene(grid, 460, 460));
////        primaryStage.show();
//    }

    public static void main(String[] args) {
        JFXGLLauncher.launchMain(FilesrcOpenglPipelineApp.class, args);
    }

    public static void jfxglmain(String[] args) throws Exception {

        GLFW.glfwInit();
//        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
//        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
//        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
//        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        long hwnd = GLFW.glfwCreateWindow(300, 169, "JFXGL", MemoryUtil.NULL, MemoryUtil.NULL);

        // init opengl
        GLFW.glfwMakeContextCurrent(hwnd);
        GL.createCapabilities();

//        // update the GL viewport when the window changes
//        GLFWWindowSizeCallbackI windowSizeCallback = (long hwndAgain, int width, int height) -> {
//            windowSize[0] = width;
//            windowSize[1] = height;
//            GL11.glViewport(0, 0, width, height);
//        };
//        GLFW.glfwSetWindowSizeCallback(hwnd, windowSizeCallback);

//        GL11.glClearColor(0f, 0f, 0f, 1.0f);
//        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        final FilesrcOpenglPipeline pipeline = new FilesrcOpenglPipeline(args, hwnd);
        pipeline.startPipeline();

        try {

            // start the JavaFX app
            JFXGL.start(hwnd, args, new HelloWorldApp());

            // render loop
            while (!GLFW.glfwWindowShouldClose(hwnd)) {

                // render the JavaFX UI
                JFXGL.render();

                GLFW.glfwSwapBuffers(hwnd);
                GLFW.glfwPollEvents();
            }

        } finally {

            JFXGL.terminate();
            Callbacks.glfwFreeCallbacks(hwnd);
            GLFW.glfwDestroyWindow(hwnd);
            GLFW.glfwTerminate();
        }
    }

    public static class HelloWorldApp extends Application {

        @Override
        @CalledByEventsThread
        public void start(Stage stage)
            throws IOException {

            Label label = new Label("Hello World!");
            label.setAlignment(Pos.CENTER);
            stage.setScene(new Scene(label));
        }
    }
}
