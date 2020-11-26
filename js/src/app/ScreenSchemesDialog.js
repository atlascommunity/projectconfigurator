import React from 'react';
import PropTypes from 'prop-types';

import {connect} from 'react-redux';

import Modal from '@atlaskit/modal-dialog';

import ScreenScheme from './ScreenScheme';

import i18n from '../common/i18n';

import {ScreenSchemeActionCreators} from '../service/project.configurator.reducer';


class ScreenSchemesDialog extends React.Component {
    static propTypes = {
        issueType: PropTypes.object.isRequired,
        schemes: PropTypes.array.isRequired,
        onClose: PropTypes.func.isRequired,
    };

    _onClose() {
        this.props.onClose();
    }

    render() {
        const {issueType, schemes, onClose, selectedScreenScheme, selectedScreenSchemes} = this.props;

        const actions = [
            {
                text: i18n.getText('common.words.select'),
                onClick: () => {
                    if (selectedScreenScheme == null) {
                        onClose();
                    } else {
                        this.props.addScreenScheme(issueType, selectedScreenScheme);
                        onClose();
                    }
                },
            },
            {
                text: i18n.getText('common.words.close'),
                onClick: () => {
                    this.props.selectScreenScheme(null);
                    onClose();
                },
            }
        ];

        return (
            <Modal
                heading={`${i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.screenSchemes.select.issueType')} ${issueType.name}`}
                scrollBehavior="outside"
                actions={actions}
                onClose={onClose}
                width="large"
            >
                <div>
                    {schemes.map(scheme => (
                        <ScreenScheme  screenScheme={scheme} selectedScheme={selectedScreenSchemes[issueType.id]} key={scheme.id} />
                    ))}
                </div>
            </Modal>
        );
    }
}

export default connect(
    state => {
        return {
            selectedScreenScheme: state.screenSchemeReducer.selectedScreenScheme,
            selectedScreenSchemes: state.screenSchemeReducer.selectedScreenSchemes
        };
    },
    ScreenSchemeActionCreators
)(ScreenSchemesDialog) ;
