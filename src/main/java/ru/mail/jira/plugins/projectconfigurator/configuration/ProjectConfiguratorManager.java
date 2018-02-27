package ru.mail.jira.plugins.projectconfigurator.configuration;

import com.atlassian.jira.bc.issue.fields.screen.FieldScreenService;
import com.atlassian.jira.bc.project.ProjectCreationData;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.bc.projectroles.ProjectRoleService;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.entity.WithFunctions;
import com.atlassian.jira.issue.context.JiraContextNode;
import com.atlassian.jira.issue.customfields.CustomFieldUtils;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManager;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.issue.fields.screen.FieldScreenFactory;
import com.atlassian.jira.issue.fields.screen.FieldScreenManager;
import com.atlassian.jira.issue.fields.screen.FieldScreenScheme;
import com.atlassian.jira.issue.fields.screen.FieldScreenSchemeItem;
import com.atlassian.jira.issue.fields.screen.FieldScreenSchemeItemImpl;
import com.atlassian.jira.issue.fields.screen.FieldScreenSchemeManager;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenScheme;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenSchemeEntity;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenSchemeManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.notification.NotificationScheme;
import com.atlassian.jira.notification.NotificationSchemeManager;
import com.atlassian.jira.notification.NotificationSchemeService;
import com.atlassian.jira.permission.PermissionScheme;
import com.atlassian.jira.permission.PermissionSchemeManager;
import com.atlassian.jira.permission.PermissionSchemeService;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.type.ProjectTypeKeys;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.scheme.SchemeManager;
import com.atlassian.jira.scheme.SchemeManagerFactory;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleActor;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.jira.workflow.AssignableWorkflowScheme;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.jira.workflow.WorkflowSchemeManager;
import com.atlassian.jira.workflow.migration.AssignableWorkflowSchemeMigrationHelper;
import com.atlassian.jira.workflow.migration.MigrationHelperFactory;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import ru.mail.jira.plugins.projectconfigurator.customfield.ProjectConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProjectConfiguratorManager {
    private final String ADMIN_USER_KEY = "admin";

    private final I18nHelper i18nHelper;
    private final IssueTypeManager issueTypeManager;
    private final IssueTypeSchemeManager issueTypeSchemeManager;
    private final IssueTypeScreenSchemeManager issueTypeScreenSchemeManager;
    private final FieldConfigSchemeManager fieldConfigSchemeManager;
    private final FieldManager fieldManager;
    private final FieldScreenFactory fieldScreenFactory;
    private final FieldScreenManager fieldScreenManager;
    private final FieldScreenService fieldScreenService;
    private final FieldScreenSchemeManager fieldScreenSchemeManager;
    private final MigrationHelperFactory migrationHelperFactory;
    private final NotificationSchemeManager notificationSchemeManager;
    private final NotificationSchemeService notificationSchemeService;
    private final PermissionSchemeManager permissionSchemeManager;
    private final PermissionSchemeService permissionSchemeService;
    private final ProjectManager projectManager;
    private final ProjectService projectService;
    private final ProjectRoleService projectRoleService;
    private final SchemeManagerFactory  schemeManagerFactory;
    private final WorkflowManager workflowManager;
    private final WorkflowSchemeManager workflowSchemeManager;
    private final UserManager userManager;

    public ProjectConfiguratorManager(I18nHelper i18nHelper, IssueTypeManager issueTypeManager, IssueTypeSchemeManager issueTypeSchemeManager, IssueTypeScreenSchemeManager issueTypeScreenSchemeManager, FieldConfigSchemeManager fieldConfigSchemeManager, FieldManager fieldManager, FieldScreenFactory fieldScreenFactory, FieldScreenManager fieldScreenManager, FieldScreenService fieldScreenService, MigrationHelperFactory migrationHelperFactory, NotificationSchemeService notificationSchemeService, PermissionSchemeService permissionSchemeService, FieldScreenSchemeManager fieldScreenSchemeManager, NotificationSchemeManager notificationSchemeManager, PermissionSchemeManager permissionSchemeManager, ProjectManager projectManager, ProjectService projectService, ProjectRoleService projectRoleService, SchemeManagerFactory schemeManagerFactory, WorkflowManager workflowManager, WorkflowSchemeManager workflowSchemeManager, UserManager userManager) {
        this.i18nHelper = i18nHelper;
        this.issueTypeManager = issueTypeManager;
        this.issueTypeSchemeManager = issueTypeSchemeManager;
        this.issueTypeScreenSchemeManager = issueTypeScreenSchemeManager;
        this.fieldConfigSchemeManager = fieldConfigSchemeManager;
        this.fieldManager = fieldManager;
        this.fieldScreenFactory = fieldScreenFactory;
        this.fieldScreenManager = fieldScreenManager;
        this.fieldScreenService = fieldScreenService;
        this.notificationSchemeService = notificationSchemeService;
        this.fieldScreenSchemeManager = fieldScreenSchemeManager;
        this.migrationHelperFactory = migrationHelperFactory;
        this.permissionSchemeService = permissionSchemeService;
        this.notificationSchemeManager = notificationSchemeManager;
        this.permissionSchemeManager = permissionSchemeManager;
        this.projectManager = projectManager;
        this.projectService = projectService;
        this.projectRoleService = projectRoleService;
        this.schemeManagerFactory = schemeManagerFactory;
        this.workflowManager = workflowManager;
        this.workflowSchemeManager = workflowSchemeManager;
        this.userManager = userManager;
    }

    public JiraWorkflow getWorkflow(String name) {
        return workflowManager.getWorkflow(name);
    }

    public List<JiraWorkflow> getWorkflows(List<String> workflowNames) {
        List<JiraWorkflow> result = new ArrayList<JiraWorkflow>();
        if (workflowNames != null && workflowNames.size() > 0)
            for (String workflowName : workflowNames)
                result.add(workflowManager.getWorkflow(workflowName));
        return result;
    }

    public IssueType getIssueType(String id) {
        return issueTypeManager.getIssueType(id);
    }

    public List<IssueType> getIssueTypes(List<String> issueTypeIds) {
        List<IssueType> result = new ArrayList<IssueType>();
        if (issueTypeIds != null && issueTypeIds.size() > 0)
            for (String issueTypeId : issueTypeIds)
                result.add(issueTypeManager.getIssueType(issueTypeId));
        return result;
    }

    public FieldScreenScheme getFieldScreenScheme(Long id) {
        return fieldScreenSchemeManager.getFieldScreenScheme(id);
    }

    public List<FieldScreenScheme> getScreenSchemes(List<String> screenSchemeIds) {
        List<FieldScreenScheme> result = new ArrayList<FieldScreenScheme>();
        if (screenSchemeIds != null && screenSchemeIds.size() > 0)
            for (String screenSchemeId : screenSchemeIds)
                result.add(fieldScreenSchemeManager.getFieldScreenScheme(Long.parseLong(screenSchemeId)));
        return result;
    }

    public PermissionScheme getPermissionScheme(Long id) {
        return permissionSchemeService.getPermissionScheme(userManager.getUserByKey(ADMIN_USER_KEY), id).get();
    }

    public List<PermissionScheme> getPermissionSchemes(List<String> permissionSchemeIds) {
        List<PermissionScheme> result = new ArrayList<PermissionScheme>();
        if (permissionSchemeIds != null && permissionSchemeIds.size() > 0)
            for (String permissionSchemeId : permissionSchemeIds)
                result.add(permissionSchemeService.getPermissionScheme(userManager.getUserByKey(ADMIN_USER_KEY), Long.parseLong(permissionSchemeId)).get());
        return result;
    }

    public NotificationScheme getNotificationScheme(Long id) {
        return notificationSchemeService.getNotificationScheme(userManager.getUserByKey(ADMIN_USER_KEY), id).get();
    }

    public List<NotificationScheme> getNotificationSchemes(List<String> notificationSchemeIds) {
        List<NotificationScheme> result = new ArrayList<NotificationScheme>();
        if (notificationSchemeIds != null && notificationSchemeIds.size() > 0)
            for (String notificationSchemeId : notificationSchemeIds)
                result.add(notificationSchemeService.getNotificationScheme(userManager.getUserByKey(ADMIN_USER_KEY), Long.parseLong(notificationSchemeId)).get());
        return result;
    }

    public Collection<JiraWorkflow> getAllWorkflows() {
        return workflowManager.getWorkflows();
    }

    public Collection<IssueType> getAllIssueTypes() {
        return issueTypeManager.getIssueTypes();
    }

    public Collection<FieldScreenScheme> getAllScreenSchemes() {
        return fieldScreenSchemeManager.getFieldScreenSchemes();
    }

    public Collection<PermissionScheme> getAllPermissionSchemes() {
        return permissionSchemeService.getPermissionSchemes(userManager.getUserByKey(ADMIN_USER_KEY)).get();
    }

    public Collection<Scheme> getAllNotificationSchemes() {
        return notificationSchemeManager.getSchemeObjects();
    }

    private Collection<Long> getChangedProjectIds(FieldConfigScheme configScheme, List<Long> newProjectIds) {
        List<Project> previousProjects = configScheme.getAssociatedProjectObjects();
        List<Long> previousProjectIds = Collections.emptyList();
        if (previousProjects != null && !previousProjects.isEmpty())
            previousProjectIds = Lists.newArrayList(Iterables.transform(previousProjects, WithFunctions.getId()));

        Collection<Long> affectedProjectIds = CollectionUtils.disjunction(previousProjectIds, newProjectIds);
        return affectedProjectIds;
    }

    private FieldConfigScheme createFieldConfigScheme(String projectKey, Set<IssueType> issueTypes) {
        List<String> issueTypeIds = new ArrayList<>();
        for (IssueType issueType : issueTypes)
            issueTypeIds.add(issueType.getId());
        return issueTypeSchemeManager.create(String.format("%s Issue Types Scheme", projectKey), null, issueTypeIds);
    }

    private void associateIssueTypeSchemeWithProject(Project project, Set<IssueType> issueTypes) {
        FieldConfigScheme fieldConfigScheme = createFieldConfigScheme(project.getKey(), issueTypes);
        List<JiraContextNode> contexts = CustomFieldUtils.buildJiraIssueContexts(false, new Long[]{project.getId()}, projectManager);
        fieldConfigSchemeManager.updateFieldConfigScheme(fieldConfigScheme, contexts, fieldManager.getConfigurableField(fieldManager.getIssueTypeField().getId()));
        fieldManager.refresh();
    }

    private FieldScreenScheme copyFieldScreenScheme(String projectKey, String issueTypeName, FieldScreenScheme fieldScreenScheme) {
        FieldScreenScheme copyFieldScreenScheme = fieldScreenFactory.createFieldScreenScheme();
        copyFieldScreenScheme.setName(String.format("%s %s Screen Scheme", projectKey, issueTypeName));
        copyFieldScreenScheme.setDescription(null);
        copyFieldScreenScheme.store();

        // Copy all the scheme entities
        for (FieldScreenSchemeItem fieldScreenSchemeItem : fieldScreenScheme.getFieldScreenSchemeItems()) {
            FieldScreen copyScreen = fieldScreenService.copy(fieldScreenSchemeItem.getFieldScreen(), String.format("%s %s %s Screen", projectKey, issueTypeName, i18nHelper.getText(fieldScreenSchemeItem.getIssueOperationName())), fieldScreenSchemeItem.getFieldScreen().getDescription(), userManager.getUserByKey("admin")).get();
            FieldScreenSchemeItem copyFieldScreenSchemeItem = new FieldScreenSchemeItemImpl(fieldScreenSchemeManager, fieldScreenSchemeItem, fieldScreenManager);
            copyFieldScreenSchemeItem.setFieldScreen(copyScreen);
            copyFieldScreenScheme.addFieldScreenSchemeItem(copyFieldScreenSchemeItem);
        }
        return copyFieldScreenScheme;
    }

    private IssueTypeScreenSchemeEntity createIssueTypeScreenSchemeEntity(String issueTypeId, FieldScreenScheme fieldScreenScheme) {
        IssueTypeScreenSchemeEntity issueTypeScreenSchemeEntity = fieldScreenFactory.createIssueTypeScreenSchemeEntity();
        issueTypeScreenSchemeEntity.setIssueTypeId(issueTypeId);
        issueTypeScreenSchemeEntity.setFieldScreenScheme(fieldScreenScheme);
        return issueTypeScreenSchemeEntity;
    }

    private IssueTypeScreenScheme createIssueTypeScreenScheme(String projectKey, Map<IssueType, FieldScreenScheme> issueTypeFieldScreenSchemeMap) {
        final IssueTypeScreenScheme issueTypeScreenScheme = fieldScreenFactory.createIssueTypeScreenScheme();
        issueTypeScreenScheme.setName(String.format("%s Issue Types Screen Scheme", projectKey));
        issueTypeScreenScheme.setDescription(null);
        issueTypeScreenScheme.store();

        // Add default screen scheme
        issueTypeScreenScheme.addEntity(createIssueTypeScreenSchemeEntity(null, fieldScreenSchemeManager.getFieldScreenScheme(FieldScreenSchemeManager.DEFAULT_FIELD_SCREEN_SCHEME_ID)));
        for (Map.Entry<IssueType, FieldScreenScheme> entry : issueTypeFieldScreenSchemeMap.entrySet()) {
            IssueType issueType = entry.getKey();
            FieldScreenScheme copiedScreenScheme = copyFieldScreenScheme(projectKey, issueType.getName(), entry.getValue());
            issueTypeScreenScheme.addEntity(createIssueTypeScreenSchemeEntity(issueType.getId(), copiedScreenScheme));
        }
        return issueTypeScreenScheme;
    }

    private void associateIssueTypeScreenSchemeWithProject(Project project, Map<IssueType, FieldScreenScheme> screenSchemes) {
        IssueTypeScreenScheme issueTypeScreenScheme = createIssueTypeScreenScheme(project.getKey(), screenSchemes);
        issueTypeScreenSchemeManager.addSchemeAssociation(project, issueTypeScreenScheme);
    }

    private AssignableWorkflowScheme createWorkflowScheme(String projectKey, Map<IssueType, JiraWorkflow> workflows) {
        AssignableWorkflowScheme.Builder assignableWorkflowSchemeBuilder = workflowSchemeManager.assignableBuilder();
        assignableWorkflowSchemeBuilder.setName(String.format("%s Workflow Scheme", projectKey));
        for(Map.Entry<IssueType, JiraWorkflow> entry : workflows.entrySet()) {
            IssueType issueType = entry.getKey();
            JiraWorkflow workflow = entry.getValue();
            JiraWorkflow copyWorkflow = workflowManager.copyWorkflow(userManager.getUserByKey("admin"), String.format("%s %s Workflow", projectKey, issueType.getName()), null, workflow);
            assignableWorkflowSchemeBuilder.setMapping(issueType.getId(), copyWorkflow.getName());
        }
        return workflowSchemeManager.createScheme(assignableWorkflowSchemeBuilder.build());
    }

    private void associateWorkflowSchemeWithProject(Project project, Map<IssueType, JiraWorkflow> workflows) throws Exception {
        AssignableWorkflowScheme workflowScheme = createWorkflowScheme(project.getKey(), workflows);
        AssignableWorkflowSchemeMigrationHelper migrationHelper = migrationHelperFactory.createMigrationHelper(project, workflowScheme);
        migrationHelper.associateProjectAndWorkflowScheme();
    }

    private void associatePermissionSchemeWithProject(Project project, PermissionScheme permissionScheme) {
        Scheme copyScheme = permissionSchemeManager.copyScheme(permissionSchemeManager.getSchemeObject(permissionScheme.getId()));
        copyScheme.setName(String.format("%s Permission Scheme", project.getKey()));
        permissionSchemeManager.updateScheme(copyScheme);
        permissionSchemeManager.removeSchemesFromProject(project);
        permissionSchemeManager.addSchemeToProject(project, copyScheme);
    }

    private void associateNotificationSchemeWithProject(Project project, NotificationScheme notificationScheme) {
        Scheme copyScheme = notificationSchemeManager.copyScheme(notificationSchemeManager.getSchemeObject(notificationScheme.getId()));
        copyScheme.setName(String.format("%s Notification Scheme", project.getKey()));
        notificationSchemeManager.updateScheme(copyScheme);
        notificationSchemeManager.removeSchemesFromProject(project);
        notificationSchemeManager.addSchemeToProject(project, copyScheme);
    }

    private void associateSchemeWithProject(Project project, Long schemeId, String schemeType, SchemeManager schemeManager) {
        Scheme copyScheme = schemeManager.copyScheme(schemeManager.getSchemeObject(schemeId));
        copyScheme.setName(String.format("%s %s Scheme", project.getKey(), schemeType));
        schemeManager.updateScheme(copyScheme);
        schemeManager.removeSchemesFromProject(project);
        schemeManager.addSchemeToProject(project, copyScheme);
    }

    private void associateUsersWithProjectRoles(Project project, Map<ProjectRole, Collection<ApplicationUser>> projectRoles) {
        for (Map.Entry<ProjectRole, Collection<ApplicationUser>> entry : projectRoles.entrySet()) {
            ProjectRole projectRole = entry.getKey();
            Collection<ApplicationUser> users = entry.getValue();
            List<String> userKeys = new ArrayList<>();
            for (ApplicationUser user : users)
                userKeys.add(user.getKey());
            projectRoleService.addActorsToProjectRole(userKeys, projectRole, project, ProjectRoleActor.USER_ROLE_ACTOR_TYPE, new SimpleErrorCollection());
        }
    }

    public Project createProject(ProjectConfiguration projectConfiguration) throws Exception {
        ProjectCreationData projectCreationData = new ProjectCreationData.Builder()
                .withName(projectConfiguration.getProjectName())
                .withKey(projectConfiguration.getProjectKey())
                .withLead(userManager.getUserByKey(ADMIN_USER_KEY)) // todo: required
                .withType(ProjectTypeKeys.BUSINESS) // todo: required
                .build();
        ProjectService.CreateProjectValidationResult createProjectValidationResult = projectService.validateCreateProject(userManager.getUserByKey("admin"), projectCreationData);
        if (createProjectValidationResult.isValid()) {
            Project project = projectService.createProject(createProjectValidationResult);
            associateIssueTypeSchemeWithProject(project, projectConfiguration.getIssueTypes().keySet());
            associateIssueTypeScreenSchemeWithProject(project, projectConfiguration.getScreenSchemes());
            associateWorkflowSchemeWithProject(project, projectConfiguration.getIssueTypes());
            associateSchemeWithProject(project, projectConfiguration.getPermissionScheme().getId(), "Permission", permissionSchemeManager);
            associateSchemeWithProject(project, projectConfiguration.getNotificationScheme().getId(), "Notification", notificationSchemeManager);
            associateUsersWithProjectRoles(project, projectConfiguration.getRoles());
            return project;
        } else {
            StringBuilder errors = new StringBuilder();
            for (Map.Entry<String, String> error: createProjectValidationResult.getErrorCollection().getErrors().entrySet())
                errors.append(String.format("Key: %s. Message: %s.\n", error.getKey(), error.getValue()));
            for (String errorMessage : createProjectValidationResult.getErrorCollection().getErrorMessages())
                errors.append(String.format("Error Message: %s.\n", errorMessage));
            throw new Exception(errors.toString());
        }
    }
}
