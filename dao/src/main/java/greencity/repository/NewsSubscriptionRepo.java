package greencity.repository;

import greencity.entity.NewsSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsSubscriptionRepo extends JpaRepository<NewsSubscription, Long> {
    boolean existsByEmail(String email);
}
