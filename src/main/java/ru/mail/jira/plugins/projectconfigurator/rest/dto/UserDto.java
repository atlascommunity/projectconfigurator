package ru.mail.jira.plugins.projectconfigurator.rest.dto;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("UnusedDeclaration")
@XmlRootElement
@Getter
@Setter
public class UserDto {
    @XmlElement
    String key;
    @XmlElement
    String name;
    @XmlElement
    String displayName;
    @XmlElement
    String avatarUrl;
}
