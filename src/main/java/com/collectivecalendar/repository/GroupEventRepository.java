package com.collectivecalendar.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.collectivecalendar.model.GroupEvent;

@Repository
public interface GroupEventRepository extends JpaRepository<GroupEvent, UUID> {
	List<GroupEvent> findByGroupId(UUID groupId);
	List<GroupEvent> findByEventId(UUID eventId);
	boolean existsByGroupIdAndEventId(UUID groupId, UUID eventId);
	void deleteByGroupIdAndEventId(UUID groupId, UUID eventId);
}
