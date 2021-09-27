package org.example.app.repository;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.Card;
import org.example.jdbc.JdbcTemplate;
import org.example.jdbc.RowMapper;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CardRepository {
  private final JdbcTemplate jdbcTemplate;
  private final RowMapper<Card> cardRowMapper = resultSet -> new Card(
      resultSet.getLong("id"),
      resultSet.getString("number"),
      resultSet.getLong("balance")
  );

  public List<Card> getAllCardsByOwnerId(long ownerId) {
    // language=PostgreSQL
    return jdbcTemplate.queryAll(
        "SELECT id, number, balance FROM cards WHERE \"ownerId\" = ? AND active = TRUE",
        cardRowMapper,
        ownerId
    );
  }

  public Optional<Card> getCardById(long ownerId, long id) {
    // language=PostgreSQL
    return jdbcTemplate.queryOne(
            "SELECT id, number, balance FROM cards WHERE \"ownerId\" = ? AND id = ?",
            cardRowMapper,
            ownerId,
            id
    );
  }


  public void deleteCardById(long ownerId, long cardId) {
    // language=PostgreSQL
    jdbcTemplate.update(
            "DELETE FROM cards WHERE \"ownerId\" = ? AND id = ?",
            ownerId,
            cardId
    );
  }

  public void createNewCard(long ownerId, String vipNumber) {
    // language=PostgreSQL
    jdbcTemplate.update("INSERT INTO cards (\"ownerId\", number) VALUES (?, ?)",
           ownerId,
            vipNumber
   );
  }


  public void cashFromCardToAnotherCard(int moneyValue,long ownerId,long cardId, long companionId, long companionCardId) {
    // language=PostgreSQL
    jdbcTemplate.update(
             "UPDATE cards SET balance = balance - ? WHERE \"ownerId\" = ? AND id = ?",
            moneyValue,
            ownerId,
            cardId
    );
    cashToCardFromAnotherCard(moneyValue,companionId,companionCardId);
  }

  private void cashToCardFromAnotherCard(int moneyValue,long ownerId,long companionCardId) {
    // language=PostgreSQL
    jdbcTemplate.update("UPDATE cards SET balance = balance + ? WHERE \"ownerId\" = ? AND id = ?",
            moneyValue,
            ownerId,
            companionCardId
    );
  }
}
