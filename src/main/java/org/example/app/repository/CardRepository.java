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

  private final RowMapper<Card> cardOwnerRowMapper = resultSet -> new Card(
          resultSet.getLong("id"),
          resultSet.getString("number"),
          resultSet.getLong("balance"),
          resultSet.getLong("ownerId")
  );

  public List<Card> getAllCardsByOwnerId(long ownerId) {
    // language=PostgreSQL
    return jdbcTemplate.queryAll(
            "SELECT id, number, balance FROM cards WHERE \"ownerId\" = ? AND active = TRUE",
            cardRowMapper,
            ownerId
    );
  }

  public Optional<Card> getCardByNumber(String cardNumber) {
    // language=PostgreSQL
    return jdbcTemplate.queryOne(
            "SELECT id, number, balance, \"ownerId\" FROM cards WHERE number = ?",
            cardOwnerRowMapper,
            cardNumber
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


  public void cashFromCardToAnotherCard(String ownerCard, String companionCard, long moneyValue) {
    // language=PostgreSQL
    jdbcTemplate.update(
            "UPDATE cards SET balance = balance - ? WHERE number = ?",
            moneyValue,
            ownerCard
    );
    cashToCardFromAnotherCard(moneyValue, companionCard);
  }

  private void cashToCardFromAnotherCard(long moneyValue, String companionCard) {
    // language=PostgreSQL
    jdbcTemplate.update("UPDATE cards SET balance = balance + ? WHERE number = ?",
            moneyValue,
            companionCard
    );
  }

  public Optional<Card> getCardById(long ownerId, long cardId) {
    // language=PostgreSQL
    return jdbcTemplate.queryOne("SELECT id, number, balance, \"ownerId\"  FROM cards WHERE id = ? AND \"ownerId\" = ?",
            cardOwnerRowMapper, cardId, ownerId);
  }
}
