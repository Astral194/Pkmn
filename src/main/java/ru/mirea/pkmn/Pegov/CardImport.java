package ru.mirea.pkmn.Pegov;

import com.fasterxml.jackson.databind.JsonNode;
import ru.mirea.pkmn.*;
import ru.mirea.pkmn.Pegov.web.http.PkmnHttpClient;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CardImport {

    public static Card readCard(String path)
    {
        Card card = new Card();
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            List<String> data = new ArrayList<>();
            while ((line = reader.readLine()) != null)
            {
                data.add(line);
            }

            for (int i = 0; i < 13; i ++)
            {
                switch (i) {
                    case 0 -> card.setPokemonStage(PokemonStage.valueOf(data.get(0)));
                    case 1 -> card.setName(data.get(1));
                    case 2 -> card.setNumber(data.get(2));
                    case 3 -> card.setHp(Integer.parseInt(data.get(3)));
                    case 4 -> card.setPokemonType(EnergyType.valueOf(data.get(4).toUpperCase()));
                    case 5 ->
                            card.setEvolvesFrom((data.get(5).equalsIgnoreCase("None") || data.get(5).equalsIgnoreCase("-")) ? null : readCard(data.get(5)));
                    case 6 -> {card.setSkills(getAttackskils(data.get(6), card));break;}
                    case 7 ->
                            card.setWeaknessType((data.get(7).equalsIgnoreCase("None") || data.get(7).equalsIgnoreCase("-")) ? null : EnergyType.valueOf(data.get(7).toUpperCase()));
                    case 8 ->
                            card.setResistanceType((data.get(8).equalsIgnoreCase("None") || data.get(8).equalsIgnoreCase("-")) ? null : EnergyType.valueOf(data.get(8).toUpperCase()));
                    case 9 -> card.setRetreatCost(data.get(9));
                    case 10 -> card.setGameSet(data.get(10));
                    case 11 -> card.setRegulationMark(data.get(11).charAt(0));
                    case 12 -> card.setPokemonOwner(getStudent(data.get(12)));
                }
            }
        }
        catch (FileNotFoundException e){
            throw new RuntimeException("Файл карты не найден");
        }
        catch (ArrayIndexOutOfBoundsException EnumConstantNotPresentException) {
            throw new RuntimeException("Неправильный формат файла карты");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return card;
    }

    public static  List<AttackSkill> getAttackskils(String attacks, Card pokemon) throws IOException {
        ArrayList<AttackSkill> skill = new ArrayList<>();
        String[] attack_skill = attacks.split(",");
        List<String> desc_of_attack= getDesc(pokemon);
        int i = 0;
        for (String attack : attack_skill) {
            String[] tmp = attack.split(" / ");
            AttackSkill as = new AttackSkill(tmp[1], desc_of_attack.get(i++) , tmp[0], Integer.parseInt(tmp[2]));
            skill.add(as);
        }
        return skill;
    }

    public static List<String> getDesc(Card card1) throws IOException {
        PkmnHttpClient pkmnHttpClient = new PkmnHttpClient();
        JsonNode cardjson = pkmnHttpClient.getPokemonCard(card1.getName(), card1.getNumber());
        List<JsonNode> attack = cardjson.findValues("attacks");
        List<String> desc_of_attack = new ArrayList<>();
        for (JsonNode att : attack){
            for (JsonNode attacks : att){
                desc_of_attack.add(attacks.get("text").toString());
            }
        }
        return desc_of_attack;
    }

    private static Student getStudent(String s){
        if (s.equalsIgnoreCase("none")) return new Student();

        String[] info = s.split(" / ");
        Student student = new Student(info[0], info[1], info[2], info[3]);
        return student;
    }

    public static Card cardImportByte(String path) throws ClassNotFoundException {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
            return (Card) in.readObject();
        }catch (IOException e){
            throw new RuntimeException("Путь до файла не найден");
        }
    }
}