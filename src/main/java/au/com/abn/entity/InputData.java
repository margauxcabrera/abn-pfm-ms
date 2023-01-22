package au.com.abn.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InputData {
    private String recordCode;
    private String clientType;
    private String clientNumber;
    private String accountNumber;
    private String subAccountNumber;
    private String oppositePartyCode;
    private String productGroupCode;
    private String exchangeCode;
    private String symbol;
    private String expirationDate;
    private String currencyCode;
    private String movementCode;
    private String buySellCode;
    private String quantityLongSign;
    private String quantityLong;
    private String quantityShortSign;
    private String quantityShort;
    private String brokerFeeDec;
    private String brokerFeeDc;
    private String brokerFeeCurCode;
    private String clearingFeeDec;
    private String clearingFeeDc;
    private String clearingFeeCurCode;
    private String commission;
    private String commissionDc;
    private String commissionCurCode;
    private String transactionDate;
    private String futureReference;
    private String ticketNumber;
    private String externalNumber;
    private String transactionPrice;
    private String traderInitials;
    private String oppositionTraderID;
    private String openCloseCode;
}
