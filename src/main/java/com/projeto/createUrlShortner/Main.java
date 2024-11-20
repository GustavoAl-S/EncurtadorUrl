package com.projeto.createUrlShortner;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


import java.io.ObjectStreamException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main implements RequestHandler<Map<String, Object>, Map<String, String>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    //Criaçao dessa dependencia para estabelecer conexao com o S3
    private final S3Client s3Client = S3Client.builder().build();

    @Override
    public Map<String, String> handleRequest(Map<String, Object> input, Context context) {

        // Lambda vai repassar para o input e desse input vamos extrair o body
        // O body veio como objeto, então transformamos ele para String com o "(String)"
        String body = (String) input.get("body");

        /* Tentando transformar a String em Map desse formato:

            {
               "originalUrl": "https://exemplo.com.br"
            }

            Nesse formato a chave é uma String e um Valor é uma String
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
        // Var para transformar expirationTime em Long
        long expirationTimeInSeconds = Long.parseLong(expirationTime);

        //Gerar um UUID para a Url e oo expirationTime
        String shortUrlCode = UUID.randomUUID().toString().substring(0, 8);

        // Objeto para empacotar a "Url" e "expirationTime"
        UrlData urlData = new UrlData(originalUrl, expirationTimeInSeconds);

        //Bloco de TryCatch para fazer a conexao  com o S3 e salvar o obj Json
        try {
            String urlDataJson = objectMapper.writeValueAsString(urlData);//Transformando obj em Json

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket("url-shortener-bucket-project")//indicou qual é o Bucket
                    .key(shortUrlCode + ".json")//Deu um nome pro arquivo
                    .build();

            s3Client.putObject(request, RequestBody.fromString(urlDataJson));// Isso está passando o conteudo do arquivo
        } catch (Exception exception) {
            throw new RuntimeException("Error saving Url data to S3: " + exception.getMessage(), exception);
        }

        //Montando um objeto de resposta
        Map<String , String> response = new HashMap<>();
        response.put("code", shortUrlCode);

        return response;
    }
}