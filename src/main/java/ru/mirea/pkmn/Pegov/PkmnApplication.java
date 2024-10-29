package ru.mirea.pkmn.Pegov;

import com.fasterxml.jackson.databind.JsonNode;
import ru.mirea.pkmn.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PkmnApplication {
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        Card card1 = CardImport.readCard("src/main/resources/my_card.txt");
        System.out.println(card1.toString());

        CardExport.cardSerialization(card1);

        Card card = CardImport.cardImportByte("src/main/resources/Pangoro.crd");
        System.out.println(card.toString());

    }
}



