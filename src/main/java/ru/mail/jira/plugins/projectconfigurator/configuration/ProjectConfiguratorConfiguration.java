package ru.mail.jira.plugins.projectconfigurator.configuration;

import com.atlassian.jira.issue.fields.screen.FieldScreenScheme;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.notification.NotificationScheme;
import com.atlassian.jira.permission.PermissionScheme;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.jira.workflow.JiraWorkflow;

import java.util.List;

public class ProjectConfiguratorConfiguration extends JiraWebActionSupport {
    private List<String> workflowNames;
    private List<String> issueTypeIds;
    private List<String> screenSchemeIds;
    private List<String> permissionSchemeIds;
    private List<String> notificationSchemeIds;

    private final PluginData pluginData;
    private final ProjectConfiguratorManager projectConfiguratorManager;

    public ProjectConfiguratorConfiguration(PluginData pluginData, ProjectConfiguratorManager projectConfiguratorManager) {
        this.pluginData = pluginData;
        this.projectConfiguratorManager = projectConfiguratorManager;
    }

    @Override
    public String doExecute() {
        workflowNames = pluginData.getWorkflowNames();
        issueTypeIds = pluginData.getIssueTypeIds();
        screenSchemeIds = pluginData.getScreenSchemeIds();
        permissionSchemeIds = pluginData.getPermissionSchemeIds();
        notificationSchemeIds = pluginData.getNotificationSchemeIds();
        return SUCCESS;
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
    public String getContextPath() {
        return getHttpRequest().getContextPath();
    }
}
