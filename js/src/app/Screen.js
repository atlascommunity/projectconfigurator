import React from 'react';
import PropTypes from 'prop-types';

import InlineDialog from '@atlaskit/inline-dialog';
import { Row, Cell } from '@atlaskit/table-tree';
import QuestionCircleIcon from '@atlaskit/icon/glyph/question-circle';


class Screen extends React.Component {
    static propTypes = {
        screen: PropTypes.object.isRequired,
    };

    state = {
        dialogOpen: false,
    };

    _toggleDialog = () => this.setState({ dialogOpen: !this.state.dialogOpen });

    render() {
        const {screen} = this.props;
        const {dialogOpen} = this.state;

        return (
            <Row itemId={screen.id} hasChildren={false}>
                <Cell singleLine>{screen.id}</Cell>
                <Cell singleLine>{screen.name}</Cell>
                <Cell singleLine className="screen-information">
                    <InlineDialog
                        content={
                            screen.children.map(field => (
                                <div key={field.id}>{field.name}</div>
                            ))
                        }
                        position="right middle"
                        isOpen={dialogOpen}
                    >
                        <QuestionCircleIcon size="small" onClick={this._toggleDialog} />
                    </InlineDialog>
                </Cell>
            </Row>
        );
    }
}

export default Screen;
