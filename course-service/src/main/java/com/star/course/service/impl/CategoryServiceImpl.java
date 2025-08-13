package com.star.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.course.dto.CategoryDTO;
import com.star.course.dto.CategoryRequest;
import com.star.course.entity.Category;
import com.star.course.exception.CategoryException;
import com.star.course.exception.CourseServiceException;
import com.star.course.mapper.CategoryMapper;
import com.star.course.service.CategoryService;
import com.star.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 * 
 * @author star
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDTO> getAllCategories() {
        log.info("查询所有分类列表");

        // 使用MyBatis Plus的LambdaQueryWrapper
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSortOrder, Category::getId);
        
        List<Category> categories = list(queryWrapper);
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> getCategoryTree() {
        log.info("查询分类树");

        // 使用MyBatis Plus查询所有分类
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSortOrder, Category::getId);
        
        List<Category> allCategories = list(queryWrapper);
        return buildCategoryTree(allCategories, 0L);
    }

    @Override
    public List<CategoryDTO> getCategoriesByParentId(Long parentId) {
        log.info("查询子分类: parentId={}", parentId);

        // 使用MyBatis Plus的LambdaQueryWrapper
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getParentId, parentId)
                    .orderByAsc(Category::getSortOrder, Category::getId);
        
        List<Category> categories = list(queryWrapper);
        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getCategoryDetail(Long id) {
        log.info("查询分类详情: id={}", id);

        Category category = getById(id);
        if (category == null) {
            throw CategoryException.notFound(id);
        }

        CategoryDTO dto = convertToDTO(category);
        
        // 查询子分类
        List<CategoryDTO> children = getCategoriesByParentId(id);
        dto.setChildren(children);

        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(CategoryRequest request) {
        log.info("创建分类: name={}", request.getName());

        // 检查分类名称是否已存在
        if (existsByName(request.getName(), null)) {
            throw CategoryException.nameExists(request.getName());
        }

        // 检查父分类是否存在（如果不是顶级分类）
        if (request.getParentId() != null && request.getParentId() > 0) {
            Category parentCategory = getById(request.getParentId());
            if (parentCategory == null) {
                throw CategoryException.notFound(request.getParentId());
            }
        }

        // 设置排序值
        if (request.getSortOrder() == null) {
            Integer maxSort = categoryMapper.getMaxSortOrder(request.getParentId());
            request.setSortOrder(maxSort == null ? 1 : maxSort + 1);
        }

        // 创建分类
        Category category = new Category();
        BeanUtils.copyProperties(request, category);
        category.setCreatedTime(LocalDateTime.now());
        category.setUpdatedTime(LocalDateTime.now());

        if (!save(category)) {
            throw CourseServiceException.internalError("创建分类失败");
        }

        log.info("分类创建成功: id={}", category.getId());
        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCategory(Long id, CategoryRequest request) {
        log.info("更新分类: id={}", id);

        // 检查分类是否存在
        Category existsCategory = getById(id);
        if (existsCategory == null) {
            throw CategoryException.notFound(id);
        }

        // 检查分类名称是否已存在（排除自己）
        if (existsByName(request.getName(), id)) {
            throw CategoryException.nameExists(request.getName());
        }

        // 检查是否设置自己为父分类
        if (request.getParentId() != null && request.getParentId().equals(id)) {
            throw CategoryException.cannotSetSelfAsParent();
        }

        // 检查父分类是否存在
        if (request.getParentId() != null && request.getParentId() > 0) {
            Category parentCategory = getById(request.getParentId());
            if (parentCategory == null) {
                throw CategoryException.notFound(request.getParentId());
            }
        }

        // 更新分类
        Category category = new Category();
        BeanUtils.copyProperties(request, category);
        category.setId(id);
        category.setUpdatedTime(LocalDateTime.now());

        boolean success = updateById(category);
        if (!success) {
            throw CourseServiceException.internalError("更新分类失败");
        }

        log.info("分类更新成功: id={}", id);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteCategory(Long id) {
        log.info("删除分类: id={}", id);

        // 检查分类是否存在
        Category category = getById(id);
        if (category == null) {
            throw CategoryException.notFound(id);
        }

        // 检查分类下是否有课程
        Integer courseCount = categoryMapper.countCoursesByCategoryId(id);
        if (courseCount > 0) {
            throw CategoryException.hasCoursesCannotDelete(id);
        }

        // 检查分类下是否有子分类
        Integer childrenCount = categoryMapper.countChildrenByCategoryId(id);
        if (childrenCount > 0) {
            throw CategoryException.hasCoursesCannotDelete(id);
        }

        boolean success = removeById(id);
        if (!success) {
            throw CourseServiceException.internalError("删除分类失败");
        }

        log.info("分类删除成功: id={}", id);
        return true;
    }

    @Override
    public Boolean existsByName(String name, Long excludeId) {
        Category category = categoryMapper.selectByName(name);
        if (category == null) {
            return false;
        }
        return excludeId == null || !excludeId.equals(category.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCourseCount(Long categoryId) {
        log.info("更新分类课程数量: categoryId={}", categoryId);

        Integer courseCount = categoryMapper.countCoursesByCategoryId(categoryId);
        
        // 使用MyBatis Plus的LambdaUpdateWrapper
        LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Category::getUpdatedTime, LocalDateTime.now())
                    .eq(Category::getId, categoryId);

        return update(updateWrapper);
    }

    /**
     * 构建分类树
     */
    private List<CategoryDTO> buildCategoryTree(List<Category> allCategories, Long parentId) {
        return allCategories.stream()
                .filter(category -> Objects.equals(category.getParentId(), parentId))
                .map(category -> {
                    CategoryDTO dto = convertToDTO(category);
                    List<CategoryDTO> children = buildCategoryTree(allCategories, category.getId());
                    dto.setChildren(children);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 转换为DTO
     */
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        BeanUtils.copyProperties(category, dto);

        // 查询课程数量
        Integer courseCount = categoryMapper.countCoursesByCategoryId(category.getId());
        dto.setCourseCount(courseCount);

        return dto;
    }
}