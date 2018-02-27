package ru.mail.jira.plugins.projectconfigurator.configuration;

import java.util.List;

public interface PluginData {
    List<String> getWorkflowNames();
    void setWorkflowNames(List<String> workflowNames);

    List<String> getIssueTypeIds();
    void setIssueTypeIds(List<String> issueTypeIds);

    List<String> getScreenSchemeIds();
    void setScreenSchemeIds(List<String> screenSchemeIds);

    List<String> getPermissionSchemeIds();
    void setPermissionSchemeIds(List<String> permissionSchemeIds);

    List<String> getNotificationSchemeIds();
    void setNotificationSchemeIds(List<String> notificationSchemeIds);
}
