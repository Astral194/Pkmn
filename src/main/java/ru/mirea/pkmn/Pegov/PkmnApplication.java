package ru.mirea.pkmn.Pegov;

import com.fasterxml.jackson.databind.JsonNode;
import ru.mirea.pkmn.*;
import ru.mirea.pkmn.Pegov.web.http.PkmnHttpClient;
import ru.mirea.pkmn.Pegov.web.jdbc.DatabaseServiceImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PkmnApplication {
    public static void main(String[] args) throws IOException, SQLException {
        DatabaseServiceImpl db = new DatabaseServiceImpl();

        Card myCard = CardImport.readCard("src/main/resources/my_card.txt");
        db.saveCardToDatabase(myCard);
    }
}