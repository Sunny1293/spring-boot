package com.example.spring_boot_demo.service.impl;

//import com.example.spring_boot_demo.TestContainerConfiguration;
import com.example.spring_boot_demo.dto.EmployeeRequestDto;
import com.example.spring_boot_demo.dto.EmployeeResponseDto;
import com.example.spring_boot_demo.entity.Employee;
import com.example.spring_boot_demo.exception.ResourceNotFoundException;
import com.example.spring_boot_demo.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Import(TestContainerConfiguration.class)
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee mockEmployee;
    private EmployeeRequestDto mockEmployeeRequestDto;
    private EmployeeResponseDto mockEmployeeResponseDto;


    @BeforeEach
    void setUp() {
        mockEmployee = Employee.builder()
                .id(1L)
                .email("sunny@gmail.com")
                .name("Sunny")
                .salary(200L)
                .build();

        mockEmployeeRequestDto = EmployeeRequestDto.builder()
                .id(1L)
                .email("sunny@gmail.com")
                .name("Sunny")
                .salary(200L)
                .build();

        mockEmployeeResponseDto = modelMapper.map(mockEmployee, EmployeeResponseDto.class);
    }

    @Test
    void testGetEmployeeById_WhenEmployeeIdIsPresent_ThenReturnEmployeeDto() {
        Long id = mockEmployee.getId();
        when(employeeRepository.findById(id)).thenReturn(Optional.of(mockEmployee)); //stubbing

        EmployeeResponseDto employeeDto = employeeService.getEmployeeById(id);

        assertThat(employeeDto).isNotNull();
        assertThat(employeeDto.getId()).isEqualTo(id);
        assertThat(employeeDto.getEmail()).isEqualTo(mockEmployee.getEmail());
        verify(employeeRepository, only()).findById(id);
    }

    @Test
    void testGetEmployeeById_whenEmployeeIsNotPresent_thenThrowException() {
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployeeById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        verify(employeeRepository).findById(1L);
    }

    @Test
    void testCreateNewEmployee_WhenValidEmployee_ThenCreateNewEmployee() {
        List<Employee> employees = new ArrayList<>();
        when(employeeRepository.findByEmail(anyString())).thenReturn(employees);
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

        EmployeeResponseDto employeeDto = employeeService.createNewEmployee(mockEmployeeRequestDto);
        assertThat(employeeDto).isNotNull();
        assertThat(employeeDto.getEmail()).isEqualTo(mockEmployee.getEmail());

        ArgumentCaptor<Employee> employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(employeeArgumentCaptor.capture());

        Employee capturedEmployee = employeeArgumentCaptor.getValue();
        assertThat(capturedEmployee.getEmail()).isEqualTo(mockEmployee.getEmail());
    }

    @Test
    void testCreateNewEmployee_whenAttemptingToCreateEmployeeWithExistingEmail_thenThrowException() {
        when(employeeRepository.findByEmail(mockEmployeeResponseDto.getEmail())).thenReturn(List.of(mockEmployee));

        assertThatThrownBy(() -> employeeService.createNewEmployee(mockEmployeeRequestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee already exists with email: "+mockEmployee.getEmail());

        verify(employeeRepository).findByEmail(mockEmployeeResponseDto.getEmail());
        verify(employeeRepository, never()).save(any());
    }


    @Test
    void testUpdateEmployee_whenEmployeeDoesNotExists_thenThrowException() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.updateEmployee(1L, mockEmployeeRequestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        verify(employeeRepository).findById(1L);
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testUpdateEmployee_whenAttemptingToUpdateEmail_thenThrowException() {
        when(employeeRepository.findById(mockEmployeeRequestDto.getId())).thenReturn(Optional.of(mockEmployee));
        mockEmployeeRequestDto.setName("Random");
        mockEmployeeRequestDto.setEmail("random@gmail.com");

        assertThatThrownBy(() -> employeeService.updateEmployee(mockEmployeeRequestDto.getId(), mockEmployeeRequestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The email of the employee cannot be updated");

        verify(employeeRepository).findById(mockEmployeeRequestDto.getId());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testUpdateEmployee_whenValidEmployee_thenUpdateEmployee() {
        when(employeeRepository.findById(mockEmployeeResponseDto.getId())).thenReturn(Optional.of(mockEmployee));
        mockEmployeeResponseDto.setName("Random name");
        mockEmployeeResponseDto.setSalary(199L);

        Employee newEmployee = modelMapper.map(mockEmployeeResponseDto, Employee.class);
        when(employeeRepository.save(any(Employee.class))).thenReturn(newEmployee);

        EmployeeResponseDto updatedEmployeeDto = employeeService.updateEmployee(mockEmployeeRequestDto.getId(), mockEmployeeRequestDto);

        assertThat(updatedEmployeeDto).isEqualTo(mockEmployeeResponseDto);

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(any());
    }

    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExists_thenThrowException() {
        when(employeeRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> employeeService.deleteEmployeeById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: " + 1L);

        verify(employeeRepository, never()).deleteById(anyLong());
    }


    @Test
    void testDeleteEmployee_whenEmployeeIsValid_thenDeleteEmployee() {
        when(employeeRepository.existsById(1L)).thenReturn(true);

        assertThatCode(() -> employeeService.deleteEmployeeById(1L))
                .doesNotThrowAnyException();

        verify(employeeRepository).deleteById(1L);
    }

    @Test
    void testUpdatePartialEmployee_Success_NameUpdate() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "UpdatedSunny");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

        EmployeeResponseDto responseDto = new EmployeeResponseDto();
        responseDto.setName("UpdatedSunny");
        responseDto.setEmail("sunny@test.com");

        EmployeeResponseDto result = employeeService.updatePartialEmployee(1L, updates);

        assertThat("UpdatedSunny").isEqualTo(result.getName());
        assertThat("sunny@gmail.com").isEqualTo(result.getEmail());

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(mockEmployee);
    }

    @Test
    void testUpdatePartialEmployee_Success_EmailUpdate() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("email", "updated@test.com");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

        EmployeeResponseDto responseDto = new EmployeeResponseDto();
        responseDto.setName("Sunny");
        responseDto.setEmail("updated@test.com");

        EmployeeResponseDto result = employeeService.updatePartialEmployee(1L, updates);

        assertThat("Sunny").isEqualTo(result.getName());
        assertThat("updated@test.com").isEqualTo(result.getEmail());
    }

    @Test
    void testUpdatePartialEmployee_EmployeeNotFound() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Test");

        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> employeeService.updatePartialEmployee(1L, updates))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with ID:" +1L);

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testUpdatePartialEmployee_InvalidField() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("invalidField", "value");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));

        assertThatThrownBy(
                () -> employeeService.updatePartialEmployee(1L, updates))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Field is not supported");

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testUpdatePartialEmployee_MultipleFields() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "NewName");
        updates.put("email", "new@test.com");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

        EmployeeResponseDto responseDto = new EmployeeResponseDto();
        responseDto.setName("NewName");
        responseDto.setEmail("new@test.com");

        EmployeeResponseDto result = employeeService.updatePartialEmployee(1L, updates);

        assertThat("NewName").isEqualTo(result.getName());
        assertThat("new@test.com").isEqualTo(result.getEmail());
    }


}