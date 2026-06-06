package com.collectivecalendar.repository;

import com.collectivecalendar.model.Notify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotifyRepository extends JpaRepository<Notify, UUID> {
	List<Notify> findByUserUid(UUID userUid);
	List<Notify> findByEventUid(UUID eventUid);
	void deleteByUserUidAndEventUid(UUID userUid, UUID eventUid);
}
