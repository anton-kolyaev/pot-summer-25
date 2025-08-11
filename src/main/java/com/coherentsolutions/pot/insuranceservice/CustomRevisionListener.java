package com.coherentsolutions.pot.insuranceservice;


import java.util.Optional;
import java.util.UUID;
import org.hibernate.envers.RevisionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;


@Component
public class CustomRevisionListener implements RevisionListener {

  private static AuditorAware<UUID> auditorAware;

  @Autowired
  public void setAuditorAware(AuditorAware<UUID> auditorAware) {
    CustomRevisionListener.auditorAware = auditorAware;
  }

  @Override
  public void newRevision(Object revisionEntity) {
    CustomRevisionEntity rev = (CustomRevisionEntity) revisionEntity;

    Optional<UUID> currentAuditor = auditorAware.getCurrentAuditor();
    rev.setCreatedBy(
        currentAuditor.orElse(UUID.fromString("00000000-0000-0000-0000-000000000000")));
  }
}