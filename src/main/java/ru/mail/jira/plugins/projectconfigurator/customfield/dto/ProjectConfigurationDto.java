package ru.mail.jira.plugins.projectconfigurator.customfield.dto;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;

@SuppressWarnings("UnusedDeclaration")
@XmlRootElement
@Getter
@Setter
public class ProjectConfigurationDto {
    @XmlElement
    private String projectName;
    @XmlElement
    private String projectKey;
    @XmlElement
    private Collection<String> workflowNames;
    @XmlElement
    private Collection<IssueTypeDto> issueTypes;
    @XmlElement
    private Collection<ScreenSchemeDto> screenSchemes;
    @XmlElement
    private Collection<RoleDto> roles;
    @XmlElement
    private Long permissionSchemeId;
    @XmlElement
    private Long notificationSchemeId;

    public ProjectConfigurationDto() {
    }
}
