package greencity.controller;

import greencity.dto.PageableDto;
import greencity.dto.habitfact.*;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.service.HabitFactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitFactControllerTest {

    @Mock
    private HabitFactService habitFactService;
    @Mock
    private ModelMapper modelMapper;
    private HabitFactController habitFactController;

    @BeforeEach
    void setUp() {
        habitFactController = new HabitFactController(habitFactService, modelMapper);
    }

    @Test
    void testGetRandomFactByHabitId() {
        Long habitId = 1L;
        Locale locale = new Locale("en");
        LanguageTranslationDTO dto = new LanguageTranslationDTO();
        when(habitFactService.getRandomHabitFactByHabitIdAndLanguage(habitId, "en")).thenReturn(dto);

        LanguageTranslationDTO result = habitFactController.getRandomFactByHabitId(habitId, locale);
        assertThat(result).isEqualTo(dto);
        verify(habitFactService).getRandomHabitFactByHabitIdAndLanguage(habitId, "en");
    }

    @Test
    void testGetHabitFactOfTheDay() {
        Long langId = 1L;
        LanguageTranslationDTO dto = new LanguageTranslationDTO();
        when(habitFactService.getHabitFactOfTheDay(langId)).thenReturn(dto);

        LanguageTranslationDTO result = habitFactController.getHabitFactOfTheDay(langId);
        assertThat(result).isEqualTo(dto);
        verify(habitFactService).getHabitFactOfTheDay(langId);
    }

    @Test
    void testGetAll() {
        PageableDto<LanguageTranslationDTO> page = new PageableDto<>(List.of(), 0, 1, 1);
        when(habitFactService.getAllHabitFacts(any(), eq("en"))).thenReturn(page);

        ResponseEntity<PageableDto<LanguageTranslationDTO>> result =
                habitFactController.getAll(PageRequest.of(0, 10), new Locale("en"));

        assertThat(result.getBody()).isEqualTo(page);
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        verify(habitFactService).getAllHabitFacts(any(), eq("en"));
    }

    @Test
    void testSave() {
        HabitFactPostDto postDto = new HabitFactPostDto();
        HabitFactDtoResponse serviceResponse = new HabitFactDtoResponse();
        when(habitFactService.save(postDto)).thenReturn(new HabitFactVO());
        when(modelMapper.map(any(), eq(HabitFactDtoResponse.class))).thenReturn(serviceResponse);

        ResponseEntity<HabitFactDtoResponse> result = habitFactController.save(postDto);

        assertThat(result.getStatusCodeValue()).isEqualTo(201);
        assertThat(result.getBody()).isEqualTo(serviceResponse);
        verify(habitFactService).save(postDto);
    }

    @Test
    void testUpdate() {
        HabitFactUpdateDto updateDto = new HabitFactUpdateDto();
        HabitFactPostDto mapped = new HabitFactPostDto();
        when(habitFactService.update(updateDto, 1L)).thenReturn(new HabitFactVO());
        when(modelMapper.map(any(), eq(HabitFactPostDto.class))).thenReturn(mapped);

        ResponseEntity<HabitFactPostDto> result = habitFactController.update(updateDto, 1L);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).isEqualTo(mapped);
        verify(habitFactService).update(updateDto, 1L);
    }

    @Test
    void testDelete() {
        when(habitFactService.delete(anyLong())).thenReturn(1L);
        ResponseEntity<Object> result = habitFactController.delete(1L);
        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        verify(habitFactService, times(1)).delete(1L);
    }




}
