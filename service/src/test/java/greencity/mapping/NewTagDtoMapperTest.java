package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.tag.NewTagDto;
import greencity.entity.Language;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class NewTagDtoMapperTest {
    private NewTagDtoMapper newTagDtoMapper;
    private Tag tag;

    @BeforeEach
    void setUp(){
        newTagDtoMapper = new NewTagDtoMapper();
        tag = ModelUtils.getTag();
    }

    @Test
    void testConvertFromEntityToDto(){
        NewTagDto dto = newTagDtoMapper.convert(tag);

        assertNotNull(dto);
        assertEquals(tag.getId(), dto.getId());
        assertEquals("News", dto.getName());
        assertEquals("Новини", dto.getNameUa());
    }

    @Test
    void testConvertFromEntityToDto_NoEnglishTranslation(){
        tag.setTagTranslations(Collections.singletonList(
                TagTranslation.builder().id(1L).name("Новини").language(Language.builder().id(2L).code("ua").build())
                        .build()));

        NewTagDto dto = newTagDtoMapper.convert(tag);

        assertNotNull(dto);
        assertEquals(tag.getId(), dto.getId());
        assertNull(dto.getName());
        assertEquals("Новини", dto.getNameUa());
    }

    @Test
    void testConvertFromEntityToDto_NoTranslation(){
        tag.setTagTranslations(Collections.EMPTY_LIST);

        NewTagDto dto = newTagDtoMapper.convert(tag);

        assertNotNull(dto);
        assertEquals(tag.getId(), dto.getId());
        assertNull(dto.getName());
        assertNull( dto.getNameUa());
    }

}
