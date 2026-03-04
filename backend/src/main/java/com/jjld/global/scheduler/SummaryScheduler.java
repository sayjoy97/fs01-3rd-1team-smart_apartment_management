package com.jjld.global.scheduler;

import com.jjld.domain.complaint.ai.AiSummaryService;
import com.jjld.domain.complaint.ai.SummaryResponse;
import com.jjld.domain.complaint.entity.Complaint;
import com.jjld.domain.complaint.entity.ComplaintAnalysis;
import com.jjld.domain.complaint.entity.Enum.SummaryStatus;
import com.jjld.domain.complaint.repository.ComplaintAnalysisRepository;
import com.jjld.domain.complaint.repository.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class SummaryScheduler {
    private final ComplaintRepository complaintRepository;
    private final ComplaintAnalysisRepository analysisRepository;
    private final AiSummaryService aiSummaryService;


    // 1분마다
    @Transactional
    @Scheduled(fixedDelay = 6000000)
    public void run(){
        List<Complaint> waitingList = complaintRepository.findBySummaryStatus(SummaryStatus.WAITING);

        log.info("요약 대상 개수 = {}", waitingList.size());

        for (Complaint complaint : waitingList){
            // 이미 요약 있으면 스킵
            if(analysisRepository.existsByComplaint(complaint)){
                continue;
            }

            String content = complaint.getContent();

            // 일정 글자 수 미만은 요약 대상 제외
            if(content.length() < 100) {
                complaint.setSummaryStatus(SummaryStatus.NOT_REQUIRED);
                complaintRepository.save(complaint);
                continue;
            }

            try{
                // AI 호출
                SummaryResponse response = aiSummaryService.summarize(content);
                String summary = response.getSummary();

                ComplaintAnalysis analysis =
                        ComplaintAnalysis.builder()
                                .complaint(complaint)
                                .summary(summary)
                                .build();

                analysisRepository.save(analysis);

                complaint.setSummaryStatus(SummaryStatus.COMPLETED);
                complaintRepository.save(complaint);

                log.info("요약 완료 id={}", complaint.getComplaintId());

            }catch (Exception e){
                log.info("요약 실패 id= "+complaint.getComplaintId());
            }
        }
        }
}
