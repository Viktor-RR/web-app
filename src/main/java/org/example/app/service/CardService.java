package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.Card;
import org.example.app.domain.User;
import org.example.app.dto.TransferRequestDto;
import org.example.app.exception.CardNotFoundException;
import org.example.app.repository.CardRepository;
import org.example.framework.security.Roles;
import org.example.jdbc.DataAccessException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CardService {

  public static final String NO_PERMISSION = "No permission to operation";
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


  public void moneyTransfer(TransferRequestDto requestDto, User user, Collection<String> auth) {
    if (auth.contains(Roles.ROLE_USER)) {
      final var ownerCard = cardRepository.getCardByNumber(requestDto.getOwnerCardNumber()).orElseThrow(CardNotFoundException::new);
      final var companionCard = cardRepository.getCardByNumber(requestDto.getCompanionCardNumber()).orElseThrow(CardNotFoundException::new);
      if (ownerCard.getBalance() >= requestDto.getMoneyValue() && requestDto.getMoneyValue() > 0 && ownerCard.getOwnerId() == user.getId()) {
        cardRepository.cashFromCardToAnotherCard(ownerCard.getNumber(), companionCard.getNumber(), requestDto.getMoneyValue());
      } else {
        throw new CardNotFoundException();
      }
    } else {
      throw new DataAccessException(NO_PERMISSION);
    }
  }

}
