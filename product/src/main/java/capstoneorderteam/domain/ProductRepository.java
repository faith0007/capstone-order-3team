package capstoneorderteam.domain;

import capstoneorderteam.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="products", path="products")
public interface ProductRepository extends PagingAndSortingRepository<Product, Integer>{

}
