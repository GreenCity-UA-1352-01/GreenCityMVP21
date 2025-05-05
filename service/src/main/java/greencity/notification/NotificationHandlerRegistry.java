package greencity.notification;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class NotificationHandlerRegistry {
    private final Map<Method, NotificationEventFactory> registry = new HashMap<>();

    /**
     * Registers a handler for the given method. Only one handler can be registered for each method.
     *
     * @param method  method to register
     * @param factory factory to register
     * @throws IllegalStateException if there is already a handler for the given method
     * @author Roman Diakov & Rostyslav Zadyraichuk
     */
    public void register(Method method, NotificationEventFactory factory) {
        if (registry.containsKey(method)) {
            throw new IllegalStateException("Handler for method " + method + " is already registered: "
                + registry.get(method).getClass().getName());
        }
        registry.put(method, factory);
    }

    /**
     * Returns the factory for the given method.
     *
     * @param method method to find the factory for
     * @return factory for the given method
     * @throws IllegalStateException if there is no factory for the given method
     * @author Roman Diakov & Rostyslav Zadyraichuk
     */
    public NotificationEventFactory getFactory(Method method) {
        return registry.get(method);
    }
}
