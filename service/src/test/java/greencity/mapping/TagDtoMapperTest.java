package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.tag.TagDto;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

public class TagDtoMapperTest {
    TagDtoMapper tagDtoMapper;
    TagTranslation tagTranslation;
    Tag tag;

    @BeforeEach
    void setUp() {
        tagDtoMapper = new TagDtoMapper();
        tag = ModelUtils.getTag();
        tagTranslation = ModelUtils.getTagTranslations().get(0);
        tagTranslation.setTag(tag);
    }

    @Test
    void testConvertFromEntityToDto() {

        TagDto dto = tagDtoMapper.convert(tagTranslation);

        assertNotNull(dto);
        assertEquals(tagTranslation.getTag().getId(), dto.getId());
        assertEquals(tagTranslation.getName(), dto.getName());
    }

    @Test
    void testConvertFromEntityToDto_NullTag() {
        tagTranslation.setTag(null);
        assertThrows(NullPointerException.class, () -> tagDtoMapper.convert(tagTranslation));
    }

    @Test
    void testConvertFromEntityToDto_EmptyName() {
        tagTranslation.setName("");
        TagDto dto = tagDtoMapper.convert(tagTranslation);

        assertNotNull(dto);
        assertEquals(tagTranslation.getTag().getId(), dto.getId());
        assertEquals(tagTranslation.getName(), dto.getName());
    }

    @Test
    void testConvertFromEntityToDto_NullName() {
        tagTranslation.setName(null);
        TagDto dto = tagDtoMapper.convert(tagTranslation);

        assertNotNull(dto);
        assertEquals(tagTranslation.getTag().getId(), dto.getId());
        assertNull(dto.getName());
    }
}
