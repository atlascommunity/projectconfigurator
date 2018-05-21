package ru.mail.jira.plugins.projectconfigurator.configuration;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

import java.util.List;

public class PluginDataImpl implements PluginData {
    private static final String PLUGIN_PREFIX = "ru.mail.jira.plugins.projectconfigurator:";
    private static final String WORKFLOW_NAMES = PLUGIN_PREFIX + "workflowNames";
    private static final String ISSUE_TYPE_IDS = PLUGIN_PREFIX + "issueTypeIds";
    private static final String SCREEN_SCHEME_IDS = PLUGIN_PREFIX + "screenSchemeIds";
    private static final String PERMISSION_SCHEME_IDS = PLUGIN_PREFIX + "permissionSchemeIds";
    private static final String NOTIFICATION_SCHEME_IDS = PLUGIN_PREFIX + "notificationSchemeIds";
    private static final String ADMIN_USER_KEY = PLUGIN_PREFIX + "adminUserKey";

    private static final String PROJECT_ID = PLUGIN_PREFIX + "projectId";
    private static final String ISSUE_TYPE_ID = PLUGIN_PREFIX + "issueTypeId";
    private static final String PROJECT_CONFIGURATION_CF_ID = PLUGIN_PREFIX + "projectConfigurationCfId";

    private final PluginSettingsFactory pluginSettingsFactory;

    public PluginDataImpl(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public List<String> getWorkflowNames() {
        return (List<String>) pluginSettingsFactory.createGlobalSettings().get(WORKFLOW_NAMES);
    }

    @Override
    public void setWorkflowNames(List<String> workflowNames) {
        pluginSettingsFactory.createGlobalSettings().put(WORKFLOW_NAMES, workflowNames);
    }

    @Override
    public List<String> getIssueTypeIds() {
        return (List<String>) pluginSettingsFactory.createGlobalSettings().get(ISSUE_TYPE_IDS);
    }

    @Override
    public void setIssueTypeIds(List<String> issueTypeIds) {
        pluginSettingsFactory.createGlobalSettings().put(ISSUE_TYPE_IDS, issueTypeIds);
    }

    @Override
    public List<String> getScreenSchemeIds() {
        return (List<String>) pluginSettingsFactory.createGlobalSettings().get(SCREEN_SCHEME_IDS);
    }

    @Override
    public void setScreenSchemeIds(List<String> screenSchemeIds) {
        pluginSettingsFactory.createGlobalSettings().put(SCREEN_SCHEME_IDS, screenSchemeIds);
    }

    @Override
    public List<String> getPermissionSchemeIds() {
        return (List<String>) pluginSettingsFactory.createGlobalSettings().get(PERMISSION_SCHEME_IDS);
    }

    @Override
    public void setPermissionSchemeIds(List<String> permissionSchemeIds) {
        pluginSettingsFactory.createGlobalSettings().put(PERMISSION_SCHEME_IDS, permissionSchemeIds);
    }

    @Override
    public List<String> getNotificationSchemeIds() {
        return (List<String>) pluginSettingsFactory.createGlobalSettings().get(NOTIFICATION_SCHEME_IDS);
    }

    @Override
    public void setNotificationSchemeIds(List<String> notificationSchemeIds) {
        pluginSettingsFactory.createGlobalSettings().put(NOTIFICATION_SCHEME_IDS, notificationSchemeIds);
    }

    @Override
    public String getAdminUserKey() {
        return (String) pluginSettingsFactory.createGlobalSettings().get(ADMIN_USER_KEY);
    }

    @Override
    public void setAdminUserKey(String adminUserKey) {
        pluginSettingsFactory.createGlobalSettings().put(ADMIN_USER_KEY, adminUserKey);
    }

    @Override
    public String getProjectId() {
        return (String) pluginSettingsFactory.createGlobalSettings().get(PROJECT_ID);
    }

    @Override
    public void setProjectId(String projectId) {
        pluginSettingsFactory.createGlobalSettings().put(PROJECT_ID, projectId);
    }

    @Override
    public String getIssueTypeId() {
        return (String) pluginSettingsFactory.createGlobalSettings().get(ISSUE_TYPE_ID);
    }

    @Override
    public void setIssueTypeId(String issueTypeId) {
        pluginSettingsFactory.createGlobalSettings().put(ISSUE_TYPE_ID, issueTypeId);
    }

    @Override
    public String getProjectConfigurationCfId() {
        return (String) pluginSettingsFactory.createGlobalSettings().get(PROJECT_CONFIGURATION_CF_ID);
    }

    @Override
    public void setProjectConfigurationCfId(String projectConfigurationCfId) {
        pluginSettingsFactory.createGlobalSettings().put(PROJECT_CONFIGURATION_CF_ID, projectConfigurationCfId);
    }
}
