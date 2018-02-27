package ru.mail.jira.plugins.projectconfigurator.function;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;
import ru.mail.jira.plugins.projectconfigurator.configuration.ProjectConfiguratorManager;
import ru.mail.jira.plugins.projectconfigurator.customfield.ProjectConfiguration;

import java.util.Map;

public class CreateProjectFromConfiguratorFunction extends AbstractJiraFunctionProvider {
    private final CommentManager commentManager;
    private final ProjectConfiguratorManager projectConfiguratorManager;

    public CreateProjectFromConfiguratorFunction(CommentManager commentManager, ProjectConfiguratorManager projectConfiguratorManager) {
        this.commentManager = commentManager;
        this.projectConfiguratorManager = projectConfiguratorManager;
    }

    @Override
    public void execute(Map transientVars, Map args, PropertySet propertySet) throws WorkflowException {
        try {
            CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
            Issue issue = getIssue(transientVars);
            CustomField customField = customFieldManager.getCustomFieldObject(10000L);
            ProjectConfiguration projectConfiguration = (ProjectConfiguration) issue.getCustomFieldValue(customField);
            if (projectConfiguration == null)
                throw new Exception(String.format("Field %s value can't be NULL", customField.getName()));
            Project project = projectConfiguratorManager.createProject(projectConfiguration);
            commentManager.create(issue, getCallerUser(transientVars, args), String.format("Project \"%s\" was successfully created!", project.getName()), true);

        } catch (Exception e) {
            throw new WorkflowException(e);
        }
    }
}
