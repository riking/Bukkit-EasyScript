package org.cyberiantiger.minecraft.easyscript.unsafe;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

public interface EventRegistration extends Listener, EventExecutor {
	public Class<? extends Event> getEventClass();
	public EventPriority getPriority();
	public boolean ignoresCancelled();
	public String getCallbackName();
	public String getCallbackScriptLanguage();
	
	/**
	 * Registers this EventRegistration to the Event.
	 */
	public void activate();

	/**
	 * Removes this EventRegistration from the event.
	 */
	public void deactivate();
	
	/**
	 * If active is false, all events received will be discarded
	 * @return
	 */
	public boolean active();
	
	/**
	 * Executes the Event
	 */
	public void execute(Listener listener, Event event) throws EventException;
}
