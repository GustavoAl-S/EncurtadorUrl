package com.projeto.createUrlShortner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// Usando as anotaçoes da dependencia Lombok, para deixar o codigo menor
@AllArgsConstructor
@Getter
@Setter
public class UrlData {
    private String originalUrl;
    private long expirationTime;

}
