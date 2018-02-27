package ru.mail.jira.plugins.projectconfigurator.customfield;

import com.atlassian.jira.issue.fields.screen.FieldScreenScheme;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.notification.NotificationScheme;
import com.atlassian.jira.permission.PermissionScheme;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
public class ProjectConfiguration {
    private String projectName;
    private String projectKey;
    private Collection<JiraWorkflow> workflows;
    private Map<IssueType, JiraWorkflow> issueTypes;
    private Map<IssueType, FieldScreenScheme> screenSchemes;
    private Map<ProjectRole, Collection<ApplicationUser>> roles;
    private PermissionScheme permissionScheme;
    private NotificationScheme notificationScheme;

    public String toString() {
        JsonArray workflowNames = new JsonArray();
        for (JiraWorkflow workflow : this.workflows)
            workflowNames.add(new JsonPrimitive(workflow.getName()));
        JsonArray issueTypeIds = new JsonArray();
        for (Map.Entry<IssueType, JiraWorkflow> issueType : this.issueTypes.entrySet()) {
            JsonObject issueTypeId = new JsonObject();
            issueTypeId.addProperty("issueTypeId", issueType.getKey().getId());
            issueTypeId.addProperty("workflowName", issueType.getValue().getName());
            issueTypeIds.add(issueTypeId);
        }
        JsonArray screenSchemeIds = new JsonArray();
        for (Map.Entry<IssueType, FieldScreenScheme> screenScheme : this.screenSchemes.entrySet()) {
            JsonObject screenSchemeId = new JsonObject();
            screenSchemeId.addProperty("issueTypeId", screenScheme.getKey().getId());
            screenSchemeId.addProperty("screenSchemeId", screenScheme.getValue().getId());
            screenSchemeIds.add(screenSchemeId);
        }
        JsonArray roleIds = new JsonArray();
        for (Map.Entry<ProjectRole, Collection<ApplicationUser>> role : this.roles.entrySet()) {
            JsonArray users = new JsonArray();
            for (ApplicationUser user : role.getValue())
                users.add(new JsonPrimitive(user.getKey()));
            JsonObject roleId = new JsonObject();
            roleId.addProperty("roleId", role.getKey().getId());
            roleId.add("userKeys", users);
            roleIds.add(roleId);
        }

        JsonObject result = new JsonObject();
        result.addProperty("projectName", this.projectName);
        result.addProperty("projectKey", this.projectKey);
        result.add("workflowNames", workflowNames);
        result.add("issueTypes", issueTypeIds);
        result.add("screenSchemes", screenSchemeIds);
        result.add("roles", roleIds);
        result.addProperty("permissionSchemeId", this.permissionScheme.getId());
        result.addProperty("notificationSchemeId", this.notificationScheme.getId());
        return result.toString();
    }
}
