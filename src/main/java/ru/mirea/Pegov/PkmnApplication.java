package ru.mirea.Pegov;

public class PkmnApplication {
    public static void main(String[] args) throws ClassNotFoundException {
        Card card = CardImport.readCard("src/main/resources/my_card.txt");
        System.out.println(card.toString());
        CardExport.cardSerialization(card);
    }
}

