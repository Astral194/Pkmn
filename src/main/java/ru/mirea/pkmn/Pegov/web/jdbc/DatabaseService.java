package ru.mirea.pkmn.Pegov.web.jdbc;

import ru.mirea.pkmn.Card;
import ru.mirea.pkmn.Student;

public interface DatabaseService {
    Card getCardFromDatabase(String cardName);

    Student getStudentFromDatabase(String studentName);

    void saveCardToDatabase(Card card);

    void createPokemonOwner(Student owner);
}
