Тест-кейс 1. Создание записи:

Предусловия: В запросе использован Basic Auth c корректным логином и паролем

Шаги:

Выполнить POST запрос: http://localhost:8000/index.php?rest_route=/wp/v2/posts&title="New post"&content="<sample text>"&status=publish

Ожидаемый результат: HTTP 201, JSON

    {
        "id": целое число,
        "status": "publish",
        "title": { "rendered": "&#171;New post&#187;" },
        "content": { "rendered": "<p>&#171;sample text&#187;</p>\n" }
    }




Тест-кейс 2. Обновление записи:

Предусловия: Существует запись "id" = {id} с "title" != "New new post", в запросе использован Basic Auth c корректным логином и паролем

Шаги:

Выполнить PUT запрос: http://localhost:8000/index.php?rest_route=/wp/v2/posts/{id}&title=New new post

Ожидаемый результат: HTTP 200, JSON 

    {
        "id": {id}
        "title": { "rendered": "New new post" }
    }




Тест-кейс 3. Изменение записи:

Предусловия: Существует запись "id" = {id} с "title" != "New new post", в запросе использован Basic Auth c корректным логином и паролем

Шаги:

Выполнить PATCH запрос: http://localhost:8000/index.php?rest_route=/wp/v2/posts/{id}&title=New new post

Ожидаемый результат: HTTP 200, JSON 

    {
        "id": {id},
        "title": { "rendered": "New new post" }
    }





Тест-кейс 4. Удаление записи:

Предусловия: Существует запись "id" = {id}, в запросе использован Basic Auth c корректным логином и паролем

Шаги:

Выполнить DELETE запрос: http://localhost:8000/index.php?rest_route=/wp/v2/posts/{id}&force=true

Ожидаемый результат: HTTP 200, JSON 

    {
        "deleted": true,
        "previous": { "id": {id} }
    }