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
public class IssueTypeDto {

  @XmlElement private String id;
  @XmlElement private String name;
  @XmlElement private String avatarUrl;
  @XmlElement private String workflowName;

  public IssueTypeDto() {}

  public IssueTypeDto(String id, String workflowName) {
    this.id = id;
    this.workflowName = workflowName;
  }

  public IssueTypeDto(String id, String name, String avatarUrl) {
    this.id = id;
    this.name = name;
    this.avatarUrl = avatarUrl;
  }
}
