/* (C)2020 */
package ru.mail.jira.plugins.projectconfigurator.rest;

import com.atlassian.jira.JiraException;
import com.atlassian.jira.issue.fields.OrderableField;
import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.issue.fields.screen.FieldScreenLayoutItem;
import com.atlassian.jira.issue.fields.screen.FieldScreenScheme;
import com.atlassian.jira.issue.fields.screen.FieldScreenSchemeItem;
import com.atlassian.jira.issue.fields.screen.FieldScreenTab;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.notification.NotificationScheme;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.permission.PermissionScheme;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.type.ProjectType;
import com.atlassian.jira.project.type.ProjectTypeManager;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.mail.jira.plugins.projectconfigurator.configuration.PluginData;
import ru.mail.jira.plugins.projectconfigurator.configuration.ProjectConfiguratorManager;
import ru.mail.jira.plugins.projectconfigurator.rest.dto.IssueTypeDto;
import ru.mail.jira.plugins.projectconfigurator.rest.dto.ItemDto;
import ru.mail.jira.plugins.projectconfigurator.rest.dto.ProjectConfigurationDto;
import ru.mail.jira.plugins.projectconfigurator.rest.dto.UserDto;

@Path("/configuration")
@Produces({MediaType.APPLICATION_JSON})
@Controller
public class RestProjectConfiguratorService {

  private final GlobalPermissionManager globalPermissionManager;
  private final JiraAuthenticationContext jiraAuthenticationContext;
  private final I18nHelper i18nHelper;
  private final ProjectConfiguratorManager projectConfiguratorManager;
  private final ProjectTypeManager projectTypeManager;
  private final PluginData pluginData;

  @Autowired
  public RestProjectConfiguratorService(
      @ComponentImport GlobalPermissionManager globalPermissionManager,
      @ComponentImport JiraAuthenticationContext jiraAuthenticationContext,
      @ComponentImport I18nHelper i18nHelper,
      ProjectConfiguratorManager projectConfiguratorManager,
      @ComponentImport ProjectTypeManager projectTypeManager,
      PluginData pluginData) {
    this.globalPermissionManager = globalPermissionManager;
    this.jiraAuthenticationContext = jiraAuthenticationContext;
    this.i18nHelper = i18nHelper;
    this.projectConfiguratorManager = projectConfiguratorManager;
    this.projectTypeManager = projectTypeManager;
    this.pluginData = pluginData;
  }

  private boolean isAdministrator(ApplicationUser user) {
    return user != null
        && globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, user);
  }

  @GET
  @Path("/findUsers")
  public Response findUsers(@QueryParam("filter") final String filter) {
    ApplicationUser currentUser = jiraAuthenticationContext.getLoggedInUser();
    if (currentUser == null) {
      throw new SecurityException();
    }
    return Response.ok(projectConfiguratorManager.findUsers(currentUser, filter)).build();
  }

  @GET
  @Path("/findUsersGroups")
  public Response findUsersGroups(@QueryParam("filter") final String filter) {
    ApplicationUser currentUser = jiraAuthenticationContext.getLoggedInUser();
    if (currentUser == null) {
      throw new SecurityException();
    }

    List<Map<String, Object>> result = new ArrayList<>();

    List<UserDto> userDtos = projectConfiguratorManager.findUsers(currentUser, filter);
    if (userDtos != null && userDtos.size() > 0) {
      Map<String, Object> users = new HashMap<>();
      users.put("label", "Users");
      users.put("options", userDtos);
      result.add(users);
    }

    List<ItemDto> groupDtos = projectConfiguratorManager.findGroups(currentUser, filter);
    if (groupDtos != null && groupDtos.size() > 0) {
      Map<String, Object> groups = new HashMap<>();
      groups.put("label", "Groups");
      groups.put("options", groupDtos);
      result.add(groups);
    }

    return Response.ok(result).build();
  }

  @GET
  @Path("/data")
  public Response getConfigurationData() {
    ApplicationUser currentUser = jiraAuthenticationContext.getLoggedInUser();
    if (currentUser == null) {
      throw new SecurityException();
    }

    Map<String, Object> result = new HashMap<>();

    List<ItemDto> projectTypeDtos = new ArrayList<>();
    for (ProjectType projectType : projectTypeManager.getAllAccessibleProjectTypes()) {
      projectTypeDtos.add(
          new ItemDto(projectType.getKey().getKey(), projectType.getFormattedKey()));
    }
    result.put(
        "projectTypes",
        projectTypeDtos.stream()
            .sorted(Comparator.comparing(ItemDto::getName))
            .collect(Collectors.toList()));

    List<IssueTypeDto> issueTypeDtos = new ArrayList<>();
    List<String> issueTypeIds = pluginData.getIssueTypeIds();
    if (issueTypeIds != null) {
      for (String issueTypeId : issueTypeIds) {
        IssueType issueType = projectConfiguratorManager.getIssueType(issueTypeId);
        issueTypeDtos.add(
            new IssueTypeDto(issueType.getId(), issueType.getName(), issueType.getIconUrl()));
      }
    }
    result.put("issueTypes", issueTypeDtos);

    List<ItemDto> workflowDto = new ArrayList<>();
    List<String> workflowNames = pluginData.getWorkflowNames();
    if (workflowNames != null) {
      for (String workflowName : workflowNames) {
        JiraWorkflow workflow = projectConfiguratorManager.getWorkflow(workflowName);
        workflowDto.add(
            new ItemDto(
                String.valueOf(workflow.getDescriptor().getEntityId()), workflow.getName()));
      }
    }
    result.put("workflows", workflowDto);

    List<ItemDto> permissionSchemeDtos = new ArrayList<>();
    List<String> permissionSchemeIds = pluginData.getPermissionSchemeIds();
    if (permissionSchemeIds != null) {
      for (String permissionSchemeId : permissionSchemeIds) {
        PermissionScheme permissionScheme =
            projectConfiguratorManager.getPermissionScheme(Long.parseLong(permissionSchemeId));
        permissionSchemeDtos.add(
            new ItemDto(String.valueOf(permissionScheme.getId()), permissionScheme.getName()));
      }
    }
    result.put("permissionSchemes", permissionSchemeDtos);

    List<ItemDto> screenSchemeDtos = new ArrayList<>();
    List<String> screenSchemeIds = pluginData.getScreenSchemeIds();
    if (screenSchemeIds != null) {
      for (String screenSchemeId : screenSchemeIds) {
        FieldScreenScheme screenScheme =
            projectConfiguratorManager.getFieldScreenScheme(Long.parseLong(screenSchemeId));
        ItemDto screenSchemeDto =
            new ItemDto(String.valueOf(screenScheme.getId()), screenScheme.getName());
        List<ItemDto> children = new ArrayList<>();
        for (FieldScreenSchemeItem screenSchemeItem : screenScheme.getFieldScreenSchemeItems()) {
          FieldScreen fieldScreen = screenSchemeItem.getFieldScreen();
          ItemDto screenDto =
              new ItemDto(
                  screenSchemeItem.getIssueOperation() == null
                      ? i18nHelper.getText("common.words.default")
                      : i18nHelper.getText(screenSchemeItem.getIssueOperation().getNameKey()),
                  fieldScreen.getName());
          List<ItemDto> fieldsDto = new ArrayList<>();
          for (FieldScreenTab fieldScreenTab : fieldScreen.getTabs()) {
            for (FieldScreenLayoutItem fieldScreenLayoutItem :
                fieldScreenTab.getFieldScreenLayoutItems()) {
              OrderableField field = fieldScreenLayoutItem.getOrderableField();
              if (field != null) {
                fieldsDto.add(new ItemDto(field.getId(), field.getName()));
              }
            }
          }
          screenDto.setChildren(fieldsDto);
          children.add(screenDto);
        }
        screenSchemeDto.setChildren(children);
        screenSchemeDtos.add(screenSchemeDto);
      }
    }
    result.put("screenScheme", screenSchemeDtos);

    List<ItemDto> notificationSchemeDtos = new ArrayList<>();
    List<String> notificationSchemeIds = pluginData.getNotificationSchemeIds();
    if (notificationSchemeIds != null) {
      for (String notificationSchemeId : notificationSchemeIds) {
        NotificationScheme notificationScheme =
            projectConfiguratorManager.getNotificationScheme(Long.parseLong(notificationSchemeId));
        notificationSchemeDtos.add(
            new ItemDto(String.valueOf(notificationScheme.getId()), notificationScheme.getName()));
      }
    }
    result.put("notificationSchemes", notificationSchemeDtos);

    List<ItemDto> rolesDtos = new ArrayList<>();
    Collection<ProjectRole> projectRoles = projectConfiguratorManager.getAllProjectRoles();
    if (projectRoles != null) {
      for (ProjectRole projectRole : projectRoles) {
        rolesDtos.add(new ItemDto(String.valueOf(projectRole.getId()), projectRole.getName()));
      }
    }
    result.put("projectRoles", rolesDtos);

    return Response.ok(result).build();
  }

  @POST
  public Response createConfiguration(ProjectConfigurationDto projectConfigurationDto)
      throws JiraException {

    ApplicationUser currentUser = jiraAuthenticationContext.getLoggedInUser();
    if (currentUser == null) {
      throw new SecurityException();
    }

    Map<String, String> result = new HashMap<>();
    result.put(
        "issueKey",
        projectConfiguratorManager.createProjectConfigurationTask(projectConfigurationDto));
    return Response.ok(result).build();
  }

  @GET
  @Path("/createProject/{issueKey}")
  public Response creteProject(@PathParam("issueKey") final String issueKey) throws Exception {

    if (!isAdministrator(jiraAuthenticationContext.getLoggedInUser())) {
      throw new SecurityException();
    }
    Project project = projectConfiguratorManager.createProjectFromIssue(issueKey);

    Map<String, String> result = new HashMap<>();
    result.put("key", project.getKey());
    result.put("name", project.getName());
    return Response.ok(result).build();
  }
}
