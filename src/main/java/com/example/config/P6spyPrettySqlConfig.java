package com.example.config;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.springframework.context.annotation.Configuration;

@Configuration
public class P6spyPrettySqlConfig implements MessageFormattingStrategy {
    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        sql = formatSql(category, sql);
        // 여기서 Stack Trace를 추출해서 SQL 뒤에 붙여버립니다.
        return String.format("[%s] | %d ms | %s %s", category, elapsed, sql, getStack());
    }

    private String formatSql(String category, String sql) {
        if (sql == null || sql.trim().isEmpty()) return sql;
        if ("statement".equals(category)) {
            String tmpsql = sql.trim().toLowerCase();
            if (tmpsql.startsWith("create") || tmpsql.startsWith("alter") || tmpsql.startsWith("comment")) {
                return FormatStyle.DDL.getFormatter().format(sql);
            }
            return FormatStyle.BASIC.getFormatter().format(sql);
        }
        return sql;
    }

    private String getStack() {
        return java.util.Arrays.stream(new Throwable().getStackTrace())
                .filter(ste -> ste.getClassName().startsWith("com") // 프로젝트 시작 패키지
                        && !ste.getClassName().contains("p6spy")    // p6spy 제외
                        && !ste.getClassName().contains("Config")   // 설정 클래스 자신 제외
                        && !ste.getClassName().contains("Proxy")    // 프록시 객체 제외
                        && !ste.getClassName().contains("Hibernate")// 하이버네이트 제외
                )
                .findFirst() // 내 소스 코드 중 가장 최근 호출지점 추출
                .map(ste -> "\n\tCalled from: " + ste.toString())
                .orElse("");
    }
}