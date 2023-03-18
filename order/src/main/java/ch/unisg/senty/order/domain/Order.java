package ch.unisg.senty.order.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity(name="OrderEntity")
public class Order {

  @Id
//  @GeneratedValue(generator = "uuid2")
//  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  protected String id; //= UUID.randomUUID().toString();
  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER  )
  protected Customer customer = new Customer();
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER  )
  protected List<OrderItem> items = new ArrayList<OrderItem>();

  public void addItem(OrderItem i) {
    items.add(i);
  }
  
  public int getTotalSum() {
    int sum = 0;
    for (OrderItem orderItem : items) {
      sum += orderItem.getAmount();
    }
    return sum;
  }
  
  public String getId() {
    return id;
  }

  @JsonProperty("orderId")
  public void setId(String id) {
    this.id = id;
  }

  public List<OrderItem> getItems() {
    return items;
  }


 
  @Override
  public String toString() {
    return "Order [id=" + id + ", items=" + items + "]";
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }


  
}
