package greencity.filters;

import greencity.ModelUtils;
import greencity.entity.ShoppingListItem;
import greencity.entity.ShoppingListItem_;
import greencity.entity.Translation;
import greencity.entity.Translation_;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.entity.localization.ShoppingListItemTranslation_;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingListItemSpecificationTest {

    @Mock
    private Root<ShoppingListItem> root;
    @Mock
    private Root<ShoppingListItemTranslation> itemTranslationRoot;
    @Mock
    private CriteriaQuery<ShoppingListItem> criteriaQuery;
    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Predicate allPredicates;
    @Mock
    private Predicate idPredicate;
    @Mock
    private Predicate contentPredicate;
    @Mock
    private Predicate combinedPredicate;

    @Mock
    private SingularAttribute<Translation, String> contentAttribute;
    @Mock
    private SingularAttribute<ShoppingListItemTranslation, ShoppingListItem> shoppingListItemAttribute;
    @Mock
    private SingularAttribute<ShoppingListItem, Long> idAttribute;

    @Mock
    private Path<String> contentPath;
    @Mock
    private Path<ShoppingListItem> shoppingListItemPath;
    @Mock
    private Path<Long> idPath;

    private final List<SearchCriteria> searchCriteriaList = new ArrayList<>();

    @Spy
    private ShoppingListItemSpecification shoppingListItemSpecification = new ShoppingListItemSpecification(searchCriteriaList);

    @BeforeEach
    public void setUp() {
        Translation_.content = contentAttribute;
        ShoppingListItemTranslation_.shoppingListItem = shoppingListItemAttribute;
        ShoppingListItem_.id = idAttribute;

        searchCriteriaList.clear();

        when(criteriaBuilder.conjunction()).thenReturn(allPredicates);
    }

    @Test
    void testToPredicate_withEmptySearchCriteriaList() {
        Predicate actual = shoppingListItemSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertEquals(allPredicates, actual);
        verify(shoppingListItemSpecification).toPredicate(any(), any(), any());
        verify(shoppingListItemSpecification, never()).getNumericPredicate(any(), any(), any());
        verify(criteriaQuery, never()).from(ShoppingListItemTranslation.class);
    }

    @Test
    void testToPredicate_withIdSearchCriteria() {
        ShoppingListItem entity = ModelUtils.getShoppingListItem();
        SearchCriteria idSearchCriteria = getIdSearchCriteria(entity);
        searchCriteriaList.add(idSearchCriteria);

        doReturn(idPredicate).when(shoppingListItemSpecification).getNumericPredicate(
                eq(root), eq(criteriaBuilder), any(SearchCriteria.class)
        );
        when(criteriaBuilder.and(allPredicates, idPredicate)).thenReturn(idPredicate);

        Predicate actual = shoppingListItemSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertEquals(idPredicate, actual);
        verify(shoppingListItemSpecification).toPredicate(any(), any(), any());
        verify(shoppingListItemSpecification).getNumericPredicate(any(), any(), any());
        verify(criteriaQuery, never()).from(ShoppingListItemTranslation.class);
    }

    @Test
    void testToPredicate_withContentSearchCriteria_withEmptyValue() {
        ShoppingListItem entity = ModelUtils.getShoppingListItem();
        SearchCriteria contentSearchCriteria = getContentSearchCriteria(entity);
        contentSearchCriteria.setValue("");
        searchCriteriaList.add(contentSearchCriteria);

        when(criteriaQuery.from(ShoppingListItemTranslation.class)).thenReturn(itemTranslationRoot);
        when(criteriaBuilder.and(allPredicates, allPredicates)).thenReturn(contentPredicate);

        Predicate actual = shoppingListItemSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertEquals(contentPredicate, actual);
        verify(shoppingListItemSpecification).toPredicate(any(), any(), any());
        verify(shoppingListItemSpecification, never()).getNumericPredicate(any(), any(), any());
        verify(criteriaQuery).from(ShoppingListItemTranslation.class);
    }

    @Test
    void testToPredicate_withContentSearchCriteria_withNonEmptyValue() {
        ShoppingListItem entity = ModelUtils.getShoppingListItem();
        SearchCriteria contentSearchCriteria = getContentSearchCriteria(entity);
        searchCriteriaList.add(contentSearchCriteria);

        when(criteriaQuery.from(ShoppingListItemTranslation.class)).thenReturn(itemTranslationRoot);
        when(root.get(ShoppingListItem_.id)).thenReturn(idPath);
        when(itemTranslationRoot.get(ShoppingListItemTranslation_.shoppingListItem)).thenReturn(shoppingListItemPath);
        when(shoppingListItemPath.get(ShoppingListItem_.id)).thenReturn(idPath);
        when(criteriaBuilder.equal(idPath, idPath)).thenReturn(contentPredicate);
        when(itemTranslationRoot.get(Translation_.content)).thenReturn(contentPath);
        when(criteriaBuilder.like(eq(contentPath), anyString())).thenReturn(contentPredicate);
        when(criteriaBuilder.and(contentPredicate, contentPredicate)).thenReturn(contentPredicate);
        when(criteriaBuilder.and(allPredicates, contentPredicate)).thenReturn(contentPredicate);

        Predicate actual = shoppingListItemSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertEquals(contentPredicate, actual);
        verify(shoppingListItemSpecification).toPredicate(any(), any(), any());
        verify(shoppingListItemSpecification, never()).getNumericPredicate(any(), any(), any());
        verify(criteriaQuery).from(ShoppingListItemTranslation.class);
    }

    @ParameterizedTest
    @MethodSource("provideSearchCriteria")
    void testToPredicate_withIdAndContentSearchCriteria(SearchCriteria firstSearchCriteria,
                                                        SearchCriteria secondSearchCriteria,
                                                        SearchCriteria idSearchCriteria) {
        searchCriteriaList.add(firstSearchCriteria);
        searchCriteriaList.add(secondSearchCriteria);

        doReturn(idPredicate).when(shoppingListItemSpecification).getNumericPredicate(
                eq(root), eq(criteriaBuilder), any(SearchCriteria.class)
        );
        when(criteriaQuery.from(ShoppingListItemTranslation.class)).thenReturn(itemTranslationRoot);
        when(root.get(ShoppingListItem_.id)).thenReturn(idPath);
        when(itemTranslationRoot.get(ShoppingListItemTranslation_.shoppingListItem)).thenReturn(shoppingListItemPath);
        when(shoppingListItemPath.get(ShoppingListItem_.id)).thenReturn(idPath);
        when(criteriaBuilder.equal(idPath, idPath)).thenReturn(contentPredicate);
        when(itemTranslationRoot.get(Translation_.content)).thenReturn(contentPath);
        when(criteriaBuilder.like(eq(contentPath), anyString())).thenReturn(contentPredicate);
        when(criteriaBuilder.and(contentPredicate, contentPredicate)).thenReturn(contentPredicate);
        lenient().when(criteriaBuilder.and(allPredicates, idPredicate)).thenReturn(idPredicate);
        lenient().when(criteriaBuilder.and(allPredicates, contentPredicate)).thenReturn(contentPredicate);
        lenient().when(criteriaBuilder.and(idPredicate, contentPredicate)).thenReturn(combinedPredicate);
        lenient().when(criteriaBuilder.and(contentPredicate, idPredicate)).thenReturn(combinedPredicate);

        Predicate actual = shoppingListItemSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);

        assertEquals(combinedPredicate, actual);
        verify(shoppingListItemSpecification).toPredicate(any(), any(), any());
        verify(shoppingListItemSpecification).getNumericPredicate(any(), any(), any());
        verify(criteriaQuery).from(ShoppingListItemTranslation.class);
    }

    private static Stream<Arguments> provideSearchCriteria() {
        ShoppingListItem entity = ModelUtils.getShoppingListItem();
        SearchCriteria idSearchCriteria = getIdSearchCriteria(entity);
        SearchCriteria contentSearchCriteria = getContentSearchCriteria(entity);

        return Stream.of(
                // Test ID criteria first, content criteria second
                Arguments.of(idSearchCriteria, contentSearchCriteria, idSearchCriteria),
                // Test content criteria first, ID criteria second
                Arguments.of(contentSearchCriteria, idSearchCriteria, idSearchCriteria)
        );
    }

    private static SearchCriteria getIdSearchCriteria(ShoppingListItem entity) {
        return SearchCriteria.builder()
                .type(ShoppingListItem_.ID)
                .key(ShoppingListItem_.ID)
                .value(entity.getId())
                .build();
    }

    private static SearchCriteria getContentSearchCriteria(ShoppingListItem entity) {
        return SearchCriteria.builder()
                .type(Translation_.CONTENT)
                .key(Translation_.CONTENT)
                .value(entity.getTranslations().getFirst().getContent())
                .build();
    }

}
