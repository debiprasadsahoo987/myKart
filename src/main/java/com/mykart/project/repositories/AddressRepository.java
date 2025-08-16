package com.mykart.project.repositories;

import com.mykart.project.model.Address;
import com.mykart.project.payload.AddressDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    AddressDTO findAddressesByAddressId(Long addressId);
}
