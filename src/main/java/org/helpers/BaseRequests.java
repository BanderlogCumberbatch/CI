package org.helpers;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.List;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.hamcrest.core.IsEqual.equalTo;
import org.pojo.Post;

/**
 * Базовый класс спецификации запроса.
 */
public final class BaseRequests {

    private BaseRequests() { }

    /**
     * URL WordPress.
     */
    private static final String API_URL = "http://localhost:8000/";

    /**
     * API для взаимодействия с записями.
     */
    private static final String API_POSTS = "index.php?rest_route=/wp/v2/posts";

    /**
     * Логин для WordPress.
     */
    private static final String USERNAME = PropertyProvider.getInstance()
            .getProperty("api.username");

    /**
     * Пароль для WordPress.
     */
    private static final String PASSWORD = PropertyProvider.getInstance()
            .getProperty("api.password");

    /**
     * Подготовка спецификации запроса.
     */
    public static void initRequestSpecification() {

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder
                .setContentType(ContentType.JSON)
                .setBaseUri(API_URL)
                .setAccept(ContentType.JSON);
        requestSpecification = requestSpecBuilder.build();
    }

    /**
     * Создание сущности.
     * @param postsId список id
     * @param postPojo экземпляр создаваемой записи
     * @param status Ожидаемый статус записи
     * @param title Ожидаемый заголовок записи
     * @param content Ожидаемое содержимое записи
     */
    public static void createPost(final List<String> postsId,
                                  final Post postPojo,
                                  final String status,
                                  final String title,
                                  final String content) {

        String postId = given()
                .spec(requestSpecification)
                .auth().preemptive().basic(USERNAME, PASSWORD)
                .body(postPojo)
                .when()
                    .post(API_POSTS)
                .then()
                    .statusCode(201)
                    .body("status", equalTo(status))
                    .body("title.rendered", equalTo(title))
                    .body("content.rendered", equalTo(content))
                .extract()
                    .jsonPath().getString("id");

        postsId.add(postId);
    }

    /**
     * Получение сущности по id.
     * @param postId id записи
     * @return экземпляр записи
     */
    public static Post getPostById(final String postId) {
        Response response = given()
                .spec(requestSpecification)
                .pathParam("id", postId)
                .when()
                    .get(API_POSTS + "/{id}")
                .then()
                    .statusCode(200)
                    .extract().response();

        return Post.builder()
                .status(response.jsonPath().getString("status"))
                .title(response.jsonPath().getString("title.rendered"))
                .content(response.jsonPath().getString("content.rendered"))
                .build();
    }

    /**
     * Удаление списка сущностей id.
     * @param postsId список id удаляемых записей
     */
    public static void deletePostsById(final List<String> postsId) {

        for (String postId : postsId) {
            given()
                    .spec(requestSpecification)
                    .auth().preemptive().basic(USERNAME, PASSWORD)
                    .pathParam("id", postId)
                    .when()
                        .delete(API_POSTS + "/{id}&force=true")
                    .then()
                        .statusCode(200);
        }
    }

}
