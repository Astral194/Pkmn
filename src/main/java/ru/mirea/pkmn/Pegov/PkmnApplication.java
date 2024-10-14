package ru.mirea.pkmn.Pegov;

import ru.mirea.pkmn.Card;

public class PkmnApplication {
    public static void main(String[] args) throws ClassNotFoundException {
        Card card = CardImport.readCard("src/main/resources/my_card.txt");
        System.out.println(card.toString());
        CardExport.cardSerialization(card);

        Card card1 = CardImport.cardImportByte("src/main/resources/Croagunk.crd");
        System.out.println(card1.toString());
    }
}

