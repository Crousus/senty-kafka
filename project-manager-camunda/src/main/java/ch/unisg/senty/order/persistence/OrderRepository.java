package ch.unisg.senty.order.persistence;

import ch.unisg.senty.order.domain.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface OrderRepository extends CrudRepository<Order, String> {

}
