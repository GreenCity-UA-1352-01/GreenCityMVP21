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
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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
    private Predicate predicate;

    @Mock
    private SingularAttribute<Translation, String> content;
    @Mock
    private SingularAttribute<ShoppingListItemTranslation, ShoppingListItem> shoppingListItem;
    @Mock
    private SingularAttribute<ShoppingListItem, Long> id;
    @Mock
    private Path<String> contentPath;
    @Mock
    private Path<ShoppingListItem> shoppingListItemPath;
    @Mock
    private Path<Long> idPath;

    private List<SearchCriteria> searchCriteriaList = new ArrayList<>();

    @Spy
    private ShoppingListItemSpecification shoppingListItemSpecification = new ShoppingListItemSpecification(searchCriteriaList);

    @BeforeEach
    public void setUp() {
        Translation_.content = content;
        ShoppingListItemTranslation_.shoppingListItem = shoppingListItem;
        ShoppingListItem_.id = id;

        searchCriteriaList.clear();
//        when(shoppingListItemSpecification.getNumericPredicate(root, criteriaBuilder, any()))
//                .thenReturn(numericPredicate);
//
//        when(criteriaBuilder.conjunction()).thenReturn(allPredicates);
//        when(criteriaBuilder.and(allPredicates, numericPredicate)).thenReturn(expected);
//        when(criteriaBuilder.and(expected, numericPredicate)).thenReturn(expected);
//        when(criteriaQuery.from(ShoppingListItemTranslation.class)).thenReturn(itemTranslationRoot);
//        when(root.get(ShoppingListItem_.id)).thenReturn(idPath);
//        when(itemTranslationRoot.get(ShoppingListItemTranslation_.shoppingListItem)).thenReturn(shoppingListItemPath);
//        when(shoppingListItemPath.get(ShoppingListItem_.id)).thenReturn(idPath);
//        when(criteriaBuilder.equal(idPath, idPath)).thenReturn(equalsPredicate);
//        when(itemTranslationRoot.get(Translation_.content)).thenReturn(contentPath);
//        when(criteriaBuilder.like(contentPath, anyString())).thenReturn(likePredicate);
//        when(criteriaBuilder.and(likePredicate, equalsPredicate)).thenReturn(andPredicate);
//        when(criteriaBuilder.and(allPredicates, andPredicate)).thenReturn(expected);
//        when(criteriaBuilder.and(expected, andPredicate)).thenReturn(expected);
        when(shoppingListItemSpecification.getNumericPredicate(root, criteriaBuilder, any()))
                .thenReturn(predicate);

        when(criteriaBuilder.conjunction()).thenReturn(predicate);
        when(criteriaBuilder.and(predicate, predicate)).thenReturn(predicate);
        when(criteriaQuery.from(ShoppingListItemTranslation.class)).thenReturn(itemTranslationRoot);
        when(root.get(ShoppingListItem_.id)).thenReturn(idPath);
        when(itemTranslationRoot.get(ShoppingListItemTranslation_.shoppingListItem)).thenReturn(shoppingListItemPath);
        when(shoppingListItemPath.get(ShoppingListItem_.id)).thenReturn(idPath);
        when(criteriaBuilder.equal(idPath, idPath)).thenReturn(predicate);
        when(itemTranslationRoot.get(Translation_.content)).thenReturn(contentPath);
        when(criteriaBuilder.like(contentPath, anyString())).thenReturn(predicate);
    }

    @Test
    void testToPredicate() {
        ShoppingListItem entity = ModelUtils.getShoppingListItem();
        SearchCriteria idSearchCriteria = getIdSearchCriteria(entity);
        SearchCriteria contentSearchCriteria = getContentSearchCriteria(entity);
        searchCriteriaList.add(idSearchCriteria);
        searchCriteriaList.add(contentSearchCriteria);

        Predicate actual = shoppingListItemSpecification.toPredicate(root, criteriaQuery, criteriaBuilder);
        assertEquals(predicate, actual);
        verify(shoppingListItemSpecification).toPredicate(root, criteriaQuery, criteriaBuilder);
        verify(shoppingListItemSpecification).getNumericPredicate(root, criteriaBuilder, idSearchCriteria);
    }

    private SearchCriteria getIdSearchCriteria(ShoppingListItem entity) {
        return SearchCriteria.builder()
                .type(ShoppingListItem_.ID)
                .key(ShoppingListItem_.ID)
                .value(entity.getId())
                .build();
    }

    private SearchCriteria getContentSearchCriteria(ShoppingListItem entity) {
        return SearchCriteria.builder()
                .type(Translation_.CONTENT)
                .key(Translation_.CONTENT)
                .value(entity.getTranslations().getFirst().getContent())
                .build();
    }

}
