package greencity.repository;

import greencity.entity.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class EventSearchRepo {
    private final EntityManager entityManager;
    private final CriteriaBuilder cb;

    public EventSearchRepo(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.cb = entityManager.getCriteriaBuilder();
    }

    public Page<Event> find(Pageable pageable, String searchText) {
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);

        Predicate predicate = buildPredicate(searchText, root);
        query.select(root).distinct(true).where(predicate);

        TypedQuery<Event> typedQuery = entityManager.createQuery(query)
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                .setMaxResults(pageable.getPageSize());

        List<Event> resultList = typedQuery.getResultList();
        long total = countMatchingEvents(searchText);

        return new PageImpl<>(resultList, pageable, total);
    }

    private long countMatchingEvents(String searchText) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Event> root = countQuery.from(Event.class);
        countQuery.select(cb.countDistinct(root)).where(buildPredicate(searchText, root));
        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private Predicate buildPredicate(String searchText, Root<Event> root) {
        List<Predicate> predicates = Arrays.stream(searchText.split("\\s+"))
                .map(word -> cb.like(cb.lower(root.get("title")), "%" + word.toLowerCase() + "%"))
                .collect(Collectors.toList());

        return cb.or(predicates.toArray(new Predicate[0]));
    }

}
