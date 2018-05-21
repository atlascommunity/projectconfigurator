import React from 'react';
import PropTypes from 'prop-types';

import QuestionCircleIcon from '@atlaskit/icon/glyph/question-circle';
import Tooltip from '@atlaskit/tooltip';
import {ValidatorMessage} from '@atlaskit/form';

import './fieldContainer.less';


class FieldContainer extends React.Component {
    static propTypes = {
        size: PropTypes.oneOf(['small', 'medium', 'large']),
        label: PropTypes.string,
        description: PropTypes.string,
        info: PropTypes.string,
        isInvalid: PropTypes.bool,
        invalidMessage: PropTypes.string
    };

    static defaultProps = {
        size: 'medium',
    };

    render() {
        return (
            <div>
                <div>
                    <div className={this.props.size}>
                        {this.props.hasOwnProperty('label') ?
                            <label className="field-label">{this.props.label}</label> :
                            null
                        }
                        {this.props.children}
                    </div>
                    {this.props.hasOwnProperty('info') ?
                        <div className="field-info">
                            <Tooltip content={this.props.info} position="right">
                                <QuestionCircleIcon size="small" />
                            </Tooltip>
                        </div> :
                        null
                    }
                </div>
                {this.props.hasOwnProperty('description') ?
                    <div className={`field-description ${this.props.size}`}>{this.props.description}</div> :
                    null
                }
                {this.props.hasOwnProperty('isInvalid') && this.props.isInvalid ?
                    <ValidatorMessage
                        isInvalid={this.props.isInvalid}
                        invalidMessage={this.props.invalidMessage}
                    />:
                    null
                }
            </div>
        );
    };
}

export default FieldContainer;
