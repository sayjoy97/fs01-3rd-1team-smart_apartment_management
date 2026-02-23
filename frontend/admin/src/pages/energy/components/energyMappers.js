// src/pages/energy/components/energyMappers.js

export function mapDeviceRow(dto) {
  return {
    deviceId: dto.deviceId,
    deviceName: dto.deviceName,
    deviceStatus: dto.deviceStatus,
    deviceType: dto.deviceType,
    building: dto.building,
    location: dto.location,
    estimatedWasteKwh: dto.estimatedWasteKwh || 0,
    estimatedWasteCost: dto.estimatedWasteCost || 0,
    monthChangeRate: dto.monthChangeRate || 0,
  };
}
