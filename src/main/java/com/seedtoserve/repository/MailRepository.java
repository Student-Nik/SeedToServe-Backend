package com.seedtoserve.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.seedtoserve.model.Mail;

@Repository
public interface MailRepository extends JpaRepository<Mail, Long>{

}
