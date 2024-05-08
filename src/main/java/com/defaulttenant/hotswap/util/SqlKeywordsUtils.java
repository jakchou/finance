package com.defaulttenant.hotswap.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * check sql injection
 */
public class SqlKeywordsUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlKeywordsUtils.class);
    private static final String PATTERN = "( +)";
    private static final Map<String, List<String>> DATABASE_KEYWORDS_MAP = new HashMap<>();

    static {
        loadKeywords("MYSQL", "/sqlconfig/mysql.txt");
    }

    private SqlKeywordsUtils() {}

    private static void loadKeywords(String databaseType, String fileName) {
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(SqlKeywordsUtils.class.getResourceAsStream(fileName)))) {
            List<String> result = new ArrayList<>();
            String str;
            while ((str = reader.readLine()) != null) {
                result.add(str);
            }
            DATABASE_KEYWORDS_MAP.put(databaseType, result);
        } catch (Exception e) {
            LOGGER.error("get keywords failed, fileName [{}]", fileName, e);
        }
    }

    public static Set<String> getForbiddenKeyWords(String databaseType, List<String> params) {
        Set<String> sqlStringSplit = new HashSet<>();
        params.forEach(param -> sqlStringSplit.addAll(Arrays.asList(param.toLowerCase().split(PATTERN))));
        if (DATABASE_KEYWORDS_MAP.get(databaseType) != null) {
            sqlStringSplit.retainAll(DATABASE_KEYWORDS_MAP.get(databaseType));
        }
        return sqlStringSplit;
    }
}
