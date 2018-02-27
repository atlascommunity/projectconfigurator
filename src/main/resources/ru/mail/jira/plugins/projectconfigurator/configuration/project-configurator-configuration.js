(function ($) {
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
})(AJS.$);
