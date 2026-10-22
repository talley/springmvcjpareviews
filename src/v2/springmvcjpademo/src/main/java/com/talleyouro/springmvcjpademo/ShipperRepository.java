package com.talleyouro.springmvcjpademo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface ShipperRepository extends JpaRepository<Shipper, Short> {

    
    List<Shipper> findByCompanyNameContainingIgnoreCase(String name);
    
    
    Shipper findTopByOrderByShipperIdDesc();   
}
