(function ($) {
    AJS.toInit(function () {

        $(document).on('click', '.project-configurator-custom-field-create-project', function () {
            AJS.dialog2("#confirmation-dialog").show();
        });

        $(document).on('click', '#confirmation-dialog-cancel-button', function() {
            AJS.dialog2("#confirmation-dialog").hide();
        });

        $(document).on('click', '#confirmation-dialog-submit-button', function() {
            AJS.dialog2("#confirmation-dialog").hide();
            AJS.dim();
            JIRA.Loading.showLoadingIndicator();
            $.ajax({
                type: 'GET',
                url: AJS.contextPath() + '/rest/projectconfigurator/latest/configuration/createProject/'
                                       + $(".project-configurator-custom-field-operations").children()[0].getAttribute("data-issue-key"),
                success: function (project) {
                    AJS.undim();
                    JIRA.Loading.hideLoadingIndicator();
                    $(".project-configurator-custom-field-operations").remove();
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



