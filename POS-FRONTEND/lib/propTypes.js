const chainableTypeChecker = () => {};
chainableTypeChecker.isRequired = () => {};

const createChainableTypeChecker = () => {
  const checker = () => {};
  checker.isRequired = () => {};
  return checker;
};

const PropTypes = {
  string: createChainableTypeChecker(),
  bool: createChainableTypeChecker(),
  func: createChainableTypeChecker(),
  elementType: createChainableTypeChecker(),
  oneOf: () => createChainableTypeChecker(),
  oneOfType: () => createChainableTypeChecker(),
  arrayOf: () => createChainableTypeChecker(),
  shape: () => createChainableTypeChecker(),
};

export default PropTypes;
