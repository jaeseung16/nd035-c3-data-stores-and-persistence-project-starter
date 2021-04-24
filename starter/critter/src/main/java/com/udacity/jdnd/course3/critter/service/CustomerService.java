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
        logger.info("Saving employeeDTO.getDaysAvailable() = {}", customerDTO.getName());

        Customer customer = new Customer();
        customer.setName(customerDTO.getName());
        customer.setNotes(customerDTO.getNotes());
        customer.setPhoneNumber(customerDTO.getPhoneNumber());

        logger.info("Persisting customer.getId() = {}", customer.getId());
        customerRepository.persist(customer);
        logger.info("Persisting customer.getId() = {}", customer.getId());

        return convertEntityToDTO(customer, customerDTO.getPetIds());
    }

    public List<CustomerDTO> getAllCustomers() {
        List<CustomerDTO> customerDTOList = new ArrayList<>();

        List<Customer> customerList = customerRepository.findAll();

        for (Customer customer : customerList) {
            List<PetDTO> petDTOList = petService.getPetByOwner(customer.getId());
            List<Long> petIds = petDTOList.stream().map(PetDTO::getId).collect(Collectors.toList());

            CustomerDTO customerDTO = convertEntityToDTO(customer, petIds);
            customerDTOList.add(customerDTO);
        }

        return customerDTOList;
    }

    public CustomerDTO findCustomer(long customerId) {
        Customer customer = customerRepository.find(customerId);
        List<Long> petIds = petService.getPetByOwner(customer.getId()).stream()
                .map(PetDTO::getId)
                .collect(Collectors.toList());

        return convertEntityToDTO(customer, petIds);
    }

    public CustomerDTO findCustomerByPetId(long petId) {
        PetDTO petDTO = petService.getPet(petId);

        Customer customer = customerRepository.find(petDTO.getOwnerId());
        List<Long> petIds = petService.getPetByOwner(customer.getId()).stream()
                .map(PetDTO::getId)
                .collect(Collectors.toList());

        return convertEntityToDTO(customer, petIds);
    }

    private static CustomerDTO convertEntityToDTO(Customer customer, List<Long> petIds) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(customer.getId());
        customerDTO.setName(customer.getName());
        customerDTO.setNotes(customer.getNotes());
        customerDTO.setPhoneNumber(customer.getPhoneNumber());
        customerDTO.setPetIds(petIds);
        return customerDTO;
    }
}
