package ru.mail.jira.plugins.projectconfigurator.configuration;

import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.screen.FieldScreenScheme;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.notification.NotificationScheme;
import com.atlassian.jira.permission.PermissionScheme;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.jira.workflow.JiraWorkflow;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import ru.mail.jira.plugins.commons.CommonUtils;
import ru.mail.jira.plugins.projectconfigurator.customfield.ProjectConfigurationCFType;

public class ProjectConfiguratorConfigurationEdit extends JiraWebActionSupport {

  private final PluginData pluginData;
  private final ProjectConfiguratorManager projectConfiguratorManager;
  private List<String> workflowNames;
  private List<String> issueTypeIds;
  private List<String> screenSchemeIds;
  private List<String> permissionSchemeIds;
  private List<String> notificationSchemeIds;
  private String adminUserKey;
  private String projectId;
  private String issueTypeId;
  private String projectConfigurationCfId;

  public ProjectConfiguratorConfigurationEdit(
      PluginData pluginData, ProjectConfiguratorManager projectConfiguratorManager) {
    this.pluginData = pluginData;
    this.projectConfiguratorManager = projectConfiguratorManager;
  }

  @Override
  public void doValidation() {
    if (workflowNames == null || workflowNames.size() == 0) {
      addError(
          "project-configurator-workflows",
          getText("issue.field.required", getText("admin.systeminfo.workflows")));
    }
    if (issueTypeIds == null || issueTypeIds.size() == 0) {
      addError(
          "project-configurator-issue-types",
          getText("issue.field.required", getText("admin.systeminfo.issuetypes")));
    }
    if (screenSchemeIds == null || screenSchemeIds.size() == 0) {
      addError(
          "project-configurator-screen-schemes",
          getText("issue.field.required", getText("admin.systeminfo.screensschemes")));
    }
    if (permissionSchemeIds == null || permissionSchemeIds.size() == 0) {
      addError(
          "project-configurator-permission-schemes",
          getText("issue.field.required", getText("admin.systeminfo.permissionschemes")));
    }
    if (notificationSchemeIds == null || notificationSchemeIds.size() == 0) {
      addError(
          "project-configurator-notification-schemes",
          getText(
              "issue.field.required", getText("admin.schemes.notifications.notification.schemes")));
    }
    if (adminUserKey == null) {
      addError(
          "project-configurator-admin-user",
          getText(
              "issue.field.required",
              getText("ru.mail.jira.plugins.projectconfigurator.configuration.adminUser")));
    }

    if (projectId == null) {
      addError(
          "project-configurator-project",
          getText("issue.field.required", getText("common.words.project")));
    }
    if (issueTypeId == null) {
      addError(
          "project-configurator-issue-type",
          getText("issue.field.required", getText("issue.field.issuetype")));
    }
    if (projectConfigurationCfId == null) {
      addError(
          "project-configurator-field",
          getText(
              "issue.field.required", getText("ru.mail.jira.plugins.projectconfigurator.field")));
    } else if (!(projectConfiguratorManager
            .getCustomField(projectConfigurationCfId)
            .getCustomFieldType()
        instanceof ProjectConfigurationCFType)) {
      addError(
          "project-configurator-field",
          getText("ru.mail.jira.plugins.projectconfigurator.field.error.type"));
    }
  }

  @Override
  public String doDefault() {
    workflowNames = pluginData.getWorkflowNames();
    issueTypeIds = pluginData.getIssueTypeIds();
    screenSchemeIds = pluginData.getScreenSchemeIds();
    permissionSchemeIds = pluginData.getPermissionSchemeIds();
    notificationSchemeIds = pluginData.getNotificationSchemeIds();
    adminUserKey = pluginData.getAdminUserKey();

    projectId = pluginData.getProjectId();
    issueTypeId = pluginData.getIssueTypeId();
    projectConfigurationCfId = pluginData.getProjectConfigurationCfId();

    return INPUT;
  }

  @Override
  public String doExecute() {
    pluginData.setWorkflowNames(workflowNames);
    pluginData.setIssueTypeIds(issueTypeIds);
    pluginData.setScreenSchemeIds(screenSchemeIds);
    pluginData.setPermissionSchemeIds(permissionSchemeIds);
    pluginData.setNotificationSchemeIds(notificationSchemeIds);
    pluginData.setAdminUserKey(adminUserKey);

    pluginData.setProjectId(projectId);
    pluginData.setIssueTypeId(issueTypeId);
    pluginData.setProjectConfigurationCfId(projectConfigurationCfId);

    return getRedirect("/secure/admin/ProjectConfiguratorConfiguration.jspa");
  }

  @SuppressWarnings("unused")
  public String getWorkflowNames() {
    return this.workflowNames != null ? CommonUtils.join(this.workflowNames) : "";
  }

  @SuppressWarnings("unused")
  public void setWorkflowNames(String workflowNames) {
    this.workflowNames =
        StringUtils.isBlank(workflowNames) ? null : CommonUtils.split(workflowNames);
  }

  @SuppressWarnings("unused")
  public String getIssueTypeIds() {
    return this.issueTypeIds != null ? CommonUtils.join(this.issueTypeIds) : "";
  }

  @SuppressWarnings("unused")
  public void setIssueTypeIds(String issueTypeIds) {
    this.issueTypeIds = StringUtils.isBlank(issueTypeIds) ? null : CommonUtils.split(issueTypeIds);
  }

  @SuppressWarnings("unused")
  public String getScreenSchemeIds() {
    return this.screenSchemeIds != null ? CommonUtils.join(this.screenSchemeIds) : "";
  }

  @SuppressWarnings("unused")
  public void setScreenSchemeIds(String screenSchemeIds) {
    this.screenSchemeIds =
        StringUtils.isBlank(screenSchemeIds) ? null : CommonUtils.split(screenSchemeIds);
  }

  @SuppressWarnings("unused")
  public String getPermissionSchemeIds() {
    return this.permissionSchemeIds != null ? CommonUtils.join(this.permissionSchemeIds) : "";
  }

  @SuppressWarnings("unused")
  public void setPermissionSchemeIds(String permissionSchemeIds) {
    this.permissionSchemeIds =
        StringUtils.isBlank(permissionSchemeIds) ? null : CommonUtils.split(permissionSchemeIds);
  }

  @SuppressWarnings("unused")
  public String getNotificationSchemeIds() {
    return this.notificationSchemeIds != null ? CommonUtils.join(this.notificationSchemeIds) : "";
  }

  @SuppressWarnings("unused")
  public void setNotificationSchemeIds(String notificationSchemeIds) {
    this.notificationSchemeIds =
        StringUtils.isBlank(notificationSchemeIds)
            ? null
            : CommonUtils.split(notificationSchemeIds);
  }

  @SuppressWarnings("unused")
  public String getAdminUserKey() {
    return this.adminUserKey != null ? this.adminUserKey : "";
  }

  @SuppressWarnings("unused")
  public void setAdminUserKey(String adminUserKey) {
    this.adminUserKey = StringUtils.isBlank(adminUserKey) ? null : adminUserKey;
  }

  @SuppressWarnings("unused")
  public String getProjectId() {
    return this.projectId != null ? this.projectId : "";
  }

  @SuppressWarnings("unused")
  public void setProjectId(String projectId) {
    this.projectId = StringUtils.isBlank(projectId) ? null : projectId;
  }

  @SuppressWarnings("unused")
  public String getIssueTypeId() {
    return this.issueTypeId != null ? this.issueTypeId : "";
  }

  @SuppressWarnings("unused")
  public void setIssueTypeId(String issueTypeId) {
    this.issueTypeId = StringUtils.isBlank(issueTypeId) ? null : issueTypeId;
  }

  @SuppressWarnings("unused")
  public String getProjectConfigurationCfId() {
    return this.projectConfigurationCfId != null ? this.projectConfigurationCfId : "";
  }

  @SuppressWarnings("unused")
  public void setProjectConfigurationCfId(String projectConfigurationCfId) {
    this.projectConfigurationCfId =
        StringUtils.isBlank(projectConfigurationCfId) ? null : projectConfigurationCfId;
  }

  @SuppressWarnings("unused")
  public List<JiraWorkflow> getWorkflows() {
    return projectConfiguratorManager.getWorkflows(this.workflowNames);
  }

  @SuppressWarnings("unused")
  public List<IssueType> getIssueTypes() {
    return projectConfiguratorManager.getIssueTypes(this.issueTypeIds);
  }

  @SuppressWarnings("unused")
  public List<FieldScreenScheme> getScreenSchemes() {
    return projectConfiguratorManager.getScreenSchemes(this.screenSchemeIds);
  }

  @SuppressWarnings("unused")
  public List<PermissionScheme> getPermissionSchemes() {
    return projectConfiguratorManager.getPermissionSchemes(this.permissionSchemeIds);
  }

  @SuppressWarnings("unused")
  public List<NotificationScheme> getNotificationSchemes() {
    return projectConfiguratorManager.getNotificationSchemes(this.notificationSchemeIds);
  }

  @SuppressWarnings("unused")
  public ApplicationUser getAdminUser() {
    return this.adminUserKey != null ? projectConfiguratorManager.getUser(adminUserKey) : null;
  }

  @SuppressWarnings("unused")
  public Project getProject() {
    return this.projectId != null ? projectConfiguratorManager.getProject(this.projectId) : null;
  }

  @SuppressWarnings("unused")
  public IssueType getIssueType() {
    return this.issueTypeId != null
        ? projectConfiguratorManager.getIssueType(this.issueTypeId)
        : null;
  }

  @SuppressWarnings("unused")
  public CustomField getProjectConfigurationCf() {
    return this.projectConfigurationCfId != null
        ? projectConfiguratorManager.getCustomField(this.projectConfigurationCfId)
        : null;
  }

  @SuppressWarnings("unused")
  public Collection<JiraWorkflow> getAllWorkflows() {
    return projectConfiguratorManager.getAllWorkflows();
  }

  @SuppressWarnings("unused")
  public Collection<IssueType> getAllIssueTypes() {
    return projectConfiguratorManager.getAllIssueTypes();
  }

  @SuppressWarnings("unused")
  public Collection<FieldScreenScheme> getAllScreenSchemes() {
    return projectConfiguratorManager.getAllScreenSchemes();
  }

  @SuppressWarnings("unused")
  public Collection<PermissionScheme> getAllPermissionSchemes() {
    return projectConfiguratorManager.getAllPermissionSchemes();
  }

  @SuppressWarnings("unused")
  public Collection<Scheme> getAllNotificationSchemes() {
    return projectConfiguratorManager.getAllNotificationSchemes();
  }

  @SuppressWarnings("unused")
  public Collection<ApplicationUser> getAllAdminUsers() {
    return projectConfiguratorManager.getAllAdminUsers();
  }

  @SuppressWarnings("unused")
  public Collection<Project> getAllProjects() {
    return projectConfiguratorManager.getAllProjects();
  }

  @SuppressWarnings("unused")
  public Collection<CustomField> getAllProjectConfiguratorCustomFields() {
    return projectConfiguratorManager.getAllProjectConfiguratorCustomFields();
  }

  @SuppressWarnings("unused")
  public String getContextPath() {
    return getHttpRequest().getContextPath();
  }
}
