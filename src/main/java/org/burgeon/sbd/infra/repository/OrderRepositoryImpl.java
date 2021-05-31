package org.burgeon.sbd.infra.repository;

import org.burgeon.sbd.domain.DomainRepository;
import org.burgeon.sbd.domain.order.OrderAggregate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Sam Lu
 * @date 2021/5/31
 */
@Component
public class OrderRepositoryImpl implements DomainRepository<OrderAggregate, String> {

    @Override
    public OrderAggregate load(String orderNo) {
        return null;
    }

    @Override
    public void save(OrderAggregate order) {

    }

    @Override
    public void save(List<OrderAggregate> orderAggregates) {

    }

}
