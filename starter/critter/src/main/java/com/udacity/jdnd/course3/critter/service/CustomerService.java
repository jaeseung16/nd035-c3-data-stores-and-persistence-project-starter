package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Customer;
import com.udacity.jdnd.course3.critter.pet.PetDTO;
import com.udacity.jdnd.course3.critter.repository.CustomerRepository;
import com.udacity.jdnd.course3.critter.user.CustomerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PetService petService;

    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        logger.info("Saving customerDTO={}", customerDTO);

        Customer customer = getEntityFromDTO(customerDTO);
        customerRepository.persist(customer);
        logger.info("Persisted a customer: id={}", customer.getId());

        CustomerDTO customerDTOToReturn = convertEntityToDTO(customer, customerDTO.getPetIds());
        logger.info("Returning {}", customerDTOToReturn);
        return customerDTOToReturn;
    }

    public List<CustomerDTO> getAllCustomers() {
        logger.info("Retrieving all customers");
        List<CustomerDTO> customerDTOList = new ArrayList<>();

        List<Customer> customerList = customerRepository.findAll();

        if (customerList.isEmpty()) {
            logger.warn("No customers found");
            throw new CustomerNotFoundException();
        }

        for (Customer customer : customerList) {
            List<Long> petIds = findPetIds(customer.getId());
            customerDTOList.add(convertEntityToDTO(customer, petIds));
        }

        logger.info("Retrieving {} customers", customerDTOList.size());
        return customerDTOList;
    }

    public CustomerDTO findCustomer(long customerId) {
        logger.info("Retrieving a customer: id={}", customerId);
        CustomerDTO customerDTO = getCustomerDTOFromEntities(customerId);
        logger.info("Returning {}", customerDTO);
        return customerDTO;
    }

    public CustomerDTO findCustomerByPetId(long petId) {
        logger.info("Retrieving a customer owning a pet: petId={}", petId);
        PetDTO petDTO = petService.getPet(petId);
        CustomerDTO customerDTO = getCustomerDTOFromEntities(petDTO.getOwnerId());
        logger.info("Returning {}", customerDTO);
        return customerDTO;
    }

    private CustomerDTO getCustomerDTOFromEntities(long customerId) {
        Customer customer = customerRepository.find(customerId);

        if (customer == null) {
            logger.warn("Cannot find a customer for a given id = {}", customerId);
            throw new CustomerNotFoundException("Cannot find a customer for a given id = " + customerId + ".");
        }

        List<Long> petIds = findPetIds(customerId);
        return convertEntityToDTO(customer, petIds);
    }

    private List<Long> findPetIds(long customerId) {
        return petService.getPetByOwner(customerId)
                .stream()
                .map(PetDTO::getId)
                .collect(Collectors.toList());
    }

    private CustomerDTO convertEntityToDTO(Customer customer, List<Long> petIds) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(customer.getId());
        customerDTO.setName(customer.getName());
        customerDTO.setNotes(customer.getNotes());
        customerDTO.setPhoneNumber(customer.getPhoneNumber());
        customerDTO.setPetIds(petIds);
        return customerDTO;
    }

    private Customer getEntityFromDTO(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        customer.setName(customerDTO.getName());
        customer.setNotes(customerDTO.getNotes());
        customer.setPhoneNumber(customerDTO.getPhoneNumber());
        return customer;
    }
}
