package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.tag.TagTranslationVO;
import greencity.dto.tag.TagVO;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class TagVOMapperTest {
    TagVOMapper tagVOMapper;
    Tag tag;
    @BeforeEach
    void setUp(){
        tagVOMapper = new TagVOMapper();
        tag = ModelUtils.getTag();
    }

    @Test
    void testConvertFromEntityToVo(){
        TagVO tagVO = tagVOMapper.convert(tag);

        assertNotNull(tagVO);
        assertEquals(tag.getId(), tagVO.getId());
        assertEquals(tag.getType(), tagVO.getType());
        assertEquals(tag.getTagTranslations().size(), tagVO.getTagTranslations().size());

        for (int i = 0; i<tag.getTagTranslations().size(); i++){
            TagTranslation tagTranslation = tag.getTagTranslations().get(i);
            TagTranslationVO tagTranslationVO = tagVO.getTagTranslations().get(i);

            assertEquals(tagTranslation.getId(), tagTranslationVO.getId());
            assertEquals(tagTranslation.getName(), tagTranslationVO.getName());
            assertNotNull(tagTranslationVO.getLanguageVO());
            assertEquals(tagTranslation.getLanguage().getId(), tagTranslationVO.getLanguageVO().getId());
            assertEquals(tagTranslation.getLanguage().getCode(), tagTranslationVO.getLanguageVO().getCode());
        }
    }

    @Test
    void testConvertFromEntityToVo_EmptyTranslations(){
        tag.setTagTranslations(List.of());
        TagVO tagVO = tagVOMapper.convert(tag);

        assertNotNull(tagVO);
        assertEquals(tag.getId(), tagVO.getId());
        assertEquals(tag.getType(), tagVO.getType());
        assertNotNull(tagVO.getTagTranslations());
        assertTrue(tagVO.getTagTranslations().isEmpty());
    }
    @Test
    void testConvertFromEntityToVo_NullTranslations() {
        tag.setTagTranslations(null);
        assertThrows(NullPointerException.class, () -> tagVOMapper.convert(tag));
    }
}
