package org.example.market.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.market.domain.Transaction.TransactionStatus;

@Data
@AllArgsConstructor
public class TransactionCompleteResponse {
    Long id;
    Long buyerId;
    Long quantity;
    TransactionStatus status;
}
