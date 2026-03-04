import heapq

class Elevator:
    def __init__(self):
        self.current_floor = 1
        self.direction = "STAY"
        self.system_status = "IDLE"
        self.up_heap = []
        self.down_heap = []

    def process_command(self, target_floor, status):
        if target_floor == -1: return
        if str(status) == '1': # 예약
            if target_floor > self.current_floor:
                if target_floor not in self.up_heap: heapq.heappush(self.up_heap, target_floor)
            elif target_floor < self.current_floor:
                if -target_floor not in self.down_heap: heapq.heappush(self.down_heap, -target_floor)
        elif str(status) == '0': # 취소
            self.remove_from_heap(target_floor)

    def remove_from_heap(self, floor):
        if floor in self.up_heap:
            self.up_heap.remove(floor)
            heapq.heapify(self.up_heap)
        if -floor in self.down_heap:
            self.down_heap.remove(-floor)
            heapq.heapify(self.down_heap)