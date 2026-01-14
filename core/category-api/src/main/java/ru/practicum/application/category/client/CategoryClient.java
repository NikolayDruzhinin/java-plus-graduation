package ru.practicum.application.category.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.application.category.api.CommonCategoryInterface;

@FeignClient(name = "category-service")
public interface CategoryClient extends CommonCategoryInterface {
}
