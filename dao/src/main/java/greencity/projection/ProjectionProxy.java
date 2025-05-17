package greencity.projection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

public class ProjectionProxy {
    private ProjectionProxy() {
    }

    public static <T> T createProjection(Class<T> projectionInterface, Map<String, Object> values) {
        InvocationHandler handler = (ignoredProxy, method, args) -> {
            String methodName = method.getName();
            if (methodName.startsWith("get") && args == null) {
                String property = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
                return values.get(property);
            }
            throw new UnsupportedOperationException("Method not supported: " + methodName);
        };

        return projectionInterface.cast(Proxy.newProxyInstance(
            projectionInterface.getClassLoader(),
            new Class<?>[] {projectionInterface},
            handler
        ));
    }
}
