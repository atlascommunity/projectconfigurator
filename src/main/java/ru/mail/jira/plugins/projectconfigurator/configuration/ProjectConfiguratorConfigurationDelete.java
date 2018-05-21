package ru.mail.jira.plugins.projectconfigurator.configuration;

import com.atlassian.jira.web.action.JiraWebActionSupport;

public class ProjectConfiguratorConfigurationDelete extends JiraWebActionSupport {
    private final PluginData pluginData;

    public ProjectConfiguratorConfigurationDelete(PluginData pluginData) {
        this.pluginData = pluginData;
    }

    @Override
    public String doDefault() {
        return INPUT;
    }

    @Override
    public String doExecute() {
        pluginData.setWorkflowNames(null);
        pluginData.setIssueTypeIds(null);
        pluginData.setScreenSchemeIds(null);
        pluginData.setPermissionSchemeIds(null);
        pluginData.setNotificationSchemeIds(null);
        pluginData.setAdminUserKey(null);

        pluginData.setProjectId(null);
        pluginData.setIssueTypeId(null);
        pluginData.setProjectConfigurationCfId(null);

        return getRedirect("/secure/admin/ProjectConfiguratorConfiguration.jspa");
    }

    @SuppressWarnings("unused")
    public String getContextPath() {
        return getHttpRequest().getContextPath();
    }
}
