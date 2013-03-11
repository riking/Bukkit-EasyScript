package org.cyberiantiger.minecraft.easyscript.unsafe;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

public class EventRegistrationFactory {
	private HashMap<String, EventRegistration> eventMap = new HashMap<String, EventRegistration>();

	public static EventRegistrationFactory createEventRegistrationFactory()
	{
		return new EventRegistrationFactory();
	}
	
	public Map<String, EventRegistration> getAllEvents() {
		return Collections.unmodifiableMap(eventMap);
	}
	
	public void unregisterAllEvents() {
		for (EventRegistration e : eventMap.values())
		{
			if (!e.active())
				e.deactivate();
		}
		eventMap.clear();
	}
    /**
     * Register an event handler.
     *
     * Uses EventPriority.NORMAL and ignores cancelled events.
     *
     * @param eventClass The event to register the handler for.
     * @param function The function to call to handle this event.
     */
    public EventRegistration registerEvent(Class<? extends Event> eventClass, String function) {
        return registerEvent(eventClass, EventPriority.NORMAL, function);
    }

    /**
     * Register an event handler.
     *
     * Ignores cancelled events.
     *
     * @param eventClass The event to register the handler for.
     * @param priority The priority of the event handler.
     * @param function The function to call to handle this event.
     */
    public EventRegistration registerEvent(Class<? extends Event> eventClass, EventPriority priority, String function) {
        return registerEvent(eventClass, priority, true, function);
    }

    /**
     * Register an event handler.
     * You may not register two events to the same function.
     *
     * @throws IllegalArgumentException This function callback already exists
     * @param eventClass The event to register the handler for.
     * @param priority The priority of the event handler.
     * @param ignoreCancelled Whether the handler should be passed cancelled events.
     * @param function The function to call to handle this event.
     * @return the EventRegistration, a Java event handler
     */
    public EventRegistration registerEvent(Class<? extends Event> eventClass, EventPriority priority, boolean ignoreCancelled, final String function) {
    	EventRegistration reg = eventMap.get(function);
    	if (reg != null)
    	{
    		throw new IllegalArgumentException(function + " already has an event registered");
    	}
    	reg = new EventRegistrationBukkit(eventClass, priority, ignoreCancelled, function, "javascript");
    	reg.activate();
    	eventMap.put(function, reg);
    	return reg;
    }
    /**
     * Unregister an event handler.
     *
     * @param function The function you provided during registration.
     */
    public void unregisterEvent(String function) {
    	eventMap.get(function).deactivate();
    	eventMap.remove(function);
    	return;
    }
}
