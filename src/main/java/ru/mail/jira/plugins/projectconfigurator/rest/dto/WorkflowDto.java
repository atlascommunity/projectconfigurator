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
public class WorkflowDto {

  @XmlElement String id;
  @XmlElement String name;
  @XmlElement boolean active;

  public WorkflowDto(String id, String name, boolean active) {
    this.id = id;
    this.name = name;
    this.active = active;
  }
}
