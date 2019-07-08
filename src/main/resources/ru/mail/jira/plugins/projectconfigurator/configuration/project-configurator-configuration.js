require(['jquery'], function($) {
    AJS.toInit(function () {
        $(".project-configurator-workflows").auiSelect2({
            placeholder: "Select a workflows",
            allowClear: true
        });
        $(".project-configurator-issue-types").auiSelect2({
            placeholder: "Select an issue types",
            allowClear: true
        });
        $(".project-configurator-screen-schemes").auiSelect2({
            placeholder: "Select a field screen schemes",
            allowClear: true
        });
        $(".project-configurator-permission-schemes").auiSelect2({
            placeholder: "Select a permission schemes",
            allowClear: true
        });
        $(".project-configurator-notification-schemes").auiSelect2({
            placeholder: "Select a notification schemes",
            allowClear: true
        });
        $(".project-configurator-admin-user").auiSelect2({
            placeholder: "Select a admin user",
            allowClear: true
        });
        $(".project-configurator-project").auiSelect2({
            placeholder: "Select a project",
            allowClear: true
        });
        $(".project-configurator-issue-type").auiSelect2({
            placeholder: "Select an issue type",
            allowClear: true
        });
        $(".project-configurator-field").auiSelect2({
            placeholder: "Select a project configuration field",
            allowClear: true
        });
    });

    $(document).on('change', '.project-configurator-workflows', function() {
        var fieldValue = $(this).auiSelect2('data');
        $('#project-configurator-workflows').attr('value', fieldValue.length > 0 ? fieldValue.map(function(item) { return item.id }) : '');
    });

    $(document).on('change', '.project-configurator-issue-types', function() {
        var fieldValue = $(this).auiSelect2('data');
        $('#project-configurator-issue-types').attr('value', fieldValue.length > 0 ? fieldValue.map(function(item) { return item.id }) : '');
    });

    $(document).on('change', '.project-configurator-screen-schemes', function() {
        var fieldValue = $(this).auiSelect2('data');
        $('#project-configurator-screen-schemes').attr('value', fieldValue.length > 0 ? fieldValue.map(function(item) { return item.id }) : '');
    });

    $(document).on('change', '.project-configurator-permission-schemes', function() {
        var fieldValue = $(this).auiSelect2('data');
        $('#project-configurator-permission-schemes').attr('value', fieldValue.length > 0 ? fieldValue.map(function(item) { return item.id }) : '');
    });

    $(document).on('change', '.project-configurator-notification-schemes', function() {
        var fieldValue = $(this).auiSelect2('data');
        $('#project-configurator-notification-schemes').attr('value', fieldValue.length > 0 ? fieldValue.map(function(item) { return item.id }) : '');
    });

    $(document).on('change', '.project-configurator-admin-user', function() {
        var fieldValue = $(this).auiSelect2('data');
        $('#project-configurator-admin-user').attr('value', fieldValue != null ? fieldValue.id : null);
    });

    $(document).on('change', '.project-configurator-project', function() {
        var fieldValue = $(this).auiSelect2('data');
        $('#project-configurator-project').attr('value', fieldValue != null ? fieldValue.id : null);
    });

    $(document).on('change', '.project-configurator-issue-type', function() {
        var fieldValue = $(this).auiSelect2('data');
        $('#project-configurator-issue-type').attr('value', fieldValue != null ? fieldValue.id : null);
    });

    $(document).on('change', '.project-configurator-field', function() {
        var fieldValue = $(this).auiSelect2('data');
        $('#project-configurator-field').attr('value', fieldValue != null ? fieldValue.id : null);
    });
});
