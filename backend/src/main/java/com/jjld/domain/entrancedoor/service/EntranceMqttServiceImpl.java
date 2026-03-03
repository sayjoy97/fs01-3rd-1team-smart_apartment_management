package com.jjld.domain.entrancedoor.service;


import com.jjld.domain.entrancedoor.entity.EntranceGateLog;
import com.jjld.domain.entrancedoor.entity.Enum.AccessType;
import com.jjld.domain.entrancedoor.entity.Enum.FailReason;
import com.jjld.domain.entrancedoor.repository.EntranceGateLogRepository;
import com.jjld.domain.house.entity.EntranceCard;
import com.jjld.domain.house.entity.House;
import com.jjld.domain.house.repository.EntranceCardRepository;
import com.jjld.domain.house.repository.HouseRepository;
import com.jjld.global.mqtt.MqttPublish;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class  EntranceMqttServiceImpl implements EntranceMqttService{
    private final EntranceCardRepository cardRepository;
    private final EntranceGateLogRepository gateLogRepository;
    private final HouseRepository houseRepository;
    private final MqttPublish mqttPublish;
    private final PasswordEncoder passwordEncoder;


    // 로그 기록+mqtt 발행 공통
    private void recordLogAndPublish(Integer houseDong, Integer houseHo,
                                     AccessType accessType, boolean outcome,
                                     FailReason failReason, String topicSuffix){

        // 로그 기록
        EntranceGateLog logEntry = EntranceGateLog.builder()
                .houseDong(houseDong)
                .houseHo(houseHo)
                .accessType(accessType)
                .outcome(outcome)
                .failReason(failReason)
                .build();
        gateLogRepository.save(logEntry);

        // MQTT 발행
        String topic = String.format("jjld/entrance/door/%d/%d/%s", houseDong, houseHo, topicSuffix);
        mqttPublish.sendToMqtt(outcome ? "OK" : "FAIL", topic);

        log.info("MQTT publish: {} | outcome: {} | failReason: {}", topic, outcome, failReason);
    }
    // 현관 카드조회
    @Transactional
    @Override
    public boolean handleCard(Integer houseDong, String cardUid) {

        EntranceCard card = cardRepository
                .findByCardUidAndHouse_HouseDong(cardUid, houseDong);

        boolean success = card != null;
        Integer houseHo = success ? card.getHouse().getHouseHo() : 0;

        // 로그 기록
        recordLogAndPublish(
                houseDong,
                houseHo,
                AccessType.RESIDENT_CARD,
                success,
                success ? FailReason.NONE : FailReason.INVALID_CARD,
                "CARD_RESULT"
        );

        return success;
    }

    @Override
    public boolean handlePass(Integer houseDong, Integer houseHo, String rawPass) {

        House house = houseRepository.findByHouseInfo(houseDong, houseHo);

        boolean success = house != null;
        FailReason failReason = FailReason.NONE;

        if(house == null){
            success = false;
            failReason = FailReason.NOT_EXIST_HOUSE;
        }else{
            String encodedePass = house.getEntrancePass();

            if(passwordEncoder.matches(rawPass, encodedePass)){
                success = true;
            }else{
                success = false;
                failReason = FailReason.WRONG_PASSWORD;
            }
        }

        recordLogAndPublish(
                houseDong,
                houseHo,
                AccessType.RESIDENT_PASSWORD,
                success,
                success ? FailReason.NONE : failReason,
                "PASS_RESULT"
        );


        return success;
    }
}
