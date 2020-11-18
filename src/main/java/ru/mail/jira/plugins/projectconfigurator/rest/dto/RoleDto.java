package ru.mail.jira.plugins.projectconfigurator.rest.dto;

import java.util.Collection;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("UnusedDeclaration")
@XmlRootElement
@Getter
@Setter
public class RoleDto {

  @XmlElement private Long projectRoleId;
  @XmlElement private Collection<String> userKeys;
  @XmlElement private Collection<String> groupNames;

  public RoleDto() {}

  public RoleDto(Long projectRoleId, Collection<String> userKeys, Collection<String> groupNames) {
    this.projectRoleId = projectRoleId;
    this.userKeys = userKeys;
    this.groupNames = groupNames;
  }
}
