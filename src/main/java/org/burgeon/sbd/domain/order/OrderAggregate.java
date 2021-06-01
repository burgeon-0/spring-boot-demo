package org.burgeon.sbd.domain.order;

import lombok.*;
import org.burgeon.sbd.core.DomainEventBus;
import org.burgeon.sbd.core.DomainRepository;
import org.burgeon.sbd.core.SnKeeper;
import org.burgeon.sbd.core.SpringBeanFactory;
import org.burgeon.sbd.core.base.OrderBase;
import org.burgeon.sbd.core.base.OrderItem;
import org.burgeon.sbd.domain.order.command.PlaceOrderCommand;
import org.burgeon.sbd.domain.order.event.CancelOrderEvent;
import org.burgeon.sbd.domain.order.event.DeleteOrderEvent;
import org.burgeon.sbd.domain.order.event.PayOrderEvent;
import org.burgeon.sbd.domain.order.event.PlaceOrderEvent;
import org.burgeon.sbd.domain.product.ProductAggregate;
import org.burgeon.sbd.core.exception.BizException;
import org.burgeon.sbd.core.exception.ErrorCode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Sam Lu
 * @date 2021/5/30
 */
public class OrderAggregate extends OrderBase {

    @Setter
    @Getter
    private String orderNo;

    private DomainRepository<OrderAggregate, String> orderRepository = SpringBeanFactory.getDomainRepository(
            OrderAggregate.class, String.class);
    private DomainRepository<OrderItem, String> orderItemRepository = SpringBeanFactory.getDomainRepository(
            OrderItem.class, String.class);
    private DomainEventBus domainEventBus = SpringBeanFactory.getBean(DomainEventBus.class);

    public OrderAggregate(PlaceOrderCommand placeOrderCommand) {
        orderNo = generateOrderNo();
        setPlaceTime(new Date());
        setStatus(Status.UNPAID.ordinal());

        setItems(new ArrayList<>(placeOrderCommand.getItems().size()));
        List<OrderItem> eventOrderItems = new ArrayList<>(placeOrderCommand.getItems().size());
        int totalPrice = 0;
        for (PlaceOrderCommand.Item item : placeOrderCommand.getItems()) {
            ProductAggregate productAggregate = item.getProductAggregate();
            if (!productAggregate.stockEnough(item.getCount())) {
                throw new BizException(ErrorCode.PRODUCT_STOCK_NOT_ENOUGH);
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderNo(orderNo);
            orderItem.setProductNo(productAggregate.getProductNo());
            orderItem.setProductName(productAggregate.getProductName());
            orderItem.setTotalCount(item.getCount());
            orderItem.setTotalPrice(productAggregate.getPrice() * item.getCount());
            totalPrice += orderItem.getTotalPrice();
            getItems().add(orderItem);

            OrderItem eventOrderItem = orderItem.to(OrderItem.class);
            eventOrderItems.add(eventOrderItem);
        }
        setTotalPrice(totalPrice);
        orderItemRepository.save(getItems());
        orderRepository.save(this);

        PlaceOrderEvent placeOrderEvent = new PlaceOrderEvent();
        placeOrderEvent.setOrderNo(orderNo);
        placeOrderEvent.setItems(eventOrderItems);
        placeOrderEvent.setTotalPrice(totalPrice);
        placeOrderEvent.setPlaceTime(getPlaceTime());
        domainEventBus.publishEvent(placeOrderEvent);
    }

    public void pay() {
        setPayTime(new Date());
        setStatus(Status.PAID.ordinal());
        orderRepository.save(this);

        PayOrderEvent payOrderEvent = new PayOrderEvent();
        payOrderEvent.setOrderNo(orderNo);
        payOrderEvent.setPayTime(getPayTime());
        domainEventBus.publishEvent(payOrderEvent);
    }

    public void cancel() {
        setCancelTime(new Date());
        setStatus(Status.CANCELLED.ordinal());
        orderRepository.save(this);

        CancelOrderEvent cancelOrderEvent = new CancelOrderEvent();
        cancelOrderEvent.setOrderNo(orderNo);
        cancelOrderEvent.setCancelTime(getCancelTime());
        domainEventBus.publishEvent(cancelOrderEvent);
    }

    public void delete() {
        setDeleteTime(new Date());
        setStatus(Status.DELETED.ordinal());
        orderRepository.save(this);

        DeleteOrderEvent deleteOrderEvent = new DeleteOrderEvent();
        deleteOrderEvent.setOrderNo(orderNo);
        deleteOrderEvent.setDeleteTime(getDeleteTime());
        domainEventBus.publishEvent(deleteOrderEvent);
    }

    private String generateOrderNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String prefix = sdf.format(new Date());
        String sn = SnKeeper.get("Order:" + prefix);
        return prefix + sn;
    }

    enum Status {
        /**
         * 未支付
         */
        UNPAID,
        /**
         * 已支付
         */
        PAID,
        /**
         * 已取消
         */
        CANCELLED,
        /**
         * 已删除
         */
        DELETED
    }

}
