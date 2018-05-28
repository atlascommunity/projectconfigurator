import $ from 'AJS.$';
import AJS from 'AJS';
import JIRA from 'JIRA';
import React from 'react';
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd';

import {connect} from 'react-redux';

import memoize from 'lodash.memoize';

import Page from '@atlaskit/page';
import TextField from '@atlaskit/field-text';
import Select, {AsyncSelect, CheckboxSelect} from '@atlaskit/select';
import Avatar, { AvatarItem } from '@atlaskit/avatar';
import QuestionCircleIcon from '@atlaskit/icon/glyph/question-circle';
import EditorCloseIcon from '@atlaskit/icon/glyph/editor/close';
import Tooltip from '@atlaskit/tooltip';
import Flag from '@atlaskit/flag';
import { colors } from '@atlaskit/theme';
import ErrorIcon from '@atlaskit/icon/glyph/error';
import Button, { ButtonGroup } from '@atlaskit/button';
import PeopleGroupIcon from '@atlaskit/icon/glyph/people-group';
import TrashIcon from '@atlaskit/icon/glyph/trash';
import DropdownMenu, { DropdownItemGroupCheckbox, DropdownItemCheckbox } from '@atlaskit/dropdown-menu';
import {ValidatorMessage} from '@atlaskit/form';

import ScreenSchemesDialog from './ScreenSchemesDialog';
import WorkflowInfoDialog from './WorkflowInfoDialog';

import i18n from '../common/i18n';
import FieldContainer from '../common/FieldContainer';

import {ScreenSchemeActionCreators} from '../service/project.configurator.reducer';


class ProjectConfigurator extends React.Component {
    constructor(props) {
        super(props);
        this.onDragEnd = this.onDragEnd.bind(this);
    }

    state = {
        projectName: null,
        projectKey: null,
        projectLead: null,
        issueTypes: [],
        workflows: {},
        permissionScheme: null,
        notificationScheme: null,
        usersGroups: [],
        usersProjectRoles: {},
        groupsProjectRoles: {},
        availableIssueTypes: [],
        availableWorkflows: [],
        availableScreenScheme: [],
        availablePermissionSchemes: [],
        availableProjectRoles: [],
        activeDialog: null,
        errors: {},
        errorMessages: {}
    };

    componentDidMount() {
        $.ajax({
            type: 'GET',
            url: `${AJS.contextPath()}/rest/projectconfigurator/latest/configuration/data`,
            context: this,
            success: function(data) {
                this.setState({
                    availableIssueTypes: data.issueTypes,
                    availableWorkflows: data.workflows,
                    availableScreenScheme: data.screenScheme,
                    availablePermissionSchemes: data.permissionSchemes,
                    availableNotificationSchemes: data.notificationSchemes,
                    availableProjectRoles: data.projectRoles
                });
            }
        });
    };

    _toggleDialog = memoize(
        (dialog) => () => this.setState(state => {
            if (state.activeDialog === dialog) {
                return {
                    activeDialog: null
                };
            }
            return {
                activeDialog: dialog
            };
        })
    );

    _selectProjectName = event => {
        this.setState({projectName: event.target.value});
    };

    _selectProjectKey = event => {
        this.setState({projectKey: event.target.value});
    };

    _selectProjectLead = option => {
        this.setState({projectLead: option});
    };

    _selectIssueTypes = options => {
        let workflows = {};
        let screenSchemes = {};
        options.forEach(issueType => {
            if (this.state.workflows.hasOwnProperty(issueType.id)) {
                workflows[issueType.id] = this.state.workflows[issueType.id];
            }
            if (this.props.screenSchemes.hasOwnProperty(issueType.id)) {
                screenSchemes[issueType.id] = this.props.screenSchemes[issueType.id];
            }
        });
        this.setState({
            issueTypes: options,
            workflows: workflows
        });
        this.props.updateScreenSchemes(screenSchemes);
    };

    _selectPermissionScheme = option => {
        this.setState({permissionScheme: option});
    };

    _selectNotificationScheme = option => {
        this.setState({notificationScheme: option});
    };

    _selectUserGroup = option => {
        if (this.state.usersGroups.indexOf(option) === -1) {
            let usersGroups = this.state.usersGroups;
            usersGroups.push(option);
            this.setState({usersGroups: usersGroups});
        }
    };

    _getProjectRoles = (item) => {
        const isUser = item.hasOwnProperty('key');
        const key = isUser ? item.key : item.id;

        let itemsProjectRoles = isUser ? this.state.usersProjectRoles : this.state.groupsProjectRoles;
        if (itemsProjectRoles.hasOwnProperty(key)) {
            return itemsProjectRoles[key].map(projectRole => projectRole.name).join(', ');
        } else {
            return i18n.getText('admin.notifications.choose.a.projectrole');
        }
    };

    _selectProjectRole = (item, projectRole) => () => {
        const isUser = item.hasOwnProperty('key');
        const key = isUser ? item.key : item.id;

        let itemsProjectRoles = isUser ? this.state.usersProjectRoles : this.state.groupsProjectRoles;
        if (!itemsProjectRoles.hasOwnProperty(key)) {
            itemsProjectRoles[key] = [projectRole];
        } else if (itemsProjectRoles[key].indexOf(projectRole) === -1) {
            let roles = itemsProjectRoles[key];
            roles.push(projectRole);
            itemsProjectRoles[key] = roles;
        } else {
            itemsProjectRoles[key] = itemsProjectRoles[key].filter(role => { return role.id !== projectRole.id; });
            if (itemsProjectRoles[key].length === 0) {
                delete itemsProjectRoles[key];
            }
        }

        if (isUser) {
            this.setState({usersProjectRoles: itemsProjectRoles});
        } else {
            this.setState({groupsProjectRoles: itemsProjectRoles});
        }
    };

    _removeUserGroup = (item) => () => {
        this.setState({usersGroups: this.state.usersGroups.filter(usersGroup => { return usersGroup.name !== item.name; })});
        if (item.hasOwnProperty('key')) {
            let usersProjectRoles = this.state.usersProjectRoles;
            delete usersProjectRoles[item.key];
            this.setState({usersProjectRoles: usersProjectRoles});
        } else {
            let groupsProjectRoles = this.state.groupsProjectRoles;
            delete groupsProjectRoles[item.id];
            this.setState({groupsProjectRoles: groupsProjectRoles});
        }
    };

    _loadUsers = inputValue => {
        return $.ajax({
            type: 'GET',
            url: `${AJS.contextPath()}/rest/projectconfigurator/latest/configuration/findUsers?filter=${inputValue}`,
        }).then(users => {
                return users;
            });
    };

    _loadUsersGroups = inputValue => {
        return $.ajax({
            type: 'GET',
            url: `${AJS.contextPath()}/rest/projectconfigurator/latest/configuration/findUsersGroups?filter=${inputValue}`,
        }).then(groupOptions => {
            return groupOptions;
        });
    };

    _formatIssueTypeOption = (option, properties) => {
        return properties.context === 'value' ?
            (
                <div className="issue-type-select-value">
                    <img alt={option.name} src={option.avatarUrl} />
                    <span>{option.name}</span>
                </div>
            ) :
            (
                <div className="issue-type-select-menu">
                    <span>{option.name}</span>
                    <img alt={option.name} src={option.avatarUrl} />
                </div>
            );
    };

    onDragEnd(result) {
        if (!result.destination) {
            return;
        }

        let workflows = this.state.workflows;
        workflows[result.destination.droppableId] = this.state.availableWorkflows.filter(workflow => workflow.id === result.draggableId)[0];
        this.setState({workflows: workflows});
    };

    _removeWorkflow = (issueTypeId) => () => {
        let workflows = this.state.workflows;
        delete workflows[issueTypeId];
        this.setState({workflows: workflows});
    };

    startPageLoading() {
        AJS.dim();
        JIRA.Loading.showLoadingIndicator();
    }

    finishPageLoading() {
        JIRA.Loading.hideLoadingIndicator();
        AJS.undim();
    }

    _buildProjectRoles() {
        if ($.isEmptyObject(this.state.usersGroups) && $.isEmptyObject(this.state.usersProjectRoles)) {
            return [];
        }

        let roles = [];
        this.state.availableProjectRoles.forEach(role => {

            let groups = [];
            Object.keys(this.state.groupsProjectRoles).forEach(group => {
                this.state.groupsProjectRoles[group].forEach(projectRole => {
                    if (projectRole.id === role.id) {
                        groups.push(group);
                    }
                });
            });

            let users = [];
            Object.keys(this.state.usersProjectRoles).forEach(user => {
                this.state.usersProjectRoles[user].forEach(projectRole => {
                    if (projectRole.id === role.id) {
                        users.push(user);
                    }
                });
            });

            if (users.length > 0 || groups.length > 0) {
                roles.push({
                    projectRoleId: role.id,
                    userKeys: users,
                    groupNames: groups
                });
            }
        });
        return roles;
    }

    _buildProcess() {
        if (this.state.issueTypes.length === 0) {
            return [];
        }

        let processes = [];
        this.state.issueTypes.forEach(issueType => {
            processes.push({
                issueTypeId: issueType.id,
                workflowName: this.state.workflows.hasOwnProperty(issueType.id) ? this.state.workflows[issueType.id].name : null,
                screenSchemeId: this.props.screenSchemes.hasOwnProperty(issueType.id) ? this.props.screenSchemes[issueType.id].id : null,
            });
        });
        return processes;
    }

    _createProjectConfiguration = () => {
        this.startPageLoading();
        this.setState({
            errors: {},
            errorMessages: {}
        });
        $.ajax({
            url: `${AJS.contextPath()}/rest/projectconfigurator/latest/configuration`,
            type: 'POST',
            context: this,
            contentType: 'application/json',
            data: JSON.stringify({
                projectName: this.state.projectName,
                projectKey: this.state.projectKey,
                projectLeadKey: this.state.projectLead != null ? this.state.projectLead.key : null,
                processes: this._buildProcess(),
                permissionSchemeId: this.state.permissionScheme != null ? this.state.permissionScheme.id : null,
                notificationSchemeId: this.state.notificationScheme != null ? this.state.notificationScheme.id : null,
                roles: this._buildProjectRoles()
            }),
            success: function(response) {
                this.finishPageLoading();
                window.location = `${AJS.contextPath()}/browse/${response.issueKey}`;
            },
            error: function(xhr) {
                try {
                    const response =  JSON.parse(xhr.responseText);
                    if (response.hasOwnProperty('errors')) {
                        this.setState({errors: response.errors});
                    }
                    if (response.hasOwnProperty('errorMessages')) {
                        this.setState({errorMessages: response.errorMessages});
                    }
                    this.finishPageLoading();
                } catch(e) {
                    alert(e); // error in the above string (in this case, yes)!
                }
            }
        });
    };

    render() {
        return (
            <Page>
                <div className="project-configurator-page-content">
                    <div className="project-configurator-page-header">{i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.title')}</div>
                    <div className="project-configurator-page-panel">
                        <div className="project-configurator-page-panel-header">{i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.generalInformation')}</div>
                        <div className="project-configurator-page-panel-content">
                            <FieldContainer
                                description={i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.project.description')}
                                isInvalid={this.state.errors.hasOwnProperty('projectName')}
                                invalidMessage={this.state.errors['projectName']}
                            >
                                <TextField label={i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.project.name')} shouldFitContainer maxLength={80} onChange={this._selectProjectName} />
                            </FieldContainer>
                            <FieldContainer
                                info={i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.project.key.info')}
                                isInvalid={this.state.errors.hasOwnProperty('projectKey')}
                                invalidMessage={this.state.errors['projectKey']}
                            >
                                <TextField label={i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.project.key')} shouldFitContainer maxLength={10} onChange={this._selectProjectKey} />
                            </FieldContainer>
                            <FieldContainer
                                label={i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.project.lead')}
                                info={i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.project.lead.info')}
                                isInvalid={this.state.errors.hasOwnProperty('projectLead')}
                                invalidMessage={this.state.errors['projectLead']}
                            >
                                <AsyncSelect
                                    cacheOptions
                                    loadOptions={this._loadUsers}
                                    defaultOptions
                                    onChange={this._selectProjectLead}
                                    placeholder={i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.project.lead.select')}
                                    getOptionLabel={option => option.displayName}
                                    formatOptionLabel={option => (<AvatarItem avatar={<Avatar src={option.avatarUrl} size="small" />} key={option.key} primaryText={option.displayName} backgroundColor="transparent" />)}
                                    getOptionValue={option => option.key}
                                    shouldFitContainer
                                />
                            </FieldContainer>
                        </div>
                    </div>
                    <div className="project-configurator-page-panel">
                        <div className="project-configurator-page-panel-header">{i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.configurator')}</div>
                        <div className="project-configurator-page-panel-content">
                            <FieldContainer
                                label={i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.issueTypes')}
                                isInvalid={this.state.errors.hasOwnProperty('issueTypes')}
                                invalidMessage={this.state.errors['issueTypes']}
                            >
                                <CheckboxSelect
                                    options={this.state.availableIssueTypes}
                                    onChange={this._selectIssueTypes}
                                    getOptionLabel={option => option.name}
                                    formatOptionLabel={this._formatIssueTypeOption}
                                    getOptionValue={option => option.id}
                                    placeholder={i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.issueTypes.select')}
                                    shouldFitContainer
                                />
                            </FieldContainer>
                        </div>
                    </div>
                    <div className="project-configurator-page-panel">
                        <div className="project-configurator-page-panel-header">{i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.workflow')}</div>
                        <div className="project-configurator-page-panel-content">
                            <div className="workflow-matching">
                                <DragDropContext onDragEnd={this.onDragEnd}>
                                    <Droppable droppableId="droppable" isDropDisabled>
                                        {(provided, snapshot) => ( // eslint-disable-line no-unused-vars
                                            <div ref={provided.innerRef} className="workflow-list">
                                                {this.state.availableWorkflows.map((workflow, index) => (
                                                    <div className="workflow" key={workflow.id} index={index}>
                                                        <Draggable draggableId={workflow.id}>
                                                            {(provided, snapshot) => (
                                                                <div
                                                                    ref={provided.innerRef}
                                                                    {...provided.draggableProps}
                                                                    {...provided.dragHandleProps}
                                                                    className={`workflow-inner ${snapshot.isDragging ? 'dragging' : ''}`}
                                                                >
                                                                    <span className="workflow-name">{workflow.name}</span>
                                                                    <span className="workflow-info">
                                                                        <QuestionCircleIcon size="small" onClick={this._toggleDialog(`workflow-${workflow.id}`)} />
                                                                    </span>
                                                                </div>
                                                            )}
                                                        </Draggable>
                                                        {this.state.activeDialog === `workflow-${workflow.id}`
                                                        && <WorkflowInfoDialog
                                                            workflow={workflow}
                                                            onClose={this._toggleDialog(`workflow-${workflow.id}`)}
                                                        />
                                                        }
                                                    </div>
                                                ))}
                                            </div>
                                        )}
                                    </Droppable>
                                    <div className="issue-type-list">
                                        {   this.state.issueTypes.length === 0 ?
                                            <Flag
                                                icon={<ErrorIcon label="Error" secondaryColor={colors.R400} />}
                                                title={i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.workflow.error')}
                                                description={i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.workflow.error.description')}
                                            /> :
                                            this.state.issueTypes.map((issueType, index) => (
                                                <Droppable droppableId={issueType.id} key={issueType.id} index={index} isDropDisabled={this.state.workflows.hasOwnProperty(issueType.id)}>
                                                    {(provided, snapshot) => (
                                                        <div
                                                            className={`workflow-assign ${snapshot.isDraggingOver ? 'dragging-over' : ''}`}
                                                            ref={provided.innerRef}
                                                            {...provided.droppableProps}
                                                        >
                                                            <div className="workflow-assign-inner">
                                                                <div className="workflow-assign-inner-issue-type">
                                                                    <img alt={issueType.name} src={issueType.avatarUrl} />
                                                                    <span>{issueType.name}</span>
                                                                </div>
                                                                {this.state.workflows.hasOwnProperty(issueType.id) ?
                                                                    <div className="workflow-assign-inner-workflow">
                                                                        <span className="workflow-name">{this.state.workflows[issueType.id].name}</span>
                                                                        <span className="workflow-info">
                                                                            <Tooltip content={i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.workflow.delete')} position="right">
                                                                                <EditorCloseIcon size="small" onClick={ this._removeWorkflow(issueType.id) } />
                                                                            </Tooltip>
                                                                        </span>
                                                                    </div> :
                                                                    <div className="workflow-assign-inner-workflow empty">{i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.workflow.add')}</div>
                                                                }
                                                            </div>
                                                        </div>
                                                    )}
                                                </Droppable>
                                            ))
                                        }
                                    </div>
                                </DragDropContext>
                            </div>
                        </div>
                        <ValidatorMessage
                            isInvalid={this.state.errors.hasOwnProperty('workflows')}
                            invalidMessage={this.state.errors['workflows']}
                        />
                    </div>
                    <div className="project-configurator-page-panel">
                        <div className="project-configurator-page-panel-header">{i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.screenSchemes')}</div>
                        <div className="project-configurator-page-panel-content">
                            <div className="issue-type-screen-schemes">
                                {this.state.issueTypes.length > 0 ?
                                    this.state.issueTypes.map(issueType => (
                                        <div className="issue-type-screen-scheme" key={issueType.id}>
                                            <div className="issue-type-select-value">
                                                <img alt={issueType.name} src={issueType.avatarUrl} />
                                                <span>{issueType.name}</span>
                                            </div>
                                            <div>
                                                <Button appearance="link" onClick={this._toggleDialog(`issue-type-${issueType.id}`)}>
                                                    {this.props.screenSchemes.hasOwnProperty(issueType.id) ? this.props.screenSchemes[issueType.id].name: i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.screenSchemes.select')}
                                                </Button>
                                            </div>
                                            {this.state.activeDialog === `issue-type-${issueType.id}` ?
                                                <ScreenSchemesDialog
                                                    issueType={issueType}
                                                    schemes={this.state.availableScreenScheme}
                                                    onClose={this._toggleDialog(`issue-type-${issueType.id}`)}
                                                /> :
                                                null
                                            }
                                        </div>
                                    ))
                                    :
                                    <Flag
                                        icon={<ErrorIcon label="Error" secondaryColor={colors.R400} />}
                                        title={i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.workflow.error')}
                                        description={i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.screenSchemes.error.description')}
                                    />
                                }
                            </div>
                        </div>
                        <ValidatorMessage
                            isInvalid={this.state.errors.hasOwnProperty('screenSchemes')}
                            invalidMessage={this.state.errors['screenSchemes']}
                        />
                    </div>
                    <div className="project-configurator-page-panel">
                        <div className="project-configurator-page-panel-header">{i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.roles')}</div>
                        <div className="project-configurator-page-panel-content">
                            <FieldContainer
                                label={i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.permissions')}
                                isInvalid={this.state.errors.hasOwnProperty('permissionScheme')}
                                invalidMessage={this.state.errors['permissionScheme']}
                            >
                                <Select
                                    options={this.state.availablePermissionSchemes}
                                    getOptionLabel={option => option.name}
                                    getOptionValue={option => option.id}
                                    onChange={this._selectPermissionScheme}
                                    placeholder={i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.permissions.select')}
                                    shouldFitContainer
                                />
                            </FieldContainer>
                            <FieldContainer
                                label={i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.notifications')}
                                isInvalid={this.state.errors.hasOwnProperty('notificationScheme')}
                                invalidMessage={this.state.errors['notificationScheme']}
                            >
                                <Select
                                    options={this.state.availableNotificationSchemes}
                                    getOptionLabel={option => option.name}
                                    getOptionValue={option => option.id}
                                    onChange={this._selectNotificationScheme}
                                    placeholder={i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.notifications.select')}
                                    shouldFitContainer
                                />
                            </FieldContainer>
                            <FieldContainer
                                label={i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.roles')}
                                size="large"
                                isInvalid={this.state.errors.hasOwnProperty('roles')}
                                invalidMessage={this.state.errors['roles']}
                            >
                                <AsyncSelect
                                    cacheOptions
                                    loadOptions={this._loadUsersGroups}
                                    defaultOptions
                                    onChange={this._selectUserGroup}
                                    placeholder={i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.roles.select')}
                                    getOptionLabel={option => option.hasOwnProperty('key') ? option.displayName : option.name}
                                    formatOptionLabel={option => (<AvatarItem avatar={option.hasOwnProperty('key') ? <Avatar src={option.avatarUrl} size="small" /> : <PeopleGroupIcon size="small"/>} key={option.name} primaryText={option.hasOwnProperty('key') ? option.displayName : option.name} backgroundColor="transparent" />)}
                                    getOptionValue={option => option.name}
                                    value={null}
                                    shouldFitContainer
                                />
                                <div className="role-users-groups">
                                    {this.state.usersGroups.length > 0 ?
                                        this.state.usersGroups.map(item => (
                                            <div className="role-user-group" key={item.name}>
                                                <div className="role-user-group-avatar">
                                                    <AvatarItem avatar={item.hasOwnProperty('key') ? <Avatar src={item.avatarUrl} size="small" /> : <PeopleGroupIcon size="small"/>} key={item.name} primaryText={item.hasOwnProperty('key') ? item.displayName : item.name} backgroundColor="transparent" />
                                                </div>
                                                <div className="role-user-group-actions">
                                                    <div className="role-user-group-assign">
                                                        <DropdownMenu triggerType="button" trigger={this._getProjectRoles(item)}>
                                                            <DropdownItemGroupCheckbox>
                                                                {this.state.availableProjectRoles.map(projectRole => (
                                                                    <DropdownItemCheckbox
                                                                        id={projectRole.id}
                                                                        key={projectRole.id}
                                                                        onClick={this._selectProjectRole(item, projectRole)}
                                                                    >
                                                                        {projectRole.name}
                                                                    </DropdownItemCheckbox>
                                                                ))}
                                                            </DropdownItemGroupCheckbox>
                                                        </DropdownMenu>
                                                    </div>
                                                    <div className="role-user-group-delete">
                                                        <TrashIcon size="small" onClick={this._removeUserGroup(item)} />
                                                    </div>
                                                </div>
                                            </div>
                                        )) :
                                        null
                                    }
                                </div>
                            </FieldContainer>
                        </div>
                    </div>
                    <div className="project-configurator-page-panel">
                        <div className="project-configurator-page-panel-content">
                            <ButtonGroup>
                                <Button appearance="primary" onClick={this._createProjectConfiguration}>{i18n.getText('common.words.create')}</Button>
                                <Button onClick={() => { window.location = `${AJS.contextPath()}/secure/Dashboard.jspa`; }}>{i18n.getText('common.words.cancel')}</Button>
                            </ButtonGroup>
                        </div>
                    </div>
                </div>
            </Page>
        );
    }
}

export default connect(
    state => {
        return {
            screenSchemes: state.screenSchemeReducer.selectedScreenSchemes
        };
    },
    ScreenSchemeActionCreators
)(ProjectConfigurator) ;
