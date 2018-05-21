package ru.mail.jira.plugins.projectconfigurator.function;

import com.atlassian.crowd.util.I18nHelper;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;
import ru.mail.jira.plugins.projectconfigurator.configuration.PluginData;
import ru.mail.jira.plugins.projectconfigurator.configuration.ProjectConfiguratorManager;
import ru.mail.jira.plugins.projectconfigurator.customfield.ProjectConfiguration;

import java.util.Map;

public class CreateProjectFromConfiguratorFunction extends AbstractJiraFunctionProvider {
    private final CommentManager commentManager;
    private final I18nHelper i18nHelper;
    private final PluginData pluginData;
    private final ProjectConfiguratorManager projectConfiguratorManager;

    public CreateProjectFromConfiguratorFunction(CommentManager commentManager, I18nHelper i18nHelper, PluginData pluginData, ProjectConfiguratorManager projectConfiguratorManager) {
        this.commentManager = commentManager;
        this.i18nHelper = i18nHelper;
        this.pluginData = pluginData;
        this.projectConfiguratorManager = projectConfiguratorManager;
    }

    @Override
    public void execute(Map transientVars, Map args, PropertySet propertySet) throws WorkflowException {
        try {
            CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
            Issue issue = getIssue(transientVars);
            if (pluginData.getProjectConfigurationCfId() == null)
                throw new Exception(i18nHelper.getText("ru.mail.jira.plugins.projectconfigurator.creation.error.field"));
            CustomField customField = customFieldManager.getCustomFieldObject(pluginData.getProjectConfigurationCfId());
            if (customField == null)
                throw new Exception(i18nHelper.getText("ru.mail.jira.plugins.projectconfigurator.creation.error.field.empty", pluginData.getProjectConfigurationCfId()));
            ProjectConfiguration projectConfiguration = (ProjectConfiguration) issue.getCustomFieldValue(customField);
            if (projectConfiguration == null)
                throw new Exception(i18nHelper.getText("ru.mail.jira.plugins.projectconfigurator.creation.error.field.value.empty", customField.getFieldName()));
            Project project = projectConfiguratorManager.createProject(projectConfiguration);
            commentManager.create(issue, getCallerUser(transientVars, args), i18nHelper.getText("ru.mail.jira.plugins.projectconfigurator.creation.success", project.getName()), true);
        } catch (Exception e) {
            throw new WorkflowException(e);
        }
    }
}
