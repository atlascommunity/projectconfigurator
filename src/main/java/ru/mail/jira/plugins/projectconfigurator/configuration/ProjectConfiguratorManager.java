/* (C)2020 */
package ru.mail.jira.plugins.projectconfigurator.configuration;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.jira.JiraException;
import com.atlassian.jira.avatar.Avatar;
import com.atlassian.jira.avatar.AvatarService;
import com.atlassian.jira.bc.JiraServiceContext;
import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.bc.ServiceOutcome;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.fields.screen.FieldScreenService;
import com.atlassian.jira.bc.project.ProjectCreationData;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.bc.projectroles.ProjectRoleService;
import com.atlassian.jira.bc.user.search.UserSearchParams;
import com.atlassian.jira.bc.user.search.UserSearchService;
import com.atlassian.jira.bc.workflow.WorkflowSchemeService;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFieldConstants;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.context.JiraContextNode;
import com.atlassian.jira.issue.customfields.CustomFieldUtils;
import com.atlassian.jira.issue.fields.CustomField;
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
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.permission.PermissionScheme;
import com.atlassian.jira.permission.PermissionSchemeManager;
import com.atlassian.jira.permission.PermissionSchemeService;
import com.atlassian.jira.project.AssigneeTypes;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.type.ProjectType;
import com.atlassian.jira.project.type.ProjectTypeKey;
import com.atlassian.jira.project.type.ProjectTypeKeys;
import com.atlassian.jira.project.type.ProjectTypeManager;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.scheme.SchemeManager;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleActor;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.ApplicationUsers;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.jira.workflow.AssignableWorkflowScheme;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.jira.workflow.WorkflowSchemeManager;
import com.atlassian.jira.workflow.migration.AssignableWorkflowSchemeMigrationHelper;
import com.atlassian.jira.workflow.migration.MigrationHelperFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.mail.jira.plugins.projectconfigurator.customfield.ProjectConfiguration;
import ru.mail.jira.plugins.projectconfigurator.customfield.ProjectConfigurationCFType;
import ru.mail.jira.plugins.projectconfigurator.rest.dto.ItemDto;
import ru.mail.jira.plugins.projectconfigurator.rest.dto.ProcessDto;
import ru.mail.jira.plugins.projectconfigurator.rest.dto.ProjectConfigurationDto;
import ru.mail.jira.plugins.projectconfigurator.rest.dto.RoleDto;
import ru.mail.jira.plugins.projectconfigurator.rest.dto.UserDto;

@Service
public class ProjectConfiguratorManager {

  private static final Logger log = LoggerFactory.getLogger(ProjectConfiguratorManager.class);

  private final ApplicationProperties applicationProperties;
  private final AvatarService avatarService;
  private final CommentManager commentManager;
  private final CustomFieldManager customFieldManager;
  private final I18nHelper i18nHelper;
  private final IssueManager issueManager;
  private final IssueService issueService;
  private final IssueTypeManager issueTypeManager;
  private final IssueTypeSchemeManager issueTypeSchemeManager;
  private final IssueTypeScreenSchemeManager issueTypeScreenSchemeManager;
  private final FieldConfigSchemeManager fieldConfigSchemeManager;
  private final FieldManager fieldManager;
  private final FieldScreenFactory fieldScreenFactory;
  private final FieldScreenManager fieldScreenManager;
  private final FieldScreenService fieldScreenService;
  private final FieldScreenSchemeManager fieldScreenSchemeManager;
  private final GlobalPermissionManager globalPermissionManager;
  private final GroupManager groupManager;
  private final JiraAuthenticationContext jiraAuthenticationContext;
  private final MigrationHelperFactory migrationHelperFactory;
  private final NotificationSchemeManager notificationSchemeManager;
  private final NotificationSchemeService notificationSchemeService;
  private final PermissionSchemeManager permissionSchemeManager;
  private final PermissionSchemeService permissionSchemeService;
  private final PluginData pluginData;
  private final ProjectManager projectManager;
  private final ProjectService projectService;
  private final ProjectRoleManager projectRoleManager;
  private final ProjectRoleService projectRoleService;
  private final ProjectTypeManager projectTypeManager;
  private final WorkflowManager workflowManager;
  private final WorkflowSchemeManager workflowSchemeManager;
  private final WorkflowSchemeService workflowSchemeService;
  private final UserManager userManager;
  private final UserSearchService userSearchService;

  public ProjectConfiguratorManager(
      @ComponentImport ApplicationProperties applicationProperties,
      @ComponentImport AvatarService avatarService,
      @ComponentImport CommentManager commentManager,
      @ComponentImport CustomFieldManager customFieldManager,
      @ComponentImport I18nHelper i18nHelper,
      @ComponentImport IssueManager issueManager,
      @ComponentImport IssueService issueService,
      @ComponentImport IssueTypeManager issueTypeManager,
      @ComponentImport IssueTypeSchemeManager issueTypeSchemeManager,
      @ComponentImport IssueTypeScreenSchemeManager issueTypeScreenSchemeManager,
      @ComponentImport FieldConfigSchemeManager fieldConfigSchemeManager,
      @ComponentImport FieldManager fieldManager,
      @ComponentImport FieldScreenFactory fieldScreenFactory,
      @ComponentImport FieldScreenManager fieldScreenManager,
      @ComponentImport FieldScreenService fieldScreenService,
      @ComponentImport GlobalPermissionManager globalPermissionManager,
      @ComponentImport GroupManager groupManager,
      @ComponentImport JiraAuthenticationContext jiraAuthenticationContext,
      @ComponentImport MigrationHelperFactory migrationHelperFactory,
      @ComponentImport NotificationSchemeService notificationSchemeService,
      @ComponentImport PermissionSchemeService permissionSchemeService,
      @ComponentImport FieldScreenSchemeManager fieldScreenSchemeManager,
      @ComponentImport NotificationSchemeManager notificationSchemeManager,
      @ComponentImport PermissionSchemeManager permissionSchemeManager,
      PluginData pluginData,
      @ComponentImport ProjectManager projectManager,
      @ComponentImport ProjectService projectService,
      @ComponentImport ProjectRoleManager projectRoleManager,
      @ComponentImport ProjectRoleService projectRoleService,
      @ComponentImport ProjectTypeManager projectTypeManager,
      @ComponentImport WorkflowManager workflowManager,
      @ComponentImport WorkflowSchemeManager workflowSchemeManager,
      @ComponentImport WorkflowSchemeService workflowSchemeService,
      @ComponentImport UserManager userManager,
      @ComponentImport UserSearchService userSearchService) {
    this.applicationProperties = applicationProperties;
    this.avatarService = avatarService;
    this.commentManager = commentManager;
    this.customFieldManager = customFieldManager;
    this.i18nHelper = i18nHelper;
    this.issueManager = issueManager;
    this.issueService = issueService;
    this.issueTypeManager = issueTypeManager;
    this.issueTypeSchemeManager = issueTypeSchemeManager;
    this.issueTypeScreenSchemeManager = issueTypeScreenSchemeManager;
    this.fieldConfigSchemeManager = fieldConfigSchemeManager;
    this.fieldManager = fieldManager;
    this.fieldScreenFactory = fieldScreenFactory;
    this.fieldScreenManager = fieldScreenManager;
    this.fieldScreenService = fieldScreenService;
    this.globalPermissionManager = globalPermissionManager;
    this.groupManager = groupManager;
    this.jiraAuthenticationContext = jiraAuthenticationContext;
    this.notificationSchemeService = notificationSchemeService;
    this.fieldScreenSchemeManager = fieldScreenSchemeManager;
    this.migrationHelperFactory = migrationHelperFactory;
    this.permissionSchemeService = permissionSchemeService;
    this.notificationSchemeManager = notificationSchemeManager;
    this.permissionSchemeManager = permissionSchemeManager;
    this.pluginData = pluginData;
    this.projectManager = projectManager;
    this.projectService = projectService;
    this.projectRoleManager = projectRoleManager;
    this.projectRoleService = projectRoleService;
    this.projectTypeManager = projectTypeManager;
    this.workflowManager = workflowManager;
    this.workflowSchemeManager = workflowSchemeManager;
    this.workflowSchemeService = workflowSchemeService;
    this.userManager = userManager;
    this.userSearchService = userSearchService;
  }

  private static JSONObject formatErrorCollection(ErrorCollection errorCollection) {
    JSONObject result = new JSONObject();
    try {
      JSONObject errorsObject = new JSONObject();
      Collection<String> errorMessages = errorCollection.getErrorMessages();
      if (errorMessages != null && errorMessages.size() > 0) {
        result.put("errorMessages", errorMessages);
      }

      if (errorCollection.getErrors() != null && errorCollection.getErrors().size() > 0) {
        for (Map.Entry<String, String> entry : errorCollection.getErrors().entrySet()) {
          errorsObject.put(entry.getKey(), entry.getValue());
        }
      }
      result.put("errors", errorsObject);
    } catch (Exception ignore) {
      // ignore exception
    }

    return result;
  }

  public boolean hasPluginConfiguration() {
    List<String> workflowNames = pluginData.getWorkflowNames();
    List<String> issueTypeIds = pluginData.getIssueTypeIds();
    List<String> screenSchemeIds = pluginData.getScreenSchemeIds();
    List<String> permissionSchemeIds = pluginData.getPermissionSchemeIds();
    List<String> notificationSchemeIds = pluginData.getNotificationSchemeIds();
    String adminUserKey = pluginData.getAdminUserKey();

    String projectId = pluginData.getProjectId();
    String issueTypeId = pluginData.getIssueTypeId();
    String projectConfigurationCfId = pluginData.getProjectConfigurationCfId();
    return CollectionUtils.isNotEmpty(workflowNames)
        && CollectionUtils.isNotEmpty(issueTypeIds)
        && CollectionUtils.isNotEmpty(screenSchemeIds)
        && CollectionUtils.isNotEmpty(permissionSchemeIds)
        && CollectionUtils.isNotEmpty(notificationSchemeIds)
        && StringUtils.isNoneBlank(adminUserKey)
        && StringUtils.isNoneBlank(projectId)
        && StringUtils.isNoneBlank(issueTypeId)
        && StringUtils.isNoneBlank(projectConfigurationCfId);
  }

  public JiraWorkflow getWorkflow(String name) {
    return workflowManager.getWorkflow(name);
  }

  public List<JiraWorkflow> getWorkflows(List<String> workflowNames) {
    List<JiraWorkflow> result = new ArrayList<>();
    if (workflowNames != null && workflowNames.size() > 0) {
      for (String workflowName : workflowNames) {
        result.add(workflowManager.getWorkflow(workflowName));
      }
    }
    return result;
  }

  public IssueType getIssueType(String id) {
    return issueTypeManager.getIssueType(id);
  }

  public List<IssueType> getIssueTypes(List<String> issueTypeIds) {
    List<IssueType> result = new ArrayList<>();
    if (issueTypeIds != null && issueTypeIds.size() > 0) {
      for (String issueTypeId : issueTypeIds) {
        result.add(issueTypeManager.getIssueType(issueTypeId));
      }
    }
    return result;
  }

  public FieldScreenScheme getFieldScreenScheme(Long id) {
    return fieldScreenSchemeManager.getFieldScreenScheme(id);
  }

  public List<FieldScreenScheme> getScreenSchemes(List<String> screenSchemeIds) {
    List<FieldScreenScheme> result = new ArrayList<>();
    if (screenSchemeIds != null && screenSchemeIds.size() > 0) {
      for (String screenSchemeId : screenSchemeIds) {
        result.add(fieldScreenSchemeManager.getFieldScreenScheme(Long.parseLong(screenSchemeId)));
      }
    }
    return result;
  }

  public PermissionScheme getPermissionScheme(Long id) {
    return permissionSchemeService
        .getPermissionScheme(userManager.getUserByKey(pluginData.getAdminUserKey()), id)
        .get();
  }

  public List<PermissionScheme> getPermissionSchemes(List<String> permissionSchemeIds) {
    List<PermissionScheme> result = new ArrayList<>();
    if (permissionSchemeIds != null && permissionSchemeIds.size() > 0) {
      for (String permissionSchemeId : permissionSchemeIds) {
        result.add(
            permissionSchemeService
                .getPermissionScheme(
                    userManager.getUserByKey(pluginData.getAdminUserKey()),
                    Long.parseLong(permissionSchemeId))
                .get());
      }
    }
    return result;
  }

  public NotificationScheme getNotificationScheme(Long id) {
    return notificationSchemeService
        .getNotificationScheme(userManager.getUserByKey(pluginData.getAdminUserKey()), id)
        .get();
  }

  public List<NotificationScheme> getNotificationSchemes(List<String> notificationSchemeIds) {
    List<NotificationScheme> result = new ArrayList<>();
    if (notificationSchemeIds != null && notificationSchemeIds.size() > 0) {
      for (String notificationSchemeId : notificationSchemeIds) {
        result.add(
            notificationSchemeService
                .getNotificationScheme(
                    userManager.getUserByKey(pluginData.getAdminUserKey()),
                    Long.parseLong(notificationSchemeId))
                .get());
      }
    }
    return result;
  }

  public ApplicationUser getUser(String userKey) {
    return userManager.getUserByKey(userKey);
  }

  public Group getGroup(String name) {
    return groupManager.getGroup(name);
  }

  public Project getProject(String id) {
    return projectManager.getProjectObj(Long.parseLong(id));
  }

  public CustomField getCustomField(String id) {
    return customFieldManager.getCustomFieldObject(id);
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
    return permissionSchemeService
        .getPermissionSchemes(jiraAuthenticationContext.getLoggedInUser())
        .get();
  }

  public Collection<Scheme> getAllNotificationSchemes() {
    return notificationSchemeManager.getSchemeObjects();
  }

  public Collection<ApplicationUser> getAllAdminUsers() {
    return userSearchService.findUsers(
        "",
        new UserSearchParams(
            true,
            true,
            false,
            true,
            null,
            null,
            user ->
                globalPermissionManager.hasPermission(
                    GlobalPermissionKey.ADMINISTER, ApplicationUsers.from(user)),
            null));
  }

  public Collection<ProjectRole> getAllProjectRoles() {
    return projectRoleManager.getProjectRoles();
  }

  public Collection<Project> getAllProjects() {
    return projectManager.getProjects();
  }

  public Collection<CustomField> getAllProjectConfiguratorCustomFields() {
    List<CustomField> result = new ArrayList<>();
    for (CustomField customField : customFieldManager.getCustomFieldObjects()) {
      if (customField.getCustomFieldType() instanceof ProjectConfigurationCFType) {
        result.add(customField);
      }
    }
    return result;
  }

  private String formatErrorCollections(ErrorCollection errorCollection) {
    StringBuilder errors = new StringBuilder();
    for (Map.Entry<String, String> error : errorCollection.getErrors().entrySet()) {
      errors.append(String.format("Key: %s. Message: %s.\n", error.getKey(), error.getValue()));
    }
    for (String errorMessage : errorCollection.getErrorMessages()) {
      errors.append(String.format("Error Message: %s.\n", errorMessage));
    }
    return errors.toString();
  }

  private FieldConfigScheme createFieldConfigScheme(String projectKey, List<IssueType> issueTypes) {
    List<String> issueTypeIds = new ArrayList<>();
    for (IssueType issueType : issueTypes) {
      issueTypeIds.add(issueType.getId());
    }
    return issueTypeSchemeManager.create(
        String.format("%s Issue Types Scheme", projectKey), null, issueTypeIds);
  }

  private void associateIssueTypeSchemeWithProject(
      Project project, FieldConfigScheme issueTypeScheme) {
    List<JiraContextNode> contexts =
        CustomFieldUtils.buildJiraIssueContexts(
            false, new Long[] {project.getId()}, projectManager);
    fieldConfigSchemeManager.updateFieldConfigScheme(
        issueTypeScheme,
        contexts,
        fieldManager.getConfigurableField(IssueFieldConstants.ISSUE_TYPE));
    fieldManager.refresh();
  }

  private FieldScreenScheme copyFieldScreenScheme(
      String projectKey, String issueTypeName, FieldScreenScheme fieldScreenScheme)
      throws Exception {
    FieldScreenScheme copyFieldScreenScheme = fieldScreenFactory.createFieldScreenScheme();
    copyFieldScreenScheme.setName(String.format("%s %s Screen Scheme", projectKey, issueTypeName));
    copyFieldScreenScheme.setDescription(null);
    copyFieldScreenScheme.store();

    // Copy all the scheme entities
    for (FieldScreenSchemeItem fieldScreenSchemeItem :
        fieldScreenScheme.getFieldScreenSchemeItems()) {
      ServiceOutcome<FieldScreen> copyScreenResult =
          fieldScreenService.copy(
              fieldScreenSchemeItem.getFieldScreen(),
              String.format(
                  "%s %s %s Screen",
                  projectKey,
                  issueTypeName,
                  i18nHelper.getText(fieldScreenSchemeItem.getIssueOperationName())),
              fieldScreenSchemeItem.getFieldScreen().getDescription(),
              userManager.getUserByKey(pluginData.getAdminUserKey()));
      if (!copyScreenResult.isValid()) {
        throw new Exception(formatErrorCollections(copyScreenResult.getErrorCollection()));
      }

      FieldScreenSchemeItem copyFieldScreenSchemeItem =
          new FieldScreenSchemeItemImpl(
              fieldScreenSchemeManager, fieldScreenSchemeItem, fieldScreenManager);
      copyFieldScreenSchemeItem.setFieldScreen(copyScreenResult.getReturnedValue());
      copyFieldScreenScheme.addFieldScreenSchemeItem(copyFieldScreenSchemeItem);
    }
    return copyFieldScreenScheme;
  }

  private IssueTypeScreenSchemeEntity createIssueTypeScreenSchemeEntity(
      String issueTypeId, FieldScreenScheme fieldScreenScheme) {
    IssueTypeScreenSchemeEntity issueTypeScreenSchemeEntity =
        fieldScreenFactory.createIssueTypeScreenSchemeEntity();
    issueTypeScreenSchemeEntity.setIssueTypeId(issueTypeId);
    issueTypeScreenSchemeEntity.setFieldScreenScheme(fieldScreenScheme);
    return issueTypeScreenSchemeEntity;
  }

  private IssueTypeScreenScheme createIssueTypeScreenScheme(
      String projectKey, List<ProjectConfiguration.JiraProcess> processes) throws Exception {
    final IssueTypeScreenScheme issueTypeScreenScheme =
        fieldScreenFactory.createIssueTypeScreenScheme();
    issueTypeScreenScheme.setName(String.format("%s Issue Types Screen Scheme", projectKey));
    issueTypeScreenScheme.setDescription(null);
    issueTypeScreenScheme.store();

    // Add default screen scheme
    issueTypeScreenScheme.addEntity(
        createIssueTypeScreenSchemeEntity(
            null,
            fieldScreenSchemeManager.getFieldScreenScheme(
                FieldScreenSchemeManager.DEFAULT_FIELD_SCREEN_SCHEME_ID)));
    for (ProjectConfiguration.JiraProcess process : processes) {
      IssueType issueType = process.getIssueType();
      FieldScreenScheme copiedScreenScheme =
          copyFieldScreenScheme(projectKey, issueType.getName(), process.getFieldScreenScheme());
      issueTypeScreenScheme.addEntity(
          createIssueTypeScreenSchemeEntity(issueType.getId(), copiedScreenScheme));
    }
    return issueTypeScreenScheme;
  }

  private void associateIssueTypeScreenSchemeWithProject(
      Project project, IssueTypeScreenScheme issueTypeScreenScheme) {
    issueTypeScreenSchemeManager.addSchemeAssociation(project, issueTypeScreenScheme);
  }

  private AssignableWorkflowScheme createWorkflowScheme(
      String projectKey, List<ProjectConfiguration.JiraProcess> processes) throws Exception {
    AssignableWorkflowScheme.Builder assignableWorkflowSchemeBuilder =
        workflowSchemeManager.assignableBuilder();
    assignableWorkflowSchemeBuilder.setName(String.format("%s Workflow Scheme", projectKey));
    for (ProjectConfiguration.JiraProcess process : processes) {
      IssueType issueType = process.getIssueType();
      JiraWorkflow workflow = process.getJiraWorkflow();
      JiraWorkflow copyWorkflow =
          workflowManager.copyWorkflow(
              userManager.getUserByKey(pluginData.getAdminUserKey()),
              String.format("%s %s Workflow", projectKey, issueType.getName()),
              null,
              workflow);
      assignableWorkflowSchemeBuilder.setMapping(issueType.getId(), copyWorkflow.getName());
    }
    ServiceOutcome<AssignableWorkflowScheme> createWorkflowSchemeResult =
        workflowSchemeService.createScheme(
            userManager.getUserByKey(pluginData.getAdminUserKey()),
            assignableWorkflowSchemeBuilder.build());
    if (!createWorkflowSchemeResult.isValid()) {
      throw new Exception(formatErrorCollections(createWorkflowSchemeResult.getErrorCollection()));
    }
    return createWorkflowSchemeResult.getReturnedValue();
  }

  private void associateWorkflowSchemeWithProject(
      Project project, AssignableWorkflowScheme workflowScheme) throws Exception {
    if (workflowScheme != null) {
      AssignableWorkflowSchemeMigrationHelper migrationHelper =
          migrationHelperFactory.createMigrationHelper(project, workflowScheme);
      migrationHelper.associateProjectAndWorkflowScheme();
    }
  }

  private Scheme copyScheme(
      String projectKey, Long schemeId, String schemeType, SchemeManager schemeManager) {
    Scheme copyScheme = schemeManager.copyScheme(schemeManager.getSchemeObject(schemeId));
    copyScheme.setName(String.format("%s %s Scheme", projectKey, schemeType));
    schemeManager.updateScheme(copyScheme);
    return copyScheme;
  }

  private void associateSchemesWithProject(
      Project project, Scheme permissionScheme, Scheme notificationScheme) throws Exception {
    ProjectService.UpdateProjectSchemesValidationResult updateProjectSchemesValidationResult =
        projectService.validateUpdateProjectSchemes(
            userManager.getUserByKey(pluginData.getAdminUserKey()),
            permissionScheme != null ? permissionScheme.getId() : -1L,
            notificationScheme != null ? notificationScheme.getId() : -1,
            -1L);
    if (updateProjectSchemesValidationResult.isValid()) {
      projectService.updateProjectSchemes(updateProjectSchemesValidationResult, project);
    } else {
      log.error(formatErrorCollections(updateProjectSchemesValidationResult.getErrorCollection()));
    }
  }

  private void associateUsersWithProjectRoles(
      Project project, List<ProjectConfiguration.Role> roles) {
    for (ProjectConfiguration.Role role : roles) {
      ProjectRole projectRole = role.getProjectRole();
      List<String> userKeys = new ArrayList<>();
      for (ApplicationUser user : role.getUsers()) {
        userKeys.add(user.getKey());
      }
      List<String> groupNames = new ArrayList<>();
      for (Group group : role.getGroups()) {
        groupNames.add(group.getName());
      }
      projectRoleService.addActorsToProjectRole(
          userKeys,
          projectRole,
          project,
          ProjectRoleActor.USER_ROLE_ACTOR_TYPE,
          new SimpleErrorCollection());
      projectRoleService.addActorsToProjectRole(
          groupNames,
          projectRole,
          project,
          ProjectRoleActor.GROUP_ROLE_ACTOR_TYPE,
          new SimpleErrorCollection());
    }
  }

  private String getAvatar(ApplicationUser user, Avatar.Size size) {
    return avatarService.getAvatarURL(user, user, size).toString();
  }

  private boolean isAdministrator(ApplicationUser user) {
    return globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, user);
  }

  private void validateProjectConfiguration(ProjectConfigurationDto projectConfigurationDto) {
    ErrorCollection errors = new SimpleErrorCollection();

    String projectName = StringUtils.trimToNull(projectConfigurationDto.getProjectName());
    if (StringUtils.isBlank(projectName)) {
      errors.addError(
          "projectName",
          i18nHelper.getText(
              "issue.field.required",
              i18nHelper.getText("ru.mail.jira.plugins.projectconfigurator.page.project.name")));
    } else if (StringUtils.trimToNull(projectName).length() > 150) {
      errors.addError(
          "projectName",
          i18nHelper.getText(
              "admin.generalconfiguration.maximum.length.project.names.is.too.large"));
    } else if (projectManager.getProjectObjByName(projectName) != null) {
      errors.addError(
          "projectName", i18nHelper.getText("admin.errors.project.with.that.name.already.exists"));
    }

    String projectKey = StringUtils.trimToNull(projectConfigurationDto.getProjectKey());
    JiraServiceContext context =
        new JiraServiceContextImpl(
            userManager.getUserByKey(pluginData.getAdminUserKey()), new SimpleErrorCollection());
    if (StringUtils.isBlank(projectKey)) {
      errors.addError(
          "projectKey",
          i18nHelper.getText(
              "issue.field.required",
              i18nHelper.getText("ru.mail.jira.plugins.projectconfigurator.page.project.key")));
    } else if (!projectService.isValidProjectKey(context, projectKey)) {
      errors.addErrorCollection(context.getErrorCollection());
    }

    String projectLeadKey = StringUtils.trimToNull(projectConfigurationDto.getProjectLeadKey());
    if (StringUtils.isBlank(projectLeadKey)) {
      errors.addError(
          "projectLead",
          i18nHelper.getText(
              "issue.field.required",
              i18nHelper.getText("ru.mail.jira.plugins.projectconfigurator.page.project.lead")));
    } else if (userManager.getUserByKey(projectLeadKey) == null) {
      errors.addError(
          "projectLead", i18nHelper.getText("user.hover.user.does.not.exist", projectLeadKey));
    }

    String projectType = StringUtils.trimToNull(projectConfigurationDto.getProjectType());
    if (StringUtils.isBlank(projectType)) {
      errors.addError(
          "projectType",
          i18nHelper.getText(
              "issue.field.required",
              i18nHelper.getText("ru.mail.jira.plugins.projectconfigurator.page.project.type")));
    }

    Collection<ProcessDto> processes = projectConfigurationDto.getProcesses();
    if (processes.size() == 0) {
      errors.addError(
          "issueTypes",
          i18nHelper.getText(
              "issue.field.required",
              i18nHelper.getText("ru.mail.jira.plugins.projectconfigurator.page.issueTypes")));
      errors.addError(
          "workflows",
          i18nHelper.getText(
              "issue.field.required",
              i18nHelper.getText("ru.mail.jira.plugins.projectconfigurator.page.workflows")));
      errors.addError(
          "screenSchemes",
          i18nHelper.getText(
              "issue.field.required",
              i18nHelper.getText("ru.mail.jira.plugins.projectconfigurator.page.screenSchemes")));
    } else {
      int issueTypeCount = processes.size();
      int workflowCount =
          processes.stream()
              .filter(processDto -> processDto.getWorkflowName() != null)
              .toArray()
              .length;
      int screenSchemeCount =
          processes.stream()
              .filter(processDto -> processDto.getScreenSchemeId() != null)
              .toArray()
              .length;
      if (workflowCount != issueTypeCount) {
        errors.addError(
            "workflows",
            i18nHelper.getText(
                "issue.field.required",
                i18nHelper.getText("ru.mail.jira.plugins.projectconfigurator.page.workflows")));
      }
      if (screenSchemeCount != issueTypeCount) {
        errors.addError(
            "screenSchemes",
            i18nHelper.getText(
                "issue.field.required",
                i18nHelper.getText("ru.mail.jira.plugins.projectconfigurator.page.screenSchemes")));
      }
    }

    Long permissionSchemeId = projectConfigurationDto.getPermissionSchemeId();
    if (permissionSchemeId == null) {
      errors.addError(
          "permissionScheme",
          i18nHelper.getText(
              "issue.field.required",
              i18nHelper.getText(
                  "ru.mail.jira.plugins.projectconfigurator.page.permissionScheme")));
    }

    Long notificationSchemeId = projectConfigurationDto.getNotificationSchemeId();
    if (notificationSchemeId == null) {
      errors.addError(
          "notificationScheme",
          i18nHelper.getText(
              "issue.field.required",
              i18nHelper.getText(
                  "ru.mail.jira.plugins.projectconfigurator.page.notificationScheme")));
    }

    Collection<RoleDto> roles = projectConfigurationDto.getRoles();
    if (roles.size() == 0) {
      errors.addError(
          "roles",
          i18nHelper.getText(
              "issue.field.required",
              i18nHelper.getText("ru.mail.jira.plugins.projectconfigurator.page.roles")));
    }

    if (errors.hasAnyErrors()) {
      throw new IllegalArgumentException(formatErrorCollection(errors).toString());
    }
  }

  public String createProjectConfigurationTask(ProjectConfigurationDto projectConfigurationDto)
      throws JiraException {
    validateProjectConfiguration(projectConfigurationDto);

    ApplicationUser currentUser = jiraAuthenticationContext.getLoggedInUser();
    IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
    issueInputParameters.setProjectId(Long.parseLong(pluginData.getProjectId()));
    issueInputParameters.setIssueTypeId(pluginData.getIssueTypeId());
    issueInputParameters.addCustomFieldValue(
        pluginData.getProjectConfigurationCfId(),
        buildValueFromDto(projectConfigurationDto).toString());
    issueInputParameters.setReporterId(currentUser.getKey());
    issueInputParameters.setSummary(
        i18nHelper.getText(
            "ru.mail.jira.plugins.projectconfigurator.creation.summary",
            projectConfigurationDto.getProjectName()));
    issueInputParameters.setApplyDefaultValuesWhenParameterNotProvided(true);

    IssueService.CreateValidationResult createValidationResult =
        issueService.validateCreate(currentUser, issueInputParameters);
    if (createValidationResult.isValid()) {
      IssueService.IssueResult issueResult =
          issueService.create(currentUser, createValidationResult);
      if (issueResult.isValid()) {
        Issue issue = issueResult.getIssue();
        return issue.getKey();
      } else {
        throw new JiraException(issueResult.getErrorCollection().toString());
      }
    } else {
      throw new JiraException(createValidationResult.getErrorCollection().toString());
    }
  }

  private ProjectType getProjectType(String projectTypeKeyString) {
    ProjectTypeKey projectTypeKey =
        ProjectTypeKeys.JIRA_PROJECT_TYPE_KEYS.stream()
            .filter(type -> type.getKey().equals(projectTypeKeyString))
            .findFirst()
            .orElse(ProjectTypeKeys.BUSINESS);
    return projectTypeManager.getAccessibleProjectType(projectTypeKey).getOrNull();
  }

  private ProjectConfiguration buildValueFromDto(ProjectConfigurationDto projectConfigurationDto) {
    if (projectConfigurationDto == null) {
      return null;
    }

    List<ProjectConfiguration.JiraProcess> processes = new ArrayList<>();
    for (ProcessDto processDto : projectConfigurationDto.getProcesses()) {
      ProjectConfiguration.JiraProcess role = new ProjectConfiguration.JiraProcess();
      role.setIssueType(getIssueType(processDto.getIssueTypeId()));
      role.setJiraWorkflow(getWorkflow(processDto.getWorkflowName()));
      role.setFieldScreenScheme(getFieldScreenScheme(processDto.getScreenSchemeId()));
      processes.add(role);
    }

    List<ProjectConfiguration.Role> roles = new ArrayList<>();
    for (RoleDto roleDto : projectConfigurationDto.getRoles()) {
      List<ApplicationUser> users = new ArrayList<>();
      for (String userKey : roleDto.getUserKeys()) {
        users.add(userManager.getUserByKey(userKey));
      }
      List<Group> groups = new ArrayList<>();
      for (String groupName : roleDto.getGroupNames()) {
        groups.add(getGroup(groupName));
      }

      ProjectConfiguration.Role role = new ProjectConfiguration.Role();
      role.setProjectRole(projectRoleManager.getProjectRole(roleDto.getProjectRoleId()));
      role.setUsers(users);
      role.setGroups(groups);
      roles.add(role);
    }

    ProjectConfiguration value = new ProjectConfiguration();
    value.setProjectName(projectConfigurationDto.getProjectName());
    value.setProjectKey(projectConfigurationDto.getProjectKey());
    value.setProjectLead(userManager.getUserByKey(projectConfigurationDto.getProjectLeadKey()));
    value.setProjectType(getProjectType(projectConfigurationDto.getProjectType()));
    value.setProcesses(processes);
    value.setRoles(roles);
    value.setPermissionScheme(getPermissionScheme(projectConfigurationDto.getPermissionSchemeId()));
    value.setNotificationScheme(
        getNotificationScheme(projectConfigurationDto.getNotificationSchemeId()));
    return value;
  }

  private Project createProject(ProjectConfiguration projectConfiguration) throws Exception {
    List<IssueType> issueTypes =
        projectConfiguration.getProcesses().stream()
            .map(ProjectConfiguration.JiraProcess::getIssueType)
            .collect(Collectors.toList());
    String projectKey = projectConfiguration.getProjectKey();

    FieldConfigScheme issueTypeScheme = createFieldConfigScheme(projectKey, issueTypes);
    IssueTypeScreenScheme issueTypeScreenScheme =
        createIssueTypeScreenScheme(projectKey, projectConfiguration.getProcesses());
    AssignableWorkflowScheme workflowScheme =
        createWorkflowScheme(projectKey, projectConfiguration.getProcesses());
    Scheme permissionScheme =
        copyScheme(
            projectKey,
            projectConfiguration.getPermissionScheme().getId(),
            "Permission",
            permissionSchemeManager);
    Scheme notificationScheme =
        copyScheme(
            projectKey,
            projectConfiguration.getNotificationScheme().getId(),
            "Notification",
            notificationSchemeManager);

    ProjectCreationData projectCreationData =
        new ProjectCreationData.Builder()
            .withName(projectConfiguration.getProjectName())
            .withKey(projectConfiguration.getProjectKey())
            .withLead(projectConfiguration.getProjectLead())
            .withType(
                projectConfiguration.getProjectType() == null
                    ? ProjectTypeKeys.BUSINESS.getKey()
                    : projectConfiguration.getProjectType().getKey().getKey())
            .withAssigneeType(AssigneeTypes.PROJECT_LEAD)
            .build();
    ProjectService.CreateProjectValidationResult createProjectValidationResult =
        projectService.validateCreateProject(
            userManager.getUserByKey(pluginData.getAdminUserKey()), projectCreationData);
    if (createProjectValidationResult.isValid()) {
      Project project = projectService.createProject(createProjectValidationResult);
      projectManager.refresh();
      if (project != null) {
        associateIssueTypeSchemeWithProject(project, issueTypeScheme);
        associateIssueTypeScreenSchemeWithProject(project, issueTypeScreenScheme);
        associateSchemesWithProject(project, permissionScheme, notificationScheme);
        associateUsersWithProjectRoles(project, projectConfiguration.getRoles());
        associateWorkflowSchemeWithProject(project, workflowScheme);
      }
      return project;
    } else {
      throw new Exception(
          formatErrorCollections(createProjectValidationResult.getErrorCollection()));
    }
  }

  public Project createProjectFromIssue(String issueKey) throws Exception {
    Issue issue = issueManager.getIssueObject(issueKey);
    if (issue == null) {
      throw new Exception(
          i18nHelper.getText(
              "ru.mail.jira.plugins.projectconfigurator.creation.error.issue.exist", issueKey));
    }
    CustomField customField =
        customFieldManager.getCustomFieldObject(pluginData.getProjectConfigurationCfId());
    if (customField == null) {
      throw new Exception(
          i18nHelper.getText(
              "ru.mail.jira.plugins.projectconfigurator.creation.error.field.empty",
              pluginData.getProjectConfigurationCfId()));
    }
    ProjectConfiguration projectConfiguration =
        (ProjectConfiguration) issue.getCustomFieldValue(customField);
    if (projectConfiguration == null) {
      throw new Exception(
          i18nHelper.getText(
              "ru.mail.jira.plugins.projectconfigurator.creation.error.field.value.empty",
              customField.getFieldName()));
    }
    if (projectManager.getProjectByCurrentKey(projectConfiguration.getProjectKey()) != null) {
      throw new Exception(
          i18nHelper.getText(
              "ru.mail.jira.plugins.projectconfigurator.creation.error.project.exist",
              projectConfiguration.getProjectName()));
    }

    Project project = createProject(projectConfiguration);
    if (project != null) {
      commentManager.create(
          issue,
          jiraAuthenticationContext.getLoggedInUser(),
          i18nHelper.getText(
              "ru.mail.jira.plugins.projectconfigurator.creation.success",
              project.getName(),
              String.format(
                  "%s/browse/%s",
                  applicationProperties.getBaseUrl(UrlMode.ABSOLUTE), project.getKey())),
          true);
    }
    return project;
  }

  public List<UserDto> findUsers(ApplicationUser user, String filter) {
    if (!globalPermissionManager.hasPermission(GlobalPermissionKey.USER_PICKER, user)) {
      return null;
    }

    filter = filter.trim().toLowerCase();
    return userSearchService
        .findUsers(filter, new UserSearchParams(true, true, false, true, null, null)).stream()
        .limit(10)
        .map(
            (u) -> {
              UserDto userDto = new UserDto();
              userDto.setKey(u.getKey());
              userDto.setName(u.getName());
              userDto.setDisplayName(u.getDisplayName());
              userDto.setAvatarUrl(getAvatar(u, Avatar.Size.LARGE));
              return userDto;
            })
        .collect(Collectors.toList());
  }

  public List<ItemDto> findGroups(ApplicationUser user, String filter) {
    filter = filter.trim().toLowerCase();

    Collection<Group> groups =
        isAdministrator(user)
            ? groupManager.getAllGroups()
            : groupManager.getGroupsForUser(user.getName());
    List<ItemDto> result = new ArrayList<>();

    if (StringUtils.isEmpty(filter)) {
      for (Group group : groups) {
        result.add(new ItemDto(group.getName(), group.getName()));
        if (result.size() >= 10) {
          break;
        }
      }
    } else {
      for (Group group : groups) {
        if (StringUtils.containsIgnoreCase(group.getName(), filter)) {
          if (result.size() < 10) {
            result.add(new ItemDto(group.getName(), group.getName()));
          }
        }
      }
    }
    return result;
  }
}
