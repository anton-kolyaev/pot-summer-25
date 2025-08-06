package com.coherentsolutions.pot.insuranceservice.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coherentsolutions.pot.insuranceservice.dto.company.CompanyDto;
import com.coherentsolutions.pot.insuranceservice.dto.company.CompanyFilter;
import com.coherentsolutions.pot.insuranceservice.dto.company.CompanyReactivationRequest;
import com.coherentsolutions.pot.insuranceservice.enums.CompanyStatus;
import com.coherentsolutions.pot.insuranceservice.enums.UserStatus;
import com.coherentsolutions.pot.insuranceservice.mapper.CompanyMapper;
import com.coherentsolutions.pot.insuranceservice.model.Address;
import com.coherentsolutions.pot.insuranceservice.model.Company;
import com.coherentsolutions.pot.insuranceservice.model.Phone;
import com.coherentsolutions.pot.insuranceservice.repository.CompanyRepository;
import com.coherentsolutions.pot.insuranceservice.repository.UserRepository;
import com.coherentsolutions.pot.insuranceservice.service.CompanyManagementService;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Company Management Service Tests")
class CompanyManagementServiceTest {

  @Mock private CompanyRepository companyRepository;
  @Mock private UserRepository userRepository;
  @Mock private CompanyMapper companyMapper;

  @InjectMocks private CompanyManagementService companyManagementService;

  private UUID companyId;
  private Company testCompany;
  private CompanyDto testCompanyDto;

  @BeforeEach
  void setUp() {
    companyId = UUID.randomUUID();

    Address testAddress = new Address();
    testAddress.setCountry("USA");
    testAddress.setCity("New York");
    testAddress.setStreet("123 Main St");

    Phone testPhone = new Phone();
    testPhone.setCode("+1");
    testPhone.setNumber("555-1234");

    testCompany = new Company();
    testCompany.setId(companyId);
    testCompany.setName("Test Company");
    testCompany.setCountryCode("USA");
    testCompany.setEmail("test@company.com");
    testCompany.setWebsite("https://testcompany.com");
    testCompany.setStatus(CompanyStatus.ACTIVE);
    testCompany.setAddressData(List.of(testAddress));
    testCompany.setPhoneData(List.of(testPhone));

    testCompanyDto = CompanyDto.builder()
        .id(companyId)
        .name("Test Company")
        .countryCode("USA")
        .email("test@company.com")
        .website("https://testcompany.com")
        .status(CompanyStatus.ACTIVE)
        .build();
  }

  @Test
  @DisplayName("Should get companies with filters")
  void shouldGetCompaniesWithFilters() {
    // Given
    CompanyFilter filter = new CompanyFilter();
    filter.setName("Test");
    filter.setCountryCode("USA");
    filter.setStatus(CompanyStatus.ACTIVE);

    Pageable pageable = PageRequest.of(0, 10);
    Page<Company> companyPage = new PageImpl<>(List.of(testCompany), pageable, 1);

    when(companyRepository.findAll(any(Specification.class), eq(pageable)))
        .thenReturn(companyPage);
    when(companyMapper.toCompanyDto(testCompany)).thenReturn(testCompanyDto);

    // When
    Page<CompanyDto> result = companyManagementService.getCompaniesWithFilters(filter, pageable);

    // Then
    // Assertions first
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0)).isEqualTo(testCompanyDto);

    // Verifications second
    verify(companyRepository).findAll(any(Specification.class), eq(pageable));
    verify(companyMapper).toCompanyDto(testCompany);
  }

  @Test
  @DisplayName("Should create company successfully")
  void shouldCreateCompanySuccessfully() {
    // Given

    Company newCompany = new Company();
    newCompany.setId(UUID.randomUUID());
    newCompany.setName("New Company");
    newCompany.setStatus(CompanyStatus.ACTIVE);

    CompanyDto createRequest = CompanyDto.builder()
        .name("New Company")
        .countryCode("USA")
        .email("new@company.com")
        .website("https://newcompany.com")
        .build();

    when(companyMapper.toEntity(createRequest)).thenReturn(newCompany);
    when(companyRepository.save(any(Company.class))).thenReturn(newCompany);
    when(companyMapper.toCompanyDto(newCompany)).thenReturn(testCompanyDto);

    // When
    CompanyDto result = companyManagementService.createCompany(createRequest);

    // Then
    // Assertions first
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(testCompanyDto);

    // Verifications second
    verify(companyMapper).toEntity(createRequest);
    verify(companyRepository).save(newCompany);
    verify(companyMapper).toCompanyDto(newCompany);
  }

  @Test
  @DisplayName("Should get company details by ID")
  void shouldGetCompanyDetailsById() {
    // Given
    when(companyRepository.findByIdOrThrow(companyId)).thenReturn(testCompany);
    when(companyMapper.toCompanyDto(testCompany)).thenReturn(testCompanyDto);

    // When
    CompanyDto result = companyManagementService.getCompanyDetails(companyId);

    // Then
    // Assertions first
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(testCompanyDto);

    // Verifications second
    verify(companyRepository).findByIdOrThrow(companyId);
    verify(companyMapper).toCompanyDto(testCompany);
  }

  @Test
  @DisplayName("Should throw exception when company not found")
  void shouldThrowExceptionWhenCompanyNotFound() {
    // Given
    when(companyRepository.findByIdOrThrow(companyId)).thenThrow(
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

    // When & Then
    // Assertions first
    assertThatThrownBy(() -> companyManagementService.getCompanyDetails(companyId))
        .isInstanceOf(ResponseStatusException.class)
        .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
        .hasMessage("404 NOT_FOUND \"Company not found\"");

    // Verifications second
    verify(companyRepository).findByIdOrThrow(companyId);
  }

  @Test
  @DisplayName("Should update company successfully")
  void shouldUpdateCompanySuccessfully() {
    // Given
    Company updatedCompany = new Company();
    updatedCompany.setId(companyId);
    updatedCompany.setName("Updated Company");
    updatedCompany.setCountryCode("CAN");
    updatedCompany.setEmail("updated@company.com");
    updatedCompany.setWebsite("https://updatedcompany.com");

    CompanyDto updatedCompanyDto = CompanyDto.builder()
        .id(companyId)
        .name("Updated Company")
        .countryCode("CAN")
        .email("updated@company.com")
        .website("https://updatedcompany.com")
        .status(CompanyStatus.ACTIVE)
        .build();

    when(companyRepository.findByIdOrThrow(companyId)).thenReturn(testCompany);
    when(companyRepository.save(any(Company.class))).thenReturn(updatedCompany);
    when(companyMapper.toCompanyDto(updatedCompany)).thenReturn(updatedCompanyDto);

    CompanyDto updateRequest = CompanyDto.builder()
        .name("Updated Company")
        .countryCode("CAN")
        .email("updated@company.com")
        .website("https://updatedcompany.com")
        .build();

    // When
    CompanyDto result = companyManagementService.updateCompany(companyId, updateRequest);

    // Then
    // Assertions first
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Updated Company");
    assertThat(result.getCountryCode()).isEqualTo("CAN");
    assertThat(result.getEmail()).isEqualTo("updated@company.com");
    assertThat(result.getWebsite()).isEqualTo("https://updatedcompany.com");

    // Verifications second
    verify(companyRepository).findByIdOrThrow(companyId);
    verify(companyRepository).save(any(Company.class));
    verify(companyMapper).toCompanyDto(updatedCompany);
  }

  @Test
  @DisplayName("Should throw exception when updating non-existent company")
  void shouldThrowExceptionWhenUpdatingNonExistentCompany() {
    // Given
    CompanyDto updateRequest = CompanyDto.builder()
        .name("Updated Company")
        .build();

    when(companyRepository.findByIdOrThrow(companyId)).thenThrow(
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

    // When & Then
    // Assertions first
    assertThatThrownBy(() -> companyManagementService.updateCompany(companyId, updateRequest))
        .isInstanceOf(ResponseStatusException.class)
        .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
        .hasMessage("404 NOT_FOUND \"Company not found\"");

    // Verifications second
    verify(companyRepository).findByIdOrThrow(companyId);
    verify(companyRepository, never()).save(any());
  }

  @Test
  void deactivateCompanySuccess() {
    // Given
    when(companyRepository.findByIdOrThrow(companyId)).thenReturn(testCompany);
    when(companyRepository.save(any(Company.class))).thenReturn(testCompany);
    when(companyMapper.toCompanyDto(testCompany)).thenReturn(testCompanyDto);

    // When
    CompanyDto result = companyManagementService.deactivateCompany(companyId);

    // Then
    // Assertions first
    assertNotNull(result);
    assertEquals(CompanyStatus.DEACTIVATED, testCompany.getStatus());

    // Verifications second
    verify(companyRepository).findByIdOrThrow(companyId);
    verify(companyRepository).save(testCompany);
    verify(userRepository).updateUserStatusByCompanyId(companyId, UserStatus.INACTIVE);
  }

  @Test
  void deactivateCompanyCompanyNotFound() {
    // Given
    when(companyRepository.findByIdOrThrow(companyId)).thenThrow(
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

    // When & Then
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> companyManagementService.deactivateCompany(companyId));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("Company not found", exception.getReason());
  }

  @Test
  void deactivateCompanyAlreadyDeactivated() {
    // Given
    testCompany.setStatus(CompanyStatus.DEACTIVATED);
    when(companyRepository.findByIdOrThrow(companyId)).thenReturn(testCompany);

    // When & Then
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> companyManagementService.deactivateCompany(companyId));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Company is already deactivated", exception.getReason());
  }

  @Test
  void reactivateCompanySuccessAllUsers() {
    // Given
    testCompany.setStatus(CompanyStatus.DEACTIVATED);
    when(companyRepository.findByIdOrThrow(companyId)).thenReturn(testCompany);
    when(companyRepository.save(any(Company.class))).thenReturn(testCompany);
    when(companyMapper.toCompanyDto(testCompany)).thenReturn(testCompanyDto);

    CompanyReactivationRequest request = new CompanyReactivationRequest(
        CompanyReactivationRequest.UserReactivationOption.ALL, null);

    // When
    CompanyDto result = companyManagementService.reactivateCompany(companyId, request);

    // Then
    // Assertions first
    assertNotNull(result);
    assertEquals(CompanyStatus.ACTIVE, testCompany.getStatus());

    // Verifications second
    verify(companyRepository).findByIdOrThrow(companyId);
    verify(companyRepository).save(testCompany);
    verify(userRepository).updateUserStatusByCompanyId(companyId, UserStatus.ACTIVE);
  }

  @Test
  void reactivateCompanySuccessSelectedUsers() {
    // Given
    testCompany.setStatus(CompanyStatus.DEACTIVATED);
    List<UUID> selectedUserIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());

    when(companyRepository.findByIdOrThrow(companyId)).thenReturn(testCompany);
    when(companyRepository.save(any(Company.class))).thenReturn(testCompany);
    when(companyMapper.toCompanyDto(testCompany)).thenReturn(testCompanyDto);

    CompanyReactivationRequest request = new CompanyReactivationRequest(
        CompanyReactivationRequest.UserReactivationOption.SELECTED, selectedUserIds);

    // When
    CompanyDto result = companyManagementService.reactivateCompany(companyId, request);

    // Then
    // Assertions first
    assertNotNull(result);
    assertEquals(CompanyStatus.ACTIVE, testCompany.getStatus());

    // Verifications second
    verify(companyRepository).findByIdOrThrow(companyId);
    verify(companyRepository).save(testCompany);
    verify(userRepository).updateUserStatusByIds(selectedUserIds, UserStatus.ACTIVE);
  }

  @Test
  void reactivateCompanyCompanyNotFound() {
    // Given
    when(companyRepository.findByIdOrThrow(companyId)).thenThrow(
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
    CompanyReactivationRequest request = new CompanyReactivationRequest(
        CompanyReactivationRequest.UserReactivationOption.NONE, null);

    // When & Then
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> companyManagementService.reactivateCompany(companyId, request));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("Company not found", exception.getReason());
  }

  @Test
  void reactivateCompanyAlreadyActive() {
    // Given
    testCompany.setStatus(CompanyStatus.ACTIVE);
    when(companyRepository.findByIdOrThrow(companyId)).thenReturn(testCompany);
    CompanyReactivationRequest request = new CompanyReactivationRequest(
        CompanyReactivationRequest.UserReactivationOption.NONE, null);

    // When & Then
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> companyManagementService.reactivateCompany(companyId, request));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Company is already active", exception.getReason());
  }


  @Test
  void reactivateCompanySelectedOptionWithEmptyUserIds() {
    // Given
    testCompany.setStatus(CompanyStatus.DEACTIVATED);
    when(companyRepository.findByIdOrThrow(companyId)).thenReturn(testCompany);

    CompanyReactivationRequest request = new CompanyReactivationRequest(
        CompanyReactivationRequest.UserReactivationOption.SELECTED, null);

    // When & Then
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> companyManagementService.reactivateCompany(companyId, request));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Selected user IDs are required when option is SELECTED", exception.getReason());
  }

  @Test
  void reactivateCompanySelectedOptionWithEmptyUserIdsList() {
    // Given
    testCompany.setStatus(CompanyStatus.DEACTIVATED);
    when(companyRepository.findByIdOrThrow(companyId)).thenReturn(testCompany);

    CompanyReactivationRequest request = new CompanyReactivationRequest(
        CompanyReactivationRequest.UserReactivationOption.SELECTED, List.of());

    // When & Then
    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> companyManagementService.reactivateCompany(companyId, request));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Selected user IDs are required when option is SELECTED", exception.getReason());
  }

  @Test
  void reactivateCompanyNoneOption() {
    // Given
    testCompany.setStatus(CompanyStatus.DEACTIVATED);
    when(companyRepository.findByIdOrThrow(companyId)).thenReturn(testCompany);
    when(companyRepository.save(any(Company.class))).thenReturn(testCompany);
    when(companyMapper.toCompanyDto(testCompany)).thenReturn(testCompanyDto);

    CompanyReactivationRequest request = new CompanyReactivationRequest(
        CompanyReactivationRequest.UserReactivationOption.NONE, null);

    // When
    CompanyDto result = companyManagementService.reactivateCompany(companyId, request);

    // Then
    // Assertions first
    assertNotNull(result);
    assertEquals(CompanyStatus.ACTIVE, testCompany.getStatus());

    // Verifications second
    verify(companyRepository).findByIdOrThrow(companyId);
    verify(companyRepository).save(testCompany);
    verify(userRepository, never()).updateUserStatusByCompanyId(any(), any());
    verify(userRepository, never()).updateUserStatusByIds(any(), any());
  }

}
