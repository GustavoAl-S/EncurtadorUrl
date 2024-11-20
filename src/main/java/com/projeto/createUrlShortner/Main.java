package com.projeto.createUrlShortner;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.ObjectStreamException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main implements RequestHandler<Map<String, Object>, Map<String, String>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, String> handleRequest(Map<String, Object> input, Context context) {

        //Lambda vai repassar para o input e desse input vamos extrair o body
        // O body veio como objeto, então transformamos ele para String
        String body = (String) input.get("body");

        /* Tentando trasnformar a String em Map desse formato:

            {
               "originalUrl": "https://exemplo.com.br"
            }

            Nesse formato a chave é uma String e um valor que uma String
        */

        Map<String, String> bodyMap;
        try {
            //Recebe o body que pegamos do input e a Map.class para ele transformar o body em um Map
            bodyMap = objectMapper.readValue(body, Map.class);
        } catch (Exception exception){
            //Caso tenha algum problema lançamos uma exceçao
            throw new RuntimeException("Error parsing JSON body: "+ exception.getMessage(), exception);
        }

        //Vai extrair os campos do bodyMap
        String originalUrl = bodyMap.get("originalUrl");

        //Vai extrair o tempo de expiraçao da url
        String expirationTime = bodyMap.get("expirationTime");

        //Gerar um UUID
        String shortUrlCode = UUID.randomUUID().toString().substring(0, 8);

        //Montando um objeto de resposta
        Map<String , String> response = new HashMap<>();
        response.put("code", shortUrlCode);

        return response;
    }
}