package controllers;

import models.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repositories.OrderRepo;

@RestController
public class OrderController {
    private final OrderRepo orderRepo = new OrderRepo();

    @RequestMapping(value = "/order/save", method = RequestMethod.POST)
    public ResponseEntity<Order> save(@RequestBody Order order) {
        //gradelSystem.out.println(order.getCustomer().getfName());
        orderRepo.saveOrder(order);

        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }

    @RequestMapping("/order/{id}")
    public Order order(@PathVariable String id) {
        return orderRepo.getOrder(id);
    }
}
