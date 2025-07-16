package com.coherentsolutions.pot.insurance_service.service;

import com.coherentsolutions.pot.insurance_service.dto.user.UserDto;
import com.coherentsolutions.pot.insurance_service.dto.user.UserFilter;
import com.coherentsolutions.pot.insurance_service.enums.UserStatus;
import com.coherentsolutions.pot.insurance_service.mapper.UserMapper;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.model.User;
import com.coherentsolutions.pot.insurance_service.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Company Management Service Tests")
public class UserManagementServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserManagementService userManagementService;


    @Test
    @DisplayName("Should get Users by companyId")
    void shouldReturnUsersOfCompany() {
        UUID companyId = UUID.randomUUID();
        Company mockCompany = new Company();
        mockCompany.setId(companyId);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName("Alice");
        user.setLastName("Johnson");
        user.setUsername("alice.johnson");
        user.setEmail("alice@example.com");
        user.setCompany(mockCompany);
        user.setStatus(UserStatus.ACTIVE);

        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setFirstName("Bob");
        user1.setLastName("Smith");
        user1.setUsername("bob.smith");
        user1.setEmail("bob.smith@example.com");
        user1.setCompany(mockCompany);
        user1.setStatus(UserStatus.ACTIVE);

        List<User> users = List.of(user, user1);

        UserDto testUserDto1 = UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .companyId(companyId)
                .status(user.getStatus())
                .build();

        UserDto testUserDto2 = UserDto.builder()
                .id(user1.getId())
                .firstName(user1.getFirstName())
                .lastName(user1.getLastName())
                .email(user1.getEmail())
                .username(user1.getUsername())
                .companyId(companyId)
                .status(user1.getStatus())
                .build();

        UserFilter filter = new UserFilter();
        filter.setCompanyId(companyId);

        Pageable pageable = Pageable.unpaged();

        when(userRepository.findAll(Mockito.<Specification<User>>any(), Mockito.eq(pageable)))
                .thenReturn(new PageImpl<>(users));

        when(userMapper.toDto(user)).thenReturn(testUserDto1);
        when(userMapper.toDto(user1)).thenReturn(testUserDto2);

        Page<UserDto> result = userManagementService.getUsersWithFilters(filter, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getTotalElements());
        Assertions.assertEquals("alice.johnson", result.getContent().get(0).getUsername());
        Assertions.assertEquals("bob.smith", result.getContent().get(1).getUsername());
    }
    @Test
    @DisplayName("Should return empty result when no users match the companyId")
    void shouldReturnEmptyPage() {

        UUID nonExistentCompanyId = UUID.randomUUID();
        UserFilter filter = new UserFilter();
        filter.setCompanyId(nonExistentCompanyId);

        Pageable pageable = Pageable.unpaged();

        when(userRepository.findAll(Mockito.<Specification<User>>any(), Mockito.eq(pageable)))
                .thenReturn(Page.empty());

        Page<UserDto> result = userManagementService.getUsersWithFilters(filter, pageable);

        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertTrue(result.isEmpty(), "");
    }

}



