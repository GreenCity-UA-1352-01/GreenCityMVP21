package greencity.mapping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UtilsMapperTest {

    static class MyEntity {
        private Long id;
        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MyEntity myEntity = (MyEntity) o;
            return id.equals(myEntity.id) && name.equals(myEntity.name);
        }

        @Override
        public int hashCode() {
            return 31 * id.hashCode() + name.hashCode();
        }
    }

    static class MyDto {
        private Long id;
        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MyDto myDto = (MyDto) o;
            return id.equals(myDto.id) && name.equals(myDto.name);
        }

        @Override
        public int hashCode() {
            return 31 * id.hashCode() + name.hashCode();
        }
    }

    @Test
    void testMap_entityToDto() {

        MyEntity entity = new MyEntity();
        entity.setId(1L);
        entity.setName("Entity 1");

        MyDto expectedDto = new MyDto();
        expectedDto.setId(1L);
        expectedDto.setName("Entity 1");

        MyDto result = UtilsMapper.map(entity, MyDto.class);

        assertNotNull(result);
        assertEquals(expectedDto, result);
    }

    @Test
    void testMap_emptyEntityListToDtoList() {

        List<MyEntity> emptyEntityList = Arrays.asList();

        List<MyDto> result = UtilsMapper.mapAllToList(emptyEntityList, MyDto.class);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testMap_nullEntity() {

        MyEntity nullEntity = null;

        assertThrows(IllegalArgumentException.class, () -> {
            UtilsMapper.map(nullEntity, MyDto.class);
        });
    }

    @Test
    void testMap_listOfEntitiesToListOfDtos() {

        MyEntity entity1 = new MyEntity();
        entity1.setId(1L);
        entity1.setName("Entity 1");

        MyEntity entity2 = new MyEntity();
        entity2.setId(2L);
        entity2.setName("Entity 2");

        List<MyEntity> entities = Arrays.asList(entity1, entity2);

        MyDto dto1 = new MyDto();
        dto1.setId(1L);
        dto1.setName("Entity 1");

        MyDto dto2 = new MyDto();
        dto2.setId(2L);
        dto2.setName("Entity 2");

        List<MyDto> expectedDtos = Arrays.asList(dto1, dto2);

        List<MyDto> result = UtilsMapper.mapAllToList(entities, MyDto.class);

        assertNotNull(result);
        assertEquals(expectedDtos.size(), result.size());
        assertTrue(result.containsAll(expectedDtos));
    }

    @Test
    void testMap_nullListToEmptyList() {

        List<MyEntity> nullEntityList = null;

        assertThrows(NullPointerException.class, () -> {
            UtilsMapper.mapAllToList(nullEntityList, MyDto.class);
        });
    }

    @Test
    void testMapAllToSet() {

        MyEntity entity1 = new MyEntity();
        entity1.setId(1L);
        entity1.setName("Entity 1");

        MyEntity entity2 = new MyEntity();
        entity2.setId(2L);
        entity2.setName("Entity 2");

        Set<MyEntity> entities = Set.of(entity1, entity2);

        MyDto dto1 = new MyDto();
        dto1.setId(1L);
        dto1.setName("Entity 1");

        MyDto dto2 = new MyDto();
        dto2.setId(2L);
        dto2.setName("Entity 2");

        Set<MyDto> expectedDtos = Set.of(dto1, dto2);

        Set<MyDto> result = UtilsMapper.mapAllToSet(entities, MyDto.class);

        assertNotNull(result);
        assertEquals(expectedDtos.size(), result.size());
        assertTrue(result.containsAll(expectedDtos));
    }

    @Test
    void testMap_objectEquality() {

        MyEntity entity = new MyEntity();
        entity.setId(1L);
        entity.setName("Entity 1");

        MyDto expectedDto = new MyDto();
        expectedDto.setId(1L);
        expectedDto.setName("Entity 1");

        MyDto result = UtilsMapper.map(entity, MyDto.class);

        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getName(), result.getName());
    }
}