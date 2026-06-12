import PropTypes from "prop-types";
import { redirect } from "next/navigation";
import { ENTITY_CONFIG } from "../../lib/entityConfig";
import GenericForm from "../../components/GenericForm";

export default async function AddEntityPage({params,}) {
  const { entity } = await params;
  const config = ENTITY_CONFIG[entity];

  if (!config) {
    redirect("/home");}

  return (
    <div>
      <div className="mb-5">
        <h1 className="text-2xl font-bold capitalize text-black">
          Add New {entity}
        </h1>
      </div>
      <GenericForm
        entity={entity}
        config={config}
      />
    </div>
  );
}

AddEntityPage.propTypes = {
  params: PropTypes.shape({
    entity: PropTypes.string.isRequired,
  }).isRequired,
};