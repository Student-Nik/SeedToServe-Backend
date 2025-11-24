package com.seedtoserve.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seedtoserve.model.Contact;

public interface ContactRepository extends JpaRepository<Contact,Integer>{

	Optional<Contact> findByDescription(String description);
}
