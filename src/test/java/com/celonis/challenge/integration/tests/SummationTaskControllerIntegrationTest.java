package com.celonis.challenge.integration.tests;

import com.celonis.challenge.model.dto.SummationTaskDto;
import com.celonis.challenge.model.entities.SummationTaskEntity;
import com.celonis.challenge.repositories.SummationTaskRepository;
import com.celonis.challenge.services.SummationTaskBackGroundJobService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.celonis.challenge.model.entities.SummationTaskStatus.STARTED;
import static com.celonis.challenge.model.entities.SummationTaskStatus.STOPPED;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class SummationTaskControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SummationTaskRepository summationTaskRepository;
    @MockBean
    private SummationTaskBackGroundJobService summationTaskBackGroundJobService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    public void clean() {
        summationTaskRepository.deleteAll();
    }

    @Test
    public void shouldCreateTask() throws Exception {
        String response = mockMvc.perform(
                        post("/api/tasks//summation-task")
                                .contentType(APPLICATION_JSON)
                                .content("{\n" +
                                        "    \"initialValue\": 0,\n" +
                                        "    \"limitValue\": 3\n" +
                                        "}")
                                .header("Celonis-Auth", "totally_secret"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        SummationTaskDto summationTaskDto = objectMapper.readValue(response, SummationTaskDto.class);
        assertThat(summationTaskDto.getTaskId()).isNotNull();
        assertThat(summationTaskDto.getSummationTaskStatus()).isEqualTo(STARTED);
        assertThat(summationTaskDto.getInitialValue()).isEqualTo(0);
        assertThat(summationTaskDto.getLimitValue()).isEqualTo(3);
        assertThat(summationTaskDto.getCurrentValue()).isEqualTo(0);
    }

    @Test
    @SneakyThrows
    public void shouldFindAll() {
        SummationTaskEntity summationTaskEntity = new SummationTaskEntity();
        summationTaskEntity.setLimitValue(3);
        summationTaskEntity.setInitialValue(0);
        summationTaskEntity.setCurrentValue(0);
        summationTaskRepository.save(summationTaskEntity);

        String response = mockMvc.perform(
                        get("/api/tasks//summation-task/all")
                                .header("Celonis-Auth", "totally_secret"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<SummationTaskDto> summationTaskDtos = objectMapper.readValue(response, new TypeReference<>() {});
        assertThat(summationTaskDtos).isNotEmpty().hasSize(1);
        SummationTaskDto summationTaskDto = summationTaskDtos.get(0);
        assertThat(summationTaskDto.getTaskId()).isNotNull();
        assertThat(summationTaskDto.getSummationTaskStatus()).isEqualTo(STARTED);
        assertThat(summationTaskDto.getInitialValue()).isEqualTo(0);
        assertThat(summationTaskDto.getLimitValue()).isEqualTo(3);
        assertThat(summationTaskDto.getCurrentValue()).isEqualTo(0);
    }

    @Test
    @SneakyThrows
    public void shouldFindResult() {
        doNothing().when(summationTaskBackGroundJobService).executeSummation();
        SummationTaskEntity summationTaskEntity = new SummationTaskEntity();
        summationTaskEntity.setLimitValue(3);
        summationTaskEntity.setInitialValue(0);
        summationTaskEntity.setCurrentValue(0);
        String id = summationTaskRepository.save(summationTaskEntity).getId();

        String response = mockMvc.perform(
                        get(format( "/api/tasks//summation-task/%s/result", id))
                                .header("Celonis-Auth", "totally_secret"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int result = objectMapper.readValue(response, Integer.class);
        assertThat(result).isEqualTo(0);
        verify(summationTaskBackGroundJobService).executeSummation();
    }

    @Test
    @SneakyThrows
    public void shouldDeleteTask() {
        doNothing().when(summationTaskBackGroundJobService).executeSummation();
        SummationTaskEntity summationTaskEntity = new SummationTaskEntity();
        summationTaskEntity.setLimitValue(3);
        summationTaskEntity.setInitialValue(0);
        summationTaskEntity.setCurrentValue(0);
        String id = summationTaskRepository.save(summationTaskEntity).getId();

        mockMvc.perform(
                        delete(format( "/api/tasks//summation-task/%s", id))
                                .header("Celonis-Auth", "totally_secret"))
                .andExpect(status().isNoContent());

        assertThat(summationTaskRepository.findById(id).isPresent()).isTrue();
        assertThat(summationTaskRepository.findById(id).get().getId()).isEqualTo(id);
        assertThat(summationTaskRepository.findById(id).get().getStatus()).isEqualTo(STOPPED);
    }

}
