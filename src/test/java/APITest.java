import org.helpers.BaseRequests;
import org.pojo.*;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс тестов API для WordPress.
 */
public class APITest {

    @BeforeClass
    public void setup() {
        BaseRequests.initRequestSpecification();
    }

    /**
     * Список ID записей.
     */
    private final List<String> postsId = new ArrayList<>();

    /**
     * Тест создания, изменения, обновления и удаления записи.
     */
    @Test(description = "Posts API test", priority = 1)
    public void testCreatePost() {
        // Создание pojo для запроса
        Post postPojo = Post.builder()
                .status("publish")
                .title("New post")
                .content("sample text")
                .build();

        // Тест-кейс 1. Создание записи
        BaseRequests.createPost(postsId, postPojo);

        Post post = BaseRequests.getPostById(postsId.get(0), 200);

        SoftAssert softAssertion = new SoftAssert();
        softAssertion.assertEquals(post.getStatus(), postPojo.getStatus(), "Статус записи не совпадает");
        softAssertion.assertEquals(post.getTitle(), postPojo.getTitle(), "Заголовок записи не совпадает");
        String contentExpected = "<p>" + postPojo.getContent() + "</p>\n";
        softAssertion.assertEquals(post.getContent(), contentExpected,"Содержание записи не совпадает");
        softAssertion.assertAll();

        // Тест-кейс 2. Обновление записи
        postPojo.setTitle("New new post");

        BaseRequests.putPost(postsId.get(0), postPojo);

        post = BaseRequests.getPostById(postsId.get(0), 200);

        softAssertion = new SoftAssert();
        softAssertion.assertEquals(post.getStatus(), postPojo.getStatus(), "Статус записи не совпадает");
        softAssertion.assertEquals(post.getTitle(), postPojo.getTitle(), "Заголовок записи не совпадает");
        contentExpected = "<p>" + postPojo.getContent() + "</p>\n";
        softAssertion.assertEquals(post.getContent(), contentExpected,"Содержание записи не совпадает");
        softAssertion.assertAll();

        // Тест-кейс 3. Изменение записи
        postPojo.setTitle("New new new post");

        BaseRequests.patchPost(postsId.get(0), postPojo);

        post = BaseRequests.getPostById(postsId.get(0), 200);

        softAssertion = new SoftAssert();
        softAssertion.assertEquals(post.getStatus(), postPojo.getStatus(), "Статус записи не совпадает");
        softAssertion.assertEquals(post.getTitle(), postPojo.getTitle(), "Заголовок записи не совпадает");
        contentExpected = "<p>" + postPojo.getContent() + "</p>\n";
        softAssertion.assertEquals(post.getContent(), contentExpected,"Содержание записи не совпадает");
        softAssertion.assertAll();

        // Тест-кейс 4. Удаление записи
        BaseRequests.deletePostsById(postsId);

        post = BaseRequests.getPostById(postsId.get(0), 404);

        Assert.assertNull(post.getStatus(), "Запись не удалилась");
    }
}
