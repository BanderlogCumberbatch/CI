package org.helpers;

import io.restassured.RestAssured;
import io.restassured.authentication.BasicAuthScheme;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import org.pojo.Post;

/**
 * Базовый класс спецификации запроса.
 */
public class BaseRequests {

    private static final String apiUrl = "http://localhost:8000/";
    private static final String apiCreate = "index.php?rest_route=/wp/v2/posts";
    private static final String username = PropertyProvider.getInstance().getProperty("api.username");
    private static final String password = PropertyProvider.getInstance().getProperty("api.password");

    /**
     * Подготовка спецификации запроса
     * @return спецификация
     */
    public static RequestSpecification initRequestSpecification() {
        BasicAuthScheme basicAuthScheme = new BasicAuthScheme();
        basicAuthScheme.setUserName(PropertyProvider.getInstance().getProperty("api.username"));
        basicAuthScheme.setPassword(PropertyProvider.getInstance().getProperty("api.password"));

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder
                .setContentType(ContentType.JSON)
                .setBaseUri(apiUrl)
                .setAccept(ContentType.JSON)
                .setAuth(basicAuthScheme);
        return requestSpecification = requestSpecBuilder.build();
    }

    /**
     * Создание сущности
     *
     * @param entitiesId список id сущностей
     * @param postPojo экземпляр создаваемой записи
     */
    public static void createPost(List<String> entitiesId, Post postPojo) {

        entitiesId.add(given()
                .spec(requestSpecification)
                        .auth().preemptive().basic(username, password)
                .body(postPojo)
                .when()
                .post(apiCreate)
                .then()
                .statusCode(201)
                .extract().asString());
    }

}
