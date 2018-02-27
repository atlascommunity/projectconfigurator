package ru.mail.jira.plugins.projectconfigurator.customfield.dto;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("UnusedDeclaration")
@XmlRootElement
@Getter
@Setter
public class IssueTypeDto {
    @XmlElement
    private String issueTypeId;
    @XmlElement
    private String workflowName;

    public IssueTypeDto() {
    }

    public IssueTypeDto(String issueTypeId, String workflowName) {
        this.issueTypeId = issueTypeId;
        this.workflowName = workflowName;
    }
}
