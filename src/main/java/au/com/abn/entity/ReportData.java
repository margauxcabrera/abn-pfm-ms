package au.com.abn.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReportData {
    private String clientInformation;
    private String productInformation;
    private BigDecimal totalTransactionAmount;
}
