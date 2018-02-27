package ru.mail.jira.plugins.projectconfigurator.configuration;

import com.atlassian.jira.issue.fields.screen.FieldScreenScheme;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.notification.NotificationScheme;
import com.atlassian.jira.permission.PermissionScheme;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.jira.workflow.JiraWorkflow;
import org.apache.commons.lang3.StringUtils;
import ru.mail.jira.plugins.commons.CommonUtils;

import java.util.Collection;
import java.util.List;

public class ProjectConfiguratorConfigurationEdit extends JiraWebActionSupport {
    private List<String> workflowNames;
    private List<String> issueTypeIds;
    private List<String> screenSchemeIds;
    private List<String> permissionSchemeIds;
    private List<String> notificationSchemeIds;

    private final PluginData pluginData;
    private final ProjectConfiguratorManager projectConfiguratorManager;

    public ProjectConfiguratorConfigurationEdit(PluginData pluginData, ProjectConfiguratorManager projectConfiguratorManager) {
        this.pluginData = pluginData;
        this.projectConfiguratorManager = projectConfiguratorManager;
    }

    @Override
    public void doValidation() {
        if (workflowNames == null || workflowNames.size() == 0)
            addError("project-configurator-workflows", getText("issue.field.required", "Workflows"));
        if (issueTypeIds == null || issueTypeIds.size() == 0)
            addError("project-configurator-issue-types", getText("issue.field.required", "Issue Types"));
        if (screenSchemeIds == null || screenSchemeIds.size() == 0)
            addError("project-configurator-screen-schemes", getText("issue.field.required", "Field Screen Schemes"));
        if (permissionSchemeIds == null || permissionSchemeIds.size() == 0)
            addError("project-configurator-permission-schemes", getText("issue.field.required", "Permission Schemes"));
        if (notificationSchemeIds == null || notificationSchemeIds.size() == 0)
            addError("project-configurator-notification-schemes", getText("issue.field.required", "Notification Schemes"));
    }

    @Override
    public String doDefault() {
        workflowNames = pluginData.getWorkflowNames();
        issueTypeIds = pluginData.getIssueTypeIds();
        screenSchemeIds = pluginData.getScreenSchemeIds();
        permissionSchemeIds = pluginData.getPermissionSchemeIds();
        notificationSchemeIds = pluginData.getNotificationSchemeIds();
        return INPUT;
    }

    @Override
    public String doExecute() {
        pluginData.setWorkflowNames(workflowNames);
        pluginData.setIssueTypeIds(issueTypeIds);
        pluginData.setScreenSchemeIds(screenSchemeIds);
        pluginData.setPermissionSchemeIds(permissionSchemeIds);
        pluginData.setNotificationSchemeIds(notificationSchemeIds);
        return getRedirect("/secure/admin/ProjectConfiguratorConfiguration.jspa");
    }

    @SuppressWarnings("unused")
    public String getWorkflowNames() {
        return this.workflowNames != null ? CommonUtils.join(this.workflowNames) : "";
    }

    @SuppressWarnings("unused")
    public void setWorkflowNames(String workflowNames) {
        this.workflowNames = StringUtils.isBlank(workflowNames) ? null : CommonUtils.split(workflowNames);
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
        this.screenSchemeIds = StringUtils.isBlank(screenSchemeIds) ? null : CommonUtils.split(screenSchemeIds);
    }

    @SuppressWarnings("unused")
    public String getPermissionSchemeIds() {
        return this.permissionSchemeIds != null ? CommonUtils.join(this.permissionSchemeIds) : "";
    }

    @SuppressWarnings("unused")
    public void setPermissionSchemeIds(String permissionSchemeIds) {
        this.permissionSchemeIds = StringUtils.isBlank(permissionSchemeIds) ? null : CommonUtils.split(permissionSchemeIds);
    }

    @SuppressWarnings("unused")
    public String getNotificationSchemeIds() {
        return this.notificationSchemeIds != null ? CommonUtils.join(this.notificationSchemeIds) : "";
    }

    @SuppressWarnings("unused")
    public void setNotificationSchemeIds(String notificationSchemeIds) {
        this.notificationSchemeIds = StringUtils.isBlank(notificationSchemeIds) ? null : CommonUtils.split(notificationSchemeIds);
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
    public String getContextPath() {
        return getHttpRequest().getContextPath();
    }
}
