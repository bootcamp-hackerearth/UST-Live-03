import PropTypes from "prop-types";
import { redirect } from "next/navigation";
import { ENTITY_CONFIG } from "../../lib/entityConfig";
import GenericForm from "../../components/GenericForm";

export default async function AddEntityPage({ params, resolvedSearchParams }) {
  const { entity } = await params;
  const config = ENTITY_CONFIG[entity];

  if (!config) {
    redirect("/home");
  }

  let initialData = {};

  if (entity === "cartEntry") {
    initialData = {
      cartId: resolvedSearchParams?.cartId ?? "",
      productId: resolvedSearchParams?.productId ?? "",
      quantity: resolvedSearchParams?.quantity ? Number(resolvedSearchParams.quantity) : 1,
      unitPrice: resolvedSearchParams?.unitPrice ? Number(resolvedSearchParams.unitPrice) : "",
      totalPrice: resolvedSearchParams?.totalPrice ? Number(resolvedSearchParams.totalPrice) : "",
      productName: resolvedSearchParams?.productName ?? "",
    };
  }

  if (entity === "customer") {
    initialData = {
      name: resolvedSearchParams?.name ?? "",
      phoneNo: resolvedSearchParams?.phoneNo ?? "",
      identifier: resolvedSearchParams?.identifier ?? "",
      partyType: resolvedSearchParams?.partyType ?? "",
      balance: resolvedSearchParams?.balance ? Number(resolvedSearchParams.balance) : 0,
      creditLimit: resolvedSearchParams?.creditLimit ? Number(resolvedSearchParams.creditLimit) : 0,
    };
  }

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
        initialData={initialData}
      />  
    </div>
  );
}

AddEntityPage.propTypes = {
  params: PropTypes.shape({
    entity: PropTypes.string.isRequired,
  }).isRequired,
  resolvedSearchParams: PropTypes.object
};