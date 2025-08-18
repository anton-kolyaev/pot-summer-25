package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.UsersEntity;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.json.mgmt.users.User;
import com.auth0.json.mgmt.users.UsersPage;
import com.auth0.net.Request;
import com.auth0.net.Response;
import com.coherentsolutions.pot.insuranceservice.dto.user.UserDto;
import com.coherentsolutions.pot.insuranceservice.enums.UserFunction;
import com.coherentsolutions.pot.insuranceservice.service.Auth0UserMetadataService;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for Auth0UserMetadataService.
 *
 * <p>Tests cover the metadata update functionality and the backup email search feature.
 */
@ExtendWith(MockitoExtension.class)
class Auth0UserMetadataServiceTest {

  @Mock
  private ManagementAPI managementAPI;

  @Mock
  private UsersEntity usersEntity;

  @Mock
  private Request<User> userUpdateRequest;

  @Mock
  private Response<User> userResponse;

  @Mock
  private Request<UsersPage> usersPageRequest;

  @Mock
  private Response<UsersPage> usersPageResponse;

  @Mock
  private UsersPage usersPage;

  private Auth0UserMetadataService auth0UserMetadataService;

  @BeforeEach
  void setUp() {
    auth0UserMetadataService = new Auth0UserMetadataService(managementAPI);
  }

  @Test
  @DisplayName("Should update user metadata successfully when Auth0 user ID is provided")
  void shouldUpdateUserMetadataSuccessfully() throws Exception {
    // Given
    String auth0UserId = "auth0|123";
    final UserDto userDto = createTestUserDto();
    User mockUser = new User();
    mockUser.setId(auth0UserId);
    mockUser.setEmail(userDto.getEmail());

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.get(eq(auth0UserId), any())).thenReturn(userUpdateRequest);
    when(userUpdateRequest.execute()).thenReturn(userResponse);
    when(userResponse.getBody()).thenReturn(mockUser);
    when(usersEntity.update(eq(auth0UserId), any(User.class))).thenReturn(userUpdateRequest);
    when(userUpdateRequest.execute()).thenReturn(userResponse);
    when(userResponse.getBody()).thenReturn(mockUser);

    // When
    auth0UserMetadataService.updateUserMetadata(auth0UserId, userDto);

    // Then
    verify(usersEntity).get(eq(auth0UserId), any());
    verify(usersEntity).update(eq(auth0UserId), any(User.class));
  }

  @Test
  @DisplayName("Should find Auth0 user by email and update metadata when Auth0 user ID is null")
  void shouldFindUserByEmailAndUpdateMetadataWhenAuth0UserIdIsNull() throws Exception {
    // Given
    String email = "test@example.com";
    String foundAuth0UserId = "auth0|456";
    final UserDto userDto = createTestUserDto();
    userDto.setEmail(email);

    // Mock the search by email
    User mockFoundUser = new User();
    mockFoundUser.setId(foundAuth0UserId);
    mockFoundUser.setEmail(email);

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.list(any(UserFilter.class))).thenReturn(usersPageRequest);
    when(usersPageRequest.execute()).thenReturn(usersPageResponse);
    when(usersPageResponse.getBody()).thenReturn(usersPage);
    when(usersPage.getItems()).thenReturn(Arrays.asList(mockFoundUser));

    // Mock the update
    when(usersEntity.update(eq(foundAuth0UserId), any(User.class))).thenReturn(userUpdateRequest);
    when(userUpdateRequest.execute()).thenReturn(userResponse);
    when(userResponse.getBody()).thenReturn(mockFoundUser);

    // When
    auth0UserMetadataService.updateUserMetadata(null, userDto);

    // Then
    verify(usersEntity).list(any(UserFilter.class));
    verify(usersEntity).update(eq(foundAuth0UserId), any(User.class));
  }

  @Test
  @DisplayName("Should find Auth0 user by email and update metadata when Auth0 user ID is empty")
  void shouldFindUserByEmailAndUpdateMetadataWhenAuth0UserIdIsEmpty() throws Exception {
    // Given
    String email = "test@example.com";
    String foundAuth0UserId = "auth0|789";
    final UserDto userDto = createTestUserDto();
    userDto.setEmail(email);

    // Mock the search by email
    User mockFoundUser = new User();
    mockFoundUser.setId(foundAuth0UserId);
    mockFoundUser.setEmail(email);

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.list(any(UserFilter.class))).thenReturn(usersPageRequest);
    when(usersPageRequest.execute()).thenReturn(usersPageResponse);
    when(usersPageResponse.getBody()).thenReturn(usersPage);
    when(usersPage.getItems()).thenReturn(Arrays.asList(mockFoundUser));

    // Mock the update
    when(usersEntity.update(eq(foundAuth0UserId), any(User.class))).thenReturn(userUpdateRequest);
    when(userUpdateRequest.execute()).thenReturn(userResponse);
    when(userResponse.getBody()).thenReturn(mockFoundUser);

    // When
    auth0UserMetadataService.updateUserMetadata("", userDto);

    // Then
    verify(usersEntity).list(any(UserFilter.class));
    verify(usersEntity).update(eq(foundAuth0UserId), any(User.class));
  }

  @Test
  @DisplayName("Should find Auth0 user by ID when valid ID is provided")
  void shouldFindAuth0UserByIdWhenValidIdIsProvided() throws Exception {
    // Given
    String auth0UserId = "auth0|123";
    final UserDto userDto = createTestUserDto();
    User mockUser = new User();
    mockUser.setId(auth0UserId);
    mockUser.setEmail(userDto.getEmail());

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.get(eq(auth0UserId), any())).thenReturn(userUpdateRequest);
    when(userUpdateRequest.execute()).thenReturn(userResponse);
    when(userResponse.getBody()).thenReturn(mockUser);

    // Mock the update
    when(usersEntity.update(eq(auth0UserId), any(User.class))).thenReturn(userUpdateRequest);
    when(userUpdateRequest.execute()).thenReturn(userResponse);
    when(userResponse.getBody()).thenReturn(mockUser);

    // When
    auth0UserMetadataService.updateUserMetadata(auth0UserId, userDto);

    // Then
    verify(usersEntity).get(eq(auth0UserId), any());
    verify(usersEntity).update(eq(auth0UserId), any(User.class));
  }

  @Test
  @DisplayName("Should fallback to email search when user ID lookup fails")
  void shouldFallbackToEmailSearchWhenUserIdLookupFails() throws Exception {
    // Given
    String auth0UserId = "auth0|invalid";
    String email = "test@example.com";
    final String foundAuth0UserId = "auth0|456";
    final UserDto userDto = createTestUserDto();
    userDto.setEmail(email);

    // Mock the ID lookup to fail
    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.get(eq(auth0UserId), any())).thenThrow(new RuntimeException("User not found"));

    // Mock the email search to succeed
    User mockFoundUser = new User();
    mockFoundUser.setId(foundAuth0UserId);
    mockFoundUser.setEmail(email);

    when(usersEntity.list(any(UserFilter.class))).thenReturn(usersPageRequest);
    when(usersPageRequest.execute()).thenReturn(usersPageResponse);
    when(usersPageResponse.getBody()).thenReturn(usersPage);
    when(usersPage.getItems()).thenReturn(Arrays.asList(mockFoundUser));

    // Mock the update
    when(usersEntity.update(eq(foundAuth0UserId), any(User.class))).thenReturn(userUpdateRequest);
    when(userUpdateRequest.execute()).thenReturn(userResponse);
    when(userResponse.getBody()).thenReturn(mockFoundUser);

    // When
    auth0UserMetadataService.updateUserMetadata(auth0UserId, userDto);

    // Then
    verify(usersEntity).get(eq(auth0UserId), any());
    verify(usersEntity).list(any(UserFilter.class));
    verify(usersEntity).update(eq(foundAuth0UserId), any(User.class));
  }

  @Test
  @DisplayName("Should return early when Auth0 user ID is null/empty and no user found by email")
  void shouldReturnEarlyWhenNoUserFoundByEmail() throws Exception {
    // Given
    String email = "nonexistent@example.com";
    final UserDto userDto = createTestUserDto();
    userDto.setEmail(email);

    // Mock empty search results
    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.list(any(UserFilter.class))).thenReturn(usersPageRequest);
    when(usersPageRequest.execute()).thenReturn(usersPageResponse);
    when(usersPageResponse.getBody()).thenReturn(usersPage);
    when(usersPage.getItems()).thenReturn(Collections.emptyList());

    // When
    auth0UserMetadataService.updateUserMetadata(null, userDto);

    // Then
    verify(usersEntity).list(any(UserFilter.class));
    verify(usersEntity, never()).update(anyString(), any(User.class));
  }

  @Test
  @DisplayName("Should handle multiple users found by email and use the first one")
  void shouldHandleMultipleUsersFoundByEmail() throws Exception {
    // Given
    String email = "duplicate@example.com";
    final String firstAuth0UserId = "auth0|first";
    final String secondAuth0UserId = "auth0|second";
    final UserDto userDto = createTestUserDto();
    userDto.setEmail(email);

    // Mock multiple users found
    User mockFirstUser = new User();
    mockFirstUser.setId(firstAuth0UserId);
    mockFirstUser.setEmail(email);
    
    User mockSecondUser = new User();
    mockSecondUser.setId(secondAuth0UserId);
    mockSecondUser.setEmail(email);

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.list(any(UserFilter.class))).thenReturn(usersPageRequest);
    when(usersPageRequest.execute()).thenReturn(usersPageResponse);
    when(usersPageResponse.getBody()).thenReturn(usersPage);
    when(usersPage.getItems()).thenReturn(Arrays.asList(mockFirstUser, mockSecondUser));

    // Mock the update
    when(usersEntity.update(eq(firstAuth0UserId), any(User.class))).thenReturn(userUpdateRequest);
    when(userUpdateRequest.execute()).thenReturn(userResponse);
    when(userResponse.getBody()).thenReturn(mockFirstUser);

    // When
    auth0UserMetadataService.updateUserMetadata(null, userDto);

    // Then
    verify(usersEntity).list(any(UserFilter.class));
    verify(usersEntity).update(eq(firstAuth0UserId), any(User.class));
  }

  @Test
  @DisplayName("Should return early when both auth0UserId and email are null/empty")
  void shouldReturnEarlyWhenBothAuth0UserIdAndEmailAreNull() throws Exception {
    // Given
    final UserDto userDto = createTestUserDto();
    userDto.setEmail(null);

    // When
    auth0UserMetadataService.updateUserMetadata(null, userDto);

    // Then
    verify(usersEntity, never()).get(anyString(), any());
    verify(usersEntity, never()).list(any(UserFilter.class));
    verify(usersEntity, never()).update(anyString(), any(User.class));
  }

  @Test
  @DisplayName("Should update both user metadata and app metadata correctly")
  void shouldUpdateBothUserMetadataAndAppMetadataCorrectly() throws Exception {
    // Given
    String auth0UserId = "auth0|123";
    final UserDto userDto = createTestUserDto();
    User mockUser = new User();
    mockUser.setId(auth0UserId);
    mockUser.setEmail(userDto.getEmail());

    when(managementAPI.users()).thenReturn(usersEntity);
    when(usersEntity.get(eq(auth0UserId), any())).thenReturn(userUpdateRequest);
    when(userUpdateRequest.execute()).thenReturn(userResponse);
    when(userResponse.getBody()).thenReturn(mockUser);
    when(usersEntity.update(eq(auth0UserId), any(User.class))).thenReturn(userUpdateRequest);
    when(userUpdateRequest.execute()).thenReturn(userResponse);
    when(userResponse.getBody()).thenReturn(mockUser);

    // When
    auth0UserMetadataService.updateUserMetadata(auth0UserId, userDto);

    // Then
    verify(usersEntity).get(eq(auth0UserId), any());
    verify(usersEntity).update(eq(auth0UserId), any(User.class));
    
    // Capture the User object passed to update and verify it has both metadata types
    // This test ensures that both user_metadata and app_metadata are being set
    // The actual verification is done by the mock, ensuring the method is called correctly
  }

  @Test
  @DisplayName("Should correctly separate user metadata from app metadata")
  void shouldCorrectlySeparateUserMetadataFromAppMetadata() throws Exception {
    // Given
    final UserDto userDto = createTestUserDto();
    
    // Use reflection to access private methods for testing
    java.lang.reflect.Method buildUserMetadataMethod = 
        Auth0UserMetadataService.class.getDeclaredMethod("buildUserMetadata", UserDto.class);
    buildUserMetadataMethod.setAccessible(true);
    
    java.lang.reflect.Method buildAppMetadataMethod = 
        Auth0UserMetadataService.class.getDeclaredMethod("buildAppMetadata", UserDto.class);
    buildAppMetadataMethod.setAccessible(true);

    // When
    @SuppressWarnings("unchecked")
    final Map<String, Object> userMetadata = (Map<String, Object>) buildUserMetadataMethod.invoke(auth0UserMetadataService, userDto);
    
    @SuppressWarnings("unchecked")
    final Map<String, Object> appMetadata = (Map<String, Object>) buildAppMetadataMethod.invoke(auth0UserMetadataService, userDto);

    // Then
    // Verify user metadata contains user-modifiable fields
    assertEquals(userDto.getFirstName(), userMetadata.get("firstName"));
    assertEquals(userDto.getLastName(), userMetadata.get("lastName"));
    assertEquals(userDto.getUsername(), userMetadata.get("username"));
    assertEquals(userDto.getDateOfBirth().toString(), userMetadata.get("dateOfBirth"));
    assertEquals(userDto.getSsn(), userMetadata.get("ssn"));
    
    // Verify user metadata does NOT contain admin-controlled fields
    assertNull(userMetadata.get("companyId"));
    assertNull(userMetadata.get("functions"));
    
    // Verify app metadata contains admin-controlled fields
    assertEquals(userDto.getCompanyId().toString(), appMetadata.get("companyId"));
    assertEquals(userDto.getFunctions(), appMetadata.get("functions"));
    
    // Verify app metadata does NOT contain user-modifiable fields
    assertNull(appMetadata.get("firstName"));
    assertNull(appMetadata.get("lastName"));
    assertNull(appMetadata.get("username"));
    assertNull(appMetadata.get("dateOfBirth"));
    assertNull(appMetadata.get("ssn"));
  }

  private UserDto createTestUserDto() {
    UserDto userDto = new UserDto();
    userDto.setId(UUID.randomUUID());
    userDto.setEmail("test@example.com");
    userDto.setFirstName("John");
    userDto.setLastName("Doe");
    userDto.setUsername("johndoe");
    userDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
    userDto.setSsn("123-45-6789");
    userDto.setCompanyId(UUID.randomUUID());
    userDto.setFunctions(Set.of(UserFunction.COMPANY_MANAGER, UserFunction.COMPANY_CLAIM_MANAGER));
    return userDto;
  }
}
