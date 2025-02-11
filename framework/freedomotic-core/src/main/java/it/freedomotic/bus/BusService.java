/**
 *
 * Copyright (c) 2009-2013 Freedomotic team
 * http://freedomotic.com
 *
 * This file is part of Freedomotic
 *
 * This Program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General  License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This Program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General  License for more details.
 *
 * You should have received a copy of the GNU General  License
 * along with Freedomotic; see the file COPYING.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package it.freedomotic.bus;

import it.freedomotic.api.EventTemplate;
import it.freedomotic.reactions.Command;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

/**
 * 
 * Bus services interface
 * <p>
 * Provides convenience methods for sending (also replying) messages.
 * 
 * @author Freedomotic Team
 *
 */
public interface BusService {
	
	/**
	 * Sends a command to the bus
	 * 
	 * @param command The command to send
	 * @return the Command sent
	 */
	Command send(final Command command);
	
	/**
	 * Sends a command reply to the bus
	 * 
	 * @param command The command to send
	 * @param destination The destination
	 * @param correlationID The correlationID
	 */
	// TODO Think about changing the needed of a javax.jms.Destination?
	void reply(Command command, Destination destination, String correlationID);
	
	/**
	 * Sends an event to the bus
	 * 
	 * @param ev The EventTemplate to send
	 */
	void send(EventTemplate ev);
	
	/**
	/**
	 * Sends an event to the bus on queue 
	 * 
	 * @param ev The EventTemplate to send
	 * @param to The queueName used to send
	 */
	void send(final EventTemplate ev, final String toQueueName);

	/**
	 * Life cycle method used internally. Do not invoke from plugins
	 */
	void destroy();

	/**
	 * Life cycle method used internally. Do not invoke from plugins
	 */
	void init();

	/**
	 * Convenience method used by {@link BusMessagesListener} 
	 */
	BusDestination registerCommandQueue(String queueName) throws JMSException;

	/**
	 * Convenience method used by {@link BusMessagesListener} 
	 */
	BusDestination registerEventQueue(String queueName) throws JMSException;

	/**
	 * Convenience method used by {@link BusMessagesListener} 
	 */
	BusDestination registerTopic(String queueName) throws JMSException;

	/**
	 * Convenience method used by {@link BusMessagesListener} 
	 */
	Session getReceiveSession();

	/**
	 * Convenience method used by {@link BusMessagesListener} 
	 */
	Session getSendSession();

	/**
	 * Convenience method used by {@link BusMessagesListener} 
	 */
	Session getUnlistenedSession();
}
