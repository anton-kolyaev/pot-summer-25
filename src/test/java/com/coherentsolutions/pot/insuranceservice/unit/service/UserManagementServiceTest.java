package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.coherentsolutions.pot.insuranceservice.service.UserManagementService;
import com.coherentsolutions.pot.insuranceservice.repository.UserRepository;
import com.coherentsolutions.pot.insuranceservice.dto.user.UserDto;
import com.coherentsolutions.pot.insuranceservice.dto.user.UserFilter;
import com.coherentsolutions.pot.insuranceservice.enums.UserStatus;
import com.coherentsolutions.pot.insuranceservice.mapper.UserMapper;
import com.coherentsolutions.pot.insuranceservice.model.Address;
import com.coherentsolutions.pot.insuranceservice.model.Company;
import com.coherentsolutions.pot.insuranceservice.model.User;
import com.coherentsolutions.pot.insuranceservice.model.Phone;



@ExtendWith(MockitoExtension.class)
@DisplayName("User Company Management Service Tests")
public class UserManagementServiceTest {

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String EMAIL = "john.doe@example.com";
    private static final String USERNAME = "john.doe";
    private static final UUID COMPANY_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    private static final String NEW_FIRST_NAME = "New";
    private static final String NEW_LAST_NAME = "User";
    private static final String NEW_EMAIL = "new@email.com";
    private static final String NEW_USERNAME = "new_username";

    private static final String ALICE_NAME = "Alice";
    private static final String ALICE_USERNAME = "alice.johnson";
    private static final String ALICE_EMAIL = "alice@example.com";

    private static final String BOB_NAME = "Bob";
    private static final String BOB_USERNAME = "bob.smith";
    private static final String BOB_EMAIL = "bob.smith@example.com";

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @InjectMocks private UserManagementService userManagementService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setEmail(EMAIL);
        user.setUsername(USERNAME);
        user.setStatus(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should update core user fields when user exists")
    void shouldUpdateUserFieldsSuccessfully() {
        UserDto requestDto = new UserDto();
        requestDto.setFirstName(NEW_FIRST_NAME);
        requestDto.setLastName(NEW_LAST_NAME);
        requestDto.setUsername(NEW_USERNAME);
        requestDto.setEmail(NEW_EMAIL);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(requestDto);

        UserDto result = userManagementService.updateUser(USER_ID, requestDto);

        assertEquals(NEW_EMAIL, result.getEmail());
        assertEquals(NEW_FIRST_NAME, result.getFirstName());
        assertEquals(NEW_LAST_NAME, result.getLastName());
        assertEquals(NEW_USERNAME, result.getUsername());

        verify(userRepository).findById(USER_ID);
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    @DisplayName("Should update phone and address data when present in request")
    void shouldUpdatePhoneAndAddressData() {
        List<Phone> phoneDtos = List.of(new Phone());
        List<Address> addressDtos = List.of(new Address());

        UserDto requestDto = new UserDto();
        requestDto.setPhoneData(phoneDtos);
        requestDto.setAddressData(addressDtos);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(requestDto);

        UserDto result = userManagementService.updateUser(USER_ID, requestDto);

        assertEquals(phoneDtos, result.getPhoneData());
        assertEquals(addressDtos, result.getAddressData());
        verify(userRepository).save(user);
        verify(userRepository).findById(USER_ID);
        verify(userMapper).toDto(user);
    }

    @Test
    @DisplayName("Should throw ResponseStatusException when attempting to update non-existent user")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
            () -> userManagementService.updateUser(USER_ID, new UserDto()));
    }

    @Test
    @DisplayName("Should return all users of a company by companyId")
    void shouldReturnAllUsersOfExistingCompany() {
        Company company = new Company();
        company.setId(COMPANY_ID);

        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setFirstName(ALICE_NAME);
        user1.setLastName("Johnson");
        user1.setUsername(ALICE_USERNAME);
        user1.setEmail(ALICE_EMAIL);
        user1.setCompany(company);
        user1.setStatus(UserStatus.ACTIVE);

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setFirstName(BOB_NAME);
        user2.setLastName("Smith");
        user2.setUsername(BOB_USERNAME);
        user2.setEmail(BOB_EMAIL);
        user2.setCompany(company);
        user2.setStatus(UserStatus.ACTIVE);

        List<User> users = List.of(user1, user2);

        UserDto dto1 = UserDto.builder()
                .id(user1.getId())
                .firstName(ALICE_NAME)
                .lastName("Johnson")
                .email(ALICE_EMAIL)
                .username(ALICE_USERNAME)
                .companyId(COMPANY_ID)
                .status(UserStatus.ACTIVE)
                .build();

        UserDto dto2 = UserDto.builder()
                .id(user2.getId())
                .firstName(BOB_NAME)
                .lastName("Smith")
                .email(BOB_EMAIL)
                .username(BOB_USERNAME)
                .companyId(COMPANY_ID)
                .status(UserStatus.ACTIVE)
                .build();

        UserFilter filter = new UserFilter();
        filter.setCompanyId(COMPANY_ID);

        Pageable pageable = Pageable.unpaged();

        when(userRepository.findAll(Mockito.<Specification<User>>any(), Mockito.eq(pageable)))
                .thenReturn(new PageImpl<>(users));

        when(userMapper.toDto(user1)).thenReturn(dto1);
        when(userMapper.toDto(user2)).thenReturn(dto2);

        Page<UserDto> result = userManagementService.getUsersWithFilters(filter, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getTotalElements());
        Assertions.assertEquals(ALICE_USERNAME, result.getContent().get(0).getUsername());
        Assertions.assertEquals(BOB_USERNAME, result.getContent().get(1).getUsername());

        verify(userRepository).findAll(Mockito.<Specification<User>>any(), Mockito.eq(pageable));
        verify(userMapper).toDto(user1);
        verify(userMapper).toDto(user2);
    }

    @Test
    @DisplayName("Should return empty result when no users match the companyId")
    void shouldReturnEmptyPage() {
        UUID randomCompanyId = UUID.randomUUID();
        UserFilter filter = new UserFilter();
        filter.setCompanyId(randomCompanyId);
        Pageable pageable = Pageable.unpaged();

        when(userRepository.findAll(Mockito.<Specification<User>>any(), Mockito.eq(pageable)))
                .thenReturn(Page.empty());

        Page<UserDto> result = userManagementService.getUsersWithFilters(filter, pageable);

        Assertions.assertNotNull(result, "Result should not be null");
        Assertions.assertTrue(result.isEmpty(), "");

        verify(userRepository).findAll(Mockito.<Specification<User>>any(), Mockito.eq(pageable));
        verify(userMapper, times(0)).toDto(Mockito.any());
    }
}
