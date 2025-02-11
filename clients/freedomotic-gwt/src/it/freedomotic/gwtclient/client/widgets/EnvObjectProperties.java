package it.freedomotic.gwtclient.client.widgets;

import it.freedomotic.model.object.EnvObject;

import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.PopupPanel;

public class EnvObjectProperties extends DecoratedPopupPanel {

    public EnvObjectProperties(EnvObject obj) {
        super(true);

        setWidget(new EnvObjectWidget(obj));
    }
}
