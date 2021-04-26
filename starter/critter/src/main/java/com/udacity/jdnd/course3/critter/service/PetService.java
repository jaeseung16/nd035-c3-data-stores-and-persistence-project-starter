package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.entity.Pet;
import com.udacity.jdnd.course3.critter.pet.PetDTO;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    private PetRepository petRepository;

    public PetDTO savePet(PetDTO petDTO) {
        logger.info("Saving petDTO = {}", petDTO);

        Pet pet = getEntityFromDTO(petDTO);
        petRepository.save(pet);

        logger.info("Persisted a pet: id={}", pet.getOwnerId());
        return convertEntityToDTO(pet);
    }

    public PetDTO getPet(Long petId) {
        logger.info("Retrieving pets by petId = {}", petId);
        PetDTO petDTO = convertEntityToDTO(petRepository.getOne(petId));
        logger.info("Returning {}", petDTO);
        return petDTO;
    }

    public List<PetDTO> getPetByOwner(Long ownerId) {
        logger.info("Retrieving pets by ownerId = {}", ownerId);
        List<Pet> petList = petRepository.findByOwnerId(ownerId);
        logger.info("Returning {} pets with ownerId = {}", petList.size(), ownerId);
        return petList.stream()
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    public List<PetDTO> getPets() {
        logger.info("Retrieving all pets");
        List<Pet> petList = petRepository.findAll();
        logger.info("Returning {} pets", petList.size());
        return petList.stream().map(this::convertEntityToDTO).collect(Collectors.toList());
    }

    private PetDTO convertEntityToDTO(Pet pet) {
        PetDTO petDTO = new PetDTO();
        petDTO.setId(pet.getId());
        petDTO.setType(pet.getType());
        petDTO.setName(pet.getName());
        petDTO.setOwnerId(pet.getOwnerId());
        petDTO.setBirthDate(pet.getBirthDate());
        petDTO.setNotes(pet.getNotes());
        return petDTO;
    }

    private Pet getEntityFromDTO(PetDTO petDTO) {
        Pet pet = new Pet();
        pet.setType(petDTO.getType());
        pet.setName(petDTO.getName());
        pet.setOwnerId(petDTO.getOwnerId());
        pet.setBirthDate(petDTO.getBirthDate());
        pet.setNotes(petDTO.getNotes());
        return pet;
    }
}
