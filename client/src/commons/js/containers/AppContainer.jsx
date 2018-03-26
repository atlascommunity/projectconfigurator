import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { FormattedMessage } from 'react-intl';
import approvedBrowser from 'approved-browser';
import i18n from '../../../configurator/js/i18n';

import { setCookie, getCookie } from '/js/utils/cookies';

class _AppContainer extends Component {
    static propTypes = {
        background: PropTypes.string,
    };

    constructor(props) {
        super(props);
        this.state = {
            browserApproved: true,
            hideBrowserUpdateNotification: !!getCookie('hideBrowserUpdateNotification'),
        };

        approvedBrowser({
            Safari: 10,
            IE: 11,
            strict: false,
        }, (approved, ua) => {
            if (!approved) {
                this.state.browserApproved = false;
            }
        });
    }

    componentWillMount() {
        window.scrollTo(0, 0);
    }

    closeUpdateBrowserNotification(e) {
        e.preventDefault();
        setCookie('hideBrowserUpdateNotification', true);
        this.setState({
            hideBrowserUpdateNotification: true,
        });
    }

    render() {
        return (
            <div className="app-container">
                <div className={`b-wrapper ${this.props.wrapperAdditionalClass || ''}`}>
                    <div className="b-content">
                        <div className={`b-content_wrapper ${this.props.contentWrapperAdditionalClass || ''}`}>
                            {this.props.children}
                        </div>
                    </div>
                </div>

                {!this.state.browserApproved && !window.location.href.includes('browser-update') && !this.state.hideBrowserUpdateNotification ?
                    <div className="b-browser-update-notification">
                        <FormattedMessage
                            id="yourBrowserIsOutdated"
                            defaultMessage="Ваш браузер устарел"
                        />. <FormattedMessage
                        id="please"
                        defaultMessage="Пожалуйста"
                    />, <Link to="/browser-update/">
                        <FormattedMessage id="updateIt" defaultMessage="обновите его" />
                    </Link>.
                        <div className="close-container">
                            <a href="#" onClick={ e => this.closeUpdateBrowserNotification(e) }><i className="icon-close-2" /></a>
                        </div>
                    </div> :
                    null
                }

                <footer className="b-footer">
                    <div className="b-section_wrapper">
                        <div className="b-footer-left">
                            <div className="b-logo-footer"></div>
                            <div className="b-footer-menu">
                                <ul className="b-footer-menu-list">
                                    <li className="b-footer-menu-list-item">
                                        <a
                                            href="https://confluence.mail.ru/pages/viewpage.action?pageId=110140610"
                                            className="b-footer-menu-list-item-url"
                                            target="_blank"
                                        >
                                            {i18n.getText('ru.mail.jira.plugins.negotiation.footer.faq')}
                                        </a>
                                    </li>
                                    <li className="b-footer-menu-list-item">
                                        <a
                                            href="https://jira.mail.ru/secure/CreateIssueDetails!init.jspa?pid=10068&issuetype=14001&summary=%D0%9F%D1%80%D0%BE%D0%B1%D0%BB%D0%B5%D0%BC%D0%B0%20%D1%81%20%D1%81%D0%BE%D0%B3%D0%BB%D0%B0%D1%81%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5%D0%BC%20%D0%B4%D0%BE%D0%B3%D0%BE%D0%B2%D0%BE%D1%80%D0%BE%D0%B2"
                                            className="b-footer-menu-list-item-url"
                                            target="_blank"
                                        >
                                            {i18n.getText('ru.mail.jira.plugins.negotiation.footer.support')}
                                        </a>
                                    </li>
                                </ul>
                            </div>
                        </div>
                        <div className="b-footer-right">
                        </div>
                    </div>
                </footer>
            </div>
        );
    }
}

const mapStateToProps = (state, props) => props;
export default connect(mapStateToProps)(_AppContainer);
