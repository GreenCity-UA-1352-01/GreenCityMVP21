package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.tag.TagTranslationVO;
import greencity.dto.tag.TagVO;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

public class TagMapperTest {
    TagMapper tagMapper;
    TagVO tagVO;
    @BeforeEach
    void setUp(){
        tagMapper = new TagMapper();
        tagVO = ModelUtils.getTagVO();
    }

    @Test
    void testConvertFromVoToEntity(){
        Tag tag = tagMapper.convert(tagVO);

        assertNotNull(tag);
        assertEquals(tagVO.getId(), tag.getId());
        assertEquals(tagVO.getType(), tag.getType());
        assertEquals(tagVO.getTagTranslations().size(), tag.getTagTranslations().size());

        for (int i = 0; i < tagVO.getTagTranslations().size();i++){
            TagTranslationVO tagTranslationVO = tagVO.getTagTranslations().get(i);
            TagTranslation tagTranslation = tag.getTagTranslations().get(i);

            assertEquals(tagTranslation.getId(), tagTranslationVO.getId());
            assertEquals(tagTranslation.getName(), tagTranslationVO.getName());
            assertNotNull(tagTranslation.getLanguage());
            assertEquals(tagTranslationVO.getLanguageVO().getCode(), tagTranslation.getLanguage().getCode());
            assertEquals(tagTranslationVO.getLanguageVO().getId(), tagTranslation.getLanguage().getId());
        }
    }

    @Test
    void testConvertFromVoToEntity_EmptyTranslations(){
        tagVO.setTagTranslations(List.of());
        Tag tag = tagMapper.convert(tagVO);

        assertNotNull(tag);
        assertEquals(tagVO.getId(), tag.getId());
        assertEquals(tagVO.getType(), tag.getType());
        assertNotNull(tag.getTagTranslations());
        assertTrue(tag.getTagTranslations().isEmpty());
    }

    @Test
    void testConvertFromVoToEntity_NullTranslations(){
        tagVO.setTagTranslations(null);
        assertThrows(NullPointerException.class,()->tagMapper.convert(tagVO));
    }

    @Test
    void testConvertFromVoToEntity_Null(){
        tagVO.setTagTranslations(List.of());
        Tag tag = tagMapper.convert(tagVO);

        assertNotNull(tag);
        assertEquals(tagVO.getId(), tag.getId());
        assertEquals(tagVO.getType(), tag.getType());
        assertNotNull(tag.getTagTranslations());
        assertTrue(tag.getTagTranslations().isEmpty());
    }
}
