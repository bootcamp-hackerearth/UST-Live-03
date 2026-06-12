import { requiredValidation } from "@/validation/validation"

export const ProjectPageDropDowns = [
        { name: "brand", urlName: "brand/getAllActive", httpMethod: "get", ismultiple: false, validation: requiredValidation },
        { name: "model", urlName: "models/getAllActive", httpMethod: "get", ismultiple: false, validation: requiredValidation },
        { name: "category", urlName: "category/findAllWithSuperCategoryEmpty", httpMethod: "get", ismultiple: false, validation: requiredValidation },
        { name: "unit", urlName: "unit/getAllActive", httpMethod: "get", ismultiple: false, validation: requiredValidation }
    ]