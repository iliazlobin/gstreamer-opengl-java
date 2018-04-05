/*************************************************************************
 * Copyright (C) 2017, Jeffrey W. Martin "Cuchaz"
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License Version 2 with
 * the classpath exception, as published by the Free Software Foundation.
 *
 * See LICENSE.txt in the project root folder for the full license.
 *************************************************************************/
package gst.opengl;

import cuchaz.jfxgl.CalledByEventsThread;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class DemoApp extends Application {

    @Override
    @CalledByEventsThread
    public void start(Stage stage)
        throws IOException {

        // load the main fxml
//        Scene scene = new Scene();
//        stage.setScene(scene);

        // set transparency for ui overlay
//        scene.setFill(null);
        stage.initStyle(StageStyle.TRANSPARENT);

        // the window is actually already showing, but JavaFX doesn't know that yet
        // so make JavaFX catch up by "showing" the window
        stage.show();
    }
}
