import PropTypes from 'prop-types'

export default function LoginLayout({ children }) {
    return (
        <div>
            {children}
        </div>
    )
}

LoginLayout.propTypes = {
    children: PropTypes.node.isRequired,
}