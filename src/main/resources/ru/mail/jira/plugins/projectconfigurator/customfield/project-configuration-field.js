(function ($) {
    AJS.toInit(function () {
        $(document).on('click', '.project-configurator-custom-field-create-project', function () {
            AJS.dim();
            JIRA.Loading.showLoadingIndicator();
            $.ajax({
                type: 'GET',
                url: AJS.contextPath() + '/rest/projectconfigurator/latest/configuration/createProject/' + $(this).attr('data-issue-key'),
                context: this,
                success: function (project) {
                    AJS.undim();
                    JIRA.Loading.hideLoadingIndicator();
                    $(this).parent().remove();
                    AJS.flag({
                        type: 'success',
                        title: 'Project has been created!',
                        body: AJS.format('<a href="{0}/browse/{1}">{2}</a>', AJS.params.baseURL, project.key, project.name )
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