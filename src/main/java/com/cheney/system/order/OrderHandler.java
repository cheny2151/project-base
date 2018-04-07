package com.cheney.system.order;

import com.cheney.entity.jpa.BaseEntity;
import com.cheney.utils.SpringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static com.cheney.system.order.OrderFactory.DEFAULT_ORDER_PRO;

public class OrderHandler {

    private final static CriteriaBuilder criteriaBuilder;

    static {
        criteriaBuilder = (CriteriaBuilder) SpringUtils.getBean("criteriaBuilder");
    }

    private OrderHandler() {
    }

    public static void addOrders(CriteriaQuery<?> query, Root<?> root, Order... orders) {
        if (orders != null && orders.length > 0) {
            ArrayList<javax.persistence.criteria.Order> os = new ArrayList<>();
            for (Order o : orders) {
                os.add(o.create(root, criteriaBuilder));
            }
            query.orderBy(os);
        }
    }

    public static <T extends BaseEntity> void addOrders(CriteriaQuery<T> query, Root<T> root, List<Order> orders) {
        if (orders != null && orders.size() > 0) {
            ArrayList<javax.persistence.criteria.Order> os = new ArrayList<>();
            for (Order o : orders) {
                os.add(o.create(root, criteriaBuilder));
            }
            query.orderBy(os);
        }
    }

    /**
     * 生成默认排序
     */
    private static javax.persistence.criteria.Order defaultOrder(Root<?> root) {
        return criteriaBuilder.desc(root.get(DEFAULT_ORDER_PRO));
    }

}
