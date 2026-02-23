// src/pages/energy/components/EnergyDeviceTable.jsx

function EnergyDeviceTable({
  devices,
  loading,
  pageInfo,
  statusFilter,
  setStatusFilter,
  onRefresh,
  onOpenDetail,
}) {
  return (
    <div className="device-table">
      <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
        <option value="">전체</option>
        <option value="CHECK_REQUIRED">점검 필요</option>
        <option value="CHECKING">점검 중</option>
        <option value="NORMAL">정상</option>
      </select>

      {loading ? (
        <p>로딩중...</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>설비명</th>
              <th>상태</th>
              <th>추정 낭비(kWh)</th>
              <th>절감액</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {devices.map((d) => (
              <tr key={d.deviceId}>
                <td>{d.deviceName}</td>
                <td>{d.deviceStatus}</td>
                <td>{d.estimatedWasteKwh}</td>
                <td>{d.estimatedWasteCost}</td>
                <td>
                  <button onClick={() => onOpenDetail(d)}>상세</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default EnergyDeviceTable;
