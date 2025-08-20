package com.coherentsolutions.pot.insuranceservice.mapper;

import com.coherentsolutions.pot.insuranceservice.dto.claim.ClaimDto;
import com.coherentsolutions.pot.insuranceservice.dto.consumer.ConsumerDto;
import com.coherentsolutions.pot.insuranceservice.model.Claim;
import com.coherentsolutions.pot.insuranceservice.model.Phone;
import com.coherentsolutions.pot.insuranceservice.model.User;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClaimMapper {

  @Mapping(target = "enrollmentId", source = "enrollment.id")
  @Mapping(target = "planName", source = "enrollment.plan.name")
  @Mapping(target = "consumer", ignore = true)
  ClaimDto toDto(Claim claim);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "claimNumber", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "consumer", ignore = true)
  @Mapping(target = "enrollment", ignore = true)
  Claim toEntity(ClaimDto dto);

  default ConsumerDto toConsumerDto(User user) {
    if (user == null) {
      return null;
    }
    Phone primary = null;
    List<Phone> phones = user.getPhoneData();
    if (phones != null && !phones.isEmpty()) {
      primary = phones.getFirst();
    }
    return ConsumerDto.builder()
        .userId(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .phone(primary)
        .build();
  }

  @AfterMapping
  default void fillConsumer(@MappingTarget ClaimDto dto, Claim claim) {
    dto.setConsumer(toConsumerDto(claim.getConsumer()));
  }
}
