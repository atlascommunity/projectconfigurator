package ru.mail.jira.plugins.projectconfigurator.rest.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("UnusedDeclaration")
@XmlRootElement
@Getter
@Setter
public class ScreenSchemeDto {

  @XmlElement private String issueTypeId;
  @XmlElement private Long screenSchemeId;

  public ScreenSchemeDto() {}

  public ScreenSchemeDto(String issueTypeId, Long screenSchemeId) {
    this.issueTypeId = issueTypeId;
    this.screenSchemeId = screenSchemeId;
  }
}
