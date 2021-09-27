package org.example.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransferRequestDto {
    private int moneyValue;
    private int cardId;
    private long companionId;
    private long companionCardId;
}
