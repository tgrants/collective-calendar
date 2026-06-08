package com.collectivecalendar.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.collectivecalendar.model.UserGroup;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, UUID> {
	List<UserGroup> findByGroupId(UUID groupId);
	List<UserGroup> findByUserId(UUID userId);
	boolean existsByUserIdAndGroupId(UUID userId, UUID groupId);
	void deleteByUserIdAndGroupId(UUID userId, UUID groupId);
	void deleteByGroupId(UUID groupId);
}
