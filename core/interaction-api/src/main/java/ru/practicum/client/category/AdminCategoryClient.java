package ru.practicum.client.category;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.MyFeignClientFallback;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

@FeignClient(name = "event-service", fallback = MyFeignClientFallback.class)
public interface AdminCategoryClient {

    @PostMapping("/admin/categories")
    CategoryDto create(@RequestBody NewCategoryDto dto);

    @PatchMapping("/admin/categories/{catId}")
    CategoryDto update(@PathVariable Long catId,
                       @RequestBody NewCategoryDto dto);

    @DeleteMapping("/admin/categories/{id}")
    void delete(@PathVariable Long id);
}
