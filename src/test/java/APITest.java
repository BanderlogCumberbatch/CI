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

    /**
     * Список ID записей.
     */
    private final List<String> postsId = new ArrayList<>();

    /**
     * Создание pojo для запроса.
     */
    private final Post postPojo = Post.builder()
            .status("publish")
            .title("New post")
            .content("sample text")
            .build();

    @BeforeClass
    public void setup() {
        BaseRequests.initRequestSpecification();
    }

    /**
     * Тест создания записи.
     */
    @Test(description = "Posts API test", priority = 1)
    public void testCreatePost() {
        String postId = BaseRequests.createPost(postsId, postPojo);

        Post post = BaseRequests.getPostById(postId, 200);

        SoftAssert softAssertion = new SoftAssert();
        softAssertion.assertEquals(post.getStatus(), postPojo.getStatus(), "Статус записи не совпадает");
        softAssertion.assertEquals(post.getTitle(), postPojo.getTitle(), "Заголовок записи не совпадает");
        String contentExpected = "<p>" + postPojo.getContent() + "</p>\n";
        softAssertion.assertEquals(post.getContent(), contentExpected,"Содержание записи не совпадает");
        softAssertion.assertAll();
    }

    /**
     * Тест изменения записи.
     */
    @Test(description = "Change post API test", priority = 2)
    public void testChangePost() {
        String postId = BaseRequests.createPost(postsId, postPojo);

        postPojo.setTitle("New new post");
        BaseRequests.patchPost(postId, postPojo);

        Post post = BaseRequests.getPostById(postId, 200);

        SoftAssert softAssertion = new SoftAssert();
        softAssertion.assertEquals(post.getStatus(), postPojo.getStatus(), "Статус записи не совпадает");
        softAssertion.assertEquals(post.getTitle(), postPojo.getTitle(), "Заголовок записи не совпадает");
        String contentExpected = postPojo.getContent();
        softAssertion.assertEquals(post.getContent(), contentExpected,"Содержание записи не совпадает");
        softAssertion.assertAll();
    }

    /**
     * Тест обновления записи.
     */
    @Test(description = "Update post API test", priority = 3)
    public void testUpdatePost() {
        String postId = BaseRequests.createPost(postsId, postPojo);

        postPojo.setTitle("New new new post");
        BaseRequests.putPost(postId, postPojo);

        Post post = BaseRequests.getPostById(postId, 200);

        SoftAssert softAssertion = new SoftAssert();
        softAssertion.assertEquals(post.getStatus(), postPojo.getStatus(), "Статус записи не совпадает");
        softAssertion.assertEquals(post.getTitle(), postPojo.getTitle(), "Заголовок записи не совпадает");
        String contentExpected = "<p>" + postPojo.getContent() + "</p>\n";
        softAssertion.assertEquals(post.getContent(), contentExpected,"Содержание записи не совпадает");
        softAssertion.assertAll();
    }

    /**
     * Тест удаления записи
     */
    @Test(description = "Delete post API test", priority = 4)
    public void testDeletePost() {
        String postId = BaseRequests.createPost(postsId, postPojo);

        BaseRequests.deletePostsById(postsId);

        Post post = BaseRequests.getPostById(postId, 404);

        Assert.assertNull(post.getStatus(), "Запись не удалилась");
    }
}
