package ru.practicum.client.category;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.client.MyFeignClientFallback;
import ru.practicum.dto.category.CategoryDto;

import java.util.List;

@FeignClient(name = "event-service", fallback = MyFeignClientFallback.class)
public interface PublicCategoryClient {

    @GetMapping("/categories")
    List<CategoryDto> getAll(@RequestParam(defaultValue = "0") int from,
                             @RequestParam(defaultValue = "10") int size);

    @GetMapping("/categories/{catId}")
    CategoryDto getById(@PathVariable Long catId);
}
