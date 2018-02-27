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
public class RoleDto {
    @XmlElement
    private Long roleId;
    @XmlElement
    private Collection<String> userKeys;

    public RoleDto() {
    }

    public RoleDto(Long roleId, Collection<String> userKeys) {
        this.roleId = roleId;
        this.userKeys = userKeys;
    }
}
