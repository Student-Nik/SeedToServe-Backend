package com.seedtoserve.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.seedtoserve.enums.OrderStatus;
import com.seedtoserve.model.Customer;
import com.seedtoserve.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

	List<Order> findByCustomer(Customer customer);

	Optional<Customer> findByIdAndCustomerId(int orderId, int customerId);

	Optional<Order> findByRazorpayOrderId(String razorpayOrderId);

	// Count orders by status
	long countByStatus(OrderStatus status);

	// Calculate revenue by status
	@Query("""
			    SELECT COALESCE(SUM(o.totalAmount), 0)
			    FROM Order o
			    WHERE o.status = :status
			""")
	Double getTotalRevenueByStatus(@Param("status") OrderStatus status);
}
