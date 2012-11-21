package uk.ac.rdg.resc.godiva.client.widgets;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

public class OpacitySelector extends BaseSelector {
    private ListBox opacity;
    
    public OpacitySelector(ChangeHandler handler) {
        super("Opacity");
        opacity = new ListBox();
        opacity.addItem("25%");
        opacity.addItem("50%");
        opacity.addItem("75%");
        opacity.addItem("100%");
        opacity.setSelectedIndex(3);
        opacity.addChangeHandler(handler);
        opacity.setTitle("Select the opacity of the layer");
        add(opacity);
    }

    public void setEnabled(boolean enabled){
        opacity.setEnabled(enabled);
        if(!opacity.isEnabled()){
            label.addStyleDependentName("inactive");
        } else {
            label.removeStyleDependentName("inactive");
        }
    }
    
    public float getOpacity() {
        return (opacity.getSelectedIndex()+1)/4.0f;
    }
    
    public void setOpacity(float opacity) {
        this.opacity.setSelectedIndex((int)((opacity * 4) - 1));
    }
}