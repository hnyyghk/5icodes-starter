package com._5icodes.starter.demo.jdbc.repository;

import com._5icodes.starter.demo.jdbc.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * POST、PUT、PATCH、DELETE请求，对应新建、替换、更新及删除操作
 *
 * @see <a href="https://www.jianshu.com/p/3423fa97d185">通过@RepositoryRestResource自动实现REST接口进行数据库CRUD</a>
 * @see <a href="https://spring.io/guides/gs/accessing-data-rest/">accessing-data-rest</a>
 */
//path属性是表示将所有的请求路径中的orders改为order
//collectionResourceRel属性将返回的json集合中的orders集合的key改为了test_orders
//itemResourceRel将返回的json集合中的单个person的key改为了test_order
@RepositoryRestResource(path = "order", collectionResourceRel = "test_orders", itemResourceRel = "test_order")
public interface OrderRepository extends JpaRepository<Order, Long> {
    //path属性是表示将该方法请求路径中的getOrderByOrderId改为byOrderId
    //rel属性将个性化指定"links"中的属性名
    @RestResource(path = "byOrderId", rel = "getByOrderId")
    List<Order> getOrderByOrderId(String orderId);
}