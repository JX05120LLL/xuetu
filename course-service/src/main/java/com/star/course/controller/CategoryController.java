package com.star.course.controller;

import com.star.common.result.R;
import com.star.course.dto.CategoryDTO;
import com.star.course.dto.CategoryRequest;
import com.star.course.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 分类管理控制器
 * 
 * @author star
 */
@Slf4j
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@Tag(name = "分类管理", description = "课程分类的增删改查、分类树等功能")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 查询所有分类列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询所有分类列表", description = "查询所有分类的平铺列表")
    public R<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> result = categoryService.getAllCategories();
        return R.ok(result);
    }

    /**
     * 查询分类树
     */
    @GetMapping("/tree")
    @Operation(summary = "查询分类树", description = "查询分类的树形结构")
    public R<List<CategoryDTO>> getCategoryTree() {
        List<CategoryDTO> result = categoryService.getCategoryTree();
        return R.ok(result);
    }

    /**
     * 根据父ID查询子分类
     */
    @GetMapping("/children/{parentId}")
    @Operation(summary = "查询子分类", description = "根据父分类ID查询子分类列表")
    public R<List<CategoryDTO>> getCategoriesByParentId(
            @Parameter(description = "父分类ID") @PathVariable Long parentId) {
        List<CategoryDTO> result = categoryService.getCategoriesByParentId(parentId);
        return R.ok(result);
    }

    /**
     * 查询分类详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询分类详情", description = "根据分类ID查询详细信息")
    public R<CategoryDTO> getCategoryDetail(@Parameter(description = "分类ID") @PathVariable Long id) {
        CategoryDTO result = categoryService.getCategoryDetail(id);
        return R.ok(result);
    }

    /**
     * 创建分类
     */
    @PostMapping
    @Operation(summary = "创建分类", description = "创建新的课程分类")
    public R<Long> createCategory(@Valid @RequestBody CategoryRequest request) {
        Long categoryId = categoryService.createCategory(request);
        return R.ok(categoryId);
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新分类", description = "更新分类信息")
    public R<Boolean> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        Boolean result = categoryService.updateCategory(id, request);
        return R.ok(result);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类", description = "删除分类（必须没有子分类和课程）")
    public R<Boolean> deleteCategory(@Parameter(description = "分类ID") @PathVariable Long id) {
        Boolean result = categoryService.deleteCategory(id);
        return R.ok(result);
    }

    /**
     * 检查分类名称是否可用
     */
    @GetMapping("/check-name")
    @Operation(summary = "检查分类名称", description = "检查分类名称是否已被使用")
    public R<Boolean> checkCategoryName(
            @Parameter(description = "分类名称") @RequestParam String name,
            @Parameter(description = "排除的分类ID（用于更新时检查）") @RequestParam(required = false) Long excludeId) {
        Boolean exists = categoryService.existsByName(name, excludeId);
        return R.ok(!exists);
    }

    /**
     * 更新分类下的课程数量
     */
    @PostMapping("/{id}/update-course-count")
    @Operation(summary = "更新分类课程数量", description = "重新统计并更新分类下的课程数量")
    public R<Boolean> updateCourseCount(@Parameter(description = "分类ID") @PathVariable Long id) {
        Boolean result = categoryService.updateCourseCount(id);
        return R.ok(result);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查分类服务是否正常")
    public R<String> health() {
        return R.ok("分类服务运行正常");
    }
}