package com.coherentsolutions.pot.insuranceservice.model.audit;

import com.coherentsolutions.pot.insuranceservice.config.SecurityAuditor;
import org.hibernate.envers.RevisionListener;

public class RevisionInfoListener implements RevisionListener {

  @Override
  public void newRevision(Object revisionEntity) {
    RevisionInfo rev = (RevisionInfo) revisionEntity;
    rev.setUserId(SecurityAuditor.currentUserOrSystem());
  }
}
