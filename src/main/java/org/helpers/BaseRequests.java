package org.helpers;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.hamcrest.core.IsEqual.equalTo;
import org.pojo.Post;

/**
 * Базовый класс спецификации запроса.
 */
public class BaseRequests {

    private static final String apiUrl = "http://localhost:8000/";
    private static final String apiPosts = "index.php?rest_route=/wp/v2/posts";
    private static final String username = PropertyProvider.getInstance().getProperty("api.username");
    private static final String password = PropertyProvider.getInstance().getProperty("api.password");

    /**
     * Подготовка спецификации запроса
     * @return спецификация
     */
    public static RequestSpecification initRequestSpecification() {

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder
                .setContentType(ContentType.JSON)
                .setBaseUri(apiUrl)
                .setAccept(ContentType.JSON);
        return requestSpecification = requestSpecBuilder.build();
    }

    /**
     * Создание сущности
     * @param entitiesId список id сущностей
     * @param postPojo экземпляр создаваемой записи
     */
    public static void createPost(List<String> entitiesId, Post postPojo, String status, String title, String content) {

        String postId = given()
                .spec(requestSpecification)
                .auth().preemptive().basic(username, password)
                .body(postPojo)
                .when()
                .post(apiPosts)
                .then()
                .statusCode(201)
                .body("status", equalTo(status))
                .body("title.rendered", equalTo(title))
                .body("content.rendered", equalTo(content))
                .extract()
                .jsonPath().getString("id");

        entitiesId.add(postId);
    }

    /**
     * Получение сущности по id
     * @param postId id сущности
     * @return экземпляр сущности
     */
    public static Post getPostById(String postId) {
        Response response = given()
                .spec(requestSpecification)
                .pathParam("id", postId)
                .when()
                .get(apiPosts + "/{id}")
                .then()
                .statusCode(200)
                .extract().response();

        return Post.builder()
                .status(response.jsonPath().getString("status"))
                .title(response.jsonPath().getString("title.rendered"))
                .content(response.jsonPath().getString("content.rendered"))
                .build();
    }

}