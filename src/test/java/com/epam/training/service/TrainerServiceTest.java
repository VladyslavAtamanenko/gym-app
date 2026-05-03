package com.epam.training.service;

import com.epam.training.dao.TrainerDao;
import com.epam.training.dto.*;
import com.epam.training.mapper.Mapper;
import com.epam.training.mapper.ToDTOMapper;
import com.epam.training.mapper.ToEntityMapper;
import com.epam.training.model.Trainer;
import com.epam.training.model.TrainingType;
import com.epam.training.model.User;
import com.epam.training.service.impl.TrainerServiceImpl;
import com.epam.training.service.impl.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private ToEntityMapper<TrainerCreateRequest, Trainer> trainerCreateRequestMapper;

    @Mock
    private ToDTOMapper<Trainer, TrainerCreateResponse> trainerCreateResponseMapper;

    @Mock
    private Mapper<Trainer, TrainerDTO> trainerMapper;

    @Mock
    private UserUtil userUtil;

    @InjectMocks
    private TrainerServiceImpl service;

    private static final TrainingType FITNESS = new TrainingType(1L, "Fitness");
    private static final TrainingType YOGA = new TrainingType(2L, "Yoga");

    private User user;
    private Trainer trainer;
    private TrainerDTO trainerDTO;
    private TrainerCreateRequest createRequest;
    private TrainerCreateResponse createResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setUserName("Alice.Smith");
        user.setPassword("password1234");
        user.setIsActive(true);

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setSpecialization(FITNESS);
        trainer.setUser(user);

        UserDTO userDTO = new UserDTO(1L, "Alice", "Smith", "Alice.Smith", true);
        trainerDTO = new TrainerDTO(1L, FITNESS, userDTO);

        UserCreateRequest userCreateRequest = new UserCreateRequest("Alice", "Smith");
        createRequest = new TrainerCreateRequest(FITNESS, userCreateRequest);

        UserCreateResponse userCreateResponse = new UserCreateResponse(1L, "Alice", "Smith", "Alice.Smith", "secret1234");
        createResponse = new TrainerCreateResponse(1L, FITNESS, userCreateResponse);
    }


    @Test
    @DisplayName("create: should map request, initialize user, save and return response")
    void create_success() {
        when(trainerCreateRequestMapper.toEntity(createRequest)).thenReturn(trainer);
        when(trainerDao.save(trainer)).thenReturn(trainer);
        when(trainerCreateResponseMapper.toDTO(trainer)).thenReturn(createResponse);

        TrainerCreateResponse result = service.create(createRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(FITNESS, result.getSpecialization());

        verify(trainerCreateRequestMapper).toEntity(createRequest);
        verify(userUtil).initializeUser(user);
        verify(trainerDao).save(trainer);
        verify(trainerCreateResponseMapper).toDTO(trainer);
    }

    @Test
    @DisplayName("create: null request should throw IllegalArgumentException")
    void create_nullRequest_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> service.create(null));

        verifyNoInteractions(trainerCreateRequestMapper, trainerDao, trainerCreateResponseMapper, userUtil);
    }


    @Test
    @DisplayName("update: should fetch existing trainer, apply changes, save and return DTO")
    void update_success() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setFirstName("Alice");
        updatedUser.setLastName("Smith");
        updatedUser.setUserName("Alice.Smith");
        updatedUser.setIsActive(false);

        Trainer updates = new Trainer();
        updates.setId(1L);
        updates.setSpecialization(YOGA);
        updates.setUser(updatedUser);

        TrainerDTO yogaDTO = new TrainerDTO(1L, YOGA,
                new UserDTO(1L, "Alice", "Smith", "Alice.Smith", false));

        when(trainerMapper.toEntity(trainerDTO)).thenReturn(updates);
        when(trainerDao.findById(1L)).thenReturn(Optional.of(trainer));
        when(userUtil.updateUser(trainer.getUser(), updates.getUser())).thenReturn(updatedUser);
        when(trainerDao.save(trainer)).thenReturn(trainer);
        when(trainerMapper.toDTO(trainer)).thenReturn(yogaDTO);

        TrainerDTO result = service.update(trainerDTO);

        assertNotNull(result);
        assertEquals(YOGA, result.getSpecialization());

        verify(trainerDao).findById(1L);
        verify(userUtil).updateUser(user, updatedUser);
        verify(trainerDao).save(trainer);
    }

    @Test
    @DisplayName("update: non-existent ID should throw NoSuchElementException")
    void update_notFound_throwsNoSuchElement() {
        when(trainerMapper.toEntity(trainerDTO)).thenReturn(trainer);
        when(trainerDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.update(trainerDTO));

        verify(trainerDao).findById(1L);
        verify(trainerDao, never()).save(any());
    }


    @Test
    @DisplayName("findById: existing ID should return mapped DTO wrapped in Optional")
    void findById_found() {
        when(trainerDao.findById(1L)).thenReturn(Optional.of(trainer));
        when(trainerMapper.toDTO(trainer)).thenReturn(trainerDTO);

        Optional<TrainerDTO> result = service.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(FITNESS, result.get().getSpecialization());
        verify(trainerMapper).toDTO(trainer);
    }

    @Test
    @DisplayName("findById: non-existent ID should return empty Optional")
    void findById_notFound() {
        when(trainerDao.findById(99L)).thenReturn(Optional.empty());

        Optional<TrainerDTO> result = service.findById(99L);

        assertTrue(result.isEmpty());
        verify(trainerMapper, never()).toDTO(any());
    }


    @Test
    @DisplayName("findAll: should return list of all mapped DTOs")
    void findAll_returnsList() {
        Trainer second = new Trainer();
        second.setId(2L);
        second.setSpecialization(YOGA);
        second.setUser(user);

        TrainerDTO secondDTO = new TrainerDTO(2L, YOGA,
                new UserDTO(1L, "Alice", "Smith", "Alice.Smith", true));

        when(trainerDao.findAll()).thenReturn(List.of(trainer, second));
        when(trainerMapper.toDTO(trainer)).thenReturn(trainerDTO);
        when(trainerMapper.toDTO(second)).thenReturn(secondDTO);

        List<TrainerDTO> result = service.findAll();

        assertEquals(2, result.size());
        verify(trainerMapper, times(2)).toDTO(any());
    }

    @Test
    @DisplayName("findAll: empty storage should return empty list")
    void findAll_empty() {
        when(trainerDao.findAll()).thenReturn(List.of());

        List<TrainerDTO> result = service.findAll();

        assertTrue(result.isEmpty());
        verifyNoInteractions(trainerMapper);
    }
}
