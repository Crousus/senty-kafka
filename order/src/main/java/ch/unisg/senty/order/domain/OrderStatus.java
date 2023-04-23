package ch.unisg.senty.order.domain;

public enum OrderStatus {
    CREATED,
    AUTHENTICATED,
    VERIFIED,
    REJECTED,
    PAYMENT_FAILED,
    PAID,
    FULFILLED
}
