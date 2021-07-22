package com.cheney.utils;

import org.apache.commons.lang3.RandomUtils;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 随机id生成器
 *
 * @author by chenyi
 * @date 2021/7/16
 */
public class RandomIdGenerator {

    public static String generateWithTime(DateTimeFormatter formatter, int randomSize) {
        String id = LocalDateTime.now().format(formatter);
        if (randomSize > 0) {
            int max = (int) Math.pow(10, randomSize) - 1;
            int numFlag = RandomUtils.nextInt(0, max);
            id += fixSize(numFlag, randomSize);
        }
        return id;
    }

    private static String fixSize(int val, int size) {
        String valStr = String.valueOf(val);
        int length = valStr.length();
        if (length == size) {
            return valStr;
        } else if (length > size) {
            return valStr.substring(0, size);
        } else {
            return Flux.range(0, size - length)
                    .toStream().map(String::valueOf)
                    .reduce((b, a) -> a + b)
                    .orElse("") + valStr;
        }
    }

}
