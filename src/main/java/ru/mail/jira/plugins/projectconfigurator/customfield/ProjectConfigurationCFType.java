/* (C)2020 */
package ru.mail.jira.plugins.projectconfigurator.customfield;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.jira.issue.customfields.impl.AbstractSingleFieldType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.persistence.PersistenceFieldType;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.type.ProjectType;
import com.atlassian.jira.project.type.ProjectTypeKey;
import com.atlassian.jira.project.type.ProjectTypeKeys;
import com.atlassian.jira.project.type.ProjectTypeManager;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mail.jira.plugins.projectconfigurator.configuration.ProjectConfiguratorManager;

public class ProjectConfigurationCFType extends AbstractSingleFieldType<ProjectConfiguration> {

  private static final Logger log = LoggerFactory.getLogger(ProjectConfigurationCFType.class);

  private final GlobalPermissionManager globalPermissionManager;
  private final JiraAuthenticationContext jiraAuthenticationContext;
  private final ProjectConfiguratorManager projectConfiguratorManager;
  private final ProjectManager projectManager;
  private final ProjectRoleManager projectRoleManager;
  private final ProjectTypeManager projectTypeManager;
  private final UserManager userManager;

  protected ProjectConfigurationCFType(
      @ComponentImport CustomFieldValuePersister customFieldValuePersister,
      @ComponentImport GenericConfigManager genericConfigManager,
      @ComponentImport GlobalPermissionManager globalPermissionManager,
      @ComponentImport JiraAuthenticationContext jiraAuthenticationContext,
      ProjectConfiguratorManager projectConfiguratorManager,
      @ComponentImport ProjectManager projectManager,
      @ComponentImport ProjectRoleManager projectRoleManager,
      @ComponentImport ProjectTypeManager projectTypeManager,
      @ComponentImport UserManager userManager) {
    super(customFieldValuePersister, genericConfigManager);
    this.globalPermissionManager = globalPermissionManager;
    this.jiraAuthenticationContext = jiraAuthenticationContext;
    this.projectConfiguratorManager = projectConfiguratorManager;
    this.projectManager = projectManager;
    this.projectRoleManager = projectRoleManager;
    this.projectTypeManager = projectTypeManager;
    this.userManager = userManager;
  }

  @Nonnull
  @Override
  protected PersistenceFieldType getDatabaseType() {
    return PersistenceFieldType.TYPE_UNLIMITED_TEXT;
  }

  @Nullable
  @Override
  protected Object getDbValueFromObject(ProjectConfiguration value) {
    return getStringFromSingularObject(value);
  }

  @Nullable
  @Override
  protected ProjectConfiguration getObjectFromDbValue(@Nonnull Object value)
      throws FieldValidationException {
    return getSingularObjectFromString((String) value);
  }

  @Override
  public String getStringFromSingularObject(ProjectConfiguration value) {
    return value == null ? "" : value.toString();
  }

  @Override
  public ProjectConfiguration getSingularObjectFromString(String value)
      throws FieldValidationException {
    try {
      return StringUtils.isEmpty(value) ? null : buildValue(value);
    } catch (Exception e) {
      throw new FieldValidationException(e.getMessage());
    }
  }

  private ProjectType getProjectType(String projectTypeKeyString) {
    ProjectTypeKey projectTypeKey =
        ProjectTypeKeys.JIRA_PROJECT_TYPE_KEYS.stream()
            .filter(type -> type.getKey().equals(projectTypeKeyString))
            .findFirst()
            .orElse(ProjectTypeKeys.BUSINESS);
    return projectTypeManager.getAccessibleProjectType(projectTypeKey).getOrNull();
  }

  private ProjectConfiguration buildValue(String strValue) {
    try {
      if (StringUtils.isEmpty(strValue)) {
        return null;
      }

      JsonObject jsonProjectConfiguration = new JsonParser().parse(strValue).getAsJsonObject();
      List<ProjectConfiguration.JiraProcess> processes = new ArrayList<>();
      for (JsonElement processElement : jsonProjectConfiguration.getAsJsonArray("processes")) {
        JsonObject processObject = processElement.getAsJsonObject();

        ProjectConfiguration.JiraProcess role = new ProjectConfiguration.JiraProcess();
        role.setIssueType(
            projectConfiguratorManager.getIssueType(
                processObject.getAsJsonPrimitive("issueTypeId").getAsString()));
        role.setJiraWorkflow(
            projectConfiguratorManager.getWorkflow(
                processObject.getAsJsonPrimitive("workflowName").getAsString()));
        role.setFieldScreenScheme(
            projectConfiguratorManager.getFieldScreenScheme(
                processObject.getAsJsonPrimitive("screenSchemeId").getAsLong()));
        processes.add(role);
      }

      List<ProjectConfiguration.Role> roles = new ArrayList<>();
      for (JsonElement roleElement : jsonProjectConfiguration.getAsJsonArray("roles")) {
        JsonObject roleObject = roleElement.getAsJsonObject();
        List<ApplicationUser> users = new ArrayList<>();
        if (roleObject.has("userKeys")) {
          for (JsonElement userElement : roleObject.getAsJsonArray("userKeys")) {
            users.add(userManager.getUserByKey(userElement.getAsJsonPrimitive().getAsString()));
          }
        }
        List<Group> groups = new ArrayList<>();
        if (roleObject.has("groupNames")) {
          for (JsonElement groupElement : roleObject.getAsJsonArray("groupNames")) {
            groups.add(
                projectConfiguratorManager.getGroup(
                    groupElement.getAsJsonPrimitive().getAsString()));
          }
        }

        ProjectConfiguration.Role role = new ProjectConfiguration.Role();
        role.setProjectRole(
            projectRoleManager.getProjectRole(
                roleObject.getAsJsonPrimitive("projectRoleId").getAsLong()));
        role.setUsers(users);
        role.setGroups(groups);
        roles.add(role);
      }

      ProjectConfiguration value = new ProjectConfiguration();
      value.setProjectName(jsonProjectConfiguration.get("projectName").getAsString());
      value.setProjectKey(jsonProjectConfiguration.get("projectKey").getAsString());
      value.setProjectLead(
          userManager.getUserByKey(jsonProjectConfiguration.get("projectLeadKey").getAsString()));
      value.setProjectType(
          getProjectType(
              jsonProjectConfiguration.has("projectType")
                  ? jsonProjectConfiguration.get("projectType").getAsString()
                  : null));
      value.setProcesses(processes);
      value.setRoles(roles);
      value.setPermissionScheme(
          projectConfiguratorManager.getPermissionScheme(
              jsonProjectConfiguration.get("permissionSchemeId").getAsLong()));
      value.setNotificationScheme(
          projectConfiguratorManager.getNotificationScheme(
              jsonProjectConfiguration.get("notificationSchemeId").getAsLong()));
      return value;
    } catch (FieldValidationException e) {
      throw e;
    } catch (JsonSyntaxException e) {
      String errorMsg = "Bad value => " + strValue;
      log.error(errorMsg, e);
      throw new FieldValidationException(errorMsg);
    } catch (Exception e) {
      String errorMsg =
          "Error while trying to build value for project configuration picker. String value => "
              + strValue;
      log.error(errorMsg, e);
      throw new FieldValidationException(e.getMessage());
    }
  }

  public boolean hasCreateProjectPermission(String projectKey) {
    return globalPermissionManager.hasPermission(
            GlobalPermissionKey.ADMINISTER, jiraAuthenticationContext.getLoggedInUser())
        && projectManager.getProjectByCurrentKey(projectKey) == null;
  }
}
