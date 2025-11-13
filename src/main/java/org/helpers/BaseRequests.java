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
    private static final String USERNAME = getUsername();

    /**
     * Пароль для WordPress.
     */
    private static final String PASSWORD = getPassword();

    /**
     * Переменная окружения из Jenkins для доступа к логину WordPress.
     */
    private static final String JENKINS_API_USERNAME = "WORDPRESS_USERNAME";

    /**
     * Переменная окружения из Jenkins для доступа к паролю WordPress.
     */
    private static final String JENKINS_API_PASSWORD = "WORDPRESS_PASSWORD";

    /**
     * Метод, возвращающий логин WordPress
     * @return переменная окружения из Jenkins, если она присутствует
     * в противном случае логин из .properties файла
     */
    private static String getUsername() {

        String envUsername = System.getenv(JENKINS_API_USERNAME);
        if (envUsername != null && !envUsername.trim().isEmpty()) {
            return envUsername;
        }
        return PropertyProvider.getInstance().getProperty("api.username");
    }

    /**
     * Метод, возвращающий пароль WordPress
     * @return переменная окружения из Jenkins, если она присутствует
     * в противном случае пароль из .properties файла
     */
    private static String getPassword() {

        String envPassword = System.getenv(JENKINS_API_PASSWORD);
        if (envPassword != null && !envPassword.trim().isEmpty()) {
            return envPassword;
        }
        return PropertyProvider.getInstance().getProperty("api.password");
    }

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
     * @return id записи
     */
    public static String createPost(final List<String> postsId,
                                  final Post postPojo) {

        // Ожидаемое содержание записи в ответе
        String contentExpected = "<p>" + postPojo.getContent() + "</p>\n";

        String postId = given()
                .spec(requestSpecification)
                .auth().preemptive().basic(USERNAME, PASSWORD)
                .body(postPojo)
                .when()
                    .post(API_POSTS)
                .then()
                    .statusCode(201)
                    .body("status", equalTo(postPojo.getStatus()))
                    .body("title.rendered", equalTo(postPojo.getTitle()))
                    .body("content.rendered", equalTo(contentExpected))
                .extract()
                    .jsonPath().getString("id");

        postsId.add(postId);
        return postId;
    }

    /**
     * Обновление записи.
     * @param postId id записи
     * @param postPojo экземпляр создаваемой записи
     */
    public static void putPost(final String postId, final Post postPojo) {

        // Ожидаемое содержание записи в ответе
        String contentExpected = "<p>" + postPojo.getContent() + "</p>\n";

        given()
            .spec(requestSpecification)
            .auth().preemptive().basic(USERNAME, PASSWORD)
            .body(postPojo)
            .pathParam("id", postId)
            .when()
                .put(API_POSTS + "/{id}")
            .then()
                .statusCode(200)
                .body("status", equalTo(postPojo.getStatus()))
                .body("title.rendered", equalTo(postPojo.getTitle()))
                .body("content.rendered", equalTo(contentExpected));
    }

    /**
     * Изменение записи.
     * @param postId id записи
     * @param postPojo экземпляр создаваемой записи
     */
    public static void patchPost(final String postId, final Post postPojo) {

        // Ожидаемое содержание записи в ответе
        String contentExpected = "<p>" + postPojo.getContent() + "</p>\n";

        given()
            .spec(requestSpecification)
            .auth().preemptive().basic(USERNAME, PASSWORD)
            .body(postPojo)
            .pathParam("id", postId)
            .when()
                .patch(API_POSTS + "/{id}")
            .then()
                .statusCode(200)
                .body("status", equalTo(postPojo.getStatus()))
                .body("title.rendered", equalTo(postPojo.getTitle()))
                .body("content.rendered", equalTo(contentExpected));
    }

    /**
     * Получение сущности по id.
     * @param postId id записи
     * @param statusCode ожидаемый статус-код
     * @return экземпляр записи
     */
    public static Post getPostById(final String postId, final int statusCode) {
        Response response = given()
                .spec(requestSpecification)
                .pathParam("id", postId)
                .when()
                    .get(API_POSTS + "/{id}")
                .then()
                    .statusCode(statusCode)
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
