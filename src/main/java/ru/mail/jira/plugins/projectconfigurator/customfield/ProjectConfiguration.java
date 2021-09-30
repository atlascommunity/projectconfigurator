/* (C)2020 */
package ru.mail.jira.plugins.projectconfigurator.customfield;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.jira.issue.fields.screen.FieldScreenScheme;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.notification.NotificationScheme;
import com.atlassian.jira.permission.PermissionScheme;
import com.atlassian.jira.project.type.ProjectType;
import com.atlassian.jira.project.type.ProjectTypeKeys;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectConfiguration {

  private String projectName;
  private String projectKey;
  private ApplicationUser projectLead;
  private ProjectType projectType;
  private List<JiraProcess> processes;
  private List<Role> roles;
  private PermissionScheme permissionScheme;
  private NotificationScheme notificationScheme;

  @Override
  public String toString() {
    JsonArray processesJsonArray = new JsonArray();
    for (JiraProcess process : processes) {
      processesJsonArray.add(process.toJson());
    }

    JsonArray rolesJsonArray = new JsonArray();
    for (Role role : roles) {
      rolesJsonArray.add(role.toJson());
    }

    JsonObject result = new JsonObject();
    result.addProperty("projectName", this.projectName);
    result.addProperty("projectKey", this.projectKey);
    result.addProperty("projectLeadKey", this.projectLead.getKey());
    result.addProperty(
        "projectType",
        this.projectType != null
            ? this.projectType.getKey().getKey()
            : ProjectTypeKeys.BUSINESS.getKey());
    result.add("processes", processesJsonArray);
    result.add("roles", rolesJsonArray);
    result.addProperty("permissionSchemeId", this.permissionScheme.getId());
    result.addProperty("notificationSchemeId", this.notificationScheme.getId());
    return result.toString();
  }

  @Getter
  @Setter
  public static class JiraProcess {

    private IssueType issueType;
    private JiraWorkflow jiraWorkflow;
    private FieldScreenScheme fieldScreenScheme;

    public JiraProcess() {}

    private JsonObject toJson() {
      JsonObject result = new JsonObject();
      result.addProperty("issueTypeId", issueType.getId());
      result.addProperty("workflowName", jiraWorkflow.getName());
      result.addProperty("screenSchemeId", fieldScreenScheme.getId());
      return result;
    }

    @Override
    public String toString() {
      return toJson().toString();
    }
  }

  @Getter
  @Setter
  public static class Role {

    private ProjectRole projectRole;
    private Collection<ApplicationUser> users;
    private Collection<Group> groups;

    public Role() {}

    private JsonObject toJson() {
      JsonObject result = new JsonObject();
      result.addProperty("projectRoleId", projectRole.getId());

      if (users.size() > 0) {
        JsonArray usersJsonArray = new JsonArray();
        for (ApplicationUser user : users) {
          usersJsonArray.add(new JsonPrimitive(user.getKey()));
        }
        result.add("userKeys", usersJsonArray);
      }

      if (groups.size() > 0) {
        JsonArray groupsJsonArray = new JsonArray();
        for (Group group : groups) {
          groupsJsonArray.add(new JsonPrimitive(group.getName()));
        }
        result.add("groupNames", groupsJsonArray);
      }
      return result;
    }

    @Override
    public String toString() {
      return toJson().toString();
    }
  }
}
