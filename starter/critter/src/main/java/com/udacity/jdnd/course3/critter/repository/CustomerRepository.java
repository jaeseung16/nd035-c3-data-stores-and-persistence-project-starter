package com.udacity.jdnd.course3.critter.repository;

import com.udacity.jdnd.course3.critter.entity.Customer;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class CustomerRepository {
    @PersistenceContext
    EntityManager entityManager;

    public void persist(Customer customer) {
        entityManager.persist(customer);
    }

    public List<Customer> findAll() {
        return entityManager
                .createNamedQuery("Customer.findAll", Customer.class)
                .getResultList();
    }

    public Customer find(Long id) {
        return entityManager.find(Customer.class, id);
    }

    public Customer merge(Customer customer) {
        return entityManager.merge(customer);
    }

    public void delete(Long id) {
        entityManager.remove(find(id));
    }
}
