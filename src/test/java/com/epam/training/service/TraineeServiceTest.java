package com.epam.training.service;

import com.epam.training.dao.TraineeDao;
import com.epam.training.dto.*;
import com.epam.training.mapper.Mapper;
import com.epam.training.mapper.ToDTOMapper;
import com.epam.training.mapper.ToEntityMapper;
import com.epam.training.model.Trainee;
import com.epam.training.model.User;
import com.epam.training.service.impl.TraineeServiceImpl;
import com.epam.training.service.impl.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private ToEntityMapper<TraineeCreateRequest, Trainee> traineeCreateRequestMapper;

    @Mock
    private ToDTOMapper<Trainee, TraineeCreateResponse> traineeCreateResponseMapper;

    @Mock
    private Mapper<Trainee, TraineeDTO> traineeMapper;

    @Mock
    private UserUtil userUtil;

    @InjectMocks
    private TraineeServiceImpl service;

    private User user;
    private Trainee trainee;
    private TraineeDTO traineeDTO;
    private TraineeCreateRequest createRequest;
    private TraineeCreateResponse createResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUserName("John.Doe");
        user.setPassword("pass123456");
        user.setIsActive(true);

        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("123 Main St");
        trainee.setUser(user);

        UserDTO userDTO = new UserDTO(1L, "John", "Doe", "John.Doe", true);
        traineeDTO = new TraineeDTO(1L, LocalDate.of(1990, 1, 1), "123 Main St", userDTO);

        UserCreateRequest userCreateRequest = new UserCreateRequest("John", "Doe");
        createRequest = new TraineeCreateRequest(LocalDate.of(1990, 1, 1), "123 Main St", userCreateRequest);

        UserCreateResponse userCreateResponse = new UserCreateResponse(1L, "John", "Doe", "John.Doe", "pass123456");
        createResponse = new TraineeCreateResponse(1L, LocalDate.of(1990, 1, 1), "123 Main St", userCreateResponse);
    }


    @Test
    @DisplayName("create: should map request, initialize user, save and return response")
    void create_success() {
        when(traineeCreateRequestMapper.toEntity(createRequest)).thenReturn(trainee);
        when(traineeDao.save(trainee)).thenReturn(trainee);
        when(traineeCreateResponseMapper.toDTO(trainee)).thenReturn(createResponse);

        TraineeCreateResponse result = service.create(createRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("123 Main St", result.getAddress());

        verify(traineeCreateRequestMapper).toEntity(createRequest);
        verify(userUtil).initializeUser(user);
        verify(traineeDao).save(trainee);
        verify(traineeCreateResponseMapper).toDTO(trainee);
    }

    @Test
    @DisplayName("create: null request should throw IllegalArgumentException")
    void create_nullRequest_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> service.create(null));

        verifyNoInteractions(traineeCreateRequestMapper, traineeDao, traineeCreateResponseMapper, userUtil);
    }


    @Test
    @DisplayName("update: should fetch existing trainee, apply changes, save and return DTO")
    void update_success() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Doe");
        updatedUser.setUserName("jane.doe");
        updatedUser.setIsActive(true);

        Trainee updates = new Trainee();
        updates.setId(1L);
        updates.setAddress("New Address");
        updates.setDateOfBirth(LocalDate.of(1991, 2, 3));
        updates.setUser(updatedUser);

        UserDTO updatedUserDTO = new UserDTO(1L, "Jane", "Doe", "Jane.Doe", true);
        TraineeDTO updatedDTO = new TraineeDTO(1L, LocalDate.of(1991, 2, 3), "New Address", updatedUserDTO);

        when(traineeMapper.toEntity(traineeDTO)).thenReturn(updates);
        when(traineeDao.findById(1L)).thenReturn(Optional.of(trainee));
        when(userUtil.updateUser(trainee.getUser(), updates.getUser())).thenReturn(updatedUser);
        when(traineeDao.save(trainee)).thenReturn(trainee);
        when(traineeMapper.toDTO(trainee)).thenReturn(updatedDTO);

        TraineeDTO result = service.update(traineeDTO);

        assertNotNull(result);
        assertEquals("New Address", result.getAddress());
        assertEquals("Jane", result.getUser().getFirstName());

        verify(traineeDao).findById(1L);
        verify(userUtil).updateUser(user, updatedUser);
        verify(traineeDao).save(trainee);
    }

    @Test
    @DisplayName("update: non-existent ID should throw NoSuchElementException")
    void update_notFound_throwsNoSuchElement() {
        when(traineeMapper.toEntity(traineeDTO)).thenReturn(trainee);
        when(traineeDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.update(traineeDTO));

        verify(traineeDao).findById(1L);
        verify(traineeDao, never()).save(any());
    }


    @Test
    @DisplayName("delete: should delegate to DAO with the given ID")
    void delete_success() {
        service.delete(1L);

        verify(traineeDao).delete(1L);
    }


    @Test
    @DisplayName("findById: existing ID should return mapped DTO wrapped in Optional")
    void findById_found() {
        when(traineeDao.findById(1L)).thenReturn(Optional.of(trainee));
        when(traineeMapper.toDTO(trainee)).thenReturn(traineeDTO);

        Optional<TraineeDTO> result = service.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(traineeMapper).toDTO(trainee);
    }

    @Test
    @DisplayName("findById: non-existent ID should return empty Optional")
    void findById_notFound() {
        when(traineeDao.findById(99L)).thenReturn(Optional.empty());

        Optional<TraineeDTO> result = service.findById(99L);

        assertTrue(result.isEmpty());
        verify(traineeMapper, never()).toDTO(any());
    }


    @Test
    @DisplayName("findAll: should return list of all mapped DTOs")
    void findAll_returnsList() {
        Trainee second = new Trainee();
        second.setId(2L);
        second.setUser(user);

        TraineeDTO secondDTO = new TraineeDTO(2L, null, null,
                new UserDTO(1L, "John", "Doe", "John.Doe", true));

        when(traineeDao.findAll()).thenReturn(List.of(trainee, second));
        when(traineeMapper.toDTO(trainee)).thenReturn(traineeDTO);
        when(traineeMapper.toDTO(second)).thenReturn(secondDTO);

        List<TraineeDTO> result = service.findAll();

        assertEquals(2, result.size());
        verify(traineeMapper, times(2)).toDTO(any());
    }

    @Test
    @DisplayName("findAll: empty storage should return empty list")
    void findAll_empty() {
        when(traineeDao.findAll()).thenReturn(List.of());

        List<TraineeDTO> result = service.findAll();

        assertTrue(result.isEmpty());
        verifyNoInteractions(traineeMapper);
    }
}
