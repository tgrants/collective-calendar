package com.collectivecalendar.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.collectivecalendar.model.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
}
