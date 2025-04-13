package greencity.specification;

import greencity.annotations.RatingCalculationEnum;
import greencity.entity.RatingStatistics;
import greencity.entity.User;
import greencity.filters.RatingStatisticsSpecification;
import greencity.filters.SearchCriteria;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingStatisticsSpecificationTest {

    @Mock
    Root<RatingStatistics> root;

    @Mock
    CriteriaQuery<?> query;

    @Mock
    CriteriaBuilder cb;

    @Mock
    Predicate predicate;

    @Mock
    Path<Object> path;

    @Mock
    Join<RatingStatistics, User> userJoin;

    RatingStatisticsSpecification specification;

    @Test
    void shouldBuildPredicateById_WhenIdIsProvided() {

        var criteria = SearchCriteria
                .builder()
                .value(10L)
                .key("id")
                .type("id")
                .build();

        specification = new RatingStatisticsSpecification(List.of(criteria));

        when(root.get("id")).thenReturn(path);
        when(cb.equal(path, 10L)).thenReturn(predicate);

        specification.toPredicate(root, query, cb);
        verify(cb).equal(path, 10L);
    }

    @Test
    void shouldBuildPredicateByEnum_WhenEnumValueProvided() {

        var criteria = SearchCriteria
                .builder()
                .value("ADD_COMME")
                .key("ratingCalculationEnum")
                .type("enum")
                .build();

        specification = new RatingStatisticsSpecification(List.of(criteria));

        when(root.get("ratingCalculationEnum")).thenReturn(path);
        when(cb.equal(path, RatingCalculationEnum.ADD_COMMENT)).thenReturn(predicate);
        when(cb.disjunction()).thenReturn(predicate);
        when(cb.or(predicate, predicate)).thenReturn(predicate);

        specification.toPredicate(root, query, cb);
        verify(cb, atLeastOnce()).equal(path, RatingCalculationEnum.ADD_COMMENT);
    }

    @Test
    void shouldBuildPredicateByUserId_WhenUserIdIsValid() {

        var criteria = SearchCriteria
                .builder()
                .value(5L)
                .key("userId")
                .type("userId")
                .build();

        specification = new RatingStatisticsSpecification(List.of(criteria));

        when(root.join(RatingStatistics_.user)).thenReturn(userJoin);

        Path<Long> longPath = mock(Path.class);

        when(userJoin.get(User_.id)).thenReturn(longPath);
        when(cb.equal(longPath, 5L)).thenReturn(predicate);

        specification.toPredicate(root, query, cb);
        verify(cb).equal(longPath, 5L);
    }

    @Test
    void shouldReturnConjunction_WhenUserIdIsEmptyString() {

        var criteria = SearchCriteria
                .builder()
                .value("")
                .key("userId")
                .type("userId")
                .build();

        specification = new RatingStatisticsSpecification(List.of(criteria));

        when(root.join(RatingStatistics_.user)).thenReturn(userJoin);

        Predicate conjunction = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(conjunction);

        specification.toPredicate(root, query, cb);

        verify(cb).conjunction();
    }

    @Test
    void shouldBuildLikePredicate_WhenUserEmailProvided() {

        var criteria = SearchCriteria
                .builder()
                .value("test@example.com")
                .key("userMail")
                .type("userMail")
                .build();

        specification = new RatingStatisticsSpecification(List.of(criteria));

        when(root.join(RatingStatistics_.user)).thenReturn(userJoin);

        Path<String> emailPath = mock(Path.class);
        when(userJoin.get(User_.email)).thenReturn(emailPath);
        when(cb.like(emailPath, "%test@example.com%")).thenReturn(predicate);

        specification.toPredicate(root, query, cb);
        verify(cb).like(emailPath, "%test@example.com%");
    }

    @Test
    void shouldBuildCombinedPredicate_WhenMultipleCriteriaProvided() {

        var idCriteria = SearchCriteria
                .builder()
                .type("id")
                .key("id")
                .value(1L)
                .build();

        var pointsChangedCriteria = SearchCriteria
                .builder()
                .type("pointsChanged")
                .key("pointsChanged")
                .value(10)
                .build();

        specification = new RatingStatisticsSpecification(List.of(idCriteria, pointsChangedCriteria));

        var idPath = mock(Path.class);
        var pointsPath = mock(Path.class);

        Predicate p1 = mock(Predicate.class);
        Predicate p2 = mock(Predicate.class);

        Predicate andResult = mock(Predicate.class);

        when(root.get("id")).thenReturn(idPath);
        when(cb.equal(idPath, 1L)).thenReturn(p1);

        when(root.get("pointsChanged")).thenReturn(pointsPath);
        when(cb.equal(pointsPath, 10)).thenReturn(p2);

        when(cb.and(any(), eq(p1))).thenReturn(p1);
        when(cb.and(eq(p1), eq(p2))).thenReturn(andResult);

        Predicate result = specification.toPredicate(root, query, cb);
        Assertions.assertEquals(andResult, result);
    }

    public static class RatingStatistics_ {
        public static final SingularAttribute<RatingStatistics, User> user = null;
    }

    public static class User_ {
        public static final SingularAttribute<User, Long> id = null;
        public static final SingularAttribute<User, String> email = null;
    }
}