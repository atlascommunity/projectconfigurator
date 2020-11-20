Project Configurator
============================

## Description

This is the ru.mail.jira.plugins:projectconfigurator(Project Configurator) plugin for Atlassian JIRA.

## Pages

#### Project configurator plugin settings page (Administration->Projects)
/secure/ProjectConfiguratorConfiguration.jspa

#### Project configurator plugin edit settings page (Administration->Projects)
/secure/ProjectConfiguratorConfigurationEdit.jspa

#### Project configurator plugin delete settings page (Administration->Projects)
/secure/ProjectConfiguratorConfigurationDelete.jspa

#### Project configurator page
/secure/ProjectConfigurator.jspa


## Rest API

- **GET** [/rest/projectconfigurator/1.0/configuration/findUsers]()

public Response findUsers(@QueryParam("filter") final String filter) 

- **GET** [/rest/projectconfigurator/1.0/configuration/findUsersGroups]()

public Response findUsersGroups(@QueryParam("filter") final String filter) 

- **GET** [/rest/projectconfigurator/1.0/configuration/data]()

public Response getConfigurationData() 

- **POST** [/rest/projectconfigurator/1.0/configuration/]()

public Response createConfiguration(ProjectConfigurationDto projectConfigurationDto) throws JiraException 

- **GEt** [/rest/projectconfigurator/1.0/configuration/createProject/{issueKey}]()

public Response creteProject(@PathParam("issueKey") final String issueKey) throws Exception 
