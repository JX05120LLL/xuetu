package com.star.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.course.dto.CategoryDTO;
import com.star.course.dto.CategoryRequest;
import com.star.course.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 * 
 * @author star
 */
public interface CategoryService extends IService<Category> {

    /**
     * 查询所有分类列表
     * 
     * @return 分类列表
     */
    List<CategoryDTO> getAllCategories();

    /**
     * 查询分类树
     * 
     * @return 分类树
     */
    List<CategoryDTO> getCategoryTree();

    /**
     * 根据父ID查询子分类
     * 
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<CategoryDTO> getCategoriesByParentId(Long parentId);

    /**
     * 根据ID查询分类详情
     * 
     * @param id 分类ID
     * @return 分类详情
     */
    CategoryDTO getCategoryDetail(Long id);

    /**
     * 创建分类
     * 
     * @param request 创建请求
     * @return 分类ID
     */
    Long createCategory(CategoryRequest request);

    /**
     * 更新分类
     * 
     * @param id 分类ID
     * @param request 更新请求
     * @return 是否成功
     */
    Boolean updateCategory(Long id, CategoryRequest request);

    /**
     * 删除分类
     * 
     * @param id 分类ID
     * @return 是否成功
     */
    Boolean deleteCategory(Long id);

    /**
     * 检查分类名称是否存在
     * 
     * @param name 分类名称
     * @param excludeId 排除的ID（用于更新时检查）
     * @return 是否存在
     */
    Boolean existsByName(String name, Long excludeId);

    /**
     * 更新分类下的课程数量
     * 
     * @param categoryId 分类ID
     * @return 是否成功
     */
    Boolean updateCourseCount(Long categoryId);
}