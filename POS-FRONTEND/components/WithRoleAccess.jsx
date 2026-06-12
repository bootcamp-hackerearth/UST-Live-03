import PropTypes from 'prop-types'
import useRoleValidation from '@/hooks/useRoleValidation'
import AccessDenied from '@/components/AccessDenied'
import Spinner from '@/components/Spinner'

const WithRoleAccess = ({ urlName, children }) => {
    const flag = useRoleValidation(urlName)

    if (flag === null) return <Spinner />
    if (!flag) return <AccessDenied />

    return children
}

WithRoleAccess.propTypes = {
    urlName: PropTypes.string.isRequired,
    children: PropTypes.node.isRequired,
}

export default WithRoleAccess