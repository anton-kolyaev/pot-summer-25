package com.coherentsolutions.pot.insurance_service.unit.service;

import com.coherentsolutions.pot.insurance_service.dto.CompanyDto;
import com.coherentsolutions.pot.insurance_service.dto.CompanyFilter;
import com.coherentsolutions.pot.insurance_service.enums.CompanyStatus;
import com.coherentsolutions.pot.insurance_service.mapper.CompanyMapper;
import com.coherentsolutions.pot.insurance_service.model.Address;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.model.Phone;
import com.coherentsolutions.pot.insurance_service.repository.CompanyRepository;
import com.coherentsolutions.pot.insurance_service.service.CompanyManagementService;
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

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Company Management Service Tests")
class CompanyManagementServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @InjectMocks
    private CompanyManagementService companyManagementService;

    private UUID testCompanyId;
    private Company testCompany;
    private CompanyDto testCompanyDto;
    private Address testAddress;
    private Phone testPhone;

    @BeforeEach
    void setUp() {
        testCompanyId = UUID.randomUUID();
        testAddress = new Address();
        testAddress.setCountry("USA");
        testAddress.setCity("New York");
        testAddress.setStreet("123 Main St");

        testPhone = new Phone();
        testPhone.setCode("+1");
        testPhone.setNumber("555-1234");

        testCompany = new Company();
        testCompany.setId(testCompanyId);
        testCompany.setName("Test Company");
        testCompany.setCountryCode("USA");
        testCompany.setEmail("test@company.com");
        testCompany.setWebsite("https://testcompany.com");
        testCompany.setStatus(CompanyStatus.ACTIVE);
        testCompany.setAddressData(List.of(testAddress));
        testCompany.setPhoneData(List.of(testPhone));
        testCompany.setCreatedAt(Instant.now());
        testCompany.setUpdatedAt(Instant.now());

        testCompanyDto = CompanyDto.builder()
                .id(testCompanyId)
                .name("Test Company")
                .countryCode("USA")
                .email("test@company.com")
                .website("https://testcompany.com")
                .status(CompanyStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
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
        CompanyDto createRequest = CompanyDto.builder()
                .name("New Company")
                .countryCode("USA")
                .email("new@company.com")
                .website("https://newcompany.com")
                .build();

        Company newCompany = new Company();
        newCompany.setId(UUID.randomUUID());
        newCompany.setName("New Company");
        newCompany.setStatus(CompanyStatus.ACTIVE);

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
        when(companyRepository.findByIdOrThrow(testCompanyId)).thenReturn(testCompany);
        when(companyMapper.toCompanyDto(testCompany)).thenReturn(testCompanyDto);

        // When
        CompanyDto result = companyManagementService.getCompanyDetails(testCompanyId);

        // Then
        // Assertions first
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testCompanyDto);

        // Verifications second
        verify(companyRepository).findByIdOrThrow(testCompanyId);
        verify(companyMapper).toCompanyDto(testCompany);
    }

    @Test
    @DisplayName("Should throw exception when company not found")
    void shouldThrowExceptionWhenCompanyNotFound() {
        // Given
        when(companyRepository.findByIdOrThrow(testCompanyId)).thenThrow(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        // When & Then
        // Assertions first
        assertThatThrownBy(() -> companyManagementService.getCompanyDetails(testCompanyId))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
                .hasMessage("404 NOT_FOUND \"Company not found\"");

        // Verifications second
        verify(companyRepository).findByIdOrThrow(testCompanyId);
    }

    @Test
    @DisplayName("Should update company successfully")
    void shouldUpdateCompanySuccessfully() {
        // Given
        CompanyDto updateRequest = CompanyDto.builder()
                .name("Updated Company")
                .countryCode("CAN")
                .email("updated@company.com")
                .website("https://updatedcompany.com")
                .build();

        Company updatedCompany = new Company();
        updatedCompany.setId(testCompanyId);
        updatedCompany.setName("Updated Company");
        updatedCompany.setCountryCode("CAN");
        updatedCompany.setEmail("updated@company.com");
        updatedCompany.setWebsite("https://updatedcompany.com");

        CompanyDto updatedCompanyDto = CompanyDto.builder()
                .id(testCompanyId)
                .name("Updated Company")
                .countryCode("CAN")
                .email("updated@company.com")
                .website("https://updatedcompany.com")
                .status(CompanyStatus.ACTIVE)
                .build();

        when(companyRepository.findByIdOrThrow(testCompanyId)).thenReturn(testCompany);
        when(companyRepository.save(any(Company.class))).thenReturn(updatedCompany);
        when(companyMapper.toCompanyDto(updatedCompany)).thenReturn(updatedCompanyDto);

        // When
        CompanyDto result = companyManagementService.updateCompany(testCompanyId, updateRequest);

        // Then
        // Assertions first
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Company");
        assertThat(result.getCountryCode()).isEqualTo("CAN");
        assertThat(result.getEmail()).isEqualTo("updated@company.com");
        assertThat(result.getWebsite()).isEqualTo("https://updatedcompany.com");

        // Verifications second
        verify(companyRepository).findByIdOrThrow(testCompanyId);
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

        when(companyRepository.findByIdOrThrow(testCompanyId)).thenThrow(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        // When & Then
        // Assertions first
        assertThatThrownBy(() -> companyManagementService.updateCompany(testCompanyId, updateRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
                .hasMessage("404 NOT_FOUND \"Company not found\"");

        // Verifications second
        verify(companyRepository).findByIdOrThrow(testCompanyId);
        verify(companyRepository, never()).save(any());
    }
} 