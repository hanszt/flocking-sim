package hzt.model.custom_controls;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;

public class SwitchButton extends Button {

    private String activeLabel;
    private String inActiveLabel;
    private final MultipleEventsHandler multipleEventsHandler;

    private boolean active;
    private boolean switchModeEnabled;
    private String activeStyle;
    private String inActiveStyle;

    public SwitchButton() {
        this(false);
    }

    private SwitchButton(boolean active) {
        this(active, "active", "off");
    }

    public SwitchButton(boolean active, String enabledText, String disabledText) {
        this(active, enabledText, disabledText, null);
    }

    private SwitchButton(boolean active, String enabledText, String disabledText, Node node) {
        super(active ? enabledText : disabledText, node);
        this.active = active;
        this.activeLabel = enabledText;
        this.inActiveLabel = disabledText;
        this.multipleEventsHandler = new MultipleEventsHandler();
        this.multipleEventsHandler.addEventHandler(changeStateAndTextOnClick());
        this.setOnAction(multipleEventsHandler);
        this.switchModeEnabled = true;
    }

    private EventHandler<ActionEvent> changeStateAndTextOnClick() {
        return e -> {
            if (switchModeEnabled) {
                SwitchButton.this.active = !active;
                SwitchButton.this.setButtonLabel();
                SwitchButton.this.setStyleByStatus();
            }
        };
    }

    private void setButtonLabel() {
        if (active) super.setText(activeLabel);
        else super.setText(inActiveLabel);
    }

    private void setStyleByStatus() {
        if (active) super.setStyle(activeStyle);
        else super.setStyle(inActiveStyle);
    }

    public String getActiveLabel() {
        return activeLabel;
    }

    public void setActiveLabel(String activeLabel) {
        this.activeLabel = activeLabel;
    }

    public String getInActiveLabel() {
        return inActiveLabel;
    }

    public void setInActiveLabel(String inActiveLabel) {
        this.inActiveLabel = inActiveLabel;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        setToSwitchModeLabelAndStyle();
    }

    public void setToSwitchModeLabelAndStyle() {
        setButtonLabel();
        setStyleByStatus();
    }

    public void setSwitchModeEnabled(boolean switchModeEnabled) {
        this.switchModeEnabled = switchModeEnabled;
    }

    public void setButtonFocused(boolean focused) {
        this.setFocused(focused);
    }

    public void setActiveStyle(String activeStyle) {
        this.activeStyle = activeStyle;
    }

    public void setInActiveStyle(String inActiveStyle) {
        this.inActiveStyle = inActiveStyle;
    }

    public MultipleEventsHandler getMultipleEventsHandler() {
        return multipleEventsHandler;
    }
}
