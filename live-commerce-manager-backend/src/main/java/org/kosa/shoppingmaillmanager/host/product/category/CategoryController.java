package org.kosa.shoppingmaillmanager.host.product.category;



import java.util.List;

import org.kosa.shoppingmaillmanager.host.product.dto.CategoryTreeDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "상품 관리 API", description = "상품 분류(카테고리) 관련 기능 제공")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;
    
    @Operation(summary = "카테고리 트리 조회", description = "전체 카테고리를 트리 구조로 조회합니다. (대분류 → 소분류)")
    @GetMapping("/tree")
    public List<CategoryTreeDTO> getCategoryTree() {
        return categoryService.getCategoryTree();
    }
    
    @Operation(summary = "대분류 카테고리 조회", description = "최상위 카테고리(대분류) 목록을 조회합니다.")
    @GetMapping("/main")
    public List<CategoryTreeDTO> getMainCategories() {
        return categoryService.getMainCategories();
    }
    
    @Operation(summary = "카테고리 경로 문자열 반환", description = "카테고리 ID를 받아 전체 경로 문자열로 변환합니다. (예: '식품 > 과일 > 사과')")
    @GetMapping("/path")
    public String getCategoryPath(@RequestParam Integer categoryId) {
        List<CategoryTreeDTO> flatList = categoryService.getFlatCategoryList();
        return CategoryTreeDTO.getCategoryPath(categoryId, flatList);
    }
    
    @Hidden
    @Operation(summary = "카테고리 평탄화 리스트 조회", description = "전체 카테고리를 계층 없이 flat 구조로 조회합니다.")
    @GetMapping("/flat")
    public List<CategoryTreeDTO> getFlatList() {
        return categoryService.getFlatCategoryList();
    }
}
