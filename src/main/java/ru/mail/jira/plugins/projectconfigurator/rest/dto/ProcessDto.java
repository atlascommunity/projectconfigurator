/* (C)2020 */
package ru.mail.jira.plugins.projectconfigurator.rest.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("UnusedDeclaration")
@XmlRootElement
@Getter
@Setter
public class ProcessDto {

  @XmlElement String issueTypeId;
  @XmlElement String workflowName;
  @XmlElement Long screenSchemeId;

  public ProcessDto() {}
}
