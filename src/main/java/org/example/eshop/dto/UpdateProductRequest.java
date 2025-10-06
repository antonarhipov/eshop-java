package org.example.eshop.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.example.eshop.entity.ProductStatus;

public final class UpdateProductRequest {
    private final String slug; // nullable
    private final String title; // nullable
    private final String type; // nullable
    private final String description; // nullable
    private final ProductStatus status; // nullable

    @JsonCreator
    public UpdateProductRequest(
            @JsonProperty("slug") @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase letters, numbers, and hyphens") String slug,
            @JsonProperty("title") @Size(max = 255, message = "Title must not exceed 255 characters") String title,
            @JsonProperty("type") @Size(max = 100, message = "Type must not exceed 100 characters") String type,
            @JsonProperty("description") @Size(max = 5000, message = "Description must not exceed 5000 characters") String description,
            @JsonProperty("status") ProductStatus status
    ) {
        this.slug = slug;
        this.title = title;
        this.type = type;
        this.description = description;
        this.status = status;
    }

    public String getSlug() { return slug; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public ProductStatus getStatus() { return status; }
}
