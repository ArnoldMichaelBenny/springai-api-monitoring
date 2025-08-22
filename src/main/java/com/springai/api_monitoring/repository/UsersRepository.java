package com.springai.api_monitoring.repository;

import com.springai.api_monitoring.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {}
