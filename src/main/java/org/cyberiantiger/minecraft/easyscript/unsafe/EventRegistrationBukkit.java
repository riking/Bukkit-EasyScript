package org.cyberiantiger.minecraft.easyscript.unsafe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

import javax.script.ScriptException;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.SimplePluginManager;
import org.cyberiantiger.minecraft.easyscript.EasyScript;

public class EventRegistrationBukkit implements EventRegistration {

	public EventRegistrationBukkit(Class<? extends Event> eventClass,
			EventPriority priority, boolean ignoreCancelled,
			final String function, String language) {
		this.eventClass = eventClass;
		this.priority = priority;
		this.ignoreCancelled = ignoreCancelled;
		this.callback = function;
		this.scriptEngine = language;
	}

	private Class<? extends Event> eventClass;
	private EventPriority priority;
	private boolean ignoreCancelled;
	private String callback;
	private String scriptEngine;
	private RegisteredListener listener;

	private boolean active;

	public void activate() {
		if (active)
			throw new IllegalStateException("Attempt to activate EventRegistration multiple times");
		active = true;

		RegisteredListener rl = new RegisteredListener(this, this, priority,
				EasyScript.instance, ignoreCancelled);
		HandlerList handlerlist = getHandlerList(getPluginManager());
		handlerlist.register(rl);
		this.listener = rl;
	}

	public void deactivate() throws IllegalStateException {
		if (!active)
			throw new IllegalStateException("Attempt to deactivate non-active EventRegistration");
		active = false;
		getHandlerList(getPluginManager()).unregister(listener);
	}

	public Class<? extends Event> getEventClass() {
		return eventClass;
	}

	public EventPriority getPriority() {
		return priority;
	}

	public boolean ignoresCancelled() {
		return ignoreCancelled;
	}

	public String getCallbackName() {
		return callback;
	}

	public String getCallbackScriptLanguage() {
		return scriptEngine;
	}

	private PluginManager getPluginManager() {
		PluginManager pman = EasyScript.instance.getServer().getPluginManager();
		if (!(pman instanceof SimplePluginManager)) {
			EasyScript.reflectionError("EventRegistration",
					"Can't find SimplePluginManager.getEventListeners(Class)",
					new RuntimeException());
		}
		return pman;
	}

	public HandlerList getHandlerList(PluginManager pman) {
		try {
			Method method = SimplePluginManager.class.getDeclaredMethod(
					"getEventListeners", new Class[] { Class.class });
			method.setAccessible(true);
			return (HandlerList) method.invoke(pman, eventClass);
		} catch (NoSuchMethodException e) {
			EasyScript.reflectionError("getHandlerList", "Can't find SimplePluginManager.getEventListeners(Class)", e);
		} catch (SecurityException e) {
			EasyScript.reflectionError("getHandlerList", "Can't find SimplePluginManager.getEventListeners(Class)", e);
		} catch (IllegalAccessException e) {
			EasyScript.reflectionError("getHandlerList", "Can't use SimplePluginManager.getEventListeners(Class)", e);
		} catch (IllegalArgumentException e) {
			EasyScript.reflectionError("getHandlerList", "Can't use SimplePluginManager.getEventListeners(Class)", e);
		} catch (InvocationTargetException e) {
			EasyScript.reflectionError("getHandlerList", "Exception during SimplePluginManager.getEventListeners(Class)", e);
		}
		return null;
	}

	public boolean active() {
		return active;
	}

	public void execute(Listener arg0, Event event) throws EventException {
		if (!EasyScript.instance.isEnabled()) {
			return;
		}
		if (!EasyScript.instance.checkLibraries()) {
			return;
		}
		if (!active) {
			return;
		}
		try {
			EasyScript.instance.invocable
				.invokeFunction(callback, new Object[] {event} );
		} catch (ScriptException ex) {
			EasyScript.instance.getLogger().log(Level.WARNING, "Script error while handling event: " + ex.getMessage());
		} catch (NoSuchMethodException ex) {
			EasyScript.instance.getLogger().log( Level.WARNING, "Library non-existent registered event handler function: " + callback);
		}
	}
}
