package org.example.eshop.repository;

import org.example.eshop.entity.Product;
import org.example.eshop.entity.ProductStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void findBySlug_shouldReturnProduct_whenExists() {
        Product p = new Product("green-tea", "Green Tea", "tea", "Fresh green tea", ProductStatus.ACTIVE);
        productRepository.save(p);

        Product found = productRepository.findBySlug("green-tea");
        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("Green Tea");
    }

    @Test
    void findByStatusAndOptionalType_shouldFilterByStatusAndTypeWhenProvided() {
        productRepository.save(new Product("black-tea", "Black Tea", "tea", "", ProductStatus.ACTIVE));
        productRepository.save(new Product("oolong", "Oolong", "tea", "", ProductStatus.INACTIVE));
        productRepository.save(new Product("teapot", "Teapot", "accessory", "", ProductStatus.ACTIVE));

        List<Product> onlyActiveTea = productRepository.findByStatusAndOptionalType(ProductStatus.ACTIVE, "tea");
        assertThat(onlyActiveTea).extracting(Product::getSlug).containsExactly("black-tea");

        List<Product> allActive = productRepository.findByStatusAndOptionalType(ProductStatus.ACTIVE, null);
        assertThat(allActive).extracting(Product::getSlug).containsExactlyInAnyOrder("black-tea", "teapot");
    }

    @Test
    void findByKeyword_shouldReturnMatchesInTitleOrDescription() {
        productRepository.save(new Product("gyokuro", "Gyokuro", "tea", "shaded green tea", ProductStatus.ACTIVE));
        productRepository.save(new Product("sencha", "Sencha", "tea", "Japanese green tea", ProductStatus.ACTIVE));
        productRepository.save(new Product("gaiwan", "Gaiwan", "accessory", "porcelain brewing cup", ProductStatus.ACTIVE));

        List<Product> results = productRepository.findByKeyword("green");
        assertThat(results).extracting(Product::getSlug).containsExactlyInAnyOrder("gyokuro", "sencha");
    }
}
