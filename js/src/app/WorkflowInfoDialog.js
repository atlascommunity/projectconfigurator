import $ from 'AJS.$';
import React from 'react';
import PropTypes from 'prop-types';

import Modal from '@atlaskit/modal-dialog';

import i18n from '../common/i18n';


class WorkflowInfoDialog extends React.Component {
    static propTypes = {
        workflow: PropTypes.object.isRequired,
        onClose: PropTypes.func.isRequired,
    };

    state = {
        workflowDesigner: {}
    };
    _onClose() {
        this.props.onClose();
    }

    _initWorkflowDesigner = () => {
        let workflowDesigner = new JIRA.WorkflowDesigner.Application({  // eslint-disable-line no-undef
            element: $(`#workflow-${this.props.workflow.id}-info`),
            workflowId: this.props.workflow.name,
            immutable: true
        });
        this.setState({workflowDesigner: workflowDesigner});
    };

    _destroyWorkflowDesigner = () => {
        this.state.workflowDesigner.destroy();
    };

    render() {
        const {workflow, onClose} = this.props;

        const actions = [
            {
                text: i18n.getText('common.words.close'),
                onClick: onClose,
            }
        ];

        return (
            workflow.active?
            <Modal
                heading={workflow.name}
                scrollBehavior="outside"
                onOpenComplete={this._initWorkflowDesigner}
                actions={actions}
                onClose={onClose}
                onCloseComplete={this._destroyWorkflowDesigner}
                width="large"
            >
                <div id={`workflow-${workflow.id}-info`} className="flex-column full-width" />
            </Modal>:
            <Modal
                heading={workflow.name}
                actions={actions}
                onClose={onClose}
                width="large"
            >
                <div>{i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.workflow.inactive')}</div>
            </Modal>
        );
    }
}

export default WorkflowInfoDialog;
