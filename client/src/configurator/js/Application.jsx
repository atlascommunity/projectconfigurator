import React from 'react';


class Application extends React.Component {

    componentWillMount() {

    }

    render() {
        return (
            <div>
                {this.props.children}
            </div>
        );
    }
}

export default Application;
