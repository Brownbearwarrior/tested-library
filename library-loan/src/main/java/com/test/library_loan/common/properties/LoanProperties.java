package com.test.library_loan.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "loan.properties")
public record LoanProperties(int dueDays, int loans) {
}
