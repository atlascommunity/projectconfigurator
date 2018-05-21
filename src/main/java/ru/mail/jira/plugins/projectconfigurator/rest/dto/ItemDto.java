package ru.mail.jira.plugins.projectconfigurator.rest.dto;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
@XmlRootElement
@Getter
@Setter
public class ItemDto {
    @XmlElement
    String id;
    @XmlElement
    String name;
    @XmlElement
    List<ItemDto> children;

    public ItemDto(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
