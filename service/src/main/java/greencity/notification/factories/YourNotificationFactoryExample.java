package greencity.notification.factories;

//import greencity.dto.notification.NotificationRequestDto;
//import greencity.dto.user.UserVO;
//import greencity.notification.NotificationEventFactory;
//import java.lang.reflect.Method;
//import java.time.ZonedDateTime;
//import org.springframework.stereotype.Component;

//@Component
//public class YourNotificationFactoryExample implements NotificationEventFactory {
//    @Override
//    public boolean supports(Method method) {
//        return method.getDeclaringClass() instanceof YourControllerClassExample.class
//            && method.getName().equals("methodInYourControllerExample");
//    }
//
//    @Override
//    public NotificationRequestDto createEvent(Object[] args) {
//        // for example
//        UserVO user = (UserVO) args[2];
//
//        return NotificationRequestDto.builder()
//            .action((String) args[0])
//            .objectName((String) args[1])
//            .creationDate(ZonedDateTime.now())
//            .status("........")
//            .receiverId(user.getId())
//            .initiatorId((Long) args[3])
//            .build();
//    }
//}
