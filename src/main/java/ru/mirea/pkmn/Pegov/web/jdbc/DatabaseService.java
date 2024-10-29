package ru.mirea.pkmn.Pegov.web.jdbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.mirea.pkmn.Card;
import ru.mirea.pkmn.Student;

import java.sql.SQLException;
import java.util.UUID;

public interface DatabaseService {
    Card getCardFromDatabase(String cardName) throws SQLException, JsonProcessingException;

    Card getCardFromDatabase(UUID cardName) throws SQLException, JsonProcessingException;

    Student getStudentFromDatabase(String studentName) throws SQLException;

    Student getStudentFromDatabase(UUID studentName) throws SQLException;

    void saveCardToDatabase(Card card) throws SQLException;

    UUID createPokemonOwner(Student owner) throws SQLException;
}
