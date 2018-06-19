(function ($) {
    AJS.toInit(function () {
        $(document).on('click', '.project-configurator-custom-field-create-project', function () {
            AJS.dim();
            JIRA.Loading.showLoadingIndicator();
            $.ajax({
                type: 'GET',
                url: AJS.contextPath() + '/rest/projectconfigurator/latest/configuration/createProject/' + $(this).attr('data-issue-key'),
                context: this,
                success: function (result) {
                    AJS.undim();
                    JIRA.Loading.hideLoadingIndicator();
                    $(this).parent().remove();
                    AJS.flag({
                        type: 'success',
                        title: 'Project has been created.',
                        body: 'http://localhost:2990/jira/browse/' + result.projectKey
                    });
                },
                error: function (request) {
                    AJS.undim();
                    JIRA.Loading.hideLoadingIndicator();
                    AJS.flag({
                        type: 'error',
                        title: 'Project has not been created.',
                        body: request.responseText
                    });
                }
            });
        });
    });
})(AJS.$);