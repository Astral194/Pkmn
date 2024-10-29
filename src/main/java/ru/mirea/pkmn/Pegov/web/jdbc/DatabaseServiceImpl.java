package ru.mirea.pkmn.Pegov.web.jdbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.mirea.pkmn.*;
import ru.mirea.pkmn.Pegov.CardImport;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.UUID;

public class DatabaseServiceImpl implements DatabaseService {

    private final Connection connection;

    private final Properties databaseProperties;

    public DatabaseServiceImpl() throws SQLException, IOException {

        // Загружаем файл database.properties

        databaseProperties = new Properties();
        databaseProperties.load(new FileInputStream("src/main/resources/database.properties"));

        // Подключаемся к базе данных

        connection = DriverManager.getConnection(
                databaseProperties.getProperty("database.url"),
                databaseProperties.getProperty("database.user"),
                databaseProperties.getProperty("database.password")
        );
        System.out.println("Connection is "+(connection.isValid(0) ? "up" : "down"));
    }

    @Override
    public Card getCardFromDatabase(String cardName) throws SQLException, JsonProcessingException {
        Card result = new Card();
        String query = String.format("SELECT * FROM card WHERE (name = '%s');", cardName);
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery(query);

        if(rs.next()){

            UUID evolves_from = (UUID) rs.getObject("evolves_from");
            result.setName(cardName);
            result.setEvolvesFrom(evolves_from == null ? null : getCardFromDatabase(evolves_from));
            result.setNumber(String.valueOf(Integer.parseInt(rs.getString("card_number"))));
            result.setHp(rs.getInt("hp"));
            result.setPokemonOwner(getStudentFromDatabase((UUID) rs.getObject("pokemon_owner")));
            result.setRegulationMark(rs.getString("regulation_mark").charAt(0));
            result.setWeaknessType(EnergyType.valueOf(rs.getString("weakness_type")));
            result.setGameSet(rs.getString("game_set"));
            String resist = rs.getString("resistance_type");
            result.setResistanceType(resist == null ? null : EnergyType.valueOf(resist));
            result.setPokemonStage(PokemonStage.valueOf(rs.getString("stage").toUpperCase()));
            result.setRetreatCost(rs.getString("retreat_cost"));
            result.setSkills(CardImport.parseAttackSkillsFromJson(rs.getString("attack_skills")));
        }
        else throw new RuntimeException("Empty result from database");
        return result;
    }


    @Override
    public Card getCardFromDatabase(UUID cardName) throws SQLException, JsonProcessingException {

        Card result = new Card();
        String query = String.format("SELECT * FROM card WHERE (id = '%s');", cardName);
        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery(query);

        if(rs.next()){
            result.setName(rs.getString("name"));
            UUID evolves_from = (UUID) rs.getObject("evolves_from");
            result.setEvolvesFrom(evolves_from == null ? null : getCardFromDatabase(evolves_from));
            result.setNumber(String.valueOf(Integer.parseInt(rs.getString("card_number"))));
            result.setHp(rs.getInt("hp"));
            result.setPokemonOwner(getStudentFromDatabase((UUID) rs.getObject("pokemon_owner")));
            result.setRegulationMark(rs.getString("regulation_mark").charAt(0));
            result.setWeaknessType(EnergyType.valueOf(rs.getString("weakness_type")));
            result.setGameSet(rs.getString("game_set"));
            String resist = rs.getString("resistance_type");
            result.setResistanceType(resist == null ? null : EnergyType.valueOf(resist));
            result.setPokemonStage(PokemonStage.valueOf(rs.getString("stage").toUpperCase()));
            result.setRetreatCost(rs.getString("retreat_cost"));
            result.setSkills(CardImport.parseAttackSkillsFromJson(rs.getString("attack_skills")));

        }
        else throw new RuntimeException("Empty result from database");
        return result;
    }

    @Override
    public Student getStudentFromDatabase(String studentName) throws SQLException {

        Student result = new Student();
        String[] studentFullName = studentName.split(" ");
        String query = String.format("SELECT * FROM student WHERE (\"familyName\" = '%s' AND \"firstName\" = '%s' AND \"patronicName\" = '%s');",
                studentFullName[0], studentFullName[1], studentFullName[2]);
        ResultSet rs = connection.createStatement().executeQuery(query);
        if(rs.next()){

            result.setFirstName(rs.getString("firstName"));
            result.setFamilyName(rs.getString("familyName"));
            result.setSurName(rs.getString("patronicName"));
            result.setGroup(rs.getString("group"));

        }
        return result;
    }


    @Override
    public Student getStudentFromDatabase(UUID studentName) throws SQLException {

        Student result = new Student();
        String query = String.format("SELECT * FROM student WHERE (id = '%s');", studentName);
        ResultSet rs = connection.createStatement().executeQuery(query);
        if(rs.next()){

            result.setFirstName(rs.getString("firstName"));
            result.setFamilyName(rs.getString("familyName"));
            result.setSurName(rs.getString("patronicName"));
            result.setGroup(rs.getString("group"));

        }
        else {
            throw new RuntimeException("Empty result from database");
        }
        return result;
    }

    @Override
    public void saveCardToDatabase(Card card) throws SQLException {
        StringBuilder queryBase = new StringBuilder("INSERT INTO card(");
        StringBuilder query = new StringBuilder("VALUES(");
        if (card.getEvolvesFrom() != null){
            queryBase.append("evolves_from, ");
            try {
                ResultSet rs = connection.createStatement().executeQuery(String.format("SELECT id FROM card WHERE (name = '%s');", card.getEvolvesFrom().getName()));
                rs.next();
                query.append("'").append(rs.getObject("id")).append("', ");
            } catch (SQLException e){
                saveCardToDatabase(card.getEvolvesFrom());
                try {
                    ResultSet rs = connection.createStatement().executeQuery(String.format("SELECT id FROM card WHERE (name = '%s');", card.getEvolvesFrom().getName()));
                    rs.next();
                    query.append("'").append(rs.getObject("id")).append("',");
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        if (card.getPokemonOwner() != null) {
            queryBase.append(" pokemon_owner,");
            try{
                String tmp = String.format("SELECT id FROM student WHERE (\"familyName\" = '%s' AND \"firstName\" = '%s' AND \"patronicName\" = '%s');",
                        card.getPokemonOwner().getFamilyName(), card.getPokemonOwner().getFirstName(), card.getPokemonOwner().getSurName());
                ResultSet rs = connection.createStatement().executeQuery(tmp);
                rs.next();
                query.append("'").append(rs.getObject("id")).append("', ");
            }catch (Exception e){
                query.append("'").append(createPokemonOwner(card.getPokemonOwner())).append("', ");
            }

        }
        queryBase.append(" id, name, hp, game_set, stage, retreat_cost, weakness_type, resistance_type, attack_skills, pokemon_type, regulation_mark, card_number) ");
        query.append("'").append(UUID.randomUUID()).append("', '");
        query.append(card.getName()).append("', ");
        query.append(card.getHp()).append(", '");
        query.append(card.getGameSet()).append("', '");
        query.append(card.getPokemonStage()).append("', '");
        query.append(card.getRetreatCost()).append("', '");
        query.append(card.getWeaknessType()).append("', '");
        query.append(card.getResistanceType()).append("', '");
        query.append("[");
        for (AttackSkill as : card.getSkills()){
            query.append(as.toString().replace('\'', '`')).append(", ");
        }
        query.delete(query.length()-2, query.length()-1);
        query.append("]").append("', '");
        query.append(card.getPokemonType()).append("', '");
        query.append(card.getRegulationMark()).append("', ");
        query.append(card.getNumber());
        query.append(");");

        System.out.println(queryBase.toString() + query.toString());

        connection.createStatement().executeUpdate(queryBase.toString() + query.toString());
    }

    @Override
    public UUID createPokemonOwner(Student owner) throws SQLException {
        UUID ownerId = UUID.randomUUID();

        String query = String.format("INSERT INTO student (\"id\", \"firstName\", \"familyName\", \"patronicName\", \"group\" ) " +
                        "VALUES ('%s', '%s', '%s', '%s', '%s');",
                ownerId, owner.getFirstName(), owner.getSurName(), owner.getFamilyName(), owner.getGroup());
        System.out.println(query);
        connection.createStatement().executeUpdate(query);
        return ownerId;
    }
}