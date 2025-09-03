package com.jiji.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 词性验证工具类
 * 包含数据库中真实存在的所有词性类型
 */
public class PartOfSpeechValidator {
    
    // 所有有效的词性类型
    private static final Set<String> VALID_POS_TYPES = new HashSet<>(Arrays.asList(
        "v.",           // 动词
        "n.",           // 名词
        "vt.",          // 及物动词
        "adj.",         // 形容词
        "phrase.",      // 短语
        "adv.",         // 副词
        "vt.& vi.",     // 及物和不及物动词
        "vi.",          // 不及物动词
        "num.",         // 数词
        "exclam.",      // 感叹词
        "prep.",        // 介词
        "modal.",       // 情态动词
        "abbr.",        // 缩写
        "conj.",        // 连词
        "pron.",        // 代词
        "inf.",         // 不定式
        "aux.",         // 助动词
        "det.",         // 限定词
        "n.& adv.",     // 名词和副词
        "art."          // 冠词
    ));
    
    /**
     * 验证词性是否有效
     * @param pos 词性字符串
     * @return 如果有效返回true，否则返回false
     */
    public static boolean isValid(String pos) {
        if (pos == null || pos.trim().isEmpty()) {
            return false;
        }
        return VALID_POS_TYPES.contains(pos.trim());
    }
    
    /**
     * 获取所有有效的词性类型
     * @return 有效词性类型的集合
     */
    public static Set<String> getValidPosTypes() {
        return new HashSet<>(VALID_POS_TYPES);
    }
    
    /**
     * 获取词性的中文描述
     * @param pos 词性字符串
     * @return 中文描述，如果未找到则返回原字符串
     */
    public static String getChineseDescription(String pos) {
        if (pos == null) return "";
        
        switch (pos.trim()) {
            case "v.": return "动词";
            case "n.": return "名词";
            case "vt.": return "及物动词";
            case "adj.": return "形容词";
            case "phrase.": return "短语";
            case "adv.": return "副词";
            case "vt.& vi.": return "及物和不及物动词";
            case "vi.": return "不及物动词";
            case "num.": return "数词";
            case "exclam.": return "感叹词";
            case "prep.": return "介词";
            case "modal.": return "情态动词";
            case "abbr.": return "缩写";
            case "conj.": return "连词";
            case "pron.": return "代词";
            case "inf.": return "不定式";
            case "aux.": return "助动词";
            case "det.": return "限定词";
            case "n.& adv.": return "名词和副词";
            case "art.": return "冠词";
            default: return pos;
        }
    }
    
    /**
     * 获取所有词性的中文描述映射
     * @return 词性到中文描述的映射
     */
    public static java.util.Map<String, String> getPosChineseMap() {
        java.util.Map<String, String> map = new java.util.HashMap<>();
        for (String pos : VALID_POS_TYPES) {
            map.put(pos, getChineseDescription(pos));
        }
        return map;
    }
}
