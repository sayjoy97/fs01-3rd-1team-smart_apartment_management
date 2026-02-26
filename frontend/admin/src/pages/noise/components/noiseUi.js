// noise/components/noiseUi.js

export function statusUi(status) {
  switch (status) {
    case "UNPROCESSED":
      return { label: "대기", cls: "st-wait" };
    case "OBSERVING":
      return { label: "관찰 중", cls: "st-observing" };
    case "NOTIFIED":
      return { label: "알림 완료", cls: "st-notified" };
    default:
      return { label: status ?? "-", cls: "st-wait" };
  }
}
