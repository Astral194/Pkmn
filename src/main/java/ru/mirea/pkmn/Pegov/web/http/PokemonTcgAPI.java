package ru.mirea.pkmn.Pegov.web.http;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import com.fasterxml.jackson.databind.JsonNode;

public interface PokemonTcgAPI {
    @GET("/v2/cards")
    Call<JsonNode> getPokemon(@Query(value = "q", encoded = true) String query);
}
