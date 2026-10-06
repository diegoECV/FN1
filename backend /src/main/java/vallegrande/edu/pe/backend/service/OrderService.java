package vallegrande.edu.pe.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vallegrande.edu.pe.backend.dto.OrderDto;
import vallegrande.edu.pe.backend.model.Order;
import vallegrande.edu.pe.backend.model.OrderTracking;
import vallegrande.edu.pe.backend.repository.OrderRepository;
import vallegrande.edu.pe.backend.repository.OrderTrackingRepository;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderTrackingRepository orderTrackingRepository;

    public Flux<OrderDto> findAll() {
        return orderRepository.findAll()
                .flatMap(order -> orderTrackingRepository.findByOrderId(order.getOrderId())
                        .map(tracking -> buildDto(order, tracking))
                        .defaultIfEmpty(buildDto(order, new OrderTracking(null, order.getOrderId(), null))));
    }

    public Mono<OrderDto> findById(Integer orderId) {
        return orderRepository.findById(orderId)
                .flatMap(order -> orderTrackingRepository.findByOrderId(orderId)
                        .map(tracking -> buildDto(order, tracking))
                        .defaultIfEmpty(buildDto(order, new OrderTracking(null, orderId, null))));
    }

    public Mono<OrderDto> save(OrderDto dto) {
        Order order = Order.builder()
                .clientId(dto.getClientId())
                .orderCode(dto.getOrderCode())
                .orderDate(dto.getOrderDate())
                .incoterm(dto.getIncoterm())
                .status(dto.getStatus())
                .statusDescription(dto.getStatusDescription())
                .build();

        return orderRepository.save(order)
                .flatMap(savedOrder -> {
                    OrderTracking tracking = OrderTracking.builder()
                            .orderId(savedOrder.getOrderId())
                            .events(dto.getTrackingEvents())
                            .build();
                    return orderTrackingRepository.save(tracking)
                            .map(savedTracking -> buildDto(savedOrder, savedTracking));
                });
    }

    public Mono<OrderDto> update(Integer id, OrderDto dto) {
        return orderRepository.findById(id)
                .flatMap(existingOrder -> {
                    existingOrder.setClientId(dto.getClientId());
                    existingOrder.setOrderCode(dto.getOrderCode());
                    existingOrder.setOrderDate(dto.getOrderDate());
                    existingOrder.setIncoterm(dto.getIncoterm());
                    existingOrder.setStatus(dto.getStatus());
                    existingOrder.setStatusDescription(dto.getStatusDescription());
                    return orderRepository.save(existingOrder);
                })
                .flatMap(updatedOrder -> orderTrackingRepository.findByOrderId(id)
                        .flatMap(existingTracking -> {
                            existingTracking.setEvents(dto.getTrackingEvents());
                            return orderTrackingRepository.save(existingTracking);
                        })
                        .switchIfEmpty(orderTrackingRepository.save(OrderTracking.builder()
                                .orderId(id)
                                .events(dto.getTrackingEvents())
                                .build()))
                        .map(updatedTracking -> buildDto(updatedOrder, updatedTracking)));
    }

    private OrderDto buildDto(Order order, OrderTracking tracking) {
        return OrderDto.builder()
                .orderId(order.getOrderId())
                .clientId(order.getClientId())
                .orderCode(order.getOrderCode())
                .orderDate(order.getOrderDate())
                .incoterm(order.getIncoterm())
                .status(order.getStatus())
                .statusDescription(order.getStatusDescription())
                .trackingEvents(tracking.getEvents())
                .build();
    }
}
