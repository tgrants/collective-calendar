package com.collectivecalendar.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.collectivecalendar.model.Invite;

@Repository
public interface InviteRepository extends JpaRepository<Invite, UUID> {
	List<Invite> findByUserId(UUID userId);
	List<Invite> findByGroupId(UUID groupId);
	List<Invite> findByUserIdAndStatus(UUID userId, String status);
	boolean existsByUserIdAndGroupIdAndStatus(UUID userId, UUID groupId, String status);
}
