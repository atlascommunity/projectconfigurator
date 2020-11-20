/* (C)2020 */
package ru.mail.jira.plugins.projectconfigurator.configurator;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import ru.mail.jira.plugins.projectconfigurator.configuration.ProjectConfiguratorManager;

public class ProjectConfigurator extends JiraWebActionSupport {

  private static final String SECURITY_BREACH = "securitybreach";

  private final ProjectConfiguratorManager projectConfiguratorManager;

  public ProjectConfigurator(ProjectConfiguratorManager projectConfiguratorManager) {
    this.projectConfiguratorManager = projectConfiguratorManager;
  }

  @Override
  public String doExecute() {
    if (getLoggedInUser() == null) {
      return SECURITY_BREACH;
    }
    if (!projectConfiguratorManager.hasPluginConfiguration()) {
      return ERROR;
    }
    return SUCCESS;
  }
}
