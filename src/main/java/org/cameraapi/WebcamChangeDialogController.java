package org.cameraapi;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.cameraapi.effects.LiveEffect;
import java.util.HashMap;

public class WebcamChangeDialogController {
    @FXML public Label palle;

    public void reset(HashMap<Class<? extends LiveEffect>, LiveEffect> liveEffects){
    }
}
