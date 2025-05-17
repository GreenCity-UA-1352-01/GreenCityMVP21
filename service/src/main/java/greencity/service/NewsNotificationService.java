package greencity.service;

import greencity.entity.EcoNews;
import greencity.entity.NewsSubscription;
import greencity.repository.NewsSubscriptionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsNotificationService {
    private final NewsSubscriptionRepo newsSubscriptionRepo;
    private final EmailService emailService;

    @Async
    public void notifySubscribers(EcoNews ecoNews) {
        int pageSize = 100;
        int pageNumber = 0;
        Page<NewsSubscription> subscriptionPage;

        do {
            subscriptionPage = newsSubscriptionRepo.findAll(PageRequest.of(pageNumber, pageSize));

            for (NewsSubscription subscription : subscriptionPage.getContent()) {
                emailService.sendEmail(
                        subscription.getEmail(),
                        "New eco news!",
                        "Hello! New Eco news has been added: \"" + ecoNews.getTitle()
                            + "\".\nGo to the site to see more."
                );
            }

            pageNumber++;
        } while (subscriptionPage.hasNext());
    }
}
