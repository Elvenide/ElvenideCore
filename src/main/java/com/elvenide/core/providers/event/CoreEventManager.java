package com.elvenide.core.providers.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;

/// Used internally to manage CoreEvents and their handlers.
class CoreEventManager {

    private static final HashMap<Class<? extends CoreEvent>, EnumMap<CoreEventPriority, ArrayList<CoreEventExecutor>>> handlers = new HashMap<>();
    private static final HashMap<CoreCancellable, Boolean> cancellations = new HashMap<>();

    static void cancel(CoreCancellable coreCancellable, boolean cancelled) {
        cancellations.put(coreCancellable, cancelled);
    }

    @SuppressWarnings("unchecked")
    private static HashMap<Class<? extends CoreEvent>, EnumMap<CoreEventPriority, ArrayList<CoreEventExecutor>>> getHandlers(CoreListener listener) {
        HashMap<Class<? extends CoreEvent>, EnumMap<CoreEventPriority, ArrayList<CoreEventExecutor>>> localHandlers = new HashMap<>();

        for (Method method : listener.getClass().getDeclaredMethods()) {
            CoreEventHandler annotation = method.getAnnotation(CoreEventHandler.class);
            if (annotation == null)
                continue;

            if (method.getParameterCount() != 1)
                throw new IllegalStateException("Your CoreListener method " + method.getName() + " must have exactly one parameter, the event object.");

            if (!CoreEvent.class.isAssignableFrom(method.getParameterTypes()[0]))
                throw new IllegalStateException("Your CoreListener method " + method.getName() + " must have an object implementing CoreEvent as its parameter.");

            if (!method.trySetAccessible())
                throw new IllegalStateException("Failed to make your CoreListener method " + method.getName() + " accessible to ElvenideCore.");

            CoreEventExecutor executor = new CoreEventExecutor() {
                @Override
                public CoreEventHandler getData() {
                    return annotation;
                }

                @Override
                public CoreListener getListener() {
                    return listener;
                }

                @Override
                public void accept(CoreEvent event) {
                    try {
                        method.invoke(listener, method.getParameterTypes()[0].cast(event));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            };

            Class<? extends CoreEvent> eventClass = (Class<? extends CoreEvent>) method.getParameterTypes()[0];
            localHandlers.computeIfAbsent(eventClass, k -> new EnumMap<>(CoreEventPriority.class))
                .computeIfAbsent(annotation.priority(), k -> new ArrayList<>()).add(executor);
        }

        return localHandlers;
    }

    public static void register(CoreListener listener) {
        HashMap<Class<? extends CoreEvent>, EnumMap<CoreEventPriority, ArrayList<CoreEventExecutor>>> localHandlers = getHandlers(listener);

        for (Class<? extends CoreEvent> eventClass : localHandlers.keySet()) {
            for (CoreEventPriority priority : localHandlers.get(eventClass).keySet()) {
                handlers.computeIfAbsent(eventClass, k -> new EnumMap<>(CoreEventPriority.class))
                    .computeIfAbsent(priority, k -> new ArrayList<>()).addAll(localHandlers.get(eventClass).get(priority));
            }
        }
    }

    public static void unregister(CoreListener listener) {
        for (Class<? extends CoreEvent> eventClass : handlers.keySet()) {
            for (CoreEventPriority priority : handlers.get(eventClass).keySet()) {
                ArrayList<CoreEventExecutor> executors = handlers.get(eventClass).get(priority);
                executors.removeIf(executor -> executor.getListener() == listener);
            }
        }
    }

    public static void unregisterAllWithListener(Class<? extends CoreListener> listenerClass) {
        for (Class<? extends CoreEvent> eventClass : handlers.keySet()) {
            for (CoreEventPriority priority : handlers.get(eventClass).keySet()) {
                ArrayList<CoreEventExecutor> executors = handlers.get(eventClass).get(priority);
                executors.removeIf(executor -> executor.getListener().getClass() == listenerClass);
            }
        }
    }

    public static void unregisterAll() {
        handlers.clear();
    }

    private static boolean execute(CoreEventExecutor executor, CoreEvent event, boolean cancelled) {
        if (cancelled && executor.getData().ignoreCancelled())
            return cancelled;

        executor.accept(event);
        if (event instanceof CoreCancellable)
            cancelled = cancellations.getOrDefault(event, false);

        return cancelled;
    }

    public static boolean call(CoreEvent event) {
        if (!handlers.containsKey(event.getClass()))
            return false;

        boolean cancelled = false;
        EnumMap<CoreEventPriority, ArrayList<CoreEventExecutor>> eventExecutors = handlers.get(event.getClass());

        // EARLIEST
        for (CoreEventExecutor executor : new ArrayList<>(eventExecutors.getOrDefault(CoreEventPriority.EARLIEST, new ArrayList<>())))
            cancelled = execute(executor, event, cancelled);

        // EARLY
        for (CoreEventExecutor executor : new ArrayList<>(eventExecutors.getOrDefault(CoreEventPriority.EARLY, new ArrayList<>())))
            cancelled = execute(executor, event, cancelled);

        // NORMAL
        for (CoreEventExecutor executor : new ArrayList<>(eventExecutors.getOrDefault(CoreEventPriority.NORMAL, new ArrayList<>())))
            cancelled = execute(executor, event, cancelled);

        // LATE
        for (CoreEventExecutor executor : new ArrayList<>(eventExecutors.getOrDefault(CoreEventPriority.LATE, new ArrayList<>())))
            cancelled = execute(executor, event, cancelled);

        // LATEST
        for (CoreEventExecutor executor : new ArrayList<>(eventExecutors.getOrDefault(CoreEventPriority.LATEST, new ArrayList<>())))
            cancelled = execute(executor, event, cancelled);

        // RESULT
        for (CoreEventExecutor executor : new ArrayList<>(eventExecutors.getOrDefault(CoreEventPriority.RESULT, new ArrayList<>())))
            execute(executor, event, cancelled);

        // Clear cancellations for this event
        if (event instanceof CoreCancellable)
            cancellations.remove(event);

        return cancelled;
    }

}
