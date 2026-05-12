package com.AAZl3l4.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 敏感词过滤工具类 - 基于DFA（确定性有限状态自动机）算法
 * 支持敏感词检测、替换等功能
 */
@Slf4j
@Component
public class SensitiveWordUtil {

    /**
     * 敏感词库文件路径
     */
    private static final String WORD_FILE = "sensitive-word.txt";

    /**
     * 敏感词替换符
     */
    private static final String REPLACEMENT = "***";

    /**
     * 敏感词字典树根节点
     * key: 字符, value: 子节点Map
     */
    private static final Map<Character, Map> WORD_TREE = new HashMap<>();

    /**
     * 标记敏感词结束节点
     */
    private static final String END_FLAG = "end";

    /**
     * 初始化加载敏感词库
     */
    // 静态代码块：类加载时自动执行，不依赖 Spring
    static {
        try {
            ClassPathResource resource = new ClassPathResource(WORD_FILE);
            if (!resource.exists()) {
                log.warn("敏感词库文件不存在: {}", WORD_FILE);
            } else {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    addWord(line);
                }
                reader.close();
                log.info("敏感词库加载完成，共 {} 个敏感词", WORD_TREE.size());
            }
        } catch (Exception e) {
            log.error("敏感词库加载失败", e);
        }
    }

    /**
     * 向字典树中添加单个敏感词
     *
     * @param word 敏感词
     */
    private static void addWord(String word) {
        if (word == null || word.isEmpty()) {
            return;
        }
        Map<Character, Map> currentNode = WORD_TREE;
        for (char c : word.toCharArray()) {
            currentNode = currentNode.computeIfAbsent(c, k -> new HashMap<>());
        }
        currentNode.put(END_FLAG.charAt(0), new HashMap<>());
    }

    /**
     * 检测文本中是否包含敏感词
     *
     * @param text 待检测文本
     * @return true-包含敏感词, false-不包含
     */
    public static boolean containsSensitiveWord(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            int length = checkSensitiveWord(text, i);
            if (length > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文本中所有敏感词
     *
     * @param text 待检测文本
     * @return 敏感词集合
     */
    public static Set<String> getSensitiveWords(String text) {
        Set<String> sensitiveWords = new HashSet<>();
        if (text == null || text.isEmpty()) {
            return sensitiveWords;
        }
        for (int i = 0; i < text.length(); i++) {
            int length = checkSensitiveWord(text, i);
            if (length > 0) {
                sensitiveWords.add(text.substring(i, i + length));
                i = i + length - 1;
            }
        }
        return sensitiveWords;
    }

    /**
     * 替换文本中的敏感词
     *
     * @param text 待处理文本
     * @return 替换后的文本
     */
    public static String replaceSensitiveWords(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        StringBuilder result = new StringBuilder(text);
        List<int[]> positions = new ArrayList<>();

        // 收集所有敏感词位置
        for (int i = 0; i < text.length(); i++) {
            int length = checkSensitiveWord(text, i);
            if (length > 0) {
                positions.add(new int[]{i, i + length});
                i = i + length - 1;
            }
            log.info("了、 {}",length);
            log.info("I {}",i);
        }

        // 从后向前替换，避免位置偏移
        for (int i = positions.size() - 1; i >= 0; i--) {
            int[] pos = positions.get(i);
            result.replace(pos[0], pos[1], REPLACEMENT);
        }
        log.info("result {}",result);

        return result.toString();
    }

    /**
     * 检查从指定位置开始的敏感词长度
     *
     * @param text  待检测文本
     * @param begin 开始位置
     * @return 敏感词长度，0表示不是敏感词
     */
    private static int checkSensitiveWord(String text, int begin) {
        if (WORD_TREE.isEmpty()) {
            return 0;
        }

        Map<Character, Map> currentNode = WORD_TREE;
        int length = 0;
        int maxLength = 0;

        for (int i = begin; i < text.length(); i++) {
            char c = text.charAt(i);
            currentNode = currentNode.get(c);
            if (currentNode == null) {
                break;
            }
            length++;
            // 检查是否是结束节点
            if (currentNode.containsKey(END_FLAG.charAt(0))) {
                maxLength = length;
            }
        }

        return maxLength;
    }
}
