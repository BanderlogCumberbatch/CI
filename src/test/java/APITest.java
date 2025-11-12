import org.helpers.BaseRequests;
import org.pojo.*;
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
     * Статус создаваемой записи
     */
    final String status = "publish";

    /**
     * Заголовок создаваемой записи
     */
    final String title = "New post";

    /**
     * Содержание создаваемой записи
     */
    final String content = "sample text";

    /**
     * Ожидаемое содержание создаваемой записи
     */
    final String contentExpected = "<p>" + content + "</p>\n";

    @BeforeClass
    public void setup() {
        BaseRequests.initRequestSpecification();
    }

    /**
     * Список ID записей.
     */
    private final List<String> postsId = new ArrayList<>();

    /**
     * Тест создания записи.
     */
    @Test(description = "Create post API test", priority = 1)
    public void testCreatePost() {
        Post postPojo = Post.builder()
                .status(status)
                .title(title)
                .content(content)
                .build();

        BaseRequests.createPost(postsId, postPojo, status, title, contentExpected);

        Post post = BaseRequests.getPostById(postsId.get(0));

        SoftAssert softAssertion = new SoftAssert();
        softAssertion.assertEquals(post.getStatus(), status, "Статус записи не совпадает");
        softAssertion.assertEquals(post.getTitle(), title, "Заголовок записи не совпадает");
        softAssertion.assertEquals(post.getContent(), contentExpected,"Содержание записи не совпадает");
        softAssertion.assertAll();
        
        BaseRequests.deletePostsById(postsId);
    }
}
