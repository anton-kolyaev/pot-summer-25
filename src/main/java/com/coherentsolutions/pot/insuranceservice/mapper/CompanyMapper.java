package com.coherentsolutions.pot.insuranceservice.mapper;

import com.coherentsolutions.pot.insuranceservice.dto.company.CompanyDto;
import com.coherentsolutions.pot.insuranceservice.model.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between {@link Company} entities and {@link CompanyDto} objects.
 *
 * <p>Uses MapStruct for automatic mapping.
 */
@Mapper(componentModel = "spring")
public interface CompanyMapper {

  /**
   * Maps a {@link CompanyDto} to a {@link Company} entity. Ignores fields: id, createdAt,
   * updatedAt; maps status directly.
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "status", source = "status")
  Company toEntity(CompanyDto dto);


  /**
   * Maps a {@link Company} entity to a {@link CompanyDto}.
   */
  CompanyDto toCompanyDto(Company company);
}
