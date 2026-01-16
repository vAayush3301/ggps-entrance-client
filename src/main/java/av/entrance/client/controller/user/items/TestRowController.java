package av.entrance.client.controller.user.items;

import av.entrance.client.model.Test;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class TestRowController {
    public Label testName;
    public Label sNo;
    public Button attemptTest;
    public Label duration;

    public void attempt() {
    }

    public void setData(Test test) {
        testName.setText(test.getTestName());
    }
}
