package ru.mail.jira.plugins.projectconfigurator.configurator;

import com.atlassian.jira.plugin.webfragment.conditions.AbstractWebCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;
import ru.mail.jira.plugins.projectconfigurator.configuration.ProjectConfiguratorManager;

public class ProjectConfiguratorCondition extends AbstractWebCondition {
    private final ProjectConfiguratorManager projectConfiguratorManager;

    public ProjectConfiguratorCondition(ProjectConfiguratorManager projectConfiguratorManager) {
        this.projectConfiguratorManager = projectConfiguratorManager;
    }

    @Override
    public boolean shouldDisplay(ApplicationUser applicationUser, JiraHelper jiraHelper) {
        return projectConfiguratorManager.hasPluginConfiguration();
    }
}
