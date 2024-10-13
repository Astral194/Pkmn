package ru.mirea.Pegov;

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

            for (int i = 0; i < 12; i ++)
            {
                switch (i)
                {
                    case 0: card.setPokemonStage(PokemonStage.valueOf(data.get(0))); break;
                    case 1: card.setName(data.get(1)); break;
                    case 2: card.setHp(Integer.parseInt(data.get(2))); break;
                    case 3: card.setPokemonType(EnergyType.valueOf(data.get(3).toUpperCase())); break;
                    case 4: card.setEvolvesFrom(data.get(4).equalsIgnoreCase("None") ? null : readCard(data.get(4))); break;
                    case 5:
                    {
                        ArrayList<AttackSkill> skill = new ArrayList<>();
                        String[] attack_skill = data.get(5).split(",");
                        for(String attack : attack_skill){
                            String[] tmp = attack.split(" / ");
                            AttackSkill as = new AttackSkill(tmp[1], "", tmp[0], Integer.parseInt(tmp[2]));
                            skill.add(as);
                        }
                        card.setSkills(skill);
                        break;
                    }
                    case 6: card.setWeaknessType((data.get(6).equalsIgnoreCase("none")) ? null : EnergyType.valueOf(data.get(6).toUpperCase())); break;
                    case 7: card.setResistanceType((data.get(7).equalsIgnoreCase("none")) ? null : EnergyType.valueOf(data.get(7).toUpperCase())); break;
                    case 8: card.setRetreatCost(data.get(8)); break;
                    case 9: card.setGameSet(data.get(9)); break;
                    case 10: card.setRegulationMark(data.get(10).charAt(0)); break;
                    case 11: card.setPokemonOwner(getStudent(data.get(11))); break;
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

    private static Student getStudent(String s){
        if (s.equalsIgnoreCase("none")) return new Student();

        String[] info = s.split(" / ");
        Student student = new Student(info[0], info[1], info[2], info[3]);
        return student;
    }

    public static <RandomClass> Card cardImportByte(String path) throws ClassNotFoundException {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
            return (Card) in.readObject();
        }catch (IOException e){
            throw new RuntimeException("Путь до файла не найден");
        }
    }
}