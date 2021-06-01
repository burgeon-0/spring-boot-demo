package org.burgeon.sbd.domain.product;

import org.burgeon.sbd.core.DomainFactory;
import org.burgeon.sbd.core.SpringBeanFactory;
import org.springframework.stereotype.Component;

/**
 * @author Sam Lu
 * @date 2021/5/31
 */
@Component
public class ProductAggregateFactory extends DomainFactory<ProductAggregate, String> {

    @Override
    public ProductAggregate load(String productNo) {
        domainRepository = SpringBeanFactory.getDomainRepository(ProductAggregate.class, String.class);
        return super.load(productNo);
    }

}
