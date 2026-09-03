package com.CineLock.Service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisSeatHoldService {

    private final StringRedisTemplate redisTemplate;

    public RedisSeatHoldService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void holdSeat(Long seatId, String username) {

        String key = "seat:hold:" + seatId;

        redisTemplate.opsForValue()
                .set(key, username, Duration.ofMinutes(2));
    }

    public boolean isSeatHeld(Long seatId) {

        String key = "seat:hold:" + seatId;

        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void releaseSeat(Long seatId) {

        String key = "seat:hold:" + seatId;

        redisTemplate.delete(key);
    }
}