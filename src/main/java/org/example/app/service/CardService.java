package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.Card;
import org.example.app.repository.CardRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CardService {
  private final CardRepository cardRepository;

  public List<Card> getAllByOwnerId(long ownerId) {
    return cardRepository.getAllCardsByOwnerId(ownerId);
  }

  public Optional<Card> getCardById(long ownerId, long cardId) {
    return cardRepository.getCardById(ownerId, cardId);
  }

  public void deleteCardById(long ownerId, long cardId) {
    cardRepository.deleteCardById(ownerId,cardId);
  }

  public void createCard(long ownerId, String vipNumber) {
    cardRepository.createNewCard(ownerId, vipNumber);
  }

  public void moneyTransfer(int moneyValue, long ownerId, long cardId, long companionId, long companionCardId) {
    cardRepository.cashFromCardToAnotherCard(moneyValue,ownerId, cardId,companionId,companionCardId);
  }
}
