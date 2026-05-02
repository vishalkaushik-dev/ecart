package com.JVM.eCart.category.service;

import com.JVM.eCart.category.dto.*;
import com.JVM.eCart.category.entity.Category;
import com.JVM.eCart.category.entity.CategoryMetadataField;
import com.JVM.eCart.category.entity.CategoryMetadataFieldValue;
import com.JVM.eCart.category.repository.CategoryMetaDataFieldRepository;
import com.JVM.eCart.category.repository.CategoryMetadataFieldValuesRepository;
import com.JVM.eCart.category.repository.CategoryRepository;
import com.JVM.eCart.common.exception.BadRequestException;
import com.JVM.eCart.common.exception.DuplicateValidationException;
import com.JVM.eCart.product.entity.Product;
import com.JVM.eCart.product.entity.ProductVariation;
import com.JVM.eCart.product.repository.ProductRepository;
import com.JVM.eCart.product.repository.ProductVariationRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryMetaDataFieldRepository categoryMetaDataFieldRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMetadataFieldValuesRepository categoryMetadataFieldValuesRepository;
    private final ProductRepository productRepository;
    private final ProductVariationRepository productVariationRepository;

    public ApiResponse addMetaDataField(MetaDataFieldRequest request) {

        if (categoryMetaDataFieldRepository.existsByNameIgnoreCase(request.name()))
            throw new BadRequestException("Metadata field with name '" + request.name() + "' already exists.");

        CategoryMetadataField categoryMetaDataField = new CategoryMetadataField();
        categoryMetaDataField.setName(request.name().trim().toLowerCase());

        try {
            categoryMetaDataField = categoryMetaDataFieldRepository.save(categoryMetaDataField);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Metadata field with name '" + request.name() + "' already exists.");
        }

        return new ApiResponse("Meta data field created successfully.", categoryMetaDataField.getId());
    }

    public Page<FetchMetaDataAllFieldResponse> fetchMetaDataAllFields(int pageOffset, int max, String sort, String order, String query) {

        Sort.Direction direction = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageOffset, max, Sort.by(direction, sort));

        Page<CategoryMetadataField> page;

        if (query != null && !query.trim().isBlank()) {
            page = categoryMetaDataFieldRepository.findByNameIgnoreCase(query.trim(), pageable);
        } else {
            page = categoryMetaDataFieldRepository.findAll(pageable);
        }

        return page.map(field -> new FetchMetaDataAllFieldResponse(field.getId(), field.getName()));
    }

    public ApiResponse createCategory(CreateCategoryRequest request) {

        Long parentId = request.parentId();

        if (categoryRepository.existsByNameAndParentCategory_Id(request.name(), parentId))
            throw new DuplicateValidationException("Duplicate category");

        Category category = new Category();
        category.setName(request.name());

        if (parentId != null) {
            Category parentCategory = categoryRepository.findById(parentId).orElseThrow(() -> new RuntimeException("Parent category not found with id: " + parentId));
            category.setParentCategory(parentCategory);
        }
        categoryRepository.save(category);
        return new ApiResponse("Category created successfully.", category.getId());
    }

    public FetchCategoryResponse getCategoryById(Long id) {

        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        List<ParentCategoryDto> parentHierarchy = buildParentHierarchy(category);

        List<CategoryMetadataResponse> metadata = getMetadata(category.getId());
        System.out.println("Metadata: " + metadata);

        List<FetchCategoryResponse> childResponses = getCategoryChildren(category.getId());

        return FetchCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parent(parentHierarchy)
                .children(childResponses)
                .metadata(metadata)
                .build();
    }

    public List<FetchCategoryResponse> getAllCategoriesWithAdminView(CategoryFilterRequest request) {

        Sort.Direction direction =
                request.getOrder().equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
                request.getOffset() / request.getMax(),
                request.getMax(),
                Sort.by(direction, request.getSort())
        );

        Page<Category> page;

        if (request.getQuery() != null && !request.getQuery().isBlank()) {
            page = categoryRepository.findByNameContainingIgnoreCase(
                    request.getQuery(), pageable);
        } else {
            page = categoryRepository.findAll(pageable);
        }

        return page.getContent().stream()
                .map(this::mapToAllCategoryAdminResponse)
                .toList();

    }

    public ApiResponse updateCategory(Long categoryId, UpdateCategoryRequest request) {

        // Step 1: Validate ID
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Long parentId = category.getParentCategory() != null ? category.getParentCategory().getId() : null;

        boolean exists = categoryRepository.existsByNameIgnoreCaseAndParentCategory_IdAndIdNot(request.name(), parentId, categoryId);
        if (exists) {
            throw new RuntimeException("Category with same name already exists");
        }

        category.setName(request.name());
        categoryRepository.save(category);
        return new ApiResponse("Category updated successfully.", categoryId);
    }

    @Transactional
    public String addMetadataFieldValuesWithCategory(CategoryMetadataFieldValuesRequest request) {

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        for (FieldValuesDto field : request.fields()) {

            CategoryMetadataField metaField = categoryMetaDataFieldRepository.findById(field.fieldId())
                    .orElseThrow(() -> new RuntimeException("Invalid Field ID"));

            List<String> values = field.values();
            if (values == null || values.isEmpty()) {
                throw new RuntimeException("Values cannot be empty for field '" + metaField.getName() + "'");
            }

            // Normalize + trim + validate input values
            Set<String> normalizedInput = new HashSet<>();
            Set<String> cleanedInput = new LinkedHashSet<>();

            for (String val : values) {
                String trimmed = val.trim();

                if (trimmed.isEmpty()) {
                    throw new RuntimeException("Empty string not allowed in values");
                }

                String normalized = trimmed.toLowerCase();

                if (!normalizedInput.add(normalized)) {
                    throw new DuplicateValidationException("Duplicate value in request: " + val);
                }

                cleanedInput.add(trimmed);
            }

            Optional<CategoryMetadataFieldValue> existingFieldValue =
                    categoryMetadataFieldValuesRepository
                            .findByCategory_IdAndMetadataField_Id(category.getId(), metaField.getId());

            if (existingFieldValue.isPresent()) {

                CategoryMetadataFieldValue fieldValue = existingFieldValue.get();

                Set<String> normalizedSet = new HashSet<>();
                Set<String> finalList = new LinkedHashSet<>();

                // Existing values
                if (fieldValue.getValues() != null && !fieldValue.getValues().isEmpty()) {
                    for (String val : fieldValue.getValues().split(",")) {
                        String trimmed = val.trim();
                        String normalized = trimmed.toLowerCase();

                        normalizedSet.add(normalized);
                        finalList.add(trimmed);
                    }
                }

                // Add new values
                for (String val : cleanedInput) {
                    String normalized = val.toLowerCase();

                    if (!normalizedSet.add(normalized)) {
                        throw new RuntimeException("Value already exists: " + val);
                    }

                    finalList.add(val);
                }

                fieldValue.setValues(String.join(",", finalList));
                categoryMetadataFieldValuesRepository.save(fieldValue);

            } else {

                // Create new entry
                CategoryMetadataFieldValue newFieldValue = new CategoryMetadataFieldValue();
                newFieldValue.setCategory(category);
                newFieldValue.setMetadataField(metaField);
                newFieldValue.setValues(String.join(",", cleanedInput));

                categoryMetadataFieldValuesRepository.save(newFieldValue);
            }
        }
        return "Metadata values added successfully to category";
    }

    @Transactional
    public String updateMetadataValues(UpdateCategoryMetadataValuesRequest request) {

        // Validate Category
        Category category = categoryRepository.findById(request.categoryId()).orElseThrow(() -> new RuntimeException("Category not found"));
        for (FieldValuesDto field : request.fields()) {

            CategoryMetadataField metaField = categoryMetaDataFieldRepository.findById(field.fieldId())
                    .orElseThrow(() -> new RuntimeException("Invalid Field ID"));

            // Check mapping exists
            CategoryMetadataFieldValue existingFieldValue = categoryMetadataFieldValuesRepository.findByCategoryAndMetadataField(category, metaField)
                    .orElseThrow(() -> new RuntimeException("Field not associated with Category "));

            // validate new Values list
            List<String> newValues = field.values();
            if(newValues.isEmpty())
                throw new RuntimeException("Values cannot be empty for field '" + metaField.getName() + "'");

            // Set for duplicate check (normalized)
            Set<String> normalizedSet = new HashSet<>();

            // List to preserve original values
            Set<String> finalList = new HashSet<>();
            if(existingFieldValue.getValues() != null) {
                for (String val : existingFieldValue.getValues().split(",")) {
                    String trimmed = val.trim();
                    String normalized = trimmed.toLowerCase();

                    normalizedSet.add(normalized);
                    finalList.add(trimmed);
                }
            }

            // Add new values to existing list
            for (String val : newValues) {
                String trimmed = val.trim();
                String normalized = trimmed.toLowerCase();

                if(!normalizedSet.add(normalized))
                    throw new DuplicateValidationException("Duplicate value: " + val);

                finalList.add(trimmed);
            }

            // save back the entity that we fetched
            existingFieldValue.setValues(String.join(",", finalList));
            categoryMetadataFieldValuesRepository.save(existingFieldValue);
        }
        return "Metadata values updated successfully";
    }

    @Transactional
    public List<FetchCategoryResponse> getAllLeafCategories() {

        List<Category> leafCategoryList = categoryRepository.findLeafCategories();
        return leafCategoryList.stream()
                .map(this::mapToAllLeafCategorySellerResponse)
                .toList();
    }

    private FetchCategoryResponse mapToAllLeafCategorySellerResponse(Category category) {

        return FetchCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parent(buildParentHierarchy(category))
                .metadata(getMetadata(category.getId()))
                .build();
    }

    private FetchCategoryResponse mapToAllCategoryAdminResponse(Category category) {

        List<ParentCategoryDto> parentHierarchy = buildParentHierarchy(category);

        List<FetchCategoryResponse> childResponses = getCategoryChildren(category.getId());

        List<CategoryMetadataResponse> metadata = getMetadata(category.getId());

        return FetchCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parent(parentHierarchy)
                .children(childResponses)
                .metadata(metadata)
                .build();
    }

    public List<CategoryLevelResponse> getAllCategoriesWithSellerView(Long categoryId) {

        List<Category> categories;
        if(categoryId == null) {
            categories = categoryRepository.findByParentCategoryIsNull();
        } else {

            Category parent = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Invalid Category ID"));

            categories = categoryRepository.findByParentCategoryId(parent.getId()); // find Immediate children
        }

        return categories.stream()
                .map(Category -> new CategoryLevelResponse(
                        Category.getId(),
                        Category.getName()
                )).toList();
    }

    public CustomerFilterCategoryResponse getCategoryByIdForCustomer(Long categoryId) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Invalid Category ID"));

        List<Category> leafCategories = getLeafNodes(category); // get leaf nodes

        List<Product> products = productRepository.findByCategoryIn(leafCategories); // Get products in those leaf nodes

        List<ProductVariation> variations = productVariationRepository.findByProductIn(products); // Get variations for those products

        // Brands
        List<String> brands = products.stream()
                .map((product) -> product.getBrand())
                .filter(Objects::nonNull)
                .distinct().toList();

        BigDecimal minPrice = variations.stream()
                .map(ProductVariation::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal maxPrice = variations.stream()
                .map(ProductVariation::getPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        // Metadata from category
        List<CategoryMetadataResponse> metadata = getMetadata(category.getId());

        return CustomerFilterCategoryResponse.builder()
                .metadata(metadata)
                .brands(brands)
                .maxPrice(minPrice)
                .maxPrice(maxPrice)
                .build();

    }

    private List<Category> getLeafNodes(Category category) {

        List<Category> result = new ArrayList<>();

        List<Category> children = categoryRepository.findByParentCategoryId(category.getId());

        if (children.isEmpty()) {
            result.add(category);
            return result;
        }

        for (Category child : children) {
            result.addAll(getLeafNodes(child)); // recursion 🔥
        }

        return result;
    }

    private List<ParentCategoryDto> buildParentHierarchy(Category category) {

        List<ParentCategoryDto> parentHierarchy = new ArrayList<>();

        Category current = category.getParentCategory();

        while (current != null) {
            parentHierarchy.add(
                    new ParentCategoryDto(current.getId(), current.getName())
            );
            current = current.getParentCategory();
        }
//        Collections.reverse(parentHierarchy); // root → parent order

        return parentHierarchy;
    }

    private List<CategoryMetadataResponse> getMetadata(Long categoryId) {

        List<CategoryMetadataFieldValue> values =
                categoryMetadataFieldValuesRepository.findByCategory_Id(categoryId);

        // group by field name
        Map<String, List<String>> grouped =
                values.stream()
                        .collect(Collectors.groupingBy(
                                v -> v.getMetadataField().getName(),
                                Collectors.mapping(
                                        CategoryMetadataFieldValue::getValues,
                                        Collectors.toList()
                                )
                        ));

        System.out.println("Grouped Metadata: " + grouped);

        // convert to DTO
        return grouped.entrySet().stream()
                .map(e -> new CategoryMetadataResponse(
                        e.getKey(),
                        e.getValue()
                ))
                .toList();
    }

    private List<FetchCategoryResponse> getCategoryChildren(Long categoryId) {

        List<Category> children =
                categoryRepository.findByParentCategory_Id(categoryId);
        List<FetchCategoryResponse> childResponses = children.stream()
                .map(c -> FetchCategoryResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .build())
                .toList();
        return childResponses;

    }
}
