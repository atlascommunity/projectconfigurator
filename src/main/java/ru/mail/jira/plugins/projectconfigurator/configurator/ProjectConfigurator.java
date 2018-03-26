package ru.mail.jira.plugins.projectconfigurator.configurator;

import com.atlassian.jira.web.action.JiraWebActionSupport;

public class ProjectConfigurator extends JiraWebActionSupport {
    private static final String SECURITY_BREACH = "securitybreach";

    @Override
    public String doExecute() throws Exception {
        if (getLoggedInUser() == null)
            return SECURITY_BREACH;
        return SUCCESS;
    }
}
