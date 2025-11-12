package org.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Post {

    /**
     * Статус записи.
     */
    @Builder.Default
    private String status = "private";

    /**
     * Название записи.
     */
    @Builder.Default
    private String title = "default";

    /**
     * Содержимое записи.
     */
    @Builder.Default
    private String content = "default";
}
