import org.helpers.BaseRequests;
import org.pojo.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.requestSpecification;

public class APITest {

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
                .status("publish")
                .title("New post")
                .content("sample text")
                .build();

        BaseRequests.createPost(entitiesId, postPojo);
    }

}
