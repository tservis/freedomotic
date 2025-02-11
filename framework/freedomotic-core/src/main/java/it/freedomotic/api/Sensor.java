/**
 *
 * Copyright (c) 2009-2013 Freedomotic team http://freedomotic.com
 *
 * This file is part of Freedomotic
 *
 * This Program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * This Program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Freedomotic; see the file COPYING. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package it.freedomotic.api;

import it.freedomotic.app.Freedomotic;
import it.freedomotic.bus.BusConsumer;
import it.freedomotic.bus.BusService;
import it.freedomotic.bus.BusMessagesListener;
import it.freedomotic.exceptions.UnableToExecuteException;
import it.freedomotic.security.Auth;

import java.io.IOException;
import java.util.logging.Logger;

import javax.jms.ObjectMessage;

import com.google.inject.Inject;

/**
 *
 * @author Enrico Nicoletti
 */
@Deprecated
public abstract class Sensor extends Plugin implements Runnable, BusConsumer {

	private static final Logger LOG = Logger.getLogger(Sensor.class.getName());

	private static final String DEFAULT_QUEUE_PREFIX = "app.sensor.";
    private static final String SENSORS_QUEUE_DOMAIN = "app.sensor.";
    
    @Inject
    private Auth auth;

    private BusService busService;
    
    private boolean isPollingSensor = true;
    
    //TODO Check why is initialized
    private BusMessagesListener listener;

    protected abstract void onInformationRequest(/*TODO: define parameters*/) throws IOException, UnableToExecuteException;

    protected abstract void onRun(); //you can override public void run() anyway

    public Sensor(String pluginName, String manifest) {
        super(pluginName, manifest);
		this.busService = Freedomotic.INJECTOR.getInstance(BusService.class);
        register();
        setAsNotPollingSensor();
    }

    private void register() {
    	listener = new BusMessagesListener(this);
    }

    public String listenMessagesOn() {
        String defaultQueue = DEFAULT_QUEUE_PREFIX + category + "." + shortName;
        String fromFile = SENSORS_QUEUE_DOMAIN + listenOn;

        if (listenOn.equalsIgnoreCase("undefined")) {
            listenOn = defaultQueue;

            return listenOn;
        } else {
            return fromFile;
        }
    }

    public void notifyEvent(EventTemplate ev) {
        if (isRunning) {
            notifyEvent(ev,
                    ev.getDefaultDestination());
        }
    }

    public void notifyEvent(EventTemplate ev, String destination) {
        if (isRunning) {
            LOG.fine("Sensor " + this.getName() + " notify event " + ev.getEventName() + ":"
                    + ev.getPayload().toString());
            busService.send(ev, destination);
        }
    }

    @Override
    public void start() {
        if (!isRunning) {
            Runnable action = new Runnable() {
                @Override
                public void run() {
                    Thread thread = new Thread(this);
                    thread.setName("Thread-" + getClass().getSimpleName());
                    onStart();
                    if (isPollingSensor()) {
                        //enters in run only if is required
                        thread.start();
                    }
                    isRunning = true;
                }
            };
            auth.pluginExecutePrivileged(this, action);
        }
    }

    @Override
    public void stop() {
        if (isRunning) {
            Runnable action = new Runnable() {
                @Override
                public void run() {
                    isRunning = false;
                    onStop();
                }
            };
            auth.pluginExecutePrivileged(this, action);
        }
    }

    @Override
    public void run() {
        //onStart();
        while (isRunning && isPollingSensor) {
            onRun();
        }

        //onStop();
    }

    public final boolean isPollingSensor() {
        return isPollingSensor;
    }

    public final void setAsNotPollingSensor() {
        this.isPollingSensor = false;
    }

    public final void setAsPollingSensor() {
        this.isPollingSensor = true;
    }

    @Override
    public void onMessage(ObjectMessage ev) {
        LOG.severe("Sensor class have received a message");

//        if (isRunning) {
//            if (ev instanceof QueryResult) {
//                try {
//                    QueryResult ir = (QueryResult) ev;
//                    onInformationRequest();
//                    ev.setExecuted(true);
//                    queryBus.replyTo(ev);
//                } catch (IOException ex) {
//                    Logger.getLogger(Sensor.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (UnableToExecuteException ex) {
//                    Logger.getLogger(Sensor.class.getName()).log(Level.SEVERE, null, ex);
//                    ev.setExecuted(false);
//                }
//            }
//        }
    }
}
