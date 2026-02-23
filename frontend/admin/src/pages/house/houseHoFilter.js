export function houseHoFilter(maxFloor, unitsPerFloor) {
  const options = [];

  for (let floor = 1; floor <= maxFloor; floor++) {
    for (let unit = 1; unit <= unitsPerFloor; unit++) {
      const ho = floor * 100 + unit;

      options.push({
        label: `${ho}호`,
        value: ho,
      });
    }
  }

  return options;
}
