package com.coherentsolutions.pot.insurance_service.service;

import com.coherentsolutions.pot.insurance_service.dto.CompanyDto;
import com.coherentsolutions.pot.insurance_service.dto.CompanyReactivationRequest;
import com.coherentsolutions.pot.insurance_service.enums.CompanyStatus;
import com.coherentsolutions.pot.insurance_service.enums.UserStatus;
import com.coherentsolutions.pot.insurance_service.mapper.CompanyMapper;
import com.coherentsolutions.pot.insurance_service.model.Company;
import com.coherentsolutions.pot.insurance_service.model.User;
import com.coherentsolutions.pot.insurance_service.repository.CompanyRepository;
import com.coherentsolutions.pot.insurance_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyManagementServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyMapper companyMapper;

    @InjectMocks
    private CompanyManagementService companyManagementService;

    private Company testCompany;
    private CompanyDto testCompanyDto;
    private UUID companyId;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        testCompany = new Company();
        testCompany.setId(companyId);
        testCompany.setName("Test Company");
        testCompany.setStatus(CompanyStatus.ACTIVE);
        testCompany.setCreatedAt(Instant.now());

        testCompanyDto = CompanyDto.builder()
                .id(companyId)
                .name("Test Company")
                .status(CompanyStatus.ACTIVE)
                .build();
    }

    @Test
    void deactivateCompany_Success() {
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
    void deactivateCompany_CompanyNotFound() {
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
    void deactivateCompany_AlreadyDeactivated() {
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
    void reactivateCompany_Success_AllUsers() {
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
    void reactivateCompany_Success_SelectedUsers() {
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
    void reactivateCompany_CompanyNotFound() {
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
    void reactivateCompany_AlreadyActive() {
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
    void reactivateCompany_SelectedOptionWithEmptyUserIds() {
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
    void reactivateCompany_SelectedOptionWithEmptyUserIdsList() {
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
    void reactivateCompany_NoneOption() {
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