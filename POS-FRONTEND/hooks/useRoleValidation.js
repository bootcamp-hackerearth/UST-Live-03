import { useEffect, useState } from 'react'
import { validateRole } from '@/validation/validateRole'

const useRoleValidation = (urlName) => {
    const [flag, setFlag] = useState(null)

    useEffect(() => {
        async function roleValidate() {
            const status = await validateRole(urlName)
            setFlag(status)
        }
        roleValidate()
    }, [urlName])

    return flag
}

export default useRoleValidation