"use client";

import React from "react";
import PropTypes from "prop-types";

import DynamicList from "./DynamicList";
import POSLayout from "../PosLayout";

function ListPage({
  title,
  routeName,
  columns,
  addUrl,
  editUrl,
}) {
  return (
    <POSLayout>
      <DynamicList
        title={title}
        routeName={routeName}
        columns={columns}
        addUrl={addUrl}
        editUrl={editUrl}
      />
    </POSLayout>
  );
}

ListPage.propTypes = {
  title: PropTypes.string.isRequired,
  routeName: PropTypes.string.isRequired,
  columns: PropTypes.arrayOf(PropTypes.object).isRequired,
  addUrl: PropTypes.string,
  editUrl: PropTypes.string,
};

export default ListPage;