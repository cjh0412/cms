package com.cjhdev.cms.order.client;

import com.cjhdev.cms.order.domain.redis.Cart;
import com.cjhdev.cms.order.exception.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import static com.cjhdev.cms.order.exception.ErrorCode.CART_CHANGE_FAIL;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisClient {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final ObjectMapper mapper = new ObjectMapper();

    public <T> T get(Long key, Class<T> classType){
        return get(key.toString(), classType);
    }

    private <T> T get(String key, Class<T> classType){
        String redisValue = (String) redisTemplate.opsForValue().get(key);
        if(ObjectUtils.isEmpty(redisValue)){
            return null;
        }else{
            try {
                return mapper.readValue(redisValue, classType);
            } catch (JsonProcessingException e) {
               log.error("parsing error", e);
               return null;
            }
        }
    }


    public void put(Long key, Cart cart){
        put(key.toString(), cart);
    }

    public void put(String key, Cart cart){
       try {
           redisTemplate.opsForValue().set(key, mapper.writeValueAsString(cart));
       } catch (JsonProcessingException e) {
           throw new CustomException(CART_CHANGE_FAIL);
       }
    }
}
