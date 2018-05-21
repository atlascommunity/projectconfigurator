import React from 'react';
import PropTypes from 'prop-types';

import {connect} from 'react-redux';

import Button from '@atlaskit/button';
import ChevronDownIcon from '@atlaskit/icon/glyph/chevron-down';
import ChevronUpIcon from '@atlaskit/icon/glyph/chevron-up';
import CheckIcon from '@atlaskit/icon/glyph/check';
import TableTree, { Headers, Header, Rows } from '@atlaskit/table-tree';

import Screen from './Screen';

import i18n from '../common/i18n';

import {ScreenSchemeActionCreators} from '../service/project.configurator.reducer';


class ScreenScheme extends React.Component {
    static propTypes = {
        screenScheme: PropTypes.object.isRequired,
        selectedScheme: PropTypes.object
    };

    state = {
        expand: false,
    };

    _expand = () => {
        this.setState({expand: !this.state.expand});
    };

    render() {
        const {screenScheme, selectedScreenScheme, selectedScheme} = this.props;
        const {expand} = this.state;

        const selected = (selectedScreenScheme != null && selectedScreenScheme.id === screenScheme.id) || (selectedScreenScheme == null && selectedScheme !== undefined && selectedScheme.id === screenScheme.id);

        return (
            <div className="screen-scheme">
                <div className={`screen-scheme-general ${selected ? 'selected' : ''}`} onClick={() => this.props.selectScreenScheme(screenScheme)}>
                    <div className="screen-scheme-title">
                        <div className={selected ? 'screen-scheme-icon-selected' : 'screen-scheme-not-selected'}>
                            {selected ?
                                <CheckIcon size="medium" /> :
                                null
                            }
                        </div>
                        {screenScheme.name}
                    </div>
                    <div>
                        {expand ?
                            <Button appearance="subtle-link" onClick={this._expand}>
                                <ChevronUpIcon size="large" />
                            </Button> :
                            <Button appearance="subtle-link" onClick={this._expand}>
                                <ChevronDownIcon size="large" />
                            </Button>
                        }
                    </div>
                </div>
                <div className={`screen-scheme-details ${expand ? '' : 'hide'}`}>
                    <TableTree>
                        <Headers>
                            <Header width="30%">{i18n.getText('admin.common.words.operation')}</Header>
                            <Header width="60%">{i18n.getText('ru.mail.jira.plugins.projectconfigurator.page.screenSchemes.screen')}</Header>
                            <Header width="10%">{i18n.getText('admin.common.words.details')}</Header>
                        </Headers>
                        <Rows
                            items={(parent) => { return parent ? []: screenScheme.children; }}
                            render={({id, name, children}) => (
                                <Screen screen={{id, name, children}}/>
                            )}
                        />
                    </TableTree>
                </div>
            </div>
        );
    }
}

export default connect(
    state => {
        return {
            selectedScreenScheme: state.screenSchemeReducer.selectedScreenScheme
        };
    },
    ScreenSchemeActionCreators
)(ScreenScheme) ;
