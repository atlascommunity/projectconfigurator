/* (C)2020 */
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
public class ProjectConfigurationDto {

  @XmlElement private String projectName;
  @XmlElement private String projectKey;
  @XmlElement private String projectLeadKey;
  @XmlElement private String projectType;
  @XmlElement private Collection<ProcessDto> processes;
  @XmlElement private Collection<RoleDto> roles;
  @XmlElement private Long permissionSchemeId;
  @XmlElement private Long notificationSchemeId;

  public ProjectConfigurationDto() {}
}
