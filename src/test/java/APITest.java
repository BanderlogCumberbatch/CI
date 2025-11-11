import org.helpers.BaseRequests;
import org.pojo.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.requestSpecification;

public class APITest {

    final String status = "publish";
    final String title = "New post";
    final String content = "sample text";
    final String contentExpected = "<p>" + content + "</p>\n";

    @BeforeClass
    public void setup() {
        requestSpecification = BaseRequests.initRequestSpecification();
    }

    /**
     * Список ID сущностей.
     */
    private final List<String> entitiesId = new ArrayList<>();

    /**
     * Тест создания сущности.
     */
    @Test(description = "Create post API test", priority = 1)
    public void testCreatePost() {
        Post postPojo = Post.builder()
                .status(status)
                .title(title)
                .content(content)
                .build();

        BaseRequests.createPost(entitiesId, postPojo, status, title, contentExpected);

        Post post = BaseRequests.getPostById(entitiesId.get(0));

        SoftAssert softAssertion = new SoftAssert();
        softAssertion.assertEquals(post.getStatus(), status, "Статус записи не совпадает");
        softAssertion.assertEquals(post.getTitle(), title, "Заголовок записи не совпадает");
        softAssertion.assertEquals(post.getContent(), contentExpected,"Содержание записи не совпадает");
        softAssertion.assertAll();
    }

}
