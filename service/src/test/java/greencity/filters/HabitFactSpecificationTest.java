package greencity.filters;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import greencity.entity.*;
import greencity.entity.HabitFactTranslation_;
import greencity.entity.HabitFact_;
import greencity.entity.Habit_;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

public class HabitFactSpecificationTest {

    @Mock private CriteriaBuilder criteriaBuilder;
    @Mock private CriteriaQuery<?> criteriaQuery;
    @Mock private Root<HabitFact> root;
    @Mock private Predicate mockPredicate;
    @Mock private Predicate allPredicate;
    @Mock private Predicate criteriaPredicate;
    @Mock private Predicate anotherPredicate;
    @Mock private Predicate finalCombinedPredicate;


    private HabitFactSpecification specification;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testToPredicate_SearchById() {
        SearchCriteria searchCriteria = new SearchCriteria(1, "id", "id");
        specification = new HabitFactSpecification(List.of(searchCriteria));

        Path idPath = mock(Path.class);
        when(root.get("id")).thenReturn(idPath);
        when(criteriaBuilder.equal(idPath, 1)).thenReturn(criteriaPredicate);
        when(criteriaBuilder.conjunction()).thenReturn(allPredicate);
        when(criteriaBuilder.and(allPredicate, criteriaPredicate)).thenReturn(mockPredicate);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        verify(criteriaBuilder).equal(idPath, 1);
        verify(criteriaBuilder).and(allPredicate, criteriaPredicate);
        verify(criteriaBuilder).conjunction();
        assertEquals(mockPredicate, result);
    }

    @Test
    void testToPredicate_SearchByHabitId() {
        SearchCriteria searchCriteria = new SearchCriteria(2, "habitId", "habitId");
        specification = new HabitFactSpecification(List.of(searchCriteria));

        Join<HabitFact, Habit> habitJoin = mock(Join.class);
        when(root.join(HabitFact_.habit)).thenReturn(habitJoin);
        when(criteriaBuilder.equal(habitJoin.get(Habit_.id), 2)).thenReturn(criteriaPredicate);
        when(criteriaBuilder.conjunction()).thenReturn(allPredicate);
        when(criteriaBuilder.and(allPredicate, criteriaPredicate)).thenReturn(mockPredicate);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        verify(criteriaBuilder).equal(habitJoin.get(Habit_.id), 2);
        verify(criteriaBuilder).and(allPredicate, criteriaPredicate);
        verify(criteriaBuilder).conjunction();
        assertEquals(mockPredicate, result);
    }

    @Test
    void testToPredicate_SearchByContent() {
        SearchCriteria searchCriteria = new SearchCriteria("test content", "content", "content");
        specification = new HabitFactSpecification(List.of(searchCriteria));

        when(criteriaBuilder.conjunction()).thenReturn(allPredicate);

        Root<HabitFactTranslation> habitFactTranslationRoot = mock(Root.class);
        when(criteriaQuery.from(HabitFactTranslation.class)).thenReturn(habitFactTranslationRoot);

        Path idPath = mock(Path.class);
        when(root.get(anyString())).thenReturn(idPath);
        when(habitFactTranslationRoot.get(HabitFactTranslation_.habitFact)).thenReturn(idPath);
        when(idPath.get(HabitFact_.id)).thenReturn(idPath);
        when(root.get(HabitFact_.id)).thenReturn(idPath);
        when(habitFactTranslationRoot.get(searchCriteria.getType())).thenReturn(idPath);
        when(idPath.get(searchCriteria.getKey())).thenReturn(idPath);
        when(criteriaBuilder.equal(habitFactTranslationRoot.get(searchCriteria.getType()).get(searchCriteria.getKey()), idPath))
                .thenReturn(criteriaPredicate);
        when(criteriaBuilder.like(habitFactTranslationRoot.get(searchCriteria.getType()), "%" + searchCriteria.getValue() + "%"))
                .thenReturn(criteriaPredicate);
        when(criteriaBuilder.and(allPredicate, criteriaPredicate)).thenReturn(mockPredicate);
        when(criteriaBuilder.equal(any(), any())).thenReturn(anotherPredicate);
        when(criteriaBuilder.and(criteriaPredicate, anotherPredicate)).thenReturn(finalCombinedPredicate);
        when(criteriaBuilder.and(allPredicate, finalCombinedPredicate)).thenReturn(mockPredicate);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        verify(criteriaBuilder).like(habitFactTranslationRoot.get(searchCriteria.getType()), "%" + searchCriteria.getValue() + "%");
        verify(criteriaBuilder).equal(habitFactTranslationRoot.get(searchCriteria.getType()).get(searchCriteria.getKey()), idPath);
        verify(criteriaBuilder).and(criteriaPredicate, anotherPredicate);
        verify(criteriaBuilder).and(allPredicate, finalCombinedPredicate);
        verify(criteriaBuilder).conjunction();
        assertEquals(result, mockPredicate);
        assertNotEquals(result, criteriaPredicate);
        assertNotEquals(result, allPredicate);
        assertNotEquals(result, anotherPredicate);
        assertNotEquals(result, finalCombinedPredicate);
    }

    @Test
    void testToPredicate_EmptyContent() {
        SearchCriteria searchCriteria = new SearchCriteria("", "content", "string");
        specification = new HabitFactSpecification(List.of(searchCriteria));

        when(criteriaBuilder.conjunction()).thenReturn(mockPredicate);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        verify(criteriaBuilder, times(0)).like((Expression<String>) any(), (Expression<String>) any());
        verify(criteriaBuilder, times(0)).equal(any(), any());
        assertNotNull(result);
    }

    @Test
    void testToPredicate_CombinationOfSearchCriteria() {
        SearchCriteria searchCriteriaId = new SearchCriteria(1, "id", "id");
        SearchCriteria searchCriteriaHabitId = new SearchCriteria(2, "habitId", "habitId");
        SearchCriteria searchCriteriaContent = new SearchCriteria("test", "content", "content");

        specification = new HabitFactSpecification(List.of(searchCriteriaId, searchCriteriaHabitId, searchCriteriaContent));

        when(criteriaBuilder.conjunction()).thenReturn(allPredicate);
        when(criteriaBuilder.and(any(), any())).thenReturn(allPredicate);

        Join<HabitFact, Habit> habitJoin = mock(Join.class);
        when(root.join(HabitFact_.habit)).thenReturn(habitJoin);
        Root<HabitFactTranslation> habitFactTranslationRoot = mock(Root.class);
        when(criteriaQuery.from(HabitFactTranslation.class)).thenReturn(habitFactTranslationRoot);
        Path idPath = mock(Path.class);
        when(root.get(searchCriteriaId.getKey())).thenReturn(idPath);


        when(criteriaBuilder.equal(idPath, searchCriteriaId.getValue())).thenReturn(criteriaPredicate);
        when(criteriaBuilder.equal(habitJoin.get(Habit_.id), searchCriteriaHabitId.getValue()))
                .thenReturn(criteriaPredicate);

        when(root.get(anyString())).thenReturn(idPath);
        when(habitFactTranslationRoot.get(HabitFactTranslation_.habitFact)).thenReturn(idPath);
        when(idPath.get(HabitFact_.id)).thenReturn(idPath);
        when(root.get(HabitFact_.id)).thenReturn(idPath);
        when(habitFactTranslationRoot.get(searchCriteriaContent.getType())).thenReturn(idPath);
        when(idPath.get(searchCriteriaContent.getKey())).thenReturn(idPath);
        when(criteriaBuilder.equal(habitFactTranslationRoot.get(searchCriteriaContent.getType()).get(searchCriteriaContent.getKey()), idPath))
                .thenReturn(criteriaPredicate);
        when(criteriaBuilder.like(habitFactTranslationRoot.get(searchCriteriaContent.getType()), "%" + searchCriteriaContent.getValue() + "%"))
                .thenReturn(criteriaPredicate);
        when(criteriaBuilder.and(allPredicate, criteriaPredicate)).thenReturn(mockPredicate);
        when(criteriaBuilder.equal(any(), any())).thenReturn(anotherPredicate);
        when(criteriaBuilder.and(criteriaPredicate, anotherPredicate)).thenReturn(finalCombinedPredicate);
        when(criteriaBuilder.and(allPredicate, finalCombinedPredicate)).thenReturn(mockPredicate);

        Predicate result = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        verify(criteriaBuilder).equal(root.get(HabitFact_.id), searchCriteriaId.getValue());
        verify(criteriaBuilder).equal(habitJoin.get(Habit_.id), searchCriteriaHabitId.getValue());
        verify(criteriaBuilder).like(habitFactTranslationRoot.get(HabitFactTranslation_.content), "%" + searchCriteriaContent.getValue() + "%");
        verify(criteriaBuilder).and(criteriaPredicate, anotherPredicate);
        verify(criteriaBuilder).and(allPredicate, finalCombinedPredicate);
        verify(criteriaBuilder).conjunction();
        assertEquals(result, mockPredicate);
        assertNotEquals(result, criteriaPredicate);
        assertNotEquals(result, allPredicate);
        assertNotEquals(result, anotherPredicate);
        assertNotEquals(result, finalCombinedPredicate);
    }
}