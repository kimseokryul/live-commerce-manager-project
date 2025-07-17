package org.kosa.shoppingmaillmanager.host.product;


import java.util.List;
import java.util.Map;

import org.kosa.shoppingmaillmanager.host.product.dto.LowStockProductSummaryDto;
import org.kosa.shoppingmaillmanager.host.product.dto.PopularProductDto;
import org.kosa.shoppingmaillmanager.host.product.dto.ProductRequestDto;
import org.kosa.shoppingmaillmanager.host.product.dto.ProductSearchCondition;
import org.kosa.shoppingmaillmanager.host.product.dto.ProductSimpleDTO;
import org.kosa.shoppingmaillmanager.host.product.dto.ProductStatusDto;
import org.kosa.shoppingmaillmanager.host.product.dto.ProductUpdateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "상품 관리 API", description = "상품 등록, 수정, 조회 등의 기능을 제공")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/products")
public class ProductRestController {

    private final ProductService productService;

    // 상품 리스트 조회
    @Operation(summary = "상품 목록 조회", description = "판매자가 등록한 상품 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ProductListResponse> getProductList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "createdDate") String sort
    ) {
        String userId = (String) request.getAttribute("userId");
        log.info("🔑 userId from JWT: {}", userId);

        ProductSearchCondition cond = new ProductSearchCondition();
        cond.setPage(page);
        cond.setSize(size);
        cond.setStatus(status);
        cond.setCategoryId(categoryId);
        cond.setKeyword(keyword);
        cond.setSort(sort);

        ProductListResponse response = productService.getProductList(userId, cond);
        return ResponseEntity.ok(response);
    }

    // 진열 여부 변경 (userId 포함)
    @Operation(summary = "진열 여부 변경", description = "상품의 진열 상태를 변경합니다.")
    @PostMapping("/display-yn")
    public ResponseEntity<?> updateDisplayYn(
            @RequestBody Map<String, Object> body,
            HttpServletRequest request
    ) {
        String userId = (String) request.getAttribute("userId");
        Integer productId = (Integer) body.get("productId");
        String displayYn = (String) body.get("displayYn");

        if (productId == null || displayYn == null) {
            return ResponseEntity.badRequest().body("productId와 displayYn은 필수입니다.");
        }

        productService.updateDisplayYn(userId, productId, displayYn);
        return ResponseEntity.ok().build();
    }

    // 상품 상세 조회
    @Operation(summary = "상품 상세 조회", description = "상품 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{productId}")
    public ResponseEntity<ProductSimpleDTO> getProductDetail(
        @PathVariable Integer productId,
        HttpServletRequest request
    ) {
        String userId = (String) request.getAttribute("userId");
        ProductSimpleDTO dto = productService.getProductDetail(userId, productId);
        return ResponseEntity.ok(dto);
    }

    // 상품 필드 개별 수정
    @Operation(summary = "상품 개별 수정", description = "상품 상세 정보화면에서 개별 수정합니다.")
    @PatchMapping("/{productId}")
    public ResponseEntity<?> updateProductField(
        @PathVariable Integer productId,
        @RequestBody Map<String,Object> updates,
        HttpServletRequest request
    ) {
        String userId = (String) request.getAttribute("userId");
        productService.updateProductField(userId, productId, updates);
        return ResponseEntity.ok().build();
    }
    @Operation(summary = "상품 등록", description = "판매할 상품을 등록합니다.")
    @PostMapping
    public ResponseEntity<?> registerProduct(
        @RequestPart("product") ProductRequestDto productDto,
        @RequestPart("mainImage") MultipartFile mainImage,
        HttpServletRequest request
    ) {
        String userId = (String) request.getAttribute("userId");
        log.info("📦 상품 등록 요청 by userId: {}", userId);

        // 필드 유효성 검사 (프론트에도 있지만 백엔드에도 최소 검증)
        if (mainImage == null || mainImage.isEmpty()) {
            return ResponseEntity.badRequest().body("대표 이미지는 필수입니다.");
        }

        try {
            productService.registerProduct(userId, productDto, mainImage);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("❌ 상품 등록 실패", e);
            return ResponseEntity.internalServerError().body("상품 등록 중 오류가 발생했습니다.");
        }
    }
    @Operation(summary = "상품 수정", description = "등록된 상품을 수정합니다.")
    @PostMapping("/{productId}/edit")
    public ResponseEntity<?> updateProduct(
            @PathVariable("productId") Integer productId,
            @RequestPart("product") ProductUpdateDto dto,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest request
    ) {
        try {
            String userId = (String) request.getAttribute("userId");

            // 이미지가 따로 넘어왔으면 DTO에 넣어줌
            if (dto.getMainImage() == null && mainImage != null) {
                dto.setMainImage(mainImage);
            }

            productService.updateProduct(userId, productId, dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    @Operation(summary = "품절 임박 상품 조회", description = "등록한 상품 중 품절에 임박한 상품을 조회합니다", tags = {"매출 현황 및 대시보드 API"})
    @GetMapping("/dashboard/sold-out")
    public ResponseEntity<LowStockProductSummaryDto> getLowStockProducts(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        log.info("📦 품절 임박 상품 요청 by userId: {}", userId);

        LowStockProductSummaryDto result = productService.getLowStockProducts(userId);
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "인기 상품 조회", description = "등록한 상품 중 인기 상품을 조회합니다.", tags = {"매출 현황 및 대시보드 API"})
    @GetMapping("/dashboard/popular")
    public ResponseEntity<List<PopularProductDto>> getPopularProducts(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        log.info("🔥 인기 상품 요청 by userId: {}", userId);

        List<PopularProductDto> result = productService.getPopularProducts(userId);
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "상품 상태 조회", description = "등록한 상품의 상품 상태를 조회합니다." ,tags = {"매출 현황 및 대시보드 API"})
    @GetMapping("/dashboard/product-status")
    public ResponseEntity<ProductStatusDto> getProductStatus(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        log.info("📦 상품 상태 요청 by userId: {}", userId);

        ProductStatusDto result = productService.getProductStatus(userId);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/dashboard/admin/popular")
    public ResponseEntity<List<PopularProductDto>> getWholePopularProducts(HttpServletRequest request) {

        List<PopularProductDto> result = productService.getWholePopularProducts();
        return ResponseEntity.ok(result);
    }
}

