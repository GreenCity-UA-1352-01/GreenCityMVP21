package greencity.notification;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class NotificationHandlerRegistry {
    private final Map<Method, NotificationEventFactory> registry = new HashMap<>();

    public void register(Method method, NotificationEventFactory factory) {
        if (registry.containsKey(method)) {
            throw new IllegalStateException("Handler for method " + method + " is already registered: "
                + registry.get(method).getClass().getName());
        }
        registry.put(method, factory);
    }

    public NotificationEventFactory getFactory(Method method) {
        return registry.get(method);
    }
}
