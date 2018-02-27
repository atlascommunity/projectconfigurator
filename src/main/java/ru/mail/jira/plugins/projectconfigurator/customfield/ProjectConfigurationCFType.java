package ru.mail.jira.plugins.projectconfigurator.customfield;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.customfields.impl.AbstractSingleFieldType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.persistence.PersistenceFieldType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.screen.FieldScreenScheme;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.notification.NotificationScheme;
import com.atlassian.jira.permission.PermissionScheme;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mail.jira.plugins.commons.RestExecutor;
import ru.mail.jira.plugins.projectconfigurator.configuration.ProjectConfiguratorManager;
import ru.mail.jira.plugins.projectconfigurator.customfield.dto.IssueTypeDto;
import ru.mail.jira.plugins.projectconfigurator.customfield.dto.ProjectConfigurationDto;
import ru.mail.jira.plugins.projectconfigurator.customfield.dto.RoleDto;
import ru.mail.jira.plugins.projectconfigurator.customfield.dto.ScreenSchemeDto;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Path("/customfield")
public class ProjectConfigurationCFType extends AbstractSingleFieldType<ProjectConfiguration> {
    private final static Logger log = LoggerFactory.getLogger(ProjectConfigurationCFType.class);

    private final CustomFieldManager customFieldManager;
    private final IssueManager issueManager;
    private final IssueService issueService;
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final ProjectConfiguratorManager projectConfiguratorManager;
    private final ProjectRoleManager projectRoleManager;
    private final UserManager userManager;

    protected ProjectConfigurationCFType(CustomFieldValuePersister customFieldValuePersister, GenericConfigManager genericConfigManager, CustomFieldManager customFieldManager, IssueManager issueManager, IssueService issueService, JiraAuthenticationContext jiraAuthenticationContext, ProjectConfiguratorManager projectConfiguratorManager, ProjectRoleManager projectRoleManager, UserManager userManager) {
        super(customFieldValuePersister, genericConfigManager);
        this.customFieldManager = customFieldManager;
        this.issueManager = issueManager;
        this.issueService = issueService;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.projectConfiguratorManager = projectConfiguratorManager;
        this.projectRoleManager = projectRoleManager;
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
    protected ProjectConfiguration getObjectFromDbValue(@Nonnull Object value) throws FieldValidationException {
        return getSingularObjectFromString((String) value);
    }

    @Override
    public String getStringFromSingularObject(ProjectConfiguration value) {
        return value == null ? "" : value.toString();
    }

    @Override
    public ProjectConfiguration getSingularObjectFromString(String value) throws FieldValidationException {
        try {
            return StringUtils.isEmpty(value) ? null : buildValue(value);
        } catch (Exception e) {
            throw new FieldValidationException(e.getMessage());
        }
    }

    private ProjectConfiguration buildValue(String strValue) {
        try {
            if (StringUtils.isEmpty(strValue))
                return null;

            JsonObject jsonProjectConfiguration = new JsonParser().parse(strValue).getAsJsonObject();
            Collection<JiraWorkflow> workflows = new ArrayList<>();
            for (JsonElement workflowName : jsonProjectConfiguration.getAsJsonArray("workflowNames"))
                workflows.add(projectConfiguratorManager.getWorkflow(workflowName.getAsJsonPrimitive().getAsString()));
            Map<IssueType, JiraWorkflow> issueTypes = new HashMap<>();
            for (JsonElement issueTypeId : jsonProjectConfiguration.getAsJsonArray("issueTypes")) {
                JsonObject issueType = issueTypeId.getAsJsonObject();
                issueTypes.put(projectConfiguratorManager.getIssueType(issueType.get("issueTypeId").getAsString()), projectConfiguratorManager.getWorkflow(issueType.get("workflowName").getAsString()));
            }
            Map<IssueType, FieldScreenScheme> screenSchemes = new HashMap<>();
            for (JsonElement screenSchemeId : jsonProjectConfiguration.getAsJsonArray("screenSchemes")) {
                JsonObject screenScheme = screenSchemeId.getAsJsonObject();
                screenSchemes.put(projectConfiguratorManager.getIssueType(screenScheme.get("issueTypeId").getAsString()), projectConfiguratorManager.getFieldScreenScheme(screenScheme.get("screenSchemeId").getAsLong()));
            }
            Map<ProjectRole, Collection<ApplicationUser>> roles = new HashMap<>();
            for (JsonElement roleId : jsonProjectConfiguration.getAsJsonArray("roles")) {
                JsonObject role = roleId.getAsJsonObject();
                Collection<ApplicationUser> users = new ArrayList<>();
                for (JsonElement userKey : role.getAsJsonArray("userKeys"))
                    users.add(userManager.getUserByKey(userKey.getAsJsonPrimitive().getAsString()));
                roles.put(projectRoleManager.getProjectRole(role.get("roleId").getAsLong()), users);
            }

            ProjectConfiguration value = new ProjectConfiguration();
            value.setProjectName(jsonProjectConfiguration.get("projectName").getAsString());
            value.setProjectKey(jsonProjectConfiguration.get("projectKey").getAsString());
            value.setWorkflows(workflows);
            value.setIssueTypes(issueTypes);
            value.setScreenSchemes(screenSchemes);
            value.setRoles(roles);
            value.setPermissionScheme(projectConfiguratorManager.getPermissionScheme(jsonProjectConfiguration.get("permissionSchemeId").getAsLong()));
            value.setNotificationScheme(projectConfiguratorManager.getNotificationScheme(jsonProjectConfiguration.get("notificationSchemeId").getAsLong()));
            return value;
        } catch (FieldValidationException e) {
            throw e;
        } catch (JsonSyntaxException e) {
            String errorMsg = "Bad value => " + strValue;
            log.error(errorMsg, e);
            throw new FieldValidationException(errorMsg);
        } catch (Exception e) {
            String errorMsg = "Error while trying to build value for project configuration picker. String value => " + strValue;
            log.error(errorMsg, e);
            throw new FieldValidationException(e.getMessage());
        }
    }

    private ProjectConfiguration buildValueFromDto(ProjectConfigurationDto projectConfigurationDto) {
        String projectName = projectConfigurationDto.getProjectName();
        String projectKey = projectConfigurationDto.getProjectKey();
        Collection<JiraWorkflow> workflows = new ArrayList<>();
        for (String workflowName : projectConfigurationDto.getWorkflowNames())
            workflows.add(projectConfiguratorManager.getWorkflow(workflowName));
        Map<IssueType, JiraWorkflow> issueTypes = new HashMap<>();
        for (IssueTypeDto issueTypeDto : projectConfigurationDto.getIssueTypes()) {
            issueTypes.put(projectConfiguratorManager.getIssueType(issueTypeDto.getIssueTypeId()), projectConfiguratorManager.getWorkflow(issueTypeDto.getWorkflowName()));
        }
        Map<IssueType, FieldScreenScheme> screenSchemes = new HashMap<>();
        for (ScreenSchemeDto screenSchemeDto : projectConfigurationDto.getScreenSchemes()) {
            screenSchemes.put(projectConfiguratorManager.getIssueType(screenSchemeDto.getIssueTypeId()), projectConfiguratorManager.getFieldScreenScheme(screenSchemeDto.getScreenSchemeId()));
        }
        Map<ProjectRole, Collection<ApplicationUser>> roles = new HashMap<>();
        for (RoleDto roleDto : projectConfigurationDto.getRoles()) {
            Collection<ApplicationUser> users = new ArrayList<>();
            for (String userKey : roleDto.getUserKeys())
                users.add(userManager.getUserByKey(userKey));
            roles.put(projectRoleManager.getProjectRole(roleDto.getRoleId()), users);
        }
        PermissionScheme permissionScheme = projectConfiguratorManager.getPermissionScheme(projectConfigurationDto.getPermissionSchemeId());
        NotificationScheme notificationScheme = projectConfiguratorManager.getNotificationScheme(projectConfigurationDto.getNotificationSchemeId());

        ProjectConfiguration value = new ProjectConfiguration();
        value.setProjectName(projectName);
        value.setProjectKey(projectKey);
        value.setWorkflows(workflows);
        value.setIssueTypes(issueTypes);
        value.setScreenSchemes(screenSchemes);
        value.setRoles(roles);
        value.setPermissionScheme(permissionScheme);
        value.setNotificationScheme(notificationScheme);
        return value;
    }

    @POST
    @Path("/{id}/{issueKey}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setCustomFieldValue(@PathParam("id") final long id,
                                        @PathParam("issueKey") final String issueKey,
                                        final ProjectConfigurationDto projectConfigurationDto) {
        return new RestExecutor<Void>() {
            @Override
            protected Void doAction() throws Exception {
                MutableIssue issue = issueManager.getIssueObject(issueKey);
                if (issue == null)
                    return null;
                CustomField customField = customFieldManager.getCustomFieldObject(id);
                if (customField == null)
                    return null;

                ProjectConfiguration fieldValue = buildValueFromDto(projectConfigurationDto);
                if (fieldValue != null) {
                    IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
                    issueInputParameters.addCustomFieldValue(id, fieldValue.toString());
                    IssueService.UpdateValidationResult validationResult = issueService.validateUpdate(jiraAuthenticationContext.getLoggedInUser(), issue.getId(), issueInputParameters);
                    issueService.update(jiraAuthenticationContext.getLoggedInUser(), validationResult);
                }

                return null;
            }
        }.getResponse();
    }
}
