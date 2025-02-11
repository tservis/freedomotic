/**
 *
 * Copyright (c) 2009-2013 Freedomotic team
 * http://freedomotic.com
 *
 * This file is part of Freedomotic
 *
 * This Program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This Program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Freedomotic; see the file COPYING.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
 package it.freedomotic.events;

import it.freedomotic.api.EventTemplate;

import it.freedomotic.objects.BehaviorLogic;
import it.freedomotic.objects.EnvObjectLogic;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * Channel <b>app.event.sensor.object.behavior.change</b> informs that an object
 * has changed its behavior (eg: a light change behavior from off to on).
 *
 * Available tokens for triggers:
 *
 * @see it.freedomotic.api.EventTemplate for properties like date, time, sender
 * which are common to all events
 * @see it.freedomotic.object.EnvObjectLogic#getExposedProperties() for object
 * data
 *
 * @author enrico
 */
public class ObjectHasChangedBehavior extends EventTemplate {

    private static final long serialVersionUID = 6892968576173017195L;

	//private EnvObject obj;
    public ObjectHasChangedBehavior(Object source, EnvObjectLogic obj) {
        super(source);

        //add default object properties
        Iterator<Entry<String,String>> it = obj.getExposedProperties().entrySet().iterator();
        while (it.hasNext()) {
            Entry<String,String> entry = it.next();
            payload.addStatement(entry.getKey().toString(), entry.getValue().toString());
        }

        //add the list of changed behaviors
        payload.addStatement("object.currentRepresentation",
                obj.getPojo().getCurrentRepresentationIndex());

        for (BehaviorLogic behavior : obj.getBehaviors()) {
            if (behavior.isChanged()) {
                payload.addStatement("object.behavior." + behavior.getName(),
                        behavior.getValueAsString());
                behavior.setChanged(false);
            }
        }
    }

    @Override
    protected void generateEventPayload() {
    }

    @Override
    public String getDefaultDestination() {
        return "app.event.sensor.object.behavior.change";
    }
    private static final Logger LOG = Logger.getLogger(ObjectHasChangedBehavior.class.getName());
}
