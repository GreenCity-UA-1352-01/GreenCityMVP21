package greencity.service;

import greencity.entity.EcoNews;
import greencity.entity.NewsSubscription;
import greencity.repository.NewsSubscriptionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsNotificationService {
    private final NewsSubscriptionRepo newsSubscriptionRepo;
    private final EmailService emailService;

    @Async
    public void notifySubscribers(EcoNews ecoNews) {
        List<NewsSubscription> subscriptions = newsSubscriptionRepo.findAll();

        for (NewsSubscription subscription : subscriptions) {
            emailService.sendEmail(
                    subscription.getEmail(),
                    "New eco news!",
                    "Hello! New Eco news has benn added: \"" + ecoNews.getTitle() + "\".\nGo to the site to see more."
            );
        }
    }
}
